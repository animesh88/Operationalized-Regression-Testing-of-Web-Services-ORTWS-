int init_config(void)
{
    ccResource *res = NULL;
    char *tmpstr = NULL, *proxyIp = NULL;
    int rc, numHosts, use_wssec, use_tunnels, use_proxy, proxy_max_cache_size, schedPolicy, idleThresh, wakeThresh, i;

    char configFiles[2][MAX_PATH], netPath[MAX_PATH], eucahome[MAX_PATH], policyFile[MAX_PATH], home[MAX_PATH], proxyPath[MAX_PATH], arbitrators[256];

    time_t instanceTimeout, ncPollingFrequency, clcPollingFrequency, ncFanout;

    // read in base config information
    tmpstr = getenv(EUCALYPTUS_ENV_VAR_NAME);
    if (!tmpstr) {
        snprintf(home, MAX_PATH, "/");
    } else {
        snprintf(home, MAX_PATH, "%s", tmpstr);
    }

    bzero(configFiles[0], MAX_PATH);
    bzero(configFiles[1], MAX_PATH);
    bzero(netPath, MAX_PATH);
    bzero(policyFile, MAX_PATH);

    snprintf(configFiles[1], MAX_PATH, EUCALYPTUS_CONF_LOCATION, home);
    snprintf(configFiles[0], MAX_PATH, EUCALYPTUS_CONF_OVERRIDE_LOCATION, home);
    snprintf(netPath, MAX_PATH, CC_NET_PATH_DEFAULT, home);
    snprintf(policyFile, MAX_PATH, EUCALYPTUS_KEYS_DIR "/nc-client-policy.xml", home);
    snprintf(eucahome, MAX_PATH, "%s/", home);

    sem_mywait(INIT);

    if (config_init && config->initialized) {
        // this means that this thread has already been initialized
        sem_mypost(INIT);
        return (0);
    }

    if (config->initialized) {
        config_init = 1;
        sem_mypost(INIT);
        return (0);
    }

    LOGINFO("initializing CC configuration\n");

    configInitValues(configKeysRestartCC, configKeysNoRestartCC);
    readConfigFile(configFiles, 2);

    // DHCP configuration section
    {
        char *daemon = NULL,
            *dhcpuser = NULL,
            *numaddrs = NULL,
            *pubmode = NULL,
            *pubmacmap = NULL,
            *pubips = NULL,
            *pubInterface = NULL,
            *privInterface = NULL, *pubSubnet = NULL, *pubSubnetMask = NULL, *pubBroadcastAddress = NULL, *pubRouter = NULL, *pubDomainname =
            NULL, *pubDNS = NULL, *localIp = NULL, *macPrefix = NULL;

        uint32_t *ips, *nms;
        int initFail = 0, len, usednew = 0;;

        // DHCP Daemon Configuration Params
        daemon = configFileValue("VNET_DHCPDAEMON");
        if (!daemon) {
            LOGWARN("no VNET_DHCPDAEMON defined in config, using default\n");
        }

        dhcpuser = configFileValue("VNET_DHCPUSER");
        if (!dhcpuser) {
            dhcpuser = strdup("root");
            if (!dhcpuser) {
                LOGFATAL("Out of memory\n");
                unlock_exit(1);
            }
        }

        pubmode = configFileValue("VNET_MODE");
        if (!pubmode) {
            LOGWARN("VNET_MODE is not defined, defaulting to 'SYSTEM'\n");
            pubmode = strdup("SYSTEM");
            if (!pubmode) {
                LOGFATAL("Out of memory\n");
                unlock_exit(1);
            }
        }

        macPrefix = configFileValue("VNET_MACPREFIX");
        if (!macPrefix) {
            LOGWARN("VNET_MACPREFIX is not defined, defaulting to 'd0:0d'\n");
            macPrefix = strdup("d0:0d");
            if (!macPrefix) {
                LOGFATAL("Out of memory!\n");
                unlock_exit(1);
            }
        } else {
            unsigned int a = 0, b = 0;
            if (sscanf(macPrefix, "%02X:%02X", &a, &b) != 2 || (a > 0xFF || b > 0xFF)) {
                LOGWARN("VNET_MACPREFIX is not defined, defaulting to 'd0:0d'\n");
                EUCA_FREE(macPrefix);
                macPrefix = strdup("d0:0d");
            }
        }

        pubInterface = configFileValue("VNET_PUBINTERFACE");
        if (!pubInterface) {
            LOGWARN("VNET_PUBINTERFACE is not defined, defaulting to 'eth0'\n");
            pubInterface = strdup("eth0");
            if (!pubInterface) {
                LOGFATAL("out of memory!\n");
                unlock_exit(1);
            }
        } else {
            usednew = 1;
        }

        privInterface = NULL;
        privInterface = configFileValue("VNET_PRIVINTERFACE");
        if (!privInterface) {
            LOGWARN("VNET_PRIVINTERFACE is not defined, defaulting to 'eth0'\n");
            privInterface = strdup("eth0");
            if (!privInterface) {
                LOGFATAL("out of memory!\n");
                unlock_exit(1);
            }
            usednew = 0;
        }

        if (!usednew) {
            tmpstr = NULL;
            tmpstr = configFileValue("VNET_INTERFACE");
            if (tmpstr) {
                LOGWARN("VNET_INTERFACE is deprecated, please use VNET_PUBINTERFACE and VNET_PRIVINTERFACE instead. Will set both to value of "
                        "VNET_INTERFACE (%s) for now.\n", tmpstr);
                EUCA_FREE(pubInterface);
                pubInterface = strdup(tmpstr);
                if (!pubInterface) {
                    LOGFATAL("out of memory!\n");
                    unlock_exit(1);
                }

                EUCA_FREE(privInterface);
                privInterface = strdup(tmpstr);
                if (!privInterface) {
                    LOGFATAL("out of memory!\n");
                    unlock_exit(1);
                }
            }
            EUCA_FREE(tmpstr);
        }

        if (pubmode && (!strcmp(pubmode, "STATIC") || !strcmp(pubmode, "STATIC-DYNMAC"))) {
            pubSubnet = configFileValue("VNET_SUBNET");
            pubSubnetMask = configFileValue("VNET_NETMASK");
            pubBroadcastAddress = configFileValue("VNET_BROADCAST");
            pubRouter = configFileValue("VNET_ROUTER");
            pubDNS = configFileValue("VNET_DNS");
            pubDomainname = configFileValue("VNET_DOMAINNAME");
            pubmacmap = configFileValue("VNET_MACMAP");
            pubips = configFileValue("VNET_PUBLICIPS");

            if (!pubSubnet || !pubSubnetMask || !pubBroadcastAddress || !pubRouter || !pubDNS || (!strcmp(pubmode, "STATIC") && !pubmacmap)
                || (!strcmp(pubmode, "STATIC-DYNMAC") && !pubips)) {
                LOGFATAL("in '%s' network mode, you must specify values for 'VNET_SUBNET, VNET_NETMASK, VNET_BROADCAST, VNET_ROUTER, "
                         "VNET_DNS and %s'\n", pubmode, (!strcmp(pubmode, "STATIC")) ? "VNET_MACMAP" : "VNET_PUBLICIPS");
                initFail = 1;
            }

        } else if (pubmode && (!strcmp(pubmode, "MANAGED") || !strcmp(pubmode, "MANAGED-NOVLAN"))) {
            numaddrs = configFileValue("VNET_ADDRSPERNET");
            pubSubnet = configFileValue("VNET_SUBNET");
            pubSubnetMask = configFileValue("VNET_NETMASK");
            pubDNS = configFileValue("VNET_DNS");
            pubDomainname = configFileValue("VNET_DOMAINNAME");
            pubips = configFileValue("VNET_PUBLICIPS");
            localIp = configFileValue("VNET_LOCALIP");
            if (!localIp) {
                LOGWARN("VNET_LOCALIP not defined, will attempt to auto-discover (consider setting this explicitly if tunnelling does not function "
                        "properly.)\n");
            }

            if (!pubSubnet || !pubSubnetMask || !pubDNS || !numaddrs) {
                LOGFATAL("in 'MANAGED' or 'MANAGED-NOVLAN' network mode, you must specify values for 'VNET_SUBNET, VNET_NETMASK, "
                         "VNET_ADDRSPERNET, and VNET_DNS'\n");
                initFail = 1;
            }
        }

        if (initFail) {
            LOGFATAL("bad network parameters, must fix before system will work\n");
            EUCA_FREE(pubSubnet);
            EUCA_FREE(pubSubnetMask);
            EUCA_FREE(pubBroadcastAddress);
            EUCA_FREE(pubRouter);
            EUCA_FREE(pubDomainname);
            EUCA_FREE(pubDNS);
            EUCA_FREE(pubmacmap);
            EUCA_FREE(numaddrs);
            EUCA_FREE(pubips);
            EUCA_FREE(localIp);
            EUCA_FREE(pubInterface);
            EUCA_FREE(privInterface);
            EUCA_FREE(dhcpuser);
            EUCA_FREE(daemon);
            EUCA_FREE(pubmode);
            EUCA_FREE(macPrefix);
            sem_mypost(INIT);
            return (1);
        }

        sem_mywait(VNET);

        int ret = vnetInit(vnetconfig, pubmode, eucahome, netPath, CLC, pubInterface, privInterface, numaddrs, pubSubnet, pubSubnetMask,
                           pubBroadcastAddress, pubDNS, pubDomainname, pubRouter, daemon,
                           dhcpuser, NULL, localIp, macPrefix);
        EUCA_FREE(pubSubnet);
        EUCA_FREE(pubSubnetMask);
        EUCA_FREE(pubBroadcastAddress);
        EUCA_FREE(pubDomainname);
        EUCA_FREE(pubDNS);
        EUCA_FREE(pubRouter);
        EUCA_FREE(numaddrs);
        EUCA_FREE(pubmode);
        EUCA_FREE(dhcpuser);
        EUCA_FREE(daemon);
        EUCA_FREE(privInterface);
        EUCA_FREE(pubInterface);
        EUCA_FREE(macPrefix);
        EUCA_FREE(localIp);

        if (ret > 0) {
            sem_mypost(VNET);
            sem_mypost(INIT);
            return (1);
        }

        vnetAddDev(vnetconfig, vnetconfig->privInterface);

        if (pubmacmap) {
            char *mac = NULL, *ip = NULL, *ptra = NULL, *toka = NULL, *ptrb = NULL;
            toka = strtok_r(pubmacmap, " ", &ptra);
            while (toka) {
                mac = ip = NULL;
                mac = strtok_r(toka, "=", &ptrb);
                ip = strtok_r(NULL, "=", &ptrb);
                if (mac && ip) {
                    vnetAddHost(vnetconfig, mac, ip, 0, -1);
                }
                toka = strtok_r(NULL, " ", &ptra);
            }
            vnetKickDHCP(vnetconfig);
            EUCA_FREE(pubmacmap);
        }else if (pubips) {
            char *ip, *ptra, *toka;
            toka = strtok_r(pubips, " ", &ptra);
            while (toka) {
                ip = toka;
                if (ip) {
                    rc = vnetAddPublicIP(vnetconfig, ip);
                    if (rc) {
                        LOGERROR("could not add public IP '%s'\n", ip);
                    }
                }
                toka = strtok_r(NULL, " ", &ptra);
            }

            // detect and populate ips
            if (vnetCountLocalIP(vnetconfig) <= 0) {
                ips = nms = NULL;
                rc = getdevinfo("all", &ips, &nms, &len);
                if (!rc) {
                    for (i = 0; i < len; i++) {
                        char *theip = NULL;
                        theip = hex2dot(ips[i]);
                        if (vnetCheckPublicIP(vnetconfig, theip)) {
                            vnetAddLocalIP(vnetconfig, ips[i]);
                        }
                        EUCA_FREE(theip);
                    }
                }
                EUCA_FREE(ips);
                EUCA_FREE(nms);
            }
            //EUCA_FREE(pubips);
        }

        EUCA_FREE(pubips);
        sem_mypost(VNET);
    }

    tmpstr = configFileValue("SCHEDPOLICY");
    if (!tmpstr) {
        // error
        LOGWARN("parsing config file (%s) for SCHEDPOLICY, defaulting to GREEDY\n", configFiles[0]);
        schedPolicy = SCHEDGREEDY;
        tmpstr = NULL;
    } else {
        if (!strcmp(tmpstr, "GREEDY"))
            schedPolicy = SCHEDGREEDY;
        else if (!strcmp(tmpstr, "ROUNDROBIN"))
            schedPolicy = SCHEDROUNDROBIN;
        else if (!strcmp(tmpstr, "POWERSAVE"))
            schedPolicy = SCHEDPOWERSAVE;
        else
            schedPolicy = SCHEDGREEDY;
    }
    EUCA_FREE(tmpstr);

    // powersave options
    tmpstr = configFileValue("POWER_IDLETHRESH");
    if (!tmpstr) {
        if (SCHEDPOWERSAVE == schedPolicy)
            LOGWARN("parsing config file (%s) for POWER_IDLETHRESH, defaulting to 300 seconds\n", configFiles[0]);
        idleThresh = 300;
        tmpstr = NULL;
    } else {
        idleThresh = atoi(tmpstr);
        if (idleThresh < 300) {
            LOGWARN("POWER_IDLETHRESH set too low (%d seconds), resetting to minimum (300 seconds)\n", idleThresh);
            idleThresh = 300;
        }
    }
    EUCA_FREE(tmpstr);

    tmpstr = configFileValue("POWER_WAKETHRESH");
    if (!tmpstr) {
        if (SCHEDPOWERSAVE == schedPolicy)
            LOGWARN("parsing config file (%s) for POWER_WAKETHRESH, defaulting to 300 seconds\n", configFiles[0]);
        wakeThresh = 300;
        tmpstr = NULL;
    } else {
        wakeThresh = atoi(tmpstr);
        if (wakeThresh < 300) {
            LOGWARN("POWER_WAKETHRESH set too low (%d seconds), resetting to minimum (300 seconds)\n", wakeThresh);
            wakeThresh = 300;
        }
    }
    EUCA_FREE(tmpstr);

    // some administrative options
    tmpstr = configFileValue("NC_POLLING_FREQUENCY");
    if (!tmpstr) {
        ncPollingFrequency = 6;
        tmpstr = NULL;
    } else {
        ncPollingFrequency = atoi(tmpstr);
        if (ncPollingFrequency < 6) {
            LOGWARN("NC_POLLING_FREQUENCY set too low (%ld seconds), resetting to minimum (6 seconds)\n", ncPollingFrequency);
            ncPollingFrequency = 6;
        }
    }
    EUCA_FREE(tmpstr);

    tmpstr = configFileValue("CLC_POLLING_FREQUENCY");
    if (!tmpstr) {
        clcPollingFrequency = 6;
        tmpstr = NULL;
    } else {
        clcPollingFrequency = atoi(tmpstr);
        if (clcPollingFrequency < 1) {
            LOGWARN("CLC_POLLING_FREQUENCY set too low (%ld seconds), resetting to default (6 seconds)\n", clcPollingFrequency);
            clcPollingFrequency = 6;
        }
    }
    EUCA_FREE(tmpstr);

    // CC Arbitrators
    tmpstr = configFileValue("CC_ARBITRATORS");
    if (tmpstr) {
        snprintf(arbitrators, 255, "%s", tmpstr);
        EUCA_FREE(tmpstr);
    } else {
        bzero(arbitrators, 256);
    }

    tmpstr = configFileValue("NC_FANOUT");
    if (!tmpstr) {
        ncFanout = 1;
        tmpstr = NULL;
    } else {
        ncFanout = atoi(tmpstr);
        if (ncFanout < 1 || ncFanout > 32) {
            LOGWARN("NC_FANOUT set out of bounds (min=%d max=%d) (current=%ld), resetting to default (1 NC)\n", 1, 32, ncFanout);
            ncFanout = 1;
        }
    }
    EUCA_FREE(tmpstr);

    tmpstr = configFileValue("INSTANCE_TIMEOUT");
    if (!tmpstr) {
        instanceTimeout = 300;
        tmpstr = NULL;
    } else {
        instanceTimeout = atoi(tmpstr);
        if (instanceTimeout < 30) {
            LOGWARN("INSTANCE_TIMEOUT set too low (%ld seconds), resetting to minimum (30 seconds)\n", instanceTimeout);
            instanceTimeout = 30;
        }
    }
    EUCA_FREE(tmpstr);

    // WS-Security
    use_wssec = 0;
    tmpstr = configFileValue("ENABLE_WS_SECURITY");
    if (!tmpstr) {
        // error
        LOGFATAL("parsing config file (%s) for ENABLE_WS_SECURITY\n", configFiles[0]);
        sem_mypost(INIT);
        return (1);
    } else {
        if (!strcmp(tmpstr, "Y")) {
            use_wssec = 1;
        }
    }
    EUCA_FREE(tmpstr);

    // Multi-cluster tunneling
    use_tunnels = 1;
    tmpstr = configFileValue("DISABLE_TUNNELING");
    if (tmpstr) {
        if (!strcmp(tmpstr, "Y")) {
            use_tunnels = 0;
        }
    }
    EUCA_FREE(tmpstr);

    // CC Image Caching
    proxyIp = NULL;
    use_proxy = 0;
    tmpstr = configFileValue("CC_IMAGE_PROXY");
    if (tmpstr) {
        proxyIp = strdup(tmpstr);
        if (!proxyIp) {
            LOGFATAL("out of memory!\n");
            unlock_exit(1);
        }
        use_proxy = 1;
    }
    EUCA_FREE(tmpstr);

    proxy_max_cache_size = 32768;
    tmpstr = configFileValue("CC_IMAGE_PROXY_CACHE_SIZE");
    if (tmpstr) {
        proxy_max_cache_size = atoi(tmpstr);
        if (proxy_max_cache_size <= 0) {
            LOGINFO("disabling CC image proxy cache due to size %d\n", proxy_max_cache_size);
            use_proxy = 0; /* Disable proxy if zero-sized. */
        }
    }
    EUCA_FREE(tmpstr);

    tmpstr = configFileValue("CC_IMAGE_PROXY_PATH");
    if (tmpstr)
        tmpstr = euca_strreplace(&tmpstr, "$EUCALYPTUS", eucahome);
    if (tmpstr) {
        snprintf(proxyPath, MAX_PATH, "%s", tmpstr);
        EUCA_FREE(tmpstr);
    } else {
        snprintf(proxyPath, MAX_PATH, EUCALYPTUS_STATE_DIR "/dynserv", eucahome);
    }

    if (use_proxy)
        LOGINFO("enabling CC image proxy cache with size %d, path %s\n", proxy_max_cache_size, proxyPath);

    sem_mywait(CONFIG);
    // set up the current config
    euca_strncpy(config->eucahome, eucahome, MAX_PATH);
    euca_strncpy(config->policyFile, policyFile, MAX_PATH);
    // snprintf(config->proxyPath, MAX_PATH, EUCALYPTUS_STATE_DIR "/dynserv/data", config->eucahome);
    snprintf(config->proxyPath, MAX_PATH, "%s", proxyPath);
    config->use_proxy = use_proxy;
    config->proxy_max_cache_size = proxy_max_cache_size;
    if (use_proxy) {
        snprintf(config->proxyIp, 32, "%s", proxyIp);
    }
    EUCA_FREE(proxyIp);

    config->use_wssec = use_wssec;
    config->use_tunnels = use_tunnels;
    config->schedPolicy = schedPolicy;
    config->idleThresh = idleThresh;
    config->wakeThresh = wakeThresh;
    config->instanceTimeout = instanceTimeout;
    config->ncPollingFrequency = ncPollingFrequency;
    config->ncSensorsPollingInterval = ncPollingFrequency; // initially poll sensors with the same frequency as other NC ops
    config->clcPollingFrequency = clcPollingFrequency;
    config->ncFanout = ncFanout;
    locks[REFRESHLOCK] = sem_open("/eucalyptusCCrefreshLock", O_CREAT, 0644, config->ncFanout);
    config->initialized = 1;
    ccChangeState(LOADED);
    config->ccStatus.localEpoch = 0;
    snprintf(config->arbitrators, 255, "%s", arbitrators);
    snprintf(config->ccStatus.details, 1024, "ERRORS=0");
    snprintf(config->ccStatus.serviceId.type, 32, "cluster");
    snprintf(config->ccStatus.serviceId.name, 32, "self");
    snprintf(config->ccStatus.serviceId.partition, 32, "unset");
    config->ccStatus.serviceId.urisLen = 0;
    for (i = 0; i < 32 && config->ccStatus.serviceId.urisLen < 8; i++) {
        if (vnetconfig->localIps[i]) {
            char *host;
            host = hex2dot(vnetconfig->localIps[i]);
            if (host) {
                snprintf(config->ccStatus.serviceId.uris[config->ccStatus.serviceId.urisLen], 512, "http://%s:8774/axis2/services/EucalyptusCC",
                         host);
                config->ccStatus.serviceId.urisLen++;
                EUCA_FREE(host);
            }
        }
    }
    snprintf(config->configFiles[0], MAX_PATH, "%s", configFiles[0]);
    snprintf(config->configFiles[1], MAX_PATH, "%s", configFiles[1]);

    LOGINFO(" CC Configuration: eucahome=%s\n", SP(config->eucahome));
    LOGINFO(" policyfile=%s\n", SP(config->policyFile));
    LOGINFO(" ws-security=%s\n", use_wssec ? "ENABLED" : "DISABLED");
    LOGINFO(" schedulerPolicy=%s\n", SP(SCHEDPOLICIES[config->schedPolicy]));
    LOGINFO(" idleThreshold=%d\n", config->idleThresh);
    LOGINFO(" wakeThreshold=%d\n", config->wakeThresh);
    sem_mypost(CONFIG);

    res = NULL;
    rc = refreshNodes(config, &res, &numHosts);
    if (rc) {
        LOGERROR("cannot read list of nodes, check your config file\n");
        sem_mypost(INIT);
        return (1);
    }
    // update resourceCache
    sem_mywait(RESCACHE);
    resourceCache->numResources = numHosts;
    if (numHosts) {
        memcpy(resourceCache->resources, res, sizeof(ccResource) * numHosts);
    }
    EUCA_FREE(res);
    resourceCache->lastResourceUpdate = 0;
    sem_mypost(RESCACHE);

    config_init = 1;
    LOGTRACE("done\n");

    sem_mypost(INIT);
    return (0);
}

//!
//!
//!
//! @return
//!
//! @pre
//!
//! @note
//!
