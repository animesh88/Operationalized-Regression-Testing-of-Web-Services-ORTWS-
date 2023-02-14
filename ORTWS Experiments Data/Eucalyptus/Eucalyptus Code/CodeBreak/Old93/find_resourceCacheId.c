int find_resourceCacheId(char *host, ccResource ** out)
{
    int i, done;

    if (!host || !out) {
        return (1);
    }

    sem_mywait(RESCACHE);
    *out = NULL;
    done = 0;
    for (i = 0; i < MAXNODES && !done; i++) {
        if (resourceCache->cacheState[i] == RESVALID) {
            if (!strcmp(resourceCache->resources[i].hostname, host)) {
                // found it
                *out = EUCA_ZALLOC(1, sizeof(ccResource));
                if (!*out) {
                    LOGFATAL("out of memory!\n");
                    unlock_exit(1);
                }
                allocate_ccResource(*out, resourceCache->resources[i].ncURL, resourceCache->resources[i].ncService,
                                    resourceCache->resources[i].ncPort, resourceCache->resources[i].hostname, resourceCache->resources[i].mac,
                                    resourceCache->resources[i].ip, resourceCache->resources[i].maxMemory, resourceCache->resources[i].availMemory,
                                    resourceCache->resources[i].maxDisk, resourceCache->resources[i].availDisk, resourceCache->resources[i].maxCores,
                                    resourceCache->resources[i].availCores, resourceCache->resources[i].state, resourceCache->resources[i].lastState,
                                    resourceCache->resources[i].stateChange, resourceCache->resources[i].idleStart);
                done++;
            }
        }
    }

    sem_mypost(RESCACHE);
    if (done) {
        return (0);
    }
    return (1);
}