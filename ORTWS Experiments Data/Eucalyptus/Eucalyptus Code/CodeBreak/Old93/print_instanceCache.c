void print_instanceCache(void)
{
    int i;

    sem_mywait(INSTCACHE);
    for (i = 0; i < MAXINSTANCES_PER_CC; i++) {
        if (instanceCache->cacheState[i] == INSTVALID) {
            LOGDEBUG("\tcache: %d/%d %s %s %s %s\n", i, instanceCache->numInsts, instanceCache->instances[i].instanceId,
                     instanceCache->instances[i].ccnet.publicIp, instanceCache->instances[i].ccnet.privateIp, instanceCache->instances[i].state);
        }
    }
    sem_mypost(INSTCACHE);
}