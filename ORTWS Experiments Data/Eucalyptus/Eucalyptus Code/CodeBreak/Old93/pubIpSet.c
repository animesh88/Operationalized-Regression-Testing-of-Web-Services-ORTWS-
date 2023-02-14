int pubIpSet(ccInstance * inst, void *ip)
{
    if (!ip || !inst) {
        return (1);
    }

    if ((strcmp(inst->state, "Pending") && strcmp(inst->state, "Extant"))) {
        snprintf(inst->ccnet.publicIp, 24, "0.0.0.0");
        return (0);
    }

    LOGDEBUG("pubIpSet(): set: %s/%s\n", inst->ccnet.publicIp, (char *)ip);
    snprintf(inst->ccnet.publicIp, 24, "%s", (char *)ip);
    return (0);
}

//!
//!
//!
//! @param[in] match
//! @param[in] matchParam
//! @param[in] operate
//! @param[in] operateParam
//!
//! @return
//!
//! @pre
//!
//! @note
//!
int map_instanceCache(int (*match) (ccInstance *, void *), void *matchParam, int (*operate) (ccInstance *, void *), void *operateParam)
{
    int i, ret = 0;

    sem_mywait(INSTCACHE);

    for (i = 0; i < MAXINSTANCES_PER_CC; i++) {
        if (!match(&(instanceCache->instances[i]), matchParam)) {
            if (operate(&(instanceCache->instances[i]), operateParam)) {
                LOGWARN("instance cache mapping failed to operate at index %d\n", i);
                ret++;
            }
        }
    }

    sem_mypost(INSTCACHE);
    return (ret);
}