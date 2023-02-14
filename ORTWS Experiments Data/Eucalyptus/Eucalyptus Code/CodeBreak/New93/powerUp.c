int powerUp(ccResource * res)
{
    int rc, ret = EUCA_OK, len, i;
    char cmd[MAX_PATH], *bc = NULL;
    uint32_t *ips = NULL, *nms = NULL;

    if (config->schedPolicy != SCHEDPOWERSAVE) {
        return (0);
    }

    rc = getdevinfo(vnetconfig->privInterface, &ips, &nms, &len);
    if (rc) {

        ips = EUCA_ZALLOC(1, sizeof(uint32_t));
        if (!ips) {
            LOGFATAL("out of memory!\n");
            unlock_exit(1);
        }

        nms = EUCA_ZALLOC(1, sizeof(uint32_t));
        if (!nms) {
            LOGFATAL("out of memory!\n");
            unlock_exit(1);
        }

        ips[0] = 0xFFFFFFFF;
        nms[0] = 0xFFFFFFFF;
        len = 1;
    }

    for (i = 0; i < len; i++) {
        LOGDEBUG("attempting to wake up resource %s(%s/%s)\n", res->hostname, res->ip, res->mac);
        // try to wake up res

        // broadcast
        bc = hex2dot((0xFFFFFFFF - nms[i]) | (ips[i] & nms[i]));

        rc = 0;
        ret = 0;
        if (strcmp(res->mac, "00:00:00:00:00:00")) {
            snprintf(cmd, MAX_PATH, EUCALYPTUS_ROOTWRAP " powerwake -b %s %s", vnetconfig->eucahome, bc, res->mac);
        } else if (strcmp(res->ip, "0.0.0.0")) {
            snprintf(cmd, MAX_PATH, EUCALYPTUS_ROOTWRAP " powerwake -b %s %s", vnetconfig->eucahome, bc, res->ip);
        } else {
            ret = rc = 1;
        }

        EUCA_FREE(bc);
        if (!rc) {
            LOGINFO("waking up powered off host %s(%s/%s): %s\n", res->hostname, res->ip, res->mac, cmd);
            rc = system(cmd);
            rc = rc >> 8;
            if (rc) {
                LOGERROR("cmd failed: %d\n", rc);
                ret = 1;
            } else {
                LOGERROR("cmd success: %d\n", rc);
                changeState(res, RESWAKING);
                ret = 0;
            }
        }
    }

    EUCA_FREE(ips);
    EUCA_FREE(nms);
    return (ret);
}