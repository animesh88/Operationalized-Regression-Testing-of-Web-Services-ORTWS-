int schedule_instance_explicit(virtualMachine * vm, char *targetNode, int *outresid)
{
    int i, done, resid, sleepresid;
    ccResource *res;

    *outresid = 0;

    LOGDEBUG("scheduler using EXPLICIT policy to run VM on target node '%s'\n", targetNode);

    // find the best 'resource' on which to run the instance
    resid = sleepresid = -1;
    done = 0;
    for (i = 0; i < resourceCache->numResources && !done; i++) {
        int mem, disk, cores;

        res = &(resourceCache->resources[i]);
        if (!strcmp(res->hostname, targetNode)) {
            done++;
            if (res->state == RESUP) {
                mem = res->availMemory - vm->mem;
                disk = res->availDisk - vm->disk;
                cores = res->availCores - vm->cores;

                if (mem >= 0 && disk >= 0 && cores >= 0) {
                    resid = i;
                }
            } else if (res->state == RESASLEEP) {
                mem = res->availMemory - vm->mem;
                disk = res->availDisk - vm->disk;
                cores = res->availCores - vm->cores;

                if (mem >= 0 && disk >= 0 && cores >= 0) {
                    sleepresid = i;
                }
            }
        }
    }

    if (resid == -1 && sleepresid == -1) {
        // target resource is unavailable
        return (1);
    }

    if (resid != -1) {
        res = &(resourceCache->resources[resid]);
        *outresid = resid;
    } else if (sleepresid != -1) {
        res = &(resourceCache->resources[sleepresid]);
        *outresid = sleepresid;
    }
    if (res->state == RESASLEEP) {
        powerUp(res);
    }

    return (0);
}