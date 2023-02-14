int doMigrateInstances(ncMetadata * pMeta, char *nodeName)
{
    int i, rc, ret = 0, timeout;
    int src_index = -1, dst_index = -1;
    ccResourceCache resourceCacheLocal;

    rc = initialize(pMeta);
    if (rc || ccIsEnabled()) {
        return (1);
    }

    if (!nodeName) {
        LOGERROR("bad input params\n");
        return (1);
    }
    LOGINFO("modifying node %s\n", SP(nodeName));

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

    LOGINFO("migrating from %s to %s\n", SP(resourceCacheLocal.resources[src_index].hostname), SP(resourceCacheLocal.resources[dst_index].hostname));

    // find an instance running on the host
    int found_instance = 0;
    ccInstance cc_instance;
    sem_mywait(INSTCACHE);
    if (instanceCache->numInsts) {
        for (i = 0; i < MAXINSTANCES_PER_CC; i++) {
            if (instanceCache->cacheState[i] == INSTVALID && instanceCache->instances[i].ncHostIdx == src_index
                && (!strcmp(instanceCache->instances[i].state, "Extant"))) {
                memcpy(&cc_instance, &(instanceCache->instances[i]), sizeof(ccInstance));
                found_instance = 1;
                break;
            }
        }
    }
    sem_mypost(INSTCACHE);
    if (!found_instance) {
        LOGINFO("no instances running on host %s\n", SP(nodeName));
        goto out;
    }

    if (dst_index == -1) {
        LOGERROR("have instances to migrate, but no destinations\n");
        goto out;
    }

    ncInstance nc_instance;
    ccInstance_to_ncInstance(&nc_instance, &cc_instance);
    strncpy(nc_instance.migration_src, resourceCacheLocal.resources[src_index].hostname, sizeof(nc_instance.migration_src));
    strncpy(nc_instance.migration_dst, resourceCacheLocal.resources[dst_index].hostname, sizeof(nc_instance.migration_dst));
    ncInstance *instances = &nc_instance;

    // notify the destination
    timeout = ncGetTimeout(time(NULL), OP_TIMEOUT, 1, 0);
    rc = ncClientCall(pMeta, timeout, resourceCacheLocal.resources[dst_index].lockidx, resourceCacheLocal.resources[dst_index].ncURL,
                      "ncMigrateInstances", &instances, 1, "Prepare", NULL);
    if (rc) {
        LOGERROR("failed to request migration on destination\n");
        ret = 1;
        goto out;
    }
    // notify source
    timeout = ncGetTimeout(time(NULL), OP_TIMEOUT, 1, 0);
    rc = ncClientCall(pMeta, timeout, resourceCacheLocal.resources[src_index].lockidx, resourceCacheLocal.resources[src_index].ncURL,
                      "ncMigrateInstances", &instances, 1, "Prepare", NULL);
    if (rc) {
        LOGERROR("failed to request migration on source\n");
        ret = 1;
        goto out;
    }

out:

    LOGTRACE("done\n");

    shawn();

    return (ret);
}