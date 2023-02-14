void unlock_exit(int code)
{
    int i;

    LOGDEBUG("params: code=%d\n", code);

    for (i = 0; i < ENDLOCK; i++) {
        if (mylocks[i]) {
            LOGWARN("unlocking index '%d'\n", i);
            sem_post(locks[i]);
        }
    }
    exit(code);
}