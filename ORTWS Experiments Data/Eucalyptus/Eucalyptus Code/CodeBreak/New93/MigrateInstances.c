int doMigrateInstances(ncMetadata * pMeta, char *nodeName, char *nodeAction)
{
    int i, rc, ret = 0, timeout;
    int src_index = -1, dst_index = -1;
    int preparing = 0;
    ccResourceCache resourceCacheLocal;

    rc = initialize(pMeta);
    if (rc || ccIsEnabled()) {
        return (1);
    }

    if (!nodeName) {
        LOGERROR("bad input params\n");
        return (1);
    }
    if (!strcmp(nodeAction, "prepare")) {
        LOGINFO("preparing migration from node %s\n", SP(nodeName));
        preparing = 1;
    } else if (!strcmp(nodeAction, "commit")) {
        LOGINFO("committing migration from node %s\n", SP(nodeName));
    } else if (!strcmp(nodeAction, "rollback")) {
        LOGINFO("rolling back migration on node %s\n", SP(nodeName));
        // FIXME: Remove this warning once rollback has been fully implemented.
        LOGWARN("rollbacks have not yet been implemented\n");
        return (1);
    } else {
        LOGERROR("invalid action parameter: %s\n", nodeAction);
        return (1);
    }

    sem_mywait(RESCACHE);
    memcpy(&resourceCacheLocal, resourceCache, sizeof(ccResourceCache));
    sem_mypost(RESCACHE);

    // FIXME: this assumes two nodes, one of which is a source and one of which is a destination.
    for (i = 0; i < resourceCacheLocal.numResources && (src_index == -1 || dst_index == -1); i++) {
        if (resourceCacheLocal.resources[i].state != RESASLEEP) {
            if (!strcmp(resourceCacheLocal.resources[i].hostname, nodeName)) {
                // found it
                src_index = i;
            } else {
                // FIXME: This goes away once we're doing real scheduling.
                if (dst_index == -1) {
                    // This will be ignored if we're not preparing.
                    dst_index = i;
                }
            }
        }
    }
    if (src_index == -1) {
        LOGERROR("node requested for migration (%s) cannot be found\n", SP(nodeName));
        goto out;
    }
    if (preparing && (dst_index == -1)) {
        LOGERROR("have instances to migrate, but no destinations\n");
        goto out;
    }

    // FIXME: needs to find all instances running on host -- it currently only finds the first one.
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

    ncInstance nc_instance;
    ccInstance_to_ncInstance(&nc_instance, &cc_instance);
    strncpy(nc_instance.migration_src, resourceCacheLocal.resources[src_index].hostname, sizeof(nc_instance.migration_src));
    strncpy(nc_instance.migration_dst, resourceCacheLocal.resources[dst_index].hostname, sizeof(nc_instance.migration_dst));
    ncInstance *instances = &nc_instance;

    if (preparing) {
        char *migration_dst = strdup(nc_instance.migration_dst);
        // FIXME: temporary hack for testing an idea: need to fill in include & exclude lists.
        rc = schedule_instance_migration(&nc_instance, &migration_dst, NULL, &dst_index);
        EUCA_FREE(migration_dst);

        if (rc || (dst_index == -1)) {
            LOGERROR("[%s] cannot schedule destination node (%s) for migration\n", nc_instance.instanceId, nc_instance.migration_dst);
            goto out;
        }
    }

    LOGINFO("migrating from %s to %s\n", SP(resourceCacheLocal.resources[src_index].hostname), SP(resourceCacheLocal.resources[dst_index].hostname));

    if (!strcmp(nodeAction, "prepare")) {
        // notify source
        timeout = ncGetTimeout(time(NULL), OP_TIMEOUT, 1, 0);
        rc = ncClientCall(pMeta, timeout, resourceCacheLocal.resources[src_index].lockidx, resourceCacheLocal.resources[src_index].ncURL, "ncMigrateInstances",
                          &instances, 1, nodeAction, NULL);
        if (rc) {
            LOGERROR("failed: request to prepare migration on source\n");
            ret = 1;
            goto out;
        }
        // notify the destination
        timeout = ncGetTimeout(time(NULL), OP_TIMEOUT, 1, 0);
        rc = ncClientCall(pMeta, timeout, resourceCacheLocal.resources[dst_index].lockidx, resourceCacheLocal.resources[dst_index].ncURL, "ncMigrateInstances",
                          &instances, 1, nodeAction, NULL);
        if (rc) {
            LOGERROR("failed: request to prepare migration on destination\n");
            ret = 1;
            goto out;
        }
    } else { // Commit
        // call commit on source
        timeout = ncGetTimeout(time(NULL), OP_TIMEOUT, 1, 0);
        rc = ncClientCall(pMeta, timeout, resourceCacheLocal.resources[src_index].lockidx, resourceCacheLocal.resources[src_index].ncURL, "ncMigrateInstances",
                          &instances, 1, nodeAction, NULL);
        if (rc) {
            LOGERROR("failed: migration request on source\n");
            ret = 1;
            goto out;
        }
    }
out:

    LOGTRACE("done\n");

    shawn();

    return (ret);
}