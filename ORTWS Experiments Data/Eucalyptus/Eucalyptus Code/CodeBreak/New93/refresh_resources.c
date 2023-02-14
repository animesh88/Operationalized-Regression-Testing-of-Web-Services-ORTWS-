int refresh_resources(ncMetadata * pMeta, int timeout, int dolock)
{
    int i, rc, nctimeout, pid, *pids = NULL;
    int status;
    time_t op_start;
    ncResource *ncResDst = NULL;

    if (timeout <= 0)
        timeout = 1;

    op_start = time(NULL);
    LOGDEBUG("invoked: timeout=%d, dolock=%d\n", timeout, dolock);

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

    for (i = 0; i < resourceCacheStage->numResources; i++) {
        sem_mywait(REFRESHLOCK);

        pid = fork();
        if (!pid) {
            ncResDst = NULL;
            if (resourceCacheStage->resources[i].state != RESASLEEP && resourceCacheStage->resources[i].running == 0) {
                nctimeout = ncGetTimeout(op_start, timeout, 1, 1);
                rc = ncClientCall(pMeta, nctimeout, resourceCacheStage->resources[i].lockidx, resourceCacheStage->resources[i].ncURL,
                                  "ncDescribeResource", NULL, &ncResDst);
                if (rc != 0) {
                    powerUp(&(resourceCacheStage->resources[i]));

                    if (resourceCacheStage->resources[i].state == RESWAKING
                        && ((time(NULL) - resourceCacheStage->resources[i].stateChange) < config->wakeThresh)) {
                        LOGDEBUG("resource still waking up (%ld more seconds until marked as down)\n",
                                 config->wakeThresh - (time(NULL) - resourceCacheStage->resources[i].stateChange));
                    } else {
                        LOGERROR("bad return from ncDescribeResource(%s) (%d)\n", resourceCacheStage->resources[i].hostname, rc);
                        resourceCacheStage->resources[i].maxMemory = 0;
                        resourceCacheStage->resources[i].availMemory = 0;
                        resourceCacheStage->resources[i].maxDisk = 0;
                        resourceCacheStage->resources[i].availDisk = 0;
                        resourceCacheStage->resources[i].maxCores = 0;
                        resourceCacheStage->resources[i].availCores = 0;
                        changeState(&(resourceCacheStage->resources[i]), RESDOWN);
                    }
                } else {
                    LOGDEBUG("received data from node=%s mem=%d/%d disk=%d/%d cores=%d/%d\n",
                             resourceCacheStage->resources[i].hostname,
                             ncResDst->memorySizeAvailable,
                             ncResDst->memorySizeMax, ncResDst->diskSizeAvailable, ncResDst->diskSizeMax, ncResDst->numberOfCoresAvailable,
                             ncResDst->numberOfCoresMax);
                    resourceCacheStage->resources[i].maxMemory = ncResDst->memorySizeMax;
                    resourceCacheStage->resources[i].availMemory = ncResDst->memorySizeAvailable;
                    resourceCacheStage->resources[i].maxDisk = ncResDst->diskSizeMax;
                    resourceCacheStage->resources[i].availDisk = ncResDst->diskSizeAvailable;
                    resourceCacheStage->resources[i].maxCores = ncResDst->numberOfCoresMax;
                    resourceCacheStage->resources[i].availCores = ncResDst->numberOfCoresAvailable;

                    // set iqn, if set
                    if (strlen(ncResDst->iqn)) {
                        snprintf(resourceCacheStage->resources[i].iqn, 128, "%s", ncResDst->iqn);
                    }

                    changeState(&(resourceCacheStage->resources[i]), RESUP);
                }
            } else {
                LOGDEBUG("resource asleep/running instances (%d), skipping resource update\n", resourceCacheStage->resources[i].running);
            }

            // try to discover the mac address of the resource
            if (resourceCacheStage->resources[i].mac[0] == '\0' && resourceCacheStage->resources[i].ip[0] != '\0') {
                char *mac;
                rc = ip2mac(vnetconfig, resourceCacheStage->resources[i].ip, &mac);
                if (!rc) {
                    euca_strncpy(resourceCacheStage->resources[i].mac, mac, 24);
                    EUCA_FREE(mac);
                    LOGDEBUG("discovered MAC '%s' for host %s(%s)\n", resourceCacheStage->resources[i].mac,
                             resourceCacheStage->resources[i].hostname, resourceCacheStage->resources[i].ip);
                }
            }

            EUCA_FREE(ncResDst);
            sem_mypost(REFRESHLOCK);
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

    sem_mywait(RESCACHE);
    memcpy(resourceCache, resourceCacheStage, sizeof(ccResourceCache));
    sem_mypost(RESCACHE);

    EUCA_FREE(pids);
    LOGTRACE("done\n");
    return (0);
}