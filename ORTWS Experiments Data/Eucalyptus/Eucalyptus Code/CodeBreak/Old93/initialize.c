int initialize(ncMetadata * pMeta)
{
    int rc, ret;

    ret = 0;
    rc = init_thread();
    if (rc) {
        ret = 1;
        LOGERROR("cannot initialize thread\n");
    }

    rc = init_log();
    if (rc) {
        ret = 1;
        LOGERROR("cannot initialize local state\n");
    }

    rc = init_eucafaults("cc"); // Returns # of faults loaded into registry.
    if (!rc) {
        LOGERROR("cannot initialize eucafault registry at startup--will retry initialization upon detection of any faults.\n");
    }

    rc = init_config();
    if (rc) {
        ret = 1;
        LOGERROR("cannot initialize from configuration file\n");
    }

    if (config->use_tunnels) {
        rc = vnetInitTunnels(vnetconfig);
        if (rc) {
            LOGERROR("cannot initialize tunnels\n");
        }
    }

    rc = init_pthreads();
    if (rc) {
        LOGERROR("cannot initialize background threads\n");
        ret = 1;
    }

    if (pMeta != NULL) {
        LOGDEBUG("pMeta: userId=%s correlationId=%s\n", pMeta->userId, pMeta->correlationId);
    }

    if (!ret) {
        // store information from CLC that needs to be kept up-to-date in the CC
        if (pMeta != NULL) {
            int i;
            sem_mywait(CONFIG);
            memcpy(config->services, pMeta->services, sizeof(serviceInfoType) * 16);
            memcpy(config->disabledServices, pMeta->disabledServices, sizeof(serviceInfoType) * 16);
            memcpy(config->notreadyServices, pMeta->notreadyServices, sizeof(serviceInfoType) * 16);

            for (i = 0; i < 16; i++) {
                if (strlen(config->services[i].type)) {
                    // search for this CCs serviceInfoType
                    /* if (!strcmp(config->services[i].type, "cluster")) {
char uri[MAX_PATH], uriType[32], host[MAX_PATH], path[MAX_PATH];
int port, done;
snprintf(uri, MAX_PATH, "%s", config->services[i].uris[0]);
rc = tokenize_uri(uri, uriType, host, &port, path);
if (strlen(host)) {
done=0;
for (j=0; j<32 && !done; j++) {
uint32_t hostip;
hostip = dot2hex(host);
if (hostip == vnetconfig->localIps[j]) {
// found a match, update local serviceInfoType
memcpy(&(config->ccStatus.serviceId), &(config->services[i]), sizeof(serviceInfoType));
done++;
}
}
}
} else */
                    if (!strcmp(config->services[i].type, "eucalyptus")) {
                        char uri[MAX_PATH], uriType[32], host[MAX_PATH], path[MAX_PATH];
                        int port;
                        // this is the cloud controller serviceInfo
                        snprintf(uri, MAX_PATH, "%s", config->services[i].uris[0]);
                        rc = tokenize_uri(uri, uriType, host, &port, path);
                        if (strlen(host)) {
                            config->cloudIp = dot2hex(host);
                        }
                    }
                }
            }
            sem_mypost(CONFIG);
        }

        sem_mywait(INIT);
        if (!init) {
            // first time operations with everything initialized
            sem_mywait(VNET);
            vnetconfig->cloudIp = 0;
            sem_mypost(VNET);
            sem_mywait(CONFIG);
            config->cloudIp = 0;
            sem_mypost(CONFIG);
        }
        // initialization went well, this thread is now initialized
        init = 1;
        sem_mypost(INIT);
    }

    return (ret);
}