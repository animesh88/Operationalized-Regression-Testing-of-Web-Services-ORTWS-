int privIpCmp(ccInstance * inst, void *ip)
{
    if (!ip || !inst) {
        return (1);
    }

    if (!strcmp((char *)ip, inst->ccnet.privateIp)) {
        return (0);
    }
    return (1);
}