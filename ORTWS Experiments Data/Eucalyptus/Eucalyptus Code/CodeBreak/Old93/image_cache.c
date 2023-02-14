int image_cache(char *id, char *url)
{
    int rc;
    int pid;
    char path[MAX_PATH], finalpath[MAX_PATH];

    if (url && id) {
        pid = fork();
        if (!pid) {
            snprintf(finalpath, MAX_PATH, "%s/data/%s.manifest.xml", config->proxyPath, id);
            snprintf(path, MAX_PATH, "%s/data/%s.manifest.xml.staging", config->proxyPath, id);
            if (check_file(path) && check_file(finalpath)) {
                rc = walrus_object_by_url(url, path, 0);
                if (rc) {
                    LOGERROR("could not cache image manifest (%s/%s)\n", id, url);
                    unlink(path);
                    exit(1);
                }
                rename(path, finalpath);
                chmod(finalpath, 0600);
            }
            snprintf(path, MAX_PATH, "%s/data/%s.staging", config->proxyPath, id);
            snprintf(finalpath, MAX_PATH, "%s/data/%s", config->proxyPath, id);
            if (check_file(path) && check_file(finalpath)) {
                rc = walrus_image_by_manifest_url(url, path, 1);
                if (rc) {
                    LOGERROR("could not cache image (%s/%s)\n", id, url);
                    unlink(path);
                    exit(1);
                }
                rename(path, finalpath);
                chmod(finalpath, 0600);
            }
            exit(0);
        }
    }

    return (0);
}