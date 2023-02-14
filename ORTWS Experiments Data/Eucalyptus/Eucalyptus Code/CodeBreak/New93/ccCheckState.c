int ccCheckState(int clcTimer)
{
    char localDetails[1024];
    int ret = 0;
    char cmd[MAX_PATH];
    int rc;

    if (!config) {
        return (1);
    }
    // check local configuration
    if (config->ccState == SHUTDOWNCC) {
        LOGINFO("this cluster controller marked as shut down\n");
        ret++;
    }
    // configuration
    {
        char cmd[MAX_PATH];
        snprintf(cmd, MAX_PATH, "%s", config->eucahome);
        if (check_directory(cmd)) {
            LOGERROR("cannot find directory '%s'\n", cmd);
            ret++;
        }
    }

    // shellouts
    {
        snprintf(cmd, MAX_PATH, EUCALYPTUS_ROOTWRAP, config->eucahome);
        if (check_file(cmd)) {
            LOGERROR("cannot find shellout '%s'\n", cmd);
            ret++;
        }

        snprintf(cmd, MAX_PATH, EUCALYPTUS_HELPER_DIR "/dynserv.pl", config->eucahome);
        if (check_file(cmd)) {
            LOGERROR("cannot find shellout '%s'\n", cmd);
            ret++;
        }

        snprintf(cmd, MAX_PATH, "ip addr show");
        if (system(cmd)) {
            LOGERROR("cannot run shellout '%s'\n", cmd);
            ret++;
        }
    }
    // filesystem

    // network
    // arbitrators
    if (clcTimer == 1 && strlen(config->arbitrators)) {
        char *tok, buf[256], *host;
        uint32_t hostint;
        int count = 0;
        int arbitratorFails = 0;
        snprintf(buf, 255, "%s", config->arbitrators);
        tok = strtok(buf, " ");
        while (tok && count < 3) {
            hostint = dot2hex(tok);
            host = hex2dot(hostint);
            if (host) {
                LOGDEBUG("checking health of arbitrator (%s)\n", tok);
                snprintf(cmd, 255, "ping -c 1 %s", host);
                rc = system(cmd);
                if (rc) {
                    LOGDEBUG("cannot ping arbitrator %s (ping rc=%d)\n", host, rc);
                    arbitratorFails++;
                }
                EUCA_FREE(host);
            }
            tok = strtok(NULL, " ");
            count++;
        }
        if (arbitratorFails) {
            config->arbitratorFails++;
        } else {
            config->arbitratorFails = 0;
        }

        if (config->arbitratorFails > 10) {
            LOGDEBUG("more than 10 arbitrator ping fails in a row (%d), failing check\n", config->arbitratorFails);
            ret++;
        }
    }
    // broker pairing algo
    rc = doBrokerPairing();
    if (rc) {
        ret++;
    }

    snprintf(localDetails, 1023, "ERRORS=%d", ret);
    snprintf(config->ccStatus.details, 1023, "%s", localDetails);

    return (ret);
}