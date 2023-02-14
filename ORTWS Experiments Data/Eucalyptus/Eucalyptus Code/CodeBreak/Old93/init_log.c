int init_log(void)
{
    char logFile[MAX_PATH], configFiles[2][MAX_PATH], home[MAX_PATH];

    if (local_init == 0) { // called by this process for the first time

        //! @TODO code below is replicated in init_config(), it would be good to join them
        bzero(logFile, MAX_PATH);
        bzero(home, MAX_PATH);
        bzero(configFiles[0], MAX_PATH);
        bzero(configFiles[1], MAX_PATH);

        char *tmpstr = getenv(EUCALYPTUS_ENV_VAR_NAME);
        if (!tmpstr) {
            snprintf(home, MAX_PATH, "/");
        } else {
            snprintf(home, MAX_PATH, "%s", tmpstr);
        }

        snprintf(configFiles[1], MAX_PATH, EUCALYPTUS_CONF_LOCATION, home);
        snprintf(configFiles[0], MAX_PATH, EUCALYPTUS_CONF_OVERRIDE_LOCATION, home);
        snprintf(logFile, MAX_PATH, EUCALYPTUS_LOG_DIR "/cc.log", home);

        configInitValues(configKeysRestartCC, configKeysNoRestartCC); // initialize config subsystem
        readConfigFile(configFiles, 2);

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
        // set the log file path (levels and size limits are set below)
        log_file_set(logFile);

        local_init = 1;
    }
    // update log params on every request so that the updated values discovered
    // by monitoring_thread will get picked up by other processes, too
    log_params_set(config->log_level, (int)config->log_roll_number, config->log_max_size_bytes);
    log_prefix_set(config->log_prefix);
    log_facility_set(config->log_facility, "cc");

    return 0;
}