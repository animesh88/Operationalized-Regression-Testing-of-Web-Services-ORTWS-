int update_config(void)
{
    char *tmpstr = NULL;
    ccResource *res = NULL;
    int rc, numHosts, ret = 0;

    sem_mywait(CONFIG);

    rc = isConfigModified(config->configFiles, 2);
    if (rc < 0) { // error
        sem_mypost(CONFIG);
        return (1);
    } else if (rc > 0) { // config modification time has changed
        rc = readConfigFile(config->configFiles, 2);
        if (rc) {
            // something has changed that can be read in
            LOGINFO("ingressing new options\n");

            // read log params from config file and update in-memory configuration
            char *log_prefix;
            configReadLogParams(&(config->log_level), &(config->log_roll_number), &(config->log_max_size_bytes), &log_prefix);
            if (log_prefix && strlen(log_prefix) > 0) {
                euca_strncpy(config->log_prefix, log_prefix, sizeof(config->log_prefix));
            }
            EUCA_FREE(log_prefix);

            char *log_facility = configFileValue("LOGFACILITY");
            if (log_facility) {
                if (strlen(log_facility) > 0) {
                    euca_strncpy(config->log_facility, log_facility, sizeof(config->log_facility));
                }
                EUCA_FREE(log_facility);
            }
            // reconfigure the logging subsystem to use the new values, if any
            log_params_set(config->log_level, (int)config->log_roll_number, config->log_max_size_bytes);
            log_prefix_set(config->log_prefix);
            log_facility_set(config->log_facility, "cc");

            // NODES
            LOGINFO("refreshing node list\n");
            res = NULL;
            rc = refreshNodes(config, &res, &numHosts);
            if (rc) {
                LOGERROR("cannot read list of nodes, check your config file\n");
                sem_mywait(RESCACHE);
                resourceCache->numResources = 0;
                config->schedState = 0;
                bzero(resourceCache->resources, sizeof(ccResource) * MAXNODES);
                sem_mypost(RESCACHE);
                ret = 1;
            } else {
                sem_mywait(RESCACHE);
                if (numHosts > MAXNODES) {
                    LOGWARN("the list of nodes specified exceeds the maximum number of nodes that a single CC can support (%d). "
                            "Truncating list to %d nodes.\n", MAXNODES, MAXNODES);
                    numHosts = MAXNODES;
                }
                resourceCache->numResources = numHosts;
                config->schedState = 0;
                memcpy(resourceCache->resources, res, sizeof(ccResource) * numHosts);
                sem_mypost(RESCACHE);
            }
            EUCA_FREE(res);

            // CC Arbitrators
            tmpstr = configFileValue("CC_ARBITRATORS");
            if (tmpstr) {
                snprintf(config->arbitrators, 255, "%s", tmpstr);
                EUCA_FREE(tmpstr);
            } else {
                bzero(config->arbitrators, 256);
            }

            // polling frequencies

            // CLC
            tmpstr = configFileValue("CLC_POLLING_FREQUENCY");
            if (tmpstr) {
                if (atoi(tmpstr) > 0) {
                    config->clcPollingFrequency = atoi(tmpstr);
                } else {
                    config->clcPollingFrequency = 6;
                }
                EUCA_FREE(tmpstr);
            } else {
                config->clcPollingFrequency = 6;
            }

            // NC
            tmpstr = configFileValue("NC_POLLING_FREQUENCY");
            if (tmpstr) {
                if (atoi(tmpstr) > 6) {
                    config->ncPollingFrequency = atoi(tmpstr);
                } else {
                    config->ncPollingFrequency = 6;
                }
                EUCA_FREE(tmpstr);
            } else {
                config->ncPollingFrequency = 6;
            }

        }
    }

    sem_mypost(CONFIG);

    return (ret);
}