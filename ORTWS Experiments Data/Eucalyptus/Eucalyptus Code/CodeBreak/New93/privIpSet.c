int privIpSet(ccInstance * inst, void *ip)
{
    if (!ip || !inst) {
        return (1);
    }

    if ((strcmp(inst->state, "Pending") && strcmp(inst->state, "Extant"))) {
        snprintf(inst->ccnet.privateIp, 24, "0.0.0.0");
        return (0);
    }

    LOGDEBUG("privIpSet(): set: %s/%s\n", inst->ccnet.privateIp, (char *)ip);
    snprintf(inst->ccnet.privateIp, 24, "%s", (char *)ip);
    return (0);
}