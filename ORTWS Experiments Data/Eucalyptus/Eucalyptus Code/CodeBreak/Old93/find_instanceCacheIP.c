int find_instanceCacheIP(char *ip, ccInstance ** out)
{
    int i, done;

    if (!ip || !out) {
        return (1);
    }

    sem_mywait(INSTCACHE);
    *out = NULL;
    done = 0;
    for (i = 0; i < MAXINSTANCES_PER_CC && !done; i++) {
        if ((instanceCache->instances[i].ccnet.publicIp[0] != '\0' || instanceCache->instances[i].ccnet.privateIp[0] != '\0')) {
            if (!strcmp(instanceCache->instances[i].ccnet.publicIp, ip) || !strcmp(instanceCache->instances[i].ccnet.privateIp, ip)) {
                // found it
                *out = EUCA_ZALLOC(1, sizeof(ccInstance));
                if (!*out) {
                    LOGFATAL("out of memory!\n");
                    unlock_exit(1);
                }

                allocate_ccInstance(*out, instanceCache->instances[i].instanceId, instanceCache->instances[i].amiId,
                                    instanceCache->instances[i].kernelId, instanceCache->instances[i].ramdiskId, instanceCache->instances[i].amiURL,
                                    instanceCache->instances[i].kernelURL, instanceCache->instances[i].ramdiskURL,
                                    instanceCache->instances[i].ownerId, instanceCache->instances[i].accountId, instanceCache->instances[i].state,
                                    instanceCache->instances[i].ccState, instanceCache->instances[i].ts, instanceCache->instances[i].reservationId,
                                    &(instanceCache->instances[i].ccnet), &(instanceCache->instances[i].ncnet), &(instanceCache->instances[i].ccvm),
                                    instanceCache->instances[i].ncHostIdx, instanceCache->instances[i].keyName,
                                    instanceCache->instances[i].serviceTag, instanceCache->instances[i].userData,
                                    instanceCache->instances[i].launchIndex, instanceCache->instances[i].platform,
                                    instanceCache->instances[i].bundleTaskStateName, instanceCache->instances[i].groupNames,
                                    instanceCache->instances[i].volumes, instanceCache->instances[i].volumesSize);
                done++;
            }
        }
    }

    sem_mypost(INSTCACHE);
    if (done) {
        return (0);
    }
    return (1);
}