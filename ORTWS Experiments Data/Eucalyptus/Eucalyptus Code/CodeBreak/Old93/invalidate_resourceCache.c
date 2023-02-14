void invalidate_resourceCache(void)
{
    sem_mywait(RESCACHE);

    bzero(resourceCache->cacheState, sizeof(int) * MAXNODES);
    resourceCache->numResources = 0;
    resourceCache->resourceCacheUpdate = 0;

    sem_mypost(RESCACHE);

}