package visualgraph.swing;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import visualgraph.core.Adj_Node;

/**GUI-Klasse f�r die Knoten des Graphen*/
public class Grf_Node extends java.awt.Component
{   
    /**Node-Types Konstanten*/
    
    public static final int SIMPLE_GRAPH = 0;
    public static final int EVENT_GRAPH  = 1;
    public static final int TODO_GRAPH   = 2;

    public static final int node_types[] = {SIMPLE_GRAPH,EVENT_GRAPH,TODO_GRAPH};
        
    public static final String node_type_names[] = {
                                    "Standard Graph",
                                    "Event Graph",
                                    "Action Graph"
                                                   };
                                                    
    public static final Dimension node_type_dimensions[] = {
                                    new Dimension(20,20),
                                    new Dimension(20,20),
                                    new Dimension(90,80)
                                                           };
                                                           
    protected static final int node_type_font_size[] = {
                                    9,
                                    9,
                                    9
                                                           };
    
    /**Node Identification*/
    public static final int NODE_IDENT_NUMERIC  = 0;
    public static final int NODE_IDENT_ALPHA    = 1;

    /**interaktive Editier-Modi*/
    protected static final int EDIT_NONE      = 0;
    protected static final int EDIT_NUMBER    = 1;
    protected static final int EDIT_CAPTION   = 2;
    protected static final int EDIT_WEIGHT    = 3;
    
    /**Node-Typ*/
    protected int node_typ;
    
    /**Verweis auf Adj_Node*/
    protected Adj_Node   adn_node;

    /**Verweis auf Graph-Manager*/
    protected Grf_Manager grm;
    
    /**Hilfsvariablen f�r MouseEvent-Verarbeitung*/
    protected Grf_Connection new_con;
    
    /**Locking*/
    protected boolean is_locked;
    
    /**Eingabe-TextArea*/
    protected TextArea w_text;
    
    /**aktueller Editier-Modus*/
    protected int edit_mode;
    
    /**Popup*/
    protected PopupMenu grn_popup;
    
    /**Action-Listener-Objects*/
    protected Grf_ButtonAction grn_button_action;

    /**Highlitening*/
    protected transient boolean highlite;
    protected transient Color fill_color;
    protected transient Color text_color;    

    /**Property-Dialog*/
    protected transient Dlg_NodeProp prop;
    
    static final long serialVersionUID = 476059938529429544L;        
    
    /**PrepareSerializedObject: Serialisierungsverarbeitung*/
    public void PrepareSerializedObject(int prepare_mode) 
    {
        switch(prepare_mode)
        {
            case Frs_Manager.SER_BEFORE_WRITE:
                remove(grn_popup);
                
          		/**Property-Dialog noch ge�ffnet -> schlie�en*/
        		if(prop != null)        		
        		{
        		    prop.Remove();
        		}    
                break;
                
            case Frs_Manager.SER_AFTER_WRITE:
                add(grn_popup);                    
                break;
                
            case Frs_Manager.SER_AFTER_READ:
                add(grn_popup);                    
                
                /**neue Listeners hinzuf�gen*/
          		addMouseListener(new Grf_NodeMouse());
          		addMouseMotionListener(new Grf_NodeMouseMotion());			
                break;
        }        
    }

    /**Konstruktor*/
    public Grf_Node(Grf_Manager gr_mgr)
    {        
        MenuItem mi;
        
        /**Graph-Manager einh�ngen*/
        grm = gr_mgr;

        /**Node-Typ setzen*/
        node_typ = grm.Get_Node_Typ();
                
        /**Initial-Size setzen*/
        setSize(InitialDimension());      
        
        /**Font-Setzen*/
        setFont(InitialFont());
        
        /**Event-Listeners erzeugen*/
        grn_button_action = new Grf_ButtonAction();
        
  		addMouseListener(new Grf_NodeMouse());
  		addMouseMotionListener(new Grf_NodeMouseMotion());			

        /**Einagbe-TextArea erzeugen*/
        edit_mode = EDIT_NONE;
                
		/**Popup-Menu einrichten*/
		grn_popup = new PopupMenu();
		
    	mi = new MenuItem("Delete Node");
		mi.addActionListener(grn_button_action);
		mi.setActionCommand("DEL_NODE");		
        grn_popup.add(mi);

		grn_popup.addSeparator();
		
    	mi = new MenuItem("First Node");
		mi.addActionListener(grn_button_action);
		mi.setActionCommand("FIRST_NODE");		
        grn_popup.add(mi);

    	mi = new MenuItem("Set Start Node");
		mi.addActionListener(grn_button_action);
		mi.setActionCommand("START_NODE");		
        grn_popup.add(mi);
        
    	mi = new MenuItem("-");
		grn_popup.add(mi);
		
		mi = new MenuItem("Properties");
		mi.addActionListener(grn_button_action);
		mi.setActionCommand("PROP");		
		grn_popup.add(mi);
		
		add(grn_popup);		
	}    
        
    /**Zugriffsfunktionen*/
    
    /**Set_Adn_Node: Setzen der Verkn�pfung nach Adj_Node*/
    public void Set_Adn_Node(Adj_Node adn)
    {
        adn_node = adn;
    }

    /**Get_Adn_Node: R�ckgabe d. Verkn�pfung nach Adj_Node*/
    public Adj_Node Get_Adn_Node()
    {
        return(adn_node);
    }
            
    /**Get_Node_Number: Gibt die Node-Number des Knotens zur�ck*/
    public int Get_Node_Number()
    {
        return(adn_node.Get_Node_Number());
    }

    /**Get_Node_Alpha_Number: gibt alphabetische Nummer zur�ck*/
    public String Get_Node_Alpha_Number()
    {
        return(adn_node.Get_Node_Alpha_Number());
    }
    
    /**Set_Node_Number: Setzt die Node-Number des Knotens*/
    public void Set_Node_Number(int number)
    {
        adn_node.Set_Node_Number(number);

        grm.Set_Edited();
    }

    /**Set_Node_Caption: Node Bezeichnung setzen*/
    public void Set_Node_Caption (String caption)
    {
        adn_node.Set_Node_Caption(caption);

        grm.Set_Edited();
    }

    /**Get_Node_Caption: */
    public String Get_Node_Caption ()
    {
        return(adn_node.Get_Node_Caption());
    }
    
    /**Set_Node_Weight: Knotengewicht setzen*/
    public void Set_Node_Weight (double val)
    {
        adn_node.Set_Node_Weight(val);

        grm.Set_Edited();
    }

    /**Get_Node_Weight: Knotengewicht zur�ckgeben*/
    public double Get_Node_Weight ()
    {
        return(adn_node.Get_Node_Weight());
    }

    /**Set_Lock: Locking setzen*/
    public void Set_Lock (boolean lock)
    {
        is_locked = lock;
    }

    /**SetMarker: Kennzeichnet Node als markiert*/
    public void SetMarker ()
    {
        adn_node.SetMarker();

        /**Knoten neu zeichnen*/
        repaint();
    }
    
    /**DeleteMarker: Kennzeichnet Node als nicht markiert*/
    public void DeleteMarker()
    {
        adn_node.DeleteMarker();

        /**Knoten neu zeichnen*/
        repaint();        
    }

    /**IsMarked:*/
    public boolean IsMarked()
    {
        return(adn_node.IsMarked());
    }    
 
    /**Mouse-Motion-Listener-Adapter*/
  	class Grf_NodeMouseMotion extends MouseMotionAdapter 
	{
		public void mouseDragged(MouseEvent e)
		{
		    Handle_MouseDragged (e);
		}
	}

    /**GetId: Identificator eines Knoten zur�ckgeben*/
    public String GetId ()
    {
        String ident;
        
        ident = "";
        
        switch(grm.Get_Node_Ident())
        {
            case NODE_IDENT_NUMERIC:
                ident = ""+Get_Node_Number();
                break;
                
            case NODE_IDENT_ALPHA:
                ident = Get_Node_Alpha_Number();                                
                break;
                
            default:
                break;
        }                            
        return(ident);
    }

    public void Handle_MouseDragged (MouseEvent e)
    {
	    Grf_Node grn;		   		    
        int xm,ym;
        boolean shift_pressed;
        Point grid_loc,new_loc;
        
		grn = (Grf_Node) e.getSource();
		
		shift_pressed = (e.getModifiers() & InputEvent.SHIFT_MASK)!=0 ? 
                        true:false;
		
        if(!shift_pressed)
        {
            if(is_locked == true)
                return;
            
            /**Neue Connection aufziehen*/
            if(new_con == null)
            {
                new_con = new Grf_Connection(grm);
                new_con.Grc_Set_Grf_Nodes(grn,null,null);
                
                /**und einh�ngen*/
                getParent().add(new_con);
            }
            
            new_con.Grc_RecalculateBoundsByDestPoint(grn.getLocation().x+e.getX(),
                                                        grn.getLocation().y+e.getY());    
        }

		if(shift_pressed)
		{
		    xm = (int) grn.getSize().width/2;
		    ym = (int) grn.getSize().height/2;

	        //Grid-Alignment pr�fen
	        if(!grm.Get_Align_Grid())
	        {		            
    		    if ( (grn.getLocation().x+e.getX()-xm >= 0) &&
    		         (grn.getLocation().y+e.getY()-ym >= 0) )
    		    {   
    	    		/**Node-Move relativ zu Dragging verschieben*/
                    grn.setLocation(grn.getLocation().x+e.getX()-xm,
                                    grn.getLocation().y+e.getY()-ym);            			             

                    /**Node-Move verarbeiten, ScrollPane-Size evtl. �ndern*/
                    grm.HandleNodeMotion(grn);                                    
                }                    
            }	    
            else
            {
    		    if ( (grn.getLocation().x+e.getX() >= 0) &&
    		         (grn.getLocation().y+e.getY() >= 0) )
    		    {   
                    //Grid-Snapping                
                    grid_loc = grm.NodeKoordToGrid (grn.getLocation().x+e.getX(),
                                    grn.getLocation().y+e.getY(), 0,0);
                
                    new_loc = grm.NodeGridToKoord (grid_loc.x,grid_loc.y,0,0);
                
                    grn.setLocation(new_loc.x,new_loc.y);

                    /**Node-Move verarbeiten, ScrollPane-Size evtl. �ndern*/
                    grm.HandleNodeMotion(grn);                                                    
                }    
            }
        }            
    }
    
    /**Mouse-Listener-Adapter*/
  	class Grf_NodeMouse extends MouseAdapter 
	{
		public void mouseClicked(MouseEvent e)
		{
		    Handle_MouseClicked (e);
   		}
		public void mouseReleased(MouseEvent e)
		{
            Handle_MouseReleased(e);		    
        }
    }

    public void Handle_MouseClicked (MouseEvent e)
    {
	    Grf_Node grn;		   		    
	    Rectangle rect,vgl_rect;
	    boolean right_pressed;
	    Double dbl;
	    
        if(is_locked == true)
            return;

        right_pressed = (e.getModifiers() & InputEvent.META_MASK) != 0 ? true:false;

		grn = (Grf_Node) e.getSource();            
		
        if(!right_pressed)
        {                            
            /**falls neue Connection noch vorhanden -> l�schen*/
            if(new_con != null)
            {
                getParent().remove(new_con);            
                
                new_con = null;
            }
            
            /**Evtl. Eingabe-Field anzeigen*/
            if(e.getClickCount() == 2)
            {
               /**Eingabe-Text-Field erzeugen und positionieren*/
               w_text = new TextArea("",1,1,TextArea.SCROLLBARS_NONE);
               w_text.setFont(getFont());        
               w_text.setVisible(false);
               
               w_text.addKeyListener(new Grf_TextAreaKeyAdapter());        
               w_text.addFocusListener(new Grf_TextAreaFocusAdapter());

               /**...und anzeigen */
               grm.Get_Container().add(w_text);               
                                            
               switch(node_typ)
               {
                    case SIMPLE_GRAPH: 
                    case EVENT_GRAPH:

                        edit_mode = EDIT_NUMBER;
                        
                        w_text.setSize(getSize().width-2,getSize().height-2);
                        w_text.setLocation(getLocation().x+1,getLocation().y+1);

                        switch(grm.Get_Node_Ident())
                        {
                            case NODE_IDENT_NUMERIC:
                                w_text.setText(""+Get_Node_Number());
                                break;
                                
                            case NODE_IDENT_ALPHA:
                                w_text.setText(Get_Node_Alpha_Number());                                
                                break;
                                
                            default:
                                break;
                        }                            
                        
                        w_text.setVisible(true);
                        
                        /**Focus-request*/
                        w_text.requestFocus();
                        w_text.selectAll();                                            
                        break;

                    case TODO_GRAPH:
                        
                        /**Pr�fung auf Nummer*/
                        vgl_rect = new Rectangle(1,1,3*getSize().width/8-1,getSize().height/5-1);
                        
                        if(vgl_rect.contains(e.getX(),e.getY()))
                        {
                            edit_mode = EDIT_NUMBER;

                            vgl_rect.setLocation(1+getLocation().x,1+getLocation().y);                            
                            w_text.setBounds(vgl_rect);

                            switch(grm.Get_Node_Ident())
                            {
                                case NODE_IDENT_NUMERIC:
                                    w_text.setText(""+Get_Node_Number());
                                    break;
                                    
                                case NODE_IDENT_ALPHA:
                                    w_text.setText(Get_Node_Alpha_Number());                                
                                    break;
                                    
                                default:
                                    break;
                            }                            
                            
                            w_text.setVisible(true);
                            
                            /**Focus-request*/
                            w_text.requestFocus();
                            w_text.selectAll();                                                                                                            
                        }
                        
                        /**Pr�fung auf Gewichtung*/
                        vgl_rect = null;
                        
                        vgl_rect = new Rectangle(1,4*getSize().height/5+1,3*getSize().width/8-1,getSize().height/5-2);
                        
                        if(vgl_rect.contains(e.getX(),e.getY()))
                        {
                            edit_mode = EDIT_WEIGHT;

                            vgl_rect.setLocation(1+getLocation().x,getLocation().y+4*getSize().height/5+1);                                                        
                            w_text.setBounds(vgl_rect);
                       
                            dbl = Double.valueOf(Get_Node_Weight());
                            
                            w_text.setText(dbl.toString());

                            w_text.setVisible(true);
                            
                            /**Focus-request*/
                            w_text.requestFocus();
                            w_text.selectAll();                                                                                                            
                        }

                        /**Pr�fung auf Bezeichnung*/
                        vgl_rect = null;
                        
                        vgl_rect = new Rectangle(1,getSize().height/5,grn.getSize().width-2,3*grn.getSize().height/5);
                                                                                
                        if(vgl_rect.contains(e.getX(),e.getY()))
                        {
                            edit_mode = EDIT_CAPTION;

                            vgl_rect.setLocation(1+getLocation().x,getLocation().y+getSize().height/5);                                                                                    
                            w_text.setBounds(vgl_rect);
                       
                            w_text.setText(Get_Node_Caption());
                            
                            w_text.setVisible(true);                                                            
                            
                            /**Focus-request*/
                            w_text.requestFocus();
                            w_text.selectAll();                                                                                                            
                        }                            
                        break;
                        
                    default:
                        edit_mode = EDIT_NONE;
               }    
            }
        }
        
        /**Popup-Menu darstellen       	    */
   	    if(right_pressed)
   	    {
   	        grn_popup.show((Component)grn,e.getX(),e.getY());
   	    }
    }

    public void Handle_MouseReleased (MouseEvent e)
    {
	    Rectangle clip;

        if(is_locked == true)
            return;
		
//		(Grf_Node) e.getSource();
        
        /**Knoten neu zeichnen*/
        repaint();
        
        /**falls neue Verbindung noch vorhanden -> l�schen*/
        if(new_con != null)
        {
            /**Ggf. Pr�fen, ob Ziel sich innerhalb eines Knoten      */
            /**befindet -> neue Verbindung erstellen*/

            grm.ConnectNodeByInteraction(new_con.Grc_Get_Src_Node(), getLocation().x+e.getX(),
                                                        getLocation().y+e.getY());
            
            getParent().remove(new_con);            

            /**geclippt Container neu zeichnen*/
            clip = new_con.getBounds();
            getParent().repaint(clip.x,clip.y,clip.width,clip.height);
 
            /**und l�schen*/
            new_con = null;
        }
    }
    
    /**Grf_ButtonAction: PopupMenu Action-Verabeitung  */
	class Grf_ButtonAction implements java.awt.event.ActionListener, Serializable
	{
		private static final long serialVersionUID = 1204393966718296944L;

		public void actionPerformed(java.awt.event.ActionEvent event)
		{
            Handle_ActionPerformed(event);		    
        }       
        protected void writeObject(java.io.ObjectOutputStream out) throws IOException
        {
            out.defaultWriteObject();
        }
        protected void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
        {
            in.defaultReadObject();        
        }        
    }   

    public void Handle_ActionPerformed (ActionEvent event)
    {
	    String command;
		Object object = event.getSource();
        
        if(is_locked == true)
            return;
        
        command = event.getActionCommand();
        
        /**Eigenschaften*/
        if(command.equals("PROP"))
        {
            prop = new Dlg_NodeProp (grm, this);
        }
        
        /**Knoten l�schen*/
        if(command.equals("DEL_NODE"))
        {
            grm.DeleteNode(Get_Node_Number());
        }

        /**Startknoten setzen*/
        if(command.equals("START_NODE"))
        {
            grm.Set_Start_Node_Id(Get_Node_Number());
        }

        /**Erster Knoten setzen*/
        if(command.equals("FIRST_NODE"))
        {
            switch(grm.Get_Node_Ident())
            {
                case Grf_Node.NODE_IDENT_NUMERIC:

                    grm.RenumberNodes(Grf_Node.this,"1");                    
                    break;
                    
                case Grf_Node.NODE_IDENT_ALPHA:
      
                    grm.RenumberNodes(Grf_Node.this,"A");                    
                    break;
            }
        }
    }
    
    /**KeyEvent-Handling TextArea    */
    class Grf_TextAreaKeyAdapter extends KeyAdapter
    {
         public void keyPressed(KeyEvent e)
         {
            Handle_TextAreaKeyEvent(e);            
         }         
    }
    
    public void Handle_TextAreaKeyEvent(KeyEvent e)
    {
        TextArea act_text;
        boolean dispatch;
        
        act_text = (TextArea) e.getSource();

        dispatch = false;
        
        switch(edit_mode)
        {
            case EDIT_NUMBER:
            case EDIT_WEIGHT:

                if( (e.getKeyCode() == KeyEvent.VK_TAB) || (e.getKeyCode() == KeyEvent.VK_ESCAPE) ||
                    (e.getKeyCode() == KeyEvent.VK_ENTER) )                    
                    dispatch = true;

                break;
                
            case EDIT_CAPTION:

                if( (e.getKeyCode() == KeyEvent.VK_TAB) || (e.getKeyCode() == KeyEvent.VK_ESCAPE) )
                    dispatch = true;
                    
                break;
                
            default:
                dispatch = true;
                break;
        }
        
        if(dispatch)
            act_text.dispatchEvent(new FocusEvent(act_text,FocusEvent.FOCUS_LOST));                
        
        grm.Set_Edited();
    }
    
    /**FocusEvent-Handling TextArea*/
    class Grf_TextAreaFocusAdapter extends FocusAdapter 
    {
        public void focusLost(FocusEvent e)
        {
            Handle_TextAreaFocusLost(e);
        }
    }

    public void Handle_TextAreaFocusLost (FocusEvent e)
    {
        TextArea act_text;        
        double d_val;
        
        /**TextField und Label switchen*/
        act_text = (TextArea) e.getSource();

        switch(edit_mode)
        {
            case EDIT_NUMBER:

                /**neusetzen der Node-Nummern*/
                grm.RenumberNodes (this,act_text.getText());                      
                break;
                
            case EDIT_WEIGHT:

                try
                {
                    d_val = Double.valueOf(act_text.getText()).doubleValue();

                    /**neusetzen der Gewichtung*/
                    Set_Node_Weight(d_val);
                }            
                catch(NumberFormatException exc) {};            
                break;
                
            case EDIT_CAPTION:
            
                Set_Node_Caption(act_text.getText());
                break;

            default:
                break;
        }
        
        edit_mode = EDIT_NONE;
        
        /**TextArea entfernen*/
        grm.Get_Container().remove(w_text);        
        
        /**und neu zeichnen*/
        repaint();
    }
    
    /**InitialDimension: Initialisierungsgr��e des Knoten bereitstellen*/
    protected Dimension InitialDimension ()
    {
        Dimension new_dim = new Dimension();

        new_dim.width  = (int) (node_type_dimensions[node_typ].width*grm.GetZoom());
        new_dim.height = (int) (node_type_dimensions[node_typ].height*grm.GetZoom());                

        return(new_dim);
    }

    /**InitialFont: Initialisierungsgr��e Fonts zur�ckgeben*/
    protected Font InitialFont()
    {
        return(new Font("SansSerif",Font.PLAIN,(int) 
                    (node_type_font_size[node_typ]*grm.GetZoom())));     
    }    
    
    /**InitialFont: Initialisierungsgr��en aktualisieren*/
    public void UpdateDimension()
    {   
        /**Location aktualisieren*/
        setLocation((int) (getLocation().x*(1/grm.GetLastZoom())*grm.GetZoom()),
                        (int) (getLocation().y*(1/grm.GetLastZoom())*grm.GetZoom()));        

        /**Initial-Size setzen*/
        setSize(InitialDimension());      
        
        /**Font-Setzen*/
        setFont(InitialFont());       
    }
    
    /**ChangeNodeTyp: Knotentyp ver�ndern*/
    public void ChangeNodeTyp(int new_type)
    {
        /**Noch im Edit-Modus ? dann raus*/
        if( (edit_mode != EDIT_NONE) && (w_text != null) )
            w_text.dispatchEvent(new FocusEvent(w_text,FocusEvent.FOCUS_LOST));
        
        /**Node-Typ setzen*/
        node_typ = new_type;

        /**Font-Setzen*/
        setFont(InitialFont());
        
        /**neue Gr��e setzen*/
        setSize(InitialDimension()); 
        
        /**neu zeichnen*/
        repaint();
    }

    /**HighliteNode*/
    public void HighliteNode(Color fill_color, Color text_color)
    {
        highlite = true;
        
        if(fill_color != null)
            this.fill_color = fill_color;
        else
            this.fill_color = Color.white;            
            
        if(text_color != null)
            this.text_color = text_color;        
        else    
            this.text_color = Color.black;                
        
        repaint();    
    }
    
    /**UnHighliteNode*/
    public void UnHighliteNode()
    {
        highlite = false;
        
        repaint();
    }
    
    /**PaintNodeInt: zeichnen des Knoten*/
    protected void PaintNodeInt (Graphics g)
    { 
        int i,row,anz_rows,t_width,t_height,i_weight;
        Rectangle rect;
        String s="",out_s;
        FontMetrics fm;
        double f_weight;
        Color txt_color;
        
        rect = getBounds();
        g.clearRect(0,0,rect.width-1,rect.height-1);              
        g.setFont(getFont());
        
    	fm = getFontMetrics(getFont());
        //fm = getToolkit().getFontMetrics(getFont());
         
        /**Highlite �berpr�fen*/
        if(highlite) 
            txt_color = text_color;                    
        else
            txt_color = Color.black;
        
        switch(node_typ)
        {            
            case SIMPLE_GRAPH: 

                /**Highlite �berpr�fen*/
                if(highlite) 
                {
                    g.setColor(fill_color);        
                    g.fillOval(0,0,rect.width-1,rect.height-1);
                }
                
                g.setColor(Color.black);        
                g.drawOval(0,0,rect.width-1,rect.height-1);
 
                switch(grm.Get_Node_Ident())
                {
                    case NODE_IDENT_NUMERIC:
                        s = ""+Get_Node_Number();                                        
                        break;
                        
                    case NODE_IDENT_ALPHA:
                        s = Get_Node_Alpha_Number();            
                        break;
                        
                    default:
                        s = "";
                }

                t_width = fm.stringWidth(s);
                t_height = fm.getAscent();

                g.setColor(txt_color);                        
                g.drawString(s,rect.width/2-t_width/2,rect.height/2+t_height/2-1);

                break;
                
            case EVENT_GRAPH:        

                g.clearRect(0,0,rect.width-1,rect.height-1);

                /**Highlite �berpr�fen            */
                if(highlite) 
                {
                    g.setColor(fill_color);        
                    g.fillOval(0,0,rect.width-1,rect.height-1);
                }
                
                g.setColor(Color.black);        
                g.drawOval(0,0,rect.width-1,rect.height-1);
                
                switch(grm.Get_Node_Ident())
                {
                    case NODE_IDENT_NUMERIC:
                        s = ""+Get_Node_Number();                                        
                        break;
                        
                    case NODE_IDENT_ALPHA:
                        s = Get_Node_Alpha_Number();            
                        break;
                        
                    default:
                        s = "";
                }
                
                t_width = fm.stringWidth(s);
                t_height = fm.getAscent();

                g.setColor(txt_color);                        
                g.drawString(s,rect.width/2-t_width/2,rect.height/2+t_height/2-1);
            
                break;

            case TODO_GRAPH:

                /**Highlite �berpr�fen            */
                if(highlite) 
                {
                    g.setColor(fill_color);        
                    g.fillRect(0,0,rect.width-1,rect.height-1);  
                }
                
                g.setColor(Color.black);        
                g.drawRect(0,0,rect.width-1,rect.height-1);  
                
                /**unterteilen*/
                g.drawLine(0,rect.height/5,rect.width-1,rect.height/5);
                g.drawLine(0,4*rect.height/5,rect.width-1,4*rect.height/5);                

                g.drawLine(3*rect.width/8,0,3*rect.width/8,rect.height/5);
                g.drawLine(3*rect.width/8,4*rect.height/5,3*rect.width/8,rect.height);                
                
                /**Knotennummer zeichnen*/
                switch(grm.Get_Node_Ident())
                {
                    case NODE_IDENT_NUMERIC:
                        s = ""+Get_Node_Number();                                        
                        break;
                        
                    case NODE_IDENT_ALPHA:
                        s = Get_Node_Alpha_Number();            
                        break;
                        
                    default:
                        s = "";
                }
                
                t_width = fm.stringWidth(s);
                t_height = fm.getAscent();

                g.setColor(txt_color);                        
                g.drawString(s,(3*rect.width/8)/2-t_width/2,(rect.height/5)/2+t_height/2-1);

                /**Knotengewicht zeichnen*/
                f_weight = Get_Node_Weight();
                i_weight = (int)f_weight;
            
                /**Nachkommastellen vorhanden ?*/
                if( ((double)i_weight) == f_weight)
                    s = ""+i_weight;
                else
                    s = ""+f_weight;                    
                
                t_width = fm.stringWidth(s);
                t_height = fm.getAscent();

                g.setColor(txt_color);                        
                g.drawString(s,(3*rect.width/8)/2-t_width/2,4*rect.height/5+(rect.height/5)/2+t_height/2-1);
                
                /**Bezeichnung ausgeben*/
                s = Get_Node_Caption();
                s = s.replace('\n',' ');

                if(!s.equals(""))
                {                
                    t_width = fm.stringWidth(s);
                    t_height = fm.getAscent();
                    
                    /**Zeilenumbruch berechnen*/
                    if(t_width > (rect.width-4))
                    {
                        anz_rows = (int) (t_width / (rect.width-4))+1;
                        
                        if(anz_rows >= 4)
                            anz_rows = 3;
                            
                        i=0;
                        
                        for(row=0;row<anz_rows;row++)
                        {
                            out_s = "";
                            
                            for (;i<s.length();i++)
                            {                                
                                out_s = out_s + s.charAt(i);
                                
                                t_width = fm.stringWidth(out_s);                                
                                
                                if( (t_width >= (rect.width-10)) || ((i+1) == s.length()) )
                                {
                                    g.setColor(txt_color);                                      
                                    g.drawString(out_s,rect.width/2-t_width/2,(row+1)*(rect.height/5)+t_height+1);                                                        
                                 
                                    i++;
                                    break;
                                }
                            }
                        }
                    }                
                    else
                    {
                        g.setColor(txt_color);                        
                        g.drawString(s,rect.width/2-t_width/2,rect.height/2+t_height/2-1);                
                    }
                }                
                break;
                
            default:
                break;
        }        
        
        /**Marker �berpr�fen*/
        if(adn_node.IsMarked() == true)
        {
            s = "*";
            t_width = fm.stringWidth(s);
            t_height = fm.getAscent();
            
            g.setColor(Color.blue);        
            g.fillRect(1,1,t_width+1,t_height-1);                                                   
            
            g.setColor(Color.white);
            g.drawString(s,1,t_height);                                
        }
    }
    
    /**GUI-Override Paint*/
    public void paint(Graphics g)
    { 
        Rectangle bnds;       
 
        /**aktuelle Gr��e bereitstellen*/
        bnds = getBounds();
        //getToolkit().getFontMetrics(getFont());
       
        /**Drawing in Buffer bereitstellen*/
        g = grm.Get_DrawGraphics().create(bnds.x,bnds.y, bnds.width, bnds.height);
        
        /**interne Zeichenroutine ansto�en*/
        PaintNodeInt(g);       
                            
        /**Paint-Overridable*/
        switch(node_typ)
        {
           case SIMPLE_GRAPH: 
                break;
            
           case EVENT_GRAPH:        
                break;
                
           case TODO_GRAPH:        
                break;
        }          
    }    
}
