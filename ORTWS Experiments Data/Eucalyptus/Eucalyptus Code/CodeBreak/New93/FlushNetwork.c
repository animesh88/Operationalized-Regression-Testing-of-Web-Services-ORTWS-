int doFlushNetwork(ncMetadata * pMeta, char *accountId, char *destName)
{
    int rc;

    rc = initialize(pMeta);
    if (rc || ccIsEnabled()) {
        return (1);
    }

    if (!strcmp(vnetconfig->mode, "SYSTEM") || !strcmp(vnetconfig->mode, "STATIC") || !strcmp(vnetconfig->mode, "STATIC-DYNMAC")) {
        return (0);
    }

    sem_mywait(VNET);
    rc = vnetFlushTable(vnetconfig, accountId, destName);
    sem_mypost(VNET);
    return (rc);
}