int changeState(ccResource * in, int newstate)
{
    if (in == NULL)
        return (1);
    if (in->state == newstate)
        return (0);

    in->lastState = in->state;
    in->state = newstate;
    in->stateChange = time(NULL);
    in->idleStart = 0;

    return (0);
}