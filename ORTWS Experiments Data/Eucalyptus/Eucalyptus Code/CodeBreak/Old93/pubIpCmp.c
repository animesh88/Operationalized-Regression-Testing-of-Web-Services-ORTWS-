int pubIpCmp(ccInstance * inst, void *ip)
{
    if (!ip || !inst) {
        return (1);
    }

    if (!strcmp((char *)ip, inst->ccnet.publicIp)) {
        return (0);
    }
    return (1);
}