package visualgraph.util;

public class Uti_LongSortAdapter implements Uti_SortAdapter
{
    /** compare two values*/
    public int compare
        (
        Object x1,
        Object x2
        )
    {
        if ((x1 instanceof Long)
        &&  (x2 instanceof Long))
        {
            long n1 = ((Long)x1).longValue();
            long n2 = ((Long)x2).longValue();

            if (n1 < n2)
                return COMP_LESS;
            else
                if (n1 > n2)
                    return COMP_GRTR;
                else
                    return COMP_EQUAL;
        }
        else
            throw Uti_SortAdapter.err1;
    }
}

