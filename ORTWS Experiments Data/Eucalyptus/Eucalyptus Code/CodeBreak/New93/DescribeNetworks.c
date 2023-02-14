int doDescribeNetworks(ncMetadata * pMeta, char *nameserver, char **ccs, int ccsLen, vnetConfig * outvnetConfig)
{
    int rc;

    rc = initialize(pMeta);
    if (rc || ccIsEnabled()) {
        return (1);
    }

    LOGDEBUG("invoked: userId=%s, nameserver=%s, ccsLen=%d\n", SP(pMeta ? pMeta->userId : "UNSET"), SP(nameserver), ccsLen);

    // ensure that we have the latest network state from the CC (based on instance cache) before responding to CLC
    rc = checkActiveNetworks();
    if (rc) {
        LOGWARN("checkActiveNetworks() failed, will attempt to re-sync\n");
    }

    sem_mywait(VNET);
    if (nameserver) {
        vnetconfig->euca_ns = dot2hex(nameserver);
    }
    if (!strcmp(vnetconfig->mode, "MANAGED") || !strcmp(vnetconfig->mode, "MANAGED-NOVLAN")) {
        rc = vnetSetCCS(vnetconfig, ccs, ccsLen);
        rc = vnetSetupTunnels(vnetconfig);
    }
    memcpy(outvnetConfig, vnetconfig, sizeof(vnetConfig));

    sem_mypost(VNET);
    LOGTRACE("done\n");

    shawn();

    return (0);
}