int checkActiveNetworks(void)
{
    int i, rc;
    if (!strcmp(vnetconfig->mode, "MANAGED") || !strcmp(vnetconfig->mode, "MANAGED-NOVLAN")) {
        int activeNetworks[NUMBER_OF_VLANS];
        bzero(activeNetworks, sizeof(int) * NUMBER_OF_VLANS);

        LOGDEBUG("checkActiveNetworks(): maintaining active networks\n");
        for (i = 0; i < MAXINSTANCES_PER_CC; i++) {
            if (instanceCache->cacheState[i] != INSTINVALID) {
                if (strcmp(instanceCache->instances[i].state, "Teardown")) {
                    int vlan = instanceCache->instances[i].ccnet.vlan;
                    activeNetworks[vlan] = 1;
                    if (!vnetconfig->networks[vlan].active) {
                        LOGWARN("checkActiveNetworks(): instance running in network that is currently inactive (%s, %s, %d)\n",
                                vnetconfig->users[vlan].userName, vnetconfig->users[vlan].netName, vlan);
                    }
                }
            }
        }

        for (i = 0; i < NUMBER_OF_VLANS; i++) {
            sem_mywait(VNET);
            if (!activeNetworks[i] && vnetconfig->networks[i].active && ((time(NULL) - vnetconfig->networks[i].createTime) > 300)) {
                LOGWARN("checkActiveNetworks(): network active but no running instances (%s, %s, %d)\n", vnetconfig->users[i].userName,
                        vnetconfig->users[i].netName, i);
                rc = vnetStopNetwork(vnetconfig, i, vnetconfig->users[i].userName, vnetconfig->users[i].netName);
                if (rc) {
                    LOGERROR("checkActiveNetworks(): failed to stop network (%s, %s, %d), will re-try\n", vnetconfig->users[i].userName,
                             vnetconfig->users[i].netName, i);
                }
            }
            sem_mypost(VNET);

            /*
if ( activeNetworks[i] ) {
// make sure all active network indexes are used by an instance
for (j=0; j<NUMBER_OF_HOSTS_PER_VLAN; j++) {
if (vnetconfig->networks[i].addrs[j].active && (vnetconfig->networks[i].addrs[j].ip != 0) ) {
// dan
char *ip=NULL;
ccInstance *myInstance=NULL;

ip = hex2dot(vnetconfig->networks[i].addrs[j].ip);
rc = find_instanceCacheIP(ip, &myInstance);
if (rc) {
// network index marked as used, but no instance in cache with that index/ip
LOGWARN("checkActiveNetworks(): address active but no instances using addr (%s, %d, %d\n", ip, i, j);
} else {
LOGDEBUG("checkActiveNetworks(): address active and found for instance (%s, %s, %d, %d\n", myInstance->instanceId, ip, i, j);
}
EUCA_FREE(myInstance);
EUCA_FREE(ip);
}
}
}
*/
        }
    }
    return (0);
}