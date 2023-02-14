void invalidate_instanceCache(void)
{
    int i;

    sem_mywait(INSTCACHE);
    for (i = 0; i < MAXINSTANCES_PER_CC; i++) {
        // if instance is in teardown, free up network information
        if (!strcmp(instanceCache->instances[i].state, "Teardown")) {
            free_instanceNetwork(instanceCache->instances[i].ccnet.privateMac, instanceCache->instances[i].ccnet.vlan, 0, 0);
        }
        if ((instanceCache->cacheState[i] == INSTVALID) && ((time(NULL) - instanceCache->lastseen[i]) > config->instanceTimeout)) {
            LOGDEBUG("invalidating instance '%s' (last seen %ld seconds ago)\n", instanceCache->instances[i].instanceId,
                     (time(NULL) - instanceCache->lastseen[i]));
            bzero(&(instanceCache->instances[i]), sizeof(ccInstance));
            instanceCache->lastseen[i] = 0;
            instanceCache->cacheState[i] = INSTINVALID;
            instanceCache->numInsts--;
        }
    }
    sem_mypost(INSTCACHE);
}