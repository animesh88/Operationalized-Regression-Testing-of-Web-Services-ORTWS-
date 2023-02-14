int refresh_resourceCache(char *host, ccResource * in)
{
    int i, done;

    if (!host || !in) {
        return (1);
    }

    sem_mywait(RESCACHE);
    done = 0;
    for (i = 0; i < MAXNODES && !done; i++) {
        if (resourceCache->cacheState[i] == RESVALID) {
            if (!strcmp(resourceCache->resources[i].hostname, host)) {
                // in cache
                memcpy(&(resourceCache->resources[i]), in, sizeof(ccResource));
                sem_mypost(RESCACHE);
                return (0);
            }
        }
    }
    sem_mypost(RESCACHE);

    add_resourceCache(host, in);

    return (0);
}