int is_clean_instanceCache(void)
{
    int ret = 1;
    sem_mywait(INSTCACHE);
    if (instanceCache->dirty) {
        ret = 0;
    } else {
        ret = 1;
    }
    sem_mypost(INSTCACHE);
    return (ret);
}