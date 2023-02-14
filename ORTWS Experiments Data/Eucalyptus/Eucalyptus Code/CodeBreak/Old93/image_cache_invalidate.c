int image_cache_invalidate(void)
{
    time_t oldest;
    char proxyPath[MAX_PATH], path[MAX_PATH], oldestpath[MAX_PATH], oldestmanifestpath[MAX_PATH];
    DIR *DH = NULL;
    struct dirent dent, *result = NULL;
    struct stat mystat;
    int rc, total_megs = 0;

    if (config->use_proxy) {
        proxyPath[0] = '\0';
        path[0] = '\0';
        oldestpath[0] = '\0';
        oldestmanifestpath[0] = '\0';

        oldest = time(NULL);
        snprintf(proxyPath, MAX_PATH, "%s/data", config->proxyPath);
        DH = opendir(proxyPath);
        if (!DH) {
            LOGERROR("could not open dir '%s'\n", proxyPath);
            return (1);
        }

        rc = readdir_r(DH, &dent, &result);
        while (!rc && result) {
            if (strcmp(dent.d_name, ".") && strcmp(dent.d_name, "..") && !strstr(dent.d_name, "manifest.xml")) {
                snprintf(path, MAX_PATH, "%s/%s", proxyPath, dent.d_name);
                rc = stat(path, &mystat);
                if (!rc) {
                    LOGDEBUG("evaluating file: name=%s size=%ld atime=%ld'\n", dent.d_name, mystat.st_size / 1048576, mystat.st_atime);
                    if (mystat.st_atime < oldest) {
                        oldest = mystat.st_atime;
                        snprintf(oldestpath, MAX_PATH, "%s", path);
                        snprintf(oldestmanifestpath, MAX_PATH, "%s.manifest.xml", path);
                    }
                    total_megs += mystat.st_size / 1048576;
                }
            }
            rc = readdir_r(DH, &dent, &result);
        }
        closedir(DH);
        LOGDEBUG("summary: totalMBs=%d oldestAtime=%ld oldestFile=%s\n", total_megs, oldest, oldestpath);
        if (total_megs > config->proxy_max_cache_size) {
            // start slowly deleting
            LOGINFO("invalidating cached image %s\n", oldestpath);
            unlink(oldestpath);
            unlink(oldestmanifestpath);
        }
    }

    return (0);
}