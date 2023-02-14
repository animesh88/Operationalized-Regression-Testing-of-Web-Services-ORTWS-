int doUnassignAddress(ncMetadata * pMeta, char *src, char *dst)
{
    int rc, ret;
    ccInstance *myInstance = NULL;
    ccResourceCache resourceCacheLocal;

    rc = initialize(pMeta);
    if (rc || ccIsEnabled()) {
        return (1);
    }

    LOGINFO("unassigning address %s\n", SP(src));
    LOGDEBUG("invoked: userId=%s, src=%s, dst=%s\n", SP(pMeta ? pMeta->userId : "UNSET"), SP(src), SP(dst));

    if (!src || !dst || !strcmp(src, "0.0.0.0")) {
        LOGDEBUG("bad input params\n");
        return (1);
    }
    set_dirty_instanceCache();

    sem_mywait(RESCACHE);
    memcpy(&resourceCacheLocal, resourceCache, sizeof(ccResourceCache));
    sem_mypost(RESCACHE);

    ret = 0;

    if (!strcmp(vnetconfig->mode, "SYSTEM") || !strcmp(vnetconfig->mode, "STATIC") || !strcmp(vnetconfig->mode, "STATIC-DYNMAC")) {
        ret = 0;
    } else {

        sem_mywait(VNET);

        ret = vnetReassignAddress(vnetconfig, "UNSET", src, "0.0.0.0");
        if (ret) {
            LOGERROR("vnetReassignAddress() failed ret=%d\n", ret);
            ret = 1;
        }

        sem_mypost(VNET);
    }

    if (!ret) {

        rc = find_instanceCacheIP(src, &myInstance);
        if (!rc) {
            LOGDEBUG("found instance %s in cache with IP %s\n", myInstance->instanceId, myInstance->ccnet.publicIp);
            // found the instance in the cache
            if (myInstance) {
                //timeout = ncGetTimeout(op_start, OP_TIMEOUT, 1, myInstance->ncHostIdx);
                rc = ncClientCall(pMeta, OP_TIMEOUT, resourceCacheLocal.resources[myInstance->ncHostIdx].lockidx,
                                  resourceCacheLocal.resources[myInstance->ncHostIdx].ncURL, "ncAssignAddress", myInstance->instanceId, "0.0.0.0");
                if (rc) {
                    LOGERROR("could not sync IP with NC\n");
                    ret = 1;
                } else {
                    ret = 0;
                }
                EUCA_FREE(myInstance);
            }
        }
        // refresh instance cache
        rc = map_instanceCache(pubIpCmp, src, pubIpSet, "0.0.0.0");
        if (rc) {
            LOGERROR("map_instanceCache() failed to assign %s->%s\n", dst, src);
        }
    }

    LOGTRACE("done\n");

    shawn();

    return (ret);
}