int doCreateImage(ncMetadata * pMeta, char *instanceId, char *volumeId, char *remoteDev)
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

    LOGINFO("[%s] creating image\n", SP(instanceId));
    LOGDEBUG("invoked: userId=%s, volumeId=%s, instanceId=%s, remoteDev=%s\n", SP(pMeta ? pMeta->userId : "UNSET"), SP(volumeId), SP(instanceId),
             SP(remoteDev));
    if (!volumeId || !instanceId || !remoteDev) {
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
        rc = ncClientCall(pMeta, timeout, resourceCacheLocal.resources[i].lockidx, resourceCacheLocal.resources[i].ncURL, "ncCreateImage", instanceId,
                          volumeId, remoteDev);
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

//!
//!
//!
//! @param[in] pMeta a pointer to the node controller (NC) metadata structure
//! @param[in] historySize
//! @param[in] collectionIntervalTimeMs
//! @param[in] instIds
//! @param[in] instIdsLen
//! @param[in] sensorIds
//! @param[in] sensorIdsLen
//! @param[out] outResources
//! @param[out] outResourcesLen
//!
//! @return
//!
//! @pre
//!
//! @note
//!
int doDescribeSensors(ncMetadata * pMeta, int historySize, long long collectionIntervalTimeMs, char **instIds, int instIdsLen, char **sensorIds,
                      int sensorIdsLen, sensorResource *** outResources, int *outResourcesLen)
{
    int rc = initialize(pMeta);
    if (rc || ccIsEnabled()) {
        return 1;
    }

    LOGDEBUG("invoked: historySize=%d collectionIntervalTimeMs=%lld instIdsLen=%d i[0]='%s' sensorIdsLen=%d s[0]='%s'\n",
             historySize, collectionIntervalTimeMs, instIdsLen, instIdsLen > 0 ? instIds[0] : "*", sensorIdsLen,
             sensorIdsLen > 0 ? sensorIds[0] : "*");
    int err = sensor_config(historySize, collectionIntervalTimeMs); // update the config parameters if they are different
    if (err != 0)
        LOGWARN("failed to update sensor configuration (err=%d)\n", err);
    if (historySize > 0 && collectionIntervalTimeMs > 0) {
        int col_interval_sec = collectionIntervalTimeMs / 1000;
        int nc_poll_interval_sec = col_interval_sec * historySize - POLL_INTERVAL_SAFETY_MARGIN_SEC;
        if (nc_poll_interval_sec < POLL_INTERVAL_MINIMUM_SEC)
            nc_poll_interval_sec = POLL_INTERVAL_MINIMUM_SEC;
        if (config->ncSensorsPollingInterval != nc_poll_interval_sec) {
            config->ncSensorsPollingInterval = nc_poll_interval_sec;
            LOGDEBUG("changed NC sensors poll interval to %d (col_interval_sec=%d historySize=%d)\n", nc_poll_interval_sec, col_interval_sec,
                     historySize);
        }
    }

    int num_resources = sensor_get_num_resources();
    if (num_resources < 0) {
        LOGERROR("failed to determine number of available sensor resources\n");
        return 1;
    }
    // oddly, an empty set of instanceIds or sensorIds in XML is presented
    // by Axis as an array of size 1 with an empty string as the only element
    int num_instances = instIdsLen;
    if (instIdsLen == 1 && strlen(instIds[0]) == 0)
        num_instances = 0; // which is to say all instances

    *outResources = NULL;
    *outResourcesLen = 0;

    if (num_resources > 0) {

        int num_slots = num_resources; // report on all instances
        if (num_instances > 0)
            num_slots = num_instances; // report on specific instances

        *outResources = EUCA_ZALLOC(num_slots, sizeof(sensorResource *));
        if ((*outResources) == NULL) {
            return OUT_OF_MEMORY;
        }
        for (int i = 0; i < num_slots; i++) {
            (*outResources)[i] = EUCA_ZALLOC(1, sizeof(sensorResource));
            if (((*outResources)[i]) == NULL) {
                return OUT_OF_MEMORY;
            }
        }

        int num_results = 0;
        if (num_instances == 0) { // report on all instances
            // if number of resources has changed since the call to sensor_get_num_resources(),
            // then we may not report on everything (ok, since we'll get it next time)
            // or we may have fewer records in outResrouces[] (ok, since empty ones will be ignored)
            if (sensor_get_instance_data(NULL, NULL, 0, *outResources, num_slots) == 0)
                num_results = num_slots; // actually num_results <= num_slots, but that's OK

        } else { // report on specific instances
            // if some instances requested by ID were not found on this CC,
            // we will have fewer records in outResources[] (ok, since empty ones will be ignored)
            for (int i = 0; i < num_instances; i++) {
                if (sensor_get_instance_data(instIds[i], NULL, 0, (*outResources + num_results), 1) == 0)
                    num_results++;
            }
        }
        *outResourcesLen = num_results;
    }

    LOGTRACE("returning (outResourcesLen=%d)\n", *outResourcesLen);

    return 0;
}