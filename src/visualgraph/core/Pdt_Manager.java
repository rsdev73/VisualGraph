package visualgraph.core;
import java.io.*;
import java.util.*;

import visualgraph.util.Uti_DoubleSortAdapter;
import visualgraph.util.Uti_IntegerSortAdapter;
import visualgraph.util.Uti_SortAdapter;
import visualgraph.util.Uti_StringSortAdapter;

/**Mengen-Container f�r Pdt_Elements*/
public class Pdt_Manager extends java.lang.Object implements Serializable
{
    /**Element-Numeration*/
    private int element_number_circle = 0;

    /**Verweis auf Elemente*/
    private Vector elems;
    
    static final long serialVersionUID = -8330351481806874901L;
    
    /**Serialize write*/
    private void writeObject(java.io.ObjectOutputStream out) throws IOException
    {
        out.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
    }

    /**Konstruktoren:*/
    public Pdt_Manager ()
    {
        //Elementvektor bereitstellen
        elems = new Vector();
    }

    /**Copy-Konstruktor*/
    public Pdt_Manager(Pdt_Manager src)
    {
        int i;
        Pdt_Element pdt;
        
        elems = new Vector();
        
        for(i=0;i<src.elems.size();i++)
        {
            pdt = (Pdt_Element) src.elems.elementAt(i);    
            
            CopyElement(pdt);
        }
    }
    
    /**Zugriffsmethoden*/
    /**Add: Integer-Element hinzuf�gen*/
    public int Add(int value)
    {
        Pdt_Element pdt;

        pdt = new Pdt_Element(Pdt_Element.TYP_INTEGER,Integer.valueOf(value));

        return(Add(pdt));
    }

    /**Add: Double-Element hinzuf�gen*/
    public int Add(double value)
    {
        Pdt_Element pdt;

        pdt = new Pdt_Element(Pdt_Element.TYP_DOUBLE,Double.valueOf(value));

        return(Add(pdt));
    }

    /**Add: String-Element hinzuf�gen*/
    public int Add(String value)
    {
        Pdt_Element pdt;

        pdt = new Pdt_Element(Pdt_Element.TYP_STRING,new String(value));

        return(Add(pdt));
    }

    /**Add: Pdt-Element hinzuf�gen*/
    public int Add(Pdt_Element elem)
    {
        element_number_circle = element_number_circle+1;
        
        elem.Set_Element_Number(element_number_circle);

        elems.addElement((Object)elem);

        return(element_number_circle);
    }

    /**Insert: Integer Element vor vorhandenes Elment einf�gen*/
    public int Insert(int element_number, int value)
    {
        Pdt_Element pdt;

        pdt = new Pdt_Element(Pdt_Element.TYP_INTEGER,Integer.valueOf(value));
        
        return(Insert(element_number,pdt));                    
    }

    /**Insert: Double Element vor vorhandenes Elment einf�gen*/
    public int Insert(int element_number, double value)
    {
        Pdt_Element pdt;

        pdt = new Pdt_Element(Pdt_Element.TYP_DOUBLE,Double.valueOf(value));
        
        return(Insert(element_number,pdt));                    
    }

    /**Insert: String Element vor vorhandenes Elment einf�gen*/
    public int Insert(int element_number, String value)
    {
        Pdt_Element pdt;

        pdt = new Pdt_Element(Pdt_Element.TYP_STRING,new String(value));
        
        return(Insert(element_number,pdt));                    
    }
    
    /**Insert: Pdt-Element vor vorhandens Element einf�gen*/
    public int Insert(int element_number, Pdt_Element elem)
    {
        Pdt_Element pdt;
        int i,index;
        
        //index des vorhandenen Elements ermitteln
        pdt = LookupIdentElement(element_number);
        if(pdt == null)
            return(0);
            
        index = elems.indexOf(pdt);            
        
        //und vor vorhandes Element einf�gen
        elems.insertElementAt((Object)elem, index);        

        /**Elemente neu nummerieren*/       
        element_number_circle = 0;         
        for(i=0;i<elems.size();i++)
        {
            pdt = (Pdt_Element) elems.elementAt(i);
            
            element_number_circle ++;
                
            pdt.Set_Element_Number(element_number_circle);
        }

        return(elem.Get_Element_Number());
    }

    /**Vertauschen zweier Elemente*/
    public void Swap(int first_elem_nr, int second_elem_nr)
    {
        Pdt_Element first,second;
        Object temp;
        int temp_number,f_index,s_index;
        
        first = LookupIdentElement(first_elem_nr);
        if(first == null)
            return;

        f_index = elems.indexOf(first);
        
        second = LookupIdentElement(second_elem_nr);
        if(second == null)
            return;

        s_index = elems.indexOf(second);
        
        //und vertauschen...
        elems.setElementAt(second,f_index);
        elems.setElementAt(first,s_index);        

        /**Elementnummern vertauchen*/
        second.Set_Element_Number(first_elem_nr);
        first.Set_Element_Number(second_elem_nr);
    }

    /**CopyElement: Kopiert gegebenes Element*/
    public int CopyElement (Pdt_Element src)
    {   
        Pdt_Element pdt;        
        int id=-1;

        /**Inhalt kopieren*/
        switch(src.Get_Content_Typ())
        {
            case Pdt_Element.TYP_INTEGER:            
                id = Add(src.GetInt());
                break; 
                
            case Pdt_Element.TYP_DOUBLE:            
                id = Add(src.GetDouble());            
                break;            
                
            case Pdt_Element.TYP_STRING:            
                id = Add(src.GetString());            
                break;            
        }        
        
        pdt = LookupIdentElement(id);
        if(pdt != null)
        {
            pdt.Set_Element_Number(src.Get_Element_Number());

            if(element_number_circle < src.Get_Element_Number())
                element_number_circle = src.Get_Element_Number();
        }    
        
        return(pdt.Get_Element_Number());
    }
    
    /**LookupIdentElement: Element-Zugriff �ber ID*/
    public Pdt_Element LookupIdentElement (int id)
    {
        int i;
        Pdt_Element pdt;
        
        for(i=0;i<elems.size();i++)
        {
            pdt = (Pdt_Element) elems.elementAt(i);
            
            if(pdt.Get_Element_Number() == id)
                return(pdt);
        }        
        return(null);
    }

    /**Get: R�ckgabe Element der Menge*/
    public Object Get(int id)
    {
        return(LookupIdentElement(id).Get());
    }

    /**Get_Element: R�ckgabe des Pdt_Elements nach Nummer*/
    public Pdt_Element Get_Element(int id)
    {
        return(LookupIdentElement(id));
    }
    
    /**Get: R�ckgabe Element der Menge*/
    public int GetInt(int id)
    {
        return(LookupIdentElement(id).GetInt());
    }

    /**Get: R�ckgabe Element der Menge*/
    public String GetString(int id)
    {
        return(LookupIdentElement(id).GetString());
    }

    /**Get: R�ckgabe Element der Menge*/
    public double GetDouble(int id)
    {
        return(LookupIdentElement(id).GetDouble());
    }

    /**Set: Setzen Element-Inhalt*/
    public void Set(int id, int value)
    {
        LookupIdentElement(id).Set(value);
    }

    /**Set: Setzen Element-Inhalt*/
    public void Set(int id, double value)
    {
        LookupIdentElement(id).Set(value);
    }

    /**Set: Setzen Element-Inhalt*/
    public void Set(int id, String value)
    {
        LookupIdentElement(id).Set(value);
    }

    /**Set: Setzen Element-Inhalt*/
    public void Set(int id, Object value)
    {
        int i;
        Pdt_Element pdt;
        
        for(i=0;i<elems.size();i++)
        {
            pdt = (Pdt_Element) elems.elementAt(i);
            
            if(pdt.Get_Element_Number() == id)
            {
                pdt.Set(value);
                break;
            }    
        }        
    }

    /**Size: R�ckgabe der Mengengr��e*/
    public int Size()
    {
        return(elems.size());
    }

    /**DeleteElement: L�schen eines Elements �ber Id*/
    public void DeleteElement (int id)
    {
        Pdt_Element pdt;
        int i;
        
        pdt = LookupIdentElement(id);
        if(pdt == null)
            return;
            
        //Element entfernen
        elems.removeElement((Object)pdt);
        
        /**Elemente neu nummerieren*/       
        element_number_circle = 0;         
        for(i=0;i<elems.size();i++)
        {
            pdt = (Pdt_Element) elems.elementAt(i);
            
            element_number_circle ++;
                
            pdt.Set_Element_Number(element_number_circle);
        }
    }

    /**DeleteAllElements: Alle Elemente der Menge l�schen*/
    public void DeleteAllElements ()
    {
        //Elemente entfernen
        elems = null;        
        elems = new Vector();
        
        /**Nummerkreis setzen*/       
        element_number_circle = 0;         
    }
 
    /**ElementNrVector: Menge aller Element-Nummern*/
    public Id_Vector ElementNrVector ()
    {
        Id_Vector res_vector;
        int i;
        Pdt_Element pdt;
        
        res_vector = new Id_Vector();                            

        for(i=0;i<elems.size();i++)
        {
            pdt = (Pdt_Element) elems.elementAt(i);
            
            res_vector.Add(pdt.Get_Element_Number());
        }

        return(res_vector);
    }    
 
    /**ReIdentify: erneute Identifizierung der Elemente, nach Ser.*/
    public void ReIdentify ()
    {
        int i;
        Pdt_Element pdt;
        
        for(i=0;i<elems.size();i++)
        {
            pdt = (Pdt_Element) elems.elementAt(i);
            
            pdt.ReIdentify();
        }        
    }   
 
    /**Zwei Elemente inhaltlich vergleichen*/
    public int Compare (int first_elem_nr, int second_elem_nr)
    {
        Pdt_Element first,second;
        Uti_SortAdapter comp_tool=null;
        
        first = LookupIdentElement(first_elem_nr);
        if(first == null)
            return(Uti_SortAdapter.COMP_EQUAL);

        second = LookupIdentElement(second_elem_nr);
        if(second == null)
            return(Uti_SortAdapter.COMP_EQUAL);

        //gleiche Typen =
        if(first.Get_Content_Typ() != second.Get_Content_Typ())
        {
            //nein - kein Vergleich m�glich
            return(Uti_SortAdapter.COMP_EQUAL);
        }
        
        //compare-Tool bereitstellen
        switch(first.Get_Content_Typ())
        {
            case Pdt_Element.TYP_INTEGER:
                comp_tool = new Uti_IntegerSortAdapter();
                break;
                
            case Pdt_Element.TYP_DOUBLE:            
                comp_tool = new Uti_DoubleSortAdapter();            
                break;
                
            case Pdt_Element.TYP_STRING:            
                comp_tool = new Uti_StringSortAdapter();                        
                break;
        }
        
        //und Vergleich durchf�hren...
        return(comp_tool.compare(first.Get(),second.Get()));
    }
    
    /**Hilfsmethoden*/
    public String toString()
    {
        return(GetString());
    }
    
    public String GetString()
    {
        int i;
        String s="";
        Pdt_Element pdt;
        
        if(elems.size() == 0)
            s+="empty";

        for(i=0;i<elems.size();i++)
        {
            pdt = (Pdt_Element) elems.elementAt(i);
            
            switch(pdt.Get_Content_Typ())
            {
                case Pdt_Element.TYP_INTEGER:

                    if(s.equals("") == false)
                        s += ", ["+pdt.GetInt()+"]";
                    else
                        s += "["+pdt.GetInt()+"]";                        
                    break;

                case Pdt_Element.TYP_DOUBLE:

                    if(s.equals("") == false)
                        s += ", ["+pdt.GetDouble()+"]";
                    else
                        s += "["+pdt.GetDouble()+"]";
                    break;

                case Pdt_Element.TYP_STRING:
                
                    if(s.equals("") == false)
                        s += ", ["+pdt.GetString()+"]";
                    else
                        s += "["+pdt.GetString()+"]";
                    break;
            }
        }
        return(s);
    }
}