int doModifyNode(ncMetadata * pMeta, char *nodeName, char *stateName)
{
    int i, rc, ret = 0, timeout;
    int src_index = -1, dst_index = -1;
    ccResourceCache resourceCacheLocal;

    rc = initialize(pMeta);
    if (rc || ccIsEnabled()) {
        return (1);
    }

    if (!nodeName || !stateName) {
        LOGERROR("bad input params\n");
        return (1);
    }
    LOGINFO("modifying node %s with state=%s\n", SP(nodeName), SP(stateName));

    sem_mywait(RESCACHE);
    memcpy(&resourceCacheLocal, resourceCache, sizeof(ccResourceCache));
    sem_mypost(RESCACHE);

    for (i = 0; i < resourceCacheLocal.numResources && (src_index == -1 || dst_index == -1); i++) {
        if (resourceCacheLocal.resources[i].state != RESASLEEP) {
            if (!strcmp(resourceCacheLocal.resources[i].hostname, nodeName)) {
                // found it
                src_index = i;
            } else {
                if (dst_index == -1)
                    dst_index = i;
            }
        }
    }
    if (src_index == -1) {
        LOGERROR("node requested for modification (%s) cannot be found\n", SP(nodeName));
        goto out;
    }

    timeout = ncGetTimeout(time(NULL), OP_TIMEOUT, 1, 0);
    rc = ncClientCall(pMeta, timeout, resourceCacheLocal.resources[src_index].lockidx, resourceCacheLocal.resources[src_index].ncURL, "ncModifyNode", stateName); // no need to pass nodeName as ncClientCall sets that up for all NC requests
    if (rc) {
        ret = 1;
        goto out;
    }

    // FIXME: This is only here for compatability with earlier demo
    // development. Remove.
    if (!doMigrateInstances(pMeta, nodeName, "prepare")) {
        LOGERROR("doModifyNode() call of doMigrateInstances() failed.\n");
    }

 out:
    LOGTRACE("done\n");

    shawn();

    return (ret);
}