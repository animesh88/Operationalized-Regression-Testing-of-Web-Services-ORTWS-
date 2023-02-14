int init_pthreads(void)
{
    // start any background threads
    if (!config_init) {
        return (1);
    }
    sem_mywait(CONFIG);

    if (sensor_initd == 0) {
        sem *s = sem_alloc_posix(locks[SENSORCACHE]);
        if (config->threads[SENSOR] == 0 || check_process(config->threads[SENSOR], NULL)) {
            int pid;
            pid = fork();
            if (!pid) {
                // set up default signal handler for this child process (for SIGTERM)
                struct sigaction newsigact = { {NULL} };
                newsigact.sa_handler = SIG_DFL;
                newsigact.sa_flags = 0;
                sigemptyset(&newsigact.sa_mask);
                sigprocmask(SIG_SETMASK, &newsigact.sa_mask, NULL);
                sigaction(SIGTERM, &newsigact, NULL);
                LOGDEBUG("sensor polling process running\n");
                LOGDEBUG("calling sensor_init() to not return.\n");
                if (sensor_init(s, ccSensorResourceCache, MAX_SENSOR_RESOURCES, TRUE, update_config) != EUCA_OK) // this call will not return
                    LOGERROR("failed to invoke the sensor polling process\n");
                exit(0);
            } else {
                config->threads[SENSOR] = pid;
            }
        }
        LOGDEBUG("calling sensor_init(..., NULL) to return.\n");
        if (sensor_init(s, ccSensorResourceCache, MAX_SENSOR_RESOURCES, FALSE, NULL) != EUCA_OK) { // this call will return
            LOGERROR("failed to initialize sensor subsystem in this process\n");
        } else {
            LOGDEBUG("sensor subsystem initialized in this process\n");
            sensor_initd = 1;
        }
    }
    // sensor initialization should preceed monitor thread creation so
    // that monitor thread has its sensor subsystem initialized

    if (config->threads[MONITOR] == 0 || check_process(config->threads[MONITOR], "httpd-cc.conf")) {
        int pid;
        pid = fork();
        if (!pid) {
            // set up default signal handler for this child process (for SIGTERM)
            struct sigaction newsigact = { {NULL} };
            newsigact.sa_handler = SIG_DFL;
            newsigact.sa_flags = 0;
            sigemptyset(&newsigact.sa_mask);
            sigprocmask(SIG_SETMASK, &newsigact.sa_mask, NULL);
            sigaction(SIGTERM, &newsigact, NULL);
            config->kick_dhcp = 1;
            config->kick_network = 1;
            monitor_thread(NULL);
            exit(0);
        } else {
            config->threads[MONITOR] = pid;
        }
    }

    sem_mypost(CONFIG);

    return (0);
}