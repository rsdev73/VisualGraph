package visualgraph.swing;
import java.awt.*;
import java.util.Vector;

import visualgraph.algorithms.Base_Algorithm;
import visualgraph.core.Adj_Connection;
import visualgraph.core.Pdt_Element;
import visualgraph.util.Uti_PictureButton;

import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;

/**Frame-Manager*/
public class Frm_Manager extends java.awt.Panel 
{
    /**Konstanten*/
    private static final int frm_height         = 22;
    private static final int pb_width           = 25;
    private static final int pb_scroll_width    = 16;
    private static final int scroll_step        = 50;
    private static final int inner_border       = 6;
    private static final int pb_font_size       = 9;
    
    /**Manager-Name*/
    private String frm_name;
    private String frm_file;
        
    /**�bergeordnetes Container-Panel*/
    private  Panel frm_top_panel;
    
    /**Verweis auf FrameSet-Manager*/
    private  Frs_Manager frs_mgr;
    
    /**Frame Scroller*/
    private  ScrollPane  frm_scroll;    
    private  Panel       frm_scroll_panel;
    
    /**Schaltfl�chen*/
    private Uti_PictureButton  frm_pb_left;
    private Uti_PictureButton  frm_pb_right;
    private Uti_PictureButton  frm_pb_one_view;
    private Uti_PictureButton  frm_pb_menu;
    
    private Uti_PictureButton  alg_pb_start;    
    private Uti_PictureButton  alg_pb_step;
    private Uti_PictureButton  alg_pb_rewind;
    private Uti_PictureButton  alg_pb_info;
    
    /**View-Panel*/
    private  Panel frm_view;

    /**Sub-View-Panels*/
    private  Vector frm_sub_views;
    
    /**Frame-List*/
    private  Vector frm_frames;

    /**Button-List*/
    private  Vector frm_pbs;

    /**Aktuell sichtbare Frames*/
    private  Vector frm_act_vis;

    /**Gestartetes Frame*/
    private Frm_Frame running_frame;

    /**Aktuell sichtbare Alg-Info-Frames*/
    private Vector frm_alg_infos;
    private Vector frm_alg_info_classes;
    
    /**�nderungsflag*/
    private boolean edited;
    
    /**Action-Listener-Object*/
    Frm_ButtonAction        frm_button_action;
    Frm_MouseEvents         frm_mouse_events;
    Frm_ItemEventAdapter    frm_item_event;
    
    /**Popup*/
    private  PopupMenu  frm_popup;
    private  Menu       zoom_menu;    
    
    /**Hilfsattribute*/
    private Vector zoom_cmi;
    
    private  boolean is_dragging = false;    
    private  int l_idx,r_idx;

    static final long serialVersionUID = -1747525614788542813L;
    
    /**Konstruktor*/
    public Frm_Manager (Panel top_panel, Frs_Manager mgr)
    {
        super(null);
        Dimension vp_size;
        Panel sub_panel;
        Font ft;
        MenuItem mi;
        Menu mu;
        CheckboxMenuItem cmi;

        /**eigenes Panel erzeugen, ohne Layout*/
        
        if(top_panel == null)
            return;
            
        frm_top_panel = top_panel;
        
        /**Verweise setzen*/
        frs_mgr = mgr;
        
        frm_file = "";
      
        running_frame = null;
        
        /**Gr��e und Position einstellen*/
        setLocation(0,0);
        setSize(frm_top_panel.getBounds().width,frm_top_panel.getBounds().height);
                       
        /**View-Panel einf�gen*/
        frm_view = new Panel(null);
        frm_view.setBackground(Color.lightGray);
        frm_view.setLocation(1,1);
        frm_view.setSize(frm_top_panel.getBounds().width-2,frm_top_panel.getBounds().height - frm_height-2);
        frm_view.addMouseMotionListener(new Frm_FrmViewMouseMotion()); 
        frm_view.addMouseListener(new Frm_FrmViewMouseListener());        
        this.add(frm_view);               
        
        /**Buttons einf�gen*/
        ft = new Font("SansSerif",Font.PLAIN,pb_font_size);

        alg_pb_start = new Uti_PictureButton (Apl_Context.Get_Img_Path()+"play.gif","Start");
        alg_pb_start.setLocation(0,frm_top_panel.getBounds().height - frm_height);
        alg_pb_start.setSize(pb_width,frm_height-1);
        alg_pb_start.setFont(ft);
        this.add(alg_pb_start);
        alg_pb_start.setEnabled(false);

        alg_pb_step = new Uti_PictureButton (Apl_Context.Get_Img_Path()+"step.gif","Step");
        alg_pb_step.setLocation(pb_width,frm_top_panel.getBounds().height - frm_height);
        alg_pb_step.setSize(pb_width,frm_height-1);
        alg_pb_step.setFont(ft);
        this.add(alg_pb_step);
        alg_pb_step.setEnabled(false);

        alg_pb_rewind = new Uti_PictureButton (Apl_Context.Get_Img_Path()+"rewind.gif","Rew");
        alg_pb_rewind.setLocation(2*pb_width,frm_top_panel.getBounds().height - frm_height);
        alg_pb_rewind.setSize(pb_width,frm_height-1);
        alg_pb_rewind.setFont(ft);
        this.add(alg_pb_rewind);
        alg_pb_rewind.setEnabled(false);

        alg_pb_info = new Uti_PictureButton (Apl_Context.Get_Img_Path()+"ainfo.gif","Inf");
        alg_pb_info.setLocation(3*pb_width,frm_top_panel.getBounds().height - frm_height);
        alg_pb_info.setSize(pb_width,frm_height-1);
        alg_pb_info.setFont(ft);
        this.add(alg_pb_info);
        alg_pb_info.setEnabled(false);

        frm_pb_menu = new Uti_PictureButton (Apl_Context.Get_Img_Path()+"menu.gif","Do");
        frm_pb_menu.setLocation(4*pb_width,frm_top_panel.getBounds().height - frm_height);
        frm_pb_menu.setSize(pb_width,frm_height-1);
        frm_pb_menu.setFont(ft);
        this.add(frm_pb_menu);
        frm_pb_menu.setEnabled(true);

        frm_pb_one_view = new Uti_PictureButton (Apl_Context.Get_Img_Path()+"view.gif","One");
        frm_pb_one_view.setLocation(frm_top_panel.getBounds().width-2*pb_scroll_width-pb_width,
                                    frm_top_panel.getBounds().height - frm_height);
        frm_pb_one_view.setSize(pb_width,frm_height-1);
        frm_pb_one_view.setFont(ft);
        this.add(frm_pb_one_view);
        frm_pb_one_view.setEnabled(false);

        frm_pb_left = new Uti_PictureButton (Apl_Context.Get_Img_Path()+"left.gif","<");
        frm_pb_left.setLocation(frm_top_panel.getBounds().width-2*pb_scroll_width,
                                    frm_top_panel.getBounds().height - frm_height);
        frm_pb_left.setSize(pb_scroll_width,frm_height-1);
        this.add(frm_pb_left);
        frm_pb_left.setEnabled(false);
		
        frm_pb_right = new Uti_PictureButton (Apl_Context.Get_Img_Path()+"right.gif",">");
        frm_pb_right.setLocation(frm_top_panel.getBounds().width-pb_scroll_width,
                                    frm_top_panel.getBounds().height - frm_height);
        frm_pb_right.setSize(pb_scroll_width,frm_height-1);
        frm_pb_right.setFont(ft);                
        this.add(frm_pb_right);
        frm_pb_right.setEnabled(false);
        		
        /**Scroller bereitstellen*/
        frm_scroll = new ScrollPane(ScrollPane.SCROLLBARS_NEVER);
        frm_scroll.setLocation(5*pb_width,frm_top_panel.getBounds().height - frm_height);
        frm_scroll.setSize(frm_top_panel.getBounds().width-6*pb_width-2*pb_scroll_width,frm_height);        
        this.add(frm_scroll);
        
        /**Scroll Panel-Resize*/
        frm_scroll_panel = new Panel(null);            
        frm_scroll_panel.setSize(frm_scroll.getBounds().width,frm_scroll.getBounds().height);
        frm_scroll.setBackground(Color.lightGray);        
        /**.. und einh�ngen*/
        frm_scroll.add(frm_scroll_panel);        
        
        /**Frame-List bereitstellen*/
        frm_frames = new Vector();

        /**Button-List bereitstellen*/
        frm_pbs = new Vector();

        /**Sichtbare Frames*/
        frm_act_vis = new Vector();                
        
        /**Sub-View Vector*/
        frm_sub_views = new Vector();
        
        /**Alg-Info Frame Vector erzeugen*/
        frm_alg_infos = new Vector();
        frm_alg_info_classes = new Vector();
        
        /**neues Sub-Panel erzeugen             */
        sub_panel = new Panel(null);
        sub_panel.setBackground(Color.white);
        sub_panel.setSize(frm_view.getSize());
        frm_sub_views.addElement(sub_panel);
        frm_view.add(sub_panel);
        
        /**Event-Listeners registrieren*/
		addComponentListener(new Frm_ComponentEvent());        
		
		/**Button-Action-Listeners registrieren*/
        frm_button_action = new Frm_ButtonAction();    
		frm_pb_left.addActionListener(frm_button_action);
		frm_pb_right.addActionListener(frm_button_action);		
		frm_pb_menu.addActionListener(frm_button_action);		
		alg_pb_start.addActionListener(frm_button_action);		
		alg_pb_step.addActionListener(frm_button_action);		
		alg_pb_rewind.addActionListener(frm_button_action);				
        alg_pb_info.addActionListener(frm_button_action);		        
        
		/**Button-Clickes*/
		frm_mouse_events = new Frm_MouseEvents();

        /**Listener f�r Button-Clicks registrieren*/
		frm_pb_one_view.addMouseListener(frm_mouse_events);		
		
		/**Popup-Menu einrichten*/
		frm_popup = new PopupMenu();

        /**Dokumente*/
    	mu = new Menu("New Graph");
    	frm_popup.add(mu);

    	mi = new MenuItem("Directed");
		mi.addActionListener(frm_button_action);
		mi.setActionCommand("NEW_DIRECTED_GRAPH");		
    	mu.add(mi);

    	mi = new MenuItem("Undirected");
		mi.addActionListener(frm_button_action);
		mi.setActionCommand("NEW_NOTDIRECTED_GRAPH");		
    	mu.add(mi);

    	mu = new Menu("New Sequence");
    	frm_popup.add(mu);

    	mi = new MenuItem("Integer");
		mi.addActionListener(frm_button_action);
		mi.setActionCommand("NEW_SEQUENCE_INTEGER");		
    	mu.add(mi);

    	mi = new MenuItem("Real");
		mi.addActionListener(frm_button_action);
		mi.setActionCommand("NEW_SEQUENCE_DOUBLE");		
    	mu.add(mi);

    	mi = new MenuItem("String");
		mi.addActionListener(frm_button_action);
		mi.setActionCommand("NEW_SEQUENCE_STRING");		
    	mu.add(mi);

        /*
    	mi = new MenuItem("New Atomar Data Types");
		mi.addActionListener(frm_button_action);
		mi.setActionCommand("NEW_PRIMITIVE_DT");		
		frm_popup.add(mi);
        */
        
    	mi = new MenuItem("New Documentation");
		mi.addActionListener(frm_button_action);
		mi.setActionCommand("NEW_DOCUMENTATION");		
		frm_popup.add(mi);

		frm_popup.addSeparator();

        /**Zoom-Menu*/
        frm_item_event = new Frm_ItemEventAdapter();
        
        zoom_cmi = new Vector();
        
		mu = new Menu("Zoom");
		frm_popup.add(mu);
        zoom_menu = mu;
        
		cmi = new CheckboxMenuItem("50%");
       	cmi.addItemListener(frm_item_event);    		
		cmi.setActionCommand("ZOOM_50");		
        zoom_cmi.addElement(cmi);		
		mu.add(cmi);

		cmi = new CheckboxMenuItem("80%");
       	cmi.addItemListener(frm_item_event);    		
		cmi.setActionCommand("ZOOM_80");	
        zoom_cmi.addElement(cmi);				
		mu.add(cmi);

		cmi = new CheckboxMenuItem("90%");
       	cmi.addItemListener(frm_item_event);    		
		cmi.setActionCommand("ZOOM_90");		
        zoom_cmi.addElement(cmi);				
		mu.add(cmi);

        mu.addSeparator();
        
		cmi = new CheckboxMenuItem("100%");
       	cmi.addItemListener(frm_item_event);    		
		cmi.setActionCommand("ZOOM_100");
        zoom_cmi.addElement(cmi);				
        cmi.setState(true);
		mu.add(cmi);

        mu.addSeparator();

		cmi = new CheckboxMenuItem("125%");
       	cmi.addItemListener(frm_item_event);    		
		cmi.setActionCommand("ZOOM_125");
        zoom_cmi.addElement(cmi);				
		mu.add(cmi);

		cmi = new CheckboxMenuItem("150%");
       	cmi.addItemListener(frm_item_event);    		
		cmi.setActionCommand("ZOOM_150");
        zoom_cmi.addElement(cmi);				
		mu.add(cmi);
        
		frm_popup.addSeparator();
        
        /**Closing und Props		*/
    	mi = new MenuItem("Close Document");
		mi.addActionListener(frm_button_action);
		mi.setActionCommand("CLOSE_FRAME");		
		frm_popup.add(mi);

    	mi = new MenuItem("-");    	
		frm_popup.add(mi);
		
    	mi = new MenuItem("Properties");
		mi.addActionListener(frm_button_action);
		mi.setActionCommand("PROP");		
		frm_popup.add(mi);
        
        /**Listener registrieren*/
   		frm_scroll_panel.addMouseListener(frm_mouse_events);		
		frm_scroll_panel.add(frm_popup);		
		
		/**..und anzeigen*/
		top_panel.add(this);                		
  	}
    
    /**Set_Name*/
    public void Set_Name(String name)        
    {
        frm_name = name;  
        
        Set_Edited(true);
    }

    /**Get_Name*/
    public String Get_Name()        
    {
        return(frm_name);
    }
    
    /**Get_Frm_Frames: f�r Serializierung*/
    public Vector Get_Frm_Frames()
    {
        return(frm_frames);
    }

    /**Get_Frm_Act_Vis: f�r Serializierung*/
    public Vector Get_Frm_Act_Vis ()
    {
        return(frm_act_vis);
    }

    /**Get_Frm_Alg_Infos:*/
    public Vector Get_Frm_Alg_Infos()
    {
        return(frm_alg_infos);
    }

    /**Get_Frm_Alg_Info_Classes:*/
    public Vector Get_Frm_Alg_Info_Classes()
    {
        return(frm_alg_info_classes);
    }

    /**Set_Frm_Alg_Infos:*/
    public void Set_Frm_Alg_Infos(Vector frm_alg_infos)
    {
        this.frm_alg_infos = frm_alg_infos;
    }

    /**Set_Frm_Alg_Info_Classes:*/
    public void Set_Frm_Alg_Info_Classes(Vector frm_alg_info_classes)
    {
        this.frm_alg_info_classes = frm_alg_info_classes;
    }
        
    /**Set_Frm_File*/
    public void Set_Frm_File (String file_name)
    {
        frm_file = file_name;
    }

    /**Get_Frm_File*/
    public String Get_Frm_File ()
    {
        return(frm_file);
    }
    
    /**Set_Edited: �nderungsflag setzen*/
    public void Set_Edited(boolean change)        
    {
        if(edited != change)
        {
            edited = change;
        
            if(frs_mgr != null)
                frs_mgr.LayoutButtonEnabling();
        }        
    }

    /**Get_Edited: �nderungsflag zur�ckgeben*/
    public boolean Get_Edited()        
    {
        return(edited);
    }

    /**GetAlgorithms: Stellt die registrierten Algorithmen-Klassennamen bereit*/
    public Vector GetAlgorithms()
    {
        return(frs_mgr.GetAlgorithms());
    }

    /**GetAlgorithms: Stellt die registrierten Algorithmen-Namen bereit*/
    public Vector GetAlgorithmNames()
    {
        return(frs_mgr.GetAlgorithmNames());
    }

    /**GetAlgorithmDoks: Stellt die registrierten Algorithmen-Dokumente bereit*/
    public Vector GetAlgorithmDoks()
    {
        return(frs_mgr.GetAlgorithmDoks());
    }        

    /**LayoutButtonEnabling: Button-enabling zeichnen*/
    public void LayoutButtonEnabling ()
    {
        int i;
        Button act_pb;
        Frm_Frame act_frame;
        boolean alg_stat;
        
        for (i=0;i<frm_pbs.size();i++)
        {
            act_pb = (Button) frm_pbs.elementAt(i);
            act_frame = (Frm_Frame) frm_frames.elementAt(i);

            if(frm_act_vis.contains((Object)act_frame) == true)
                act_pb.setForeground(Color.gray);
            else
                act_pb.setForeground(Color.black);                
        }
        
        if(frm_act_vis.size() <= 1)
            frm_pb_one_view.setEnabled(false);
        else
            frm_pb_one_view.setEnabled(true);        
            
        /**Alogrithmussteuerung-Enabling*/

        alg_stat = false;
        
        if(running_frame == null)
        {
            if(frm_act_vis.size() == 1)
            {
                act_frame = (Frm_Frame) frm_act_vis.elementAt(0);
                        
                if(act_frame.IsRunable() == true)
                {
                    alg_pb_start.ChangeImage (Apl_Context.Get_Img_Path()+"play.gif","Start");
                    
                    alg_pb_start.setEnabled(true);        
                    alg_pb_step.setEnabled(true);        
                    alg_pb_rewind.setEnabled(false);        
                    alg_pb_info.setEnabled(true);
                    
                    alg_stat = true;                                    
                }
            }
        }
        else
        {
            switch(running_frame.Get_Alg_Status())
            {
                case Base_Algorithm.ALG_RUNNING:                        
                case Base_Algorithm.ALG_RUNNING_STEP:                        
                
                    alg_pb_start.ChangeImage (Apl_Context.Get_Img_Path()+"pause.gif","Stop");
                    
                    alg_pb_start.setEnabled(true);   /**Stop-enabling     */
                    alg_pb_step.setEnabled(false);        
                    alg_pb_rewind.setEnabled(false);        
                    alg_pb_info.setEnabled(true);
                    break;

                case Base_Algorithm.ALG_STOPPED:                                                    
                    
                    alg_pb_start.ChangeImage (Apl_Context.Get_Img_Path()+"play.gif","Start");
                    
                    alg_pb_start.setEnabled(true);   
                    alg_pb_step.setEnabled(true);        
                    alg_pb_rewind.setEnabled(true);        
                    alg_pb_info.setEnabled(true);
                    break;

                case Base_Algorithm.ALG_FINISHED:                        

                    alg_pb_start.ChangeImage (Apl_Context.Get_Img_Path()+"play.gif","Start");
                    
                    alg_pb_start.setEnabled(false);   
                    alg_pb_step.setEnabled(false);        
                    alg_pb_rewind.setEnabled(true);        
                    alg_pb_info.setEnabled(true);
                    break;                        

                default:
                    break;
            }
    
            alg_stat = true;                
        }
        
        if(!alg_stat)
        {
            alg_pb_start.ChangeImage (Apl_Context.Get_Img_Path()+"play.gif","Start");
            
            alg_pb_start.setEnabled(false);        
            alg_pb_step.setEnabled(false);        
            alg_pb_rewind.setEnabled(false);     
            alg_pb_info.setEnabled(false);
        }    
    }
    
    /**LayoutButtonList: Button-List zeichnen*/
    public void LayoutButtonList ()
    {
        int i,ii,act_x,pb_width;
        Button act_pb;
        Font ft;
        Frm_Frame act_frame;        
        Component act_comp;
        boolean found;
        
        /**Ver�nderung d. Button List pr�fen*/
        for(i=0;i<frm_scroll_panel.getComponentCount();i++)
        {
            act_comp = frm_scroll_panel.getComponent(i);
            
            if(act_comp instanceof Button)
            {
                found = false;
                for (ii=0;ii<frm_pbs.size();ii++)
                {
                    act_pb = (Button) frm_pbs.elementAt(ii);
                    
                    /**ist Button noch vorhanden*/
                    if(act_pb == (Button)act_comp)
                    {
                        found = true;
                        break;
                    }    
                }
                if(!found)
                {
                    /**nein .. Component l�schen*/
                    frm_scroll_panel.remove(act_comp);
                }
            }
        }
        
        /**Alle Buttons entfernen*/
        /**frm_scroll_panel.removeAll();*/
        
        act_x = 0;
        
        /**..und neu aufbauen*/
        for (i=0;i<frm_pbs.size();i++)
        {
            act_pb = (Button) frm_pbs.elementAt(i);
            act_frame = (Frm_Frame) frm_frames.elementAt(i);
            
            /**Button Label setzen und sichtbar machen*/
            ft = new Font("SansSerif",Font.PLAIN,pb_font_size);
            act_pb.setFont(ft);
            act_pb.setLabel(act_frame.Get_Name());
            pb_width = act_pb.getFontMetrics(ft).stringWidth(act_frame.Get_Name())+15;
            act_pb.setSize(pb_width,frm_height-4);
            act_pb.setLocation(act_x,0);
            act_x += pb_width;
                
            /**ist Button noch nicht eingeh�ngt*/
            found = false;
            for(ii=0;ii<frm_scroll_panel.getComponentCount();ii++)
            {
                act_comp = frm_scroll_panel.getComponent(ii);
                
                if(act_comp == act_pb)
                {
                    found = true;
                    break;
                }
            }
            
            if(!found) /**einh�ngen*/
                frm_scroll_panel.add(act_pb);                   
        }

        /**Panel-Resize*/
        frm_scroll_panel.setSize(act_x,frm_scroll_panel.getSize().height);
        
        frm_scroll.doLayout();
                
        /**Button-aktivieren*/
        LayoutButtonEnabling();
        
        /**Scrollers aktualisieren*/
        LayoutScrollButtonEnabling();        
    }

    /**LayoutScrollButtonEnabling: Scroll-Button enabling*/
    private void LayoutScrollButtonEnabling ()
    {
        int act_scp,last_x;
        
        act_scp = frm_scroll.getScrollPosition().x;

        last_x = frm_scroll_panel.getSize().width;
        
        if(act_scp > 0)
            frm_pb_left.setEnabled(true);            
        else
            frm_pb_left.setEnabled(false);                    
            
        if( (act_scp+frm_scroll.getViewportSize().width) < last_x)
            frm_pb_right.setEnabled(true);               
        else            
            frm_pb_right.setEnabled(false);                                
    }
    
    /**AddFrame: neues Frame aufnehmen*/
    public void AddFrame(Frm_Frame frame)
    {        
        Button pb;
        Panel sub_panel;
                        
        /**Frame einh�ngen*/
        frm_frames.addElement(frame);
        
        /**neuer Button erzeugen*/
        pb = new Button(frame.Get_Name());
        frm_pbs.addElement(pb);
        
        /**Listener f�r Button registrieren*/
		pb.addMouseListener(frm_mouse_events);
        
        /**neuer Frame hidden*/
        frame.setVisible(false);

        /**ist noch kein Frame dargestellt ? -> darstellen                */
        if(frm_act_vis.size() == 0)
        {
            sub_panel = (Panel) frm_sub_views.elementAt(0);
            
            /**Initial-Size setzen*/
            frame.InitialSize(sub_panel.getBounds().width,sub_panel.getBounds().height);

            sub_panel.add(frame);
            
            frame.setVisible(true);

            /**Frame-Resize*/
            frame.dispatchEvent(new ComponentEvent(sub_panel,
                        ComponentEvent.COMPONENT_RESIZED));                          

            /**sichtbare Frame-List erweitern*/
            frm_act_vis.addElement(frame);            
        }

        /**Button-List neu zeichnen*/
        LayoutButtonList ();                
        
        Set_Edited(true);        
    }

    /**AddResultFrame: neues Result-Frame aufnehmen*/
    public void AddResultFrame(Frm_Frame frame)
    {        
        frame.Set_Result_Frame(true);
        frame.Set_Lock(true);
        
        AddFrame(frame);        
    }
    
    /**L�scht alle Ergebnis-Frames des aktuellen Sets, schaltet dann auf new_frame um*/ 
    public void DeleteResultFrames(Frm_Frame new_frame)
    {
        int i;
        boolean found;
        Frm_Frame act_frame;
        
        //new Frame �berhaupt vorhanden?
        if(!frm_frames.contains(new_frame))
            return;
        
        //SwitchTo new_frame
        if(new_frame.Get_Result_Frame())
            return;
            
        SwitchToFrame(new_frame);
        
        //ResultFrames l�schen
        found = false;
        
        for(;;)
        {
            for(i=0;i<frm_frames.size();i++)
            {
                act_frame = (Frm_Frame) frm_frames.elementAt(i);    

                if(act_frame.Get_Result_Frame() == true)
                {
                    found = true;
                    
                    /**Button entfernen*/
                    frm_pbs.removeElementAt(i);

                    /**Aus Frame-List entfernen*/
                    frm_frames.removeElementAt(i);

                    /**Button Re-Layout*/
                    LayoutButtonList();                

                    break;
                }
            }
            
            if(found == true)
                found = false;
            else
               break;
        }
    }
    
    /**Frame entfernen, switch_other d.h. Sicht Umschalten zu vorhandenem Frame*/
    public void DeleteFrame(Frm_Frame frame)
    {
        int i,idx,vis_idx,ainfo_idx;
        Panel sub_panel;
        Frm_Frame new_frame;
        
        if (frm_frames.contains(frame) == true)
        {
            idx = frm_frames.lastIndexOf(frame);
            if(idx != -1)
            {
                if(frm_act_vis.size() != 0)
                {
                    //Frame noch sichtbar ?*/
                    if(frm_act_vis.contains(frame) == true)
                    {
                        /**Umschaltalternative vorhanden ?*/
                        if(frm_frames.size() > 1)
                        {
                            //Umschalten zu anderem Frame
                            for(i=0;;i++)
                            {
                                new_frame = (Frm_Frame) frm_frames.elementAt(i);
                            
                                if(new_frame != frame)
                                    break;
                            }
                            
                            SwitchToFrame(new_frame);
                        }
                        else
                        {
                            /**keine Umschaltalternative, alles l�schen*/
                            vis_idx = frm_act_vis.lastIndexOf(frame);

                            if(vis_idx != -1)
                            {        
                                /**sub_panel-clear*/
                                sub_panel = (Panel) frm_sub_views.elementAt(vis_idx);         

                                sub_panel.setVisible(false);
                                sub_panel.removeAll();
                                
                                frm_view.remove(sub_panel);
                                frm_sub_views.removeElementAt(vis_idx);                                            
                                
                                frm_act_vis.removeElementAt(vis_idx);  
                            }
                            
                            if(frm_sub_views.size() == 0)                
                            {
                                sub_panel = new Panel(null);
                                sub_panel.setBackground(Color.white);                                
                                sub_panel.setSize(frm_view.getSize());
                                
                                frm_sub_views.addElement(sub_panel);
                                frm_view.add(sub_panel);
                            }                            
                        }
                    }
                }
                
                /**ist Frame in Alg-Info Frames ?*/
                if(frm_alg_infos.contains(frame))
                {
                    ainfo_idx = frm_alg_infos.indexOf(frame);
                    
                    frm_alg_infos.removeElementAt(ainfo_idx);
                    frm_alg_info_classes.removeElementAt(ainfo_idx);
                }
                
                /**Button entfernen*/
                frm_pbs.removeElementAt(idx);

                /**Aus Frame-List entfernen*/
                frm_frames.removeElementAt(idx);

                /**Button Re-Layout*/
                LayoutButtonList();                
            }
            
            Set_Edited(true);
        }
    }
    
    /**SwitchToFrame: Anzeige wechseln auf Frame*/
    public void SwitchToFrame(Frm_Frame frame)
    {        
        int i;
        Frm_Frame act_frame;
	    Panel sub_panel,act_sub_panel;
	    
        if(frame == null)
            return;

        //Frame �berhaupt eingetragen?    
        if(frm_frames.contains(frame) == false)
            return;

        /**wird frame schon angezeigt ?*/
        if(frm_act_vis.size() == 1)
        {
            if(((Frm_Frame)frm_act_vis.elementAt(0)) == frame)
                return;
        }              
                    
        /**...Switch*/
 
        /**Sub-Panels entfernen*/
        for (i=0;i<frm_sub_views.size();i++)
        {
            act_frame = (Frm_Frame) frm_act_vis.elementAt(i);                        
            act_frame.setVisible(false);
            
            act_sub_panel = (Panel) frm_sub_views.elementAt(i);
            
            /**Frame aus Sub-Panel aush�ngen*/
            act_sub_panel.remove(act_frame);                                  
            
            /**und Sub-Panel aush�ngen*/
            frm_view.remove(act_sub_panel);
        }

        /**sichtbare Frame-List l�schen*/
        frm_act_vis = null;
        frm_act_vis = new Vector();

        /**Sub-Panel-Liste l�schen*/
        frm_sub_views = null;
        frm_sub_views = new Vector();
                    
        /**Neues Frame sichtbar machen*/                            
        frame.setVisible(true);                             
        
        sub_panel = new Panel(null);
        sub_panel.setBackground(Color.white);                        
        sub_panel.setSize(frm_view.getSize());
        sub_panel.setVisible(false);
        
        frm_sub_views.addElement(sub_panel);                        
        frm_view.add(sub_panel);                                               
                                        
        /**Initial-Size setzen*/
        frame.InitialSize(sub_panel.getBounds().width,sub_panel.getBounds().height);
        
        sub_panel.add(frame);
        
        /**Sicht switchen*/
        sub_panel.setVisible(true);
        
        /**sichtbare Frame-List erweitern*/
        frm_act_vis.addElement(frame);            
                    
        /**Button-Enabling neu zeichnen*/
        LayoutButtonEnabling ();
        
        if(getParent() != null)
            Handle_ComponentResized(new ComponentEvent(getParent(),ComponentEvent.COMPONENT_RESIZED));                    

        /**Frame neu zeichnen*/
        frame.Repaint();                
        
        Set_Edited(true);
    }

    /**AddFrameToView: Frame der Anzeige hinzuf�gen*/
    public void AddFrameToView (Frm_Frame frame)
    {
        Frm_Frame act_frame;
        int i,f_width,avail_width,total_width,diff_width;
	    Panel sub_panel,act_sub_panel;
        Vector frm_dims;
        Dimension act_dim;
        float fakt;

        if(frame == null)
            return;
        
        //Frame �berhaupt eingetragen?    
        if(frm_frames.contains(frame) == false)
            return;
            
        //Frame schon in Ansicht ?
        if(frm_act_vis.contains(frame) == true)
            return;

        //..switch        
        frame.setVisible(true);

        //sichtbare Frame-List erweitern
        frm_act_vis.addElement(frame);

        //neues Panel erzeugen
        sub_panel = new Panel(null);
        sub_panel.setBackground(Color.white);
        frm_sub_views.addElement(sub_panel);
        sub_panel.setVisible(false);
        
        //neues Panel einh�ngen
        frm_view.add(sub_panel);

        //Frame einh�ngen
        sub_panel.add(frame);

        if(frm_act_vis.size() != 0)
        {
            //Frames resizen            
            avail_width = frm_view.getSize().width-(frm_act_vis.size()-1)*inner_border;

            //Dimension-Vektor init
            frm_dims = new Vector();

            //Ben�tigte Gr��en der Frames ermitteln
            total_width = 0;
            for(i=0;i<frm_act_vis.size();i++)
            {
                act_frame = (Frm_Frame) frm_act_vis.elementAt(i);
                act_dim = act_frame.RecalcPreferredDimension();

                frm_dims.addElement(act_dim);

                total_width += act_dim.width;
            }

            //Differenz-Gr��en brechnen
            if(total_width > avail_width)
            {
                diff_width = total_width - avail_width;

                fakt = (float)diff_width/(float)total_width;

                for(i=0;i<frm_act_vis.size();i++)
                {
                    act_dim = (Dimension) frm_dims.elementAt(i);
                    act_dim.width -= (int) ((float)act_dim.width*fakt);
                }
            }

            //Gr��en setzen
            total_width = 0;

            for(i=0;i<frm_act_vis.size();i++)
            {
                act_dim = (Dimension) frm_dims.elementAt(i);

                f_width = act_dim.width;

                act_frame = (Frm_Frame) frm_act_vis.elementAt(i);

                act_sub_panel = (Panel) frm_sub_views.elementAt(i);
                act_sub_panel.setLocation(total_width+(i*inner_border),0);

                //letztes Panel angleichen
                if(i == (frm_act_vis.size()-1))
                {
                    f_width = frm_view.getSize().width - (total_width+(i*inner_border));
                }

                act_sub_panel.setSize(f_width,frm_view.getSize().height);

                total_width += f_width;

                //Initial-Size neu setzen
                act_frame.InitialSize(act_sub_panel.getBounds().width,
                        act_sub_panel.getBounds().height);
            }

            //Dimensions l�schen
            frm_dims = null;
       
            sub_panel.setVisible(true);            
        }
               
        //Button-Enabling neu zeichnen
        LayoutButtonEnabling ();

        if(getParent() != null)
            Handle_ComponentResized(new ComponentEvent(getParent(),ComponentEvent.COMPONENT_RESIZED));

        //Frame neu zeichnen
        frame.Repaint();
        
        Set_Edited(true);
    }

    /**Frm_ShowAlgInfo: Anzeige der Ablaufinformation eines Alg.*/
    private void ShowAlgInfo ()
    {
        int i;
        Class alg_class;
        String alg_class_name,alg_name,showed_class;
        Frm_Frame act_frame,switch_frame;
        Base_Algorithm alg = null;
        Frm_Documentation frm_info;
        Vector reg_names,reg_classes;
        
        alg_class_name = null;
        
        if(running_frame == null)
        {
            if(frm_act_vis.size() == 1)
            {
                act_frame = (Frm_Frame) frm_act_vis.elementAt(0);

                if(act_frame.IsRunable())
                {
                    alg_class_name = act_frame.Get_Alg_Class();
                    
                    if(alg_class_name == null)
                        return;
                }    
            }
        }
        else
        {
            alg_class_name = running_frame.Get_Alg_Class();
            
            if(alg_class_name == null)
                return;
        }
        
        /**ermitteln des Alg-Names*/
        reg_classes = GetAlgorithms();
        reg_names = GetAlgorithmNames();
        
        alg_name = "";
        for(i=0;i<reg_classes.size();i++)
        {
            if(((String)(reg_classes.elementAt(i))).equals(alg_class_name))
            {
                alg_name = (String) reg_names.elementAt(i);
                break;
            }
        }
        
        for(i=0;i<frm_alg_info_classes.size();i++)
        {
            showed_class = (String) frm_alg_info_classes.elementAt(i);
            
            if(showed_class.equals(alg_class_name))
            {
                switch_frame = (Frm_Frame)frm_alg_infos.elementAt(i);
                
                if(!frm_act_vis.contains(switch_frame))                
                    AddFrameToView(switch_frame);
                    
                return;
            }                    
        }
        
        /**Alg.Info wird noch nicht angezeigt -> AddToView*/
        frm_info = new Frm_Documentation("Algorithm "+alg_name,this);
            
        /**Klasse ermitteln*/
   	    try
       	{
       	    alg_class = Class.forName(alg_class_name); 
       	}
       	catch (ClassNotFoundException e) 
       	{
            System.err.println("ClassNotFoundException (Frm_ShowAlgInfo): "+e.getMessage());
            return;
       	};

        /**neue Instanz erzeugen*/
        try
        {
            alg = (Base_Algorithm) alg_class.getDeclaredConstructor().newInstance();
        }
        catch(InstantiationException e)
        {
            System.err.println("InstantiationException (Frm_ShowAlgInfo): "+e.getMessage());
            return;
        }
        catch(IllegalAccessException e)
        {
            System.err.println("IllegalAccessException (Frm_ShowAlgInfo): "+e.getMessage());
            return;
        } catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        /**Infos eintragen*/
        frm_info.AppendLine(alg_name+"-Algorithm Information:");
        frm_info.AppendLine("");
        
        /**nicht editierbar!*/
        frm_info.Set_Lock(true);
        
        alg.info(frm_info);
        
        /**..und registrieren*/
        frm_alg_info_classes.addElement(alg_class_name);
        frm_alg_infos.addElement(frm_info);
        
        AddFrame(frm_info);
        
        AddFrameToView(frm_info);
    }
    
    /**Resize-Event-Verarbeitung*/
    class Frm_ComponentEvent extends java.awt.event.ComponentAdapter
    {
    	public void componentResized(java.awt.event.ComponentEvent event)
    	{
    	    Handle_ComponentResized(event);
        }
    }

    public synchronized void Handle_ComponentResized (ComponentEvent event)
    {
        int i,act_x,f_width,loc_x;
        Frm_Frame act_frf;
        Button act_pb;
        Panel act_panel;
        float fakt_x;
        
        /**Panel Resize*/
        setSize(frm_top_panel.getBounds().width,frm_top_panel.getBounds().height);        

        /**Gr��en�nderungsfaktor berechnen*/
        fakt_x = (float) (frm_top_panel.getBounds().width-2) / frm_view.getSize().width;
        
        /**neue Gr��e setzen*/
        frm_view.setSize(frm_top_panel.getBounds().width-2,frm_top_panel.getBounds().height - frm_height - 2);

        alg_pb_start.setLocation(0,frm_top_panel.getBounds().height - frm_height);
        alg_pb_step.setLocation(pb_width,frm_top_panel.getBounds().height - frm_height);
        alg_pb_rewind.setLocation(2*pb_width,frm_top_panel.getBounds().height - frm_height);
        alg_pb_info.setLocation(3*pb_width,frm_top_panel.getBounds().height - frm_height);        
        frm_pb_menu.setLocation(4*pb_width,frm_top_panel.getBounds().height - frm_height);

        frm_pb_one_view.setLocation(frm_top_panel.getBounds().width-2*pb_scroll_width-pb_width,
                                    frm_top_panel.getBounds().height - frm_height);
        frm_pb_left.setLocation(frm_top_panel.getBounds().width-2*pb_scroll_width,frm_top_panel.getBounds().height - frm_height);
        frm_pb_right.setLocation(frm_top_panel.getBounds().width-pb_scroll_width,frm_top_panel.getBounds().height - frm_height);
        
        frm_scroll.setLocation(5*pb_width,frm_top_panel.getBounds().height - frm_height);        
        frm_scroll.setSize(frm_top_panel.getBounds().width-6*pb_width-2*pb_scroll_width,frm_height);

        /**Scroll-Panel neu setzen*/
        act_x = 0;
        for (i=0;i<frm_pbs.size();i++)
        {
            act_pb = (Button) frm_pbs.elementAt(i);
            act_x += act_pb.getSize().width;
        }    

        /**Panel-Resize*/
        frm_scroll_panel.setSize(act_x,frm_scroll_panel.getSize().height);
        
        frm_scroll.doLayout();
                     
        /**Frames im urspr�nglichen Verh�ltnis resizen*/

        if(frm_act_vis.size() != 0)
        {
            loc_x = 0;
            
            for(i=0;i<frm_act_vis.size();i++)
            {                        
                act_frf = (Frm_Frame) frm_act_vis.elementAt(i);

                act_panel = (Panel) frm_sub_views.elementAt(i);
                act_panel.setLocation(loc_x,0);
                
                f_width = (int) (((float)(act_panel.getSize().width) * fakt_x));
                
                /**letztes Panel angleichen*/
                if(i == (frm_act_vis.size()-1))
                {
                    f_width = frm_view.getSize().width - act_panel.getLocation().x;
                }
                
                act_panel.setSize(f_width,frm_view.getSize().height);
                
                loc_x = act_panel.getLocation().x + act_panel.getSize().width+inner_border;
                
                /**Initial-Size neu setzen*/
                act_frf.InitialSize(act_panel.getBounds().width,
                        act_panel.getBounds().height);
            } 
        }
        else
        {
            /**Sub-Panel 0 resize*/
            act_panel = (Panel) frm_sub_views.elementAt(0);
            act_panel.setSize(frm_view.getSize().width,frm_view.getSize().height);
        }
        
        /**Scroll-Buttons �berpr�fen*/
        LayoutScrollButtonEnabling();
    }
    
    /**Class Frm_MouseEvents: Adapter f�r MouseEvents*/
	class Frm_MouseEvents extends MouseAdapter
	{
	    public void mouseReleased(MouseEvent event)
        {
            Handle_MouseReleased(event);
        }
    }

    public void Handle_MouseReleased (MouseEvent event)
    {
        MenuItem mi;
        CheckboxMenuItem act_cmi;
        int i,idx;
	    boolean shift_pressed,right_pressed,cont;
	    Frm_Frame frame;

 	    Object object = event.getSource();		
 	    
        cont = false;
        idx = 0;
                    
        shift_pressed = (event.getModifiers() & InputEvent.SHIFT_MASK) != 0 ? true:false;                                    
        right_pressed = (event.getModifiers() & InputEvent.META_MASK) != 0 ? true:false;

        /**Popup-Menu darstellen       	    */
   	    if(right_pressed)
   	    {
   	        /**MenuItem-Enabling setzen*/
   	        for(i=0;i<frm_popup.getItemCount();i++)
   	        {
   	            mi = frm_popup.getItem(i);
   	            
   	            if(mi.getActionCommand() == "CLOSE_FRAME")
   	            {
   	                if(frm_act_vis.size() == 1)
   	                    mi.setEnabled(true);
   	                else
   	                    mi.setEnabled(false);
   	            }
   	            
   	            if(mi.getActionCommand() == "PROP")
   	            {
   	                if(frm_act_vis.size() == 1)
   	                {
                        frame = (Frm_Frame) frm_act_vis.elementAt(0);                        

                        if(frame.Has_Properties())
       	                    mi.setEnabled(true);
       	                else
       	                    mi.setEnabled(false);       	                           	                
       	            }
   	                else
   	                    mi.setEnabled(false);       	                
   	            }
   	        }

            if(frm_act_vis.size() == 1)
            {
                frame = (Frm_Frame) frm_act_vis.elementAt(0);                        

                if(frame.IsZoomable())
                {
                    zoom_menu.setEnabled(true);
                    
                    /**Zoom-Check setzen*/
                    
                    for(i=0;i<zoom_cmi.size();i++)
                    {
                        act_cmi = (CheckboxMenuItem) zoom_cmi.elementAt(i);
                        
                        if(act_cmi.getActionCommand().equals("ZOOM_"+((int)frame.GetZoomPercent())))
                        {
                            act_cmi.setState(true);
                        }
                        else
                            act_cmi.setState(false);                        
                    }
                }    
                else
                    zoom_menu.setEnabled(false);       	                           	                
            }
            else
                zoom_menu.setEnabled(false);       	                
   	        
   	        frm_popup.show((Component)object,event.getX(),event.getY());
   	        return;
   	    }
                
        if(object == frm_pb_one_view)
        {
            /**OneView-Click -> aktuelle Sicht l�schen und erstes Frame anzeigen*/
            idx = 0;
            cont = true;
            shift_pressed = false;
        }
        else
        {
            if (frm_pbs.contains(object) == true)
            {
                cont = true;
                idx = frm_pbs.lastIndexOf(object);                                    
            }
        }

        /**Frame-Button Event*/
        if(cont == true)
        {                
            if (idx != -1)
            {                    
                if( (!shift_pressed) && (!event.isPopupTrigger()) )
                {
                    frame = (Frm_Frame) frm_frames.elementAt(idx);
                    
                    SwitchToFrame(frame);
                }

                if( (shift_pressed) && (!event.isPopupTrigger()))
                {                
                    frame = (Frm_Frame) frm_frames.elementAt(idx);                                              

                    AddFrameToView(frame);                    
                }
            }
        }            
    }
    
    /**Button-Action Event Adapter*/
	class Frm_ButtonAction implements java.awt.event.ActionListener
	{
		public void actionPerformed(java.awt.event.ActionEvent event)
		{
            Handle_ActionPerformed (event);
   		}
	}

    public void Handle_ActionPerformed (ActionEvent event)
    {
        int act_scp,last_x;
		Object object = event.getSource();		
	    String command;
        Grf_Manager new_gm;
        Frm_Documentation new_doc;
        Gdt_Manager new_gdt;
        Gse_Manager new_gse;        
        Frm_Frame frame;
        Dlg_Confirm cdlg;        
        boolean commit;
        
        /**Popup-Commands auswerten*/
        command = event.getActionCommand();
            
        /**Seite l�schen*/
        if(command.equals("CLOSE_FRAME"))
        {
            if(frm_act_vis.size() == 1)
            {
                commit = false;

                frame = (Frm_Frame)frm_act_vis.elementAt(0);
                
                if( (!frame.Get_Result_Frame()) && (frm_alg_infos.contains(frame) == false) )
                {
                    cdlg = new Dlg_Confirm (frame.Get_Name(), 
                            "Really close document? Content will be lost!", Dlg_Confirm.DLG_YES_NO, frm_top_panel);
                                    
                    if(cdlg.Get_Result() == Dlg_Confirm.RES_YES)
                        commit = true;
                }
                else
                    commit = true;
                
                if(commit)    
                    DeleteFrame (frame);
            }
        }
        
        /**gerichteter Graph erzeugen*/
        if(command.equals("NEW_DIRECTED_GRAPH"))
        {
            new_gm = new Grf_Manager(Adj_Connection.TypDirected,Frm_Manager.this);

            if(new_gm != null)
            {
                AddFrame(new_gm);
                SwitchToFrame(new_gm);
            }    
        }

        /**ungerichteter Graph erzeugen*/
        if(command.equals("NEW_NOTDIRECTED_GRAPH"))
        {
            new_gm = new Grf_Manager(Adj_Connection.TypNotDirected,Frm_Manager.this);

            if(new_gm != null)
            {
                AddFrame(new_gm);
                SwitchToFrame(new_gm);                    
            }    
        }

        /**Atomar DT-Dokument erzeugen*/
        if(command.equals("NEW_PRIMITIVE_DT"))
        {            
            new_gdt = new Gdt_Manager(Frm_Manager.this);

            if(new_gdt != null)
            {
                AddFrame(new_gdt);
                SwitchToFrame(new_gdt);                    
            }    
        }

        /**Sequence-Dokumente erzeugen*/
        if(command.equals("NEW_SEQUENCE_INTEGER"))
        {            
            new_gse = new Gse_Manager(Pdt_Element.TYP_INTEGER,Frm_Manager.this);

            if(new_gse != null)
            {
                AddFrame(new_gse);
                SwitchToFrame(new_gse);                    
            }    
        }

        if(command.equals("NEW_SEQUENCE_DOUBLE"))
        {            
            new_gse = new Gse_Manager(Pdt_Element.TYP_DOUBLE,Frm_Manager.this);

            if(new_gse != null)
            {
                AddFrame(new_gse);
                SwitchToFrame(new_gse);                    
            }    
        }

        if(command.equals("NEW_SEQUENCE_STRING"))
        {            
            new_gse = new Gse_Manager(Pdt_Element.TYP_STRING,Frm_Manager.this);

            if(new_gse != null)
            {
                AddFrame(new_gse);
                SwitchToFrame(new_gse);                    
            }    
        }

        /**Dokumentationsseite erzeugen*/
        if(command.equals("NEW_DOCUMENTATION"))
        {
            new_doc = new Frm_Documentation(this);

            if(new_doc != null)
            {
                AddFrame(new_doc);
                SwitchToFrame(new_doc);                    
            }    
        }

        /**Eigenschaften des aktuellen Frames �ffnen*/
        if(command.equals("PROP"))
        {
            if(frm_act_vis.size() == 1)
            {
                frame = (Frm_Frame)frm_act_vis.elementAt(0);                    
                               
                /**direkter Aufruf*/
                frame.Handle_ActionEvent(
                    new ActionEvent(frame,ActionEvent.ACTION_PERFORMED,"PROP"));
            }
        }

        /**Alg-Info anzeigen*/
        if(object == alg_pb_info)
        {
            ShowAlgInfo();
        }

        if(object == alg_pb_start)
        {
            if(running_frame == null)
            {
                if(frm_act_vis.size() == 1)
                {
                    frame = (Frm_Frame)frm_act_vis.elementAt(0);                    
                
                    if(frame.IsRunable() == true)
                        running_frame = frame;
                }
            }    
            
            if(running_frame != null)
            {
                switch(running_frame.Get_Alg_Status())
                {
                    case Base_Algorithm.ALG_READY:

                        running_frame.StartAlg(Base_Algorithm.ALG_RUNNING);
                        break;
                    
                    case Base_Algorithm.ALG_STOPPED:                                            
                        running_frame.ResumeAlg(Base_Algorithm.ALG_RUNNING);
                        break;
                    
                    case Base_Algorithm.ALG_RUNNING:
                    case Base_Algorithm.ALG_RUNNING_STEP:
                                         
                        running_frame.StopAlg();
                        break;
                    
                    default:
                        break;                            
                }    
            }    
        }

        if(object == alg_pb_step)
        {
            if(running_frame == null)
            {
                if(frm_act_vis.size() == 1)
                {
                    frame = (Frm_Frame)frm_act_vis.elementAt(0);                    
                
                    if(frame.IsRunable() == true)
                        running_frame = frame;
                }
            }    

            if(running_frame!=null)
            {
                switch(running_frame.Get_Alg_Status())
                {
                    case Base_Algorithm.ALG_READY:
                    
                        running_frame.StartAlg(Base_Algorithm.ALG_RUNNING_STEP);
                        break;
                         
                    case Base_Algorithm.ALG_STOPPED:                                            

                        running_frame.ResumeAlg(Base_Algorithm.ALG_RUNNING_STEP);                      
                        break;

                    default:
                        break;
                }    
            }            
        }

        if(object == alg_pb_rewind)
        {
            if(running_frame != null)
            {
                switch(running_frame.Get_Alg_Status())
                {
                    case Base_Algorithm.ALG_STOPPED:
                    case Base_Algorithm.ALG_FINISHED:
                                         
                        running_frame.RewindAlg();

                        running_frame = null;       
                        
                        LayoutButtonEnabling();
                        
                        break;

                    default:
                        break;
                }    
            }    
        }
        
        /**Action-Menu Button*/
        if(object == frm_pb_menu)
        {
            /**Popup-Menu Event absetzen*/

            frm_scroll_panel.dispatchEvent(new MouseEvent(frm_scroll_panel,MouseEvent.MOUSE_RELEASED,0l,
                        MouseEvent.META_MASK,-2*pb_width,0,1,false));                
        }
        
        /**Scroll-Button Event*/
        if(object == frm_pb_left)
        {
            act_scp = frm_scroll.getScrollPosition().x;
            
            if( (act_scp-scroll_step) > 0)
                act_scp -= scroll_step;
            else
                act_scp = 0;
                
            frm_scroll.setScrollPosition(act_scp,0);
            
            LayoutScrollButtonEnabling();
        }
        
        if(object == frm_pb_right)
        {
            act_scp = frm_scroll.getScrollPosition().x;

            last_x = frm_scroll_panel.getSize().width;

            if( (act_scp+frm_scroll.getViewportSize().width+scroll_step) <= last_x)
                act_scp += scroll_step;
            else
                act_scp = last_x - frm_scroll.getViewportSize().width;
            
            frm_scroll.setScrollPosition(act_scp,0);                
            
            LayoutScrollButtonEnabling();
        }
    }
    
    /**Mouse-Motion-Adapter f�r Sub-View-Panel-Resize*/
    class Frm_FrmViewMouseMotion extends MouseMotionAdapter
    {
        public void mouseDragged(MouseEvent e)
        {
            Handle_MouseDragged (e);
        }
    }

    public void Handle_MouseDragged (MouseEvent e)
    {
        int i,dx;
        Panel l_panel=null,r_panel=null;
        Point mp;
        boolean found;
        Frm_Frame l_frame=null,r_frame=null;
        
        Object object = e.getSource();
        
        if(object == (Object) frm_view)
        {
            /**nochmal G�ltigkeit pr�fen*/
            if(frm_act_vis.size() <= 1)
                return;

            mp = e.getPoint();
            
            found = false;
            
            if(is_dragging == false)                   
            {
                /**ermitteln des linken Sub-Views*/
                for(i=0;i<(frm_sub_views.size()-1);i++)
                {
                    l_panel = (Panel) frm_sub_views.elementAt(i);
                    r_panel = (Panel) frm_sub_views.elementAt(i+1);
                    
                    if( ( mp.x >= (l_panel.getLocation().x+l_panel.getSize().width-20) ) &&
                        ( mp.x <= r_panel.getLocation().x+20) )
                    {
                        l_frame = (Frm_Frame) frm_act_vis.elementAt(i);
                        r_frame = (Frm_Frame) frm_act_vis.elementAt(i+1);
                        
                        is_dragging = true;
                        
                        l_idx = i;
                        r_idx = i+1;
                        
                        found = true;
                        break;
                    }                    
                }
            }
            else
            {                   
                l_panel = (Panel) frm_sub_views.elementAt(l_idx);
                r_panel = (Panel) frm_sub_views.elementAt(r_idx);

                l_frame = (Frm_Frame) frm_act_vis.elementAt(l_idx);
                r_frame = (Frm_Frame) frm_act_vis.elementAt(r_idx);                    
                
                found = true;
            }
            
            if(found)
            {            
                /**linker Sub-View Resize*/
                dx = mp.x - (l_panel.getLocation().x+l_panel.getSize().width);
                
                l_panel.setSize(l_panel.getSize().width + dx,l_panel.getSize().height);
                r_panel.setLocation(r_panel.getLocation().x+dx,r_panel.getLocation().y);
                
                /**ist r_panel letztes Panel -> dann Breite ad�quat setzen*/
                if (r_idx == (frm_sub_views.size()-1))
                    r_panel.setSize(frm_view.getSize().width-r_panel.getLocation().x,r_panel.getSize().height);                    
                else
                    r_panel.setSize(r_panel.getSize().width - dx,r_panel.getSize().height);                    

                /**Initial-Size neu setzen*/
                l_frame.InitialSize(l_panel.getBounds().width,
                            l_panel.getBounds().height);

                /**Initial-Size neu setzen*/
                r_frame.InitialSize(r_panel.getBounds().width,
                            r_panel.getBounds().height);                                
            }   
        }                    
    }
    
    /**Mouse-Motion-Adapter f�r Sub-View-Panel-Resize - Cursor-Switching*/
    class Frm_FrmViewMouseListener extends MouseAdapter
    {    
        public void mouseExited(MouseEvent e)
        {
            Handle_FrmViewMouseExited (e);
        }

        public void mouseEntered(MouseEvent e)
        {
            Handle_FrmViewMouseEntered (e);
        }        
        
        /**Dragging-Reset*/
        public void mouseReleased(MouseEvent e)
        {
            Handle_FrmViewMouseReleased (e);
        }
    }

    public void Handle_FrmViewMouseExited (MouseEvent e)
    {
        Object object = e.getSource();            
        
        if(object == (Object) frm_view)            
        {
            if (frm_view.getCursor().getType() != Cursor.DEFAULT_CURSOR) 
            {
                frm_view.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); 
            }
        }
    }

    public void Handle_FrmViewMouseEntered (MouseEvent e)
    {
            Object object = e.getSource();            
            
            if(object == (Object) frm_view)            
            {
                if (frm_view.getCursor().getType() != Cursor.W_RESIZE_CURSOR) 
                {
                    frm_view.setCursor(new Cursor(Cursor.W_RESIZE_CURSOR)); 
                }
            }                
    }

    public void Handle_FrmViewMouseReleased (MouseEvent e)
    {
        Frm_Frame l_frame,r_frame;            
        Object object = e.getSource();            
                
        if(object == (Object) frm_view)            
        {
            if(is_dragging == true)
            {
                is_dragging = false;
                    
                l_frame = (Frm_Frame) frm_act_vis.elementAt(l_idx);
                r_frame = (Frm_Frame) frm_act_vis.elementAt(r_idx);                    
            }                            
            repaint();
        }
    }

    /**Frm_ItemEventAdapter: f�r Popup-Menu*/
    class Frm_ItemEventAdapter implements ItemListener
    {    
        public void itemStateChanged(ItemEvent event)
        {   
            Handle_ItemStateChanged (event);
        }
    }    

    public void Handle_ItemStateChanged (ItemEvent event)
    {
        CheckboxMenuItem cmi,ucmi;
        int i;
        Frm_Frame act_frame;
        
        cmi = (CheckboxMenuItem) event.getSource();

        if(frm_act_vis.size() != 1)
            return;
            
        act_frame = (Frm_Frame) frm_act_vis.elementAt(0);            
        
        if(cmi.getActionCommand().startsWith("ZOOM_"))    		    
        {   
            /**Zooming setzen            */
            if(cmi.getState() == false)
                cmi.setState(true);
                
	        /**andere Checks l�schen*/
	        for(i=0;i<zoom_cmi.size();i++)
	        {
        		ucmi = (CheckboxMenuItem)zoom_cmi.elementAt(i);

        		if(ucmi != cmi)
        		    ucmi.setState(false);
            }

            /**und setzen...*/
            if(cmi.getActionCommand().equals("ZOOM_50"))
                act_frame.SetZoom(50);

            if(cmi.getActionCommand().equals("ZOOM_80"))
                act_frame.SetZoom(80);

            if(cmi.getActionCommand().equals("ZOOM_90"))
                act_frame.SetZoom(90);

            if(cmi.getActionCommand().equals("ZOOM_100"))
                act_frame.SetZoom(100);

            if(cmi.getActionCommand().equals("ZOOM_125"))
                act_frame.SetZoom(125);

            if(cmi.getActionCommand().equals("ZOOM_150"))
                act_frame.SetZoom(150);
            
            /**Zoom durchf�hren*/
            act_frame.DoZoom();
		}
    }
        
    /**GUI-Override*/
    public void paint(Graphics g)
    {
        g.setColor(Color.black);
        g.drawRect(0,0,frm_top_panel.getBounds().width-1,frm_top_panel.getBounds().height - frm_height - 1);
    }
}
