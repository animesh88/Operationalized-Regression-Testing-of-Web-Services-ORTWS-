int refresh_instances(ncMetadata * pMeta, int timeout, int dolock)
{
    ccInstance *myInstance = NULL;
    int i, numInsts = 0, found, ncOutInstsLen, rc, pid, nctimeout, *pids = NULL, status;
    time_t op_start;
    char *migration_host = NULL;
    char *migration_action = NULL;

    ncInstance **ncOutInsts = NULL;

    op_start = time(NULL);

    LOGDEBUG("invoked: timeout=%d, dolock=%d\n", timeout, dolock);
    set_clean_instanceCache();

    // critical NC call section
    sem_mywait(RESCACHE);
    memcpy(resourceCacheStage, resourceCache, sizeof(ccResourceCache));
    sem_mypost(RESCACHE);

    sem_close(locks[REFRESHLOCK]);
    locks[REFRESHLOCK] = sem_open("/eucalyptusCCrefreshLock", O_CREAT, 0644, config->ncFanout);

    pids = EUCA_ZALLOC(resourceCacheStage->numResources, sizeof(int));
    if (!pids) {
        LOGFATAL("out of memory!\n");
        unlock_exit(1);
    }

    invalidate_instanceCache();

    for (i = 0; i < resourceCacheStage->numResources; i++) {
        sem_mywait(REFRESHLOCK);
        pid = fork();
        if (!pid) {
            if (resourceCacheStage->resources[i].state == RESUP) {
                int j;

                nctimeout = ncGetTimeout(op_start, timeout, 1, 1);
                rc = ncClientCall(pMeta, nctimeout, resourceCacheStage->resources[i].lockidx, resourceCacheStage->resources[i].ncURL,
                                  "ncDescribeInstances", NULL, 0, &ncOutInsts, &ncOutInstsLen);
                if (!rc) {

                    // if idle, power down
                    if (ncOutInstsLen == 0) {
                        LOGDEBUG("node %s idle since %ld: (%ld/%d) seconds\n", resourceCacheStage->resources[i].hostname,
                                 resourceCacheStage->resources[i].idleStart, time(NULL) - resourceCacheStage->resources[i].idleStart,
                                 config->idleThresh);
                        if (!resourceCacheStage->resources[i].idleStart) {
                            resourceCacheStage->resources[i].idleStart = time(NULL);
                        } else if ((time(NULL) - resourceCacheStage->resources[i].idleStart) > config->idleThresh) {
                            // call powerdown

                            if (powerDown(pMeta, &(resourceCacheStage->resources[i]))) {
                                LOGWARN("powerDown for %s failed\n", resourceCacheStage->resources[i].hostname);
                            }
                        }
                    } else {
                        resourceCacheStage->resources[i].idleStart = 0;
                    }

                    // populate instanceCache
                    for (j = 0; j < ncOutInstsLen; j++) {
                        found = 1;
                        if (found) {
                            myInstance = NULL;
                            // add it
                            LOGDEBUG("describing instance %s, %s, %d\n", ncOutInsts[j]->instanceId, ncOutInsts[j]->stateName, j);
                            numInsts++;

                            // grab instance from cache, if available. otherwise, start from scratch
                            rc = find_instanceCacheId(ncOutInsts[j]->instanceId, &myInstance);
                            if (rc || !myInstance) {
                                myInstance = EUCA_ZALLOC(1, sizeof(ccInstance));
                                if (!myInstance) {
                                    LOGFATAL("out of memory!\n");
                                    unlock_exit(1);
                                }
                            }
                            // update CC instance with instance state from NC
                            rc = ncInstance_to_ccInstance(myInstance, ncOutInsts[j]);

                            // migration-related logic
                            if (ncOutInsts[j]->migration_state != NOT_MIGRATING) {

                                rc = migration_handler(myInstance,
                                                       resourceCacheStage->resources[i].hostname,
                                                       ncOutInsts[j]->migration_src,
                                                       ncOutInsts[j]->migration_dst,
                                                       ncOutInsts[j]->migration_state,
                                                       &migration_host,
                                                       &migration_action);

                                // For now just ignore updates from destination while migrating.
                                if (!strcmp(resourceCacheStage->resources[i].hostname, ncOutInsts[j]->migration_dst)) {

                                    EUCA_FREE(myInstance);
                                    break;
                                }
                            }
                            // instance info that the CC maintains
                            myInstance->ncHostIdx = i;

                            // FIXME: Is this redundant?
                            myInstance->migration_state = ncOutInsts[j]->migration_state;

                            euca_strncpy(myInstance->serviceTag, resourceCacheStage->resources[i].ncURL, 384);
                            {
                                char *ip = NULL;
                                if (!strcmp(myInstance->ccnet.publicIp, "0.0.0.0")) {
                                    if (!strcmp(vnetconfig->mode, "SYSTEM") || !strcmp(vnetconfig->mode, "STATIC")
                                        || !strcmp(vnetconfig->mode, "STATIC-DYNMAC")) {
                                        rc = mac2ip(vnetconfig, myInstance->ccnet.privateMac, &ip);
                                        if (!rc) {
                                            euca_strncpy(myInstance->ccnet.publicIp, ip, 24);
                                        }
                                    }
                                }

                                EUCA_FREE(ip);
                                if (!strcmp(myInstance->ccnet.privateIp, "0.0.0.0")) {
                                    rc = mac2ip(vnetconfig, myInstance->ccnet.privateMac, &ip);
                                    if (!rc) {
                                        euca_strncpy(myInstance->ccnet.privateIp, ip, 24);
                                    }
                                }

                                EUCA_FREE(ip);
                            }

                            //#if 0
                            if ((myInstance->ccnet.publicIp[0] != '\0' && strcmp(myInstance->ccnet.publicIp, "0.0.0.0"))
                                && (myInstance->ncnet.publicIp[0] == '\0' || !strcmp(myInstance->ncnet.publicIp, "0.0.0.0"))) {
                                // CC has network info, NC does not
                                LOGDEBUG("sending ncAssignAddress to sync NC\n");
                                rc = ncClientCall(pMeta, nctimeout, resourceCacheStage->resources[i].lockidx, resourceCacheStage->resources[i].ncURL,
                                                  "ncAssignAddress", myInstance->instanceId, myInstance->ccnet.publicIp);
                                if (rc) {
                                    // problem, but will retry next time
                                    LOGWARN("could not send AssignAddress to NC\n");
                                }
                            }
                            //#endif

                            refresh_instanceCache(myInstance->instanceId, myInstance);
                            if (!strcmp(myInstance->state, "Extant")) {
                                if (myInstance->ccnet.vlan < 0) {
                                    vnetEnableHost(vnetconfig, myInstance->ccnet.privateMac, myInstance->ccnet.privateIp, 0);
                                } else {
                                    vnetEnableHost(vnetconfig, myInstance->ccnet.privateMac, myInstance->ccnet.privateIp, myInstance->ccnet.vlan);
                                }
                            }
                            LOGDEBUG("storing instance state: %s/%s/%s/%s\n", myInstance->instanceId, myInstance->state, myInstance->ccnet.publicIp,
                                     myInstance->ccnet.privateIp);
                            print_ccInstance("refresh_instances(): ", myInstance);
                            sensor_set_resource_alias(myInstance->instanceId, myInstance->ncnet.privateIp);
                            EUCA_FREE(myInstance);
                        }

                    }
                }
                if (ncOutInsts) {
                    for (j = 0; j < ncOutInstsLen; j++) {
                        free_instance(&(ncOutInsts[j]));
                    }
                    EUCA_FREE(ncOutInsts);
                }
            }
            sem_mypost(REFRESHLOCK);

            if (migration_host) {
                if (!strcmp(migration_action, "commit")) {
                    LOGDEBUG("notifying source %s to commit migration.\n", migration_host);
                    doMigrateInstances(pMeta, migration_host, migration_action);
                } else {
                    LOGWARN("unexpected migration action %s for source %s -- doing nothing\n",
                            migration_action, migration_host);
                }
                EUCA_FREE(migration_host);
            }
            EUCA_FREE(migration_action);

            exit(0);
        } else {
            pids[i] = pid;
        }
    }

    for (i = 0; i < resourceCacheStage->numResources; i++) {
        rc = timewait(pids[i], &status, 120);
        if (!rc) {
            // timed out, really bad failure (reset REFRESHLOCK semaphore)
            sem_close(locks[REFRESHLOCK]);
            locks[REFRESHLOCK] = sem_open("/eucalyptusCCrefreshLock", O_CREAT, 0644, config->ncFanout);
            rc = 1;
        } else if (rc > 0) {
            // process exited, and wait picked it up.
            if (WIFEXITED(status)) {
                rc = WEXITSTATUS(status);
            } else {
                rc = 1;
            }
        } else {
            // process no longer exists, and someone else reaped it
            rc = 0;
        }
        if (rc) {
            LOGWARN("error waiting for child pid '%d', exit code '%d'\n", pids[i], rc);
        }
    }

    invalidate_instanceCache();

    sem_mywait(RESCACHE);
    memcpy(resourceCache, resourceCacheStage, sizeof(ccResourceCache));
    sem_mypost(RESCACHE);

    EUCA_FREE(pids);

    LOGTRACE("done\n");
    return (0);
}