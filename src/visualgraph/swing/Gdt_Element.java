package visualgraph.swing;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import visualgraph.core.Pdt_Element;

/**GUI-Klasse f�r Atomar-Datentypen in Sequenz-Darstellung*/
public class Gdt_Element extends java.awt.Component
{   
    /**Initialgr��en*/
    protected static final Dimension elem_dimensions[] =  {
                                                        new Dimension(30,30), 
                                                        new Dimension(30,30),
                                                        new Dimension(60,30) 
                                                        };

    protected static final int elem_font_size[] = {
                                    9,
                                    9,
                                    9
                                                };
                                                        
    
    /**Raster-Dimensionen*/
    protected static final Dimension grid_dimension = new Dimension(10,10);

    /**Vertikaler Elementabstand*/
    protected static final int vert_difference = 40;
    
    /**interaktive Editier-Modi*/
    private static final int EDIT_NONE      = 0;
    private static final int EDIT_CONTENT   = 1;    

    /**Verweis auf GUIDataType-Mgmt.*/
    protected Gdt_Manager gdt_mgr;
    
    /**Verweis auf Pdt_Element*/
    protected Pdt_Element  pdt_elem;
        
    /**Locking*/
    protected boolean is_locked;
    
    /**Eingabe-TextArea*/
    protected TextArea w_text;
    
    /**aktueller Editier-Modus*/
    protected int edit_mode;
    
    /**Popup*/
    protected PopupMenu gse_popup;
    
    /**Action-Listener-Objects*/
    protected Grf_ButtonAction gse_button_action;

    /**Hilfsattribute*/
    protected Point last_location;
    
    /**Highlitening*/
    private transient boolean highlite;
    private transient Color fill_color;
    private transient Color text_color;    
        
    static final long serialVersionUID = -4244037920828209484L;
    
    /**PrepareSerializedObject: Serialisierungsverarbeitung*/
    public void PrepareSerializedObject(int prepare_mode) 
    {
        switch(prepare_mode)
        {
            case Frs_Manager.SER_BEFORE_WRITE:
                remove(gse_popup);
                break;
                
            case Frs_Manager.SER_AFTER_WRITE:
                add(gse_popup);                    
                break;
                
            case Frs_Manager.SER_AFTER_READ:
                add(gse_popup);                    
                
                /**neue Listeners hinzuf�gen*/
          		addMouseListener(new Gdt_ElementMouse());
          		addMouseMotionListener(new Gdt_ElementMouseMotion());			
                break;
        }        
    }

    /**Konstruktor*/
    public Gdt_Element(Gdt_Manager gdt_mgr, Pdt_Element pdt_elem)
    {        
        MenuItem mi;
        
        /**Verweis auf Seq.-Mgmt setzen*/
        this.gdt_mgr = gdt_mgr;
        
        /**Verweis auf Atomartyp setzen*/
        this.pdt_elem = pdt_elem;
        
        /**Font-Setzen*/
        setFont(InitialFont());
                
        /**Initial-Size setzen*/
        setSize(InitialDimension());      
                
        /**Event-Listeners erzeugen*/
        gse_button_action = new Grf_ButtonAction();
        
  		addMouseListener(new Gdt_ElementMouse());
  		addMouseMotionListener(new Gdt_ElementMouseMotion());			

        /**Location-Zwischenspeicher bereitstellen*/
        last_location = new Point(-1,-1);
        
        /**Einagbe-TextArea erzeugen*/
        edit_mode = EDIT_NONE;
                
		/**Popup-Menu einrichten*/
		gse_popup = new PopupMenu();

		//Spezielles Men� f�r Sequenzen aktivieren
		if(gdt_mgr instanceof Gse_Manager)
		{
        	mi = new MenuItem("Insert Item");
    		mi.addActionListener(gse_button_action);
    		mi.setActionCommand("INSERT_ITEM");		
            gse_popup.add(mi);
            
            gse_popup.addSeparator();
		}
		
    	mi = new MenuItem("Delete Item");
		mi.addActionListener(gse_button_action);
		mi.setActionCommand("DEL_ITEM");		
        gse_popup.add(mi);
		        
		add(gse_popup);		
	}    
        
    /**Zugriffsfunktionen*/

    /**Get_Element: R�ckgabe d. Verkn�pfung nach Pdt_Element*/
    public Pdt_Element Get_Element()
    {
        return(pdt_elem);
    }

    /**Set_Pdt_Element: Setzen d. Verkn�pfung nach Pdt_Element*/
    public void Set_Pdt_Element(Pdt_Element pdt)
    {
        pdt_elem = pdt;
    }
            
    /**Get_Element_Id: Gibt den Identificator des Elements zur�ck*/
    public int Get_Element_Number()
    {
        return(pdt_elem.Get_Element_Number());
    }

    /**Get_Content_Typ: Element-Inhalt zur�ckgeben*/
    public int Get_Content_Typ()
    {
        return(pdt_elem.Get_Content_Typ());
    }

    /**Get: R�ckgabe Element-Inhalts als Object*/
    public Object Get()
    {
        return(pdt_elem.Get());
    }

    /**Set: Setzen Element-Inhalt*/
    public void Set(Object value)
    {
        pdt_elem.Set(value);
        
        //neuzeichnen
        repaint();
    }

    /**GetId: Identificator des Elements zur�ckgeben*/
    public String GetId ()
    {
        String ident;
        
        ident = ""+Get_Element_Number();

        return(ident);
    }
                    
    /**Set_Lock: Locking setzen*/
    public void Set_Lock (boolean lock)
    {
        is_locked = lock;
    }

    /**SetMarker: Kennzeichnet Element als markiert*/
    public void SetMarker ()
    {
        pdt_elem.SetMarker();

        /**Element neu zeichnen*/
        repaint();
    }
    
    /**DeleteMarker: Kennzeichnet Element als nicht markiert*/
    public void DeleteMarker()
    {
        pdt_elem.DeleteMarker();

        /**Element neu zeichnen*/
        repaint();        
    }

    /**IsMarked:*/
    public boolean IsMarked()
    {
        return(pdt_elem.IsMarked());
    }    

    /**R�ckgabe der letzten Elementposition, nach Elementbewegung*/
    public Point Get_Last_Location()
    {
        return(last_location);
    }
    
    /**Mouse-Motion-Listener-Adapter*/
  	class Gdt_ElementMouseMotion extends MouseMotionAdapter 
	{
		public void mouseDragged(MouseEvent e)
		{
		    Handle_MouseDragged (e);
		}
	}

    public void Handle_MouseDragged (MouseEvent e)
    {
	    Gdt_Element gse;		   		    
        int xm,ym;
        boolean shift_pressed;
        Point grid_loc,new_loc;
        
		gse = (Gdt_Element) e.getSource();
		
		shift_pressed = (e.getModifiers() & InputEvent.SHIFT_MASK)!=0 ? 
                        true:false;
		
        if(!shift_pressed)
        {
            if(is_locked == true)
                return;            
        }

		if(shift_pressed)
		{
		    xm = (int) gse.getSize().width/2;
		    ym = (int) gse.getSize().height/2;

	        //Grid-Alignment pr�fen
	        if(!gdt_mgr.Get_Align_Grid())
	        {		            
    		    if ( (gse.getLocation().x+e.getX()-xm >= 0) &&
    		         (gse.getLocation().y+e.getY()-ym >= 0) )
    		    {   
    		        //letzte Position sichern
    		        last_location = gse.getLocation();
    		        
    	    		/**Element-Move relativ zu Dragging verschieben*/
                    gse.setLocation(gse.getLocation().x+e.getX()-xm,
                                    gse.getLocation().y+e.getY()-ym);            			             

                    /**Element-Move verarbeiten, ScrollPane-Size evtl. �ndern*/
                    gdt_mgr.HandleElementMotion(gse);                                    
                }                    
            }	    
            else
            {
    		    if ( (gse.getLocation().x+e.getX() >= 0) &&
    		         (gse.getLocation().y+e.getY() >= 0) )
    		    {   
    		        //letzte Position sichern
    		        last_location = gse.getLocation();

                    //Grid-Snapping                
                    grid_loc = gdt_mgr.KoordToGrid (gse.getLocation().x+e.getX(),
                                    gse.getLocation().y+e.getY(), 0,0);
                
                    new_loc = gdt_mgr.GridToKoord (grid_loc.x,grid_loc.y,0,0);
                
                    gse.setLocation(new_loc.x,new_loc.y);

                    /**Element-Move verarbeiten, ScrollPane-Size evtl. �ndern*/
                    gdt_mgr.HandleElementMotion(gse);                                                    
                }    
            }
        }            
    }
    
    /**Mouse-Listener-Adapter*/
  	class Gdt_ElementMouse extends MouseAdapter 
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
	    Gdt_Element gse;		   		    
	    Rectangle rect,vgl_rect;
	    boolean right_pressed;
	    FontMetrics fm;
	    
        if(is_locked == true)
            return;

        right_pressed = (e.getModifiers() & InputEvent.META_MASK) != 0 ? true:false;

		gse = (Gdt_Element) e.getSource();            
		
        if(!right_pressed)
        {                            
            /**Evtl. Eingabe-Field anzeigen*/
            if(e.getClickCount() == 2)
            {
            	fm = gdt_mgr.Get_Container().getFontMetrics(getFont());
               //fm = getToolkit().getFontMetrics(getFont());
               
               /**Eingabe-Text-Field erzeugen und positionieren*/
               w_text = new TextArea("",1,1,TextArea.SCROLLBARS_NONE);
               w_text.setFont(getFont());        
               w_text.setVisible(false);
               
               w_text.addKeyListener(new Grf_TextAreaKeyAdapter());        
               w_text.addFocusListener(new Grf_TextAreaFocusAdapter());

               /**...und anzeigen */
               gdt_mgr.Get_Container().add(w_text);               

               w_text.setSize(getSize().width-2,getSize().height-2-fm.getHeight());
               w_text.setLocation(getLocation().x+1,getLocation().y+1+fm.getHeight());

               edit_mode = EDIT_CONTENT;                                            
               
               switch(pdt_elem.Get_Content_Typ())
               {
                    case Pdt_Element.TYP_INTEGER:                        
                        w_text.setText(""+pdt_elem.GetInt());                        
                        break;

                    case Pdt_Element.TYP_DOUBLE:
                        w_text.setText(""+pdt_elem.GetDouble());                        
                        break;
                        
                    case Pdt_Element.TYP_STRING:                                        
                        w_text.setText(pdt_elem.GetString());                                                
                        break;
                        
                    default:
                        edit_mode = EDIT_NONE;
               }    
               
               if(edit_mode != EDIT_NONE)
               {
                   w_text.setVisible(true);                                        
                
                   /**Focus-request*/
                   w_text.requestFocus();
                   w_text.selectAll();                                            
               }
            }
        }
        
        /**Popup-Menu darstellen */
   	    if(right_pressed)
   	    {
   	        gse_popup.show((Component)gse,e.getX(),e.getY());
   	    }
    }

    public void Handle_MouseReleased (MouseEvent e)
    {
	    if(is_locked == true)
            return;
		
//		(Gdt_Element) e.getSource();
        
        /**Element neu zeichnen*/
        repaint();        
    }
    
    /**Grf_ButtonAction: PopupMenu Action-Verabeitung  */
	class Grf_ButtonAction implements java.awt.event.ActionListener, Serializable
	{
		private static final long serialVersionUID = -5419411517602472475L;

		public void actionPerformed(java.awt.event.ActionEvent event)
		{
            Handle_ActionPerformed(event);		    
        }       
        private void writeObject(java.io.ObjectOutputStream out) throws IOException
        {
            out.defaultWriteObject();
        }
        private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
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
        
        /**Element l�schen*/
        if(command.equals("DEL_ITEM"))
        {
            gdt_mgr.DeleteElement(Get_Element_Number());
        }

        /**Element einf�gen*/
        if(command.equals("INSERT_ITEM"))
        {
            gdt_mgr.InsertElement(Get_Element_Number(),pdt_elem.Get_Content_Typ());
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
            case EDIT_CONTENT:

                if( (e.getKeyCode() == KeyEvent.VK_TAB) || (e.getKeyCode() == KeyEvent.VK_ESCAPE) ||
                    (e.getKeyCode() == KeyEvent.VK_ENTER) )                    
                    dispatch = true;

                break;
                
            default:
                dispatch = true;
                break;
        }
        
        if(dispatch)
            act_text.dispatchEvent(new FocusEvent(act_text,FocusEvent.FOCUS_LOST));                
        
        gdt_mgr.Set_Edited();
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
        Object obj;
        
        /**TextField und Label switchen*/
        act_text = (TextArea) e.getSource();

        switch(edit_mode)
        {
            case EDIT_CONTENT:

                switch(pdt_elem.Get_Content_Typ())
                {
                    case Pdt_Element.TYP_INTEGER:
                        try
                        {
                            obj = (Object) (Integer.valueOf(act_text.getText()));

                            pdt_elem.Set(obj);
                        }            
                        catch(NumberFormatException exc) {};                                
                        break;
                        
                    case Pdt_Element.TYP_DOUBLE:
                        try
                        {
                            obj = (Object) (Double.valueOf(act_text.getText()));

                            pdt_elem.Set(obj);
                        }            
                        catch(NumberFormatException exc) {};                                
                        break;
                        
                    case Pdt_Element.TYP_STRING:

                        obj = (Object) (new String(act_text.getText()));

                        pdt_elem.Set(obj);

                        break;
                }
                break;
                        
            default:
                break;
        }
        
        edit_mode = EDIT_NONE;
        
        /**TextArea entfernen*/
        gdt_mgr.Get_Container().remove(w_text);        

        /**Size anpassen*/
        setSize(InitialDimension());              
                
        /**und neu zeichnen*/
        repaint();
        
        //Inhalts�nderung an Manager weitergeben
        gdt_mgr.HandleElementChange(this);
    }
    
    /**InitialDimension: Initialisierungsgr��e des Element bereitstellen*/
    protected Dimension InitialDimension ()
    {
        FontMetrics fm;
        String s="";
        Dimension new_dim = new Dimension();

        new_dim.width  = (int) (elem_dimensions[pdt_elem.Get_Content_Typ()].width*gdt_mgr.GetZoom());
        new_dim.height = (int) (elem_dimensions[pdt_elem.Get_Content_Typ()].height*gdt_mgr.GetZoom());                

        //Breite ggf. angleichen
        if(pdt_elem != null)
        {
        	fm = getFontMetrics(getFont());
            //fm = getToolkit().getFontMetrics(getFont());
            
            switch(pdt_elem.Get_Content_Typ())
            {
                case Pdt_Element.TYP_INTEGER:
                    s = ""+pdt_elem.GetInt();
                    break;
                    
                case Pdt_Element.TYP_DOUBLE:
                    s = ""+pdt_elem.GetDouble();
                    break;
                
                case Pdt_Element.TYP_STRING:            
                    s = ""+pdt_elem.GetString();
                    break;
                default:
                    break;    
            }
            
            if(new_dim.width < (fm.stringWidth(s)))
            {
                new_dim.width = (int)((fm.stringWidth(s))/grid_dimension.width)
                                *grid_dimension.width+grid_dimension.width;
            }
        }        
        return(new_dim);
    }

    /**InitialFont: Initialisierungsgr��e Fonts zur�ckgeben*/
    protected Font InitialFont()
    {
        return(new Font("SansSerif",Font.PLAIN,(int) 
                    (elem_font_size[pdt_elem.Get_Content_Typ()]*gdt_mgr.GetZoom())));     
    }    
    
    /**InitialFont: Initialisierungsgr��en aktualisieren*/
    protected void UpdateDimension()
    {   
        /**Location aktualisieren*/
        setLocation((int) (getLocation().x*(1/gdt_mgr.GetLastZoom())*gdt_mgr.GetZoom()),
                        (int) (getLocation().y*(1/gdt_mgr.GetLastZoom())*gdt_mgr.GetZoom()));        

        /**Font-Setzen*/
        setFont(InitialFont());       

        /**Initial-Size setzen*/
        setSize(InitialDimension());              
    }
    
    /**Hilfsmethode*/
    public String toString()
    {
        return(pdt_elem.toString());
    }
    
    /**HighliteElement*/
    public void HighliteElement(Color fill_color, Color text_color)
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
    
    /**UnHighliteElement*/
    public void UnHighliteElement()
    {
        highlite = false;
        
        repaint();
    }
        
    /**PaintElementInt: zeichnen des Element*/
    protected void PaintElementInt (Graphics g)
    { 
        int row,t_width,t_height;
        Rectangle rect;
        String s="",out_s;
        FontMetrics fm;
        Color txt_color;
        
        rect = getBounds();
        g.clearRect(0,0,rect.width-1,rect.height-1);              
        g.setFont(getFont());
        
    	fm = getFontMetrics(getFont());
        //fm = getToolkit().getFontMetrics(getFont());
        
        //Inset-Setzen
        rect.height -= fm.getHeight();
        rect.y      = fm.getHeight();
        
        /**Highlite �berpr�fen*/
        if(highlite) 
            txt_color = text_color;                    
        else
            txt_color = Color.black;

        /**Highlite �berpr�fen*/
        if(highlite) 
        {
            g.setColor(fill_color);        
            g.fillRect(0,rect.y,rect.width-1,rect.height-1);
        }
                
        //Rahmung und Element-Nummer zeichnen        
	    s = ""+Get_Element_Number();
	    
        t_width = fm.stringWidth(s);
        t_height = fm.getAscent();
        
        g.setColor(Color.black);                
        g.drawRect(0,0,rect.width-1,fm.getHeight());        
        g.drawRect(0,rect.y,rect.width-1,rect.height-1);

        g.drawString(s,3,t_height+1);                        
        g.drawLine(t_width+5,0,t_width+5,rect.y);
        
        //Inhalt ausgeben            
        switch(pdt_elem.Get_Content_Typ())
        {            
            case Pdt_Element.TYP_INTEGER: 

                s = ""+pdt_elem.GetInt();                
                break;
                
            case Pdt_Element.TYP_DOUBLE:        

                s = ""+pdt_elem.GetDouble();                
                break;

            case Pdt_Element.TYP_STRING:

                s = ""+pdt_elem.GetString();                
                break;
                
            default:
                break;
        }        

        t_width = fm.stringWidth(s);
        t_height = fm.getAscent();

        g.setColor(txt_color);                        
        g.drawString(s,rect.width/2-t_width/2,rect.y+rect.height/2+t_height/2-1);                
        
        /**Marker �berpr�fen*/
        if(pdt_elem.IsMarked() == true)
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
        g = gdt_mgr.Get_DrawGraphics().create(bnds.x,bnds.y, bnds.width, bnds.height);
        
        /**interne Zeichenroutine ansto�en*/
        PaintElementInt(g);       
                            
        /**Paint-Overridable*/
        switch(pdt_elem.Get_Content_Typ())
        {
           case Pdt_Element.TYP_INTEGER: 
                break;
            
           case Pdt_Element.TYP_DOUBLE:        
                break;
                
           case Pdt_Element.TYP_STRING:        
                break;
        }          
    }           
}
