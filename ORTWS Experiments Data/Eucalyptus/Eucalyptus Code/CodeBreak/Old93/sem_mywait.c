int sem_mywait(int lockno)
{
    int rc;
    rc = sem_wait(locks[lockno]);
    mylocks[lockno] = 1;
    return (rc);
}