package visualgraph.core;
import visualgraph.swing.Grf_Manager;

import java.io.*;

/**Adj_ConVectorItem: Klasse eines Adj_CocVektor-Elements*/

public class Adj_ConVectorItem extends java.lang.Object implements Serializable
{
    /**Verweis auf Adj-Connection*/
    private Adj_Connection adc_con;   

    /**Verweis auf Grf_Manager, gesetzt wenn GUI verwendet wird*/
    private Grf_Manager grm;

    /**Markierung auf Listenebene*/
    private boolean marked; 

    static final long serialVersionUID = -8090334176458212735L;
    
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
    public Adj_ConVectorItem(Adj_Connection adc)
    {
        adc_con = adc;
        
        marked = false;
    }

    /**Copy-Konstruktor*/
    public Adj_ConVectorItem(Adj_ConVectorItem aci)
    {        
        adc_con = aci.adc_con;
        grm = aci.grm;
        
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
    
    /**SetConnection: Setzt Verweis auf Adj_Connection*/
    public void SetConnection(Adj_Connection adc)
    {
        adc_con = adc;
    }

    /**GetConnection: Gibt Verweis auf Adj_Connection zur�ck*/
    public Adj_Connection GetConnection()
    {
        return(adc_con);
    }

    /**SetConnectionMarker: Kennzeichnet Connection als markiert*/
    public void SetConnectionMarker ()
    {
        adc_con.SetMarker();
        
        if(grm != null)
            grm.RepaintConnections();
    }
    
    /**DeleteConnectionMarker: Kennzeichnet Connection als nicht markiert*/
    public void DeleteConnectionMarker()
    {
        adc_con.DeleteMarker();

        if(grm != null)
            grm.RepaintConnections();
    }

    /**IsConnectionMarked: Ret: Connectionmarkierung*/
    public boolean IsConnectionMarked()
    {
        return(adc_con.IsMarked());
    }
    
    /**Get_Weight: Gewichtung zur�ckliefern*/
    public double Get_Weight()
    {
        return(adc_con.Get_Weight());
    }

    /**Set_Weight: Gewichtung setzen*/
    public void Set_Weight(int weight)
    {
        Set_Weight((double)weight);
    }
       
    public void Set_Weight(double weight)
    {
        if(grm != null)
        {
            grm.Set_Weight(adc_con.Get_Src_Node_Number(),
                        adc_con.Get_Dest_Node_Number(),weight);
            return;            
        }
        
        adc_con.Set_Weight(weight);
    }

    /**Set_Caption: Verbindungstext setzen*/
    public void Set_Caption(String text)
    {
        adc_con.Set_Caption(text);
    }

    /**Get_Caption: Verbindungstext zur�ckgeben*/
    public String Get_Caption()
    {
        return(adc_con.Get_Caption());
    }    
    
    /**Get: src_node*/
    public Adj_Node Get_Src_Node()
    {
        return(adc_con.Get_Src_Node());
    }

    /**Get: dest_node*/
    public Adj_Node Get_Dest_Node()
    {
        return(adc_con.Get_Dest_Node());
    }

    /**Get_Src_Node_Number: Knotennummer des Startknoten*/
    public int Get_Src_Node_Number()
    {
        return(adc_con.Get_Src_Node_Number());
    }

    /**Get_Dest_Node_Number: Knotennummer des Endknoten*/
    public int Get_Dest_Node_Number()
    {
        return(adc_con.Get_Dest_Node_Number());
    }    

    /**GetId: Identificator eines Knoten zur�ckgeben*/
    public String GetId (Adj_Node node)
    {        
        if(grm == null)
            return(""+node.Get_Node_Number());
        
        return(grm.GetId(node.Get_Node_Number()));
    }
}
