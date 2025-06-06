package visualgraph.core;
import java.io.*;

/**Atomar-Element Container-Klasse*/
public class Pdt_Element extends java.lang.Object implements Serializable
{
    /**lfd. Identificator*/
    private static int identificator = 0;
    
    /**Atomartypen*/
    public static final int TYP_INTEGER = 0;
    public static final int TYP_DOUBLE  = 1;
    public static final int TYP_STRING  = 2;

    /**ID*/
    private transient int element_id;

    /**Typ des Elements*/
    private int content_typ;

    /**Inhalt*/
    private Object value;

    /**Element-Markierung*/
    private transient boolean marked;

    static final long serialVersionUID = 4132266193038990090L;

    /**Serialize write*/
    private void writeObject(java.io.ObjectOutputStream out) throws IOException
    {
        out.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
    }

    /**Konstruktoren*/
    public Pdt_Element (int typ)
    {
        this(typ,null);
        
        Integer int_val;
        Double dbl_val;
        String str_val;
        Object obj=null;
        
        switch(typ)
        {
            case TYP_INTEGER:
                int_val = Integer.valueOf(0);
                obj = (Object) int_val;
                break;
                
            case TYP_DOUBLE:

                dbl_val = Double.valueOf(0);
                obj = (Object) dbl_val;
                break;
            
            case TYP_STRING:
            
                str_val = new String("");
                obj = (Object) str_val;
                break;                    
        }
        
        Set(obj);        
    }

    public Pdt_Element (int typ, Object value)
    {
        if( (typ != TYP_INTEGER) && (typ != TYP_STRING)
            && (typ != TYP_DOUBLE) )
        {
            typ = TYP_INTEGER;
        }

        content_typ = typ;

        this.value = value;
        
        //Identifaktion setzen
        identificator += 1;
        element_id = identificator;        
    }

    /**Get_Element_Number: Element-Identifikation zur�ckgeben*/
    public int Get_Element_Number()
    {
        return(element_id);
    }

    /**Set_Element_Number: Element-Identifikation setzen*/
    public void Set_Element_Number(int id)
    {
        element_id = id;
        
        if(identificator < id)
            identificator = id;
    }

    /**Get_Content_Typ: Element-Identifikation zur�ckgeben*/
    public int Get_Content_Typ()
    {
        return(content_typ);
    }

    /**Get: R�ckgabe Element-Inhalt*/
    public int GetInt()
    {
        if(content_typ != TYP_INTEGER)
            return(0);

        if(value == null)
            return(0);
            
        return(((Integer)value).intValue());
    }

    /**Get: R�ckgabe Element-Inhalt*/
    public double GetDouble()
    {
        if(content_typ != TYP_DOUBLE)
            return(0);

        if(value == null)
            return(0);

        return(((Double)value).doubleValue());
    }

    /**Get: R�ckgabe Element-Inhalt*/
    public String GetString()
    {
        if(content_typ != TYP_STRING)
            return("");

        if(value == null)
            return("");

        return(((String)value));
    }

    /**Get: R�ckgabe Element-Inhalts als Object*/
    public Object Get()
    {
        return(value);
    }

    /**Set: Setzen Element-Inhalt*/
    public void Set(Object value)
    {
        this.value = value;
    }

    /**Set: Setzen Element-Inhalt*/
    public void Set(int value)
    {
        if(content_typ != TYP_INTEGER)
            return;

        this.value = (Object) Integer.valueOf(value);
    }

    /**Set: Setzen Element-Inhalt*/
    public void Set(double value)
    {
        if(content_typ != TYP_DOUBLE)
            return;

        this.value = (Object) Double.valueOf(value);
    }

    /**Set: Setzen Element-Inhalt*/
    public void Set(String value)
    {
        if(content_typ != TYP_STRING)
            return;

        this.value = (Object) new String(value);
    }
    
    /**SetMarker: Kennzeichnet Node als markiert*/
    public void SetMarker ()
    {
        marked = true;
    }
    
    /**DeleteMarker: Kennzeichnet Node als nicht markiert*/
    public void DeleteMarker()
    {
        marked = false;
    }

    /**IsMarked:*/
    public boolean IsMarked()
    {
        return(marked);
    }    
    
    /**ReIdentify: erneute Identifizierung des Elements (n. Serialis.)*/
    public void ReIdentify ()
    {
        identificator += 1;
        element_id = identificator;               
    }   
    
    /**Hilfsmethode*/
    public String toString()
    {
        return(value.toString());
    }
}