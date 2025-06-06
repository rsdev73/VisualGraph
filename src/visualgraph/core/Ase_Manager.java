package visualgraph.core;
import java.io.*;
import java.util.*;

/**Sequenz-Management auf DS-Ebene*/
public class Ase_Manager extends java.lang.Object implements Serializable
{    
	private static final long serialVersionUID = -7115262506837099253L;

	/**Typ der Sequenz*/
    private int content_typ;
    
    /**Verweis auf Elemente*/
    private Vector elems;
    
    /**Serialize write*/
    private void writeObject(java.io.ObjectOutputStream out) throws IOException
    {
        out.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();        
    }
    
    /**Konstruktoren: typ -> Content-Typ der Sequenz*/
    public Ase_Manager ()
    {
        this(Pdt_Element.TYP_INTEGER);
    }
    
    public Ase_Manager (int typ)
    {
        if( (typ != Pdt_Element.TYP_INTEGER) && (typ != Pdt_Element.TYP_STRING) 
            && (typ != Pdt_Element.TYP_DOUBLE) )
        {    
            typ = Pdt_Element.TYP_INTEGER;
        }
        
        content_typ = typ;
        
        elems = new Vector();
    }
    
    /**Zugriffsmethoden*/
    /**Add: Integer-Element hinzuf�gen*/
    public void Add(int elem) 
    {
        Object obj;
        
        if(content_typ != Pdt_Element.TYP_INTEGER)
            return;
        
        obj = new Pdt_Element(Pdt_Element.TYP_INTEGER,Integer.valueOf(elem));
        
        elems.addElement(obj);
    }

    /**Add: String-Element hinzuf�gen*/
    public void Add(String elem) 
    {
        Object obj;
        
        if(content_typ != Pdt_Element.TYP_STRING)
            return;
        
        obj = new Pdt_Element(Pdt_Element.TYP_STRING,elem);
        
        elems.addElement(obj);
    }

    /**Add: Double-Element hinzuf�gen*/
    public void Add(double elem) 
    {
        Object obj;
        
        if(content_typ != Pdt_Element.TYP_DOUBLE)
            return;
        
        obj = new Pdt_Element(Pdt_Element.TYP_DOUBLE, Double.valueOf(elem));
        
        elems.addElement(obj);
    }
    
    /**Get: R�ckgabe Element der Sequenz, index ist 1 basiert*/
    public int GetInt(int index) 
    {
        return(((Pdt_Element)elems.elementAt(index-1)).GetInt());
    }

    /**Get: R�ckgabe Element der Sequenz, index ist 1 basiert*/
    public String GetString(int index) 
    {
        return(((Pdt_Element)elems.elementAt(index-1)).GetString());
    }

    /**Get: R�ckgabe Element der Sequenz, index ist 1 basiert*/
    public double GetDouble(int index) 
    {
        return(((Pdt_Element)elems.elementAt(index-1)).GetDouble());
    }

    /**Get: R�ckgabe Element der Sequenz, index ist 1 basiert*/
    public Pdt_Element Get(int index) 
    {
        return((Pdt_Element)elems.elementAt(index-1));
    }

    /**Size: R�ckgabe der Sequenzgr��e*/
    public int Size() 
    {
        return(elems.size());
    }

    /**Hilfsmethoden*/
    public String GetString()
    {
        int i;        
        String s="Content of sequence: ";

        switch(content_typ)
        {
            case Pdt_Element.TYP_INTEGER:                    
                s +="Integer\n";
                break;
                
            case Pdt_Element.TYP_DOUBLE:
                s += "Double\n";
                break;
                
            case Pdt_Element.TYP_STRING:                            
                s += "String\n";
                break;            
        }
        
        for(i=1;i<=elems.size();i++)
        {
            switch(content_typ)
            {
                case Pdt_Element.TYP_INTEGER:                    
                    s += "["+GetInt(i)+"] ";
                    break;
                    
                case Pdt_Element.TYP_DOUBLE:
                    s += "["+GetDouble(i)+"] ";
                    break;
                    
                case Pdt_Element.TYP_STRING:                
                    s += "["+GetString(i)+"] ";                
                    break;                
            }
        }
        
        s+="\n";
        
        return(s);        
    }
       
}