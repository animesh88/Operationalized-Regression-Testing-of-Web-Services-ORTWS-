int sem_mypost(int lockno)
{
    mylocks[lockno] = 0;
    return (sem_post(locks[lockno]));
}