int powerDown(ncMetadata * pMeta, ccResource * node)
{
    int rc, timeout;
    time_t op_start;

    if (config->schedPolicy != SCHEDPOWERSAVE) {
        node->idleStart = 0;
        return (0);
    }

    op_start = time(NULL);

    LOGINFO("powerdown to %s\n", node->hostname);

    timeout = ncGetTimeout(op_start, OP_TIMEOUT, 1, 1);
    rc = ncClientCall(pMeta, timeout, node->lockidx, node->ncURL, "ncPowerDown");

    if (rc == 0) {
        changeState(node, RESASLEEP);
    }
    return (rc);
}