int ncGetTimeout(time_t op_start, time_t op_max, int numCalls, int idx)
{
    time_t op_timer, op_pernode;
    int numLeft;

    numLeft = numCalls - idx;
    if (numLeft <= 0) {
        numLeft = 1;
    }

    op_timer = op_max - (time(NULL) - op_start);
    op_pernode = op_timer / numLeft;

    return (maxint(minint(op_pernode, OP_TIMEOUT_PERNODE), OP_TIMEOUT_MIN));
}