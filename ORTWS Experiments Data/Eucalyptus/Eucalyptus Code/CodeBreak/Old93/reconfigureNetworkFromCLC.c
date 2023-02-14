int reconfigureNetworkFromCLC(void)
{
    char clcnetfile[MAX_PATH], chainmapfile[MAX_PATH], url[MAX_PATH], cmd[MAX_PATH];
    char *cloudIp = NULL, **users = NULL, **nets = NULL;
    int fd = 0, i = 0, rc = 0, ret = 0, usernetlen = 0;
    FILE *FH = NULL;

    if (strcmp(vnetconfig->mode, "MANAGED") && strcmp(vnetconfig->mode, "MANAGED-NOVLAN")) {
        return (0);
    }
    // get the latest cloud controller IP address
    if (vnetconfig->cloudIp) {
        cloudIp = hex2dot(vnetconfig->cloudIp);
    } else {
        cloudIp = strdup("localhost");
        if (!cloudIp) {
            LOGFATAL("out of memory!\n");
            unlock_exit(1);
        }
    }

    // create and populate network state files
    snprintf(clcnetfile, MAX_PATH, "/tmp/euca-clcnet-XXXXXX");
    snprintf(chainmapfile, MAX_PATH, "/tmp/euca-chainmap-XXXXXX");

    fd = safe_mkstemp(clcnetfile);
    if (fd < 0) {
        LOGERROR("cannot open clcnetfile '%s'\n", clcnetfile);
        EUCA_FREE(cloudIp);
        return (1);
    }
    chmod(clcnetfile, 0644);
    close(fd);

    fd = safe_mkstemp(chainmapfile);
    if (fd < 0) {
        LOGERROR("cannot open chainmapfile '%s'\n", chainmapfile);
        EUCA_FREE(cloudIp);
        unlink(clcnetfile);
        return (1);
    }
    chmod(chainmapfile, 0644);
    close(fd);

    // clcnet populate
    snprintf(url, MAX_PATH, "http://%s:8773/latest/network-topology", cloudIp);
    rc = http_get_timeout(url, clcnetfile, 0, 0, 10, 15);
    EUCA_FREE(cloudIp);
    if (rc) {
        LOGWARN("cannot get latest network topology from cloud controller\n");
        unlink(clcnetfile);
        unlink(chainmapfile);
        return (1);
    }
    // chainmap populate
    FH = fopen(chainmapfile, "w");
    if (!FH) {
        LOGERROR("cannot write chain/net map to chainmap file '%s'\n", chainmapfile);
        unlink(clcnetfile);
        unlink(chainmapfile);
        return (1);
    }

    sem_mywait(VNET);
    rc = vnetGetAllVlans(vnetconfig, &users, &nets, &usernetlen);
    if (rc) {
    } else {
        for (i = 0; i < usernetlen; i++) {
            fprintf(FH, "%s %s\n", users[i], nets[i]);
            EUCA_FREE(users[i]);
            EUCA_FREE(nets[i]);
        }
    }
    fclose(FH);

    EUCA_FREE(users);
    EUCA_FREE(nets);

    snprintf(cmd, MAX_PATH, EUCALYPTUS_ROOTWRAP " " EUCALYPTUS_HELPER_DIR "/euca_ipt filter %s %s", vnetconfig->eucahome, vnetconfig->eucahome,
             clcnetfile, chainmapfile);
    rc = system(cmd);
    if (rc) {
        LOGERROR("cannot run command '%s'\n", cmd);
        ret = 1;
    }
    sem_mypost(VNET);

    unlink(clcnetfile);
    unlink(chainmapfile);

    return (ret);
}