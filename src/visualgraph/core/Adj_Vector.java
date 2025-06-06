package visualgraph.core;
import java.util.*;

import visualgraph.swing.Grf_Manager;

/**Adj_Vector: Ergebnis-Vektor f�r Adj_Node-Mengen*/
public class Adj_Vector extends Vector
{   
    /**Verweis auf Grf_Manager, zu setzen wenn GUI verwendet wird*/
    private Grf_Manager grm;
    
    static final long serialVersionUID = -6939261988659131320L;
    
    /**Konstruktoren*/
    public Adj_Vector()
    {
        this(null);
    }
    
    public Adj_Vector(Grf_Manager grm)
    {
        super();
        
        this.grm = grm;
    }
    
    /**R�ckgabe-Casting*/
    public void Add(Adj_VectorItem obj) 
    {
        super.addElement((Object)obj);
        
        /**GUI-Link setzen*/
        if(grm != null)
            obj.LinkToGUI(grm);
    }
    
    public Adj_VectorItem Get(int index) 
    {
        return((Adj_VectorItem) super.elementAt(index-1));
    }

    public int Size() 
    {
        return(super.size());
    }

    /**Union: Vereinigung zweier Mengen ermitteln*/
    public Adj_Vector Union(Adj_Vector union_vec)
    {
        Adj_Vector res_vector;   

        res_vector = new Adj_Vector(grm);
        
        return(res_vector);
    }
    
    /**Intersection: Schnittmenge ermitteln*/
    public Adj_Vector Intersection(Adj_Vector diff_vec)
    {
        Adj_Vector res_vector;
        
        res_vector = new Adj_Vector(grm);
        
        return(res_vector);
    }
    
    /**Difference: Differenzmenge ermitteln*/
    public Adj_Vector Difference(Adj_Vector substract_vec)
    {
        int i,ii;
        Adj_Vector res_vector;        
        Adj_VectorItem avi,vgl_avi;
        boolean found;
        
        res_vector = new Adj_Vector(grm);
        
        if(Size() == 0)
            return(res_vector);
            
        if(substract_vec == null)
            return (res_vector);

        for(i=1;i<=Size();i++)
        {
            vgl_avi = Get(i);

            found = false;

            for(ii=1;ii<=substract_vec.Size();ii++)
            {
                avi = substract_vec.Get(ii);                    
                
                if(avi.GetNodeNr() == vgl_avi.GetNodeNr())
                {                    
                    found = true;
                    break;
                }
            }
            
            if(!found)
            {
                res_vector.Add(vgl_avi);
            }
        }
        return (res_vector);        
    }
    
    /**GetString: Umwandeln des Vectors in einen String*/
    public String GetString ()
    {        
        int i;
        String s="";
        Adj_VectorItem avi;
        if (isEmpty()==false)
        {
            for(i=1;i<=elementCount;i++)
            {
                avi = Get(i);

                if (s!="")
                    s=s+", "+avi.GetId();
                else
                    s=s+avi.GetId();
    
                if (avi.IsNodeMarked())
                    s=s+" (m)";

            }
        }
        else
            s = "empty";
        
        return(s);
    }    
}
