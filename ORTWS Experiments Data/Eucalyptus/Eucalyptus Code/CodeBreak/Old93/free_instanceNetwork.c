int free_instanceNetwork(char *mac, int vlan, int force, int dolock)
{
    int inuse, i;
    unsigned char hexmac[6];
    mac2hex(mac, hexmac);
    if (!maczero(hexmac)) {
        return (0);
    }

    if (dolock) {
        sem_mywait(INSTCACHE);
    }

    inuse = 0;
    if (!force) {
        // check to make sure the mac isn't in use elsewhere
        for (i = 0; i < MAXINSTANCES_PER_CC && !inuse; i++) {
            if (!strcmp(instanceCache->instances[i].ccnet.privateMac, mac) && strcmp(instanceCache->instances[i].state, "Teardown")) {
                inuse++;
            }
        }
    }

    if (dolock) {
        sem_mypost(INSTCACHE);
    }

    if (!inuse) {
        // remove private network info from system
        sem_mywait(VNET);
        vnetDisableHost(vnetconfig, mac, NULL, 0);
        if (!strcmp(vnetconfig->mode, "MANAGED") || !strcmp(vnetconfig->mode, "MANAGED-NOVLAN")) {
            vnetDelHost(vnetconfig, mac, NULL, vlan);
        }
        sem_mypost(VNET);
    }
    return (0);
}

//!
//!
//!
//! @param[in] out
//! @param[in] id
//! @param[in] amiId
//! @param[in] kernelId the kernel image identifier (eki-XXXXXXXX)
//! @param[in] ramdiskId the ramdisk image identifier (eri-XXXXXXXX)
//! @param[in] amiURL
//! @param[in] kernelURL the kernel image URL address
//! @param[in] ramdiskURL the ramdisk image URL address
//! @param[in] ownerId
//! @param[in] accountId
//! @param[in] state
//! @param[in] ccState
//! @param[in] ts
//! @param[in] reservationId
//! @param[in] ccnet
//! @param[in] ncnet
//! @param[in] ccvm
//! @param[in] ncHostIdx
//! @param[in] keyName
//! @param[in] serviceTag
//! @param[in] userData
//! @param[in] launchIndex
//! @param[in] platform
//! @param[in] bundleTaskStateName
//! @param[in] groupNames
//! @param[in] volumes
//! @param[in] volumesSize
//!
//! @return
//!
//! @pre
//!
//! @note
//!
int allocate_ccInstance(ccInstance * out, char *id, char *amiId, char *kernelId, char *ramdiskId, char *amiURL, char *kernelURL, char *ramdiskURL,
                        char *ownerId, char *accountId, char *state, char *ccState, time_t ts, char *reservationId, netConfig * ccnet,
                        netConfig * ncnet, virtualMachine * ccvm, int ncHostIdx, char *keyName, char *serviceTag, char *userData, char *launchIndex,
                        char *platform, char *bundleTaskStateName, char groupNames[][64], ncVolume * volumes, int volumesSize)
{
    if (out != NULL) {
        bzero(out, sizeof(ccInstance));
        if (id)
            euca_strncpy(out->instanceId, id, 16);
        if (amiId)
            euca_strncpy(out->amiId, amiId, 16);
        if (kernelId)
            euca_strncpy(out->kernelId, kernelId, 16);
        if (ramdiskId)
            euca_strncpy(out->ramdiskId, ramdiskId, 16);

        if (amiURL)
            euca_strncpy(out->amiURL, amiURL, 512);
        if (kernelURL)
            euca_strncpy(out->kernelURL, kernelURL, 512);
        if (ramdiskURL)
            euca_strncpy(out->ramdiskURL, ramdiskURL, 512);

        if (state)
            euca_strncpy(out->state, state, 16);
        if (state)
            euca_strncpy(out->ccState, ccState, 16);
        if (ownerId)
            euca_strncpy(out->ownerId, ownerId, 48);
        if (accountId)
            euca_strncpy(out->accountId, accountId, 48);
        if (reservationId)
            euca_strncpy(out->reservationId, reservationId, 16);
        if (keyName)
            euca_strncpy(out->keyName, keyName, 1024);
        out->ts = ts;
        out->ncHostIdx = ncHostIdx;
        if (serviceTag)
            euca_strncpy(out->serviceTag, serviceTag, 384);
        if (userData)
            euca_strncpy(out->userData, userData, 16384);
        if (launchIndex)
            euca_strncpy(out->launchIndex, launchIndex, 64);
        if (platform)
            euca_strncpy(out->platform, platform, 64);
        if (bundleTaskStateName)
            euca_strncpy(out->bundleTaskStateName, bundleTaskStateName, 64);
        if (groupNames) {
            int i;
            for (i = 0; i < 64; i++) {
                if (groupNames[i]) {
                    euca_strncpy(out->groupNames[i], groupNames[i], 64);
                }
            }
        }

        if (volumes) {
            memcpy(out->volumes, volumes, sizeof(ncVolume) * EUCA_MAX_VOLUMES);
        }
        out->volumesSize = volumesSize;

        if (ccnet)
            allocate_netConfig(&(out->ccnet), ccnet->privateMac, ccnet->privateIp, ccnet->publicIp, ccnet->vlan, ccnet->networkIndex);
        if (ncnet)
            allocate_netConfig(&(out->ncnet), ncnet->privateMac, ncnet->privateIp, ncnet->publicIp, ncnet->vlan, ncnet->networkIndex);
        if (ccvm)
            allocate_virtualMachine(&(out->ccvm), ccvm);
    }
    return (0);
}