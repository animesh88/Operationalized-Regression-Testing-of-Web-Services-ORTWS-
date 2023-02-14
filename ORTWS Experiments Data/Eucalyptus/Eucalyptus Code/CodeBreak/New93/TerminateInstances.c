int doTerminateInstances(ncMetadata * pMeta, char **instIds, int instIdsLen, int force, int **outStatus)
{
    int i, j, shutdownState, previousState, rc, start, stop, done = 0, ret = 0;
    char *instId;
    ccInstance *myInstance = NULL;
    ccResourceCache resourceCacheLocal;

    i = j = 0;
    instId = NULL;
    myInstance = NULL;

    rc = initialize(pMeta);
    if (rc || ccIsEnabled()) {
        return (1);
    }
    set_dirty_instanceCache();

    print_abbreviated_instances("terminating", instIds, instIdsLen);
    LOGDEBUG("invoked: userId=%s, instIdsLen=%d, firstInstId=%s, force=%d\n", SP(pMeta ? pMeta->userId : "UNSET"), instIdsLen,
             SP(instIdsLen ? instIds[0] : "UNSET"), force);

    sem_mywait(RESCACHE);
    memcpy(&resourceCacheLocal, resourceCache, sizeof(ccResourceCache));
    sem_mypost(RESCACHE);

    for (i = 0; i < instIdsLen; i++) {
        instId = instIds[i];
        rc = find_instanceCacheId(instId, &myInstance);
        if (!rc) {
            // found the instance in the cache
            if (myInstance != NULL
                && (!strcmp(myInstance->state, "Pending") || !strcmp(myInstance->state, "Extant") || !strcmp(myInstance->state, "Unknown"))) {
                start = myInstance->ncHostIdx;
                stop = start + 1;
            } else {
                // instance is not in a terminatable state
                start = 0;
                stop = 0;
                (*outStatus)[i] = 0;
            }
            EUCA_FREE(myInstance);
        } else {
            // instance is not in cache, try all resources

            start = 0;
            stop = 0;
            (*outStatus)[i] = 0;
        }

        done = 0;
        for (j = start; j < stop && !done; j++) {
            if (resourceCacheLocal.resources[j].state == RESUP) {

                if (!strstr(resourceCacheLocal.resources[j].ncURL, "EucalyptusNC")) {
                    char cdir[MAX_PATH];
                    char cfile[MAX_PATH];
                    snprintf(cdir, MAX_PATH, EUCALYPTUS_STATE_DIR "/windows/%s/", config->eucahome, instId);
                    if (!check_directory(cdir)) {
                        snprintf(cfile, MAX_PATH, "%s/floppy", cdir);
                        if (!check_file(cfile))
                            unlink(cfile);
                        snprintf(cfile, MAX_PATH, "%s/console.append.log", cdir);
                        if (!check_file(cfile))
                            unlink(cfile);
                        rmdir(cdir);
                    }
                }

                rc = ncClientCall(pMeta, 0, resourceCacheLocal.resources[j].lockidx, resourceCacheLocal.resources[j].ncURL, "ncTerminateInstance",
                                  instId, force, &shutdownState, &previousState);
                if (rc) {
                    (*outStatus)[i] = 1;
                    LOGWARN("failed to terminate '%s': instance may not exist any longer\n", instId);
                    ret = 1;
                } else {
                    (*outStatus)[i] = 0;
                    ret = 0;
                    done++;
                }
                rc = ncClientCall(pMeta, 0, resourceCacheStage->resources[j].lockidx, resourceCacheStage->resources[j].ncURL, "ncAssignAddress",
                                  instId, "0.0.0.0");
                if (rc) {
                    // problem, but will retry next time
                    LOGWARN("could not send AssignAddress to NC\n");
                }
            }
        }
    }

    LOGTRACE("done\n");

    shawn();

    return (0);
}