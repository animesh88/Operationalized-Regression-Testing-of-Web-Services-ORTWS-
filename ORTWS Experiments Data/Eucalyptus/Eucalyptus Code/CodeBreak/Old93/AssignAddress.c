int doAssignAddress(ncMetadata * pMeta, char *uuid, char *src, char *dst)
{
    int rc, ret;
    ccInstance *myInstance = NULL;
    ccResourceCache resourceCacheLocal;

    rc = initialize(pMeta);
    if (rc || ccIsEnabled()) {
        return (1);
    }

    LOGINFO("assigning address %s to %s\n", SP(src), SP(dst));
    LOGDEBUG("invoked: src=%s, dst=%s, uuid=%s\n", SP(src), SP(dst), SP(uuid));

    if (!src || !dst || !strcmp(src, "0.0.0.0")) {
        LOGDEBUG("bad input params\n");
        return (1);
    }
    set_dirty_instanceCache();

    sem_mywait(RESCACHE);
    memcpy(&resourceCacheLocal, resourceCache, sizeof(ccResourceCache));
    sem_mypost(RESCACHE);

    ret = 1;
    if (!strcmp(vnetconfig->mode, "SYSTEM") || !strcmp(vnetconfig->mode, "STATIC") || !strcmp(vnetconfig->mode, "STATIC-DYNMAC")) {
        ret = 0;
    } else {

        rc = find_instanceCacheIP(dst, &myInstance);
        if (!rc) {
            if (myInstance) {
                LOGDEBUG("found local instance, applying %s->%s mapping\n", src, dst);

                sem_mywait(VNET);
                rc = vnetReassignAddress(vnetconfig, uuid, src, dst);
                if (rc) {
                    LOGERROR("vnetReassignAddress() failed rc=%d\n", rc);
                    ret = 1;
                } else {
                    ret = 0;
                }
                sem_mypost(VNET);

                EUCA_FREE(myInstance);
            }
        } else {
            LOGDEBUG("skipping %s->%s mapping, as this clusters does not own the instance (%s)\n", src, dst, dst);
        }
    }

    if (!ret && strcmp(dst, "0.0.0.0")) {
        // everything worked, update instance cache

        rc = map_instanceCache(privIpCmp, dst, pubIpSet, src);
        if (rc) {
            LOGERROR("map_instanceCache() failed to assign %s->%s\n", dst, src);
        } else {
            rc = find_instanceCacheIP(src, &myInstance);
            if (!rc) {
                LOGDEBUG("found instance (%s) in cache with IP (%s)\n", myInstance->instanceId, myInstance->ccnet.publicIp);
                // found the instance in the cache
                if (myInstance) {
                    //timeout = ncGetTimeout(op_start, OP_TIMEOUT, 1, myInstance->ncHostIdx);
                    rc = ncClientCall(pMeta, OP_TIMEOUT, resourceCacheLocal.resources[myInstance->ncHostIdx].lockidx,
                                      resourceCacheLocal.resources[myInstance->ncHostIdx].ncURL, "ncAssignAddress", myInstance->instanceId,
                                      myInstance->ccnet.publicIp);
                    if (rc) {
                        LOGERROR("could not sync public IP %s with NC\n", src);
                        ret = 1;
                    } else {
                        ret = 0;
                    }
                    EUCA_FREE(myInstance);
                }
            }
        }
    }

    LOGTRACE("done\n");

    shawn();

    return (ret);
}