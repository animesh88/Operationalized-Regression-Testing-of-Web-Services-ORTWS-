int setup_shared_buffer(void **buf, char *bufname, size_t bytes, sem_t ** lock, char *lockname, int mode)
{
    int shd, rc, ret;

    // create a lock and grab it
    *lock = sem_open(lockname, O_CREAT, 0644, 1);
    sem_wait(*lock);
    ret = 0;

    if (mode == SHARED_MEM) {
        // set up shared memory segment for config
        shd = shm_open(bufname, O_CREAT | O_RDWR | O_EXCL, 0644);
        if (shd >= 0) {
            // if this is the first process to create the config, init to 0
            rc = ftruncate(shd, bytes);
        } else {
            shd = shm_open(bufname, O_CREAT | O_RDWR, 0644);
        }
        if (shd < 0) {
            fprintf(stderr, "cannot initialize shared memory segment\n");
            sem_post(*lock);
            sem_close(*lock);
            return (1);
        }
        *buf = mmap(0, bytes, PROT_READ | PROT_WRITE, MAP_SHARED, shd, 0);
    } else if (mode == SHARED_FILE) {
        char *tmpstr, path[MAX_PATH];
        struct stat mystat;
        int fd;

        tmpstr = getenv(EUCALYPTUS_ENV_VAR_NAME);
        if (!tmpstr) {
            snprintf(path, MAX_PATH, EUCALYPTUS_STATE_DIR "/CC/%s", "", bufname);
        } else {
            snprintf(path, MAX_PATH, EUCALYPTUS_STATE_DIR "/CC/%s", tmpstr, bufname);
        }
        fd = open(path, O_RDWR | O_CREAT, 0600);
        if (fd < 0) {
            fprintf(stderr, "ERROR: cannot open/create '%s' to set up mmapped buffer\n", path);
            ret = 1;
        } else {
            mystat.st_size = 0;
            rc = fstat(fd, &mystat);
            // this is the check to make sure we're dealing with a valid prior config
            if (mystat.st_size != bytes) {
                rc = ftruncate(fd, bytes);
            }
            *buf = mmap(NULL, bytes, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
            if (*buf == NULL) {
                fprintf(stderr, "ERROR: cannot mmap fd\n");
                ret = 1;
            }
            close(fd);
        }
    }
    sem_post(*lock);
    return (ret);
}