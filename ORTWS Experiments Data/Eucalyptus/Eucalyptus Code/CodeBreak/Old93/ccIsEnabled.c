int ccIsEnabled(void)
{
    // initialized, but ccState is disabled (refuse to service operations)

    if (!config || config->ccState != ENABLED) {
        return (1);
    }
    return (0);
}