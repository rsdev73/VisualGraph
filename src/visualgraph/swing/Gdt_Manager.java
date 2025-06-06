package visualgraph.swing;
import java.awt.*;
import java.util.Vector;

import visualgraph.core.Id_Vector;
import visualgraph.core.Pdt_Element;
import visualgraph.core.Pdt_Manager;

import java.awt.event.*;

/**GUI-Containerklasse f�r atomare Datentypen*/
public class Gdt_Manager extends Frm_Frame
{
    /**Verweis auf Pdt_Manager*/
    protected Pdt_Manager pdt_manager;

    /**Vector der Gdt_Elements*/
    protected Vector gdt_elems;

    /**Vector der Labels*/
    protected Vector grl_labels;

    /**ScrollPane*/
    protected ScrollPane adm_scroll;

    /**zugeh�riges Panel*/
    protected Grf_DoublePanel adm_panel;

    /**Am Raster ausrichten*/
    protected boolean align_grid;

    /**Info-Label*/
    protected Grf_Label gdt_info;

    /**Element-Info Labels*/
    protected Vector elem_info;

    /**Standard-Content Typ*/
    protected int std_content_typ;

    /**Property-Dialog*/
    protected transient Dlg_GdtProp prop;

    static final long serialVersionUID = 2197200063824481689L;

    /**PrepareSerializedObject: Serialisierungsverarbeitung*/
    public void PrepareSerializedObject(int prepare_mode)
    {
        int i;
        Gdt_Element act_elem;
        Grf_EditLabel act_label;

        //Super-Methode aufrufen
        super.PrepareSerializedObject(prepare_mode);
        
        switch(prepare_mode)
        {
            case Frs_Manager.SER_BEFORE_WRITE:
                break;

            case Frs_Manager.SER_AFTER_WRITE:
                break;

            case Frs_Manager.SER_AFTER_READ:

                /**Pdt_Manager neu identifizieren*/
                pdt_manager.ReIdentify();
    
        		/**neue Listeners einh�ngen*/
                adm_panel.addMouseListener(new Gdt_MouseEvent());
                break;
        }

		/**.. und weitergeben an Gdt_Elements*/
		for(i=0;i<gdt_elems.size();i++)
		{
		    act_elem = (Gdt_Element) gdt_elems.elementAt(i);
		    act_elem.PrepareSerializedObject(prepare_mode);
		}

		/**.. und weitergeben an Grf_EditLabels*/
		for(i=0;i<grl_labels.size();i++)
		{
		    act_label = (Grf_EditLabel) grl_labels.elementAt(i);
		    act_label.PrepareSerializedObject(prepare_mode);
		}
    }

    /**Konstruktoren*/
    public Gdt_Manager(Frm_Manager mgr)
    {
        this("",mgr);
	}

    /**Hauptkonstruktor*/
    public Gdt_Manager(String name, Frm_Manager mgr)
    {
        MenuItem mi;
        CheckboxMenuItem cmi;
        Menu mu;
        Dimension vp_size;
        FontMetrics fm;

        //**Am Raster ausrichten
        align_grid = true;

        /**Verweise setzen*/
        frm_mgr = mgr;

        //Pdt-Manager bereitstellen
        pdt_manager = new Pdt_Manager();

        //Standard Content-Typ setzen
        std_content_typ = Pdt_Element.TYP_INTEGER;
        
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
        gdt_elems = new Vector();
        grl_labels = new Vector();

        /**Name initialisieren*/
        Set_Name(name);

        /**Event-Listeners registrieren*/
        adm_panel.addMouseListener(new Gdt_MouseEvent());

        /**Info-Label erzeugen*/
        gdt_info = new Grf_Label(this,"");
        gdt_info.setLocation(0,0);
        gdt_info.setFont(new Font("SansSerif",Font.PLAIN,font_size));
        fm = gdt_info.getFontMetrics(gdt_info.getFont());
        gdt_info.setSize(4,fm.getHeight());
        adm_panel.add(gdt_info);

        /**Element-Info-Labels*/
        elem_info = new Vector();

        /**Alg-Std Info Label einh�ngen*/
        adm_panel.add(alg_std_info);

        //Popup-Men� einrichten
    	mi = new MenuItem("New Integer");
		mi.addActionListener(frm_button_action);
		mi.setActionCommand("NEW_INTEGER");
        frm_popup.add(mi);

    	mi = new MenuItem("New Real");
		mi.addActionListener(frm_button_action);
		mi.setActionCommand("NEW_DOUBLE");
        frm_popup.add(mi);

    	mi = new MenuItem("New String");
		mi.addActionListener(frm_button_action);
		mi.setActionCommand("NEW_STRING");
        frm_popup.add(mi);

    	mi = new MenuItem("New Label");
		mi.addActionListener(frm_button_action);
		mi.setActionCommand("NEW_LABEL");
        frm_popup.add(mi);

		/**Algorithmus-Menu*/
		Init_Alg_Menu();
		
        /**Alignment- und Packing*/
        frm_popup.addSeparator();

    	mi = new MenuItem("Align Items");
		mi.addActionListener(frm_button_action);
		mi.setActionCommand("ALIGN_ITEMS");
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

        //Popup einh�ngen
		adm_panel.add(frm_popup);

		Set_Edited ();

		Set_Info();
    }

    /**Copy-Konstruktor*/
    public Gdt_Manager(String name, Gdt_Manager src)
    {
        this(name,src.frm_mgr);
        int i;

        Set_Align_Grid(src.Get_Align_Grid());

        SetZoom((int)src.GetZoomPercent());

        /**Elements kopieren*/
        for(i=0;i<src.gdt_elems.size();i++)
        {
            CopyElement((Gdt_Element) src.gdt_elems.elementAt(i));
        }
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

    /**GridToKoord: Berechnen der Bild-koordinaten �ber Rasterkoord.*/
    public Point GridToKoord (int col, int row, int distance, int offset)
    {
        int x,y;
        Dimension grid_dim;

        grid_dim = Gdt_Element.grid_dimension;

        x = offset + col*(grid_dim.width+distance);
        y = offset + row*(grid_dim.height+distance);

        if(x < 0)
            x = 0;

        if(y < 0)
            y = 0;
            
        return(new Point(x,y));
    }
									
    /**KoordToGrid: Berechnen der Rasterkoord. �ber Bild-koordinaten*/
    public Point KoordToGrid (int x, int y, int distance, int offset)
    {
        int col,row;
        Dimension grid_dim;

        grid_dim = Gdt_Element.grid_dimension;

        col = (int) ((x-offset)/(grid_dim.width+distance));
        row = (int) ((y-offset)/(grid_dim.height+distance));

        if(col < 0)
            col = 0;

        if(row < 0)
            row = 0;
            
        return(new Point(col,row));
    }
	
    /**Ausrichten an virtuellem Elementraster*/
    public void Align()
    {
        int i;
        Gdt_Element act_element;
        Point new_loc,grid_loc;

        for(i=0;i<gdt_elems.size();i++)
        {
            act_element = (Gdt_Element) gdt_elems.elementAt(i);

            grid_loc = KoordToGrid(act_element.getLocation().x,act_element.getLocation().y,0,0);
            new_loc  = GridToKoord(grid_loc.x,grid_loc.y,0,0);

            act_element.setLocation(new_loc.x,new_loc.y);

            //Bewegung verarbeiten
            HandleElementMotion(act_element);
        }
    }

    /**AddElement: Neues Element an Rasterkoordinaten einf�gen*/
    public int AddElement (int col, int row, int typ)
    {
        Point loc;

        loc = GridToKoord(col,row,0,0);

        return(AddElementAtPoint(loc.x,loc.y,typ));
    }

    /**AddElementAtPoint: Neues Element an Koordinaten einf�gen*/
    public int AddElementAtPoint (int x, int y, int typ)
    {
        Gdt_Element gdt;
        Pdt_Element pdt;
        Point new_loc,grid_loc;

        /**Neues Elemente erzeugen*/        
        pdt = new Pdt_Element(typ);
        
        //zum DS-Manager hinzuf�gen
        pdt_manager.Add(pdt);
                
        //neues GUI-Element erzeugen
        gdt = new Gdt_Element(this,pdt);
        gdt.Set_Lock(is_locked);

        /**Gr��e und Farbe einstellen*/
        gdt.setBackground(Color.white);

        /**Auf Alignment pr�fen*/
        if(align_grid)
        {
            grid_loc = KoordToGrid(x,y,0,0);
            new_loc = GridToKoord(grid_loc.x,grid_loc.y,0,0);

            x = new_loc.x;
            y = new_loc.y;
        }

        gdt.setBounds(x,y,gdt.getBounds().width,gdt.getBounds().height);

        /**Component einh�ngen*/
        adm_panel.add(gdt);

        /**Vector erweitern*/
        gdt_elems.addElement(gdt);

        /**Element zeichnen*/
        gdt.repaint();

        Set_Edited();
        
        return(pdt.Get_Element_Number());
    }

    /**InsertElement: Element vor vorhandenem Element einf�gen*/
    public int InsertElement(int element_id, int typ)
    {
        Gdt_Element vgl_gdt,gdt;
        Pdt_Element pdt;
        Point new_loc,grid_loc;
        int x,y,index;
        
        //Vorg�nger-Element ermitteln
        vgl_gdt = LookupIdentElement(element_id);

        if(vgl_gdt == null)
            return(0);                
        
        /**Neues Elemente erzeugen*/        
        pdt = new Pdt_Element(typ);
   
        //im DS-Manager eintragen
        pdt_manager.Insert(vgl_gdt.Get_Element_Number(),pdt);
                
        //neues GUI-Element erzeugen
        gdt = new Gdt_Element(this,pdt);
        gdt.Set_Lock(is_locked);

        /**Gr��e und Farbe einstellen*/
        gdt.setBackground(Color.white);

        /**Position einstellen*/
        x = vgl_gdt.getLocation().x-vgl_gdt.getSize().width;
        y = vgl_gdt.getLocation().y;        

        /**Auf Alignment pr�fen*/
        if(align_grid)
        {
            grid_loc = KoordToGrid(x,y,0,0);
            new_loc = GridToKoord(grid_loc.x,grid_loc.y,0,0);

            x = new_loc.x;
            y = new_loc.y;
        }

        gdt.setBounds(x,y,gdt.getBounds().width,gdt.getBounds().height);

        /**Component einh�ngen*/
        adm_panel.add(gdt);

        /**index des vorhandenen Elements ermitteln*/
        index = gdt_elems.indexOf(vgl_gdt);
        
        /**Vector erweitern*/
        gdt_elems.insertElementAt(gdt,index);

        /**Element zeichnen*/
        gdt.repaint();

        Set_Edited();
        
        return(pdt.Get_Element_Number());            
    }
    
    /**Vertauschen zweier Elemente*/
    public void SwapElements(int first_elem_nr, int second_elem_nr)
    {
        Gdt_Element first,second;
        int f_index,s_index;
        Point first_loc,second_loc;
        
        //Elemente ermitteln
        first = LookupIdentElement(first_elem_nr);
        if(first == null)
            return;

        first_loc = first.getLocation();
        
        f_index = gdt_elems.indexOf(first);
        
        second = LookupIdentElement(second_elem_nr);
        if(second == null)
            return;
        
        second_loc = second.getLocation();        
        
        s_index = gdt_elems.indexOf(second);
        
        //auf DS-Ebene vertauschen
        pdt_manager.Swap(first_elem_nr,second_elem_nr);        
        
        //und im Vector vertauschen
        gdt_elems.setElementAt(second,f_index);
        gdt_elems.setElementAt(first,s_index);        
        
        //Positionen vertauschen
        first.setLocation(second_loc);
        HandleElementMotion(first);

        second.setLocation(first_loc);
        HandleElementMotion(second);
                                       
        //und neu zeichnen
        Repaint();        
    }

    /**Zwei Elemente inhaltlich vergleichen*/
    public int CompareElements (int first_elem_nr, int second_elem_nr)
    {
        //Vergleich auf DS-Ebene durchf�hren
        return(pdt_manager.Compare(first_elem_nr,second_elem_nr));
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

    /**DeleteLabel: Label aus Dokument entfernen*/
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

    /**Copy: Kopiert ein Element*/
    private int CopyElement (Gdt_Element src)
    {
        Gdt_Element gdt;
        Pdt_Element pdt;
        int id;
        
        //zum Manager hinzuf�gen
        id = pdt_manager.CopyElement(src.Get_Element());        
        pdt = pdt_manager.LookupIdentElement(id);
        
        /**neuen Dokument-Element erzeugen*/
        gdt = new Gdt_Element(this,pdt);

        gdt.Set_Lock(is_locked);

        /**Gr��e und Farbe einstellen*/
        gdt.setBackground(Color.white);
        gdt.setBounds(src.getLocation().x,src.getLocation().y,src.getSize().width,src.getSize().height);

        /**Component einh�ngen*/
        adm_panel.add(gdt);

        /**Vector erweitern*/
        gdt_elems.addElement(gdt);

        /**neu zeichnen*/
        gdt.repaint();

        Set_Edited();

        return(id);
    }

    /**DeleteElement: Element aus Dokument entfernen*/
    public void DeleteElement(int element_id)
    {
        Gdt_Element gdt;

        gdt = LookupIdentElement(element_id);
        if(gdt == null)
            return;

        //aus Manager entfernen
        pdt_manager.DeleteElement(element_id);
        
        gdt.Set_Pdt_Element(null);

        /**Component aush�ngen*/
        adm_panel.remove(gdt);

        /**aus Liste entfernen*/
        gdt_elems.removeElement(gdt);

        /**und neu zeichenen   */
        Repaint();

        /**neue Gr��e berechnen*/
        if(getParent() != null)
            dispatchEvent(new ComponentEvent(getParent(),ComponentEvent.COMPONENT_RESIZED));

   		Set_Edited ();
    }

    /**DeleteAllElements: L�scht alle Elemente*/
    public void DeleteAllElements ()
    {
        //Ds-Management l�schen
        pdt_manager.DeleteAllElements();
        
        /**sichtbare Komponenten l�schen*/
        adm_panel.removeAll();

        /**Vektoren l�schen*/
        gdt_elems = null;
        
        gdt_elems = new Vector();

   		Set_Edited ();
    }

    /**LookupIdentElement: Lookup nach element_number*/
    public Gdt_Element LookupIdentElement(int element_number)
    {
        int i;
        Gdt_Element gdt;
        
        for(i=0;i<gdt_elems.size();i++)
        {
            gdt = (Gdt_Element) gdt_elems.elementAt(i);
            
            if(gdt.Get_Element_Number() == element_number)
                return(gdt);
        }
        
        return(null);
    }

    /**RepaintElements: Alle Elemente neu zeichnen*/
    public void RepaintElements()
    {
        int i;
        Gdt_Element act_element;

        /**Connections neu zeichnen*/
        for(i=0;i<gdt_elems.size();i++)
        {
            act_element = (Gdt_Element) gdt_elems.elementAt(i);

            act_element.repaint();
        }
    }

    /**R�ckgabe des zugrundeliegenden Pdt_Managers*/
    public Pdt_Manager Get_Pdt_Manager()
    {
        return(pdt_manager);
    }
    
    /**HandleComponentMotion: Reaktion auf das Verschieben einer Komp. im Dokument*/
    private void HandleComponentMotion(Component comp_moved)
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

    /**HandleElementMotion: Reaktion auf das Verschieben von Elementen im Dokument*/
    public void HandleElementMotion(Gdt_Element elem_moved)
    {
       Dimension dim;
       Grf_Label act_info;
       /**Component-Move verarbeiten*/
       HandleComponentMotion (elem_moved);

       /**Element-Info verschieben*/       
       if(elem_info.size() >= (elem_moved.Get_Element_Number()+1) )
       {
           act_info = (Grf_Label) elem_info.elementAt(elem_moved.Get_Element_Number());

           if(act_info != null)
           {
               dim = act_info.getSize();

               act_info.setLocation(elem_moved.getLocation().x-(dim.width-elem_moved.getSize().width)/2,
                                        elem_moved.getLocation().y+elem_moved.getSize().height+2);
           }
       }       
    }

    /**HandleElementChange: Reaktion auf Ver�ndern des Element-Inhalts*/
    public void HandleElementChange(Gdt_Element elem_changed) {;}
    
    /**HandleLabelMotion: Reaktion auf das Verschieben von Labels im Dokument*/
    public void HandleLabelMotion(Grf_EditLabel label_moved)
    {
        /**Component-Move verarbeiten*/
        HandleComponentMotion (label_moved);
    }

    /**RecalcNeededDimension: Berechnet die ben�tigte Gr��e des inneren Panels*/
    private Dimension RecalcNeededDimension ()
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

    /**RecalcPreferredDimension: Berrechnet die bevorzugte Gr��e der aktuellen Objekte */
    public Dimension RecalcPreferredDimension ()
    {
        int i,need_width,need_height;
        Gdt_Element gdt;
        Grf_EditLabel grl;
        Rectangle bnds;

        need_width = 0;
        need_height = 0;

        for(i=0;i<gdt_elems.size();i++)
        {
            gdt = (Gdt_Element) gdt_elems.elementAt(i);

            bnds = gdt.getBounds();

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

    /**DeleteElementMarkers: Alle Elementmarkierungen entfernen*/
    public void DeleteElementMarkers()
    {
        int i;
        Gdt_Element gdt;
        
        for(i=0;i<gdt_elems.size();i++)
        {
            gdt = (Gdt_Element) gdt_elems.elementAt(i);
            gdt.DeleteMarker();
        }
        
        RepaintElements();
    }

    /**HighliteElement:*/
    public void HighliteElement(Gdt_Element gdt, Color fill_color, Color text_color)
    {
        gdt.HighliteElement(fill_color,text_color);
    }

    public void HighliteElement(int element_number, Color fill_color, Color text_color)
    {
        Gdt_Element gdt;

        gdt = LookupIdentElement(element_number);
        if(gdt == null)
            return;

        HighliteElement(gdt, fill_color, text_color);
    }

    /**UnHighliteElement: */
    public void UnHighliteElement(Gdt_Element gdt)
    {
        gdt.UnHighliteElement();        
    }

    public void UnHighliteElement(int element_number)
    {
        Gdt_Element gdt;

        gdt = LookupIdentElement(element_number);
        if(gdt == null)
            return;

        UnHighliteElement(gdt);
    }

    public void UnHighliteAllElements()
    {
        int i;
        Gdt_Element gdt;

        for(i=0;i<gdt_elems.size();i++)
        {
            gdt = (Gdt_Element) gdt_elems.elementAt(i);
            gdt.UnHighliteElement();
        }
    }

    /**Hilfsfunktionen*/
    public String GetString ()
    {
        return(pdt_manager.toString());
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
        Gdt_Element gdt;
        Grf_EditLabel glabel;

        if(is_locked == lock)
            return;

        is_locked = lock;

        /**Lock-State durchschleifen, Nodes*/
        for(i=0;i<gdt_elems.size();i++)
        {
            gdt = (Gdt_Element) gdt_elems.elementAt(i);
            gdt.Set_Lock(lock);
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

    /**Has_Properties: Properties nicht vorhanden*/
    public boolean Has_Properties ()
    {
        return(true);
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

    /**Zooming anwenden*/
    public void DoZoom()
    {
        int i;
        Gdt_Element act_element;
        Grf_EditLabel act_label;

        /**Element-Initialgr��en aktualisieren*/
        for(i=0;i<gdt_elems.size();i++)
        {
            act_element = (Gdt_Element) gdt_elems.elementAt(i);
            act_element.UpdateDimension();
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
        RepaintElements();
        RepaintInfos();

        Set_Edited ();
    }

    /**RepaintInfos: Info-Labels neu berechnen und zeichnen*/
    public void RepaintInfos()
    {
        int i;
        Gdt_Element act_element;
        Grf_Label act_info;
        Dimension dim;

        super.RepaintInfos();

        /**Element-Info*/
        for(i=1;i<elem_info.size();i++)
        {
            act_info = (Grf_Label) elem_info.elementAt(i);

            if(act_info != null)
            {
                /**Font-Size aktualisieren*/
                act_info.setFont(new Font("SansSerif",Font.PLAIN,(int)(font_size*GetZoom())));

                /**Element-Lookup*/
                act_element = LookupIdentElement(i);
                if(act_element == null)
                    return;

                dim = act_info.getSize();
                act_info.setSize(dim);

                act_info.setLocation(act_element.getLocation().x-(dim.width-act_element.getSize().width)/2,
                        act_element.getLocation().y+act_element.getSize().height+2);
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
        if(command.equals("NEW_INTEGER"))
        {
            AddElementAtPoint(pt_x,pt_y,Pdt_Element.TYP_INTEGER);
        }

        if(command.equals("NEW_DOUBLE"))
        {
            AddElementAtPoint(pt_x,pt_y,Pdt_Element.TYP_DOUBLE);
        }

        if(command.equals("NEW_STRING"))
        {
            AddElementAtPoint(pt_x,pt_y,Pdt_Element.TYP_STRING);
        }

        /**Neues Label einf�gen*/
        if(command.equals("NEW_LABEL"))
        {
            AddLabelAtPoint(pt_x,pt_y);
        }

        /**Dokument ausrichten*/
        if(command.equals("ALIGN_ITEMS"))
        {
            Align();
        }
        
        /**Properties*/
        if(command.equals("PROP"))
        {
            prop = new Dlg_GdtProp (frm_mgr,this);                                              
        }        
    }

    /**Gdt_MouseEvent: Mouse-Event Verarbeitung*/
    class Gdt_MouseEvent extends java.awt.event.MouseAdapter
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
        MenuItem mi;
        //Super-Methode aufrufen
        super.Handle_MouseClicked(event);
        
        //Eigene Verarbeitung
        if(is_locked == true)
            return;

        right_pressed = (event.getModifiers() & InputEvent.META_MASK) != 0 ? true:false;

        /**Popup-Menu darstellen*/
   	    if(right_pressed)
   	    {
   	        frm_popup.show((Component)object,event.getX(),event.getY());
   	    }

   	    /**Neues Element durch doppelklicken*/
   	    if( (!right_pressed) && (event.getClickCount() == 2) )
   	    {
   	        AddElementAtPoint(event.getX(),event.getY(),std_content_typ);
   	    }
    }

    /**Handle_ComponentResized: overrideable aus Frm_Frame*/
    public void Handle_ComponentResized (ComponentEvent event)
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

    /**Handle_ItemStateChanged: overrideable aus Frm_Frame*/
    public void Handle_ItemStateChanged (ItemEvent event)
    {
        CheckboxMenuItem cmi,ucmi;
        //Super-Methode aufrufen
        super.Handle_ItemStateChanged(event);
    
        //eigene Verabeitung durchf�hren        
        cmi = (CheckboxMenuItem) event.getSource();

        /**Am Raster ausrichten*/
        if(cmi.getActionCommand().equals("ALIGN_GRID"))
        {
            Set_Align_Grid(cmi.getState());
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
        /**�berhaupt Elemente vorhanden*/
        if(gdt_elems.size() == 0)
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
        UnHighliteAllElements();
        
        DeleteElementMarkers();
        
        DeleteAllInfo();        
    }

    /**Set_Info: Info-Label neu setzen*/
    protected void Set_Info ()
    {
        String info;

        info = "";

        if(!Get_Result_Frame())
        {
            info += "Atomar Data Types";
            
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

    /**Set_Alg_Name: Algortihmus setzen*/
    public void Set_Alg_Name(String name)
    {
        super.Set_Alg_Name(name);

        Set_Info();
    }

    /**DeleteAllInfo: Alle Alg. Info-Label entfernen*/
    public void DeleteAllInfo ()
    {
        int i;

        //Alg-Infos entfernen
        super.DeleteAllInfo();

        /**Element-Info-Labels*/
        for (i=1;i<elem_info.size();i++)
        {
            /**aus Panel entfernen*/
            if(elem_info.elementAt(i) != null)
            {
                adm_panel.remove((Grf_Label) elem_info.elementAt(i));
            }
        }

        /**..und Labels l�schen*/
        elem_info = null;
        elem_info = new Vector();
    }

    /**DeleteElementInfo: Element Info-Label entfernen*/
    public void DeleteElementInfo (int element_number)
    {
        Grf_Label act;

        if( (elem_info.size() < (element_number+1)) || (elem_info.size() == 0) )
            return;

        try
        {
            act = (Grf_Label) elem_info.elementAt(element_number);
        }
        catch(ArrayIndexOutOfBoundsException exc)
        { return; }

        /**aus Panel entfernen*/
        adm_panel.remove(act);

        /**..und Label l�schen*/
        elem_info.setElementAt(null,element_number);
    }

    /**SetElementInfo: Alg. Info-Label an Element hinzuf�gen*/
    public void SetElementInfo (int element_number, String info_txt)
    {
        this.SetElementInfo(element_number,info_txt,true,false,null,null);
    }

    /**SetElementInfo: Alg. Info-Label an Element hinzuf�gen*/
    public void SetElementInfo (int element_number, String info_txt,
                             boolean transparent_bckg, boolean framed,
                             Color backg, Color foreg)
    {
        Grf_Label act_info;
        Gdt_Element gdt;
        Dimension dim;

        /**Capacity ggf. erh�hen*/
        if(elem_info.size() < (element_number+1))
            elem_info.setSize(element_number+1);

        try
        {
            act_info = (Grf_Label) elem_info.elementAt(element_number);
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
            elem_info.setElementAt(act_info,element_number);
            adm_panel.add(act_info,0);
        }
        else
        {
            act_info = (Grf_Label) elem_info.elementAt(element_number);
            act_info.setLabel(info_txt);
        }

        /**Location setzen*/
        gdt = LookupIdentElement(element_number);
        if(gdt == null)
            return;

        dim = act_info.getSize();

        act_info.setLocation(gdt.getLocation().x-(dim.width-gdt.getSize().width)/2,
                gdt.getLocation().y+gdt.getSize().height+2);
    }

    /**SetInfo: Alg. Info-Label hinzuf�gen, 1 basierte identification*/
    public void SetInfo (int identification, int x, int y, String info_txt)
    {
        this.SetInfo(identification,x,y,info_txt,true,false,null,null);
    }

    /**Get_DrawGraphics: R�ckgabe des Zeichen-Graphics*/
    public Graphics Get_DrawGraphics ()
    {
        return(adm_panel.getDblGraphics());
    }

    /**Size: Anzahl der verwalteten Elemente zur�ckgeben*/
    public int Size()
    {
        return(gdt_elems.size());
    }
    
    /**Get: R�ckgabe des Elementinhalts nach Element-Nr*/
    public Object Get(int element_number)
    {
        return(LookupIdentElement(element_number).Get());
    }    

    /**Get_Element: R�ckgabe des Pdt_Elements nach Nummer*/
    public Pdt_Element Get_Element(int id)
    {
        return(LookupIdentElement(id).Get_Element());
    }

    /**Set: Setzen des Elementwerts nach Element-Nr*/
    public void Set(int element_number, Object value)
    {
        Gdt_Element gdt;
        
        gdt = LookupIdentElement(element_number);
        if(gdt == null)
            return;
            
        gdt.Set(value);   
    }    
    
    /**GetId: Identificator eines Elements zur�ckgeben*/
    public String GetId (int element_number)
    {
        Gdt_Element gdt;
        gdt = LookupIdentElement(element_number);
        if(gdt == null)
            return("");
        
        return(gdt.GetId());
    }

    /**LinkToGUI: verbindet Elemente eines Id_Vector mit d. GUI*/
    private Id_Vector LinkToGUI (Id_Vector id_vec)
    {
        id_vec.LinkToGUI(this);

        return(id_vec);
    }

    /**Elementnummernmenge zur�ckgeben*/
    public Id_Vector ElementNrVector ()
    {
        return(LinkToGUI(pdt_manager.ElementNrVector()));                                  
    }    
}
