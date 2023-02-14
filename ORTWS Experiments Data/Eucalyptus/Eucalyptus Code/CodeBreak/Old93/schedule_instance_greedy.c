int schedule_instance_greedy(virtualMachine * vm, int *outresid)
{
    int i, done, resid, sleepresid;
    ccResource *res;

    *outresid = 0;

    if (config->schedPolicy == SCHEDGREEDY) {
        LOGDEBUG("scheduler using GREEDY policy to find next resource\n");
    } else if (config->schedPolicy == SCHEDPOWERSAVE) {
        LOGDEBUG("scheduler using POWERSAVE policy to find next resource\n");
    }
    // find the best 'resource' on which to run the instance
    resid = sleepresid = -1;
    done = 0;
    for (i = 0; i < resourceCache->numResources && !done; i++) {
        int mem, disk, cores;

        res = &(resourceCache->resources[i]);
        if ((res->state == RESUP || res->state == RESWAKING) && resid == -1) {
            mem = res->availMemory - vm->mem;
            disk = res->availDisk - vm->disk;
            cores = res->availCores - vm->cores;

            if (mem >= 0 && disk >= 0 && cores >= 0) {
                resid = i;
                done++;
            }
        } else if (res->state == RESASLEEP && sleepresid == -1) {
            mem = res->availMemory - vm->mem;
            disk = res->availDisk - vm->disk;
            cores = res->availCores - vm->cores;

            if (mem >= 0 && disk >= 0 && cores >= 0) {
                sleepresid = i;
            }
        }
    }

    if (resid == -1 && sleepresid == -1) {
        // didn't find a resource
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