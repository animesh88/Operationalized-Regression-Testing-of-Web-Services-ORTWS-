int schedule_instance_migration(ncInstance *instance, char **includeNodes, char **excludeNodes, int *outresid)
{
    int ret = 0;

    LOGDEBUG("invoked\n");

    // FIXME: assumes one-entry list:
    if (includeNodes && includeNodes[0]) {
        // FIXME: Interpreted as a single explicit destination.
        ret = schedule_instance_explicit(&(instance->params), includeNodes[0], outresid);
    } else {
        // Fall back to configured scheduling policy.
        ret = schedule_instance(&(instance->params), NULL, outresid);
    }

    if (ret) {
        LOGERROR("[%s] migration scheduler could not schedule destination node (%s).\n",
                 instance->instanceId, instance->migration_dst);
    }

    LOGDEBUG("done\n");

    return (ret);
}