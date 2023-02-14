void set_clean_instanceCache(void)
{
    sem_mywait(INSTCACHE);
    instanceCache->dirty = 0;
    sem_mypost(INSTCACHE);
}