int ccChangeState(int newstate)
{
    if (config) {
        if (config->ccState == SHUTDOWNCC) {
            // CC is to be shut down, there is no transition out of this state
            return (0);
        }
        char localState[32];
        config->ccLastState = config->ccState;
        config->ccState = newstate;
        ccGetStateString(localState, 32);
        snprintf(config->ccStatus.localState, 32, "%s", localState);
        return (0);
    }
    return (1);
}