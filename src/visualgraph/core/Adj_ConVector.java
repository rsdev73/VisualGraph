package visualgraph.core;
import java.util.*;

import visualgraph.swing.Grf_Manager;
import visualgraph.util.Uti_QuickSorter;
import visualgraph.util.Uti_SortAdapter;

/**Adj_ConVector: Ergebnis-Vektor f�r Adj_Connection-Mengen*/
public class Adj_ConVector extends Vector
{   
    /**Verweis auf Grf_Manager, zu setzen wenn GUI verwendet wird*/
    private Grf_Manager grm;
    
    static final long serialVersionUID = 3771861757080478231L;
    
    /**Konstruktoren*/
    public Adj_ConVector()
    {
        this(null);
    }
    
    public Adj_ConVector(Grf_Manager grm)
    {
        super();
        
        this.grm = grm;
    }
    
    /**R�ckgabe-Casting*/
    public void Add(Adj_ConVectorItem obj) 
    {
        super.addElement((Object)obj);
        
        /**GUI-Link setzen*/
        if(grm != null)
            obj.LinkToGUI(grm);
    }
    
    public Adj_ConVectorItem Get(int index) 
    {
        return((Adj_ConVectorItem) super.elementAt(index-1));
    }

    public int Size() 
    {
        return(super.size());
    }

    /**Union: Vereinigung zweier Mengen ermitteln*/
    public Adj_ConVector Union(Adj_ConVector union_vec)
    {
        Adj_ConVector res_vector;   

        res_vector = new Adj_ConVector(grm);
        
        return(res_vector);
    }
    
    /**Intersection: Schnittmenge ermitteln*/
    public Adj_ConVector Intersection(Adj_ConVector diff_vec)
    {
        Adj_ConVector res_vector;
        
        res_vector = new Adj_ConVector(grm);
        
        return(res_vector);
    }
    
    /**Difference: Differenzmenge ermitteln*/
    public Adj_ConVector Difference(Adj_ConVector substract_vec)
    {
        Adj_ConVector res_vector;
        
        res_vector = new Adj_ConVector(grm);
        
        return(res_vector);
    }
    
    /**GetString: Umwandeln des Vectors in einen String*/
    public String GetString ()
    {        
        int i;
        String s="";
        Adj_ConVectorItem avi;
        
        if (isEmpty()==false)
        {
            for(i=1;i<=elementCount;i++)
            {
                avi = Get(i);

                if (s!="")
                    s=s+", ("+avi.GetId(avi.Get_Src_Node())+","+avi.GetId(avi.Get_Dest_Node())+")";
                else
                    s=s+"("+avi.GetId(avi.Get_Src_Node())+","+avi.GetId(avi.Get_Dest_Node())+")";
                    
                s=s+" "+avi.Get_Weight();
                
                if (avi.IsConnectionMarked())
                    s=s+" (m)";
            }
        }
        else
            s = "empty";
        
        return(s);
    }        
    
    /**Adj_ConWeight_SortAdapter: QuickSort-Adapter f�r Sortierung nach Gewichtung*/
    private class Adj_ConWeight_SortAdapter implements Uti_SortAdapter
    {
        /**SortAdapter-Implementierung f�r Sortierung nach Gewichtung*/
        public int compare(Object x1, Object x2)
        {
            if ((x1 instanceof Adj_ConVectorItem)
            &&  (x2 instanceof Adj_ConVectorItem))
            {
                double n1 = ((Adj_ConVectorItem)x1).Get_Weight();
                double n2 = ((Adj_ConVectorItem)x2).Get_Weight();

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

    /**Sort: Sortieren nach Kantengewichten*/
    public Adj_ConVector SortByWeight(boolean descending)
    {
        Adj_ConVector res_vector;
        Object value_vec [];
        Uti_QuickSorter sorter;
        int i;
        
        res_vector = new Adj_ConVector(grm);
        
        if(Size()!=0)
        {
            value_vec = new Object[Size()];
            
            //kopieren
            copyInto(value_vec);        
            
            sorter = new Uti_QuickSorter();
            
            //sortieren
            sorter.Sort(value_vec,new Adj_ConWeight_SortAdapter(),descending);
            
            //Vector wiederherstellen
            for(i=0;i<Size();i++)
                res_vector.addElement(value_vec[i]);    
        }
        
        return(res_vector);
    }
}    
    