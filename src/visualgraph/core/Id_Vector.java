package visualgraph.core;
import java.util.*;

import visualgraph.swing.Frm_Frame;

/**Id_Vector: Ergebnis-Vektor f�r KnotenId-Mengen*/

public class Id_Vector extends Vector
{
    /**Verweis auf Frm_Frame, zu setzen wenn GUI verwendet wird*/
    private Frm_Frame frm;
    
    static final long serialVersionUID = -4762734315160201227L;
    
    /**Konstruktoren*/
    public Id_Vector()
    {
        this((Frm_Frame)(null));
    }

    /**Id-Vector aus Graph*/
    public Id_Vector(Frm_Frame frm)
    {
        this.frm = frm;
    }

    /**Copy-Konstruktor*/
    public Id_Vector(Id_Vector id_vec)
    {
        int i;
        
        this.frm = id_vec.frm;
        
        for(i=1;i<id_vec.Size();i++)
        {
            Add(id_vec.Get(i));
        }
    }

    /**Nummer hinzuf�gen*/    
    public void Add(int id) 
    {        
        super.addElement((Object) Integer.valueOf(id) );
    }
    
    /**Zugriff auf Nummernmenge, index 1 basiert*/
    public int Get(int index) 
    {
        return( ((Integer) super.elementAt(index-1)).intValue());
    }

    /**Nummer setzen, index 1 basiert*/    
    public void Set(int index, int id) 
    {
        super.setElementAt((Object) Integer.valueOf(id),index-1);
    }

    /**Gr��e der Nummernmenge*/    
    public int Size() 
    {
        return(super.size());
    }

    /**LinkToGUI: Verweis auf GUI setzen*/
    public void LinkToGUI (Frm_Frame frm)
    {
        this.frm = frm;
    }
 
    /**GetId: Identificator eines Elements zur�ckgeben*/
    public String GetId (int id)
    {        
        if(frm == null)
            return(""+Get(id));
        
        return(frm.GetId(Get(id)));
    }

    /**Union: Vereinigung zweier Mengen ermitteln*/
    public Id_Vector Union(Id_Vector union_vec)
    {
        Id_Vector res_vector;   

        res_vector = new Id_Vector(frm);
        
        return(res_vector);
    }
    
    /**Intersection: Schnittmenge ermitteln*/
    public Id_Vector Intersection(Id_Vector diff_vec)
    {
        Id_Vector res_vector;
        
        res_vector = new Id_Vector(frm);
        
        return(res_vector);
    }

    /**Difference: Differenzmenge ermitteln*/
    public Id_Vector Difference(Id_Vector substract)
    {
        int i,ii;
        Id_Vector res_vector;        
        int val,vgl_val;
        boolean found;
        
        res_vector = new Id_Vector(frm);

        if(Size() == 0)
            return(res_vector);
            
        if(substract == null)
            return (res_vector);

        for(i=1;i<=Size();i++)
        {
            vgl_val = Get(i);

            found = false;

            for(ii=1;ii<=substract.Size();ii++)
            {
                val = substract.Get(ii);                    
                
                if(val == vgl_val)
                {                    
                    found = true;
                    break;
                }
            }
            
            if(!found)
            {
                res_vector.Add(vgl_val);
            }
        }
        return (res_vector);        
    }

    /**Contains: Pr�ft ob Elements vorhanden*/
    public boolean Contains(int id) 
    {     
        int i,vgl_val;
        
        if(Size() == 0)
            return(false);
        
        for(i=1;i<=Size();i++)
        {
            vgl_val = Get(i);

            if(vgl_val == id)
                return(true);
        }
        return(false);        
    }

    /**Remove*/
    public void Remove(int id) 
    {     
        if(Size() == 0)
            return;
        
        if(Contains(id) == false)
            return;
            
        super.removeElement((Object) Integer.valueOf(id));            
    }
     
    public String GetString()
    {
        int i;
        String s="";
                
        if (isEmpty()==false)
        {
            for(i=1;i<=elementCount;i++)
            {
                if (s!="")
                    s=s+", "+GetId(i);
                else
                    s=s+GetId(i);
            }            
        }
        else
            s = "empty";
            
        return(s);    
    }
}
