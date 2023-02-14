int syncNetworkState(void)
{
    int rc, ret = 0;

    LOGDEBUG("syncNetworkState(): syncing public/private IP mapping ground truth with local state\n");
    rc = map_instanceCache(validCmp, NULL, instIpSync, NULL);
    if (rc) {
        LOGWARN("syncNetworkState(): network sync implies network restore is necessary\n");
        ret++;
    }

    return (ret);
}