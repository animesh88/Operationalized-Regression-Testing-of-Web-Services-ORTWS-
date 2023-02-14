int maintainNetworkState(void)
{
    int rc, i, ret = 0;
    char pidfile[MAX_PATH], *pidstr = NULL;

    if (!strcmp(vnetconfig->mode, "MANAGED") || !strcmp(vnetconfig->mode, "MANAGED-NOVLAN")) {
        // rc = checkActiveNetworks();
        // if (rc) {
        // LOGWARN("maintainNetworkState(): checkActiveNetworks() failed, attempting to re-sync\n");
        // }

        LOGDEBUG("maintainNetworkState(): maintaining metadata redirect and tunnel health\n");
        sem_mywait(VNET);

        // check to see if cloudIp has changed
        char *cloudIp1 = hex2dot(config->cloudIp);
        char *cloudIp2 = hex2dot(vnetconfig->cloudIp);
        LOGDEBUG("maintainNetworkState(): CCcloudIp=%s VNETcloudIp=%s\n", cloudIp1, cloudIp2);
        EUCA_FREE(cloudIp1);
        EUCA_FREE(cloudIp2);

        if (config->cloudIp && (config->cloudIp != vnetconfig->cloudIp)) {
            rc = vnetUnsetMetadataRedirect(vnetconfig);
            if (rc) {
                LOGWARN("maintainNetworkState(): failed to unset old metadata redirect\n");
            }
            vnetconfig->cloudIp = config->cloudIp;
            rc = vnetSetMetadataRedirect(vnetconfig);
            if (rc) {
                LOGWARN("maintainNetworkState(): failed to set new metadata redirect\n");
            }
        }
        // check to see if this CCs localIpId has changed
        if (vnetconfig->tunnels.localIpId != vnetconfig->tunnels.localIpIdLast) {
            LOGDEBUG("maintainNetworkState(): local CC index has changed (%d -> %d): re-assigning gateway IPs and tunnel connections.\n",
                     vnetconfig->tunnels.localIpId, vnetconfig->tunnels.localIpIdLast);

            for (i = 2; i < NUMBER_OF_VLANS; i++) {
                if (vnetconfig->networks[i].active) {
                    char brname[32];
                    if (!strcmp(vnetconfig->mode, "MANAGED")) {
                        snprintf(brname, 32, "eucabr%d", i);
                    } else {
                        snprintf(brname, 32, "%s", vnetconfig->privInterface);
                    }

                    if (vnetconfig->tunnels.localIpIdLast >= 0) {
                        vnetDelGatewayIP(vnetconfig, i, brname, vnetconfig->tunnels.localIpIdLast);
                    }
                    if (vnetconfig->tunnels.localIpId >= 0) {
                        vnetAddGatewayIP(vnetconfig, i, brname, vnetconfig->tunnels.localIpId);
                    }
                }
            }
            rc = vnetTeardownTunnels(vnetconfig);
            if (rc) {
                LOGERROR("maintainNetworkState(): failed to tear down tunnels\n");
                ret = 1;
            }

            config->kick_dhcp = 1;
            vnetconfig->tunnels.localIpIdLast = vnetconfig->tunnels.localIpId;
        }

        rc = vnetSetupTunnels(vnetconfig);
        if (rc) {
            LOGERROR("maintainNetworkState(): failed to setup tunnels during maintainNetworkState()\n");
            ret = 1;
        }

        for (i = 2; i < NUMBER_OF_VLANS; i++) {
            if (vnetconfig->networks[i].active) {
                char brname[32];
                if (!strcmp(vnetconfig->mode, "MANAGED")) {
                    snprintf(brname, 32, "eucabr%d", i);
                } else {
                    snprintf(brname, 32, "%s", vnetconfig->privInterface);
                }
                rc = vnetAttachTunnels(vnetconfig, i, brname);
                if (rc) {
                    LOGDEBUG("maintainNetworkState(): failed to attach tunnels for vlan %d during maintainNetworkState()\n", i);
                    ret = 1;
                }
            }
        }

        // rc = vnetApplyArpTableRules(vnetconfig);
        // if (rc) {
        // LOGWARN("maintainNetworkState(): failed to maintain arp tables\n");
        // }

        sem_mypost(VNET);
    }

    sem_mywait(CONFIG);
    snprintf(pidfile, MAX_PATH, EUCALYPTUS_RUN_DIR "/net/euca-dhcp.pid", config->eucahome);
    if (!check_file(pidfile)) {
        pidstr = file2str(pidfile);
    } else {
        pidstr = NULL;
    }
    if (config->kick_dhcp || !pidstr || check_process(atoi(pidstr), "euca-dhcp.pid")) {
        rc = vnetKickDHCP(vnetconfig);
        if (rc) {
            LOGERROR("maintainNetworkState(): cannot start DHCP daemon\n");
            ret = 1;
        } else {
            config->kick_dhcp = 0;
        }
    }
    sem_mypost(CONFIG);

    EUCA_FREE(pidstr);

    return (ret);
}