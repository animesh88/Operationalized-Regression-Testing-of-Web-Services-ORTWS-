int ccIsDisabled(void)
{
    // initialized, but ccState is disabled (refuse to service operations)

    if (!config || config->ccState != DISABLED) {
        return (1);
    }
    return (0);
}