void print_ccInstance(char *tag, ccInstance * in)
{
    char *volbuf, *groupbuf;
    int i;

    volbuf = EUCA_ZALLOC((EUCA_MAX_VOLUMES * 2), sizeof(ncVolume));
    if (!volbuf) {
        LOGFATAL("out of memory!\n");
        unlock_exit(1);
    }

    groupbuf = EUCA_ZALLOC((64 * 32 * 2), sizeof(char));
    if (!groupbuf) {
        LOGFATAL("out of memory!\n");
        unlock_exit(1);
    }

    for (i = 0; i < 64; i++) {
        if (in->groupNames[i][0] != '\0') {
            strncat(groupbuf, in->groupNames[i], 64);
            strncat(groupbuf, " ", 1);
        }
    }

    for (i = 0; i < EUCA_MAX_VOLUMES; i++) {
        if (in->volumes[i].volumeId[0] != '\0') {
            strncat(volbuf, in->volumes[i].volumeId, CHAR_BUFFER_SIZE);
            strncat(volbuf, ",", 1);
            strncat(volbuf, in->volumes[i].remoteDev, VERY_BIG_CHAR_BUFFER_SIZE);
            strncat(volbuf, ",", 1);
            strncat(volbuf, in->volumes[i].localDev, CHAR_BUFFER_SIZE);
            strncat(volbuf, ",", 1);
            strncat(volbuf, in->volumes[i].stateName, CHAR_BUFFER_SIZE);
            strncat(volbuf, " ", 1);
        }
    }

    LOGDEBUG("%s instanceId=%s reservationId=%s state=%s accountId=%s ownerId=%s ts=%ld keyName=%s ccnet={privateIp=%s publicIp=%s privateMac=%s "
             "vlan=%d networkIndex=%d} ccvm={cores=%d mem=%d disk=%d} ncHostIdx=%d serviceTag=%s userData=%s launchIndex=%s platform=%s "
             "bundleTaskStateName=%s, volumesSize=%d volumes={%s} groupNames={%s} migration_state=%s\n", tag, in->instanceId, in->reservationId, in->state,
             in->accountId, in->ownerId, in->ts, in->keyName, in->ccnet.privateIp, in->ccnet.publicIp, in->ccnet.privateMac, in->ccnet.vlan,
             in->ccnet.networkIndex, in->ccvm.cores, in->ccvm.mem, in->ccvm.disk, in->ncHostIdx, in->serviceTag, in->userData, in->launchIndex,
             in->platform, in->bundleTaskStateName, in->volumesSize, volbuf, groupbuf,
             migration_state_names[in->migration_state]);

    EUCA_FREE(volbuf);
    EUCA_FREE(groupbuf);
}