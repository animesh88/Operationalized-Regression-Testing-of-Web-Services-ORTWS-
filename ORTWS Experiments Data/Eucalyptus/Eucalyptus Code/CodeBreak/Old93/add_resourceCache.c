int add_resourceCache(char *host, ccResource * in)
{
    int i, done, firstNull = 0;

    if (!host || !in) {
        return (1);
    }

    sem_mywait(RESCACHE);
    done = 0;
    for (i = 0; i < MAXNODES && !done; i++) {
        if (resourceCache->cacheState[i] == RESVALID) {
            if (!strcmp(resourceCache->resources[i].hostname, host)) {
                // already in cache
                sem_mypost(RESCACHE);
                return (0);
            }
        } else {
            firstNull = i;
            done++;
        }
    }
    resourceCache->cacheState[firstNull] = RESVALID;
    allocate_ccResource(&(resourceCache->resources[firstNull]), in->ncURL, in->ncService, in->ncPort, in->hostname, in->mac, in->ip, in->maxMemory,
                        in->availMemory, in->maxDisk, in->availDisk, in->maxCores, in->availCores, in->state, in->lastState, in->stateChange,
                        in->idleStart);

    resourceCache->numResources++;
    sem_mypost(RESCACHE);
    return (0);
}