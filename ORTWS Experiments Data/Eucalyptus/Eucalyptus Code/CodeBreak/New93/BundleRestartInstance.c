int doBundleRestartInstance(ncMetadata * pMeta, char *instanceId)
{
    int j = 0;
    int rc = 0;
    int start = 0;
    int stop = 0;
    int ret = 0;
    int timeout = 0;
    int done = 0;
    ccInstance *myInstance = NULL;
    time_t op_start = time(NULL);
    ccResourceCache resourceCacheLocal;

    rc = initialize(pMeta);
    if (rc || ccIsEnabled())
        return (1);

    LOGINFO("[%s] bundling instance restart\n", SP(instanceId));
    LOGDEBUG("invoked: instanceId=%s userId=%s\n", SP(instanceId), SP(pMeta ? pMeta->userId : "UNSET"));
    if (instanceId == NULL) {
        LOGERROR("bad input params\n");
        return (1);
    }

    sem_mywait(RESCACHE);
    {
        memcpy(&resourceCacheLocal, resourceCache, sizeof(ccResourceCache));
    }
    sem_mypost(RESCACHE);

    if ((rc = find_instanceCacheId(instanceId, &myInstance)) == 0) {
        // found the instance in the cache
        if (myInstance) {
            start = myInstance->ncHostIdx;
            stop = start + 1;
            EUCA_FREE(myInstance);
            myInstance = NULL;
        }
    } else {
        start = 0;
        stop = resourceCacheLocal.numResources;
    }

    done = 0;
    for (j = start; ((j < stop) && !done); j++) {
        timeout = ncGetTimeout(op_start, OP_TIMEOUT, (stop - start), j);
        rc = ncClientCall(pMeta, timeout, resourceCacheLocal.resources[j].lockidx, resourceCacheLocal.resources[j].ncURL, "ncBundleRestartInstance",
                          instanceId);
        if (rc) {
            ret = 1;
        } else {
            ret = 0;
            done++;
        }
    }

    LOGTRACE("done\n");
    shawn();
    return (ret);
}