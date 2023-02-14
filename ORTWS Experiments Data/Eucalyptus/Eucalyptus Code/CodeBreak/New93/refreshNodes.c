int refreshNodes(ccConfig * config, ccResource ** res, int *numHosts)
{
    int rc, i, lockmod;
    char *tmpstr, *ipbuf;
    char ncservice[512];
    int ncport;
    char **hosts;

    *numHosts = 0;
    *res = NULL;

    tmpstr = configFileValue(CONFIG_NC_SERVICE);
    if (!tmpstr) {
        // error
        LOGFATAL("parsing config files (%s,%s) for NC_SERVICE\n", config->configFiles[1], config->configFiles[0]);
        return (1);
    } else {
        if (tmpstr) {
            snprintf(ncservice, 512, "%s", tmpstr);
        }

    }
    EUCA_FREE(tmpstr);

    tmpstr = configFileValue(CONFIG_NC_PORT);
    if (!tmpstr) {
        // error
        LOGFATAL("parsing config files (%s,%s) for NC_PORT\n", config->configFiles[1], config->configFiles[0]);
        return (1);
    } else {
        if (tmpstr)
            ncport = atoi(tmpstr);
    }
    EUCA_FREE(tmpstr);

    tmpstr = configFileValue(CONFIG_NODES);
    if (!tmpstr) {
        // error
        LOGWARN("NODES parameter is missing from config files(%s,%s)\n", config->configFiles[1], config->configFiles[0]);
        return (0);
    } else {
        hosts = from_var_to_char_list(tmpstr);
        if (hosts == NULL) {
            LOGWARN("NODES list is empty in config files(%s,%s)\n", config->configFiles[1], config->configFiles[0]);
            EUCA_FREE(tmpstr);
            return (0);
        }

        *numHosts = 0;
        lockmod = 0;
        i = 0;
        while (hosts[i] != NULL) {
            (*numHosts)++;
            *res = EUCA_REALLOC(*res, (*numHosts), sizeof(ccResource));
            bzero(&((*res)[*numHosts - 1]), sizeof(ccResource));
            snprintf((*res)[*numHosts - 1].hostname, 256, "%s", hosts[i]);

            ipbuf = host2ip(hosts[i]);
            if (ipbuf) {
                snprintf((*res)[*numHosts - 1].ip, 24, "%s", ipbuf);
            }
            EUCA_FREE(ipbuf);

            (*res)[*numHosts - 1].ncPort = ncport;
            snprintf((*res)[*numHosts - 1].ncService, 128, "%s", ncservice);
            snprintf((*res)[*numHosts - 1].ncURL, 384, "http://%s:%d/%s", hosts[i], ncport, ncservice);
            (*res)[*numHosts - 1].state = RESDOWN;
            (*res)[*numHosts - 1].lastState = RESDOWN;
            (*res)[*numHosts - 1].lockidx = NCCALL0 + lockmod;
            lockmod = (lockmod + 1) % 32;
            EUCA_FREE(hosts[i]);
            i++;
        }
    }

    if (config->use_proxy) {
        rc = image_cache_proxykick(*res, numHosts);
        if (rc) {
            LOGERROR("could not restart the image proxy\n");
        }
    }

    EUCA_FREE(hosts);
    EUCA_FREE(tmpstr);

    return (0);
}