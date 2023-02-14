int doStartNetwork(ncMetadata * pMeta, char *accountId, char *uuid, char *netName, int vlan, char *nameserver, char **ccs, int ccsLen)
{
    int rc, ret;
    char *brname;

    rc = initialize(pMeta);
    if (rc || ccIsEnabled()) {
        return (1);
    }

    LOGINFO("starting network %s with VLAN %d\n", SP(netName), vlan);
    LOGDEBUG("invoked: userId=%s, accountId=%s, nameserver=%s, ccsLen=%d\n", SP(pMeta ? pMeta->userId : "UNSET"), SP(accountId), SP(nameserver),
             ccsLen);

    if (!strcmp(vnetconfig->mode, "SYSTEM") || !strcmp(vnetconfig->mode, "STATIC") || !strcmp(vnetconfig->mode, "STATIC-DYNMAC")) {
        ret = 0;
    } else {
        sem_mywait(VNET);
        if (nameserver) {
            vnetconfig->euca_ns = dot2hex(nameserver);
        }

        rc = vnetSetCCS(vnetconfig, ccs, ccsLen);
        rc = vnetSetupTunnels(vnetconfig);

        brname = NULL;
        rc = vnetStartNetwork(vnetconfig, vlan, uuid, accountId, netName, &brname);
        EUCA_FREE(brname);

        sem_mypost(VNET);

        if (rc) {
            LOGERROR("vnetStartNetwork() failed (%d)\n", rc);
            ret = 1;
        } else {
            ret = 0;
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
//! @param[in] ccvms
//! @param[in] vmLen
//! @param[out] outTypesMax
//! @param[out] outTypesAvail
//! @param[out] outTypesLen
//! @param[out] outNodes
//! @param[out] outNodesLen
//!
//! @return
//!
//! @pre
//!
//! @note
//!
int doDescribeResources(ncMetadata * pMeta, virtualMachine ** ccvms, int vmLen, int **outTypesMax, int **outTypesAvail, int *outTypesLen,
                        ccResource ** outNodes, int *outNodesLen)
{
    int i;
    int rc, diskpool, mempool, corepool;
    int j;
    ccResource *res;
    ccResourceCache resourceCacheLocal;

    LOGDEBUG("invoked: userId=%s, vmLen=%d\n", SP(pMeta ? pMeta->userId : "UNSET"), vmLen);

    rc = initialize(pMeta);
    if (rc || ccIsEnabled()) {
        return (1);
    }

    if (outTypesMax == NULL || outTypesAvail == NULL || outTypesLen == NULL || outNodes == NULL || outNodesLen == NULL) {
        // input error
        return (1);
    }

    *outTypesMax = NULL;
    *outTypesAvail = NULL;

    *outTypesMax = EUCA_ZALLOC(vmLen, sizeof(int));
    *outTypesAvail = EUCA_ZALLOC(vmLen, sizeof(int));
    if (*outTypesMax == NULL || *outTypesAvail == NULL) {
        LOGERROR("out of memory\n");
        unlock_exit(1);
    }

    *outTypesLen = vmLen;

    for (i = 0; i < vmLen; i++) {
        if ((*ccvms)[i].mem <= 0 || (*ccvms)[i].cores <= 0 || (*ccvms)[i].disk <= 0) {
            LOGERROR("input error\n");
            EUCA_FREE(*outTypesAvail);
            EUCA_FREE(*outTypesMax);
            *outTypesLen = 0;
            return (1);
        }
    }

    sem_mywait(RESCACHE);
    memcpy(&resourceCacheLocal, resourceCache, sizeof(ccResourceCache));
    sem_mypost(RESCACHE);
    {
        *outNodes = EUCA_ZALLOC(resourceCacheLocal.numResources, sizeof(ccResource));
        if (*outNodes == NULL) {
            LOGFATAL("out of memory!\n");
            unlock_exit(1);
        } else {
            memcpy(*outNodes, resourceCacheLocal.resources, sizeof(ccResource) * resourceCacheLocal.numResources);
            *outNodesLen = resourceCacheLocal.numResources;
        }

        for (i = 0; i < resourceCacheLocal.numResources; i++) {
            res = &(resourceCacheLocal.resources[i]);

            for (j = 0; j < vmLen; j++) {
                mempool = res->availMemory;
                diskpool = res->availDisk;
                corepool = res->availCores;

                mempool -= (*ccvms)[j].mem;
                diskpool -= (*ccvms)[j].disk;
                corepool -= (*ccvms)[j].cores;
                while (mempool >= 0 && diskpool >= 0 && corepool >= 0) {
                    (*outTypesAvail)[j]++;
                    mempool -= (*ccvms)[j].mem;
                    diskpool -= (*ccvms)[j].disk;
                    corepool -= (*ccvms)[j].cores;
                }

                mempool = res->maxMemory;
                diskpool = res->maxDisk;
                corepool = res->maxCores;

                mempool -= (*ccvms)[j].mem;
                diskpool -= (*ccvms)[j].disk;
                corepool -= (*ccvms)[j].cores;
                while (mempool >= 0 && diskpool >= 0 && corepool >= 0) {
                    (*outTypesMax)[j]++;
                    mempool -= (*ccvms)[j].mem;
                    diskpool -= (*ccvms)[j].disk;
                    corepool -= (*ccvms)[j].cores;
                }
            }
        }
    }

    if (vmLen >= 5) {
        LOGDEBUG("resources summary ({avail/max}): %s{%d/%d} %s{%d/%d} %s{%d/%d} %s{%d/%d} %s{%d/%d}\n", (*ccvms)[0].name,
                 (*outTypesAvail)[0], (*outTypesMax)[0], (*ccvms)[1].name, (*outTypesAvail)[1], (*outTypesMax)[1], (*ccvms)[2].name,
                 (*outTypesAvail)[2], (*outTypesMax)[2], (*ccvms)[3].name, (*outTypesAvail)[3], (*outTypesMax)[3], (*ccvms)[4].name,
                 (*outTypesAvail)[4], (*outTypesMax)[4]);
    }

    LOGTRACE("done\n");

    shawn();

    return (0);
}