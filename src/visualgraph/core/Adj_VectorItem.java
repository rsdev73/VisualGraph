package visualgraph.core;
import visualgraph.swing.Grf_Manager;

import java.io.*;

/**Adj_VectorItem: Klasse eines Adj_Vektor-Elements*/

public class Adj_VectorItem extends java.lang.Object implements Serializable
{
    /**Verweis auf Adj-Knoten*/
    private Adj_Node adn_node;   

    /**Verweis auf Grf_Manager, gesetzt wenn GUI verwendet wird*/
    private Grf_Manager grm;

    /**Markierung auf Listenebene*/
    private boolean marked; 
    
    static final long serialVersionUID = 1804794994196090628L;
    
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
    public Adj_VectorItem()
    {
        this(null);
    }
 
    public Adj_VectorItem(Adj_Node adn)
    {
        adn_node = adn;
        
        marked = false;
    }

    /**LinkToGUI: Verweis auf GUI setzen*/
    public void LinkToGUI (Grf_Manager grm)
    {
        this.grm = grm;
    }
            
    /**SetMarker: Kennzeichnet Item als markiert*/
    public void SetMarker ()
    {
        marked = true;
    }
    
    /**DeleteMarker: Kennzeichnet Item als nicht markiert*/
    public void DeleteMarker()
    {
        marked = false;
    }

    /**IsMarked: Ret: Itemmarkierung*/
    public boolean IsMarked()
    {
        return(marked);
    }
    
    /**Set_Node: Setzt Verweis auf Adj_Node*/
    public void SetNode(Adj_Node adn)
    {
        adn_node = adn;
    }

    /**Get_Node: Gibt Verweis auf Adj_Node zur�ck*/
    public Adj_Node GetNode()
    {
        return(adn_node);
    }

    /**GetNodeNr: Gibt Node Number zur�ck*/
    public int GetNodeNr()
    {
        return(adn_node.Get_Node_Number());
    }

    /**GetId: Identificator eines Knoten zur�ckgeben*/
    public String GetId ()
    {        
        if(grm == null)
            return(""+adn_node.Get_Node_Number());
        
        return(grm.GetId(adn_node.Get_Node_Number()));
    }

    /**SetNodeMarker: Kennzeichnet Node als markiert*/
    public void SetNodeMarker ()
    {
        adn_node.SetMarker();
        
        if(grm != null)
            grm.RepaintNodes();
    }
    
    /**DeleteNodeMarker: Kennzeichnet Node als nicht markiert*/
    public void DeleteNodeMarker()
    {
        adn_node.DeleteMarker();

        if(grm != null)
            grm.RepaintNodes();
    }

    /**IsNodeMarked: Ret: Nodemarkierung*/
    public boolean IsNodeMarked()
    {
        return(adn_node.IsMarked());
    }
}
