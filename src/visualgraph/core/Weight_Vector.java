package visualgraph.core;
import java.util.*;

public class Weight_Vector extends Vector
{
    static final long serialVersionUID = 996410598914093856L;
    
    public void Add(double value) 
    {        
        super.addElement((Object) Double.valueOf(value) );
    }
    
    public double Get(int index) 
    {
        return( ((Double) super.elementAt(index-1)).doubleValue());
    }    

    public void Set(int index, double value) 
    {
        super.setElementAt((Object) Double.valueOf(value),index-1);
    }

    public int Size() 
    {
        return(super.size());
    }  

    /**Union: Vereinigung zweier Mengen ermitteln*/
    public Weight_Vector Union(Weight_Vector union_vec)
    {
        Weight_Vector res_vector;   

        res_vector = new Weight_Vector();
        
        return(res_vector);
    }
    
    /**Intersection: Schnittmenge ermitteln*/
    public Weight_Vector Intersection(Weight_Vector diff_vec)
    {
        Weight_Vector res_vector;
        
        res_vector = new Weight_Vector();
        
        return(res_vector);
    }

    /**Difference: Differenzmenge ermitteln*/
    public Weight_Vector Difference(Weight_Vector substract_vec)
    {
        Weight_Vector res_vector;        
        
        res_vector = new Weight_Vector();
        
        return(res_vector);
    }
    
    public String GetString ()
    {
        int i;
        String s="";
        double value;

        if (isEmpty()==false)
        {
            s="";
            for(i=1;i<=elementCount;i++)
            {
                value = Get(i);
                
                if (s!="")
                    s=s+", "+value;
                else
                    s=s+value;
            }
        }
        else
            s = "empty";
        
        return(s);
    }
}
