void print_abbreviated_instances(const char *gerund, char **instIds, int instIdsLen)
{
    int k = 0;
    int offset = 0;
    char list[60] = "";

    for (k = 0; ((k < instIdsLen) && (offset < ((sizeof(list) - 4)))); k++) {
        offset += snprintf(list + offset, sizeof(list) - 3 - offset, "%s%s", (k == 0) ? ("") : (", "), instIds[k]);
    }

    if (strlen(list) == (sizeof(list) - 4)) {
        sprintf(list + offset, "...");
    }

    LOGINFO("%s %d instance(s): %s\n", gerund, instIdsLen, list);
}

//!
//!
//!
//! @param[in] pMeta a pointer to the node controller (NC) metadata structure
//! @param[in] amiId
//! @param[in] kernelId the kernel image identifier (eki-XXXXXXXX)
//! @param[in] ramdiskId the ramdisk image identifier (eri-XXXXXXXX)
//! @param[in] amiURL
//! @param[in] kernelURL the kernel image URL address
//! @param[in] ramdiskURL the ramdisk image URL address
//! @param[in] instIds
//! @param[in] instIdsLen
//! @param[in] netNames
//! @param[in] netNamesLen
//! @param[in] macAddrs
//! @param[in] macAddrsLen
//! @param[in] networkIndexList
//! @param[in] networkIndexListLen
//! @param[in] uuids
//! @param[in] uuidsLen
//! @param[in] minCount
//! @param[in] maxCount
//! @param[in] accountId
//! @param[in] ownerId
//! @param[in] reservationId
//! @param[in] ccvm
//! @param[in] keyName
//! @param[in] vlan
//! @param[in] userData
//! @param[in] launchIndex
//! @param[in] platform
//! @param[in] expiryTime
//! @param[in] targetNode
//! @param[out] outInsts
//! @param[out] outInstsLen
//!
//! @return
//!
//! @pre
//!
//! @note
//!
int doRunInstances(ncMetadata * pMeta, char *amiId, char *kernelId, char *ramdiskId, char *amiURL, char *kernelURL, char *ramdiskURL, char **instIds,
                   int instIdsLen, char **netNames, int netNamesLen, char **macAddrs, int macAddrsLen, int *networkIndexList, int networkIndexListLen,
                   char **uuids, int uuidsLen, int minCount, int maxCount, char *accountId, char *ownerId, char *reservationId, virtualMachine * ccvm,
                   char *keyName, int vlan, char *userData, char *launchIndex, char *platform, int expiryTime, char *targetNode,
                   ccInstance ** outInsts, int *outInstsLen)
{
    int rc = 0, i = 0, done = 0, runCount = 0, resid = 0, foundnet = 0, error = 0, nidx = 0, thenidx = 0;
    ccInstance *myInstance = NULL, *retInsts = NULL;
    char instId[16], uuid[48];
    ccResource *res = NULL;
    char mac[32], privip[32], pubip[32];

    ncInstance *outInst = NULL;
    virtualMachine ncvm;
    netConfig ncnet;

    rc = initialize(pMeta);
    if (rc || ccIsEnabled()) {
        return (1);
    }
    print_abbreviated_instances("running", instIds, instIdsLen);
    LOGDEBUG("invoked: userId=%s, emiId=%s, kernelId=%s, ramdiskId=%s, emiURL=%s, kernelURL=%s, ramdiskURL=%s, instIdsLen=%d, netNamesLen=%d, "
             "macAddrsLen=%d, networkIndexListLen=%d, minCount=%d, maxCount=%d, accountId=%s, ownerId=%s, reservationId=%s, keyName=%s, vlan=%d, "
             "userData=%s, launchIndex=%s, platform=%s, targetNode=%s\n", SP(pMeta ? pMeta->userId : "UNSET"), SP(amiId), SP(kernelId), SP(ramdiskId),
             SP(amiURL), SP(kernelURL), SP(ramdiskURL), instIdsLen, netNamesLen, macAddrsLen, networkIndexListLen, minCount, maxCount, SP(accountId),
             SP(ownerId), SP(reservationId), SP(keyName), vlan, SP(userData), SP(launchIndex), SP(platform), SP(targetNode));

    if (config->use_proxy) {
        char walrusURL[MAX_PATH], *strptr = NULL, newURL[MAX_PATH];

        // get walrus IP
        done = 0;
        for (i = 0; i < 16 && !done; i++) {
            if (!strcmp(config->services[i].type, "walrus")) {
                snprintf(walrusURL, MAX_PATH, "%s", config->services[i].uris[0]);
                done++;
            }
        }

        if (done) {
            // cache and reset endpoint
            for (i = 0; i < ccvm->virtualBootRecordLen; i++) {
                newURL[0] = '\0';
                if (!strcmp(ccvm->virtualBootRecord[i].typeName, "machine") || !strcmp(ccvm->virtualBootRecord[i].typeName, "kernel")
                    || !strcmp(ccvm->virtualBootRecord[i].typeName, "ramdisk")) {
                    strptr = strstr(ccvm->virtualBootRecord[i].resourceLocation, "walrus://");
                    if (strptr) {
                        strptr += strlen("walrus://");
                        snprintf(newURL, MAX_PATH, "%s/%s", walrusURL, strptr);
                        LOGDEBUG("constructed cacheable URL: %s\n", newURL);
                        rc = image_cache(ccvm->virtualBootRecord[i].id, newURL);
                        if (!rc) {
                            snprintf(ccvm->virtualBootRecord[i].resourceLocation, CHAR_BUFFER_SIZE, "http://%s:8776/%s", config->proxyIp,
                                     ccvm->virtualBootRecord[i].id);
                        } else {
                            LOGWARN("could not cache image %s/%s\n", ccvm->virtualBootRecord[i].id, newURL);
                        }
                    }
                }
            }
        }
    }

    *outInstsLen = 0;

    if (!ccvm) {
        LOGERROR("invalid ccvm\n");
        return (-1);
    }
    if (minCount <= 0 || maxCount <= 0 || instIdsLen < maxCount) {
        LOGERROR("bad min or max count, or not enough instIds (%d, %d, %d)\n", minCount, maxCount, instIdsLen);
        return (-1);
    }
    // check health of the networkIndexList
    if ((!strcmp(vnetconfig->mode, "SYSTEM") || !strcmp(vnetconfig->mode, "STATIC") || !strcmp(vnetconfig->mode, "STATIC-DYNMAC"))
        || networkIndexList == NULL) {
        // disabled
        nidx = -1;
    } else {
        if ((networkIndexListLen < minCount) || (networkIndexListLen > maxCount)) {
            LOGERROR("network index length (%d) is out of bounds for min/max instances (%d-%d)\n", networkIndexListLen, minCount, maxCount);
            return (1);
        }
        for (i = 0; i < networkIndexListLen; i++) {
            if ((networkIndexList[i] < 0) || (networkIndexList[i] > (vnetconfig->numaddrs - 1))) {
                LOGERROR("network index (%d) out of bounds (0-%d)\n", networkIndexList[i], vnetconfig->numaddrs - 1);
                return (1);
            }
        }

        // all checked out
        nidx = 0;
    }

    retInsts = EUCA_ZALLOC(maxCount, sizeof(ccInstance));
    if (!retInsts) {
        LOGFATAL("out of memory!\n");
        unlock_exit(1);
    }

    runCount = 0;

    // get updated resource information

    done = 0;
    for (i = 0; i < maxCount && !done; i++) {
        snprintf(instId, 16, "%s", instIds[i]);
        if (uuidsLen > i) {
            snprintf(uuid, 48, "%s", uuids[i]);
        } else {
            snprintf(uuid, 48, "UNSET");
        }

        LOGDEBUG("running instance %s\n", instId);

        // generate new mac
        bzero(mac, 32);
        bzero(pubip, 32);
        bzero(privip, 32);

        strncpy(pubip, "0.0.0.0", 32);
        strncpy(privip, "0.0.0.0", 32);

        sem_mywait(VNET);
        if (nidx == -1) {
            rc = vnetGenerateNetworkParams(vnetconfig, instId, vlan, -1, mac, pubip, privip);
            thenidx = -1;
        } else {
            rc = vnetGenerateNetworkParams(vnetconfig, instId, vlan, networkIndexList[nidx], mac, pubip, privip);
            thenidx = nidx;
            nidx++;
        }
        if (rc) {
            foundnet = 0;
        } else {
            foundnet = 1;
        }
        sem_mypost(VNET);

        if (thenidx != -1) {
            LOGDEBUG("assigning MAC/IP: %s/%s/%s/%d\n", mac, pubip, privip, networkIndexList[thenidx]);
        } else {
            LOGDEBUG("assigning MAC/IP: %s/%s/%s/%d\n", mac, pubip, privip, thenidx);
        }

        if (mac[0] == '\0' || !foundnet) {
            LOGERROR("could not find/initialize any free network address, failing doRunInstances()\n");
        } else {
            // "run" the instance
            memcpy(&ncvm, ccvm, sizeof(virtualMachine));

            ncnet.vlan = vlan;
            if (thenidx >= 0) {
                ncnet.networkIndex = networkIndexList[thenidx];
            } else {
                ncnet.networkIndex = -1;
            }
            snprintf(ncnet.privateMac, 24, "%s", mac);
            snprintf(ncnet.privateIp, 24, "%s", privip);
            snprintf(ncnet.publicIp, 24, "%s", pubip);

            sem_mywait(RESCACHE);

            resid = 0;

            sem_mywait(CONFIG);
            rc = schedule_instance(ccvm, targetNode, &resid);
            sem_mypost(CONFIG);

            res = &(resourceCache->resources[resid]);

            // pick out the right LUN from the long version of the remote device string and create the remote dev string that NC expects
            for (int i = 0; i < EUCA_MAX_VBRS && i < ncvm.virtualBootRecordLen; i++) {
                virtualBootRecord * vbr = &(ncvm.virtualBootRecord[i]);
                if (strcmp(vbr->typeName, "ebs")) // skip all except EBS entries
                    continue;
                if (get_remoteDevForNC(res->iqn, vbr->resourceLocationPtr, vbr->resourceLocation, sizeof(vbr->resourceLocation))) {
                    LOGERROR("failed to parse remote dev string in VBR[%d]\n", i);
                    rc = 1;
                }
            }

            if (rc) {
                // could not find resource
                LOGERROR("scheduler could not find resource to run the instance on\n");
                // couldn't run this VM, remove networking information from system
                free_instanceNetwork(mac, vlan, 1, 1);
            } else {
                int pid, ret;

                // try to run the instance on the chosen resource
                LOGINFO("scheduler decided to run instance %s on resource %s, running count %d\n", instId, res->ncURL, res->running);

                outInst = NULL;

                pid = fork();
                if (pid == 0) {
                    time_t startRun, ncRunTimeout;

                    sem_mywait(RESCACHE);
                    if (res->running > 0) {
                        res->running++;
                    }
                    sem_mypost(RESCACHE);

                    ret = 0;
                    LOGTRACE("sending run instance: node=%s instanceId=%s emiId=%s mac=%s privIp=%s pubIp=%s vlan=%d networkIdx=%d key=%.32s... "
                             "mem=%d disk=%d cores=%d\n", res->ncURL, instId, SP(amiId), ncnet.privateMac, ncnet.privateIp, ncnet.publicIp,
                             ncnet.vlan, ncnet.networkIndex, SP(keyName), ncvm.mem, ncvm.disk, ncvm.cores);

                    rc = 1;
                    startRun = time(NULL);
                    if (config->schedPolicy == SCHEDPOWERSAVE) {
                        ncRunTimeout = config->wakeThresh;
                    } else {
                        ncRunTimeout = 15;
                    }

                    while (rc && ((time(NULL) - startRun) < ncRunTimeout)) {

                        // if we're running windows, and are an NC, create the pw/floppy locally
                        if (strstr(platform, "windows") && !strstr(res->ncURL, "EucalyptusNC")) {
                            //if (strstr(platform, "windows")) {
                            char cdir[MAX_PATH];
                            snprintf(cdir, MAX_PATH, EUCALYPTUS_STATE_DIR "/windows/", config->eucahome);
                            if (check_directory(cdir))
                                mkdir(cdir, 0700);
                            snprintf(cdir, MAX_PATH, EUCALYPTUS_STATE_DIR "/windows/%s/", config->eucahome, instId);
                            if (check_directory(cdir))
                                mkdir(cdir, 0700);
                            if (check_directory(cdir)) {
                                LOGERROR("could not create console/floppy cache directory '%s'\n", cdir);
                            } else {
                                // drop encrypted windows password and floppy on filesystem
                                rc = makeWindowsFloppy(config->eucahome, cdir, keyName, instId);
                                if (rc) {
                                    LOGERROR("could not create console/floppy cache\n");
                                }
                            }
                        }
                        // call StartNetwork client

                        rc = ncClientCall(pMeta, OP_TIMEOUT_PERNODE, res->lockidx, res->ncURL, "ncStartNetwork", uuid, NULL, 0, 0, vlan, NULL);
                        LOGDEBUG("sent network start request for network idx '%d' on resource '%s' uuid '%s': result '%s'\n", vlan, res->ncURL, uuid,
                                 rc ? "FAIL" : "SUCCESS");
                        rc = ncClientCall(pMeta, OP_TIMEOUT_PERNODE, res->lockidx, res->ncURL, "ncRunInstance", uuid, instId, reservationId, &ncvm,
                                          amiId, amiURL, kernelId, kernelURL, ramdiskId, ramdiskURL, ownerId, accountId, keyName, &ncnet, userData,
                                          launchIndex, platform, expiryTime, netNames, netNamesLen, &outInst);
                        LOGDEBUG("sent run request for instance '%s' on resource '%s': result '%s' uuis '%s'\n", instId, res->ncURL, uuid,
                                 rc ? "FAIL" : "SUCCESS");
                        if (rc) {
                            // make sure we get the latest topology information before trying again
                            sem_mywait(CONFIG);
                            memcpy(pMeta->services, config->services, sizeof(serviceInfoType) * 16);
                            memcpy(pMeta->disabledServices, config->disabledServices, sizeof(serviceInfoType) * 16);
                            memcpy(pMeta->notreadyServices, config->notreadyServices, sizeof(serviceInfoType) * 16);
                            sem_mypost(CONFIG);
                            sleep(1);
                        }
                    }
                    if (!rc) {
                        ret = 0;
                    } else {
                        ret = 1;
                    }

                    sem_mywait(RESCACHE);
                    if (res->running > 0) {
                        res->running--;
                    }
                    sem_mypost(RESCACHE);

                    exit(ret);
                } else {
                    rc = 0;
                    LOGDEBUG("call complete (pid/rc): %d/%d\n", pid, rc);
                }
                if (rc != 0) {
                    // problem
                    LOGERROR("tried to run the VM, but runInstance() failed; marking resource '%s' as down\n", res->ncURL);
                    res->state = RESDOWN;
                    i--;
                    // couldn't run this VM, remove networking information from system
                    free_instanceNetwork(mac, vlan, 1, 1);
                } else {
                    res->availMemory -= ccvm->mem;
                    res->availDisk -= ccvm->disk;
                    res->availCores -= ccvm->cores;

                    LOGDEBUG("resource information after schedule/run: %d/%d, %d/%d, %d/%d\n", res->availMemory, res->maxMemory,
                             res->availCores, res->maxCores, res->availDisk, res->maxDisk);

                    myInstance = &(retInsts[runCount]);
                    bzero(myInstance, sizeof(ccInstance));

                    allocate_ccInstance(myInstance, instId, amiId, kernelId, ramdiskId, amiURL, kernelURL, ramdiskURL, ownerId, accountId, "Pending",
                                        "", time(NULL), reservationId, &ncnet, &ncnet, ccvm, resid, keyName, resourceCache->resources[resid].ncURL,
                                        userData, launchIndex, platform, myInstance->bundleTaskStateName, myInstance->groupNames, myInstance->volumes,
                                        myInstance->volumesSize);

                    sensor_add_resource(myInstance->instanceId, "instance", uuid);
                    sensor_set_resource_alias(myInstance->instanceId, myInstance->ncnet.privateIp);

                    // start up DHCP
                    sem_mywait(CONFIG);
                    config->kick_dhcp = 1;
                    sem_mypost(CONFIG);

                    // add the instance to the cache, and continue on
                    // add_instanceCache(myInstance->instanceId, myInstance);
                    refresh_instanceCache(myInstance->instanceId, myInstance);
                    print_ccInstance("", myInstance);

                    runCount++;
                }
            }

            sem_mypost(RESCACHE);

        }

    }
    *outInstsLen = runCount;
    *outInsts = retInsts;

    LOGTRACE("done\n");

    shawn();

    if (runCount < 1) {
        error++;
        LOGERROR("unable to run input instance\n");
    }
    if (error) {
        return (1);
    }
    return (0);
}