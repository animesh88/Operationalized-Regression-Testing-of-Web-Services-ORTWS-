int ncClientCall(ncMetadata * pMeta, int timeout, int ncLock, char *ncURL, char *ncOp, ...)
{
    va_list al;
    int pid, rc = 0, ret = 0, status = 0, opFail = 0, len, rbytes, i;
    int filedes[2];

    LOGTRACE("invoked: ncOps=%s ncURL=%s timeout=%d\n", ncOp, ncURL, timeout); // these are common

    rc = pipe(filedes);
    if (rc) {
        LOGERROR("cannot create pipe ncOps=%s\n", ncOp);
        return (1);
    }

    va_start(al, ncOp);

    // grab the lock
    sem_mywait(ncLock);

    pid = fork();
    if (!pid) {
        ncStub *ncs;
        ncMetadata *localmeta = NULL;

        localmeta = EUCA_ZALLOC(1, sizeof(ncMetadata));
        if (!localmeta) {
            LOGFATAL("out of memory! ncOps=%s\n", ncOp);
            unlock_exit(1);
        }
        memcpy(localmeta, pMeta, sizeof(ncMetadata));
        if (pMeta->correlationId) {
            localmeta->correlationId = strdup(pMeta->correlationId);
        } else {
            localmeta->correlationId = strdup("unset");
        }
        if (pMeta->userId) {
            localmeta->userId = strdup(pMeta->userId);
        } else {
            localmeta->userId = strdup("eucalyptus");
        }

        close(filedes[0]);
        ncs = ncStubCreate(ncURL, NULL, NULL);
        if (config->use_wssec) {
            rc = InitWSSEC(ncs->env, ncs->stub, config->policyFile);
        }

        LOGTRACE("\tncOps=%s ppid=%d client calling '%s'\n", ncOp, getppid(), ncOp);
        if (!strcmp(ncOp, "ncGetConsoleOutput")) {
            // args: char *instId
            char *instId = va_arg(al, char *);
            char **consoleOutput = va_arg(al, char **);

            rc = ncGetConsoleOutputStub(ncs, localmeta, instId, consoleOutput);
            if (timeout && consoleOutput) {
                if (!rc && *consoleOutput) {
                    len = strlen(*consoleOutput) + 1;
                    rc = write(filedes[1], &len, sizeof(int));
                    rc = write(filedes[1], *consoleOutput, sizeof(char) * len);
                    rc = 0;
                } else {
                    len = 0;
                    rc = write(filedes[1], &len, sizeof(int));
                    rc = 1;
                }
            }
        } else if (!strcmp(ncOp, "ncAttachVolume")) {
            char *instanceId = va_arg(al, char *);
            char *volumeId = va_arg(al, char *);
            char *remoteDev = va_arg(al, char *);
            char *localDev = va_arg(al, char *);

            rc = ncAttachVolumeStub(ncs, localmeta, instanceId, volumeId, remoteDev, localDev);
        } else if (!strcmp(ncOp, "ncDetachVolume")) {
            char *instanceId = va_arg(al, char *);
            char *volumeId = va_arg(al, char *);
            char *remoteDev = va_arg(al, char *);
            char *localDev = va_arg(al, char *);
            int force = va_arg(al, int);

            rc = ncDetachVolumeStub(ncs, localmeta, instanceId, volumeId, remoteDev, localDev, force);
        } else if (!strcmp(ncOp, "ncCreateImage")) {
            char *instanceId = va_arg(al, char *);
            char *volumeId = va_arg(al, char *);
            char *remoteDev = va_arg(al, char *);

            rc = ncCreateImageStub(ncs, localmeta, instanceId, volumeId, remoteDev);
        } else if (!strcmp(ncOp, "ncPowerDown")) {
            rc = ncPowerDownStub(ncs, localmeta);
        } else if (!strcmp(ncOp, "ncAssignAddress")) {
            char *instanceId = va_arg(al, char *);
            char *publicIp = va_arg(al, char *);

            rc = ncAssignAddressStub(ncs, localmeta, instanceId, publicIp);
        } else if (!strcmp(ncOp, "ncRebootInstance")) {
            char *instId = va_arg(al, char *);

            rc = ncRebootInstanceStub(ncs, localmeta, instId);
        } else if (!strcmp(ncOp, "ncTerminateInstance")) {
            char *instId = va_arg(al, char *);
            int force = va_arg(al, int);
            int *shutdownState = va_arg(al, int *);
            int *previousState = va_arg(al, int *);

            rc = ncTerminateInstanceStub(ncs, localmeta, instId, force, shutdownState, previousState);

            if (timeout) {
                if (!rc) {
                    len = 2;
                    rc = write(filedes[1], &len, sizeof(int));
                    rc = write(filedes[1], shutdownState, sizeof(int));
                    rc = write(filedes[1], previousState, sizeof(int));
                    rc = 0;
                } else {
                    len = 0;
                    rc = write(filedes[1], &len, sizeof(int));
                    rc = 1;
                }
            }
        } else if (!strcmp(ncOp, "ncStartNetwork")) {
            char *uuid = va_arg(al, char *);
            char **peers = va_arg(al, char **);
            int peersLen = va_arg(al, int);
            int port = va_arg(al, int);
            int vlan = va_arg(al, int);
            char **outStatus = va_arg(al, char **);

            rc = ncStartNetworkStub(ncs, localmeta, uuid, peers, peersLen, port, vlan, outStatus);
            if (timeout && outStatus) {
                if (!rc && *outStatus) {
                    len = strlen(*outStatus) + 1;
                    rc = write(filedes[1], &len, sizeof(int));
                    rc = write(filedes[1], *outStatus, sizeof(char) * len);
                    rc = 0;
                } else {
                    len = 0;
                    rc = write(filedes[1], &len, sizeof(int));
                    rc = 1;
                }
            }
        } else if (!strcmp(ncOp, "ncRunInstance")) {
            char *uuid = va_arg(al, char *);
            char *instId = va_arg(al, char *);
            char *reservationId = va_arg(al, char *);
            virtualMachine *ncvm = va_arg(al, virtualMachine *);
            char *imageId = va_arg(al, char *);
            char *imageURL = va_arg(al, char *);
            char *kernelId = va_arg(al, char *);
            char *kernelURL = va_arg(al, char *);
            char *ramdiskId = va_arg(al, char *);
            char *ramdiskURL = va_arg(al, char *);
            char *ownerId = va_arg(al, char *);
            char *accountId = va_arg(al, char *);
            char *keyName = va_arg(al, char *);
            netConfig *ncnet = va_arg(al, netConfig *);
            char *userData = va_arg(al, char *);
            char *launchIndex = va_arg(al, char *);
            char *platform = va_arg(al, char *);
            int expiryTime = va_arg(al, int);
            char **netNames = va_arg(al, char **);
            int netNamesLen = va_arg(al, int);
            ncInstance **outInst = va_arg(al, ncInstance **);

            rc = ncRunInstanceStub(ncs, localmeta, uuid, instId, reservationId, ncvm, imageId, imageURL, kernelId, kernelURL, ramdiskId, ramdiskURL,
                                   ownerId, accountId, keyName, ncnet, userData, launchIndex, platform, expiryTime, netNames, netNamesLen, outInst);
            if (timeout && outInst) {
                if (!rc && *outInst) {
                    len = sizeof(ncInstance);
                    rc = write(filedes[1], &len, sizeof(int));
                    rc = write(filedes[1], *outInst, sizeof(ncInstance));
                    rc = 0;
                } else {
                    len = 0;
                    rc = write(filedes[1], &len, sizeof(int));
                    rc = 1;
                }
            }
        } else if (!strcmp(ncOp, "ncDescribeInstances")) {
            char **instIds = va_arg(al, char **);
            int instIdsLen = va_arg(al, int);
            ncInstance ***ncOutInsts = va_arg(al, ncInstance ***);
            int *ncOutInstsLen = va_arg(al, int *);

            rc = ncDescribeInstancesStub(ncs, localmeta, instIds, instIdsLen, ncOutInsts, ncOutInstsLen);
            if (timeout && ncOutInsts && ncOutInstsLen) {
                if (!rc) {
                    len = *ncOutInstsLen;
                    rc = write(filedes[1], &len, sizeof(int));
                    for (i = 0; i < len; i++) {
                        ncInstance *inst;
                        inst = (*ncOutInsts)[i];
                        rc = write(filedes[1], inst, sizeof(ncInstance));
                    }
                    rc = 0;
                } else {
                    len = 0;
                    rc = write(filedes[1], &len, sizeof(int));
                    rc = 1;
                }
            }
        } else if (!strcmp(ncOp, "ncDescribeResource")) {
            char *resourceType = va_arg(al, char *);
            ncResource **outRes = va_arg(al, ncResource **);

            rc = ncDescribeResourceStub(ncs, localmeta, resourceType, outRes);
            if (timeout && outRes) {
                if (!rc && *outRes) {
                    len = sizeof(ncResource);
                    rc = write(filedes[1], &len, sizeof(int));
                    rc = write(filedes[1], *outRes, sizeof(ncResource));
                    rc = 0;
                } else {
                    len = 0;
                    rc = write(filedes[1], &len, sizeof(int));
                    rc = 1;
                }
            }
        } else if (!strcmp(ncOp, "ncDescribeSensors")) {
            int history_size = va_arg(al, int);
            long long collection_interval_time_ms = va_arg(al, long long);
            char **instIds = va_arg(al, char **);
            int instIdsLen = va_arg(al, int);
            char **sensorIds = va_arg(al, char **);
            int sensorIdsLen = va_arg(al, int);
            sensorResource ***srs = va_arg(al, sensorResource ***);
            int *srsLen = va_arg(al, int *);

            rc = ncDescribeSensorsStub(ncs, localmeta, history_size, collection_interval_time_ms, instIds, instIdsLen, sensorIds, sensorIdsLen, srs,
                                       srsLen);

            if (timeout && srs && srsLen) {
                if (!rc) {
                    len = *srsLen;
                    rc = write(filedes[1], &len, sizeof(int));
                    for (i = 0; i < len; i++) {
                        sensorResource *sr;
                        sr = (*srs)[i];
                        rc = write(filedes[1], sr, sizeof(sensorResource));
                    }
                    rc = 0;
                } else {
                    len = 0;
                    rc = write(filedes[1], &len, sizeof(int));
                    rc = 1;
                }
            }

        } else if (!strcmp(ncOp, "ncBundleInstance")) {
            char *instanceId = va_arg(al, char *);
            char *bucketName = va_arg(al, char *);
            char *filePrefix = va_arg(al, char *);
            char *walrusURL = va_arg(al, char *);
            char *userPublicKey = va_arg(al, char *);
            char *S3Policy = va_arg(al, char *);
            char *S3PolicySig = va_arg(al, char *);

            rc = ncBundleInstanceStub(ncs, localmeta, instanceId, bucketName, filePrefix, walrusURL, userPublicKey, S3Policy, S3PolicySig);
        } else if (!strcmp(ncOp, "ncBundleRestartInstance")) {
            char *instanceId = va_arg(al, char *);
            rc = ncBundleRestartInstanceStub(ncs, localmeta, instanceId);
        } else if (!strcmp(ncOp, "ncCancelBundleTask")) {
            char *instanceId = va_arg(al, char *);
            rc = ncCancelBundleTaskStub(ncs, localmeta, instanceId);
        } else if (!strcmp(ncOp, "ncModifyNode")) {
            char *stateName = va_arg(al, char *);
            rc = ncModifyNodeStub(ncs, localmeta, stateName);
        } else if (!strcmp(ncOp, "ncMigrateInstances")) {
            ncInstance **instances = va_arg(al, ncInstance **);
            int instancesLen = va_arg(al, int);
            char *action = va_arg(al, char *);
            char *credentials = va_arg(al, char *);
            rc = ncMigrateInstancesStub(ncs, localmeta, instances, instancesLen, action, credentials);
        } else {
            LOGWARN("\tncOps=%s ppid=%d operation '%s' not found\n", ncOp, getppid(), ncOp);
            rc = 1;
        }
        LOGTRACE("\tncOps=%s ppid=%d done calling '%s' with exit code '%d'\n", ncOp, getppid(), ncOp, rc);
        if (rc) {
            ret = 1;
        } else {
            ret = 0;
        }
        close(filedes[1]);
        EUCA_FREE(localmeta);
        exit(ret);
    } else {
        // returns for each client call
        close(filedes[1]);

        if (!strcmp(ncOp, "ncGetConsoleOutput")) {
            char *instId = NULL;
            char **outConsoleOutput = NULL;

            instId = va_arg(al, char *);
            outConsoleOutput = va_arg(al, char **);
            if (outConsoleOutput) {
                *outConsoleOutput = NULL;
            }
            if (timeout && outConsoleOutput) {
                rbytes = timeread(filedes[0], &len, sizeof(int), timeout);
                if (rbytes <= 0) {
                    kill(pid, SIGKILL);
                    opFail = 1;
                } else {
                    *outConsoleOutput = EUCA_ALLOC(len, sizeof(char));
                    if (!*outConsoleOutput) {
                        LOGFATAL("out of memory! ncOps=%s\n", ncOp);
                        unlock_exit(1);
                    }
                    rbytes = timeread(filedes[0], *outConsoleOutput, len, timeout);
                    if (rbytes <= 0) {
                        kill(pid, SIGKILL);
                        opFail = 1;
                    }
                }
            }
        } else if (!strcmp(ncOp, "ncTerminateInstance")) {
            char *instId = NULL;
            int force = 0;
            int *shutdownState = NULL;
            int *previousState = NULL;

            instId = va_arg(al, char *);
            force = va_arg(al, int);
            shutdownState = va_arg(al, int *);
            previousState = va_arg(al, int *);
            if (shutdownState && previousState) {
                *shutdownState = *previousState = 0;
            }
            if (timeout && shutdownState && previousState) {
                rbytes = timeread(filedes[0], &len, sizeof(int), timeout);
                if (rbytes <= 0) {
                    kill(pid, SIGKILL);
                    opFail = 1;
                } else {
                    rbytes = timeread(filedes[0], shutdownState, sizeof(int), timeout);
                    if (rbytes <= 0) {
                        kill(pid, SIGKILL);
                        opFail = 1;
                    }
                    rbytes = timeread(filedes[0], previousState, sizeof(int), timeout);
                    if (rbytes <= 0) {
                        kill(pid, SIGKILL);
                        opFail = 1;
                    }
                }
            }
        } else if (!strcmp(ncOp, "ncStartNetwork")) {
            char *uuid = NULL;
            char **peers = NULL;
            int peersLen = 0;
            int port = 0;
            int vlan = 0;
            char **outStatus = NULL;

            uuid = va_arg(al, char *);
            peers = va_arg(al, char **);
            peersLen = va_arg(al, int);
            port = va_arg(al, int);
            vlan = va_arg(al, int);
            outStatus = va_arg(al, char **);
            if (outStatus) {
                *outStatus = NULL;
            }
            if (timeout && outStatus) {
                *outStatus = NULL;
                rbytes = timeread(filedes[0], &len, sizeof(int), timeout);
                if (rbytes <= 0) {
                    kill(pid, SIGKILL);
                    opFail = 1;
                } else {
                    *outStatus = EUCA_ALLOC(len, sizeof(char));
                    if (!*outStatus) {
                        LOGFATAL("out of memory! ncOps=%s\n", ncOp);
                        unlock_exit(1);
                    }
                    rbytes = timeread(filedes[0], *outStatus, len, timeout);
                    if (rbytes <= 0) {
                        kill(pid, SIGKILL);
                        opFail = 1;
                    }
                }
            }
        } else if (!strcmp(ncOp, "ncRunInstance")) {
            char *uuid = NULL;
            char *instId = NULL;
            char *reservationId = NULL;
            virtualMachine *ncvm = NULL;
            char *imageId = NULL;
            char *imageURL = NULL;
            char *kernelId = NULL;
            char *kernelURL = NULL;
            char *ramdiskId = NULL;
            char *ramdiskURL = NULL;
            char *ownerId = NULL;
            char *accountId = NULL;
            char *keyName = NULL;
            netConfig *ncnet = NULL;
            char *userData = NULL;
            char *launchIndex = NULL;
            char *platform = NULL;
            int expiryTime = 0;
            char **netNames = NULL;
            int netNamesLen = 0;
            ncInstance **outInst = NULL;

            uuid = va_arg(al, char *);
            instId = va_arg(al, char *);
            reservationId = va_arg(al, char *);
            ncvm = va_arg(al, virtualMachine *);
            imageId = va_arg(al, char *);
            imageURL = va_arg(al, char *);
            kernelId = va_arg(al, char *);
            kernelURL = va_arg(al, char *);
            ramdiskId = va_arg(al, char *);
            ramdiskURL = va_arg(al, char *);
            ownerId = va_arg(al, char *);
            accountId = va_arg(al, char *);
            keyName = va_arg(al, char *);
            ncnet = va_arg(al, netConfig *);
            userData = va_arg(al, char *);
            launchIndex = va_arg(al, char *);
            platform = va_arg(al, char *);
            expiryTime = va_arg(al, int);
            netNames = va_arg(al, char **);
            netNamesLen = va_arg(al, int);
            outInst = va_arg(al, ncInstance **);
            if (outInst) {
                *outInst = NULL;
            }
            if (timeout && outInst) {
                rbytes = timeread(filedes[0], &len, sizeof(int), timeout);
                if (rbytes <= 0) {
                    kill(pid, SIGKILL);
                    opFail = 1;
                } else {
                    *outInst = EUCA_ZALLOC(1, sizeof(ncInstance));
                    if (!*outInst) {
                        LOGFATAL("out of memory! ncOps=%s\n", ncOp);
                        unlock_exit(1);
                    }
                    rbytes = timeread(filedes[0], *outInst, sizeof(ncInstance), timeout);
                    if (rbytes <= 0) {
                        kill(pid, SIGKILL);
                        opFail = 1;
                    }
                }
            }
        } else if (!strcmp(ncOp, "ncDescribeInstances")) {
            char **instIds = NULL;
            int instIdsLen = 0;
            ncInstance ***ncOutInsts = NULL;
            int *ncOutInstsLen = NULL;

            instIds = va_arg(al, char **);
            instIdsLen = va_arg(al, int);
            ncOutInsts = va_arg(al, ncInstance ***);
            ncOutInstsLen = va_arg(al, int *);
            if (ncOutInstsLen && ncOutInsts) {
                *ncOutInstsLen = 0;
                *ncOutInsts = NULL;
            }
            if (timeout && ncOutInsts && ncOutInstsLen) {
                rbytes = timeread(filedes[0], &len, sizeof(int), timeout);
                if (rbytes <= 0) {
                    kill(pid, SIGKILL);
                    opFail = 1;
                } else {
                    *ncOutInsts = EUCA_ZALLOC(len, sizeof(ncInstance *));
                    if (!*ncOutInsts) {
                        LOGFATAL("out of memory! ncOps=%s\n", ncOp);
                        unlock_exit(1);
                    }
                    *ncOutInstsLen = len;
                    for (i = 0; i < len; i++) {
                        ncInstance *inst;
                        inst = EUCA_ZALLOC(1, sizeof(ncInstance));
                        if (!inst) {
                            LOGFATAL("out of memory! ncOps=%s\n", ncOp);
                            unlock_exit(1);
                        }
                        rbytes = timeread(filedes[0], inst, sizeof(ncInstance), timeout);
                        (*ncOutInsts)[i] = inst;
                    }
                }
            }
        } else if (!strcmp(ncOp, "ncDescribeResource")) {
            char *resourceType = NULL;
            ncResource **outRes = NULL;

            resourceType = va_arg(al, char *);
            outRes = va_arg(al, ncResource **);
            if (outRes) {
                *outRes = NULL;
            }
            if (timeout && outRes) {
                rbytes = timeread(filedes[0], &len, sizeof(int), timeout);
                if (rbytes <= 0) {
                    kill(pid, SIGKILL);
                    opFail = 1;
                } else {
                    *outRes = EUCA_ZALLOC(1, sizeof(ncResource));
                    if (*outRes == NULL) {
                        LOGFATAL("out of memory! ncOps=%s\n", ncOp);
                        unlock_exit(1);
                    }
                    rbytes = timeread(filedes[0], *outRes, sizeof(ncResource), timeout);
                    if (rbytes <= 0) {
                        kill(pid, SIGKILL);
                        opFail = 1;
                    }
                }
            }
        } else if (!strcmp(ncOp, "ncDescribeSensors")) {
            int history_size = 0;
            long long collection_interval_time_ms = 0L;
            char **instIds = NULL;
            int instIdsLen = 0;
            char **sensorIds = NULL;
            int sensorIdsLen = 0;
            sensorResource ***srs = NULL;
            int *srsLen = NULL;

            history_size = va_arg(al, int);
            collection_interval_time_ms = va_arg(al, long long);
            instIds = va_arg(al, char **);
            instIdsLen = va_arg(al, int);
            sensorIds = va_arg(al, char **);
            sensorIdsLen = va_arg(al, int);
            srs = va_arg(al, sensorResource ***);
            srsLen = va_arg(al, int *);

            if (srs && srsLen) {
                *srs = NULL;
                *srsLen = 0;
            }
            if (timeout && srs && srsLen) {
                rbytes = timeread(filedes[0], &len, sizeof(int), timeout);
                if (rbytes <= 0) {
                    kill(pid, SIGKILL);
                    opFail = 1;
                } else {
                    *srs = EUCA_ZALLOC(len, sizeof(sensorResource *));
                    if (*srs == NULL) {
                        LOGFATAL("out of memory! ncOps=%s\n", ncOp);
                        unlock_exit(1);
                    }
                    *srsLen = len;
                    for (i = 0; i < len; i++) {
                        sensorResource *sr;
                        sr = EUCA_ZALLOC(1, sizeof(sensorResource));
                        if (sr == NULL) {
                            LOGFATAL("out of memory! ncOps=%s\n", ncOp);
                            unlock_exit(1);
                        }
                        rbytes = timeread(filedes[0], sr, sizeof(sensorResource), timeout);
                        (*srs)[i] = sr;
                    }
                }
            }
        } else {
            // nothing to do in default case (succ/fail encoded in exit code)
        }

        close(filedes[0]);
        if (timeout) {
            rc = timewait(pid, &status, timeout);
            rc = WEXITSTATUS(status);
        } else {
            rc = 0;
        }
    }

    LOGTRACE("done ncOps=%s clientrc=%d opFail=%d\n", ncOp, rc, opFail);
    if (rc || opFail) {
        ret = 1;
    } else {
        ret = 0;
    }

    // release the lock
    sem_mypost(ncLock);

    va_end(al);

    return (ret);
}