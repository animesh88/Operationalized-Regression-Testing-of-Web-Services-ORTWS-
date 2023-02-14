int image_cache_proxykick(ccResource * res, int *numHosts)
{
    char cmd[MAX_PATH];
    char *nodestr = NULL;
    int i, rc;

    nodestr = EUCA_ZALLOC((((*numHosts) * 128) + (*numHosts) + 1), sizeof(char));
    if (!nodestr) {
        LOGFATAL("out of memory!\n");
        unlock_exit(1);
    }

    for (i = 0; i < (*numHosts); i++) {
        strcat(nodestr, res[i].hostname);
        strcat(nodestr, " ");
    }

    snprintf(cmd, MAX_PATH, EUCALYPTUS_HELPER_DIR "/dynserv.pl %s %s", config->eucahome, config->proxyPath, nodestr);
    LOGDEBUG("running cmd '%s'\n", cmd);
    rc = system(cmd);

    EUCA_FREE(nodestr);
    return (rc);
}
