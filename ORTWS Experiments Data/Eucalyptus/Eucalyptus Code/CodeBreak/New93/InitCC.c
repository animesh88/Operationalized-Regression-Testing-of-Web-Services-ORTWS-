void doInitCC(void)
{
    initialize(NULL);
}

//!
//!
//!
//! @param[in] pMeta a pointer to the node controller (NC) metadata structure
//! @param[in] instanceId
//! @param[in] bucketName
//! @param[in] filePrefix
//! @param[in] walrusURL
//! @param[in] userPublicKey
//! @param[in] S3Policy
//! @param[in] S3PolicySig
//!
//! @return
//!
//! @pre
//!
//! @note
//!
int doBundleInstance(ncMetadata * pMeta, char *instanceId, char *bucketName, char *filePrefix, char *walrusURL, char *userPublicKey, char *S3Policy,
                     char *S3PolicySig)
{
    int i, j, rc, start = 0, stop = 0, ret = 0, timeout, done;
    char internalWalrusURL[MAX_PATH], theWalrusURL[MAX_PATH];
    ccInstance *myInstance;
    time_t op_start;
    ccResourceCache resourceCacheLocal;

    i = j = 0;
    myInstance = NULL;
    op_start = time(NULL);

    rc = initialize(pMeta);
    if (rc || ccIsEnabled()) {
        return (1);
    }

    LOGINFO("[%s] bundling requested\n", instanceId);
    LOGDEBUG("invoked: userId=%s, instanceId=%s, bucketName=%s, filePrefix=%s, walrusURL=%s, userPublicKey=%s, S3Policy=%s, S3PolicySig=%s\n",
             SP(pMeta ? pMeta->userId : "UNSET"), SP(instanceId), SP(bucketName), SP(filePrefix), SP(walrusURL), SP(userPublicKey), SP(S3Policy),
             SP(S3PolicySig));
    if (!instanceId) {
        LOGERROR("bad input params\n");
        return (1);
    }
    // get internal walrus IP
    done = 0;
    internalWalrusURL[0] = '\0';
    for (i = 0; i < 16 && !done; i++) {
        if (!strcmp(config->services[i].type, "walrus")) {
            snprintf(internalWalrusURL, MAX_PATH, "%s", config->services[i].uris[0]);
            done++;
        }
    }
    if (done) {
        snprintf(theWalrusURL, MAX_PATH, "%s", internalWalrusURL);
    } else {
        strncpy(theWalrusURL, walrusURL, strlen(walrusURL) + 1);
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
    for (j = start; j < stop && !done; j++) {
        timeout = ncGetTimeout(op_start, OP_TIMEOUT, stop - start, j);
        rc = ncClientCall(pMeta, timeout, resourceCacheLocal.resources[j].lockidx, resourceCacheLocal.resources[j].ncURL, "ncBundleInstance",
                          instanceId, bucketName, filePrefix, theWalrusURL, userPublicKey, S3Policy, S3PolicySig);
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