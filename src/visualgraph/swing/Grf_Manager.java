package visualgraph.swing;
import java.awt.*;
import java.util.Vector;

import visualgraph.core.Adj_ConVector;
import visualgraph.core.Adj_ConVectorItem;
import visualgraph.core.Adj_Connection;
import visualgraph.core.Adj_Manager;
import visualgraph.core.Adj_Matrix;
import visualgraph.core.Adj_Node;
import visualgraph.core.Adj_Vector;
import visualgraph.core.Adj_VectorItem;
import visualgraph.core.Id_Vector;

import java.awt.event.*;

/**GUI-Containerklasse f�r Graphen*/
public class Grf_Manager extends Frm_Frame
{           
    /**Verbindungsart*/
    protected int connection_typ;

    /**Knotentyp*/
    protected int node_typ;
    
    /**Knotenidentifkation*/
    protected int node_ident;
    
    /**Verweis auf enthaltenen Adj_Manager*/
    protected Adj_Manager adm_manager;

    /**Vector der Graph-Nodes*/
    protected Vector grn_nodes;
    
    /**Vector der Graph-Connections*/
    protected Vector grc_cons;

    /**Vector der Graph-Labels*/
    protected Vector grl_labels;

    /**ScrollPane*/
    protected ScrollPane adm_scroll;
    
    /**zugeh�riges Panel*/
    protected Grf_DoublePanel adm_panel;
        
    /**Gewichtungen anzeigen*/
    protected boolean show_weights;

    /**Verbindungstext anzeigen*/
    protected boolean show_connection_caption;

    /**Am Raster ausrichten*/
    protected boolean align_grid;
    
    /**Start Knoten*/
    protected int start_node_id;
    
    /**Info-Label*/
    protected Grf_Label grm_info;
    
    /**Node-Info Labels*/
    protected Vector node_info;
            
    /**Hilfsattribute*/
    protected Vector grn_typ_cmi;
    protected Vector grn_ident_cmi;

    /**Property-Dialog*/
    protected transient Dlg_GraphProp prop;
    
    static final long serialVersionUID = 867847003259605202L;
    
    /**PrepareSerializedObject: Serialisierungsverarbeitung, overrides Frm_Frame*/
    public void PrepareSerializedObject(int prepare_mode) 
    {
        int i;
        Grf_Node act_node;
        Grf_Connection act_con;
        Grf_EditLabel act_label;

        //Super-Methode aufrufen
        super.PrepareSerializedObject(prepare_mode);

        //Eigene Verabreitung durchf�hren
        switch(prepare_mode)
        {
            case Frs_Manager.SER_BEFORE_WRITE:
        		
        		/**Property-Dialog noch ge�ffnet -> schlie�en*/
        		if(prop != null)
        		{
        		    prop.Remove();
        		}        		
                break;
                
            case Frs_Manager.SER_AFTER_WRITE:
                break;
                
            case Frs_Manager.SER_AFTER_READ:
            
                /**Adj_Manager neu identifizieren*/
                adm_manager.ReIdentify();

        		/**neue Listeners einh�ngen*/
                adm_panel.addMouseListener(new Grf_MouseEvent());                      
                break;
        }        

		/**.. und weitergeben an Grf_Nodes*/
		for(i=0;i<grn_nodes.size();i++)
		{
		    act_node = (Grf_Node) grn_nodes.elementAt(i);
		    act_node.PrepareSerializedObject(prepare_mode);
		}
		
		/**.. und weitergeben an Grf_Connections*/
		for(i=0;i<grc_cons.size();i++)
		{
		    act_con = (Grf_Connection) grc_cons.elementAt(i);
		    act_con.PrepareSerializedObject(prepare_mode);
		}		        

		/**.. und weitergeben an Grf_EditLabels*/
		for(i=0;i<grl_labels.size();i++)
		{
		    act_label = (Grf_EditLabel) grl_labels.elementAt(i);
		    act_label.PrepareSerializedObject(prepare_mode);
		}		        
    }
        
    /**Konstruktoren*/
    public Grf_Manager(Frm_Manager mgr)
    {                        
        this(Adj_Connection.TypDirected,"",Grf_Node.SIMPLE_GRAPH,mgr, Grf_Node.NODE_IDENT_ALPHA);
	}
    
    public Grf_Manager(String name,Frm_Manager mgr)
    {                        
        this(Adj_Connection.TypDirected,name,Grf_Node.SIMPLE_GRAPH,mgr, Grf_Node.NODE_IDENT_ALPHA);
    }

    public Grf_Manager(int con_typ, Frm_Manager mgr)
    {                        
        this(con_typ,"",Grf_Node.SIMPLE_GRAPH, mgr, Grf_Node.NODE_IDENT_ALPHA);
    }

    /**Copy-Konstruktoren*/
    public Grf_Manager(String name, Grf_Manager src)
    {      
        this(name,src, true);
    }
    
    public Grf_Manager(String name, Grf_Manager src, boolean connected)
    {               
        this(src.connection_typ,name,src.node_typ, src.frm_mgr,src.node_ident);        
        int i;
        
        Set_Show_Weights(src.Get_Show_Weights());
        Set_Align_Grid(src.Get_Align_Grid());
        
        SetZoom((int)src.GetZoomPercent());
        
        /**Nodes kopieren*/
        for(i=0;i<src.grn_nodes.size();i++)
        {
            CopyNode((Grf_Node) src.grn_nodes.elementAt(i));            
        }
        
        if(connected)
        {
            /**Connections kopieren*/
            for(i=0;i<src.grc_cons.size();i++)
                CopyConnection((Grf_Connection) src.grc_cons.elementAt(i));            
        }    
    }
    
    /**Hauptkonstruktor*/
    public Grf_Manager(int con_typ, String name, int node_type, Frm_Manager mgr, int ident)
    {                        
        int i;
        MenuItem mi;
        CheckboxMenuItem cmi;
        Menu mu;
        Dimension vp_size;
        FontMetrics fm;
        
        /**Connection-Typ sicherstellen*/
        if( (con_typ != Adj_Connection.TypDirected) && 
                (con_typ != Adj_Connection.TypNotDirected) )
            con_typ = Adj_Connection.TypDirected;            
            
        /**Connection-Typ setzen*/
        connection_typ = con_typ;
        
        /**Knotenart setzen*/
        node_typ = node_type;
        
        /**Knotenidentifaktion*/
        node_ident = ident;
        
        /**Gewichtungen anzeigen*/
        show_weights = true;

        //**Am Raster ausrichten
        align_grid = true;
        
        /**Verbindungstext anzeigen*/
        show_connection_caption = true;
        
        /**Start-Node setzen*/
        start_node_id = 1;
        
        /**Verweise setzen*/
        frm_mgr = mgr;
                
        /**Neuen Adj_Manager bereitstellen*/
        adm_manager = new Adj_Manager(con_typ);
        
        /**Neues ScrollPane erzeugen*/
        adm_scroll = new ScrollPane();
        this.add(adm_scroll);
        
        /**Neues Panel erzeugen, Layout Manager verwerfen*/
        adm_panel = new Grf_DoublePanel();

        /**Panel-Resize u. der Scroll-Pane hinzuf�gen*/
        vp_size = adm_scroll.getViewportSize();        
        vp_size.width = vp_size.width - 16;
        vp_size.height = vp_size.height - 16;

        adm_panel.setSize(vp_size.width,vp_size.height);
        adm_scroll.add(adm_panel);
        
        /**Graf-Nodes, Graf-Connections, Labels Vector erzeugen*/
        grn_nodes = new Vector();                
        grc_cons = new Vector();
        grl_labels = new Vector();
        
        /**Name initialisieren*/
        Set_Name(name);
                
        /**Event-Listeners registrieren*/
        adm_panel.addMouseListener(new Grf_MouseEvent());
        
        /**Info-Label erzeugen*/
        grm_info = new Grf_Label(this,"");
        grm_info.setLocation(0,0);
        grm_info.setFont(new Font("SansSerif",Font.PLAIN,font_size));        
        fm = grm_info.getFontMetrics(grm_info.getFont());
        grm_info.setSize(4,fm.getHeight());
        adm_panel.add(grm_info);
        
        /**Node-Info-Labels*/
        node_info = new Vector();

        /**Alg-Std Info Label einh�ngen*/
        adm_panel.add(alg_std_info);
        
		/**Popup-Menu einrichten*/		
    	mi = new MenuItem("New Node");
		mi.addActionListener(frm_button_action);
		mi.setActionCommand("NEW_NODE");		
        frm_popup.add(mi);

    	mi = new MenuItem("New Label");
		mi.addActionListener(frm_button_action);
		mi.setActionCommand("NEW_LABEL");		
        frm_popup.add(mi);

		frm_popup.addSeparator();

		mu = new Menu("Graph Type");
		frm_popup.add(mu);

        grn_typ_cmi = new Vector();
        
		for(i=0;i<Grf_Node.node_types.length;i++)
		{
    		cmi = new CheckboxMenuItem(Grf_Node.node_type_names[i]);    		
	    	cmi.addItemListener(frm_item_event);    		
			cmi.setActionCommand("NODE_TYPE_"+Grf_Node.node_types[i]);		
    		mu.add(cmi);		    

    		/**und der Liste hinzuf�gen*/
    		grn_typ_cmi.addElement(cmi);    		
    		
    	    /**Check-Setzen*/
    	    if(node_type == Grf_Node.node_types[i])
    	        cmi.setState(true);
		}

		mu = new Menu("Node Identification");
		frm_popup.add(mu);

        grn_ident_cmi = new Vector();
        
		cmi = new CheckboxMenuItem("numeric");    		
    	cmi.addItemListener(frm_item_event);    		
		cmi.setActionCommand("NODE_IDENT_"+Grf_Node.NODE_IDENT_NUMERIC);		
		mu.add(cmi);		    
  		grn_ident_cmi.addElement(cmi);    		

		cmi = new CheckboxMenuItem("alphabetical");    		
    	cmi.addItemListener(frm_item_event);    		
		cmi.setActionCommand("NODE_IDENT_"+Grf_Node.NODE_IDENT_ALPHA);		
		mu.add(cmi);		    
  		grn_ident_cmi.addElement(cmi);    		
    		
 	    /**Check-Setzen*/
        cmi.setState(true);

    	cmi = new CheckboxMenuItem("Weights",show_weights);
    	cmi.addItemListener(frm_item_event);
		cmi.setActionCommand("SHOW_WEIGHTS");		    	
        frm_popup.add(cmi);

	    Init_Alg_Menu();
	    
        /**Alignment- und Packing*/
        frm_popup.addSeparator();

    	mi = new MenuItem("Pack Graph");
		mi.addActionListener(frm_button_action);
		mi.setActionCommand("PACK_GRAPH");		
        frm_popup.add(mi);

    	mi = new MenuItem("Align Graph");
		mi.addActionListener(frm_button_action);
		mi.setActionCommand("ALIGN_GRAPH");		
        frm_popup.add(mi);

    	cmi = new CheckboxMenuItem("Align to Grid",align_grid);
    	cmi.addItemListener(frm_item_event);
		cmi.setActionCommand("ALIGN_GRID");		    	
        frm_popup.add(cmi);

		/**Eigenschaften*/
        frm_popup.addSeparator();
        
		mi = new MenuItem("Properties");
		mi.addActionListener(frm_button_action);
		mi.setActionCommand("PROP");		
		frm_popup.add(mi);
			
		adm_panel.add(frm_popup);        
	
		Set_Edited ();
		
		Set_Info();
    }
    
    /**InitialSize: Initial Size setzen*/
    public void InitialSize(int width, int height)
    {
        Dimension new_dim,vp_size,dim;
        
        new_dim = RecalcNeededDimension();
        
        /**Panel Resize*/
        setSize(width,height);        

        /**Scroll-Pane Resize*/
        synchronized(adm_scroll)
        {
            /**Scroller Resize*/
            adm_scroll.setSize(width,height);
        
            /**Adm-Panel Resize*/
            adm_panel.setSize(new_dim);                            
            
            /**Alg. Std. Info-Location neu setzen*/
            vp_size = adm_scroll.getViewportSize();
            
            dim = alg_std_info.getSize();                 
            
            alg_std_info.setLocation(vp_size.width/2-dim.width/2,
                                    adm_scroll.getScrollPosition().y+
                                    vp_size.height-info_bottom_margin-
                                    dim.height);                                    
     
            adm_scroll.doLayout();                                                                                    
        }    
    }

    /**NodeGridToKoord: Berechnen der Bild-koordinaten �ber Rasterkoord.*/
    public Point NodeGridToKoord (int col, int row, int distance, int offset)
    {
        int x,y;
        Dimension node_dim;
        
        node_dim = Grf_Node.node_type_dimensions[node_typ];        
        
        x = offset + col*(node_dim.width+2+distance);
        y = offset + row*(node_dim.height+2+distance);                
        
        return(new Point(x,y));
    }

    /**NodeKoordToGrid: Berechnen der Rasterkoord. �ber Bild-koordinaten*/
    public Point NodeKoordToGrid (int x, int y, int distance, int offset)
    {
        int col,row;
        Dimension node_dim;
        
        node_dim = Grf_Node.node_type_dimensions[node_typ];        
        
        col = (int) ((x-offset)/(node_dim.width+2+distance));
        row = (int) ((y-offset)/(node_dim.height+2+distance));                
        
        return(new Point(col,row));
    }

    //**Graph-Knoten an virtuellem Knotenraster ausrichten
    public void Align()
    {
        int i;
        Grf_Node act_node;
        Point new_loc,grid_loc;
        
        for(i=0;i<grn_nodes.size();i++)
        {
            act_node = (Grf_Node) grn_nodes.elementAt(i);
            
            grid_loc = NodeKoordToGrid(act_node.getLocation().x,act_node.getLocation().y,0,0);
            new_loc  = NodeGridToKoord(grid_loc.x,grid_loc.y,0,0);
                
            act_node.setLocation(new_loc.x,new_loc.y);
            
            //Bewegung verarbeiten
            HandleNodeMotion(act_node);
        }
    }
    
    /**AddNode: Neue Knoten an Rasterkoordinaten einf�gen*/
    public int AddNode (int col, int row)
    {
        Point loc;
        
        loc = NodeGridToKoord(col,row,0,0);
        
        return(AddNodeAtPoint(loc.x,loc.y));
    }
    
    /**AddNodeAtPoint: Neue Knoten an Koordinaten einf�gen*/
    public int AddNodeAtPoint (int x, int y)
    {
        Grf_Node grn;
        Adj_Node adn;
        int node_nr;
        Point new_loc,grid_loc;
        
        /**Neuer Adjazenz_Node erzeugen*/
        node_nr = adm_manager.AddNode();
        adn = adm_manager.LookupIdentNode(node_nr);

        /**neuen Graph_Node erzeugen*/
        grn = new Grf_Node(this);
        
        grn.Set_Lock(is_locked);
        
        /**Verweise der Knoten setzen*/
        grn.Set_Adn_Node(adn);
        
        /**Gr��e und Farbe einstellen*/
        grn.setBackground(Color.white);
        
        /**Auf Alignment pr�fen*/
        if(align_grid)
        {
            grid_loc = NodeKoordToGrid(x,y,0,0);
            new_loc = NodeGridToKoord(grid_loc.x,grid_loc.y,0,0);
 
            x = new_loc.x;
            y = new_loc.y;
        }
        
        grn.setBounds(x,y,grn.getBounds().width,grn.getBounds().height);
        
        /**Component einh�ngen*/
        adm_panel.add(grn);

        /**Vector erweitern*/
        grn_nodes.addElement(grn);

        /**Knoten zeichnen*/
        grn.repaint();

        Set_Edited();
        
        return(node_nr);
    }

    /**AddLabelAtPoint: Neues Labels an Koordinaten einf�gen*/
    public void AddLabelAtPoint (int x, int y)
    {
        Grf_EditLabel grl;

        /**neuen Graph_Labels erzeugen*/
        grl = new Grf_EditLabel(this,x,y);
        
        /**Component einh�ngen*/
        adm_panel.add(grl,0);

        /**Vector erweitern*/
        grl_labels.addElement(grl);

        Set_Edited();        
    }
    
    /**DeleteLabel: Label aus Graph entfernen*/
    public void DeleteLabel(Grf_EditLabel label)
    {
        if(grl_labels.contains(label) == false)
            return;
                                    
        /**Component aush�ngen*/
        adm_panel.remove(label);
        
        /**aus Liste entfernen*/
        grl_labels.removeElement(label);    
        
        /**und neu zeichenen   */
        Repaint();        
        
        /**neue Gr��e berechnen*/
        if(getParent() != null)        
            dispatchEvent(new ComponentEvent(getParent(),ComponentEvent.COMPONENT_RESIZED));                          
        
   		Set_Edited ();
    }

    /**Copy: Kopiert einen Knoten*/
    protected int CopyNode (Grf_Node src)
    {
        Grf_Node grn;
        Adj_Node adn;
        int node_nr;

        /**Neuer Adjazenz_Node erzeugen*/
        node_nr = adm_manager.CopyNode(src.Get_Adn_Node());        
        adn = adm_manager.LookupIdentNode(node_nr);
        
        /**neuen Graph_Node erzeugen*/
        grn = new Grf_Node(this);
        
        grn.Set_Lock(is_locked);
        
        /**Verweise der Knoten setzen*/
        grn.Set_Adn_Node(adn);

        /**Gr��e und Farbe einstellen*/
        grn.setBackground(Color.white);
        grn.setBounds(src.getLocation().x,src.getLocation().y,src.getSize().width,src.getSize().height);
        
        /**Component einh�ngen*/
        adm_panel.add(grn);

        /**Vector erweitern*/
        grn_nodes.addElement(grn);

        /**neu zeichnen*/
        grn.repaint();

        Set_Edited();
        
        return(node_nr);
    }

    /**CopyConnection: Kopiert eine Verbiundung*/
    protected Grf_Connection CopyConnection (Grf_Connection src)
    {
        Grf_Connection grc;
        
        /**Connection erzeugen*/
        grc = ConnectNode(src.Grc_Get_Src_Node().Get_Node_Number(), 
                    src.Grc_Get_Dest_Node().Get_Node_Number(), src.Get_Weight());
        
        return(grc);            
    }

    /**DeleteNode: Knoten aus Graph entfernen*/
    public void DeleteNode(int node_identificator)
    {
        int i;
        Grf_Node grn;
        Grf_Connection act_conn;
        boolean found;
        
        grn = LookupIdentNode(node_identificator);

        if(grn == null)
            return;
        
        /**Knoten aus Adjazenliste entfernen*/
        adm_manager.DeleteNode(grn.Get_Node_Number());
        
        /**Connection-Liste nach Node durchsuchen und l�schen*/
        for(;;)
        {
            found = false;

            for(i=0;i<grc_cons.size();i++)
            {
                act_conn = (Grf_Connection) grc_cons.elementAt(i);
                
                if (act_conn.Grc_NodeInvolved(grn) == true)
                {
                    /**Verweise l�schen*/
                    act_conn.Grc_Set_Grf_Nodes(null,null,null);
                    
                    /**Connection aus Panel entfernen*/
                    act_conn.RemoveLabels();
                    adm_panel.remove(act_conn);      
                    
                    /**aus Liste entfernen*/
                    grc_cons.removeElement(act_conn);
                    
                    found = true;
                    break;
                }
            }
            
            if(!found)
                break;
        }
        
        grn.Set_Adn_Node(null);
        
        /**Component aush�ngen*/
        adm_panel.remove(grn);
        
        /**aus Liste entfernen*/
        grn_nodes.removeElement(grn);    
        
        /**und neu zeichenen   */
        Repaint();        
        
        /**neue Gr��e berechnen*/
        if(getParent() != null)        
            dispatchEvent(new ComponentEvent(getParent(),ComponentEvent.COMPONENT_RESIZED));                          
        
   		Set_Edited ();
    }

    /**SetNodeVisible: Knoten anzeigen */
    public void SetNodeVisible(int node_identificator, boolean visible)
    {
        int i;
        Grf_Node grn;
        Grf_Connection act_conn;
        boolean found;
        
        grn = LookupIdentNode(node_identificator);

        if(grn == null)
            return;

        grn.setVisible(visible);
        
        /**Connection-Liste nach Node durchsuchen*/
        for(;;)
        {
            found = false;

            for(i=0;i<grc_cons.size();i++)
            {
                act_conn = (Grf_Connection) grc_cons.elementAt(i);
                
                if (act_conn.Grc_NodeInvolved(grn) == true)
                {
                    act_conn.setVisible(visible);
                                        
                    found = true;
                    break;
                }
            }
            
            if(!found)
                break;
        }
        
        /**und neu zeichenen   */
        Repaint();        
        
        /**neue Gr��e berechnen*/
        if(getParent() != null)        
            dispatchEvent(new ComponentEvent(getParent(),ComponentEvent.COMPONENT_RESIZED));                          
        
   		Set_Edited ();
    }
    
    /**DeleteConnection: Connection l�schen*/
    public void DeleteConnection(int i, int j)
    {
        int ii;
        Grf_Connection gcon=null;
        boolean found;
        
        found = false;
        
        for(ii=0;ii<grc_cons.size();ii++)
        {
            gcon = (Grf_Connection) grc_cons.elementAt(ii);
            
            if( (gcon.Grc_Get_Src_Node().Get_Node_Number() == i) &&
                (gcon.Grc_Get_Dest_Node().Get_Node_Number() == j) )
            {
                found = true;
                break;
            }
        }
        
        if(!found)
            return;
            
        /**Connection aus Adjazenzliste entfernen*/
        adm_manager.DeleteConnection(i,j);    

        /**Vector verk�rzen*/
        grc_cons.removeElement(gcon);
                                
        /**neue Connections l�schen        */
        gcon.RemoveLabels();       
        adm_panel.remove(gcon);

        /**neu zeichnen*/
        Repaint();
        
   		Set_Edited ();
    }
    
    /**DeleteAllNodes: L�scht alle Knoten*/
    public void DeleteAllNodes ()
    {
        /**Alle Adjazenzlisteneintr�ge l�schen*/
        adm_manager.DeleteAllNodes();
        
        /**sichtbare Komponenten l�schen*/
        adm_panel.removeAll();
        
        /**Vektoren l�schen*/
        grn_nodes = null;
        grc_cons = null;
        
        grn_nodes = new Vector();
        grc_cons = new Vector();
        
   		Set_Edited ();
    }

    /**DeleteAllConnections: L�scht alle Verbindungen*/
    public void DeleteAllConnections ()
    {
        int i;
        Grf_Connection act_con;
        
        /**Alle Adjazenzlisteneintr�ge l�schen*/
        adm_manager.DeleteAllConnections();
        
        /**sichtbare Komponenten l�schen*/
        for(i=0;i<grc_cons.size();i++)
        {
            act_con = (Grf_Connection) grc_cons.elementAt(i);            
            
            act_con.RemoveLabels();
            adm_panel.remove(act_con);
        }
        
        /**Vektor l�schen*/
        grc_cons = null;        
        grc_cons = new Vector();        
        
   		Set_Edited ();
    }
    
    /**ConnectionPossible: Pr�fung ob neue Verbindung zul�ssig*/
    protected boolean ConnectionPossible(int i, int j)
    {
        if(i == j)
            return(false);

        if(adm_manager.ConnectionPossible(i,j) == false)
            return(false);
            
        return(true);
    }

    /**ConnectNode: Knoten Verkn�pfen    */
    public Grf_Connection ConnectNode(int i, int j)
    {   
        return(ConnectNode(i,j,0));
    }

    public Grf_Connection ConnectNode(int i, int j, int weight)
    {   
        return(ConnectNode(i,j,(double) weight));
    }
    
    public Grf_Connection ConnectNode(int i, int j, double weight)
    {   
        Grf_Connection gcon;
        Adj_Connection acon;

        /**Plausibel ?*/
        if (ConnectionPossible(i,j) == false)
            return(null);

        /**Adjazenzlisten-Verbindung herstellen*/
        acon = adm_manager.ConnectNode(i,j,weight);
        
        gcon = new Grf_Connection(this);
            
        gcon.Set_Lock(is_locked);
        gcon.Grc_Set_Grf_Nodes(LookupIdentNode(i),LookupIdentNode(j),acon);          
                   
        /**Vector erweitern*/
        grc_cons.addElement(gcon);
                
        /**neue Connections sichtbar machen*/
        adm_panel.add(gcon);

        /**neu zeichnen*/
        gcon.repaint();

   		Set_Edited ();
   		
        return(gcon);
    }

    /**ConnectNodeByInteraction: Knoten Verkn�pfen durch Benutzer-Interaktion im GUI  */
    public void ConnectNodeByInteraction(Grf_Node src_node, int x, int y)
    {   
        int i;
        Grf_Node grn;
        
        for(i=0;i<grn_nodes.size();i++)
        {
            grn = (Grf_Node) grn_nodes.elementAt(i);
            
            if (grn.getBounds().contains(x,y) == true)
            {
                /**neue Verbindung aufbauen*/
                ConnectNode(src_node.Get_Node_Number(),grn.Get_Node_Number());
                break;
            }
        }        
    }
    
    /**LookupIdentNode: Lookup nach Id*/
    public Grf_Node LookupIdentNode(int number)
    {
        int i;
        Grf_Node grn;
        
        for(i=0;i<grn_nodes.size();i++)
        {
            grn = (Grf_Node) grn_nodes.elementAt(i);
            
            if (grn.Get_Node_Number() == number)
                return(grn);
        }
        return(null);
    }

    /**LookupIdentConnection: Lookup Connection nach Ids*/
    public Grf_Connection LookupIdentConnection (int src_nr, int dest_nr)
    {
        int i;
        Grf_Connection grc;
        
        for(i=0;i<grc_cons.size();i++)
        {
            grc = (Grf_Connection) grc_cons.elementAt(i);
            
            if( (grc.Grc_Get_Src_Node().Get_Node_Number() == src_nr) &&
                (grc.Grc_Get_Dest_Node().Get_Node_Number() == dest_nr) )
                return(grc);
        }
        return(null);
    }
    
    /**ChangeNodeTyp: Knotenart ver�ndern*/
    public void ChangeNodeTyp(int new_type)
    {
        int i;
        Grf_Node act_node;
        
        if(node_typ == new_type)
            return;
            
        /**neuer Typ setzen*/
        node_typ = new_type;
        
        /**die einzelnen Knoten ver�ndern*/
        for(i=0;i<grn_nodes.size();i++)
        {
            act_node = (Grf_Node) grn_nodes.elementAt(i);
            
            act_node.ChangeNodeTyp(new_type);
        }
        
        /**und neu zeichenen   */
        RepaintNodes();
        RepaintConnections();


   		Set_Edited ();
   		
        /**neue Gr��e berechnen*/
        if(getParent() != null)        
            dispatchEvent(new ComponentEvent(getParent(),ComponentEvent.COMPONENT_RESIZED));                                  
    }
    
    /**RepaintConnections: Alle Connections neu zeichnen*/
    public void RepaintConnections()
    {
        int i;
        Grf_Connection act_con;        
        
        /**Connections neu zeichnen*/
        for(i=0;i<grc_cons.size();i++)
        {
            act_con = (Grf_Connection) grc_cons.elementAt(i);

            act_con.Grc_RecalculateBounds();
            
            act_con.repaint();
        }                       
    }

    /**RepaintNodes: Alle Knoten neu zeichnen*/
    public void RepaintNodes()
    {
        int i;
        Grf_Node act_node;        
        
        /**Connections neu zeichnen*/
        for(i=0;i<grn_nodes.size();i++)
        {
            act_node = (Grf_Node) grn_nodes.elementAt(i);
            
            act_node.repaint();
        }                       
    }
        
    /**HandleComponentMotion: Reaktion auf das Verschieben einer Komp. im Graph*/
    protected void HandleComponentMotion(Component comp_moved)
    {
        Dimension old_dim,new_dim,vp_size;
        Point act_scp,act_p,dest_p;
        
        old_dim = adm_panel.getSize();
        
        new_dim = RecalcNeededDimension();
        
        /**ggf. Adm-Panel Resize        */
        if(old_dim.equals(new_dim)==false)
        {
            /**alte Scrollposition sichern*/
            act_scp = adm_scroll.getScrollPosition();
            
            /**Panel-Resize            */
            adm_panel.setSize(new_dim);                                    
                        
            adm_scroll.doLayout();                        			                      
        }
        
        /**Scrolling pr�fen*/
               
        act_scp = adm_scroll.getScrollPosition();
        vp_size = adm_scroll.getViewportSize();
        
        dest_p = new Point(comp_moved.getLocation().x+comp_moved.getSize().width+16,
                    comp_moved.getLocation().y+comp_moved.getSize().height+16);
                    
        act_p = new Point(act_scp.x+vp_size.width,act_scp.y+vp_size.height);             
        
        /**Scrolling rechts + Offset*/
        if(dest_p.x >= act_p.x)
            adm_scroll.getHAdjustable().setValue(act_scp.x+(dest_p.x - act_p.x));

        if(dest_p.y >= act_p.y) 
            adm_scroll.getVAdjustable().setValue(act_scp.y+(dest_p.y - act_p.y));            
        
   		Set_Edited ();                        
    }
    
    /**HandleNodeMotion: Reaktion auf das Verschieben von Knoten im Graph*/
    public void HandleNodeMotion(Grf_Node node_moved)
    {
        Dimension dim;
        Grf_Label act_info;
        Grf_Connection act_con;
        int i;
                
        /**Component-Move verarbeiten*/
        HandleComponentMotion (node_moved);

        //Connections aktualisieren
        for(i=0;i<grc_cons.size();i++)
        {
            act_con = (Grf_Connection) grc_cons.elementAt(i);
            
            /**Knoten involviert?*/
            if(act_con.Grc_NodeInvolved(node_moved) == true)
            {
                act_con.Grc_RecalculateBounds();
            }
        }

        /**Node-Info verschieben        */
        if(node_info.size() >= (node_moved.Get_Node_Number()+1) )
        {
            act_info = (Grf_Label) node_info.elementAt(node_moved.Get_Node_Number());
            
            if(act_info != null)
            {
                dim = act_info.getSize();
                
                act_info.setLocation(node_moved.getLocation().x-(dim.width-node_moved.getSize().width)/2,
                                        node_moved.getLocation().y+node_moved.getSize().height+2);
            }            
        }
    }

    /**HandleLabelMotion: Reaktion auf das Verschieben von Labels im Graph*/
    public void HandleLabelMotion(Grf_EditLabel label_moved)
    {
        /**Component-Move verarbeiten*/
        HandleComponentMotion (label_moved);
    }
    
    /**RecalcNeededDimension: Berechnet die ben�tigte Gr��e des inneren Panels*/
    protected Dimension RecalcNeededDimension ()
    {
        Dimension dim,vp_size;
        
        /**Preferred Dimension berechnen*/
        dim = RecalcPreferredDimension();        
        
        /**Minimale Gr��e ist ViewPort*/
        vp_size = adm_scroll.getViewportSize();
        
        if(dim.width < vp_size.width)
            dim.width = vp_size.width;

        if(dim.height < vp_size.height)
            dim.height = vp_size.height;

        return(dim);
    }

    /**RecalcPreferredDimension: Berrechnet die bevorzugte Gr��e des aktuellen Graphen */
    public Dimension RecalcPreferredDimension ()
    {
        int i,need_width,need_height;
        Grf_Node grn;
        Grf_EditLabel grl;
        Rectangle bnds;
        
        need_width = 0;
        need_height = 0;
        
        for(i=0;i<grn_nodes.size();i++)
        {
            grn = (Grf_Node) grn_nodes.elementAt(i);
        
            bnds = grn.getBounds();
            
            if ((bnds.x+bnds.width) > need_width)
                need_width = bnds.x+bnds.width;

            if ((bnds.y+bnds.height) > need_height)
                need_height = bnds.y+bnds.height;                
        }        

        /**Labels miteinbeziehen*/
        for(i=0;i<grl_labels.size();i++)
        {
            grl = (Grf_EditLabel) grl_labels.elementAt(i);
        
            bnds = grl.getBounds();
            
            if ((bnds.x+bnds.width) > need_width)
                need_width = bnds.x+bnds.width;

            if ((bnds.y+bnds.height) > need_height)
                need_height = bnds.y+bnds.height;                
        }        

        /**Offset addieren*/
        need_width += 32;
        need_height += 32;
        
        return(new Dimension(need_width,need_height));        
    }
    
    /**Set_Weight: Neusetzen der Gewichtung einer Verbindung*/
    public void Set_Weight(int i, int j, int weight)
    {
        Set_Weight(i,j,(double)weight);    
    }
    
    public void Set_Weight(int i, int j, double weight)
    {
        Grf_Connection act_con;
        
        adm_manager.Set_Weight(i,j,weight);
        
        act_con = LookupIdentConnection(i,j);
        
        if(act_con != null)
        {
            act_con.Grc_RecalculateBounds();
    
            act_con.repaint();
        }
        
   		Set_Edited ();
    }

    /**Get_Weight: R�ckgabe der Gewichtung einer Verbindung, nach Knotennummern*/
    public double Get_Weight(int i, int j)
    {
        return(adm_manager.Get_Weight(i,j));
    }

    /**Get_Node_Typ: Node Typ des Graphen bereitstellen*/
    public int Get_Node_Typ()
    {
        return(node_typ);
    }

    /**RenumberNodes: Neunummerierung der Knoten*/
    public void RenumberNodes (Grf_Node start_node, String new_number)
    {
        int i,old_nr,high_nr,number=-1,prefix,alpha;
        Integer ival;
        Grf_Node act_node;
        
        switch(node_ident)
        {
            case Grf_Node.NODE_IDENT_NUMERIC:
                
                try
                {
                    ival = Integer.valueOf(new_number);  

                    number = ival.intValue();                    
                } 
                catch (NumberFormatException exc) {};                
                break;
                
            case Grf_Node.NODE_IDENT_ALPHA:
                
                switch(new_number.length())
                {
                    case 1:
                        
                        alpha = (int) (new_number.charAt(0)-'A');

                        number = alpha+1;
                        
                        if(number < 1)
                            number = -1;
                            
                        break;
                        
                    case 2:

                        prefix = (int) (new_number.charAt(0)-'A');                
                        alpha = (int) (new_number.charAt(1)-'A');
                        
                        number = ((prefix+1)*26)+alpha+1;

                        if(number < 1)
                            number = -1;

                        break;
                        
                    default:
                        break;
                }
                                
                break;
            
            default:
                break;
        }
        
        if(number == -1)
            return;
        
        old_nr = start_node.Get_Node_Number();
        
        if(number == old_nr)
            return;
            
        /**h�chste Nummer ermitteln und sortiertes Array herstellen*/
        high_nr = 0;
               
        for(i=0;i<grn_nodes.size();i++)
        {
            act_node = (Grf_Node) grn_nodes.elementAt(i);
            
            if(act_node.Get_Node_Number() > high_nr)
            {
                high_nr = act_node.Get_Node_Number();
            }                
        }               

        if(number > high_nr)
            return;
                    
        /**Neunummerierung durchf�hren*/
        for(i=0;i<grn_nodes.size();i++)
        {
            act_node = (Grf_Node) grn_nodes.elementAt(i);

            if(act_node.Get_Node_Number() == number)
            {
                act_node.Set_Node_Number(old_nr);                
                break;
            }
        }
        
        start_node.Set_Node_Number(number);
        
        /**Knoten neu zeichnen*/
        RepaintNodes();
        
        Set_Edited ();
    }
    
    /**LinkToGUI: verbindet Elemente eines Adj_Vector mit d. GUI*/
    protected Adj_Vector LinkToGUI (Adj_Vector adj_vec)
    {
        for(int i=1;i<=adj_vec.Size();i++)
            ((Adj_VectorItem) adj_vec.Get(i)).LinkToGUI(this);

        return(adj_vec);
    }

    /**LinkToGUI: verbindet Elemente eines Adj_ConVector mit d. GUI*/
    protected Adj_ConVector LinkToGUI (Adj_ConVector adj_con_vec)
    {
        for(int i=1;i<=adj_con_vec.Size();i++)
            ((Adj_ConVectorItem) adj_con_vec.Get(i)).LinkToGUI(this);

        return(adj_con_vec);
    }

    /**LinkToGUI: verbindet Elemente eines Id_Vector mit d. GUI*/
    protected Id_Vector LinkToGUI (Id_Vector id_vec)
    {
        id_vec.LinkToGUI(this);

        return(id_vec);
    }

    /**LinkToGUI: verbindet Elemente einer Adj-Matrix d. GUI*/
    protected Adj_Matrix LinkToGUI (Adj_Matrix adj_matrix)
    {
        adj_matrix.LinkToGUI(this);

        return(adj_matrix);
    }

    /**Adj_Manager-Methoden Aufruf*/
    public Adj_Vector DescVector (int start_node_number)
    {
        return(LinkToGUI(adm_manager.DescVector(start_node_number)));            
    }
    
    public Id_Vector DescNrVector (int start_node_number)
    {
        return(LinkToGUI(adm_manager.DescNrVector(start_node_number)));                    
    }
    
    public Adj_Vector AscVector (int start_node_number)
    {
        return(LinkToGUI(adm_manager.AscVector(start_node_number)));                    
    }

    public Id_Vector AscNrVector (int start_node_number)
    {
        return(LinkToGUI(adm_manager.AscNrVector(start_node_number)));                    
    }
    
    public Adj_Vector SrcVector ()
    {
        return(LinkToGUI(adm_manager.SrcVector()));                            
    }

    public Id_Vector SrcNrVector ()
    {
        return(LinkToGUI(adm_manager.SrcNrVector()));                            
    }

    public Adj_Vector DestVector ()
    {
        return(LinkToGUI(adm_manager.DestVector()));                            
    }

    public Id_Vector DestNrVector ()
    {
        return(LinkToGUI(adm_manager.DestNrVector()));                            
    }

    /**Knotenmenge des Graphen*/
    public Adj_Vector NodeVector ()
    {
        return(LinkToGUI(adm_manager.NodeVector()));                    
    }
    
    /**Knotennummernmenge des Graphen*/
    public Id_Vector NodeNrVector ()
    {
        return(LinkToGUI(adm_manager.NodeNrVector()));                                  
    }

    /**Kantenmenge des Graphen*/
    public Adj_ConVector ConVector (boolean asymmetric)
    {
        return(LinkToGUI(adm_manager.ConVector(asymmetric)));                    
    }

    /**Alle zyklenfreien Pfade von Quell- nach Zielknoten*/   
    public Vector PathVector (Id_Vector src_nodes, Id_Vector dest_nodes)
    {
        int i;
        Vector paths;
        Adj_ConVector act_vec;
        
        paths = adm_manager.PathVector(src_nodes,dest_nodes);
        
        for(i=0;i<paths.size();i++)
        {
            act_vec = (Adj_ConVector) paths.elementAt(i);

            LinkToGUI(act_vec);
        }
        
        return(paths);
    }

    /**GetMatrix: Berechnen der Adjazenzmatrix gewichtet oder ungewichted */
    public Adj_Matrix GetMatrix (boolean weighted)
    {
        return(LinkToGUI(adm_manager.GetMatrix(weighted)));
    }

    /**GetTransitiveCover: ermitteln der transitiven H�lle gewichted - ungewichted*/
    public Adj_Matrix GetTransitiveCover (boolean weighted)
    {
        return(LinkToGUI(adm_manager.GetTransitiveCover(weighted)));
    }
    
    /**DeleteNodeMarkers: Alle Knotenmarkierungen entfernen*/        
    public void DeleteNodeMarkers()
    {
        adm_manager.DeleteNodeMarkers();
        
        RepaintNodes();
    }

    /**DeleteConnectionMarkers: Alle Kantenmarkierungen entfernen*/    
    public void DeleteConnectionMarkers()
    {
        adm_manager.DeleteConnectionMarkers();
        
        RepaintConnections();
    }

    /**OrderNodesByVector: Knoten anhand eines Vektors sortieren*/
    public void BlockOrder(int nodes_per_row)
    {
        Adj_Vector node_nrs;
        
        node_nrs = NodeVector();
        
        BlockOrderByVector(node_nrs, nodes_per_row);
    }

    /**CircleOrder: sternf�rmige Anordnung der Knoten*/
    public void CircleOrder(int center_node_nr)
    {
        double deg,kant;
        Point pos = new Point();
        int i,sx,sy,out_cnt,zfact,anz_nodes;
        Grf_Node grn,act_grn;
        Adj_Vector node_nrs;
        Adj_Node adn;
        
        if(center_node_nr == 0)
            return;
            
        node_nrs = NodeVector();

        if(node_nrs.Size() == 0)
            return;
        
        anz_nodes = 0;
        for(i=1;i<=node_nrs.Size();i++)
        {
            grn = (Grf_Node) LookupIdentNode(node_nrs.Get(i).GetNodeNr());

            if(grn.isVisible() == true)
                anz_nodes ++;
        }
        
        deg = 360/(anz_nodes-1);

        zfact = (int) (anz_nodes/10);        
 
        /**Start-Node location setzen*/
        grn = (Grf_Node) LookupIdentNode(center_node_nr);

        grn.setLocation(NodeGridToKoord(2+zfact,2+zfact,20,30));            
        
        sx = grn.getLocation().x;        
        sy = grn.getLocation().y;                
        
        kant = sx/5*4;
        
        for (i=1,out_cnt=0;i<=node_nrs.Size();i++)
        {            
            adn = (Adj_Node) node_nrs.Get(i).GetNode();
            
            act_grn = (Grf_Node) LookupIdentNode(adn.Get_Node_Number());
            
            if(act_grn.isVisible())
            {
                if(act_grn != grn)
                {
                    pos.y = sy - (int) (Math.sin((out_cnt*deg)/180*Math.PI)*kant);
                    pos.x = sx + (int) (Math.cos((out_cnt*deg)/180*Math.PI)*kant);
                    
                    act_grn.setLocation(pos);
                    
                    out_cnt ++;
                }
            }    
        }                

        RepaintConnections();
    }
    
    /**OrderNodesByVector: Knoten anhand eines Vektors sortieren*/
    public void BlockOrderByVector(Adj_Vector vec, int nodes_per_row)
    {
        int i,row,col,item_cnt;
        Adj_VectorItem avi;
        Adj_Node adn;
        Grf_Node grn;
        
        item_cnt = 0;
        row = col = 0;
        
        for (i=1;i<=vec.size();i++)
        {
            avi = (Adj_VectorItem) vec.Get(i);
            adn = (Adj_Node) avi.GetNode();
            
            grn = (Grf_Node) LookupIdentNode(adn.Get_Node_Number());
            
            if(grn.isVisible())
            {
                if(nodes_per_row != 0)
                {
                    if( (item_cnt%nodes_per_row) == 0)
                    {
                        row ++;
                        col = 0;
                    }        
                }

                grn.setLocation(NodeGridToKoord(col,row,20,30));
                
                col ++;
                item_cnt ++;                        
            }    
        }
        
        RepaintConnections();
    }

    /**Pack: packt den Graphen zusammen, Abst�nde werden um ein drittel reduziert */
    public void Pack()
    {
        int i,dx,dy;
        int min_x,min_y;
        Grf_Node act_node;
        Grf_EditLabel act_label;
        Point loc;

        /**Nodes: Abst�nde halbieren*/
        for (i=0;i<grn_nodes.size();i++)
        {
            act_node = (Grf_Node) grn_nodes.elementAt(i);

            loc = act_node.getLocation();    
            
            if( ((int)loc.x/3*2) >= 30)
                loc.x = (int) loc.x/3*2;

            if( ((int)loc.y/3*2) >= 30)                
                loc.y = (int) loc.y/3*2;
            
            act_node.setLocation(loc);                    
        }

        /**Labels: Abst�nde halbieren*/
        for (i=0;i<grl_labels.size();i++)
        {
            act_label = (Grf_EditLabel) grl_labels.elementAt(i);

            loc = act_label.getLocation();    
            
            if( ((int)loc.x/3*2) >= 30)
                loc.x = (int) loc.x/3*2;

            if( ((int)loc.y/3*2) >= 30)                
                loc.y = (int) loc.y/3*2;
            
            act_label.setLocation(loc);                    
        }

        /**Graph verschieben*/
        min_x = adm_panel.getSize().width;
        min_y = adm_panel.getSize().height;
        
        for (i=0;i<grn_nodes.size();i++)
        {
            act_node = (Grf_Node) grn_nodes.elementAt(i);

            loc = act_node.getLocation();    
            
            if(loc.x < min_x)
                min_x = loc.x;

            if(loc.y < min_y)
                min_y = loc.y;                    
        }

        dx = 0;
        dy = 0;
        
        if(min_x != adm_panel.getSize().width)
        {
            if(min_x >= 30)
                dx = min_x-30;   
        }

        if(min_y != adm_panel.getSize().height)
        {
            if(min_y >= 30)
                dy = min_y-30;   
        }
        
        if( (dy != 0) || (dx != 0) )
        {
            /**NodeMove*/
            for (i=0;i<grn_nodes.size();i++)
            {
                act_node = (Grf_Node) grn_nodes.elementAt(i);

                loc = act_node.getLocation();    
                
                loc.x = loc.x - dx;
                loc.y = loc.y - dy;
                
                act_node.setLocation(loc);                    
            }

            /**LabelMove*/
            for (i=0;i<grl_labels.size();i++)
            {
                act_label = (Grf_EditLabel) grl_labels.elementAt(i);

                loc = act_label.getLocation();    
                
                loc.x = loc.x - dx;
                loc.y = loc.y - dy;
                
                act_label.setLocation(loc);                    
            }
        }

        //Alignment pr�fen
        if(align_grid)
            Align();

        RepaintNodes();
        RepaintInfos();            
        RepaintConnections();
        
        /**neue Gr��e berechnen, falls Frame schon eingeh�ngt*/
        
        if(getParent() != null)
            dispatchEvent(new ComponentEvent(getParent(),ComponentEvent.COMPONENT_RESIZED));                          
        
        Set_Edited();        
    }
    
    /**ReconnectByVector: Connections anhand eines Vektors einf�gen*/
    public void ReconnectByVector (Adj_Vector vec)
    {
        int i;
        
        /**Alle Connections l�schen*/
        DeleteAllConnections();   
        
        for (i=1;i<=(vec.Size()-1);i++)
        {
            ConnectNode(vec.Get(i).GetNodeNr(),vec.Get(i+1).GetNodeNr(),0);
        }        
    }
    
    /**HighliteNodesByVector: */
    public void HighliteNodesByVector(Adj_Vector vec, Color fill_color, Color text_color)
    {
        int i;
        Adj_VectorItem avi;
        Adj_Node adn;
        Grf_Node act_node;
        
        for (i=1;i<=vec.Size();i++)
        {
            avi = (Adj_VectorItem) vec.Get(i);
            adn = (Adj_Node) avi.GetNode();
            
            act_node = LookupIdentNode(adn.Get_Node_Number());      
            act_node.HighliteNode(fill_color,text_color);
            act_node.repaint();
        }
    }    

    public void UnHighliteNodesByVector(Adj_Vector vec)
    {
        int i;
        Adj_VectorItem avi;
        Adj_Node adn;
        Grf_Node act_node;
        
        for (i=1;i<=vec.size();i++)
        {
            avi = (Adj_VectorItem) vec.Get(i);
            adn = (Adj_Node) avi.GetNode();
            
            act_node = LookupIdentNode(adn.Get_Node_Number());      
            act_node.UnHighliteNode();
            act_node.repaint();
        }
    }    

    /**HighliteNode: */
    public void HighliteNode(Grf_Node grn, Color fill_color, Color text_color)    
    {
        HighliteNode(grn.Get_Node_Number(), fill_color, text_color);
    }
    
    public void HighliteNode(int number, Color fill_color, Color text_color)
    {
        Grf_Node grn;
        
        grn = LookupIdentNode(number);
        if(grn == null)
            return;
            
        grn.HighliteNode(fill_color,text_color);            
    }

    /**UnHighliteNode: */
    public void UnHighliteNode(Grf_Node grn)    
    {
        UnHighliteNode(grn.Get_Node_Number());
    }
    
    public void UnHighliteNode(int number)
    {
        Grf_Node grn;
        
        grn = LookupIdentNode(number);
        if(grn == null)
            return;
            
        grn.UnHighliteNode();            
    }

    public void UnHighliteAllNodes()
    {
        int i;
        Grf_Node grn;
        
        for(i=0;i<grn_nodes.size();i++)
        {
            grn = (Grf_Node) grn_nodes.elementAt(i);
            grn.UnHighliteNode();
        }
    }

    /**HighliteConnection: */
    public void HighliteConnection(Grf_Connection grc, Color fill_color, Color text_color)    
    {
        grc.HighliteConnection(fill_color, text_color);
    }
    
    public void HighliteConnection(int i, int j, Color fill_color, Color text_color)
    {
        Grf_Connection grc;
        
        grc = LookupIdentConnection(i,j);
        if(grc == null)
            return;
            
        grc.HighliteConnection(fill_color, text_color);
    }

    /**UnHighliteConnection: */
    public void UnHighliteConnection(Grf_Connection grc)    
    {
        grc.UnHighliteConnection();
    }
    
    public void UnHighliteConnection(int i, int j)
    {
        Grf_Connection grc;
        
        grc = LookupIdentConnection(i,j);
        if(grc == null)
            return;
            
        grc.UnHighliteConnection();
    }

    public void UnHighliteAllConnections()
    {
        int i;
        Grf_Connection grc;
        
        for(i=0;i<grc_cons.size();i++)
        {
            grc = (Grf_Connection) grc_cons.elementAt(i);
            
            grc.UnHighliteConnection();
        }    
    }
    
    /**Hilfsfunktionen*/
    public String GetString ()
    {
        return(adm_manager.GetString());
    }

    /**IsZoomable: override super*/
    public boolean IsZoomable ()
    { 
        return(true);
    }
    
    /**Set_Lock: Locking*/
    public void Set_Lock (boolean lock)
    {
        int i;
        Grf_Node grn;
        Grf_Connection gcon;
        Grf_EditLabel glabel;
        
        if(is_locked == lock)
            return;
            
        is_locked = lock;    
        
        /**Lock-State durchschleifen, Nodes*/
        for(i=0;i<grn_nodes.size();i++)
        {
            grn = (Grf_Node) grn_nodes.elementAt(i);
            grn.Set_Lock(lock);
        }    
        
        /**.. Connections*/
        for(i=0;i<grc_cons.size();i++)
        {
            gcon = (Grf_Connection) grc_cons.elementAt(i);
            gcon.Set_Lock(lock);
        }            

        /**.. Labels*/
        for(i=0;i<grl_labels.size();i++)
        {
            glabel = (Grf_EditLabel) grl_labels.elementAt(i);
            glabel.Set_Lock(lock);
        }            
        
        Set_Info();
        
        Set_Edited ();
    }
    
    /**Get_Lock: Liefert Lock-Status*/
    public boolean Get_Lock()
    {
        return(is_locked);
    }
    
    /**Has_Properties: Properties vorhanden*/
    public boolean Has_Properties () 
    { 
        return(true); 
    }            

    /**Get_Connection_Typ: Connection-Typ des Managers zur�ckgeben*/
    public int Get_Connection_Typ()
    {
        return(connection_typ);
    }
   
    /**Get_Show_Weights: Gewichtungen anzeigen*/
    public boolean Get_Show_Weights()
    {
        return(show_weights);
    }

    /**Set_Show_Weights: Gewichtungen anzeigen setzen*/
    public void Set_Show_Weights(boolean show)
    {
        if(show_weights != show)
        {
            show_weights = show;
            
            RepaintConnections();
            
            Set_Edited ();
        }
    }

    /**Get_Align_Grid: Am Raster ausrichten?*/
    public boolean Get_Align_Grid()
    {
        return(align_grid);
    }

    /**Set_Align_Grid: Am Raster ausrichten setzen*/
    public void Set_Align_Grid(boolean align)
    {
        if(align_grid != align)
        {
            align_grid = align;
            
            Set_Edited();
        }    
    }

    /**Get_Show_Connection_Caption: Kantentext ausgeben ?*/
    public boolean Get_Show_Connection_Caption()
    {
        return(show_connection_caption);
    }
    
    /**Get_Node_Ident: Node-Identifikation*/
    public int Get_Node_Ident()
    {
        return(node_ident);
    }

    /**Set_Node_Ident: Node-Identifikation setzen*/
    public void Set_Node_Ident(int ident)
    {
        if(node_ident != ident)
        {
            node_ident = ident;
            
            RepaintNodes();
            
            Set_Info();
            
            Set_Edited ();
        }
    }
    
    /**Zooming anwenden*/
    public void DoZoom()
    {        
        int i;
        Grf_Node act_node;
        Grf_Connection act_con;
        Grf_EditLabel act_label;
        
        /**Knoten-Initialgr��en aktualisieren*/
        for(i=0;i<grn_nodes.size();i++)
        {
            act_node = (Grf_Node) grn_nodes.elementAt(i);
            act_node.UpdateDimension();
        }

        /**Connections-Initialgr��en aktualisieren*/
        for(i=0;i<grc_cons.size();i++)
        {
            act_con = (Grf_Connection) grc_cons.elementAt(i);
            act_con.UpdateDimension();
        }

        /**Labels-Initialgr��en aktualisieren*/
        for(i=0;i<grl_labels.size();i++)
        {
            act_label = (Grf_EditLabel) grl_labels.elementAt(i);
            act_label.UpdateDimension();
        }

        //Grid-Lokation ggf. anpassen
        if(align_grid)
            Align();

        /**.. und neu Zeichnen*/
        RepaintNodes();
        RepaintInfos();
        RepaintConnections();        
        
        Set_Edited ();
    }

    /**RepaintInfos: Info-Labels neu berechnen und zeichnen*/
    public void RepaintInfos()
    {
        int i;
        Grf_Node act_node;
        Grf_Label act_info;
        Dimension dim;

        super.RepaintInfos();
        
        /**Node-Info*/
        for(i=1;i<node_info.size();i++)
        {
            act_info = (Grf_Label) node_info.elementAt(i);

            if(act_info != null)
            {
                /**Font-Size aktualisieren*/
                act_info.setFont(new Font("SansSerif",Font.PLAIN,(int)(font_size*GetZoom())));
                
                /**Node-Lookup*/
                act_node = LookupIdentNode(i);
                if(act_node == null)
                    return;
                    
                dim = act_info.getSize();        
                act_info.setSize(dim);            
                
                act_info.setLocation(act_node.getLocation().x-(dim.width-act_node.getSize().width)/2,
                        act_node.getLocation().y+act_node.getSize().height+2);                                                                        
            }            
        }        
    }
    
    /**IsRunnable: Kann Algorithmus gestartet werden*/
    public boolean IsRunable() 
    { 
        /**Lauff�higkeitspr�fungen*/
        if(Get_Result_Frame() == true)
            return(false);

        if(alg_class_name.equals(""))
            return(false);
                        
        return(true); 
    }
        
    /**Handle_ActionEvent: Action-Event Verarbeitung, overrides Frm_Frame*/
    public void Handle_ActionEvent (ActionEvent event)
    {
        String command;

        //Verabeitung der Superklasse ansto�en
        super.Handle_ActionEvent(event);

        //eigene Verarbteiung durchf�hren
        if(is_locked == true)
            return;        
        
        command = event.getActionCommand();
        
        /**Neuer Knoten einf�gen*/
        if(command.equals("NEW_NODE"))
        {
            AddNodeAtPoint(pt_x,pt_y);                
        }

        /**Neues Label einf�gen*/
        if(command.equals("NEW_LABEL"))
        {
            AddLabelAtPoint(pt_x,pt_y);                
        }

        /**Graph packen*/
        if(command.equals("PACK_GRAPH"))
        {
            Pack();
        }

        /**Graph ausrichten*/
        if(command.equals("ALIGN_GRAPH"))
        {
            Align();
        }

        /**Properties*/
        if(command.equals("PROP"))
        {
            prop = new Dlg_GraphProp (frm_mgr,this);                                              
        }
    }
    
    /**Grf_MouseEvent: Mouse-Event Verarbeitung*/
    class Grf_MouseEvent extends java.awt.event.MouseAdapter
    {
	    public void mouseClicked(MouseEvent event)
        {
            Handle_MouseClicked (event);
        }
    }

    public void Handle_MouseClicked (MouseEvent event)
    {
        boolean right_pressed;
        Object object = event.getSource();		
        CheckboxMenuItem cmi;
        MenuItem mi;
        int i;
        
        //Super-Methode aufrufen
        super.Handle_MouseClicked(event);

        //eigene Verabeitung    
        if(is_locked == true)
            return;
            
        right_pressed = (event.getModifiers() & InputEvent.META_MASK) != 0 ? true:false;

        /**Popup-Menu darstellen*/
   	    if(right_pressed)
   	    {            
            /**Node-Type*/
	        for(i=0;i<grn_typ_cmi.size();i++)
	        {
        		cmi = (CheckboxMenuItem)grn_typ_cmi.elementAt(i);
        		
        		if(Grf_Node.node_types[i] == node_typ)
                    cmi.setState(true);    
                else
                    cmi.setState(false);                        
            }
            
    		/**Node-Ident*/
	        for(i=0;i<grn_ident_cmi.size();i++)
	        {
        		cmi = (CheckboxMenuItem)grn_ident_cmi.elementAt(i);
    		    
    		    if(node_ident == i)
    		        cmi.setState(true);
    		    else
    		        cmi.setState(false);
    		}

   	        frm_popup.show((Component)object,event.getX(),event.getY());
   	    }
   	    
   	    /**Neuer Knoten durch doppelklicken*/
   	    if( (!right_pressed) && (event.getClickCount() == 2) )
   	    {
   	        AddNodeAtPoint(event.getX(),event.getY());
   	    }
    }
    
    /**Handle_ComponentResized: Override aus Frm_Frame*/
    public synchronized void Handle_ComponentResized (ComponentEvent event)
    {
        Container cont_panel;
        Dimension new_dim,vp_size,dim;
        
        /**�bergeordnetes Panel bereitstellen*/
        cont_panel = (Container) getParent();
        if(cont_panel == null)
            return;
            
        new_dim = RecalcNeededDimension();
         
        if( (cont_panel.getSize().equals(getSize()) == false) ||
            (adm_panel.getSize().equals(new_dim) == false) )
        {
            /**Panel-Resize*/
            setSize(cont_panel.getSize().width,cont_panel.getSize().height);        
            
            /**Scroll-Pane Resize*/
            synchronized(adm_scroll)
            {
                adm_scroll.setSize(cont_panel.getSize().width,cont_panel.getSize().height);        
            
                /**Adm-Panel Resize*/
                adm_panel.setSize(new_dim);
                                            
                /**Alg. Std. Info-Location neu setzen*/
                vp_size = adm_scroll.getViewportSize();
                
                adm_scroll.doLayout();			                                                            
                
                dim = alg_std_info.getSize();                 
                
                alg_std_info.setLocation(vp_size.width/2-dim.width/2,
                                        adm_scroll.getScrollPosition().y+
                                        vp_size.height-info_bottom_margin-
                                        dim.height);                                    
            }    
        }
    }
    
    /**Override: Handle_ItemStateChanged aus Frm_Frame*/
    public void Handle_ItemStateChanged (ItemEvent event)
    {
        CheckboxMenuItem cmi,ucmi;
        int i,ii;

        //�bergeben an Superklasse
        super.Handle_ItemStateChanged(event);
        
        //eigene Verarbeitung
        cmi = (CheckboxMenuItem) event.getSource();
        
        /**Gewichtungen anzeigen*/
        if(cmi.getActionCommand().equals("SHOW_WEIGHTS"))
        {
            Set_Show_Weights(cmi.getState());
        }

        /**Am Raster ausrichten*/
        if(cmi.getActionCommand().equals("ALIGN_GRID"))
        {
            Set_Align_Grid(cmi.getState());
        }

        /**Knotenart pr�fen*/
		for(i=0;i<Grf_Node.node_types.length;i++)
		{
            if(cmi.getActionCommand().equals("NODE_TYPE_"+Grf_Node.node_types[i]))    		    
            {                    
		        /**andere Checks l�schen*/
		        for(ii=0;ii<grn_typ_cmi.size();ii++)
		        {
            		ucmi = (CheckboxMenuItem)grn_typ_cmi.elementAt(ii);

            		if(ucmi != (CheckboxMenuItem) grn_typ_cmi.elementAt(i))
            		    ucmi.setState(false);
                }

                ChangeNodeTyp(Grf_Node.node_types[i]);
                break;
            }
		}            

        /**Knotenidentifikation pr�fen*/
        if(cmi.getActionCommand().startsWith("NODE_IDENT_"))    		    
        {   
            /**Check gel�scht -> dann Check wieder setzen*/
            if(cmi.getState() == false)
                cmi.setState(true);
                
	        /**andere Checks l�schen*/
	        for(i=0;i<grn_ident_cmi.size();i++)
	        {
        		ucmi = (CheckboxMenuItem)grn_ident_cmi.elementAt(i);

        		if(ucmi != cmi)
        		    ucmi.setState(false);
            }

            /**und setzen...*/
            if(cmi.getActionCommand().endsWith(""+Grf_Node.NODE_IDENT_NUMERIC))
                Set_Node_Ident(Grf_Node.NODE_IDENT_NUMERIC);

            if(cmi.getActionCommand().endsWith(""+Grf_Node.NODE_IDENT_ALPHA))
                Set_Node_Ident(Grf_Node.NODE_IDENT_ALPHA);                               
		}            		    
    }		
    
    /**Get_Container: Container f�r Items zur�ckgeben*/
    public Panel Get_Container()
    {
        return(adm_panel);
    }
    
    /**Using_Popup: Nutzung des Frm-Popups ?*/    
    public boolean Using_Popup() 
    {
        return(true);
    }    
    
    /**Get_ScrollPane: Aktueller Scroll-Container zur�ckgeben*/
    public ScrollPane Get_ScrollPane() 
    { 
        return(adm_scroll);
    }
    
    /**StartAlg: Startet die Ausf�hrung des hinterlegten Algorithmus*/
    public void StartAlg(int run_mode) 
    {
        /**�berhaupt Knoten vorhanden*/
        if(grn_nodes.size() == 0)
            return;
            
        //andernfalls starten    
        super.StartAlg(run_mode);    
    }

    /**RewindAlg: Spult die Ausf�hrung des hinterlegten Algorithmus zur�ck, overrideable aus Frm_Frame*/
    public void RewindAlg()     
    {
        //Super-Methode aufrufen
        super.RewindAlg();
        
        //eigene Verabeitung: Alle Highlites u. Markierungen entfernen*/
        UnHighliteAllNodes();
        UnHighliteAllConnections();
        
        DeleteNodeMarkers();
        
        DeleteAllInfo();        
    }
    
    /**Set_Info: Info-Label neu setzen*/
    protected void Set_Info ()
    {
        String info;
        Grf_Node s_node;
        
        info = "";
        
        if(!Get_Result_Frame())
        {
            switch(connection_typ)
            {
                case Adj_Connection.TypDirected:

                    info += "Digraph";
                    break;
                    
                case Adj_Connection.TypNotDirected:
                
                    info += "Undirected Graph";
                    break;
                
                default:
                    break;
            }
            
            if(alg_name.equals("") == false)
            {
                if(info.equals(""))
                    info += alg_name;
                else    
                    info += ", "+alg_name;
            
                /**Knoten vorhanden ?*/
                if(grn_nodes.size() != 0)
                {
                    s_node = LookupIdentNode(start_node_id);
                    
                    switch(node_ident)
                    {
                        case Grf_Node.NODE_IDENT_NUMERIC:
                            
                            info += " - Start Node "+s_node.Get_Node_Number();
                            break;
                            
                        case Grf_Node.NODE_IDENT_ALPHA:
                        
                            info += " - Start Node "+s_node.Get_Node_Alpha_Number();                                
                            break;
                            
                        default:
                            break;
                    }                            
                }
            }
        }
        else
        {
            info += "Result";
        }
        
        if(Get_Lock())
            if(info.equals(""))
                info += "locked";
            else
                info += " - locked";
                
        grm_info.setText(info);
    }

    /**Set_Start_Node_Id: Alg. Start-Knoten setzen*/
    public void Set_Start_Node_Id(int number)
    {
        if (start_node_id == number)
            return;
            
        start_node_id = number;

        Set_Info();
        
        Set_Edited();
    }
    
    /**Set_Alg_Name: Algortihmus setzen*/
    public void Set_Alg_Name(String name)
    {
        super.Set_Alg_Name(name);
        
        Set_Info();        
    }
    
    /**Get_Start_Node_Id: Alg. Start-Knoten zur�ckgeben*/
    public int Get_Start_Node_Id()
    {
        return(start_node_id);
    }
    
    /**DeleteAllInfo: Alle Alg. Info-Label entfernen*/
    public void DeleteAllInfo ()
    {
        int i;

        //Alg-Infos entfernen
        super.DeleteAllInfo();
        
        /**Node-Info-Labels*/
        for (i=1;i<node_info.size();i++)
        {
            /**aus Panel entfernen*/
            if(node_info.elementAt(i) != null)
            {
                adm_panel.remove((Grf_Label) node_info.elementAt(i));            
            }    
        }        
        
        /**..und Labels l�schen*/
        node_info = null;
        node_info = new Vector();        
    }
    
    /**DeleteNodeInfo: Node Info-Label entfernen*/
    public void DeleteNodeInfo (int node_number)
    {
        Grf_Label act;
        
        if( (node_info.size() < (node_number+1)) || (node_info.size() == 0) )
            return;

        try
        {
            act = (Grf_Label) node_info.elementAt(node_number);
        }
        catch(ArrayIndexOutOfBoundsException exc)
        { return; }

        /**aus Panel entfernen*/
        adm_panel.remove(act);            

        /**..und Label l�schen*/
        node_info.setElementAt(null,node_number);
    }
    
    /**SetNodeInfo: Alg. Info-Label an Knoten hinzuf�gen*/
    public void SetNodeInfo (int node_number, String info_txt)                             
    {
        this.SetNodeInfo(node_number,info_txt,true,false,null,null);
    }
    
    /**SetNodeInfo: Alg. Info-Label an Knoten hinzuf�gen*/
    public void SetNodeInfo (int node_number, String info_txt, 
                             boolean transparent_bckg, boolean framed,
                             Color backg, Color foreg)
    {
        Grf_Label act_info;        
        Grf_Node grn;        
        Dimension dim;

        /**Capacity ggf. erh�hen*/
        if(node_info.size() < (node_number+1))
            node_info.setSize(node_number+1);
        
        try
        {
            act_info = (Grf_Label) node_info.elementAt(node_number);
        }
        catch(ArrayIndexOutOfBoundsException exc)
        { return; }
        
        /**Pr�fen ob Label schon vorhanden*/
        if(act_info == null)
        {
            act_info = AddInfoInt (info_txt,transparent_bckg,framed,backg,foreg);
            if(act_info == null)
                return;

            /**und hinzuf�gen, anzeigen*/
            node_info.setElementAt(act_info,node_number);                                
            adm_panel.add(act_info,0);                        
        }
        else
        {
            act_info = (Grf_Label) node_info.elementAt(node_number);
            act_info.setLabel(info_txt);
        }    
            
        /**Location setzen*/
        grn = LookupIdentNode(node_number);
        if(grn == null)
            return;
        
        dim = act_info.getSize();        
        
        act_info.setLocation(grn.getLocation().x-(dim.width-grn.getSize().width)/2,
                grn.getLocation().y+grn.getSize().height+2);
    }    

    /**SetInfo: Alg. Info-Label hinzuf�gen, 1 basierte identification*/
    public void SetInfo (int identification, int x, int y, String info_txt)
    {
        this.SetInfo(identification,x,y,info_txt,true,false,null,null);
    }
    
    /**GetId: Identificator eines Knoten zur�ckgeben*/
    public String GetId (int number)
    {
        Grf_Node grn;
        grn = LookupIdentNode(number);
        if(grn == null)
            return("");
        
        return(grn.GetId());
    }
        
    /**Get_Adm_Manager: R�ckgabe des Adjazenzlisten-Managers*/
    public Adj_Manager Get_Adm_Manager ()
    {
        return(adm_manager);
    }    
        
    /**Get_DrawGraphics: R�ckgabe des Zeichen-Graphics*/
    public Graphics Get_DrawGraphics ()
    {
        return(adm_panel.getDblGraphics());
    }        
}

