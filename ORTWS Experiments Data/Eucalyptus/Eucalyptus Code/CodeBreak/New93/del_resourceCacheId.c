int del_resourceCacheId(char *host)
{
    int i;

    sem_mywait(RESCACHE);
    for (i = 0; i < MAXNODES; i++) {
        if (resourceCache->cacheState[i] == RESVALID) {
            if (!strcmp(resourceCache->resources[i].hostname, host)) {
                // del from cache
                bzero(&(resourceCache->resources[i]), sizeof(ccResource));
                resourceCache->cacheState[i] = RESINVALID;
                resourceCache->numResources--;
                sem_mypost(RESCACHE);
                return (0);
            }
        }
    }
    sem_mypost(RESCACHE);
    return (0);
}