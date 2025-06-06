package visualgraph.swing;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import visualgraph.util.Uti_MultiLabel;

public class Grf_EditLabel extends Uti_MultiLabel
{   
    /**Inital-Font Einstellungen*/
    private static final int font_size = 9;
    private static final int font_style = Font.PLAIN;
    
    private static final Dimension label_dimension = new Dimension(60,30);
    
    /**interaktive Editier-Modi*/
    private static final int EDIT_NONE      = 0;
    private static final int EDIT_LABEL     = 1;

    private static Grf_EditLabel edit_label;
    
    /**Verweis auf Frame*/
    private Frm_Frame frm;
    
    /**Locking*/
    private boolean is_locked;
   
    /**Eingabe-TextField*/
    private TextArea w_text;
        
    /**aktueller Editier-Modus*/
    private int edit_mode;

    /**Popup*/
    private PopupMenu grl_popup;
    
    /**Action-Listener-Objects*/
    private Grf_ButtonAction        grl_button_action;
    private Grf_ItemEventAdapter    grl_item_event;
    
    /**Style*/
    private int f_style;
    private int f_size;
    
    /**Cmi-Vectors*/
    private Vector style_cmi;

    static final long serialVersionUID = 4551258576087353171L;
    
    /**PrepareSerializedObject: Serialisierungsverarbeitung*/
    public void PrepareSerializedObject(int prepare_mode) 
    {
        switch(prepare_mode)
        {
            case Frs_Manager.SER_BEFORE_WRITE:                
                remove(grl_popup);                
                break;
                
            case Frs_Manager.SER_AFTER_WRITE:
                add(grl_popup);                    
                break;
                
            case Frs_Manager.SER_AFTER_READ:
                add(grl_popup);                    
                                
          		addMouseListener(new Grf_LabelMouse());
          		addMouseMotionListener(new Grf_LabelMouseMotion());			

                w_text.addFocusListener(new Grf_TextAreaFocusAdapter());
                w_text.addKeyListener(new Grf_TextAreaKeyAdapter());
                break;
        }        
    }

    /**Konstruktor*/
    public Grf_EditLabel(Frm_Frame frm, int x, int y)
    {        
        super("Label",Uti_MultiLabel.LEFT);        
        
        MenuItem mi;
        CheckboxMenuItem cmi;
        Menu mu;
        
        setLocation (x,y);
        
        /**Graph-Manager einh�ngen*/
        this.frm = frm;
        
        /**Font-Setzen*/
        f_style = font_style;
        f_size = font_size;
        
        /**Initial-Size setzen*/
        setFont(InitialFont());        
        setSize(InitialDimension());      
        
        setVisible(false);
        
        /**Listeners registrieren*/
  		addMouseListener(new Grf_LabelMouse());
  		addMouseMotionListener(new Grf_LabelMouseMotion());			        

        /**Event-Listeners erzeugen*/
        grl_button_action = new Grf_ButtonAction();
        grl_item_event = new Grf_ItemEventAdapter();
                
        /**Einagbe-TextArea erzeugen*/
        edit_mode = EDIT_LABEL;

        edit_label = this;
        
        w_text = new TextArea("",0,0,TextArea.SCROLLBARS_NONE);
        w_text.setBounds(x,y,getSize().width,getSize().height);     
        w_text.setFont(getFont());        
        
        w_text.addFocusListener(new Grf_TextAreaFocusAdapter());
        w_text.addKeyListener(new Grf_TextAreaKeyAdapter());

        w_text.setVisible(true);
        frm.Get_Container().add(w_text);

        /**Cmi-Vector erstellen*/
        style_cmi = new Vector();
        
		/**Popup-Menu einrichten*/
		grl_popup = new PopupMenu();
		
    	mi = new MenuItem("Delete Label");
		mi.addActionListener(grl_button_action);
		mi.setActionCommand("DEL_LABEL");		
        grl_popup.add(mi);
		
		grl_popup.addSeparator();

    	mu = new Menu("Style");
        grl_popup.add(mu);

    	cmi = new CheckboxMenuItem("Italic",(f_style == Font.ITALIC) ? true:false);
    	cmi.addItemListener(grl_item_event);    		
		cmi.setActionCommand("STYLE_ITALIC");		
        mu.add(cmi);
        style_cmi.addElement(cmi);
        
    	cmi = new CheckboxMenuItem("Bold",(f_style == Font.BOLD) ? true:false);
    	cmi.addItemListener(grl_item_event);    		
		cmi.setActionCommand("STYLE_BOLD");		
        mu.add(cmi);
        style_cmi.addElement(cmi);
        
        mu.addSeparator();
        
    	cmi = new CheckboxMenuItem("Plain",(f_style == Font.PLAIN) ? true:false);
    	cmi.addItemListener(grl_item_event);    		
		cmi.setActionCommand("STYLE_PLAIN");		
        mu.add(cmi);
		style_cmi.addElement(cmi);

    	mu = new Menu("Size");
        grl_popup.add(mu);

    	mi = new MenuItem("Extend");
		mi.addActionListener(grl_button_action);
		mi.setActionCommand("SIZE_EXTEND");		
        mu.add(mi);

    	mi = new MenuItem("Reduce");
		mi.addActionListener(grl_button_action);
		mi.setActionCommand("SIZE_REDUCE");		
        mu.add(mi);
		
		mu.addSeparator();

    	mi = new MenuItem("Standard");
		mi.addActionListener(grl_button_action);
		mi.setActionCommand("SIZE_STANDARD");		
        mu.add(mi);
		
		add(grl_popup);			
		
		w_text.requestFocus();
	}    
        
    /**Zugriffsfunktionen*/
        
    /**Set_Lock: Locking setzen*/
    public void Set_Lock (boolean lock)
    {
        is_locked = lock;
    }
 
    /**Mouse-Motion-Listener-Adapter*/
  	class Grf_LabelMouseMotion extends MouseMotionAdapter 
	{
		public void mouseDragged(MouseEvent e)
		{
		    Handle_MouseDragged (e);
		}
	}

    public void Handle_MouseDragged (MouseEvent e)
    {
	    Grf_EditLabel grl;		   		    
        int xm,ym;
        boolean shift_pressed;
        grl = (Grf_EditLabel) e.getSource();
		
		shift_pressed = (e.getModifiers() & InputEvent.SHIFT_MASK)!=0 ? 
                        true:false;
		
		if(shift_pressed)
		{
		    xm = (int) grl.getSize().width/2;
		    ym = (int) grl.getSize().height/2;
		    
		    if ( (grl.getLocation().x+e.getX()-xm >= 0) &&
		         (grl.getLocation().y+e.getY()-ym >= 0) )
		    {     
    			/**Label-Move relativ zu Dragging verschieben*/
                grl.setLocation(grl.getLocation().x+e.getX()-xm,
                    grl.getLocation().y+e.getY()-ym);            			             
                
                /**ScrollPane-Size evtl. �ndern*/
                frm.HandleLabelMotion(grl);
            }                
        }            
    }
    
    /**Mouse-Listener-Adapter*/
  	class Grf_LabelMouse extends MouseAdapter 
	{
		public void mouseClicked(MouseEvent e)
		{
		    Handle_MouseClicked (e);
   		}
    }

    public void Handle_MouseClicked (MouseEvent e)
    {
	    Grf_EditLabel grl;		   		    
	    boolean right_pressed;
        Dimension dim;
        MenuItem mi;
        CheckboxMenuItem cmi;       
        int i;
        
        if(is_locked == true)
            return;

        right_pressed = (e.getModifiers() & InputEvent.META_MASK) != 0 ? true:false;

		grl = (Grf_EditLabel) e.getSource();
		
        if(!right_pressed)
        {                            
            if(e.getClickCount() == 2)
            {
                /**in Edit-Mode wechseln*/
                edit_mode = EDIT_LABEL;
                
                edit_label = this;
                
                setVisible(false);
                
                w_text.setFont(getFont());                
                w_text.setText(getText());       
                
                w_text.setLocation(getLocation().x,getLocation().y);
                w_text.setSize(getSize().width+4,getSize().height);
                
                /**Gr��e auf min anpassen*/
                dim = w_text.getSize();
                
                if(dim.width < label_dimension.width)
                    dim.width = label_dimension.width;

                if(dim.height < label_dimension.height)
                    dim.height = label_dimension.height;
                    
                w_text.setSize(dim);                
                
                w_text.setVisible(true);
                
                w_text.requestFocus();
            }
        }
        
        /**Popup-Menu darstellen       	    */
   	    if(right_pressed)
   	    {
   	        /**Font-Style Checks l�schen*/
   	        for(i=0;i<style_cmi.size();i++)
   	        {
       	        cmi = (CheckboxMenuItem)style_cmi.elementAt(i);    
       	        cmi.setState(false);
       	    }
       	    
   	        /**Font-Style setzen   	      */
   	        for(i=0;i<style_cmi.size();i++)
   	        {
         		cmi = (CheckboxMenuItem)style_cmi.elementAt(i);
        		    
    		    if( (cmi.getActionCommand().equals("STYLE_PLAIN")) &&
    		        (f_style == Font.PLAIN) )    		                    
    		    {    
    		        cmi.setState(true);
    		        break;
    		    }

    		    if( (cmi.getActionCommand().equals("STYLE_ITALIC")) &&
    		        (f_style == Font.ITALIC) )
    		    {    
    		        cmi.setState(true);
    		        break;
    		    }

    		    if( (cmi.getActionCommand().equals("STYLE_BOLD")) &&
    		        (f_style == Font.BOLD) )
    		    {    
    		        cmi.setState(true);
    		        break;
    		    }
    		}
   	        
   	        grl_popup.show((Component)grl,e.getX(),e.getY());
   	    }
    }
    
    /**Grf_ButtonAction: PopupMenu Action-Verabeitung  */
	class Grf_ButtonAction implements java.awt.event.ActionListener, Serializable
	{
		private static final long serialVersionUID = -2883965614370746863L;

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
        
        /**Knoten l�schen*/
        if(command.equals("DEL_LABEL"))
        {
            frm.DeleteLabel(this);
        }

        /**Schriftgr��e vergr��ern*/
        if(command.equals("SIZE_EXTEND"))
        {
            Set_Size(1);
        }

        /**Schriftgr��e verkleinern*/
        if(command.equals("SIZE_REDUCE"))
        {
            Set_Size(-1);
        }

        /**Schriftgr��e standard*/
        if(command.equals("SIZE_STANDARD"))
        {
            Set_Size(0);
        }
    }
    
    class Grf_TextAreaKeyAdapter extends KeyAdapter
    {
         public void keyPressed(KeyEvent e)
         {
            Handle_TxtFldKeyEvent(e);            
         }         
    }
    
    public void Handle_TxtFldKeyEvent(KeyEvent e)
    {
        TextArea act_text;
        Object object = e.getSource();
        
        act_text = (TextArea) object;

        if( (e.getKeyCode() == KeyEvent.VK_TAB) || (e.getKeyCode() == KeyEvent.VK_ESCAPE) )
        {
            act_text.dispatchEvent(new FocusEvent(act_text,FocusEvent.FOCUS_LOST));
        }
    }
        
    /**FocusEvent-Handling TextArea*/
    class Grf_TextAreaFocusAdapter extends FocusAdapter 
    {
        public void focusLost(FocusEvent e)
        {
            Handle_TxtFldFocusLost(e);
        }
    }

    public void Handle_TxtFldFocusLost (FocusEvent e)
    {
        TextArea act_text;
        if(edit_label == null)
            return;
            
        Object object = e.getSource();
        
        /**TextArea und Label switchen*/
        act_text = (TextArea) object;

        switch(edit_label.edit_mode)
        {
            case EDIT_LABEL:

                /**neusetzen des Label-Textes*/
                if(act_text.getText().equals(edit_label.getLabel()) == false)
                {
                    edit_label.setLabel(act_text.getText());                      
                    
                    frm.Set_Edited();
                }    
                break;
                
            default:
                break;
        }
        
        edit_label.edit_mode = EDIT_NONE;
        
        /**TextArea ausblenden*/
        edit_label.w_text.setVisible(false);

        /**Label einblenden*/
        edit_label.setVisible(true);
        
        /**Pr�fen ob Label leer -> dann l�schen*/
        if (act_text.getText().equals(""))
        {
            frm.DeleteLabel(this);
        }
        
        edit_label = null;
    }
 
    /**Grf_ItemEventAdapter: f�r Popup-Menu*/
    class Grf_ItemEventAdapter implements ItemListener, Serializable 
    {    
		private static final long serialVersionUID = -4797961552586665835L;

		public void itemStateChanged(ItemEvent event)
        {   
            Handle_ItemStateChanged (event);
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

    public void Handle_ItemStateChanged (ItemEvent event)
    {
        CheckboxMenuItem cmi;
        
        cmi = (CheckboxMenuItem) event.getSource();
        
        /**Style: plain*/
        if(cmi.getActionCommand().equals("STYLE_PLAIN"))
        {            
            Set_Style(Font.PLAIN);         
        }

        /**Style: italic*/
        if(cmi.getActionCommand().equals("STYLE_ITALIC"))
        {            
            Set_Style(Font.ITALIC);         
        }

        /**Style: bold*/
        if(cmi.getActionCommand().equals("STYLE_BOLD"))
        {            
            Set_Style(Font.BOLD);         
        }        
    }		
    
    /**InitialDimension: Initialisierungsgr��e des Knoten bereitstellen*/
    private Dimension InitialDimension ()
    {
        Dimension new_dim = new Dimension();

        new_dim.width  = (int) (label_dimension.width*frm.GetZoom());
        new_dim.height = (int) (label_dimension.height*frm.GetZoom());                

        return(new_dim);
    }

    /**InitialFont: Initialisierungsgr��e Fonts zur�ckgeben*/
    private Font InitialFont()
    {        
        return(new Font("SansSerif",f_style,(int) 
                    (f_size*frm.GetZoom())));     
    }    
    
    /**InitialFont: Initialisierungsgr��en aktualisieren*/
    public void UpdateDimension()
    {   
        /**Location aktualisieren*/
        setLocation((int) (getLocation().x*(1/frm.GetLastZoom())*frm.GetZoom()),
                        (int) (getLocation().y*(1/frm.GetLastZoom())*frm.GetZoom()));        

        /**Font-Setzen*/
        setFont(InitialFont());
        
        w_text.setFont(getFont());

        /**Gr��en anpassen*/
        w_text.setSize(getSize().width,getSize().height);        
        w_text.setLocation(getLocation().x,getLocation().y);
    }    
    
    /**Set_Style: Style d. Labels setzen*/
    public void Set_Style(int style)
    {
        f_style = style;            
        
        setFont(InitialFont());
        
        frm.Set_Edited();
    }

    /**Set_Size: Font-Size d. Labeltexts ver�ndern*/
    public void Set_Size(int size_mode)
    {
        switch(size_mode)
        {
            case 1:
                f_size = f_size +2;
                break;
                
            case -1:
                f_size = f_size -2;
                break;
            
            case 0:
            default:
                f_size = font_size;
                break;
        }
                
        /**Font anpassen*/
        setFont(InitialFont());
        
        frm.Set_Edited();
    }
     
    /**GUI-override: paint*/
    public void paint(Graphics g)
    {
        Rectangle bnds;
 
        bnds = getBounds();
        
        /**Drawing in Buffer bereitstellen*/
        g = frm.Get_DrawGraphics().create(bnds.x,bnds.y, bnds.width, bnds.height);        
               
        super.paint(g);
    }
}
