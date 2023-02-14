void shawn(void)
{
    int p = 1, status;

    // clean up any orphaned child processes
    while (p > 0) {
        p = waitpid(-1, &status, WNOHANG);
    }

    if (instanceCache)
        msync(instanceCache, sizeof(ccInstanceCache), MS_ASYNC);
    if (resourceCache)
        msync(resourceCache, sizeof(ccResourceCache), MS_ASYNC);
    if (config)
        msync(config, sizeof(ccConfig), MS_ASYNC);
    if (vnetconfig)
        msync(vnetconfig, sizeof(vnetConfig), MS_ASYNC);

}

//!
//!
//!
//! @param[in] out
//! @param[in] ncURL
//! @param[in] ncService
//! @param[in] ncPort
//! @param[in] hostname
//! @param[in] mac
//! @param[in] ip
//! @param[in] maxMemory
//! @param[in] availMemory
//! @param[in] maxDisk
//! @param[in] availDisk
//! @param[in] maxCores
//! @param[in] availCores
//! @param[in] state
//! @param[in] laststate
//! @param[in] stateChange
//! @param[in] idleStart
//!
//! @return
//!
//! @pre
//!
//! @note
//!
int allocate_ccResource(ccResource * out, char *ncURL, char *ncService, int ncPort, char *hostname, char *mac, char *ip, int maxMemory,
                        int availMemory, int maxDisk, int availDisk, int maxCores, int availCores, int state, int laststate, time_t stateChange,
                        time_t idleStart)
{

    if (out != NULL) {
        if (ncURL)
            euca_strncpy(out->ncURL, ncURL, 384);
        if (ncService)
            euca_strncpy(out->ncService, ncService, 128);
        if (hostname)
            euca_strncpy(out->hostname, hostname, 256);
        if (mac)
            euca_strncpy(out->mac, mac, 24);
        if (ip)
            euca_strncpy(out->ip, ip, 24);

        out->ncPort = ncPort;
        out->maxMemory = maxMemory;
        out->availMemory = availMemory;
        out->maxDisk = maxDisk;
        out->availDisk = availDisk;
        out->maxCores = maxCores;
        out->availCores = availCores;
        out->state = state;
        out->lastState = laststate;
        out->stateChange = stateChange;
        out->idleStart = idleStart;
    }

    return (0);
}