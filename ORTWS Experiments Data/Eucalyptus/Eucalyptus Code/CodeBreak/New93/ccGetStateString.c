int ccGetStateString(char *statestr, int n)
{
    if (config->ccState == ENABLED) {
        snprintf(statestr, n, "ENABLED");
    } else if (config->ccState == DISABLED) {
        snprintf(statestr, n, "DISABLED");
    } else if (config->ccState == STOPPED) {
        snprintf(statestr, n, "STOPPED");
    } else if (config->ccState == LOADED) {
        snprintf(statestr, n, "LOADED");
    } else if (config->ccState == INITIALIZED) {
        snprintf(statestr, n, "INITIALIZED");
    } else if (config->ccState == PRIMORDIAL) {
        snprintf(statestr, n, "PRIMORDIAL");
    } else if (config->ccState == NOTREADY || config->ccState == SHUTDOWNCC) {
        snprintf(statestr, n, "NOTREADY");
    }
    return (0);
}