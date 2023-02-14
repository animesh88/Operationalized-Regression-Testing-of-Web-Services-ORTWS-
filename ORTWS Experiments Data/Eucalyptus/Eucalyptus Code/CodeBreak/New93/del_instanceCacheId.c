int del_instanceCacheId(char *instanceId)
{
    int i;

    sem_mywait(INSTCACHE);
    for (i = 0; i < MAXINSTANCES_PER_CC; i++) {
        if ((instanceCache->cacheState[i] == INSTVALID) && (!strcmp(instanceCache->instances[i].instanceId, instanceId))) {
            // del from cache
            bzero(&(instanceCache->instances[i]), sizeof(ccInstance));
            instanceCache->lastseen[i] = 0;
            instanceCache->cacheState[i] = INSTINVALID;
            instanceCache->numInsts--;
            sem_mypost(INSTCACHE);
            return (0);
        }
    }
    sem_mypost(INSTCACHE);
    return (0);
}