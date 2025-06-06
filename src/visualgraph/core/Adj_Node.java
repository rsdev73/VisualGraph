package visualgraph.core;
import java.io.*;

public class Adj_Node extends java.lang.Object implements Serializable
{
    /**Node-Identificator*/
    private static int node_identificator = 0;
        
    /**Objektvariablen und Methoden:*/
    /**Knoten-ID*/
    private transient int node_id;
    
    /**Knoten-Nummer*/
    private int node_number;

    /**Knoten-Bezeichnung*/
    private String node_caption;

    /**Knoten-Gewicht*/
    private double node_weight;

    /**Verweis auf n�chsten Adjazenknoten in gleicher Ebene*/
    private Adj_Node next;    
    
    /**Verweis auf n�chste Verbindung in n�chster Ebene und dessen Knoten*/
    private Adj_Connection succ;

    /**Node-Markierung*/
    private transient boolean marked;
    
    static final long serialVersionUID = 2311536704086545767L;    
    
    /**Hilfsmethoden*/
    public String GetString ()
    {
        String s="Adj_Node";
        s = s+"("+node_id+")";
        
        return s;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException
    {
        out.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();        
    }
    
    /**Konstruktor*/
    public Adj_Node()  
    {        
        node_identificator += 1;
        node_id = node_identificator;
                
        node_caption = "";
    }

    /**Zugriffsmethoden*/
    
    /**Set: next*/
    public void Set_Next(Adj_Node adn)
    {
        next = adn;
    }
    
    /**Set: succ*/
    public void Set_Succ(Adj_Connection conn)
    {
        succ = conn;
    }

    /**Get: next*/
    public Adj_Node Get_Next()
    {
        return(next);
    }

    /**Get: succ*/
    public Adj_Connection Get_Succ()
    {
        return(succ);
    }

    /**Get: node_number*/
    public int Get_Node_Number()
    {
        return(node_number);
    }

    /**Set: node_number*/
    public void Set_Node_Number(int number)
    {
        node_number = number;
        
        if(node_identificator < number)
            node_identificator = number;
    }

    /**Get_Node_Alpha_Number: gibt alphabetische Nummer zur�ck*/
    public String Get_Node_Alpha_Number()
    {       
        int nr,prefix_nr,off;
        char cnr[] = new char[2];
        
        nr = node_number;
        nr --;
        
        prefix_nr = (int) nr/26;

        off = 0;
        
        if(prefix_nr > 0)
        {
            cnr[off] = (char) ('A'+(prefix_nr-1));
            off ++;
  
            nr = nr%26;
        }

        cnr[off] = (char) ('A'+nr);
        
        return(new String(cnr,0,off+1));
    }

    /**Set_Node_Caption: Node Bezeichnung setzen*/
    public void Set_Node_Caption (String caption)
    {
        node_caption = caption;
    }

    /**Get_Node_Caption: Node Bezeichnung setzen*/
    public String Get_Node_Caption ()
    {
        return(node_caption);
    }

    /**Set_Node_Weight: Knotengewicht setzen*/
    public void Set_Node_Weight (double val)
    {
        node_weight = val;
    }

    /**Get_Node_Weight: Kntoengewicht zur�ckgeben*/
    public double Get_Node_Weight ()
    {
        return(node_weight);
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
    
    /**ReIdentify: erneute Identifizierung des Knoten (n. Serialis.)*/
    public void ReIdentify ()
    {
        node_identificator += 1;
        node_id = node_identificator;        
    }
}

