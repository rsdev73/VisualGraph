package visualgraph.core;
import java.io.*;
import java.util.*;

/**Adjazenzlisten Management - Node Container Klasse*/
public class Adj_Manager extends Object implements Serializable 
{
    /**Node-Numeration*/
    private int node_number_circle = 0;
    
    /**Verbindungsart*/
    private int connection_typ;

    /**Peer*/
    private Adj_Node adn_first;      /**Graphenstruktur erster Knoten*/

    /**Akt.-Node-Zugriff*/
    private transient Adj_Node adn_act_node = null;

    /**Akt.-Connection-Zugriff*/
    private transient Adj_Connection adc_act_con = null;

    static final long serialVersionUID = 8071497129870872423L;
    
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
    public Adj_Manager ()
    {
        this(Adj_Connection.TypDirected);
    }
    
    public Adj_Manager(int con_typ)
    {
        /**Connection-Typ sicherstellen*/
        if( (con_typ != Adj_Connection.TypDirected) && (con_typ != Adj_Connection.TypNotDirected) )
            con_typ = Adj_Connection.TypDirected;            
        
        connection_typ = con_typ;    
    }

    //Copy-Konstruktoren
    public Adj_Manager(Adj_Manager src)
    {
        this(src,true);
    }    
    
    public Adj_Manager(Adj_Manager src, boolean connected)
    {        
        Adj_Connection new_adc;
        
        connection_typ = src.connection_typ;
        
        //Knoten kopieren
        if(src.MoveFirstNode())
        {
            do
            {
                CopyNode (src.adn_act_node);                
                
            } while(src.MoveNextNode());
        }
        
        //Kanten kopieren
        if(connected)
        {
            if(src.MoveFirstNode())
            {
                do
                {
                    if(src.MoveFirstConn())
                    {                        
                        do
                        {
                            if(src.adc_act_con.Is_Master())
                            {
                                ConnectNode(src.adc_act_con.Get_Src_Node_Number(),
                                            src.adc_act_con.Get_Dest_Node_Number(),
                                            src.adc_act_con.Get_Weight());
                            }
                            
                        } while(src.MoveNextConn());
                    }
                } while(src.MoveNextNode());
            }
        }    
    }
    
    /**Zugriffsmethoden*/
    /**amMoveLastNode: Letzter Knoten auf Ebene 0 ansteuern*/
    private boolean MoveLastNode ()
    {        
        if (adn_first==null)
        {
            adn_act_node = null;
            return(false); /**alt. Throw Exception*/
        }
        
        adn_act_node = adn_first;
        
        while(adn_act_node.Get_Next() != null)
        {
            adn_act_node = adn_act_node.Get_Next();            
        } 
        return(true);
    }

    /**amMoveLastConnDesc: Letzter Verbindung auf Nachfolger-Ebene ansteuern*/
    private boolean MoveLastConn ()
    {        
        if (adn_first==null)
        {
            adn_act_node = null;
            return (false); /**alt. Throw Exception*/
        }

        if (adn_act_node == null)
            return(false);
        
        if (adn_act_node.Get_Succ() != null)
            adc_act_con = adn_act_node.Get_Succ();
        else
        {
            adc_act_con = null;
            return(true);
        }
        
        while(adc_act_con.Get_Succ() != null)
        {
            adc_act_con = adc_act_con.Get_Succ();            
        }         
        return(true);
    }

    /**MoveFirstNode: Erster Knoten auf Ebene 0 ansteuern*/
    private boolean MoveFirstNode ()
    {        
        if (adn_first==null)
            return(false);
        
        adn_act_node = adn_first;

        return (true);
    }

    /**MoveFirstConn: Erste Verbindung auf Nachfolger-Ebene ansteuern*/
    private boolean MoveFirstConn ()
    {        
        if (adn_first==null)
            return(false);
        
        if(adn_act_node==null)
            return(false);
        
        adc_act_con = adn_act_node.Get_Succ();

        if (adc_act_con == null)
            return(false);
                  
        return (true);
    }

    /**MoveNextNode: N�chster Knoten auf Ebene 0 ansteuern*/
    private boolean MoveNextNode()
    {
        if (adn_first == null)
          return(false);
          
        if (adn_act_node == null)
        {
            adn_act_node = adn_first;
            return(true);
        }

        if (adn_act_node.Get_Next() == null)
            return(false);
            
        adn_act_node = adn_act_node.Get_Next();

        return(true);
    }
    
    /**MoveNextConnDesc: N�chste Verbindung auf Nachfolger-Ebene ansteuern*/
    private boolean MoveNextConn()
    {
        if (adn_first == null)
          return(false);
          
        if (adn_act_node == null)
            return(false);

        if(adc_act_con == null)
        {        
            if (adn_act_node.Get_Succ() != null)
            {
                adc_act_con = adn_act_node.Get_Succ();
                return(true);
            }
            else
            {
                adc_act_con = null;
                return(false);
            }
        }
        
        adc_act_con = adc_act_con.Get_Succ();

        if (adc_act_con == null)
            return(false);
            
        return(true);
    }

    /**LookupIdentNode: Knotensuche nach NodeNumber*/
    public Adj_Node LookupIdentNode(int node_number)
    {
        boolean found;

        if (!MoveFirstNode())
            return(null);

        found = false;
        do
        {
            if (adn_act_node.Get_Node_Number() == node_number)
            {
                found = true;
                break;
            }

        } while(MoveNextNode());

        if(!found)
            return(null);
        else
            return(adn_act_node);
    }

    /**ConnectionPossible: pr�fen ob neue Verbindung m�glich*/
    public boolean ConnectionPossible(int i, int j)
    {
        boolean possible;
        Adj_Node src,dest;
        
        if(i==j)
            return(false);
        
        possible = true;

        if((src = LookupIdentNode(i))==null)
            return(false);

        if ((dest = LookupIdentNode(j))==null)
            return(false);
        
        /**Verbindung bereits vorhanden ?*/
        if (MoveFirstNode())
        {
            do
            {
                if(adn_act_node == src)
                {
                    if(MoveFirstConn())
                    {
                        do
                        {
                            if(adc_act_con.Get_Dest_Node() == dest)
                            {
                                possible = false;
                                break;
                            }                            
                        } while(MoveNextConn());
                    } 
                }
                
            } while(MoveNextNode());
        }        
        
        return(possible);
    }

    /**Set_Weight: Neusetzen der Gewichtung einer Verbindung*/
    public void Set_Weight(int i, int j, int weight)
    {
        Set_Weight(i,j,(double)weight);    
    }
    
    public void Set_Weight(int i, int j, double weight)
    {
        Adj_Node src,dest;
        Adj_Connection con_ij,con_ji;
        
        if(i==j)
            return;
        
        if((src = LookupIdentNode(i))==null)
            return;

        if ((dest = LookupIdentNode(j))==null)
            return;
        
        con_ij = null;
        con_ji = null;
        
        /**Connection ermitteln*/
        switch(connection_typ)
        {
            case Adj_Connection.TypDirected:
            
                /**con_ij ermitteln*/
                if (MoveFirstNode())
                { 
                    do 
                    {
                        if(adn_act_node == src)
                        {
                            if(MoveFirstConn())
                            { 
                                do 
                                {
                                    if(adc_act_con.Get_Dest_Node() == dest)
                                    {
                                        con_ij = adc_act_con;
                                        break;
                                    }                            
                                } while(MoveNextConn());
                            } 
                            break;
                        }
                        
                    } while(MoveNextNode());
                }        
                break;
                
            case Adj_Connection.TypNotDirected:                                            

                /**con_ij ermitteln*/
                if (MoveFirstNode())
                {
                    do
                    {
                        if(adn_act_node == src)
                        {
                            if(MoveFirstConn())
                            {
                                do
                                {
                                    if(adc_act_con.Get_Dest_Node() == dest)
                                    {
                                        con_ij = adc_act_con;
                                        break;
                                    }                            
                                } while(MoveNextConn());
                            } 
                            break;
                        }
                        
                    } while(MoveNextNode());
                }        
 
                /**con_ji ermitteln*/
                if (MoveFirstNode())
                {
                    do
                    {
                        if(adn_act_node == dest)
                        {
                            if(MoveFirstConn())
                            {
                                do
                                {
                                    if(adc_act_con.Get_Dest_Node() == src)
                                    {
                                        con_ji = adc_act_con;
                                        break;
                                    }                            
                                } while(MoveNextConn());
                            } 
                            break;
                        }
                        
                    } while(MoveNextNode());
                }        
                break;
        }
        
        if(con_ij != null)
            con_ij.Set_Weight(weight);

        if(con_ji != null)
            con_ji.Set_Weight(weight);        
    }

    /**Get_Weight: R�ckgabe der Gewichtung einer Verbindung, nach Knotennr.*/
    public double Get_Weight(int i, int j)
    {
        Adj_Node src,dest;
        Adj_Connection con_ij;
        
        if(i==j)
            return(Double.POSITIVE_INFINITY);
                
        if((src = LookupIdentNode(i))==null)
            return(Double.POSITIVE_INFINITY);

        if ((dest = LookupIdentNode(j))==null)
            return(Double.POSITIVE_INFINITY);
        
        con_ij = null;
        
        /**con_ij ermitteln*/
        if (MoveFirstNode())
        { 
            do 
            {
                if(adn_act_node == src)
                {
                    if(MoveFirstConn())
                    { 
                        do 
                        {
                            if(adc_act_con.Get_Dest_Node() == dest)
                            {
                                con_ij = adc_act_con;
                                break;
                            }                            
                        } while(MoveNextConn());
                    } 
                    break;
                }
                
            } while(MoveNextNode());
        }        

        /**Keine Verbindung vorhanden 0 zur�ckgeben*/
        if(con_ij == null)
            return(Double.POSITIVE_INFINITY);
            
        return(con_ij.Get_Weight());
    }
    
    /**AddNode: Neuer Knoten auf Ebene 0 anf�gen , Ret: NodeNumber*/
    public int AddNode ()
    {
        Adj_Node adn;
        
        adn = new Adj_Node();
     
        node_number_circle = node_number_circle+1;
        
        adn.Set_Node_Number(node_number_circle);
        
        /**Liste erste Ebene f�llen*/
        if (adn_first == null)
            adn_first = adn;
        else
        {
            MoveLastNode();
            adn_act_node.Set_Next(adn);           
        }
               
        return(adn.Get_Node_Number());
    }

    /**CopyNode: Kopiert Knoten*/
    public int CopyNode (Adj_Node src)
    {
        Adj_Node adn;
        
        adn = new Adj_Node();
     
        if(node_number_circle < src.Get_Node_Number())
            node_number_circle = src.Get_Node_Number();
        
        adn.Set_Node_Number(src.Get_Node_Number());
        adn.Set_Node_Caption(src.Get_Node_Caption());
        adn.Set_Node_Weight(src.Get_Node_Weight());
        
        /**Liste erste Ebene f�llen*/
        if (adn_first == null)
            adn_first = adn;
        else
        {
            MoveLastNode();
            adn_act_node.Set_Next(adn);           
        }
               
        return(adn.Get_Node_Number());
    }

    /**DeleteAllNodes: Alle Knoten u. Verbindungen l�schen*/
    public void DeleteAllNodes ()
    {    
        Adj_Vector adv;
        Adj_VectorItem avi;
        Adj_Node adn;
        int i;

        node_number_circle = 0;
        
        /**Alle Nodes und Connections freigeben*/
        if(adn_first==null)
            return;

        adv = NodeVector ();
        for(i=adv.Size();i>=1;i--)
        {
            avi = adv.Get(i);
            adn = avi.GetNode();
            
            /**Verkettung aufheben*/
            adn.Set_Next(null);
        }
        
        adn_first = null;
        adn_act_node = null;
        adc_act_con = null;        
    }

    /**DeleteAllConnections: Alle Verbindungen l�schen*/
    public void DeleteAllConnections ()
    {    
        Adj_Vector adv;
        Adj_VectorItem avi;
        Adj_Node adn;
        int i;

        if(adn_first==null)
            return;
            
        /**Alle Connections freigeben        */
        adv = NodeVector ();
        for(i=adv.Size();i>=1;i--)
        {
            avi = adv.Get(i);
            adn = avi.GetNode();
            
            /**Verkettung aufheben*/
            adn.Set_Succ(null);
        }
                
        MoveFirstNode();
        
        adc_act_con = null;
    }

    /**DeleteNode: Knoten aus Adjazenzliste entfernen*/
    public void DeleteNode (int node_number)
    {
        Adj_Node adn,next_node,prev_node;
        Adj_Connection next_conn,prev_conn;
        
        adn = LookupIdentNode(node_number);
        if(adn == null)
            return;
        
        adn_act_node = adn;
        
        /**alle vom Knoten weggehende Verbindungen l�schen*/
        if(MoveFirstConn ())
        {
            /**erste Connection aush�ngen*/
            adn.Set_Succ(null);
            
            do
            {              
                next_conn = adc_act_con.Get_Succ();
                  
                /**Verweise der Connection aush�ngen  */
                adc_act_con.Set_Src_Node(null);
                adc_act_con.Set_Dest_Node(null);
                
                adc_act_con.Set_Succ(null);
                
                adc_act_con = next_conn;
                
            } while (MoveNextConn());
        }
                      
        /**Vorg�nger-Knoten ermitteln    */
        
        prev_node = null;
        
        if(adn_first != adn)
        {
            if(MoveFirstNode())
            {
                do
                {
                    if(adn_act_node.Get_Next() == adn)
                    {
                        prev_node = adn_act_node;
                        break;
                    }
                    
                } while (MoveNextNode());
            }
        }
        
        /**Knoten aus erster Ebene entfernen*/
        next_node = adn.Get_Next();
        
        /**Verweise des Knoten l�schen*/
        adn.Set_Succ(null);
        adn.Set_Next(null);
        
        /**Liste neu verketten*/
        if(prev_node != null)
            prev_node.Set_Next(next_node);
        else
            adn_first = next_node;

        /**in Verweisen nach Knoten suchen u. Verweise l�schen*/
        if(MoveFirstNode())
        {
            do
            {
                prev_conn = null;
                
                if(MoveFirstConn ())
                {
                    do
                    {              
                        if(adc_act_con.Get_Dest_Node() == adn)
                        {
                            next_conn = adc_act_con.Get_Succ();
                            
                            /**erste Verbindung im Knoten ?*/
                            if(prev_conn == null)
                                adn_act_node.Set_Succ(next_conn);
                            else
                                prev_conn.Set_Succ(next_conn);                                

                            /**Verweise der Connection aush�ngen  */
                            adc_act_con.Set_Src_Node(null);
                            adc_act_con.Set_Dest_Node(null);
                            
                            adc_act_con.Set_Succ(null);
                            
                            adc_act_con = next_conn;
                            prev_conn = next_conn;
                        }
                        else
                            prev_conn = adc_act_con;
                                                                            
                    } while (MoveNextConn());
                }                
                
            } while (MoveNextNode());
        }
        
        /**Zugriffszeiger-Reset*/
        if(MoveFirstNode())
            MoveFirstConn ();

        /**Nodes neu nummerieren*/
        node_number_circle = 0;
        if(MoveFirstNode())
        {
            do
            {
                node_number_circle ++;
                
                adn_act_node.Set_Node_Number(node_number_circle);
                
            } while(MoveNextNode());
        }
    }
    
    /**ConnectNode: Connection etablieren*/
    public Adj_Connection ConnectNode(int i, int j)
    {
        return(ConnectNode(i,j,0));
    }

    public Adj_Connection ConnectNode(int i, int j, int weight)
    {
        return(ConnectNode(i,j,(double)weight));
    }

    public Adj_Connection ConnectNode(int i, int j, double weight)
    {
        Adj_Node src,dest;
        Adj_Connection conn,dpl;
        
        if((src = LookupIdentNode(i))==null)
            return(null);

        if ((dest = LookupIdentNode(j))==null)
            return(null);

        /**neue Adj_Connection erzeugen*/
        conn = new Adj_Connection(connection_typ);
        
        /**Knotenverweise setzen*/
        conn.Set_Src_Node(src);            
        conn.Set_Dest_Node(dest);
        
        /**Gewichtung setzen*/
        conn.Set_Weight(weight);
        
        /**Verbindung in Adjazenzliste herstellen*/
        switch(connection_typ)
        {
            case Adj_Connection.TypDirected:
                
                /**src->dest Verkn�pfung*/
                adn_act_node = src;
               
                if (MoveLastConn())
                {          
                    if (adc_act_con != null)
                    {
                        adc_act_con.Set_Succ(conn);
                    }
                    else
                        adn_act_node.Set_Succ(conn);
                }
                break;
                
            case Adj_Connection.TypNotDirected:
                
                /**src->dest Verkn�pfung*/
                adn_act_node = src;
               
                if (MoveLastConn())
                {          
                    if (adc_act_con != null)
                    {
                        adc_act_con.Set_Succ(conn);
                    }
                    else
                        adn_act_node.Set_Succ(conn);
                }
                
                /**Connection koperien f�r bi-direktionale Verbindung dest->src*/
                dpl = new Adj_Connection (conn);
                
                /**Knotenverweise setzen*/
                dpl.Set_Src_Node(dest);
                dpl.Set_Dest_Node(src);
                
                adn_act_node = dest;
                
                if (MoveLastConn())
                {          
                    if (adc_act_con != null)
                    {
                        adc_act_con.Set_Succ(dpl);
                    }
                    else
                        adn_act_node.Set_Succ(dpl);
                }
                
                break;
                
            default:
                break;
        }
        
        return(conn);
    }
    
    /**DeleteConnection: Verbindung l�schen*/
    public void DeleteConnection (int i, int j)
    {
        Adj_Node src,dest;
        Adj_Connection prev_conn;
        
        if((src = LookupIdentNode(i))==null)
            return;

        if ((dest = LookupIdentNode(j))==null)
            return;

        /**Verbindung in Adjazenzliste ansteuern*/
        switch(connection_typ)
        {
            case Adj_Connection.TypDirected:
                                
                /**src->dest Verkn�pfung*/
                adn_act_node = src;
                
                prev_conn = null;
                
                if(MoveFirstConn())
                {
                    do
                    {
                        if(adc_act_con.Get_Dest_Node() == dest)
                        {
                            if(prev_conn == null)
                                adn_act_node.Set_Succ(adc_act_con.Get_Succ());                                
                            else
                                prev_conn.Set_Succ(adc_act_con.Get_Succ());                                                                

                            break;
                        }
                        
                        prev_conn = adc_act_con;
                        
                    } while(MoveNextConn());
                }
                break;
                
            case Adj_Connection.TypNotDirected:
                
                /**src->dest Verkn�pfung*/
                adn_act_node = src;
                
                prev_conn = null;
                
                if(MoveFirstConn())
                {
                    do
                    {
                        if(adc_act_con.Get_Dest_Node() == dest)
                        {
                            if(prev_conn == null)
                                adn_act_node.Set_Succ(adc_act_con.Get_Succ());                                
                            else
                                prev_conn.Set_Succ(adc_act_con.Get_Succ());                                                                

                            break;
                        }
                        
                        prev_conn = adc_act_con;
                        
                    } while(MoveNextConn());
                }
                
                /**dest->src Verkn�pfung*/
                adn_act_node = dest;
                
                prev_conn = null;
                
                if(MoveFirstConn())
                {
                    do
                    {
                        if(adc_act_con.Get_Dest_Node() == src)
                        {
                            if(prev_conn == null)
                                adn_act_node.Set_Succ(adc_act_con.Get_Succ());                                
                            else
                                prev_conn.Set_Succ(adc_act_con.Get_Succ());                                                                

                            break;
                        }
                        
                        prev_conn = adc_act_con;
                        
                    } while(MoveNextConn());
                }
                
                break;
                
            default:
                break;
        }
    }
    
    /**Hilfsmethoden*/
    public String GetString ()
    {
        String s;

        s = "";
        
        if (MoveFirstNode()) 
        {
            do
            {
                s = "Node "+adn_act_node.Get_Node_Number();
                    
                if(MoveFirstConn())
                {
                    do
                    {                        
                        s=s+"->"+adc_act_con.Get_Dest_Node_Number()+"("+adc_act_con.Get_Weight()+")";
                        
                    } while(MoveNextConn());
                }
                
                s += "\n";
                
            } while(MoveNextNode()==true);
        }
        
        return s;
    }
    
    /**Mengenmethoden*/
    
    /**DescVector: Menger aller direkten Nachfolger eines Knotens    */
    public Adj_Vector DescVector (int start_node_number)
    {
        Adj_Node start_node;
        Adj_VectorItem avi;
        Adj_Vector res_vector;
        
        if(adn_first==null)
            return null;
            
        if( (start_node = LookupIdentNode(start_node_number)) == null)
            return(null);
            
        adn_act_node = start_node;       
        
        res_vector = new Adj_Vector();                            
        
        if(MoveFirstConn())
        {            
            do
            {         
                avi = new Adj_VectorItem(adc_act_con.Get_Dest_Node());
                
                res_vector.Add(avi);
                
            } while(MoveNextConn());
        }
        
        return(res_vector);
    }

    /**DescNrVector: Menger aller direkten Nachfolger eines Knotens als Nummern   */
    public Id_Vector DescNrVector (int start_node_number)
    {
        int i;
        Adj_Vector adj_res_vector;
        Id_Vector res_vector;
        
        res_vector = new Id_Vector();
        
        adj_res_vector = DescVector(start_node_number);
        
        for(i=1;i<=adj_res_vector.Size();i++)
        {
            res_vector.Add(adj_res_vector.Get(i).GetNodeNr());
        }
        
        return(res_vector);
    }

    /**AscVector: Menger aller direkten Vorg�nger eines Knotens*/
    public Adj_Vector AscVector (int start_node_number)
    {
        Adj_VectorItem avi;
        Adj_Vector res_vector;
        boolean found;
        
        if(adn_first==null)
            return null;
                               
        if(LookupIdentNode(start_node_number)==null)
            return null;
        
        res_vector = new Adj_Vector();                            
        
        if(MoveFirstNode())
        {
            do
            {
                found = false;
                
                if(MoveFirstConn())
                {
                    do
                    {
                        if (adc_act_con.Get_Dest_Node_Number()==start_node_number)
                        {
                            found = true;
                            break;
                        }
                        
                    } while(MoveNextConn());
                }
                
                if(found)
                {
                    avi = new Adj_VectorItem(adn_act_node);
                
                    res_vector.Add(avi);
                }
                
            } while(MoveNextNode());
        }
        
        return(res_vector);
    }

    /**AscNrVector: Menger aller direkten Vorg�nger eines Knotens als Nummern*/
    public Id_Vector AscNrVector (int start_node_number)
    {
        int i;
        Adj_Vector adj_res_vector;
        Id_Vector res_vector;
        
        res_vector = new Id_Vector();
        
        adj_res_vector = AscVector(start_node_number);
        
        for(i=1;i<=adj_res_vector.Size();i++)
        {
            res_vector.Add(adj_res_vector.Get(i).GetNodeNr());
        }
        
        return(res_vector);
    }
    
    /**SrcVector: Menge aller Quellen des Graphen*/
    public Adj_Vector SrcVector ()
    {
        Adj_VectorItem avi;
        Adj_Vector res_vector;
        boolean found;
        int vgl_node_id;
        
        if(adn_first==null)
            return null;
                               
        res_vector = new Adj_Vector();                            
        
        if(MoveFirstNode())
        {
            do
            {
                vgl_node_id = adn_act_node.Get_Node_Number();
                
                found = false;
                
                if(MoveFirstNode())
                {
                    do
                    {                               
                        if(MoveFirstConn())
                        {
                            do
                            {
                                if (adc_act_con.Get_Dest_Node_Number() == vgl_node_id)
                                {
                                   found = true;
                                   break;
                                }
                        
                            } while(MoveNextConn());
                        }
                        
                        if(found)
                            break;
                            
                    } while(MoveNextNode());
                }
                
                if(!found)
                {
                    avi = new Adj_VectorItem(LookupIdentNode(vgl_node_id));
                
                    res_vector.Add(avi);
                }
                
                adn_act_node = LookupIdentNode(vgl_node_id);
                
            } while(MoveNextNode());
        }
        
        return(res_vector);
    }    

    /**SrcNrVector: Menge aller Quellen des Graphen als Nummern*/
    public Id_Vector SrcNrVector ()
    {
        int i;
        Adj_Vector adj_res_vector;
        Id_Vector res_vector;
        
        res_vector = new Id_Vector();
        
        adj_res_vector = SrcVector();
        
        for(i=1;i<=adj_res_vector.Size();i++)
        {
            res_vector.Add(adj_res_vector.Get(i).GetNodeNr());
        }
        
        return(res_vector);
    }
    
    /**DestVector: Menge aller Senken des Graphen*/
    public Adj_Vector DestVector ()
    {
        Adj_VectorItem avi;
        Adj_Vector res_vector;
        
        if(adn_first==null)
            return null;
                               
        res_vector = new Adj_Vector();                            
        
        if(MoveFirstNode())
        {
            do
            {
                if(!MoveFirstConn())
                {
                    avi = new Adj_VectorItem(adn_act_node);
                
                    res_vector.Add(avi);
                }
                                
            } while(MoveNextNode());
        }
        
        return(res_vector);
    }    

    /**DestNrVector: Menge aller Senken des Graphen als Nummern*/
    public Id_Vector DestNrVector ()
    {
        int i;
        Adj_Vector adj_res_vector;
        Id_Vector res_vector;
        
        res_vector = new Id_Vector();
        
        adj_res_vector = DestVector();
        
        for(i=1;i<=adj_res_vector.Size();i++)
        {
            res_vector.Add(adj_res_vector.Get(i).GetNodeNr());
        }
        
        return(res_vector);
    }

    /**NodeVector: Menge aller Knoten des Graphen*/
    public Adj_Vector NodeVector ()
    {
        Adj_VectorItem avi;
        Adj_Vector res_vector;
        
        res_vector = new Adj_Vector();                            
        
        if(adn_first==null)
            return (res_vector);                               
        
        if(MoveFirstNode())
        {
            do
            {
                avi = new Adj_VectorItem(adn_act_node);
                
                res_vector.Add(avi);
                                
            } while(MoveNextNode());
        }
        
        return(res_vector);
    }    

    /**NodeNrVector: Menge aller Knoten-Nummern des Graphen*/
    public Id_Vector NodeNrVector ()
    {
        Id_Vector res_vector;
        
        res_vector = new Id_Vector();                            
        
        if(adn_first==null)
            return (res_vector);                               
        
        if(MoveFirstNode())
        {
            do
            {
                res_vector.Add(adn_act_node.Get_Node_Number());
                                
            } while(MoveNextNode());
        }
        
        return(res_vector);
    }    

    /**ConVector: Menge aller Kanten des Graphen*/
    public Adj_ConVector ConVector (boolean asymmetric)
    {
        Adj_ConVectorItem avi;
        Adj_ConVector res_vector;
        boolean add;
        res_vector = new Adj_ConVector();                            
        
        if(adn_first==null)
            return (res_vector);                               
        
        if(MoveFirstNode())
        {
            do
            {
                if(MoveFirstConn())
                {
                    do
                    {   
                        add = true;                            
                        
                        if( (asymmetric) 
                          && (adc_act_con.Get_Typ() == Adj_Connection.TypNotDirected) )
                        {
                            if(!adc_act_con.Is_Master())
                                add = false;
                        }                                
                        
                        if(add)
                        {
                            avi = new Adj_ConVectorItem(adc_act_con);
                
                            res_vector.Add(avi);
                        }
                        
                    } while(MoveNextConn());
                }
                
            } while(MoveNextNode());
        }
        
        return(res_vector);
    }    
    
    /**DeleteNodeMarkers: Alle Node-Markierungen entfernen*/
    public void DeleteNodeMarkers()
    {
        if (adn_first == null)
            return;
            
        if(MoveFirstNode())
        {
            do
            {
                adn_act_node.DeleteMarker();
                
            } while(MoveNextNode());
        }        
    }

    /**DeleteConnectionMarkers: Alle Kanten-Markierungen entfernen*/
    public void DeleteConnectionMarkers()
    {
        if (adn_first == null)
            return;
            
        if(MoveFirstNode())
        {
            do
            {
                if(MoveFirstConn())
                {
                    do
                    {                                   
                        adc_act_con.DeleteMarker();
                    
                    } while(MoveNextConn());
                }
                
            } while(MoveNextNode());
        }        
    }

    /**GetMatrix: Berechnen der Adjazenzmatrix gewichtet oder ungewichted */
    public Adj_Matrix GetMatrix (boolean weighted)
    {
        int anz;
        Adj_Matrix matrix;
        
        /**Knotenanzahl ermitteln*/
        anz = 0;
        if(MoveFirstNode())
        {
            do
            {
                anz ++;                
            } while (MoveNextNode());
        }
        
        matrix = new Adj_Matrix(anz);
        
        if(weighted)
            matrix.FillInfinite();
        
        if(MoveFirstNode())
        {
            do
            {
                if(MoveFirstConn())
                {
                    do
                    {
                        try
                        {
                            if(weighted == false)
                            {
                                matrix.Set(adn_act_node.Get_Node_Number(),
                                        adc_act_con.Get_Dest_Node_Number(),1);
                            }
                            else
                            {
                                matrix.Set(adn_act_node.Get_Node_Number(),
                                        adc_act_con.Get_Dest_Node_Number(),
                                        adc_act_con.Get_Weight());
                            }
                        }
                        catch(Exception e)
                        {}             
                        
                    } while(MoveNextConn());
                }
            } while (MoveNextNode());
        }        
        
        return(matrix);
    }
 
    /**GetTransitiveCover: ermitteln der transitiven H�lle gewichted - ungewichted*/
    public Adj_Matrix GetTransitiveCover (boolean weighted)
    {
        int k,i,j;
        Adj_Matrix matrix,cover;
        
        /**Adjazenz-Matrix erstellen*/
        matrix = GetMatrix(weighted);
        
        /**Matrix kopieren*/
        cover = new Adj_Matrix(matrix);                
        
        if(weighted)
        {
            /**Floyd-Alg.:*/
            for(k=1;k<=cover.Rows();k++)
            {
                for(i=1;i<=cover.Rows();i++)
                {
                    for(j=1;j<=cover.Rows();j++)
                    {
                        if( (cover.Get(i,k) + cover.Get(k,i) ) < cover.Get(i,j) )
                        {
                            cover.Set(i,j,cover.Get(i,k) + cover.Get(k,i));
                        }
                    }
                }
            }    
        }
        else
        {
            /**Warshall Alg.:*/
            for(k=1;k<=cover.Rows();k++)
            {
                for(i=1;i<=cover.Rows();i++)
                {
                    if(cover.Get(i,k) != 0)
                    {
                        for(j=1;j<=cover.Rows();j++)
                        {
                            if(cover.Get(k,j) != 0)
                            {
                                cover.Set(i,j,1);
                            }
                        }
                    }
                }    
            }
        }
        
        return(cover);
    }
    
    /**ReIdentify: erneute Identifizierung der Knoten und Connections*/
    public void ReIdentify ()
    {
        if(MoveFirstNode())
        {
            do
            {
                if(MoveFirstConn())
                {
                    do
                    {
                        adc_act_con.ReIdentify();

                    } while(MoveNextConn());
                }
                
                adn_act_node.ReIdentify();
                
            } while(MoveNextNode());
        }        
    }   
    
    private void SearchPath (int src_node_nr, int dest_node_nr, 
                                    Adj_ConVector act_path, Vector res_path)
    {        
        Adj_ConVectorItem act_aci,new_aci;        
        Adj_ConVector new_path;
        Adj_Connection tmp_con;
        Adj_Node tmp_node;
        int i;
        boolean zyklus;
        
        //keine aktuelle Kante-> versuchen erste Kante anzusteuern
        if(adc_act_con == null)
            if(!MoveFirstConn())
                return;
                
        //horizontale Schleife
        do
        {
            //Kante verweist auf Endknoten ?
            if(adc_act_con.Get_Dest_Node_Number() == dest_node_nr)
            {
                //Pfad zuende -> neuer Pfad bereitstellen

                //neuen Pfad bereitstellen
                new_path = new Adj_ConVector();
                
                //Kanten kopieren
                for(i=0;i<act_path.size();i++)
                {
                    new_aci = new Adj_ConVectorItem((Adj_ConVectorItem) 
                                    act_path.elementAt(i));
                    
                    new_path.addElement(new_aci);
                }
                    
                //Kante auf Endknoten in neuen Pfad aufnehmen
                act_aci = new Adj_ConVectorItem(adc_act_con);                                       
                new_path.Add(act_aci);
                
                //Neuer Pfad der Ergebnismenge hinzuf�gen                
                res_path.addElement(new_path);
            }
            else //Nein
            {              
                //auf Zyklus pr�fen...
                zyklus = false;
                for(i=0;i<act_path.size();i++)
                {
                    act_aci = (Adj_ConVectorItem) act_path.elementAt(i);
                    
                    if(adc_act_con.Get_Dest_Node_Number() == act_aci.Get_Src_Node_Number())
                    {
                        zyklus = true;                        
                        break;
                    }    
                }             
                
                if(!zyklus)
                {
                    //Kante in Pfad aufnehmen
                    act_aci = new Adj_ConVectorItem(adc_act_con);                                       
                    act_path.Add(act_aci);

                    tmp_node = adn_act_node;
                    tmp_con = adc_act_con;
                    
                    //in die Tiefe gehen
                    adn_act_node = adc_act_con.Get_Dest_Node();

                    adc_act_con = null;
                    
                    //weitersuchen
                    SearchPath (src_node_nr,dest_node_nr, act_path, res_path);
                    
                    //R�cksprungpunkt wiederherstellen
                    adn_act_node = tmp_node;
                    adc_act_con = tmp_con;
                    
                    //letztes Element entfernen
                    act_path.removeElementAt(act_path.size()-1);
                }    
            }

        } while(MoveNextConn()); //zur n�chsten direkten Nachfolger-Kante springen
    }
    
    /**PathVector: Ermittelt alle zyklenfreien Wege zwischen Quell- und Zielknoten*/
    public Vector PathVector (Id_Vector src_nodes, Id_Vector dest_nodes)
    {
        Vector res_path;
        Adj_ConVector act_path;
        Adj_Node src_node;
        int i,ii,src_node_nr,dest_node_nr;
        
        res_path = new Vector();

        if( (src_nodes.Size() == 0) || (dest_nodes.Size() == 0) )
            return(res_path);
            
        //Adjazenzlisten-Durchlauf
        for(i=1;i<=src_nodes.Size();i++)
        {
            src_node_nr = src_nodes.Get(i);
    
            for(ii=1;ii<=dest_nodes.Size();ii++)
            {
                dest_node_nr = dest_nodes.Get(ii);
                
                if(src_node_nr != dest_node_nr) //evtl. Zyklus sofort abpr�fen
                {
                    if(LookupIdentNode(src_node_nr) == null)
                        break;
                    
                    if(MoveFirstConn())
                    {
                        //neue Kantenmenge bereitstellen
                        act_path = new Adj_ConVector();

                        SearchPath (src_node_nr, dest_node_nr, act_path, res_path);                
                    }                                                            
                }    
            }            
        }

        return(res_path);
    }
}
