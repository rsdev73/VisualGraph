package visualgraph.swing;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import visualgraph.algorithms.Alg_Monitor;
import visualgraph.algorithms.Base_Algorithm;
import visualgraph.util.Uti_MultiLabel;

import java.io.*;
import java.lang.reflect.InvocationTargetException;

/**Klasse Frm_Frame: Basis-Klasse f�r Strukturmodell-Manager*/
public class Frm_Frame extends java.awt.Panel
{
    /**Ursprungsg��en-Gr��e*/
    protected static final int font_size = 9;
    protected static final int info_bottom_margin = 16;
    
    /**Unknown Count*/
    private static int unknown_cnt;
    
    /**Frame-Name*/
    protected String frf_name;

    /**Frame-Manager*/
    protected transient Frm_Manager frm_mgr;
    
    /**Result-Frame*/
    private boolean result_frame;

    /**Lock-State*/
    protected boolean is_locked;
    
    /**aktueller Zoom-Wert (Faktor)*/
    private double zoom;
    private int    zoom_percent;
    private double last_zoom;
    
    /**anzuwendender Algorithmus*/
    protected String alg_class_name;
    protected String alg_name;
    protected transient Base_Algorithm alg_base;
    
    protected long  delay_millis;

    /**Protokollieren*/
    protected boolean write_protocol;

    /**Variable-Monitor anzeigen*/
    protected boolean monitor_vars;
    
    /**Std. Alg. Info*/
    protected Grf_Label alg_std_info;
    
    /**Alg. Info-Labels*/
    protected Vector alg_info;

    /**Popup*/
    protected PopupMenu frm_popup;

    /**Vektoren d. Men�eintr�ge*/
    protected Vector alg_cmi;
    protected Vector delay_cmi;    

    /**Event-Adapter f�r Popup-Men�aktionen*/
    protected Frm_ItemEventAdapter    frm_item_event;
    protected Frm_ButtonAction        frm_button_action;

    /**Koord d. letzten Popup-Triggers*/
    protected transient int pt_x,pt_y;

    static final long serialVersionUID = -997113763533995729L;

    /**PrepareSerializedObject: Serialisierungsverarbeitung, overrideable*/
    public void PrepareSerializedObject(int prepare_mode) 
    {
        switch(prepare_mode)
        {
            case Frs_Manager.SER_BEFORE_WRITE:

                /**Popup-Menu eintr�gen kurzfristig entfernen*/
                if(Using_Popup())
            		Get_Container().remove(frm_popup);                
        		
        		/**Info-Felder entfernen*/
        		DeleteAllInfo();
        		
        		/**Lock entfernen*/
        		if(Get_Result_Frame() == false)
        		    Set_Lock(false);
        		    
                break;
                
            case Frs_Manager.SER_AFTER_WRITE:

                /**Popup-Menus wieder einh�ngen*/
                if(Using_Popup())
            		Get_Container().add(frm_popup);                        		
            		
                break;
                
            case Frs_Manager.SER_AFTER_READ:
            
                /**Popup wieder einh�ngen*/
                if(Using_Popup())
            		Get_Container().add(frm_popup);                   

                //Component-Listener registrieren
        		addComponentListener(new Frm_ComponentEvent());	           		        		
                break;
        }        
    }
    
    /**Konstruktoren*/
    public Frm_Frame()
    {        
        super(null);
        
        unknown_cnt += 1;
        frf_name = "Document "+unknown_cnt;
        
        /**Initial Zoom setzen*/
        zoom = 1d;
        last_zoom = zoom;
        zoom_percent = 100;

        /**Algorithmus initialisieren*/
        alg_name = "";
        alg_class_name = "";
        alg_base = null;
                
        write_protocol = false;
        monitor_vars = false;        
        
        /**Alg.-Info-Labels*/
        alg_info = new Vector();        
        
        alg_std_info = new Grf_Label(this,"",Uti_MultiLabel.CENTER,false,false);
        alg_std_info.setFont(new Font("SansSerif",Font.PLAIN,font_size));    
        alg_std_info.setVisible(false);        
                
        /**Men�eintragsvektoren bereitstellen*/
        alg_cmi = new Vector();
		delay_cmi = new Vector();        
		
		/**Men�eintrags-Listener erzeugen*/
        frm_item_event = new Frm_ItemEventAdapter();		
        frm_button_action = new Frm_ButtonAction();
        
        //Popup-Men� bereitstellen
		frm_popup = new PopupMenu();
		frm_popup.addActionListener(frm_button_action);		        
		
		//Component-Listener eintragen
		addComponentListener(new Frm_ComponentEvent());        		
   	}
 
    /**Set_Name*/
    public void Set_Name(String name)        
    {       
        if(name != "")
        {
            if(frf_name.equals(name) == false)
            {
                frf_name = name;
                
                if(frm_mgr != null)
                    frm_mgr.LayoutButtonList();
                    
                Set_Edited();    
            }
        }    
    }    

    /**Init_Alg_Menu: Initialisiert Alg.-Popup-Men� Eintr�ge*/ 
    public void Init_Alg_Menu ()
    {
        int i;
        MenuItem mi;        
        CheckboxMenuItem cmi;
        Menu mu;
        Vector alg_doks;
        
		frm_popup.addSeparator();
		
		/**Algorithmus-Menu*/
		mu = new Menu("Used Algorithm");
		frm_popup.add(mu);

		for(i=0;i<frm_mgr.GetAlgorithmNames().size();i++)
		{
		    alg_doks = frm_mgr.GetAlgorithmDoks();
		    
		    if(((String)alg_doks.elementAt(i)).equals(getClass().getName()))
		    {
        		cmi = new CheckboxMenuItem((String)frm_mgr.GetAlgorithmNames().elementAt(i));    		
    	    	cmi.addItemListener(frm_item_event);    		
    			cmi.setActionCommand("ALGO_"+(String)frm_mgr.GetAlgorithms().elementAt(i));		
        		mu.add(cmi);		    
        		
        		/**und der Liste hinzuf�gen*/
        		alg_cmi.addElement(cmi);
        	}	
		}
		
        /**Delay Menu*/
		mu = new Menu("Delay");
		frm_popup.add(mu);
	
		cmi = new CheckboxMenuItem("disabled");    		
    	cmi.addItemListener(frm_item_event);    		
		cmi.setActionCommand("DELAY_DISABLED");		
		cmi.setState(true);
		mu.add(cmi);		    
		delay_cmi.addElement(cmi);
		
		mu.addSeparator();
		
		cmi = new CheckboxMenuItem("100 ms");    		
    	cmi.addItemListener(frm_item_event);    		
		cmi.setActionCommand("DELAY_100");		
		cmi.setState(false);
		mu.add(cmi);		    
		delay_cmi.addElement(cmi);

		cmi = new CheckboxMenuItem("500 ms");    		
    	cmi.addItemListener(frm_item_event);    		
		cmi.setActionCommand("DELAY_500");		
		cmi.setState(false);
		mu.add(cmi);		    
		delay_cmi.addElement(cmi);

		cmi = new CheckboxMenuItem("1 s");    		
    	cmi.addItemListener(frm_item_event);    		
		cmi.setActionCommand("DELAY_1000");		
		cmi.setState(false);
		mu.add(cmi);		    
		delay_cmi.addElement(cmi);

		cmi = new CheckboxMenuItem("2 s");    		
    	cmi.addItemListener(frm_item_event);    		
		cmi.setActionCommand("DELAY_2000");		
		cmi.setState(false);
		mu.add(cmi);		    
		delay_cmi.addElement(cmi);

		cmi = new CheckboxMenuItem("5 s");    		
    	cmi.addItemListener(frm_item_event);    		
		cmi.setActionCommand("DELAY_5000");		
		cmi.setState(false);
		mu.add(cmi);		    
		delay_cmi.addElement(cmi);

		cmi = new CheckboxMenuItem("Other");    		
		cmi.setActionCommand("DELAY_OTHER");		
    	cmi.addItemListener(frm_item_event);    				
		cmi.setState(false);
		mu.add(cmi);		    
		delay_cmi.addElement(cmi);

    	cmi = new CheckboxMenuItem("Write Protocol",write_protocol);
    	cmi.addItemListener(frm_item_event);
		cmi.setActionCommand("WRITE_PROTO");		    	
        frm_popup.add(cmi);

    	cmi = new CheckboxMenuItem("Monitor Variables",monitor_vars);
    	cmi.addItemListener(frm_item_event);
		cmi.setActionCommand("MONITOR_VARS");		    	
        frm_popup.add(cmi);        
    }
    
    /**Get_Name*/
    public String Get_Name()        
    {
        return(frf_name);
    }

    /**Set_Frm_Mgr: nach Serialisierung*/
    public void Set_Frm_Mgr(Frm_Manager mgr)
    {
        frm_mgr = mgr;
    }
    
    /**Set_Result_Frame:*/
    public void Set_Result_Frame(boolean is)
    {
        result_frame = is;
    }

    /**Get_Result_Frame:*/
    public boolean Get_Result_Frame()
    {
        return(result_frame);
    }

    /**GetZoom: R�ckgabe aktueller Zoomwert*/
    public double GetZoom()
    {
        return(zoom);
    }

    /**GetLastZoom: R�ckgabe letzter Zoomwert*/
    public double GetLastZoom()
    {
        return(last_zoom);
    }

    /**GetZoomPercent: R�ckgabe aktueller Zoomwert in %*/
    public double GetZoomPercent()
    {
        return(zoom_percent);
    }

    /**SetZoom: Setzen aktueller Zoom*/
    public void SetZoom(int value)
    {
        last_zoom = zoom;        
        zoom = (float)value/100d;        
        zoom_percent = value;
    }
    
    /**Set_Edited: */
    protected void Set_Edited ()
    {
        frm_mgr.Set_Edited(true);
    }

    /**Get_Alg_Name: Algortihmus zur�ckgeben*/
    public String Get_Alg_Name()
    {
        return(alg_name);
    }

    /**Set_Alg_Name: Algortihmus setzen*/
    public void Set_Alg_Name(String name)
    {
        int i;
        
        if(alg_name.equals(name) == true)
            return;
            
        alg_name = name;

        if(name.equals(""))
        {
            alg_class_name = "";
            alg_base = null;
        }
        else
        {
       		for(i=0;i<frm_mgr.GetAlgorithmNames().size();i++)
    		{
                if(name.equals((String)frm_mgr.GetAlgorithmNames().elementAt(i)))
                {
                    alg_class_name = (String) frm_mgr.GetAlgorithms().elementAt(i);
                    break;
                }
            }
        }

        /**Button-Enabling in Frame-Mgr ansto�en*/
        frm_mgr.LayoutButtonEnabling();
                       
        Set_Edited ();
    }
    
    /**Get_Write_Protocol: Protocol anzeigen*/
    public boolean Get_Write_Protocol()
    {
        return(write_protocol);
    }

    /**Set_Write_Protocol: Protocol anzeigen setzen*/
    public void Set_Write_Protocol(boolean write_it)
    {
        if(write_protocol != write_it)
        {
            write_protocol = write_it;
            
            Set_Edited ();
        }
    }

    /**Get_Monitor_Vars: Variable-Monitor anzeigen*/
    public boolean Get_Monitor_Vars()
    {
        return(monitor_vars);
    }

    /**Set_Write_Protocol: Variable-Monitor setzen*/
    public void Set_Monitor_Vars(boolean monitor_it)
    {
        if(monitor_vars != monitor_it)
        {
            monitor_vars = monitor_it;
            
            Set_Edited ();
        }
    }
    
    /**RepaintInfos: Info-Labels neu berechnen und zeichnen*/
    public void RepaintInfos()
    {
        int i;
        Grf_Label act_info;
        Dimension dim;

        /**Alg-Info*/
        for(i=1;i<alg_info.size();i++)
        {
            act_info = (Grf_Label) alg_info.elementAt(i);

            if(alg_info != null)
            {
                /**Font Size aktualisieren*/
                act_info.setFont(new Font("SansSerif",Font.PLAIN,(int)(font_size*GetZoom())));

                /**Gr��e akutalisieren*/
                dim = act_info.getSize();        
                act_info.setSize(dim);            
                
                /**Location aktualisieren*/
                act_info.setLocation((int) (act_info.getLocation().x*(1/GetLastZoom())*GetZoom()),
                                (int) (act_info.getLocation().y*(1/GetLastZoom())*GetZoom()));        
            }            
        }
    }

    /**StartAlg: Startet die Ausf�hrung des hinterlegten Algorithmus*/
    public void StartAlg(int run_mode) 
    {
        Class alg_class;
        Base_Algorithm alg = null;
        Alg_Monitor moni;
                
        /**Klasse ermitteln*/
   	    try
       	{
       	    alg_class = Class.forName(alg_class_name); 
       	}
       	catch (ClassNotFoundException e) 
       	{
            System.err.println("ClassNotFoundException (StartAlg): "+e.getMessage());
            return;
       	};
        
        /**neue Instanz erzeugen*/
        try
        {
            alg = (Base_Algorithm) alg_class.getDeclaredConstructor().newInstance();
        }
        catch(InstantiationException e)
        {
            System.err.println("InstantiationException (StartAlg): "+e.getMessage());
            return;
        }
        catch(IllegalAccessException e)
        {
            System.err.println("IllegalAccessException (StartAlg): "+e.getMessage());
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
        
        try
        {
            /**Ergebnis-Frames des letzten Durchlaufs l�schen*/
            frm_mgr.DeleteResultFrames(this);

            /**Verweis setzen*/
            alg_base = alg;
            
            /**Alg. initialisieren*/
            alg.Init_Algorithm(this,frm_mgr);
        
            /**Status setzen*/
            alg_base.Set_Status(run_mode);        
            
            /**Button Enabling setzen*/
            frm_mgr.LayoutButtonEnabling();        
            
            /**Graf-Locken*/
            Set_Lock(true);
        
            /**Monitor instanzieren*/
            moni = new Alg_Monitor();
            moni.Init_Monitor(alg_base);
            
            /**..und Ausf�hrung starten*/
            alg.start();        
            
            /**Monitor starten*/
            moni.start();
        }
        catch(Exception e)
        {
            System.err.println("Exception (StartAlg): "+e.toString());            
        }
    }

    public void ResumeAlg(int run_mode) 
    {
        if(alg_base != null)
        {
            alg_base.Resume(run_mode);               
        }
    }
    
    public void StopAlg() 
    {        
        if(alg_base != null)
        {
            alg_base.Stop();
        }
    }    

    public void RewindAlg() 
    {
        if(alg_base != null)
        {
            alg_base.Rewind();
            
            alg_base = null;
                        
            /**Lock entfernen*/
            Set_Lock(false);
        }
    }    
        
    /**Status des definierten Algorithmus zur�ckgeben*/
    public int Get_Alg_Status()
    {
        if(alg_base != null)
        {
            return(alg_base.Get_Status());
        }

        return(Base_Algorithm.ALG_READY);
    }

    /**Get_Delay_Millis: Delay-Millis zur�ckgeben*/
    public long Get_Delay_Millis()
    {
        return(delay_millis);
    }

    /**Set_Delay_Millis: Delay-Millis setzen*/
    public void Set_Delay_Millis(long millis)
    {
        if(delay_millis == millis)
            return;
            
        delay_millis = millis;
        
        Set_Edited();
    }

    /**DeleteAllInfo: Alle Alg. Info-Label entfernen*/
    public void DeleteAllInfo ()
    {
        int i;

        /**Standard-Info ausblenden*/
        alg_std_info.setVisible(false);

        /**Alg-Info-Labels*/
        for (i=1;i<alg_info.size();i++)
        {
            /**aus Panel entfernen*/
            if(alg_info.elementAt(i) != null)
            {
                if(Get_Container() != null)
                    Get_Container().remove((Grf_Label) alg_info.elementAt(i));            
            }    
        }        
        
        /**..und Labels l�schen*/
        alg_info = null;
        alg_info = new Vector();        
    }
   
    /**DeleteInfo: Alg. Info-Label entfernen, 1 basierte identification*/
    public void DeleteInfo (int identification)
    {
        Grf_Label act;

        if(identification < 1)
            return;
        
        if( (alg_info.size() < (identification+1)) || (alg_info.size() == 0) )
            return;

        try
        {
            act = (Grf_Label) alg_info.elementAt(identification);
        }
        catch(ArrayIndexOutOfBoundsException exc)
        { return; }

        /**aus Panel entfernen*/
        if(Get_Container() != null)
            Get_Container().remove(act);            

        /**..und Label l�schen*/
        alg_info.setElementAt(null,identification);
    }

    /**AddInfoInt: Alg. Info-Label hinzuf�gen*/
    protected Grf_Label AddInfoInt (String info_txt, boolean transparent_bckg, boolean framed, 
                                    Color backg, Color foreg)
    {
        Grf_Label new_info;
        
        /**neues Grf_Label erzeugen*/
        new_info = new Grf_Label(this,info_txt,Uti_MultiLabel.CENTER,transparent_bckg,framed);        
        
        if(foreg != null)
            new_info.setForeground(foreg);
            
        if(backg != null)            
            new_info.setBackground(backg);
        
        new_info.setFont(new Font("SansSerif",Font.PLAIN,(int)(font_size*GetZoom())));        
                        
        return(new_info);        
    }
  
    /**SetInfo: Alg. Info-Label hinzuf�gen, 1 basierte identification*/
    public void SetInfo (int identification, int x, int y, String info_txt,
                        boolean transparent_bckg, boolean framed,
                        Color backg, Color foreg)
    {
        Grf_Label act_info;        
        if(identification < 1)
            return;
            
        /**Capacity ggf. erh�hen*/
        if(alg_info.size() < (identification+1))
            alg_info.setSize(identification+1);
        
        try
        {
            act_info = (Grf_Label) alg_info.elementAt(identification);
        }
        catch(ArrayIndexOutOfBoundsException exc)
        { return; }
        
        /**Pr�fen ob Label schon vorhanden        */
        if(act_info == null)
        {
            act_info = AddInfoInt (info_txt,transparent_bckg,framed,backg,foreg);
            if(act_info == null)
                return;

            /**und hinzuf�gen, anzeigen*/
            alg_info.setElementAt(act_info,identification);                                

            if(Get_Container() != null)
                Get_Container().add(act_info,0);                        
        }
        else
        {
            act_info = (Grf_Label) alg_info.elementAt(identification);
            act_info.setLabel(info_txt);
        }    
            
        /**Location setzen*/
        act_info.setLocation(x,y);        
    }

    /**Get_Alg_Class: R�ckgabe des Alg-Class Names*/
    public String Get_Alg_Class() 
    {
        if(alg_class_name.equals(""))
            return(null);
            
        return(alg_class_name);
    }    
    
    /**ClearStdInfo: Alg. Std. Info-Label l�schen*/
    public void ClearStdInfo ()
    {
       alg_std_info.setVisible(false);
    }
  
    /**SetStdInfo: Alg. Std. Info-Label setzen*/
    public void SetStdInfo (String info_txt)
    {
        int i,t_width,t_height;
        Dimension vp_size,dim;
        Vector frms;
        Frm_Frame run_frm=null;
        
        if(info_txt.equals("")==true)
            alg_std_info.setVisible(false);
            
        /**Text setzen*/
        alg_std_info.setLabel(info_txt);        
        
        dim = alg_std_info.getSize();
        
        /**neue Gr��e setzen*/
        alg_std_info.setSize(dim);
        
        t_width = dim.width;
        t_height = dim.height;
        
        /**Location setzen*/
        if(Get_ScrollPane() != null)
        {
            vp_size = Get_ScrollPane().getViewportSize();
        
            alg_std_info.setLocation(vp_size.width/2-dim.width/2,
                                Get_ScrollPane().getScrollPosition().y+
                                vp_size.height-info_bottom_margin-
                                dim.height);                                    
        }
        
        /**und anzeigen*/
        alg_std_info.setVisible(true);        
        
        /**Protocol fortschreiben*/                

        //Running-Frame ermitteln
        frms = frm_mgr.Get_Frm_Frames();
        
        for (i=0;i<frms.size();i++)
        {
            run_frm = (Frm_Frame) frms.elementAt(i);

            if(run_frm.alg_base != null)            
                break;
            else
                run_frm = null;
        }
        
        if(run_frm != null)
        {
            if(run_frm.Get_Write_Protocol())
            {                        
                info_txt = info_txt.replace('\n',' ');
                
                run_frm.alg_base.Get_Protocol().AppendLine(""+
                        run_frm.alg_base.Get_Next_Protocol_Line()+": "+info_txt);
            }
        }    
    }        

    /**Frm_ItemEventAdapter: f�r Popup-Menu*/
    class Frm_ItemEventAdapter implements ItemListener, Serializable 
    {    
		private static final long serialVersionUID = 6682352185279471467L;

		public void itemStateChanged(ItemEvent event)
        {   
            Handle_ItemStateChanged (event);
        }
        
        /**Serialize-Methoden*/
        private void writeObject(java.io.ObjectOutputStream out) throws IOException
        {
            out.defaultWriteObject();
        }        
        private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
        {
            in.defaultReadObject();        
        }        
    }    
    
    /**Men�eintrags-Event Verarbeitung auf Frame-Ebene, overrideable*/
    public void Handle_ItemStateChanged (ItemEvent event)
    {
        CheckboxMenuItem cmi,ucmi;
        int i,ii;
        
        cmi = (CheckboxMenuItem) event.getSource();
        
        /**Protokollieren*/
        if(cmi.getActionCommand().equals("WRITE_PROTO"))
        {
            Set_Write_Protocol(cmi.getState());
        }

        /**Protokollieren*/
        if(cmi.getActionCommand().equals("MONITOR_VARS"))
        {
            Set_Monitor_Vars(cmi.getState());
        }
        
        /**Alogrithmus setzen*/
        for(i=0;i<alg_cmi.size();i++)
        {
      		ucmi = (CheckboxMenuItem)alg_cmi.elementAt(i);            
      		
  		    //aktiviertes Element pr�fen
  		    if(cmi == ucmi)
  		    {
                if(cmi.getState())
                {
            		for(ii=0;ii<frm_mgr.GetAlgorithmNames().size();ii++)
            		{                    
            		    if(cmi.getActionCommand().equals("ALGO_"+(String)frm_mgr.GetAlgorithms().elementAt(ii)))            		    
            		    {
                            Set_Alg_Name((String) frm_mgr.GetAlgorithmNames().elementAt(ii));            		        
                            break;
            		    }
                    }
                }    
                else
                    Set_Alg_Name("");  		        
  		    }
  		    else
  		    {
                //alle anderen Eintr�ge unchecken
      		    ucmi.setState(false);
  		    }
        }

        /**Delay pr�fen*/
        if(cmi.getActionCommand().startsWith("DELAY_"))    		    
        {               
            if(cmi.getActionCommand().equals("DELAY_OTHER"))
            {
                cmi.setState(false);
                return;
            }
            
            cmi.setState(true);
                
	        /**andere Checks l�schen*/
	        for(i=0;i<delay_cmi.size();i++)
	        {
        		ucmi = (CheckboxMenuItem)delay_cmi.elementAt(i);

        		if(ucmi != cmi)
        		    ucmi.setState(false);
            }

            /**und setzen...*/
            if(cmi.getActionCommand().equals("DELAY_DISABLED"))
                delay_millis = 0;

            if(cmi.getActionCommand().equals("DELAY_100"))
                delay_millis = 100;

            if(cmi.getActionCommand().equals("DELAY_500"))
                delay_millis = 500;

            if(cmi.getActionCommand().equals("DELAY_1000"))
                delay_millis = 1000;                

            if(cmi.getActionCommand().equals("DELAY_2000"))
                delay_millis = 2000;                

            if(cmi.getActionCommand().equals("DELAY_5000"))
                delay_millis = 5000;                
                
            Set_Edited();                
		}                    
    }

    /**Frm_ButtonAction: PopupMenu Action-Verabeitung*/
	class Frm_ButtonAction implements java.awt.event.ActionListener, Serializable
	{
		private static final long serialVersionUID = 5566697698390858245L;
		
		public void actionPerformed(java.awt.event.ActionEvent event)
		{
            Handle_ActionEvent(event);
        }

        /**Serialize-Methoden*/
        private void writeObject(java.io.ObjectOutputStream out) throws IOException
        {
            out.defaultWriteObject();
        }       
        private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
        {
            in.defaultReadObject();        
        }        
    }
    
    /**Handle_ActionEvent: Action-Event Verarbeitung, overrideable*/
    public void Handle_ActionEvent (ActionEvent event)
    {
        if(is_locked == true)
            return;        

        event.getActionCommand();        
    }

    /**Handle_MouseClicked: Mouse-Click Verabeitung*/
    public void Handle_MouseClicked (MouseEvent event)
    {
        boolean right_pressed,found;
        Object object = event.getSource();
        CheckboxMenuItem cmi;
        MenuItem mi;
        int i;
        
        if(is_locked == true)
            return;

        right_pressed = (event.getModifiers() & InputEvent.META_MASK) != 0 ? true:false;

        /**Popup-Menu darstellen*/
   	    if(right_pressed)
   	    {
   	        pt_x = event.getX();
   	        pt_y = event.getY();

            /**Write-Protocoll*/
    		for(i=0;i<frm_popup.getItemCount();i++)
    		{
    		    mi = (MenuItem) frm_popup.getItem(i);

    		    if(mi.getActionCommand().equals("WRITE_PROTO"))
    		    {
    		        cmi = (CheckboxMenuItem) frm_popup.getItem(i);
    		        cmi.setState(Get_Write_Protocol());
    		        break;
    		    }
    		}

            /**Monitor-Varaibles*/
    		for(i=0;i<frm_popup.getItemCount();i++)
    		{
    		    mi = (MenuItem) frm_popup.getItem(i);

    		    if(mi.getActionCommand().equals("MONITOR_VARS"))
    		    {
    		        cmi = (CheckboxMenuItem) frm_popup.getItem(i);
    		        cmi.setState(Get_Monitor_Vars());
    		        break;
    		    }
    		}

    		/**Used-Algo*/
	        for(i=0;i<alg_cmi.size();i++)
	        {
        		cmi = (CheckboxMenuItem)alg_cmi.elementAt(i);

                cmi.setState(false);                                                                                
                
                if(alg_name.equals("") == false)
                {
                    if(cmi.getActionCommand().equals("ALGO_"+alg_class_name))
                        cmi.setState(true);                                                
                }
            }

    		/**Delay*/
    		if(delay_millis != 0)
    		{
        		found = false;
    	        for(i=0;i<delay_cmi.size();i++)
    	        {
            		cmi = (CheckboxMenuItem)delay_cmi.elementAt(i);

        		    if(cmi.getActionCommand().equals("DELAY_"+delay_millis))
        		    {
        		        cmi.setState(true);
        		        found = true;
        		    }
        		    else
        		        cmi.setState(false);
        		}

                if(!found)
                {
        	        for(i=0;i<delay_cmi.size();i++)
        	        {
                		cmi = (CheckboxMenuItem)delay_cmi.elementAt(i);

            		    if(cmi.getActionCommand().equals("DELAY_OTHER"))
            		    {
        		            cmi.setState(true);
        		            break;
            		    }
            		}
                }
            }
            else
            {
    	        for(i=0;i<delay_cmi.size();i++)
    	        {
            		cmi = (CheckboxMenuItem)delay_cmi.elementAt(i);

        		    if(cmi.getActionCommand().equals("DELAY_DISABLED"))
        		    {
        		        cmi.setState(true);
                        break;
        		    }
        		}
            }
   	    }
   }

    /**Frm_ComponentEvent: Resize Event auswerten*/
    class Frm_ComponentEvent extends java.awt.event.ComponentAdapter
    {
    	public void componentResized(java.awt.event.ComponentEvent event)
    	{
            Handle_ComponentResized(event);
    	}
    }
       
    /**Repaint: alle Components des Containers neu zeichnen*/
    public void Repaint()
    {
        if(Get_Container() != null)
            Get_Container().repaint();
    }
        
    /**Adapter - Overrideables*/
    public void InitialSize(int width, int height) {;}
    public Dimension RecalcPreferredDimension () { return(new Dimension(0,0)); }
    public boolean IsZoomable () { return(false); }    
    public void DoZoom() {;}    
    public void Set_Lock (boolean lock) {;}    
    public boolean Get_Lock () { return(false); }    
    public boolean Has_Properties () { return(false); }        
    public boolean IsRunable() { return(false); }
    public Graphics Get_DrawGraphics() {return(this.getGraphics()); }
    public Panel Get_Container() { return(null); }    
    public ScrollPane Get_ScrollPane() { return(null); }        
    public void DeleteLabel(Grf_EditLabel label) {;}
    public void HandleLabelMotion(Grf_EditLabel label_moved) {;}    
    public void Handle_ComponentResized (ComponentEvent event) {;}            
    public boolean Using_Popup() { return(false); }
    public String GetId (int id) { return(""); }    
}