int doStopNetwork(ncMetadata * pMeta, char *accountId, char *netName, int vlan)
{
    int rc, ret;

    rc = initialize(pMeta);
    if (rc || ccIsEnabled()) {
        return (1);
    }

    LOGINFO("stopping network %d\n", vlan);
    LOGDEBUG("invoked: userId=%s, accountId=%s, netName=%s, vlan=%d\n", SP(pMeta ? pMeta->userId : "UNSET"), SP(accountId), SP(netName), vlan);
    if (!pMeta || !netName || vlan < 0) {
        LOGERROR("bad input params\n");
    }

    if (!strcmp(vnetconfig->mode, "SYSTEM") || !strcmp(vnetconfig->mode, "STATIC") || !strcmp(vnetconfig->mode, "STATIC-DYNMAC")) {
        ret = 0;
    } else {

        sem_mywait(VNET);
        if (pMeta != NULL) {
            rc = vnetStopNetwork(vnetconfig, vlan, accountId, netName);
        }
        ret = rc;
        sem_mypost(VNET);
    }

    LOGTRACE("done\n");

    shawn();

    return (ret);
}