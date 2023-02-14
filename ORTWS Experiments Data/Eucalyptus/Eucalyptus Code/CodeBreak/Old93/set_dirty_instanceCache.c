void set_dirty_instanceCache(void)
{
    sem_mywait(INSTCACHE);
    instanceCache->dirty = 1;
    sem_mypost(INSTCACHE);
}