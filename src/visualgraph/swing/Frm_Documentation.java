package visualgraph.swing;
import java.awt.*;
import java.awt.event.*;

/**Frm_Documentation: Dokumentationsseite*/
public class Frm_Documentation extends Frm_Frame
{
    /**Ursprungsg��en-Gr��e*/
    private static final int font_size = 9;

    /**TextArea*/
    private TextArea  txt_area;

    /**Property-Dialog*/
    private transient Dlg_DocProp prop;

    static final long serialVersionUID = 7882799401458441106L;            
    
    /**PrepareSerializedObject: Serialisierungsverarbeitung*/
    public void PrepareSerializedObject(int prepare_mode) 
    {
        //Super-Methode aufrufen
        super.PrepareSerializedObject(prepare_mode);
        
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
                
                /**neue Listeners erzeugen*/
           		txt_area.addTextListener(new Frm_TextEventAdapter());            
                break;
        }        
    }
    
    /**Konstruktoren*/
    public Frm_Documentation (Frm_Manager mgr)
    {
        this("",mgr);
  	}

    public Frm_Documentation (String name, Frm_Manager mgr)
    {   
        super();
        
        /**Verweise setzen*/
        frm_mgr = mgr;
        
        /**Text-Area bereitstellen*/

        txt_area = new TextArea();
        add(txt_area);
               
        txt_area.setSize(getSize().width,getSize().height);
   		txt_area.addTextListener(new Frm_TextEventAdapter());
   		txt_area.setFont(InitialFont());
   		
        /**Name initialisieren*/
        Set_Name(name);        
    }

    /**InitialFont: Initialisierungsgr��e Fonts zur�ckgeben*/
    private Font InitialFont()
    {
        return(new Font("SansSerif",Font.PLAIN,(int) 
                    (font_size*GetZoom())));     
    }    

    /**InitialFont: Initialisierungsgr��en aktualisieren*/
    public void UpdateDimension()
    {   
        /**Font-Setzen*/
        txt_area.setFont(InitialFont());       
    }

    /**InitialSize: Initial Size setzen*/
    public void InitialSize(int width, int height)
    {
        if(getSize().equals(new Dimension(width,height)) == false)
        {
            setSize(width,height);        

            /**TextArea Resize*/
            txt_area.setSize(width,height);            
        }    
    }
    
    /**RecalcNeededDimension: Berechnet die aktuell ben�tigte Gr��e des Frames*/
    public Dimension RecalcPreferredDimension () 
    { 
        return(txt_area.getPreferredSize()); 
    }   

    /**AppendLine: Textzeile mit NL anh�ngen*/
    public void AppendLine (String text)
    {
        txt_area.append(text+"\n");
        
        Set_Edited();
    }

    /**Append: Textzeile ohne NL anh�ngen*/
    public void Append (String text)
    {
        txt_area.append(text);

        Set_Edited();
    }

    /**AppendSeparatorLine: Trennlinie einf�gen*/
    public void AppendSeparatorLine ()
    {
        int i;
        String line="";
        
        for (i=0;i<5;i++)
            line = line +"----------";
        
        AppendLine(line);
    }
    
    /**IsZoomable: Zoomm�glichkeiten*/
    public boolean IsZoomable () 
    { 
        return(true);
    }
 
    /**Set_Lock: Locking setzen*/
    public void Set_Lock (boolean lock) 
    {
        txt_area.setEditable(!lock);       
        
        Set_Edited();
    }
    
    /**Get_Lock: Locking ermitteln*/
    public boolean Get_Lock () 
    {         
        return(!txt_area.isEditable()); 
    }    

    /**Zooming anwenden*/
    public void DoZoom()
    {        
        Container cont_panel;
        
        cont_panel = (Container) getParent();

        UpdateDimension();
        
        /**Panel-Resize*/
        setSize(cont_panel.getSize().width,cont_panel.getSize().height);        
        
        /**TextArea Resize*/
        txt_area.setSize(cont_panel.getSize().width,cont_panel.getSize().height);
        
        Set_Edited ();
    }
    
    //Handle_ComponentResized: Override aus Frm_Frame
    public void Handle_ComponentResized (ComponentEvent event)
    {
        Container cont_panel;
        
        cont_panel = (Container) getParent();
        
        if(cont_panel.getSize().equals(getSize()) == false)
        {
            /**Panel-Resize*/
            setSize(cont_panel.getSize().width,cont_panel.getSize().height);        
            
            /**TextArea Resize*/
            txt_area.setSize(cont_panel.getSize().width,cont_panel.getSize().height);
        }
    }
    
    /**TextEventAdapter: TextEvent Verarbeitung*/
    class Frm_TextEventAdapter implements TextListener
    {
        public void textValueChanged(TextEvent e)
        {
            Handle_TextEvent (e);
        }
    }
    
    public void Handle_TextEvent (TextEvent e)
    {
        Set_Edited();
    }    

	/**Action-Event Handling*/
	class Frm_ActionAdapter implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
		    Handle_ActionEvent(event);
		}
	}

	public void Handle_ActionEvent(ActionEvent event)
	{
	    Object object;
        
	    object = (Object) event.getSource();
        
        if(event.getActionCommand().equals("PROP"))
        {
            prop = new Dlg_DocProp (frm_mgr,this);
        }
	}

    /**Has_Properties: Sind Eigenschaften vorhanden ?*/
    public boolean Has_Properties () 
    { 
        return(true);
    }        

    /**Get_Container: R�ckgabe des Component-Containers*/
    public Panel Get_Container() 
    {
        return(this);
    }           
}
