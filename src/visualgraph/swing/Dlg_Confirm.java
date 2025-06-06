package visualgraph.swing;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Dlg_Confirm extends Object
{
    /**Dialogtypen*/
    public final static int DLG_CONFIRM      = 0;
    public final static int DLG_OK_CANCEL    = 1;
    public final static int DLG_YES_NO       = 2;
    public final static int DLG_INPUT_TXT    = 3;
    
    /**Results*/
    public final static int RES_CANCEL       = 0;
    public final static int RES_OK           = 1;
    public final static int RES_NO           = 0;
    public final static int RES_YES          = 1;    

    private Panel cont_panel;
    
    private Label ta_text;
    private TextField tf_text;
    
    private Button pb_ok;
    private Button pb_cancel;
    
    private int dlg_type;
    
    private Frame parent_frame;
    private Dialog dlg;
    
    private Dlg_ActionEventAdapter dlg_action_event;
        
    private int result;
    private String result_text="";
    
    /**Konstruktor*/
    public Dlg_Confirm (String title, String text, int type, Component src)
    {   
        int d_width,d_height;        
        FontMetrics fm;
        StringTokenizer st;
        Insets inset;
        
        result = -1;
        
        dlg_type = type;
        
        /**Parent-frame erzeugen*/
        parent_frame = new Frame();
        
        dlg = new Dialog(parent_frame,title,true);
        dlg.setResizable(false);
        dlg.setFont(new Font("SansSerif",Font.PLAIN,10));
        
        inset = Apl_Context.Get_Insets();
        
        fm = dlg.getFontMetrics(dlg.getFont());
        d_width = fm.stringWidth(text);
        
        st = new StringTokenizer(text,"\n");        
        d_height = (1+st.countTokens())*fm.getHeight();

        if(type != DLG_INPUT_TXT)
            dlg.setSize(d_width+60,inset.bottom + d_height+80);        
        else
            dlg.setSize(d_width+60+30*fm.charWidth('-')+10,inset.bottom + d_height+80);        
                    
        dlg.setLocation(src.getLocation().x+src.getSize().width/2-(dlg.getSize().width/2),
                        src.getLocation().y+src.getSize().height/2-(dlg.getSize().height/2));
        
        parent_frame.setLocation(dlg.getLocation().x,dlg.getLocation().y);
        
        cont_panel = new Panel(null);
        dlg.add(cont_panel);

        dlg_action_event = new Dlg_ActionEventAdapter();
        
        if(type != DLG_INPUT_TXT)
        {
            ta_text = new Label(text);
            ta_text.setLocation(30,10);
            ta_text.setSize(d_width,d_height);
            
            cont_panel.add(ta_text);
        }
        else
        {
            ta_text = new Label(text);
            ta_text.setLocation(30,10);
            ta_text.setSize(d_width,d_height);
            cont_panel.add(ta_text);

            tf_text = new TextField();
            tf_text.setLocation(30+d_width+10,14);
            tf_text.setSize(30*fm.charWidth('-'),d_height-6);
            tf_text.addActionListener(dlg_action_event);                        
            cont_panel.add(tf_text);            
        }
        
        dlg.addWindowListener(new Dlg_WindowAdapter());
                
        switch(type)
        {
            case DLG_CONFIRM:            
                
                pb_ok = new Button ("Ok");
                pb_ok.setBounds(dlg.getSize().width/2-25,dlg.getSize().height-60-(inset.bottom),50,20);                
                pb_ok.addActionListener(dlg_action_event);
                cont_panel.add(pb_ok);
                
                pb_ok.requestFocus();
                
                break;
                
            case DLG_OK_CANCEL:
                
                pb_ok = new Button ("Ok");
                pb_ok.setBounds(dlg.getSize().width/2-60,dlg.getSize().height-60-(inset.bottom),50,20);                
                pb_ok.addActionListener(dlg_action_event);
                cont_panel.add(pb_ok);
                
                pb_cancel = new Button ("Cancel");
                pb_cancel.setBounds(dlg.getSize().width/2+10,dlg.getSize().height-60-(inset.bottom),50,20);                
                pb_cancel.addActionListener(dlg_action_event);
                cont_panel.add(pb_cancel);

                pb_ok.requestFocus();
                break;
                
            case DLG_YES_NO:

                pb_ok = new Button ("Yes");
                pb_ok.setBounds(dlg.getSize().width/2-60,dlg.getSize().height-60-(inset.bottom),50,20);                
                pb_ok.addActionListener(dlg_action_event);
                cont_panel.add(pb_ok);
                
                pb_cancel = new Button ("No");
                pb_cancel.setBounds(dlg.getSize().width/2+10,dlg.getSize().height-60-(inset.bottom),50,20);                
                pb_cancel.addActionListener(dlg_action_event);
                cont_panel.add(pb_cancel);

                pb_ok.requestFocus();
                break;
                
            case DLG_INPUT_TXT:    
                pb_ok = new Button ("Ok");
                pb_ok.setBounds(dlg.getSize().width/2-60,dlg.getSize().height-60-(inset.bottom),50,20);                
                pb_ok.addActionListener(dlg_action_event);
                cont_panel.add(pb_ok);
                
                pb_cancel = new Button ("Cancel");
                pb_cancel.setBounds(dlg.getSize().width/2+10,dlg.getSize().height-60-(inset.bottom),50,20);                
                pb_cancel.addActionListener(dlg_action_event);
                cont_panel.add(pb_cancel);

                pb_ok.requestFocus();
                break;
        }
        
        dlg.setVisible(true);
    }

    /**Get_Result:*/
    public int Get_Result()
    {
        return(result);
    }

    /**Get_Result_Text:*/
    public String Get_Result_Text()
    {
        return(result_text);
    }
 
	class Dlg_WindowAdapter extends java.awt.event.WindowAdapter
	{
		public void windowClosing(java.awt.event.WindowEvent event)
		{
		    Dlg_Handle_ActionEvent(new ActionEvent(pb_cancel,ActionEvent.ACTION_PERFORMED,""));
		}
	}
    
    class Dlg_ActionEventAdapter implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            Dlg_Handle_ActionEvent(e);
        }
    }
    
    public void Dlg_Handle_ActionEvent(ActionEvent e)
    {
        Object object;
        
        object = (Object) e.getSource();
        
        if(result == -1)
        {
            switch (dlg_type)
            {
                case DLG_CONFIRM:   
                
                    result = RES_OK;                
                    break;
                    
                case DLG_OK_CANCEL:
                    
                    if(object == pb_ok)
                    {
                        result = RES_OK;                    
                    }
                    if(object == pb_cancel)
                    {
                        result = RES_CANCEL;                    
                    }
                    
                    break;
                    
                case DLG_YES_NO:

                    if(object == pb_ok)
                    {
                        result = RES_YES;                    
                    }
                    if(object == pb_cancel)
                    {
                        result = RES_NO;                    
                    }            
                    break;
                    
                case DLG_INPUT_TXT:                        
                    
                    result_text = tf_text.getText();

                    if(object == tf_text)
                    {
                        result = RES_OK;                    
                    }                    
                    if(object == pb_ok)
                    {
                        result = RES_OK;                    
                    }
                    if(object == pb_cancel)
                    {
                        result = RES_CANCEL;                    
                    }
                    break;                    
            }
        }
        
        dlg.dispose();
        parent_frame.dispose();

        if(Apl_Context.Get_Applet() != null)
        {
            Apl_Context.Get_Applet().requestFocus();
        }    
    }
 }
