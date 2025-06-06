package visualgraph.swing;
import java.awt.*;
import java.awt.event.*;

public class Dlg_NodeProp extends Panel
{
    /**Verweis auf Top-Panel*/
    private Panel top_panel;
    
    private Grf_Node grn;
    private Grf_Manager grm;

    private Dlg_ActionAdapter action_adapter;
    
    /**GUI-Elemente*/
	Label label1;
	Label label2;
	Label label3;
	TextField tf_number;
    TextArea  tf_caption;
    TextField tf_weight;
	Button pb_ok;
	Button pb_cancel;
    
    static final long serialVersionUID = -1238691785100313100L;
    
    /**Konstruktor*/
    public Dlg_NodeProp (Grf_Manager grm, Grf_Node grn)
    {
        super (null);
                
        top_panel = grm.Get_Container();
        
        this.grm = grm;
        this.grn = grn;

        /**Lock setzen*/
        grm.Set_Lock(true);
        
		setFont(new Font("SansSerif", Font.PLAIN, 9));       
        setBackground(Color.lightGray);
        
        /**Gr��e und Location*/
        setSize(217,162);
        setLocation(grn.getLocation().x,grn.getLocation().y);
                    
		label1 = new Label("Node number");
		label1.setBounds(getInsets().left + 12,getInsets().top + 14,64,18);
		add(label1);
		label2 = new Label("Node caption");
		label2.setBounds(getInsets().left + 12,getInsets().top + 42,64,18);
		add(label2);
		label3 = new Label("Node weight");
		label3.setBounds(getInsets().left + 12,getInsets().top + 84,64,18);
		add(label3);

		tf_number = new TextField();
		tf_number.setBounds(getInsets().left + 80,getInsets().top + 12,42,20);
		tf_number.setEditable(false);
		add(tf_number);		
		tf_caption = new TextArea("",0,0,TextArea.SCROLLBARS_NONE);
		add(tf_caption);
		tf_caption.setBounds(getInsets().left + 80,getInsets().top + 40,124,40);
		tf_weight = new TextField();
		add(tf_weight);
		tf_weight.setBounds(getInsets().left + 80,getInsets().top + 82,42,20);

		pb_ok = new Button();
		pb_ok.setActionCommand("button");
		pb_ok.setLabel("Ok");
		pb_ok.setBounds(getInsets().left + 136,getInsets().top + 132,68,19);
		add(pb_ok);
		pb_cancel = new Button();
		pb_cancel.setActionCommand("button");
		pb_cancel.setLabel("Cancel");
		pb_cancel.setBounds(getInsets().left + 60,getInsets().top + 132,68,19);
		add(pb_cancel);
		
		/**Event-Listeners registrieren*/
		action_adapter = new Dlg_ActionAdapter();
                		
		pb_ok.addActionListener(action_adapter);
		pb_cancel.addActionListener(action_adapter);
    	tf_weight.addActionListener(action_adapter);
    	
		/**addMouseMotionListener(new Dlg_MouseMotion());*/
		
        /**Value-Set				*/
        tf_number.setText(""+grn.GetId());
        tf_caption.setText(grn.Get_Node_Caption());
        tf_weight.setText(""+grn.Get_Node_Weight());
                
        /**..und einh�ngen*/
        top_panel.add(this,0);                        
        
        tf_caption.requestFocus();
	}

	/**Action-Event Handling	*/
	class Dlg_ActionAdapter implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
		    Dlg_HandleActionEvent(event);
		}
	}

	public void Dlg_HandleActionEvent(ActionEvent event)
	{
	    Object object;
	    
	    object = (Object) event.getSource();
	    
	    if( (object == tf_weight) || (object == pb_ok) )
	    {
	        if(SetValues() == true)
            {
    	        Remove();
    	    }    
        }

        if(object == pb_cancel)
        {
	        Remove();
	    }
	}

    /**Mouse-Motion-Listener-Adapter*/
  	class Dlg_MouseMotion extends MouseMotionAdapter 
	{
		public void mouseDragged(MouseEvent e)
		{
		    Dlg_Handle_MouseDragged (e);
		}
	}

    public void Dlg_Handle_MouseDragged (MouseEvent e)
    {
        int xm,ym;

	    xm = (int) getSize().width/2;
	    ym = (int) getSize().height/2;
	    
	    if ( (getLocation().x+e.getX()-xm >= 0) &&
	         (getLocation().y+e.getY()-ym >= 0) )
	    {     
			/**Node-Move relativ zu Dragging verschieben*/
            setLocation(getLocation().x+e.getX()-xm,
                getLocation().y+e.getY()-ym);            			             
        }                
    }
    
    /**SetValues: Werte setzen*/
    private boolean SetValues()
    {
        Double weight;
        
        /**Delay pr�fen u. ggf. setzen  */
        try
        {
            weight = Double.valueOf(tf_weight.getText());
    
            grn.Set_Node_Weight(weight.doubleValue());
        }
        catch(NumberFormatException exc) 
        {
            return(false);
        }
        
		grn.Set_Node_Caption(tf_caption.getText());
                
        return(true);
    }
    
    /**Remove: Panel entfernen*/
	public void Remove ()
	{
	    if(top_panel.isAncestorOf(this))
	    {
    	    top_panel.remove(this);
	    
	        grm.Set_Lock(false);
	    }    
	}

    /**GUI-override*/
    public void update(Graphics g)
    {
        paint(g);
    }
    
    /**GUI-override*/
    public void paint(Graphics g)
    {
        g.setColor(Color.black);
        g.drawRect(0,0,getSize().width-1,getSize().height-1);
    }
}
