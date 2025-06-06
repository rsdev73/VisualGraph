package visualgraph.core;
import java.io.*;

public class Adj_Connection extends java.lang.Object implements Serializable
{
    /**Connection-Identificator*/
    static private int connection_identificator = 0;

    /**Verbindungsarten*/
    public final static int TypDirected = 0;      
    public final static int TypNotDirected = 1;   

    /**Caption-Initialisierer*/
    private static final String caption_filler = "no action";
    
    /**Verbindungsart*/
    private int typ;

    /**Verbindungs-Id*/
    private transient int connection_id;
    
    /**Verkettung:*/
    private Adj_Connection succ;    /**N�chstes Connection-Objekt*/

    /**Knotenverweise*/
    private Adj_Node src_node;      /**Verweis auf Startknoten*/    
    private Adj_Node dest_node;     /**Verweis auf Endknoten*/
    
    /**Master-Kante*/
    private boolean master;
    
    /**Gewichtung*/
    private double weight;

    /**Beschriftung*/
    private String caption;
    
    /**Markierung*/
    private transient boolean marked;

    static final long serialVersionUID = 4406828013328279361L;        
    
    /**Serialisierung*/
    private void writeObject(java.io.ObjectOutputStream out) throws IOException
    {
        out.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();        
    }
    
    /**Konstruktoren*/
    public Adj_Connection()
    {
        /**Standard ConnectionTyp zuordnen*/
        this(TypDirected);        
    }

    public Adj_Connection(int direction)
    {
        if ((direction != TypDirected) && (direction != TypNotDirected))
        {;} /** Raise Exception*/
        
        typ = direction;

        connection_identificator += 1;
        connection_id = connection_identificator;        
        
        master = true;
        
        weight = 0;
        caption = caption_filler;
    }

    /**Copy-Konstruktor*/
    public Adj_Connection(Adj_Connection src)
    {
        typ = src.typ;

        connection_identificator += 1;
        connection_id = connection_identificator;
        
        master = false;
        
        weight = src.weight;
        caption = src.caption;
    }

    /**Zugriffsfunktionen Get:*/
    /**Get: typ*/
    public int Get_Typ()
    {
        return(typ);
    }

    /**Set_Master: Master-Kante setzen bei ungerichteten Verbindungen*/    
    public void Set_Master(boolean is_master)
    {
        master = is_master;
    }

    /**Get_Master: ist Verbindung Master-Kante ?*/    
    public boolean Is_Master()
    {
        return(master);
    }
   
    /**Get: src_node*/
    public Adj_Node Get_Src_Node()
    {
        return(src_node);
    }

    /**Get: dest_node*/
    public Adj_Node Get_Dest_Node()
    {
        return(dest_node);
    }

    /**Get: succ*/
    public Adj_Connection Get_Succ()
    {
        return(succ);
    }

    /**Get_Src_Node_Number: Knotennummer des Startknoten*/
    public int Get_Src_Node_Number()
    {
        return(src_node.Get_Node_Number());
    }

    /**Get_Dest_Node_Number: Knotennummer des Endknoten*/
    public int Get_Dest_Node_Number()
    {
        return(dest_node.Get_Node_Number());
    }
    
    /**Zugriffsfunktionen Set:*/
    /**Set: src_node*/
    public void Set_Src_Node(Adj_Node node)
    {
        src_node = node;
    }

    /**Set: dest_node*/
    public void Set_Dest_Node(Adj_Node node)
    {
        dest_node = node;
    }
 
    /**Set: succ*/
    public void Set_Succ(Adj_Connection conn)
    {
        succ = conn;
    }

    /**Set_Weight: Gewichtung setzen*/
    public void Set_Weight(int value)
    {
        weight = (double)value;
    }

    public void Set_Weight(double value)
    {
        weight = value;
    }

    /**Get_Weight: Gewichtung zur�ckliefern*/
    public double Get_Weight()
    {
        return(weight);
    }

    /**Set_Caption: Verbindungstext setzen*/
    public void Set_Caption(String text)
    {
        if(text == null)
            return;
            
        if(text.equals(""))
        {
            caption = caption_filler;
            return;
        }
        
        caption = text;
    }

    /**Get_Caption: Verbindungstext zur�ckgeben*/
    public String Get_Caption()
    {
        return(caption);
    }
    
    /**ReIdentify: erneute Identifizierung der Connection (n. Serialis.)*/
    public void ReIdentify ()
    {
        connection_identificator += 1;
        connection_id = connection_identificator;
    }   
    
    /**SetMarker: Kennzeichnet Connection als markiert*/
    public void SetMarker ()
    {
        marked = true;
    }
    
    /**DeleteMarker: Kennzeichnet Connection als nicht markiert*/
    public void DeleteMarker()
    {
        marked = false;
    }

    /**IsMarked:*/
    public boolean IsMarked()
    {
        return(marked);
    }        
}
