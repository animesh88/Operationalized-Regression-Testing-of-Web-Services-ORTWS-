int doBrokerPairing(void)
{
    int ret, local_broker_down, i, is_ha_cc, port;
    char buri[MAX_PATH], uriType[32], bhost[MAX_PATH], path[MAX_PATH], curi[MAX_PATH], chost[MAX_PATH];

    ret = 0;
    local_broker_down = 0;
    is_ha_cc = 0;

    snprintf(curi, MAX_PATH, "%s", config->ccStatus.serviceId.uris[0]);
    bzero(chost, sizeof(char) * MAX_PATH);
    tokenize_uri(curi, uriType, chost, &port, path);

    //enabled
    for (i = 0; i < 16; i++) {
        if (!strcmp(config->ccStatus.serviceId.name, "self")) {
            // LOGDEBUG("local CC service info not yet initialized\n");
        } else if (!memcmp(&(config->ccStatus.serviceId), &(config->services[i]), sizeof(serviceInfoType))) {
            // LOGDEBUG("found local CC information in services()\n");
        } else if (!strcmp(config->services[i].type, "cluster") && !strcmp(config->services[i].partition, config->ccStatus.serviceId.partition)) {
            // service is not 'me', but is a 'cluster' and in has the same 'partition', must be in HA mode
            // LOGDEBUG("CC is in HA mode\n");
            is_ha_cc = 1;
        }
    }
    //disabled
    for (i = 0; i < 16; i++) {
        if (!strcmp(config->ccStatus.serviceId.name, "self")) {
            // LOGDEBUG("local CC service info not yet initialized\n");
        } else if (!memcmp(&(config->ccStatus.serviceId), &(config->disabledServices[i]), sizeof(serviceInfoType))) {
            // LOGDEBUG("found local CC information in disabled services()\n");
        } else if (!strcmp(config->disabledServices[i].type, "cluster")
                   && !strcmp(config->disabledServices[i].partition, config->ccStatus.serviceId.partition)) {
            // service is not 'me', but is a 'cluster' and in has the same 'partition', must be in HA mode
            // LOGDEBUG("CC is in HA mode\n");
            is_ha_cc = 1;
        }
    }
    //notready
    for (i = 0; i < 16; i++) {
        int j;
        //test
        if (!strcmp(config->ccStatus.serviceId.name, "self")) {
            // LOGDEBUG("local CC service info not yet initialized\n");
        } else if (!memcmp(&(config->ccStatus.serviceId), &(config->notreadyServices[i]), sizeof(serviceInfoType))) {
            // LOGDEBUG("found local CC information in notreadyServices()\n");
        } else if (!strcmp(config->notreadyServices[i].type, "cluster")
                   && !strcmp(config->notreadyServices[i].partition, config->ccStatus.serviceId.partition)) {
            // service is not 'me', but is a 'cluster' and in has the same 'partition', must be in HA mode
            // LOGDEBUG("CC is in HA mode\n");
            is_ha_cc = 1;
        }

        if (strlen(config->notreadyServices[i].type)) {
            if (!strcmp(config->notreadyServices[i].type, "vmwarebroker")) {
                for (j = 0; j < 8; j++) {
                    if (strlen(config->notreadyServices[i].uris[j])) {
                        LOGDEBUG("found broker - %s\n", config->notreadyServices[i].uris[j]);

                        snprintf(buri, MAX_PATH, "%s", config->notreadyServices[i].uris[j]);
                        bzero(bhost, sizeof(char) * MAX_PATH);
                        tokenize_uri(buri, uriType, bhost, &port, path);

                        LOGDEBUG("comparing found not ready broker host (%s) with local CC host (%s)\n", bhost, chost);
                        if (!strcmp(chost, bhost)) {
                            LOGWARN("detected local broker (%s) matching local CC (%s) in NOTREADY state\n", bhost, chost);
                            // ret++;
                            local_broker_down = 1;
                        }
                    }
                }
            }
        }
    }

    if (local_broker_down && is_ha_cc) {
        LOGDEBUG("detected CC in HA mode, and local broker is not ENABLED\n");
        ret++;
    }
    return (ret);
}

//!
//! The CC will start a background thread to poll its collection of nodes. This thread populates an
//! in-memory cache of instance and resource information that can be accessed via the regular describeInstances
//! and describeResources calls to the CC. The purpose of this separation is to allow for a more scalable
//! framework where describe operations do not block on access to node controllers.
//!
//! @param[in] in
//!
//! @return
//!
//! @pre
//!
//! @note
//!
void *monitor_thread(void *in)
{
    int rc, ncTimer, clcTimer, ncSensorsTimer, ncRefresh = 0, clcRefresh = 0, ncSensorsRefresh = 0;
    ncMetadata pMeta;
    char pidfile[MAX_PATH], *pidstr = NULL;

    bzero(&pMeta, sizeof(ncMetadata));
    pMeta.correlationId = strdup("monitor");
    pMeta.userId = strdup("eucalyptus");
    if (!pMeta.correlationId || !pMeta.userId) {
        LOGFATAL("out of memory!\n");
        unlock_exit(1);
    }
    // set up default signal handler for this child process (for SIGTERM)
    struct sigaction newsigact;
    newsigact.sa_handler = SIG_DFL;
    newsigact.sa_flags = 0;
    sigemptyset(&newsigact.sa_mask);
    sigprocmask(SIG_SETMASK, &newsigact.sa_mask, NULL);
    sigaction(SIGTERM, &newsigact, NULL);

    // add 1 to each Timer so they will all fire upon the first loop iteration
    ncTimer = config->ncPollingFrequency + 1;
    clcTimer = config->clcPollingFrequency + 1;
    ncSensorsTimer = config->ncSensorsPollingInterval + 1;

    while (1) {
        LOGTRACE("running\n");

        if (config->kick_enabled) {
            ccChangeState(ENABLED);
            config->kick_enabled = 0;
        }

        rc = update_config();
        if (rc) {
            LOGWARN("bad return from update_config(), check your config file\n");
        }

        if (config->ccState == ENABLED) {

            // NC Polling operations
            if (ncTimer >= config->ncPollingFrequency) {
                ncTimer = 0;
                ncRefresh = 1;
            }
            ncTimer++;

            // CLC Polling operations
            if (clcTimer >= config->clcPollingFrequency) {
                clcTimer = 0;
                clcRefresh = 1;
            }
            clcTimer++;

            // NC Sensors Polling operation
            if (ncSensorsTimer >= config->ncSensorsPollingInterval) {
                ncSensorsTimer = 0;
                ncSensorsRefresh = 1;
            }
            ncSensorsTimer++;

            if (ncRefresh) {
                rc = refresh_resources(&pMeta, 60, 1);
                if (rc) {
                    LOGWARN("call to refresh_resources() failed in monitor thread\n");
                }

                rc = refresh_instances(&pMeta, 60, 1);
                if (rc) {
                    LOGWARN("call to refresh_instances() failed in monitor thread\n");
                }
            }

            { // print a periodic summary of instances in the log
                static time_t last_log_update = 0;

                int res_idle = 0, res_busy = 0, res_bad = 0;
                sem_mywait(RESCACHE);
                for (int i = 0; i < resourceCache->numResources; i++) {
                    ccResource *res = &(resourceCache->resources[i]);
                    if (res->state == RESDOWN) {
                        res_bad++;
                    } else {
                        if (res->maxCores != res->availCores) {
                            res_busy++;
                        } else {
                            res_idle++;
                        }
                    }
                }
                sem_mypost(RESCACHE);

                int num_pending = 0, num_extant = 0, num_teardown = 0;
                sem_mywait(INSTCACHE);
                if (instanceCache->numInsts) {
                    for (int i = 0; i < MAXINSTANCES_PER_CC; i++) {
                        if (!strcmp(instanceCache->instances[i].state, "Pending")) {
                            num_teardown++;
                        } else if (!strcmp(instanceCache->instances[i].state, "Extant")) {
                            num_extant++;
                        } else if (!strcmp(instanceCache->instances[i].state, "Teardown")) {
                            num_teardown++;
                        }
                    }
                }
                sem_mypost(INSTCACHE);

                time_t now = time(NULL);
                if ((now - last_log_update) > LOG_INTERVAL_SUMMARY_SEC) {
                    last_log_update = now;
                    LOGINFO("instances: %04d (%04d extant + %04d pending + %04d terminated)\n", (num_pending + num_extant + num_teardown), num_extant,
                            num_pending, num_teardown);
                    LOGINFO(" nodes: %04d (%04d busy + %04d idle + %04d unresponsive)\n", (res_busy + res_idle + res_bad), res_busy, res_idle,
                            res_bad);
                }
            }

            if (ncSensorsRefresh) {
                rc = refresh_sensors(&pMeta, 60, 1);
                if (rc == 0) {
                    // refresh_sensors() only returns non-zero when sensor subsystem has not been initialized.
                    // Until it is initialized, keep checking every second, so that sensory subsystems on NCs are
                    // initialized soon after it is initialized on the CC (otherwise it may take a while and NC
                    // may miss initial measurements from early instances). Once initialized, refresh can happen
                    // as configured by config->ncSensorsPollingInterval.
                    ncSensorsRefresh = 0;
                }
            }

            if (ncRefresh) {
                if (is_clean_instanceCache()) {
                    // Network state operations
                    // sem_mywait(RESCACHE);

                    LOGDEBUG("syncing network state\n");
                    rc = syncNetworkState();
                    if (rc) {
                        LOGDEBUG("syncNetworkState() triggering network restore\n");
                        config->kick_network = 1;
                    }
                    // sem_mypost(RESCACHE);

                    if (config->kick_network) {
                        LOGDEBUG("restoring network state\n");
                        rc = restoreNetworkState();
                        if (rc) {
                            // failed to restore network state, continue
                            LOGWARN("restoreNetworkState returned false (may be already restored)\n");
                        } else {
                            sem_mywait(CONFIG);
                            config->kick_network = 0;
                            sem_mypost(CONFIG);
                        }
                    }
                } else {
                    LOGDEBUG("instanceCache is dirty, skipping network update\n");
                }
            }

            if (clcRefresh) {
                LOGDEBUG("syncing CLC network rules ground truth with local state\n");
                rc = reconfigureNetworkFromCLC();
                if (rc) {
                    LOGWARN("cannot get network ground truth from CLC\n");
                }
            }

            if (ncRefresh) {
                LOGDEBUG("maintaining network state\n");
                rc = maintainNetworkState();
                if (rc) {
                    LOGERROR("network state maintainance failed\n");
                }
            }

            if (config->use_proxy) {
                rc = image_cache_invalidate();
                if (rc) {
                    LOGERROR("cannot invalidate image cache\n");
                }
                snprintf(pidfile, MAX_PATH, EUCALYPTUS_RUN_DIR "/httpd-dynserv.pid", config->eucahome);
                pidstr = file2str(pidfile);
                if (pidstr) {
                    if (check_process(atoi(pidstr), "dynserv-httpd.conf")) {
                        rc = image_cache_proxykick(resourceCache->resources, &(resourceCache->numResources));
                        if (rc) {
                            LOGERROR("could not start proxy cache\n");
                        }
                    }
                    EUCA_FREE(pidstr);
                } else {
                    rc = image_cache_proxykick(resourceCache->resources, &(resourceCache->numResources));
                    if (rc) {
                        LOGERROR("could not start proxy cache\n");
                    }
                }
            }
            config->kick_monitor_running = 1;
        } else {
            // this CC is not enabled, ensure that local network state is disabled
            rc = clean_network_state();
            if (rc) {
                LOGERROR("could not cleanup network state\n");
            }
        }

        // do state checks under CONFIG lock
        sem_mywait(CONFIG);
        if (ccCheckState(clcTimer)) {
            LOGERROR("ccCheckState() returned failures\n");
            config->kick_enabled = 0;
            ccChangeState(NOTREADY);
        } else if (config->ccState == NOTREADY) {
            ccChangeState(DISABLED);
        }
        sem_mypost(CONFIG);
        shawn();

        LOGTRACE("localState=%s - done.\n", config->ccStatus.localState);
        //sleep(config->ncPollingFrequency);
        ncRefresh = clcRefresh = 0;
        sleep(1);
    }
    return (NULL);
}