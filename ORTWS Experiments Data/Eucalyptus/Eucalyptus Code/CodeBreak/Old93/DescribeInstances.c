int doDescribeInstances(ncMetadata * pMeta, char **instIds, int instIdsLen, ccInstance ** outInsts, int *outInstsLen)
{
    int i, rc, count;
    time_t op_start;

    LOGDEBUG("invoked: userId=%s, instIdsLen=%d\n", SP(pMeta ? pMeta->userId : "UNSET"), instIdsLen);

    op_start = time(NULL);

    rc = initialize(pMeta);
    if (rc || ccIsEnabled()) {
        return (1);
    }

    *outInsts = NULL;
    *outInstsLen = 0;

    sem_mywait(INSTCACHE);
    count = 0;
    if (instanceCache->numInsts) {
        *outInsts = EUCA_ZALLOC(instanceCache->numInsts, sizeof(ccInstance));
        if (!*outInsts) {
            LOGFATAL("out of memory!\n");
            unlock_exit(1);
        }

        for (i = 0; i < MAXINSTANCES_PER_CC; i++) {
            if (instanceCache->cacheState[i] == INSTVALID) {
                if (count >= instanceCache->numInsts) {
                    LOGWARN("found more instances than reported by numInsts, will only report a subset of instances\n");
                    count = 0;
                }
                memcpy(&((*outInsts)[count]), &(instanceCache->instances[i]), sizeof(ccInstance));
                count++;
            }
        }

        *outInstsLen = instanceCache->numInsts;
    }
    sem_mypost(INSTCACHE);

    for (i = 0; i < (*outInstsLen); i++) {
        LOGDEBUG("instances summary: instanceId=%s, state=%s, publicIp=%s, privateIp=%s\n", (*outInsts)[i].instanceId,
                 (*outInsts)[i].state, (*outInsts)[i].ccnet.publicIp, (*outInsts)[i].ccnet.privateIp);
    }

    LOGTRACE("done\n");

    shawn();

    return (0);
}