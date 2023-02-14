int doDescribePublicAddresses(ncMetadata * pMeta, publicip ** outAddresses, int *outAddressesLen)
{
    int rc, ret;

    rc = initialize(pMeta);
    if (rc || ccIsEnabled()) {
        return (1);
    }

    LOGDEBUG("invoked: userId=%s\n", SP(pMeta ? pMeta->userId : "UNSET"));

    ret = 0;
    if (!strcmp(vnetconfig->mode, "MANAGED") || !strcmp(vnetconfig->mode, "MANAGED-NOVLAN")) {
        sem_mywait(VNET);
        *outAddresses = vnetconfig->publicips;
        *outAddressesLen = NUMBER_OF_PUBLIC_IPS;
        sem_mypost(VNET);
    } else {
        *outAddresses = NULL;
        *outAddressesLen = 0;
        ret = 0;
    }

    LOGTRACE("done\n");

    shawn();

    return (ret);
}