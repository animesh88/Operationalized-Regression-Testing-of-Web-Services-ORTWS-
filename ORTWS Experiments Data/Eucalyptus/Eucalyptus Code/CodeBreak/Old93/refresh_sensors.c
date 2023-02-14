int refresh_sensors(ncMetadata * pMeta, int timeout, int dolock)
{

    time_t op_start = time(NULL);
    LOGDEBUG("invoked: timeout=%d, dolock=%d\n", timeout, dolock);

    int history_size;
    long long collection_interval_time_ms;
    if ((sensor_get_config(&history_size, &collection_interval_time_ms) != 0) || history_size < 1 || collection_interval_time_ms == 0)
        return 1; // sensor system not configured yet

    // critical NC call section
    sem_mywait(RESCACHE);
    memcpy(resourceCacheStage, resourceCache, sizeof(ccResourceCache));
    sem_mypost(RESCACHE);

    sem_close(locks[REFRESHLOCK]);
    locks[REFRESHLOCK] = sem_open("/eucalyptusCCrefreshLock", O_CREAT, 0644, config->ncFanout);

    int *pids = EUCA_ZALLOC(resourceCacheStage->numResources, sizeof(int));
    if (!pids) {
        LOGFATAL("out of memory!\n");
        unlock_exit(1);
    }

    for (int i = 0; i < resourceCacheStage->numResources; i++) {
        sem_mywait(REFRESHLOCK);
        pid_t pid = fork();
        if (!pid) {
            if (resourceCacheStage->resources[i].state == RESUP) {
                int nctimeout = ncGetTimeout(op_start, timeout, 1, 1);

                sensorResource **srs;
                int srsLen;
                int rc = ncClientCall(pMeta, nctimeout, resourceCacheStage->resources[i].lockidx, resourceCacheStage->resources[i].ncURL,
                                      "ncDescribeSensors", history_size, collection_interval_time_ms,
                                      NULL, 0, NULL, 0, &srs, &srsLen);

                if (!rc) {
                    // update our cache
                    if (sensor_merge_records(srs, srsLen, TRUE) != EUCA_OK) {
                        LOGWARN("failed to store all sensor data due to lack of space");
                    }

                    if (srsLen > 0) {
                        for (int j = 0; j < srsLen; j++) {
                            EUCA_FREE(srs[j]);
                        }
                        EUCA_FREE(srs);
                    }
                }
            }
            sem_mypost(REFRESHLOCK);
            exit(0);
        } else {
            pids[i] = pid;
        }
    }

    for (int i = 0; i < resourceCacheStage->numResources; i++) {
        int status;

        int rc = timewait(pids[i], &status, 120);
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

    sem_mywait(RESCACHE);
    memcpy(resourceCache, resourceCacheStage, sizeof(ccResourceCache));
    sem_mypost(RESCACHE);

    EUCA_FREE(pids);
    LOGTRACE("done\n");
    return (0);
}