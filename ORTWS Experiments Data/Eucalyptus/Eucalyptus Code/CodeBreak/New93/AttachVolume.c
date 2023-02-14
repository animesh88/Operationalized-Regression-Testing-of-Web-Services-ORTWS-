int doAttachVolume(ncMetadata * pMeta, char *volumeId, char *instanceId, char *remoteDev, char *localDev)
{
    int i, rc, start = 0, stop = 0, ret = 0, done = 0, timeout;
    ccInstance *myInstance;
    time_t op_start;
    ccResourceCache resourceCacheLocal;

    i = 0;
    myInstance = NULL;
    op_start = time(NULL);

    rc = initialize(pMeta);
    if (rc || ccIsEnabled()) {
        return (1);
    }

    LOGINFO("[%s][%s] attaching volume\n", SP(instanceId), SP(volumeId));
    LOGDEBUG("invoked: userId=%s, volumeId=%s, instanceId=%s, remoteDev=%s, localDev=%s\n", SP(pMeta ? pMeta->userId : "UNSET"),
             SP(volumeId), SP(instanceId), SP(remoteDev), SP(localDev));
    if (!volumeId || !instanceId || !remoteDev || !localDev) {
        LOGERROR("bad input params\n");
        return (1);
    }

    sem_mywait(RESCACHE);
    memcpy(&resourceCacheLocal, resourceCache, sizeof(ccResourceCache));
    sem_mypost(RESCACHE);

    rc = find_instanceCacheId(instanceId, &myInstance);
    if (!rc) {
        // found the instance in the cache
        if (myInstance) {
            start = myInstance->ncHostIdx;
            stop = start + 1;
            EUCA_FREE(myInstance);
        }
    } else {
        start = 0;
        stop = resourceCacheLocal.numResources;
    }

    done = 0;
    for (i = start; i < stop && !done; i++) {
        timeout = ncGetTimeout(op_start, OP_TIMEOUT, stop - start, i);
        timeout = maxint(timeout, ATTACH_VOL_TIMEOUT_SECONDS);

        // pick out the right LUN from the remove device string
        char remoteDevForNC [VERY_BIG_CHAR_BUFFER_SIZE];
        if (get_remoteDevForNC(resourceCacheLocal.resources[i].iqn, remoteDev, remoteDevForNC, sizeof(remoteDevForNC))) {
            LOGERROR("failed to parse remote dev string in request\n");
            rc = 1;
        } else {
            rc = ncClientCall(pMeta, timeout, resourceCacheLocal.resources[i].lockidx, resourceCacheLocal.resources[i].ncURL, "ncAttachVolume",
                              instanceId, volumeId, remoteDevForNC, localDev);
        }

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