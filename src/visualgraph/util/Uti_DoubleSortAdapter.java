package visualgraph.util;

public class Uti_DoubleSortAdapter implements Uti_SortAdapter
{
    /** compare two values*/
    public int compare(Object x1, Object x2)
    {
        if ((x1 instanceof Double)
        &&  (x2 instanceof Double))
        {
            double n1 = ((Double)x1).doubleValue();
            double n2 = ((Double)x2).doubleValue();

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

