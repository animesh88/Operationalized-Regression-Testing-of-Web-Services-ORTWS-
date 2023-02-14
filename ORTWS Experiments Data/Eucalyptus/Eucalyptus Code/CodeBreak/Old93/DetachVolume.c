int doDetachVolume(ncMetadata * pMeta, char *volumeId, char *instanceId, char *remoteDev, char *localDev, int force)
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

    LOGINFO("[%s][%s] detaching volume\n", SP(instanceId), SP(volumeId));
    LOGDEBUG("invoked: volumeId=%s, instanceId=%s, remoteDev=%s, localDev=%s, force=%d\n", SP(volumeId), SP(instanceId), SP(remoteDev), SP(localDev),
             force);
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

    for (i = start; i < stop; i++) {
        timeout = ncGetTimeout(op_start, OP_TIMEOUT, stop - start, i);
        timeout = maxint(timeout, DETACH_VOL_TIMEOUT_SECONDS);
        rc = ncClientCall(pMeta, timeout, resourceCacheLocal.resources[i].lockidx, resourceCacheLocal.resources[i].ncURL, "ncDetachVolume",
                          instanceId, volumeId, remoteDev, localDev, force);
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
//! @param[in] accountId
//! @param[in] type
//! @param[in] namedLen
//! @param[in] sourceNames
//! @param[in] userNames
//! @param[in] netLen
//! @param[in] sourceNets
//! @param[in] destName
//! @param[in] destUserName
//! @param[in] protocol
//! @param[in] minPort
//! @param[in] maxPort
//!
//! @return
//!
//! @pre
//!
//! @note
//!
int doConfigureNetwork(ncMetadata * pMeta, char *accountId, char *type, int namedLen, char **sourceNames, char **userNames, int netLen,
                       char **sourceNets, char *destName, char *destUserName, char *protocol, int minPort, int maxPort)
{
    int rc, i, fail;

    rc = initialize(pMeta);
    if (rc || ccIsEnabled()) {
        return (1);
    }

    LOGINFO("configuring network %s\n", SP(destName));
    LOGDEBUG("invoked: userId=%s, accountId=%s, type=%s, namedLen=%d, netLen=%d, destName=%s, destUserName=%s, protocol=%s, minPort=%d, maxPort=%d\n",
             pMeta ? SP(pMeta->userId) : "UNSET", SP(accountId), SP(type), namedLen, netLen, SP(destName), SP(destUserName), SP(protocol), minPort,
             maxPort);

    if (!strcmp(vnetconfig->mode, "SYSTEM") || !strcmp(vnetconfig->mode, "STATIC") || !strcmp(vnetconfig->mode, "STATIC-DYNMAC")) {
        fail = 0;
    } else {

        if (destUserName == NULL) {
            if (accountId) {
                destUserName = accountId;
            } else {
                // destUserName is not set, return fail
                LOGERROR("cannot set destUserName from pMeta or input\n");
                return (1);
            }
        }

        sem_mywait(VNET);

        fail = 0;
        for (i = 0; i < namedLen; i++) {
            if (sourceNames && userNames) {
                rc = vnetTableRule(vnetconfig, type, destUserName, destName, userNames[i], NULL, sourceNames[i], protocol, minPort, maxPort);
            }
            if (rc) {
                LOGERROR("vnetTableRule() returned error rc=%d\n", rc);
                fail = 1;
            }
        }
        for (i = 0; i < netLen; i++) {
            if (sourceNets) {
                rc = vnetTableRule(vnetconfig, type, destUserName, destName, NULL, sourceNets[i], NULL, protocol, minPort, maxPort);
            }
            if (rc) {
                LOGERROR("vnetTableRule() returned error rc=%d\n", rc);
                fail = 1;
            }
        }
        sem_mypost(VNET);
    }

    LOGTRACE("done\n");

    shawn();

    if (fail) {
        return (1);
    }
    return (0);
}