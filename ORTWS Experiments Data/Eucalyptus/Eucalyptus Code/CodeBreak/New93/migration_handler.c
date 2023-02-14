int migration_handler(ccInstance *myInstance, char *host, char *src, char *dst, migration_states migration_state, char **node, char **action)
{
    int rc = 0;

    LOGDEBUG("invoked\n");

    if (!strcmp(host, dst)) {
        if (migration_state == MIGRATION_READY) {
            if (!strcmp(myInstance->state, "Teardown")) {
                LOGDEBUG("[%s] destination node %s reports ready to receive migration, but is in Teardown--ignoring...\n", myInstance->instanceId, host);
                rc++;
                goto out;
            }
            LOGDEBUG("[%s] destination node %s reports ready to receive migration, checking source node %s...\n", myInstance->instanceId, host, src);
            ccInstance *srcInstance = NULL;
            rc = find_instanceCacheId(myInstance->instanceId, &srcInstance);
            if (!rc) {
                if (srcInstance->migration_state == MIGRATION_READY) {
                    LOGDEBUG("[%s] source node %s reports ready to commit migration to %s.\n", myInstance->instanceId, src, dst);
                    EUCA_FREE(*node);
                    EUCA_FREE(*action);
                    *node = strdup(src);
                    *action = strdup("commit");
                } else if (srcInstance->migration_state == MIGRATION_IN_PROGRESS) {
                    LOGDEBUG("[%s] source node %s reports migration to %s in progress.\n", myInstance->instanceId, src, dst);
                } else if (srcInstance->migration_state == NOT_MIGRATING) {
                    LOGINFO("[%s] source node %s reports migration_state=%s, rolling back destination node %s...",
                            myInstance->instanceId, src, migration_state_names[srcInstance->migration_state], dst);
                    EUCA_FREE(*node);
                    EUCA_FREE(*action);
                    *node = strdup(dst);
                    *action = strdup("rollback");
                } else {
                    LOGDEBUG("[%s] source node %s not reporting ready to commit migration to %s (migration_state=%s).\n",
                             myInstance->instanceId, src, dst, migration_state_names[srcInstance->migration_state]);
                }
            } else {
                LOGERROR("[%s] could not find migration source node %s in the instance cache.\n", myInstance->instanceId, src);
            }
            EUCA_FREE(srcInstance);
        } else {
            LOGTRACE("[%s] ignoring updates from destination node %s during migration.\n", myInstance->instanceId, host);
        }
    } else if (!strcmp(host, src)) {
        LOGDEBUG("[%s] received migration state %s from source node %s\n",
                 myInstance->instanceId, migration_state_names[migration_state], host);
    } else {
        LOGERROR("[%s] received status from a migrating node that's neither the source (%s) nor the destination (%s): %s\n", myInstance->instanceId, src, dst, host);
    }
 out:
    LOGDEBUG("done\n");
    return rc;
}