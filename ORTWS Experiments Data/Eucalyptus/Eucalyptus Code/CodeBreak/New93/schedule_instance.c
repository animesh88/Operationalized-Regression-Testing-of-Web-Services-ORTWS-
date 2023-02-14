int schedule_instance(virtualMachine * vm, char *targetNode, int *outresid)
{
    int ret;

    if (targetNode != NULL) {
        ret = schedule_instance_explicit(vm, targetNode, outresid);
    } else if (config->schedPolicy == SCHEDGREEDY) {
        ret = schedule_instance_greedy(vm, outresid);
    } else if (config->schedPolicy == SCHEDROUNDROBIN) {
        ret = schedule_instance_roundrobin(vm, outresid);
    } else if (config->schedPolicy == SCHEDPOWERSAVE) {
        ret = schedule_instance_greedy(vm, outresid);
    } else {
        ret = schedule_instance_greedy(vm, outresid);
    }

    return (ret);
}