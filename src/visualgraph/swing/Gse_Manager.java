package visualgraph.swing;
import java.awt.*;
import java.util.Vector;

import visualgraph.core.Pdt_Element;

import java.awt.event.*;

/**GUI-Containerklasse f�r Sequenzen auf Basis primitiver DT*/
public class Gse_Manager extends Gdt_Manager
{
    /*Sequence-Darstellungsart Typen*/
    public static final int SEQ_VIEW_ONE_ROW       = 0;
    public static final int SEQ_VIEW_MULT_ROWS     = 1;
    public static final int SEQ_VIEW_CIRCLE        = 2;
    
    /**Sequence-Content-Typ*/
    protected int content_typ;

    /**Sequenz-Darstellungsart*/
    protected int sequence_view;

    /**Hilfsattribute*/
    protected Vector gsm_view_cmi;
    
    static final long serialVersionUID = -3487969304302903159L;
    
    /**Konstruktoren*/
    public Gse_Manager(Frm_Manager mgr)
    {
        this(Pdt_Element.TYP_INTEGER,"",mgr);
	}

    public Gse_Manager(int typ, Frm_Manager mgr)
    {
        this(typ,"",mgr);
	}

    /**Hauptkonstruktor*/
    public Gse_Manager(int typ, String name, Frm_Manager mgr)
    {
        super(name,mgr);
        
        MenuItem mi;
        CheckboxMenuItem cmi;
        Menu mu;

        //Sequence-Content setzen
        content_typ = typ;
        
        //Darstellungsart setzen
        sequence_view = SEQ_VIEW_ONE_ROW;
        
        //Std-Content setzen
        std_content_typ = content_typ;
                
        //Popup-Men� einrichten        
        frm_popup.remove(0);
        frm_popup.remove(0);
        frm_popup.remove(0);
        
    	mi = new MenuItem("New Item");
		mi.addActionListener(frm_button_action);
	    mi.setActionCommand("NEW_ITEM");		
        frm_popup.insert(mi,0);
        
        frm_popup.insertSeparator(2);
        
		mu = new Menu("Sequence View");
		frm_popup.insert(mu,3);
        
        gsm_view_cmi = new Vector();
        
		cmi = new CheckboxMenuItem("One Row",true);    		
    	cmi.addItemListener(frm_item_event);    		
		cmi.setActionCommand("SEQ_VIEW_"+SEQ_VIEW_ONE_ROW);		
		mu.add(cmi);		    

		/**und der Liste hinzuf�gen*/
		gsm_view_cmi.addElement(cmi);    		

		cmi = new CheckboxMenuItem("Multiple Rows");    		
    	cmi.addItemListener(frm_item_event);    		
		cmi.setActionCommand("SEQ_VIEW_"+SEQ_VIEW_MULT_ROWS);		
		mu.add(cmi);		    

		/**und der Liste hinzuf�gen*/
		gsm_view_cmi.addElement(cmi);    		

		cmi = new CheckboxMenuItem("Circular");    		
    	cmi.addItemListener(frm_item_event);    		
		cmi.setActionCommand("SEQ_VIEW_"+SEQ_VIEW_CIRCLE);		
		mu.add(cmi);		    

		/**und der Liste hinzuf�gen*/
		gsm_view_cmi.addElement(cmi);    		
        
        //Dokument-Info setzen            
        Set_Info();       
    }

    /**Copy-Konstruktor*/
    public Gse_Manager(String name, Gse_Manager src)
    {
        super(name,src);
        
        //Darstellungsart �bernehmen
        sequence_view = src.sequence_view;
    }
    
    /**AddElementAtPoint: Neues Element an Koordinaten einf�gen*/
    public int AddElementAtPoint (int x, int y, int typ)
    {
        int element_number;
        
        //Super-Methode aufrufen
        element_number = super.AddElementAtPoint(x,y,typ);
        
        //Position ver�ndern
        if(gdt_elems.size() > 1)
            SequenceOrder(0,0);

        return(element_number);        
    }

    /**InsertElement: Element vor vorhandenem Element einf�gen*/
    public int InsertElement(int element_id, int typ)
    {
        int new_elem_nr;
        Gdt_Element first,new_gdt;
        Point first_loc=null;
        
        //erstes Element sichern
        first = (Gdt_Element) gdt_elems.elementAt(0);
        
        if(LookupIdentElement(element_id) == first)
        {
            first_loc = first.getLocation();
        }
        else
            first = null;
                    
        //Super-Methode aufrufen
        new_elem_nr = super.InsertElement(element_id,typ);

        if(first != null)
        {
            //vor erstes Element eingef�gt?
            new_gdt = LookupIdentElement(new_elem_nr);
            
            new_gdt.setLocation(first_loc);
            
            super.HandleElementMotion(new_gdt);                        
        }    
        
        //Elemente neu anordnen
        SequenceOrder(0,0);
        
        return(new_elem_nr);
    }
    
    /**Set_Info: Info-Label neu setzen*/
    protected void Set_Info ()
    {
        String info;

        info = "";

        if(!Get_Result_Frame())
        {
            switch(content_typ)
            {
                case Pdt_Element.TYP_INTEGER:
                    info+= "Sequence of Integer";
                    break;
                    
                case Pdt_Element.TYP_DOUBLE:
                    info+= "Sequence of Real";
                    break;
                
                case Pdt_Element.TYP_STRING:                
                    info+= "Sequence of String";
                    break;
            }
            
            if(alg_name.equals("") == false)
            {
                if(info.equals(""))
                    info += alg_name;
                else
                    info += ", "+alg_name;
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

        gdt_info.setText(info);
    }    
        
    /**SequenceOrder: Elemente sequentiell anordnen*/
    public void SequenceOrder(int delta_x, int delta_y)
    {
        int i,cnt,act_x,act_y,first_x;
        Gdt_Element gdt;
        Dimension last_dim=null;
        double deg;
        Point loc;
        
        switch(sequence_view)
        {
            case SEQ_VIEW_ONE_ROW:

                act_x=-1;
                act_y=-1;

                for(i=0;i<gdt_elems.size();i++)
                {
                    gdt = (Gdt_Element) gdt_elems.elementAt(i);
                    
                    if(act_x == -1)
                    {
                        //erste Position setzen
                        act_x = gdt.getLocation().x + delta_x;
                        act_y = gdt.getLocation().y + delta_y;
                    }
                    else
                    {
                       act_x += last_dim.width;                        
                    }

                    gdt.setLocation(act_x,act_y);
                
                    super.HandleElementMotion(gdt); 
                   
                    last_dim = gdt.getSize();
                }                                          
                break;
                
            case SEQ_VIEW_MULT_ROWS: 
            
                act_x=-1;
                act_y=-1;
                first_x = 0;
                
                for(i=0,cnt=0;i<gdt_elems.size();i++,cnt++)
                {
                    gdt = (Gdt_Element) gdt_elems.elementAt(i);
                    
                    if(act_x == -1)
                    {
                        //erste Position setzen
                        act_x = gdt.getLocation().x + delta_x;
                        act_y = gdt.getLocation().y + delta_y;
                        
                        first_x = act_x;
                    }
                    else
                    {
                        if((cnt%5) == 0)
                        {
                            act_x = first_x;                            
                            act_y += Gdt_Element.vert_difference;
                        }
                        else
                        {
                            act_x += last_dim.width;                        
                        }    
                    }

                    gdt.setLocation(act_x,act_y);
                
                    super.HandleElementMotion(gdt); 
                   
                    last_dim = gdt.getSize();
                }                                          
                break;

            case SEQ_VIEW_CIRCLE: 

                deg = 360/gdt_elems.size();

                act_x=-1;
                act_y=-1;

                for(i=0;i<gdt_elems.size();i++)
                {
                    gdt = (Gdt_Element) gdt_elems.elementAt(i);
                    
                    if(act_x == -1)
                    {
                        //erste Position setzen
                        act_x = gdt.getLocation().x + delta_x;
                        act_y = gdt.getLocation().y + delta_y;
                    }
                    else
                    {
                        act_x += (int) (Math.cos(i*deg/180*Math.PI)*
                                 (gdt.getSize().width+Gdt_Element.grid_dimension.width));                        
                                 
                        act_y += (int) (Math.sin(i*deg/180*Math.PI)*
                                 (gdt.getSize().height+Gdt_Element.grid_dimension.height));
                    }

                    gdt.setLocation(act_x,act_y);
                
                    super.HandleElementMotion(gdt); 
                }                                          
                break;
        }                
    }
    
    /**Handle_MouseClicked: Overrideable aus Frm_Frame*/
    public void Handle_MouseClicked (MouseEvent event)
    {
        boolean right_pressed;
        Object object = event.getSource();		
        CheckboxMenuItem cmi;
        MenuItem mi;
        int i;

        //eigene Verabeitung    
        if(is_locked == true)
            return;
        
        right_pressed = (event.getModifiers() & InputEvent.META_MASK) != 0 ? true:false;

        /**Popup-Menu darstellen*/
   	    if(right_pressed)
   	    {            
            /**View-Type*/
	        for(i=0;i<gsm_view_cmi.size();i++)
	        {
        		cmi = (CheckboxMenuItem)gsm_view_cmi.elementAt(i);
        		
        		if(cmi.getActionCommand().equals("SEQ_VIEW_"+sequence_view))		        		
                    cmi.setState(true);    
                else
                    cmi.setState(false);                        
            }
   	    }

        //Super-Methode aufrufen
        super.Handle_MouseClicked(event);   	    
    }

    /**Handle_ActionEvent: overrideable aus Frm_Frame*/
    public void Handle_ActionEvent (ActionEvent event)
    {
        String command;
        
        //Super-Methode aufrufen
        super.Handle_ActionEvent(event);
    
        //eigene Verabeitung durchf�hren        
        if(is_locked == true)
            return;

        command = event.getActionCommand();

        /**Neues Element einf�gen*/
        if(command.equals("NEW_ITEM"))
        {
            AddElementAtPoint(pt_x,pt_y,content_typ);            
        }
    }    
    
    /**Handle_ItemStateChanged: overrideable aus Frm_Frame*/
    public void Handle_ItemStateChanged (ItemEvent event)
    {
        CheckboxMenuItem cmi,ucmi;
        int i;

        //Super-Methode aufrufen
        super.Handle_ItemStateChanged(event);
    
        //eigene Verabeitung durchf�hren        
        cmi = (CheckboxMenuItem) event.getSource();

        /**View-Type*/
        if(gsm_view_cmi.contains(cmi))
        {
            //andere Checks l�chen
            for(i=0;i<gsm_view_cmi.size();i++)
            {
        		ucmi = (CheckboxMenuItem)gsm_view_cmi.elementAt(i);
        		if(cmi != ucmi)
                    cmi.setState(false);                        
            }            

            if(cmi.getActionCommand().equals("SEQ_VIEW_"+SEQ_VIEW_ONE_ROW))        
            {            
                Set_Sequence_View(SEQ_VIEW_ONE_ROW);            
            }        

            if(cmi.getActionCommand().equals("SEQ_VIEW_"+SEQ_VIEW_MULT_ROWS))        
            {            
                Set_Sequence_View(SEQ_VIEW_MULT_ROWS);            
            }        

            if(cmi.getActionCommand().equals("SEQ_VIEW_"+SEQ_VIEW_CIRCLE))        
            {            
                Set_Sequence_View(SEQ_VIEW_CIRCLE);            
            }        
        }    
    }

    /**HandleElementMotion: Reaktion auf das Verschieben von Elementen im Dokument*/   
    public void HandleElementMotion(Gdt_Element elem_moved)
    {        
        int dx=0,dy=0;
        Point last_loc;
        
        //Super-Methode aufrufen
        super.HandleElementMotion(elem_moved);
                       
        //Positionsver�nderung ermitteln und anordnen
        if(elem_moved != (Gdt_Element) gdt_elems.elementAt(0))
        {
            last_loc = elem_moved.Get_Last_Location();
            
            if(last_loc.x != -1)
            {
                dx = elem_moved.getLocation().x - last_loc.x;    
                dy = elem_moved.getLocation().y - last_loc.y;                
            }    
        }

        SequenceOrder(dx,dy);
    }

    /**HandleElementChange: Reaktion auf Ver�ndern des Element-Inhalts*/
    public void HandleElementChange(Gdt_Element elem_changed)
    {        
        //Super-Methode aufrufen
        super.HandleElementChange(elem_changed);
        
        //Element neu sequentiell anordnen
        SequenceOrder(0,0);
    }

    /**Set_Alg_Name: Algortihmus setzen*/
    public void Set_Alg_Name(String name)
    {
        super.Set_Alg_Name(name);

        Set_Info();
    }    
    
    /**R�ckgabe der aktuellen Sequenz-Darstellung*/
    public int Get_Sequence_View()
    {
        return(sequence_view);
    }

    /**Setzen der aktuellen Sequenz-Darstellung*/    
    public void Set_Sequence_View(int mode)
    {
        if(sequence_view != mode)
        {
            sequence_view = mode;
            
            SequenceOrder(0,0);
        }    
    }
    
    /**DeleteElement: Element aus Dokument entfernen*/
    public void DeleteElement(int element_id)
    {
        Gdt_Element first;
        Point first_loc=null;
        
        //erstes Element sichern
        first = (Gdt_Element) gdt_elems.elementAt(0);

        if(first.Get_Element_Number() != element_id)
            first = null;
        else
            first_loc = first.getLocation();
            
        //Super-Method-Call
        super.DeleteElement(element_id);

        if(first != null)
        {
            //erstes Element wurde gel�scht, zweites verschieben?
            first = (Gdt_Element) gdt_elems.elementAt(0);            

            first.setLocation(first_loc);
            
            super.HandleElementMotion(first);
        }    
                    
        //Elemente neu anordnen
        SequenceOrder(0,0);
    }    
    
    /**Vertauschen zweier Elemente*/
    public void SwapElements(int first_elem_nr, int second_elem_nr)
    {
        //Super-Method-Call
        super.SwapElements(first_elem_nr,second_elem_nr);

        //neu anorndnen    
        SequenceOrder(0,0);        
    }    
}