int add_instanceCache(char *instanceId, ccInstance * in)
{
    int i, done, firstNull = 0;

    if (!instanceId || !in) {
        return (1);
    }

    sem_mywait(INSTCACHE);
    done = 0;
    for (i = 0; i < MAXINSTANCES_PER_CC && !done; i++) {
        if ((instanceCache->cacheState[i] == INSTVALID) && (!strcmp(instanceCache->instances[i].instanceId, instanceId))) {
            // already in cache
            LOGDEBUG("'%s/%s/%s' already in cache\n", instanceId, in->ccnet.publicIp, in->ccnet.privateIp);
            instanceCache->lastseen[i] = time(NULL);
            sem_mypost(INSTCACHE);
            return (0);
        } else if (instanceCache->cacheState[i] == INSTINVALID) {
            firstNull = i;
            done++;
        }
    }
    LOGDEBUG("adding '%s/%s/%s/%d' to cache\n", instanceId, in->ccnet.publicIp, in->ccnet.privateIp, in->volumesSize);
    allocate_ccInstance(&(instanceCache->instances[firstNull]), in->instanceId, in->amiId, in->kernelId, in->ramdiskId, in->amiURL, in->kernelURL,
                        in->ramdiskURL, in->ownerId, in->accountId, in->state, in->ccState, in->ts, in->reservationId, &(in->ccnet), &(in->ncnet),
                        &(in->ccvm), in->ncHostIdx, in->keyName, in->serviceTag, in->userData, in->launchIndex, in->platform, in->bundleTaskStateName,
                        in->groupNames, in->volumes, in->volumesSize);
    instanceCache->numInsts++;
    instanceCache->lastseen[firstNull] = time(NULL);
    instanceCache->cacheState[firstNull] = INSTVALID;

    sem_mypost(INSTCACHE);
    return (0);
}