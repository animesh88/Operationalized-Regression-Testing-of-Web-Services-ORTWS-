int refresh_instanceCache(char *instanceId, ccInstance * in)
{
    int i, done;

    if (!instanceId || !in) {
        return (1);
    }

    sem_mywait(INSTCACHE);
    done = 0;
    for (i = 0; i < MAXINSTANCES_PER_CC && !done; i++) {
        if (!strcmp(instanceCache->instances[i].instanceId, instanceId)) {
            // in cache
            // give precedence to instances that are in Extant/Pending over expired instances, when info comes from two different nodes
            if (strcmp(in->serviceTag, instanceCache->instances[i].serviceTag) && strcmp(in->state, instanceCache->instances[i].state)
                && !strcmp(in->state, "Teardown")) {
                // skip
                LOGDEBUG("skipping cache refresh with instance in Teardown (instance with non-Teardown from different node already cached)\n");
            } else {
                // update cached instance info
                memcpy(&(instanceCache->instances[i]), in, sizeof(ccInstance));
                instanceCache->lastseen[i] = time(NULL);
            }
            sem_mypost(INSTCACHE);
            return (0);
        }
    }
    sem_mypost(INSTCACHE);

    add_instanceCache(instanceId, in);

    return (0);
}