void print_netConfig(char *prestr, netConfig * in)
{
    LOGDEBUG("%s: vlan:%d networkIndex:%d privateMac:%s publicIp:%s privateIp:%s\n", prestr, in->vlan, in->networkIndex, in->privateMac, in->publicIp,
             in->privateIp);
}