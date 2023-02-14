int restoreNetworkState(void)
{
    int rc, ret = 0, i;

    /* this function should query both internal and external information sources and restore the CC to correct networking state
1.) restore from internal instance state
- local IPs (instance and cloud)
- networks (bridges)
2.) query CLC for sec. group rules and apply (and/or apply from in-memory iptables?)
3.) (re)start local network processes (dhcpd)
*/

    LOGDEBUG("restoreNetworkState(): restoring network state\n");

    sem_mywait(VNET);

    // sync up internal network state with information from instances
    LOGDEBUG("restoreNetworkState(): syncing internal network state with current instance state\n");
    rc = map_instanceCache(validCmp, NULL, instNetParamsSet, NULL);
    if (rc) {
        LOGERROR("restoreNetworkState(): could not sync internal network state with current instance state\n");
        ret = 1;
    }

    if (!strcmp(vnetconfig->mode, "MANAGED") || !strcmp(vnetconfig->mode, "MANAGED-NOVLAN")) {
        // restore iptables state, if internal iptables state exists
        LOGDEBUG("restoreNetworkState(): restarting iptables\n");
        rc = vnetRestoreTablesFromMemory(vnetconfig);
        if (rc) {
            LOGERROR("restoreNetworkState(): cannot restore iptables state\n");
            ret = 1;
        }
        // re-create all active networks (bridges, vlan<->bridge mappings)
        LOGDEBUG("restoreNetworkState(): restarting networks\n");
        for (i = 2; i < NUMBER_OF_VLANS; i++) {
            if (vnetconfig->networks[i].active) {
                char *brname = NULL;
                LOGDEBUG("restoreNetworkState(): found active network: %d\n", i);
                rc = vnetStartNetwork(vnetconfig, i, NULL, vnetconfig->users[i].userName, vnetconfig->users[i].netName, &brname);
                if (rc) {
                    LOGDEBUG("restoreNetworkState(): failed to reactivate network: %d", i);
                }
                EUCA_FREE(brname);
            }
        }

        rc = map_instanceCache(validCmp, NULL, instNetReassignAddrs, NULL);
        if (rc) {
            LOGERROR("restoreNetworkState(): could not (re)assign public/private IP mappings\n");
            ret = 1;
        }
    }
    // get DHCPD back up and running
    LOGDEBUG("restoreNetworkState(): restarting DHCPD\n");
    rc = vnetKickDHCP(vnetconfig);
    if (rc) {
        LOGERROR("restoreNetworkState(): cannot start DHCP daemon, please check your network settings\n");
        ret = 1;
    }
    sem_mypost(VNET);

    LOGDEBUG("restoreNetworkState(): done restoring network state\n");

    return (ret);
}