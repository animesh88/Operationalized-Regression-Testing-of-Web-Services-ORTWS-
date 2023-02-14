int doGetConsoleOutput(ncMetadata * pMeta, char *instanceId, char **consoleOutput)
{
    int i, rc, numInsts, start, stop, done, ret = EUCA_OK, timeout = 0;
    ccInstance *myInstance;
    time_t op_start;
    ccResourceCache resourceCacheLocal;

    i = numInsts = 0;
    op_start = time(NULL);

    myInstance = NULL;

    *consoleOutput = NULL;

    rc = initialize(pMeta);
    if (rc || ccIsEnabled()) {
        return (1);
    }

    LOGINFO("[%s] requesting console output\n", SP(instanceId));
    LOGDEBUG("invoked: instId=%s\n", SP(instanceId));

    sem_mywait(RESCACHE);
    memcpy(&resourceCacheLocal, resourceCache, sizeof(ccResourceCache));
    sem_mypost(RESCACHE);

    rc = find_instanceCacheId(instanceId, &myInstance);
    if (!rc) {
        // found the instance in the cache
        start = myInstance->ncHostIdx;
        stop = start + 1;
        EUCA_FREE(myInstance);
    } else {
        start = 0;
        stop = resourceCacheLocal.numResources;
    }

    done = 0;
    for (i = start; i < stop && !done; i++) {
        EUCA_FREE(*consoleOutput);

        // if not talking to Eucalyptus NC (but, e.g., a Broker)
        if (!strstr(resourceCacheLocal.resources[i].ncURL, "EucalyptusNC")) {
            char pwfile[MAX_PATH];
            *consoleOutput = NULL;
            snprintf(pwfile, MAX_PATH, EUCALYPTUS_STATE_DIR "/windows/%s/console.append.log", config->eucahome, instanceId);

            char *rawconsole = NULL;
            if (!check_file(pwfile)) { // the console log file should exist for a Windows guest (with encrypted password in it)
                rawconsole = file2str(pwfile);
            } else { // the console log file will not exist for a Linux guest
                rawconsole = strdup("not implemented");
            }
            if (rawconsole) {
                *consoleOutput = base64_enc((unsigned char *)rawconsole, strlen(rawconsole));
                EUCA_FREE(rawconsole);
            }
            // set the return code accordingly
            if (!*consoleOutput) {
                rc = 1;
            } else {
                rc = 0;
            }
            done++; // quit on the first host, since they are not queried remotely

        } else { // otherwise, we *are* talking to a Eucalyptus NC, so make the remote call
            timeout = ncGetTimeout(op_start, timeout, (stop - start), i);
            rc = ncClientCall(pMeta, timeout, resourceCacheLocal.resources[i].lockidx, resourceCacheLocal.resources[i].ncURL, "ncGetConsoleOutput",
                              instanceId, consoleOutput);
        }

        if (rc) {
            ret = 1;
        } else {
            ret = 0;
            done++;
        }
    }

    LOGTRACE("done\n");

    shawn();

    return (ret);
}