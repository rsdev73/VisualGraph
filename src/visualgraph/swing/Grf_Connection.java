package visualgraph.swing;
import java.awt.*;
import visualgraph.core.Adj_Connection;
import visualgraph.util.Uti_MultiLabel;

import java.awt.event.*;
import java.io.*;

/**Grf_Connection: Connection-Element zwischen Knoten*/
public class Grf_Connection extends java.awt.Component
{
    /**GUI-Konstanten*/
    protected static final int arrow_width    = 6;   /**Pfeilspizenbreite*/
    protected static final double arrow_deg   = 45*Math.PI/180; /**45 Grad in Radiant*/
    protected static final int font_size      = 9; /**Schriftgr��e*/
    
    /**interaktive Editier-Modi*/
    protected static final int EDIT_NONE      = 0;
    protected static final int EDIT_WEIGHT    = 1;
    protected static final int EDIT_CAPTION   = 2;

    /**aktueller Editier-Modus*/
    protected int edit_mode;

    /**Verbindungsart*/
    protected int connection_typ;

    /**Verweis auf Graph-Manager*/
    protected Grf_Manager grm;

    /**Verweis auf Ursprungsknoten*/
    protected Grf_Node src_node;
    
    /**Verweis auf Zielknoten*/
    protected Grf_Node dest_node;

    /**Verweis auf Adj_Connection*/
    protected Adj_Connection adc_con;
    
    /**Linienrechteck und Pfeilspitzenpolygon, Gewichtungstext*/
    protected Point line_src, line_dest;
    protected Polygon arr_head;
    
    /**Weight-Label, Caption-Label*/
    protected Grf_Label  lw_text;
    protected Grf_Label  lc_text;

    /**Editier-TextArea*/
    protected TextArea   ed_text;
    
    protected Font  text_ft;
    protected FontMetrics  text_ft_metrics;

    protected int used_arr_width;     /**verwendete Pfeilspizenbreite    */
    protected int min_text_width;     /**min. Textfeld-Breite*/
    protected int min_text_height;    /**min. Textfeld-H�he*/
    
    /**Richtung: positiv oder negativ, Quadrant*/
    protected int quadrant;

    /**Popup*/
    protected PopupMenu grc_popup;
    
    /**Action-Listener-Object*/
    protected Grc_ActionAdapter  grc_button_action;
            
    /**Locking*/
    protected boolean is_locked;

    /**Highlitening*/
    protected transient boolean highlite;
    protected transient Color fill_color;
    protected transient Color text_color;    

    static final long serialVersionUID = -5114535605058514529L;
    
    /**PrepareSerializedObject: Serialisierungsverarbeitung*/
    public void PrepareSerializedObject(int prepare_mode) 
    {
        switch(prepare_mode)
        {
            case Frs_Manager.SER_BEFORE_WRITE:
                remove(grc_popup);        
                break;
                
            case Frs_Manager.SER_AFTER_WRITE:
                add(grc_popup);        
                break;
                
            case Frs_Manager.SER_AFTER_READ:
                add(grc_popup);    

                /**neue Listener hinzuf�gen*/
                lw_text.addMouseListener(new Grf_LabelMouseEvent());
                lc_text.addMouseListener(new Grf_LabelMouseEvent());                
                
                break;
        }        
    }

    /**Konstruktor*/
    public Grf_Connection(Grf_Manager gr_mgr)
    {
        MenuItem mi;
        
        /**Graph-Manager einh�ngen*/
        grm = gr_mgr;
        
        /**Connection-Typ setzen*/
        connection_typ = grm.Get_Connection_Typ();
        
        /**Linien-Punkte erstellen*/
        line_src = new Point();
        line_dest = new Point();
                
        /**Label erzeugen und einh�ngen*/
        lw_text = new Grf_Label (grm,"",Uti_MultiLabel.LEFT);
        lw_text.addMouseListener(new Grf_LabelMouseEvent());
        grm.Get_Container().add(lw_text);
       
        /**Caption-Label erzeugen und einh�ngen*/
        lc_text = new Grf_Label (grm,"",Uti_MultiLabel.LEFT);
        lc_text.addMouseListener(new Grf_LabelMouseEvent());
        grm.Get_Container().add(lc_text);

        /**Editiermodus initialisieren*/
        edit_mode = EDIT_NONE;
                
        /**Initialgr��en berechnen*/
        UpdateDimension();
        
        /**neuer Action-Listener erzeugen*/
        grc_button_action = new Grc_ActionAdapter();

		/**Popup-Menu einrichten*/
		grc_popup = new PopupMenu();
        		
    	mi = new MenuItem("Delete Edge");
		mi.addActionListener(grc_button_action);
		mi.setActionCommand("DEL_EDGE");		
        grc_popup.add(mi);
        
        /**..und einh�ngen*/
        add(grc_popup);        
	}

//    /**Destruktor*/
//    protected void finalize ()
//    {               
//        try {super.finalize();} catch(Throwable t) {};        
//    }

    /**RemoveLabels: entfernt alle der Verbindung angeh�renden Labels*/
    public void RemoveLabels()
    {
        /**Labels aus GUI-System entfernen*/
        grm.Get_Container().remove(lw_text);
        grm.Get_Container().remove(lc_text);
    }
    
    /**Grc_Set_Adj_Nodes: Verweise auf Adj_Nodes setzen*/
    public void Grc_Set_Grf_Nodes(Grf_Node src, Grf_Node dest,
                                    Adj_Connection conn)
    {
        src_node = src;
        dest_node = dest;
        adc_con = conn;
        
        Grc_RecalculateBounds();
    }
    
    /**Grc_Get_Src_Node:*/
    public Grf_Node Grc_Get_Src_Node()
    {
        return(src_node);
    }

    /**Grc_Get_Dest_Node:*/
    public Grf_Node Grc_Get_Dest_Node()
    {
        return(dest_node);
    }

    /**InitialDimension: Initialpfeilspitzengr��e berechnen    */
    protected int InitialDimension()
    {
        return((int)(arrow_width*grm.GetZoom()));
    }

    /**InitialFont: Initialfontgr��e berechnen    */
    protected Font InitialFont()
    {
        return(new Font("SansSerif",Font.PLAIN,(int) (font_size*grm.GetZoom())));       
    }
 
    /**Initialgr��en neu setzen*/
    public void UpdateDimension ()
    {
        /**Ausgangsgr��e setzen*/
        used_arr_width = InitialDimension();
        
        /**Font erzeugen*/
        text_ft = InitialFont();
        text_ft_metrics = getFontMetrics(text_ft);
        
        /**minimale Textfeldbreite*/
        min_text_width = text_ft_metrics.stringWidth("000.00")+12;
        min_text_height = text_ft_metrics.getHeight()+4;
        
        /**Font setzen*/
        lw_text.setFont(text_ft);
        lc_text.setFont(text_ft);
    }
    
    /**Grc_NodeInvolved: Ist Knoten innerhalb der Connection?*/
    public boolean Grc_NodeInvolved(Grf_Node node)
    {
        if ((src_node == node) || (dest_node == node))
            return(true);
        
        return(false);
    }

    /**Grc_RecalculateBoundsByDestPoint: Bounds der Grf_Connection neu   */
    /**berechnen, dest sind dabei koordinaten*/
    public void Grc_RecalculateBoundsByDestPoint(int destx, int desty)
    {
        int xa,ya,xb,yb;
        Dimension sa,sb;
                
        if(src_node == null)
            return;

        sa = src_node.getSize();
        xa = src_node.getBounds().x;
        ya = src_node.getBounds().y;
        
        sb = new Dimension(used_arr_width*2,used_arr_width*2);
        xb = destx;
        yb = desty;        
        
        /**..und berechnen*/
        Grc_RecalculateBoundsInternal(xa,ya,xb,yb,sa,sb);        
    }

    /**Grc_RecalculateBounds: Bounds der Grf_Connection neu berechnen*/
    public void Grc_RecalculateBounds()
    {
        int xa,ya,xb,yb;
        Dimension sa,sb;
        
        if ((src_node == null) || (dest_node == null))
            return;
        
        sa = src_node.getSize();
        xa = src_node.getBounds().x;
        ya = src_node.getBounds().y;

        sb = dest_node.getSize();
        xb = dest_node.getBounds().x;
        yb = dest_node.getBounds().y;
        
        /**..und berechnen*/
        Grc_RecalculateBoundsInternal(xa,ya,xb,yb,sa,sb);        
    }
    
    /**Grc_RecalculateBoundsInternal: Bounds der Grf_Connection neu berechnen*/
    protected void Grc_RecalculateBoundsInternal(int xa, int ya, int xb, int yb, Dimension sa, Dimension sb)
    {
        int t_width,t_height,i_weight;
        int xma,yma,xmb,ymb,ax,ay,st_x=0,st_y=0,en_x=0,en_y=0;
        Rectangle size;        
        Point ap1=new Point(),ap2=new Point(),lab_p=new Point();
        double deg_x,kant,l,cos_deg,sin_deg,f_weight;
        String weight_str,caption;
        boolean commit;
        
        /**Mittelpunktberechnung*/
        xma = xa+sa.width/2;
        yma = ya+sa.height/2;

        xmb = xb+sb.width/2;
        ymb = yb+sb.height/2;

        /**Quadrant ermitteln*/
        for(;;)
        {
            quadrant = 0;
            
            if((xma>xmb) && (yma>ymb))
            {                    
                quadrant = 1;              
                
                st_x = xb;
                st_y = yb;
                en_x = xa+sa.width;
                en_y = ya+sa.height;                
                
                break;
            }
            if((xma<=xmb) && (yma>=ymb))
            {    
                quadrant = 2;

                st_x = xa;
                st_y = ya+sa.height;
                en_x = xb+sb.width;
                en_y = yb;
                
                break;
            }            
            if((xma<xmb) && (yma<ymb))
            {                    
                quadrant = 3;

                st_x = xa;
                st_y = ya;
                en_x = xb+sb.width;
                en_y = yb+sb.height;
                
                break;
            }            
            if((xma>=xmb) && (yma<=ymb))
            {    
                quadrant = 4;

                st_x = xa+sa.width;
                st_y = ya;
                en_x = xb;
                en_y = yb+sb.height;
                
                break;
            }
            break;
        }

        size = new Rectangle(Math.min(st_x,en_x),Math.min(st_y,en_y),Math.abs(st_x-en_x),Math.abs(st_y-en_y));
        
        if(getBounds().equals(size) == false)
            setBounds(size);

        //Linien-Punkte berechnen
        line_src.x = xma - size.x;
        line_src.y = yma - size.y;

        line_dest.x = xmb - size.x;                
        line_dest.y = ymb - size.y;                                

        /**Winkelung ermitteln*/        
        l = Math.sqrt((ymb-yma)*(ymb-yma) + (xmb-xma)*(xmb-xma));        
        deg_x = Math.asin((ymb-yma)/l);

        sin_deg = Math.sin(deg_x);
        cos_deg = Math.cos(deg_x);        

        l = Math.sqrt((sb.width/2)*(sb.width/2)+(sb.height/2)*(sb.height/2));
                
        switch(quadrant)
        {
            case 1:
            case 4:            
                line_dest.x += (int)(cos_deg*l);
                line_dest.y -= (int)(sin_deg*l);            
                break;
                
            case 2:
            case 3:
                line_dest.x -= (int)(cos_deg*l);
                line_dest.y -= (int)(sin_deg*l);            
                break;
        }
        
        /**Pfeilspitze neu berechnen*/
        arr_head = null;

        arr_head = new Polygon();

        ax = line_dest.x;
        ay = line_dest.y;

        l = Math.sqrt((line_dest.y-line_src.y)* (line_dest.y-line_src.y) + 
                        (line_dest.x-line_src.x)*(line_dest.x-line_src.x));                 
                        
        kant = used_arr_width/Math.cos(arrow_deg); /**�u�ere Kantenl�nge Dreieck*/
        
        switch(quadrant)
        {
            /**Drehrichtung Uhrzeigersinn*/
            case 2:
            case 3:
                ap1.x = (int) (ax-Math.cos(deg_x+arrow_deg)*kant);
                ap1.y = (int) (ay-Math.sin(deg_x+arrow_deg)*kant);

                ap2.x = (int) (ax-Math.cos(Math.PI/2-deg_x-arrow_deg)*kant);
                ap2.y = (int) (ay+Math.sin(Math.PI/2-deg_x-arrow_deg)*kant);
                break;
                
            /**Drehung umdrehen*/
            case 1:
            case 4:

                ap1.x = (int) (ax+Math.sin(deg_x+arrow_deg)*kant);
                ap1.y = (int) (ay+Math.cos(deg_x+arrow_deg)*kant);

                ap2.x = (int) (ax+Math.sin(Math.PI/2-deg_x-arrow_deg)*kant);
                ap2.y = (int) (ay-Math.cos(Math.PI/2-deg_x-arrow_deg)*kant);
                break;
        }
                
        arr_head.addPoint(ax,ay);
        arr_head.addPoint(ap1.x,ap1.y);
        arr_head.addPoint(ap2.x,ap2.y);
        arr_head.addPoint(ax,ay);

        /**Gewichtsangabe neu berechnen*/
        if(adc_con != null) 
        {
            f_weight = Get_Weight();

            i_weight = (int)f_weight;

            weight_str = "";
            
            /**Nachkommastellen vorhanden ?*/
            if( ((double)i_weight) == f_weight)
                weight_str = ""+i_weight;
            else
                weight_str = ""+f_weight;                    

            if(lw_text.getText().equals(weight_str) == false)
                lw_text.setText(weight_str);

            t_width = lw_text.getSize().width;
            t_height = lw_text.getSize().height;

            /**Position neu berechnen*/
            switch(quadrant)
            {
                case 1:                                     
                    lab_p.x = (int) (ax+cos_deg*l/2)+2;
                    lab_p.y = (int) (ay-sin_deg*l/2)-t_height-2;
                    break;
                    
                case 2:
                    lab_p.x = (int) (ax-cos_deg*l/2)-t_width-2;
                    lab_p.y = (int) (ay-sin_deg*l/2)-t_height-2;
                    break;
                    
                case 3:
                    lab_p.x = (int) (ax-cos_deg*l/2)-t_width-2;
                    lab_p.y = (int) (ay-sin_deg*l/2)+2;
                    break;
                    
                case 4:                        
                    lab_p.x = (int) (ax+cos_deg*l/2)+2;
                    lab_p.y = (int) (ay-sin_deg*l/2)+2;
                    break;
            }
            
            lab_p.x += getLocation().x;
            lab_p.y += getLocation().y;
            
            if(lw_text.getLocation().equals(lab_p) == false)
                lw_text.setLocation(lab_p);                
            
            if( (grm.Get_Show_Weights() == true) && (isVisible()) )
                lw_text.setVisible(true);            
            else
                lw_text.setVisible(false);                            
        }

        /**Pr�fen ob Captionangabe erforderlich*/
        commit = false;
        
        switch(grm.Get_Node_Typ())
        {
            case Grf_Node.SIMPLE_GRAPH:
                break;
                
            case Grf_Node.EVENT_GRAPH:
                
                if( (grm.Get_Result_Frame() == false) && (isVisible()) )
                    commit = true;
                    
                break;
                
            case Grf_Node.TODO_GRAPH:
                break;
        }        
        
        /**Captionangabe neu berechnen*/
        if(commit)
        {
            if(adc_con != null) 
            {
                caption = Get_Caption();

                if(lc_text.getText().equals(caption) == false)
                    lc_text.setText(caption);                

                t_width = lc_text.getSize().width;
                t_height = lc_text.getSize().height;
                
                /**Position neu berechnen*/
                switch(quadrant)
                {
                    case 1:                                     
                        lab_p.x = (int) (ax+cos_deg*l/2)-t_width-2;
                        lab_p.y = (int) (ay-sin_deg*l/2)+2;
                        break;
                        
                    case 2:
                        lab_p.x = (int) (ax-cos_deg*l/2)+2;
                        lab_p.y = (int) (ay-sin_deg*l/2)+2;
                        break;
                        
                    case 3:
                        lab_p.x = (int) (ax-cos_deg*l/2)+2;
                        lab_p.y = (int) (ay-sin_deg*l/2)-t_height-2;
                        break;
                        
                    case 4:                        
                        lab_p.x = (int) (ax+cos_deg*l/2)-t_width-2;
                        lab_p.y = (int) (ay-sin_deg*l/2)-t_height-2;
                        break;
                }

                lab_p.x += getLocation().x;
                lab_p.y += getLocation().y;
                
                if(lc_text.getLocation().equals(lab_p) == false)
                    lc_text.setLocation(lab_p);                                
                
                lc_text.setVisible(true);                    
            }        
        }    
        else
            lc_text.setVisible(false);         
    }
    
    /**Set_Lock: Locking setzen*/
    public void Set_Lock (boolean lock)
    {
        is_locked = lock;
    }

    /**Set_Weight: Neusetzen der Gewichtung einer Verbindung*/
    public void Set_Weight(int value)
    {
        Set_Weight((double)value);
    }

   public void Set_Weight(double value)
   {
       grm.Set_Weight(src_node.Get_Node_Number(),
                dest_node.Get_Node_Number(),value);
        
       Grc_RecalculateBounds();               
    }

    public double Get_Weight()
    {
       return(grm.Get_Weight(src_node.Get_Node_Number(),
                dest_node.Get_Node_Number()));
    }
    
    /**Set_Caption: Verbindungstext setzen*/
    public void Set_Caption(String text)
    {
        if(adc_con != null)
            adc_con.Set_Caption(text);
    }

    /**Get_Caption: Verbindungstext zur�ckgeben*/
    public String Get_Caption()
    {
        if(adc_con != null)        
            return(adc_con.Get_Caption());
        else
            return(null);
    }

    /**SetMarker: Kennzeichnet Connection als markiert*/
    public void SetMarker ()
    {
        adc_con.SetMarker();
    }
    
    /**DeleteMarker: Kennzeichnet Connection als nicht markiert*/
    public void DeleteMarker()
    {
        adc_con.DeleteMarker();
    }

    /**IsMarked:*/
    public boolean IsMarked()
    {
        return(adc_con.IsMarked());
    }        
    
    /**HighliteConnection*/
    public void HighliteConnection(Color fill_color, Color text_color)
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
    
    /**UnHighliteConnection*/
    public void UnHighliteConnection()
    {
        highlite = false;
        
        repaint();
    }

    /**ActionEvent-Handling Popup*/
    class Grc_ActionAdapter implements ActionListener, Serializable
    {
		private static final long serialVersionUID = 8379023716868674945L;

		public void actionPerformed(ActionEvent e)     
        {
            Grc_Handle_ActionPerformed (e);
        }
        
        /**Serialize write*/
        protected void writeObject(java.io.ObjectOutputStream out) throws IOException
        {
            out.defaultWriteObject();
        }

        protected void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
        {
            in.defaultReadObject();        
        }        
    }

    public void Grc_Handle_ActionPerformed (ActionEvent e)
    {
        /**Kante l�schen*/
        if(e.getActionCommand().equals("DEL_EDGE"))
        {
            grm.DeleteConnection(src_node.Get_Node_Number(),dest_node.Get_Node_Number());
        }
    }
    
    class Grf_TextAreaKeyAdapter extends KeyAdapter
    {
         public void keyPressed(KeyEvent e)
         {
            Grc_Handle_TxtFldKeyEvent(e);            
         }         
    }
    
    public void Grc_Handle_TxtFldKeyEvent(KeyEvent e)
    {
        TextArea act_text;
        boolean dispatch;
        
        act_text = (TextArea) e.getSource();

        dispatch = false;
        
        switch(edit_mode)
        {
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
    
    /**FocusEvent-Handling TextField*/
    class Grf_TextFieldFocusAdapter extends FocusAdapter
    {
        public void focusLost(FocusEvent e)
        {
            Grc_Handle_TxtFldFocusLost(e);
        }
    }

    public void Grc_Handle_TxtFldFocusLost (FocusEvent e)
    {
        TextArea act_text;
        Object object = e.getSource();
        String caption;
        Double value;

        act_text = (TextArea) object;
        
        switch(edit_mode)
        {
            case EDIT_WEIGHT:
            
                try
                {
                    /**TextField und Label switchen */
                    value = Double.valueOf(act_text.getText());

                    /**neusetzen der Gewichtung */
                    Set_Weight(value.doubleValue());

                    /**Label-Inhalt neu setzen*/
                    lw_text.setText(act_text.getText());
    
                    edit_mode = EDIT_NONE;    
                    
                    Grc_RecalculateBounds();                                   
                }            
                catch(NumberFormatException exc) 
                {    
                    edit_mode = EDIT_NONE;    
                    
                    Grc_RecalculateBounds();                                                       
                }            
                break;
                
            case EDIT_CAPTION:
            
                caption = act_text.getText();

                /**neusetzen der Caption*/
                Set_Caption(caption);
                               
                /**Label-Inhalt neu setzen*/
                lc_text.setText(Get_Caption());

                edit_mode = EDIT_NONE;                                        
                
                Grc_RecalculateBounds();              
                break;
                
            default:
                break;
        }    

        /**TextArea entfernen*/
        grm.Get_Container().remove(act_text); 
        
        ed_text = null;                    
    }
    
    /**MouseEvent-Handling TextField*/
    class Grf_LabelMouseEvent extends MouseAdapter
    {
        public void mouseClicked(MouseEvent e)
        {
            Grc_Handle_LabelMouseClicked(e);
        }
    }

    public void Grc_Handle_LabelMouseClicked (MouseEvent e)
    {
        Object object = e.getSource();
        boolean right_pressed;
        
        if(is_locked == true)
            return;

        right_pressed = (e.getModifiersEx() & InputEvent.META_DOWN_MASK) != 0 ? true:false;            

        if( (!right_pressed) && (e.getClickCount() == 2) )
        {
            if(object == lw_text)
            {                
                edit_mode = EDIT_WEIGHT;
             
                /**CaptionTextField erzeugen und einh�ngen*/
                ed_text = new TextArea("",1,1,TextArea.SCROLLBARS_NONE);            
                ed_text.setFont(lw_text.getFont());
                ed_text.addKeyListener(new Grf_TextAreaKeyAdapter());
                ed_text.addFocusListener(new Grf_TextFieldFocusAdapter());

                ed_text.setLocation(lw_text.getLocation());
                ed_text.setText(lw_text.getText());
                                               
                if(ed_text.getSize().width < min_text_width)
                    ed_text.setSize(min_text_width,lw_text.getSize().height);
                else
                    ed_text.setSize(lw_text.getSize());                
                
                grm.Get_Container().add(ed_text,0);                
                
                /**Focus setzen*/
                ed_text.requestFocus();
                ed_text.selectAll();
            }    
            
            if(object == lc_text)
            {
                edit_mode = EDIT_CAPTION;
                
                /**CaptionTextField erzeugen und einh�ngen*/
                ed_text = new TextArea("",1,1,TextArea.SCROLLBARS_NONE);            
                ed_text.setFont(lc_text.getFont());
                ed_text.addKeyListener(new Grf_TextAreaKeyAdapter());
                ed_text.addFocusListener(new Grf_TextFieldFocusAdapter());

                ed_text.setLocation(lc_text.getLocation());
                ed_text.setText(lc_text.getText());
                   
                if( (ed_text.getSize().width < (2*min_text_width)) || 
                        (ed_text.getSize().height < (2*min_text_height)) )
                {
                    ed_text.setSize(2*min_text_width,2*min_text_height);
                }
                else
                    ed_text.setSize(lc_text.getSize().width+4,lc_text.getSize().height);                                                
                    
                grm.Get_Container().add(ed_text);                
                
                /**Focus setzen*/
                ed_text.requestFocus();
                ed_text.selectAll();                
            }    
        }
        
        if(right_pressed)
        {
            /**Popup-Menu darstellen       	    */
       	    if(right_pressed)
       	    {
       	        grc_popup.show((Component)object,e.getX(),e.getY());
       	    }
        }            
    }
  
    /**PaintConnectionInt: Internes Zeichnen der Verbindung*/
    protected void PaintConnectionInt(Graphics g)
    {     
        Color draw_color;
        
        g.setFont(getFont());
        
        draw_color = Color.black;
        
        //g.setColor(Color.red);
        //g.drawRect(0,0,getBounds().width-1,getBounds().height-1);
        
        /**Highlite �berpr�fen*/
        if(highlite)
        {
            draw_color = fill_color;                    
        }        

        /**und malen ..*/
        g.setColor(draw_color);
        g.drawLine(line_src.x,line_src.y,line_dest.x,line_dest.y);
                              
        /**Pfeilspitze einzeichnen*/
        switch(connection_typ)
        {
            case Adj_Connection.TypDirected:
                g.fillPolygon(arr_head);
                break;
                
            case Adj_Connection.TypNotDirected:
                break;
        }                
    }
    
     /**paint: GUI-Override*/
    public void paint (Graphics g)
    {
        Rectangle bnds;
        
        /**aktuelle Gr��e ermitteln*/
        bnds = getBounds();
        
        //fm = getToolkit().getFontMetrics(getFont());
        
        /**Drawing in Buffer bereitstellen*/
        g = grm.Get_DrawGraphics().create(bnds.x,bnds.y, bnds.width,bnds.height);        
        
        /**interne Zeichenroutine ansto�en*/
        PaintConnectionInt(g);
    }    
}
