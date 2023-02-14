int ncInstance_to_ccInstance(ccInstance * dst, ncInstance * src)
{
    int i;

    euca_strncpy(dst->uuid, src->uuid, 48);
    euca_strncpy(dst->instanceId, src->instanceId, 16);
    euca_strncpy(dst->reservationId, src->reservationId, 16);
    euca_strncpy(dst->accountId, src->accountId, 48);
    euca_strncpy(dst->ownerId, src->ownerId, 48);
    euca_strncpy(dst->amiId, src->imageId, 16);
    euca_strncpy(dst->kernelId, src->kernelId, 16);
    euca_strncpy(dst->ramdiskId, src->ramdiskId, 16);
    euca_strncpy(dst->keyName, src->keyName, 1024);
    euca_strncpy(dst->launchIndex, src->launchIndex, 64);
    euca_strncpy(dst->platform, src->platform, 64);
    euca_strncpy(dst->bundleTaskStateName, src->bundleTaskStateName, 64);
    euca_strncpy(dst->createImageTaskStateName, src->createImageTaskStateName, 64);
    euca_strncpy(dst->userData, src->userData, 16384);
    euca_strncpy(dst->state, src->stateName, 16);
    dst->ts = src->launchTime;

    memcpy(&(dst->ncnet), &(src->ncnet), sizeof(netConfig));

    for (i = 0; i < src->groupNamesSize && i < 64; i++) {
        snprintf(dst->groupNames[i], 64, "%s", src->groupNames[i]);
    }

    memcpy(dst->volumes, src->volumes, sizeof(ncVolume) * EUCA_MAX_VOLUMES);
    dst->volumesSize = 0;
    for (i = 0; i < EUCA_MAX_VOLUMES; i++) {
        if (strlen(dst->volumes[i].volumeId) == 0)
            break;
        dst->volumesSize++;
    }

    memcpy(&(dst->ccvm), &(src->params), sizeof(virtualMachine));

    dst->blkbytes = src->blkbytes;
    dst->netbytes = src->netbytes;

    return (0);
}