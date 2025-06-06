package visualgraph.util;

public interface Uti_SortAdapter
{
    /** comparison values*/
    int COMP_LESS  = -1;
    int COMP_EQUAL =  0;
    int COMP_GRTR  =  1;

    IllegalArgumentException err1 =
        new IllegalArgumentException("incompatible objects in sort");

    /** compare two values*/
    int compare(Object x1,Object x2);
}

