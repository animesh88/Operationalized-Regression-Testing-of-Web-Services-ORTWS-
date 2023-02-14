void print_resourceCache(void)
{
    int i;

    sem_mywait(RESCACHE);
    for (i = 0; i < MAXNODES; i++) {
        if (resourceCache->cacheState[i] == RESVALID) {
            LOGDEBUG("\tcache: %s %s %s %s/%s state=%d\n", resourceCache->resources[i].hostname, resourceCache->resources[i].ncURL,
                     resourceCache->resources[i].ncService, resourceCache->resources[i].mac, resourceCache->resources[i].ip,
                     resourceCache->resources[i].state);
        }
    }
    sem_mypost(RESCACHE);
}