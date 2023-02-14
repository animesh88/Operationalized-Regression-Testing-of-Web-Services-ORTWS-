int doRebootInstances(ncMetadata * pMeta, char **instIds, int instIdsLen)
{
    int i, j, rc, numInsts, start, stop, done, timeout = 0, ret = 0;
    char *instId;
    ccInstance *myInstance;
    time_t op_start;
    ccResourceCache resourceCacheLocal;

    i = j = numInsts = 0;
    instId = NULL;
    myInstance = NULL;
    op_start = time(NULL);

    rc = initialize(pMeta);
    if (rc || ccIsEnabled()) {
        return (1);
    }

    LOGINFO("rebooting %d instances\n", instIdsLen);
    LOGDEBUG("invoked: instIdsLen=%d\n", instIdsLen);

    sem_mywait(RESCACHE);
    memcpy(&resourceCacheLocal, resourceCache, sizeof(ccResourceCache));
    sem_mypost(RESCACHE);

    for (i = 0; i < instIdsLen; i++) {
        instId = instIds[i];
        rc = find_instanceCacheId(instId, &myInstance);
        if (!rc) {
            // found the instance in the cache
            start = myInstance->ncHostIdx;
            stop = start + 1;
            EUCA_FREE(myInstance);
        } else {
            start = 0;
            stop = resourceCacheLocal.numResources;
        }

        done = 0;
        for (j = start; j < stop && !done; j++) {
            timeout = ncGetTimeout(op_start, OP_TIMEOUT, (stop - start), j);
            rc = ncClientCall(pMeta, timeout, resourceCacheLocal.resources[j].lockidx, resourceCacheLocal.resources[j].ncURL, "ncRebootInstance",
                              instId);
            if (rc) {
                ret = 1;
            } else {
                ret = 0;
                done++;
            }
        }
    }

    LOGTRACE("done\n");

    shawn();

    return (0); /// XXX:gholms
}