int schedule_instance_roundrobin(virtualMachine * vm, int *outresid)
{
    int i, done, start, found, resid = 0;
    ccResource *res;

    *outresid = 0;

    LOGDEBUG("scheduler using ROUNDROBIN policy to find next resource\n");
    // find the best 'resource' on which to run the instance
    done = found = 0;
    start = config->schedState;
    i = start;

    LOGDEBUG("scheduler state starting at resource %d\n", config->schedState);
    while (!done) {
        int mem, disk, cores;

        res = &(resourceCache->resources[i]);
        if (res->state != RESDOWN) {
            mem = res->availMemory - vm->mem;
            disk = res->availDisk - vm->disk;
            cores = res->availCores - vm->cores;

            if (mem >= 0 && disk >= 0 && cores >= 0) {
                resid = i;
                found = 1;
                done++;
            }
        }
        i++;
        if (i >= resourceCache->numResources) {
            i = 0;
        }
        if (i == start) {
            done++;
        }
    }

    if (!found) {
        // didn't find a resource
        return (1);
    }

    *outresid = resid;
    config->schedState = i;

    LOGDEBUG("scheduler state finishing at resource %d\n", config->schedState);

    return (0);
}