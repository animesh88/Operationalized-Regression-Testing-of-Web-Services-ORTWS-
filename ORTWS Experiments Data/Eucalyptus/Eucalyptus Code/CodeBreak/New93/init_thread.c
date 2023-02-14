int init_thread(void)
{
    int rc, i;

    LOGDEBUG("init=%d %p %p %p %p\n", init, config, vnetconfig, instanceCache, resourceCache);
    if (thread_init) {
        // thread has already been initialized
    } else {
        // this thread has not been initialized, set up shared memory segments
        srand(time(NULL));

        bzero(locks, sizeof(sem_t *) * ENDLOCK);
        bzero(mylocks, sizeof(int) * ENDLOCK);

        locks[INIT] = sem_open("/eucalyptusCCinitLock", O_CREAT, 0644, 1);
        sem_mywait(INIT);

        for (i = NCCALL0; i <= NCCALL31; i++) {
            char lockname[MAX_PATH];
            snprintf(lockname, MAX_PATH, "/eucalyptusCCncCallLock%d", i);
            locks[i] = sem_open(lockname, O_CREAT, 0644, 1);
        }

        if (config == NULL) {
            rc = setup_shared_buffer((void **)&config, "/eucalyptusCCConfig", sizeof(ccConfig), &(locks[CONFIG]), "/eucalyptusCCConfigLock", SHARED_FILE);
            if (rc != 0) {
                fprintf(stderr, "Cannot set up shared memory region for ccConfig, exiting...\n");
                sem_mypost(INIT);
                exit(1);
            }
        }

        if (instanceCache == NULL) {
            rc = setup_shared_buffer((void **)&instanceCache, "/eucalyptusCCInstanceCache", sizeof(ccInstanceCache), &(locks[INSTCACHE]),
                                     "/eucalyptusCCInstanceCacheLock", SHARED_FILE);
            if (rc != 0) {
                fprintf(stderr, "Cannot set up shared memory region for ccInstanceCache, exiting...\n");
                sem_mypost(INIT);
                exit(1);
            }
        }

        if (resourceCache == NULL) {
            rc = setup_shared_buffer((void **)&resourceCache, "/eucalyptusCCResourceCache", sizeof(ccResourceCache), &(locks[RESCACHE]),
                                     "/eucalyptusCCResourceCacheLock", SHARED_FILE);
            if (rc != 0) {
                fprintf(stderr, "Cannot set up shared memory region for ccResourceCache, exiting...\n");
                sem_mypost(INIT);
                exit(1);
            }
        }

        if (resourceCacheStage == NULL) {
            rc = setup_shared_buffer((void **)&resourceCacheStage, "/eucalyptusCCResourceCacheStage", sizeof(ccResourceCache),
                                     &(locks[RESCACHESTAGE]), "/eucalyptusCCResourceCacheStatgeLock", SHARED_FILE);
            if (rc != 0) {
                fprintf(stderr, "Cannot set up shared memory region for ccResourceCacheStage, exiting...\n");
                sem_mypost(INIT);
                exit(1);
            }
        }

        if (ccSensorResourceCache == NULL) {
            rc = setup_shared_buffer((void **)&ccSensorResourceCache, "/eucalyptusCCSensorResourceCache",
                                     sizeof(sensorResourceCache) + sizeof(sensorResource) * (MAX_SENSOR_RESOURCES - 1), &(locks[SENSORCACHE]),
                                     "/eucalyptusCCSensorResourceCacheLock", SHARED_FILE);
            if (rc != 0) {
                fprintf(stderr, "Cannot set up shared memory region for ccSensorResourceCache, exiting...\n");
                sem_mypost(INIT);
                exit(1);
            }
        }

        if (vnetconfig == NULL) {
            rc = setup_shared_buffer((void **)&vnetconfig, "/eucalyptusCCVNETConfig", sizeof(vnetConfig), &(locks[VNET]),
                                     "/eucalyptusCCVNETConfigLock", SHARED_FILE);
            if (rc != 0) {
                fprintf(stderr, "Cannot set up shared memory region for ccVNETConfig, exiting...\n");
                sem_mypost(INIT);
                exit(1);
            }
        }

        sem_mypost(INIT);
        thread_init = 1;
    }
    return (0);
}