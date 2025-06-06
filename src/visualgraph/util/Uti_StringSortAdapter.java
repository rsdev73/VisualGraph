package visualgraph.util;

public class Uti_StringSortAdapter implements Uti_SortAdapter
{
    /**compare*/
    public int compare(Object x1, Object x2)
    {
        if ((x1 instanceof String)
        &&  (x2 instanceof String))
        {
            String n1 = (String) x1;
            String n2 = (String) x2;            
            
            if (n1.compareTo(n2) < 0)
                return COMP_LESS;
            else
                if (n1.compareTo(n2) > 0)
                    return COMP_GRTR;
                else
                    return COMP_EQUAL;
        }
        else
            throw Uti_SortAdapter.err1;
    }
}

