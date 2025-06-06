package visualgraph.swing;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Dlg_GraphProp extends Panel
{
    /**Verweis auf Top-Panel*/
    private Panel top_panel;
    
    private Frm_Manager frm;
    private Grf_Manager grm;

    private Dlg_ActionAdapter action_adapter;
    
    /**GUI-Elemente*/
	Label label1;
	Label label2;
	Label label3;
	Label label4;
	Label label5;
	Label label6;
	TextField tf_name;
	Choice ch_graph_type;
	Choice ch_node_ident;
	Checkbox cb_show_weights;
	Choice ch_used_algo;
	TextField tf_delay;
	Button pb_ok;
	Button pb_cancel;

    static final long serialVersionUID = 3915135713038024061L;
    
    /**Konstruktor*/
    public Dlg_GraphProp (Frm_Manager frm, Grf_Manager grm)
    {
        super (null);
                
        int i;        
        Vector alg_doks;
        
        top_panel = grm.Get_Container();
        this.grm = grm;
        this.frm = frm;

        /**Lock setzen*/
        grm.Set_Lock(true);
        
		setFont(new Font("SansSerif", Font.PLAIN, 9));       
        setBackground(Color.lightGray);
        
        setSize(237,214);

        /**Location setzen*/
        setLocation(top_panel.getSize().width/2-getSize().width/2,
                    top_panel.getSize().height/2-getSize().height/2);
                    
		label1 = new Label("Document Name");
		label1.setBounds(getInsets().left + 12,getInsets().top + 14,84,18);
		add(label1);
		label2 = new Label("Graph Type");
		label2.setBounds(getInsets().left + 12,getInsets().top + 42,84,18);
		add(label2);
		label3 = new Label("Node identification");
		label3.setBounds(getInsets().left + 12,getInsets().top + 64,84,18);
		add(label3);
		label4 = new Label("Used Algorithm");
		label4.setBounds(getInsets().left + 12,getInsets().top + 110,84,18);
		add(label4);
		label5 = new Label("Delay");
		label5.setBounds(getInsets().left + 12,getInsets().top + 134,84,18);
		add(label5);
		label6 = new Label("ms");
		label6.setBounds(getInsets().left + 156,getInsets().top + 134,28,18);
		add(label6);
		tf_name = new TextField();
		tf_name.setBounds(getInsets().left + 100,getInsets().top + 12,124,20);
		add(tf_name);
		ch_graph_type = new Choice();
		add(ch_graph_type);
		ch_graph_type.setBounds(getInsets().left + 100,getInsets().top + 40,124,20);
		ch_node_ident = new Choice();
		ch_node_ident.addItem("numeric");
		ch_node_ident.addItem("alphabetical");
		try {
			ch_node_ident.select(-1);
		} catch (IllegalArgumentException e) { }
		add(ch_node_ident);
		ch_node_ident.setBounds(getInsets().left + 100,getInsets().top + 62,124,20);
		cb_show_weights = new Checkbox("Show Weights");
		cb_show_weights.setBounds(getInsets().left + 100,getInsets().top + 86,108,15);
		add(cb_show_weights);
		ch_used_algo = new Choice();
		add(ch_used_algo);
		ch_used_algo.setBounds(getInsets().left + 100,getInsets().top + 110,124,20);
		tf_delay = new TextField();
		tf_delay.setBounds(getInsets().left + 100,getInsets().top + 134,48,18);
		add(tf_delay);
		pb_ok = new Button();
		pb_ok.setActionCommand("button");
		pb_ok.setLabel("Ok");
		pb_ok.setBounds(getInsets().left + 156,getInsets().top + 186,68,19);
		add(pb_ok);
		pb_cancel = new Button();
		pb_cancel.setActionCommand("button");
		pb_cancel.setLabel("Cancel");
		pb_cancel.setBounds(getInsets().left + 80,getInsets().top + 186,68,19);
		add(pb_cancel);
		
		/**Event-Listeners registrieren*/
		action_adapter = new Dlg_ActionAdapter();
                		
		pb_ok.addActionListener(action_adapter);
		pb_cancel.addActionListener(action_adapter);
    	tf_name.addActionListener(action_adapter);
    	tf_delay.addActionListener(action_adapter);
    	
		/**addMouseMotionListener(new Dlg_MouseMotion());*/
		
		/**Inhalte f�llen*/
		
		/**Node-Type*/
    	for(i=0;i<Grf_Node.node_types.length;i++)
		    ch_graph_type.addItem(Grf_Node.node_type_names[i]);

		/**Used-Algo*/
		ch_used_algo.addItem("none");
		
		for(i=0;i<frm.GetAlgorithmNames().size();i++)
		{
		    alg_doks = frm.GetAlgorithmDoks();
		    
		    if(((String)alg_doks.elementAt(i)).equals(grm.getClass().getName()))
		    {		    
    		    ch_used_algo.addItem((String)frm.GetAlgorithmNames().elementAt(i));
    		}    
        }
        
        /**Value-Set				*/
		tf_name.setText(grm.Get_Name());
		cb_show_weights.setState(grm.Get_Show_Weights());
		
    	for(i=0;i<Grf_Node.node_types.length;i++)
		{
            if(grm.Get_Node_Typ() == Grf_Node.node_types[i])
            {
                ch_graph_type.select(i);
                break;
            }
        }

        if(grm.Get_Alg_Name().equals(""))
        {
            ch_used_algo.select(0); /**no Alg. selected*/
        }
        else
        {            
    		for(i=1;i<=ch_used_algo.getItemCount();i++)
    		{
                if(grm.Get_Alg_Name().equals(ch_used_algo.getItem(i)))
                {
                    ch_used_algo.select(i);
                    break;
                }
            }
        }
        
        switch(grm.Get_Node_Ident())                        
        {
            case Grf_Node.NODE_IDENT_NUMERIC:
                ch_node_ident.select("numeric");        
                break;

            case Grf_Node.NODE_IDENT_ALPHA:            
                ch_node_ident.select("alphabetical");        
                break;            
        }
        
        tf_delay.setText(""+grm.Get_Delay_Millis());
        
        /**..und einh�ngen*/
        top_panel.add(this,0);                        
        
        tf_name.requestFocus();
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
	    
	    if( (object == tf_name) || (object == tf_delay) || (object == pb_ok) )
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
        Long millis;

        /**Delay pr�fen u. ggf. setzen  */
        try
        {
            millis = Long.valueOf(tf_delay.getText());
    
            if(millis.longValue() < 0)
                return(false);
                
            grm.Set_Delay_Millis(millis.longValue());
        }
        catch(NumberFormatException exc) 
        {
            return(false);
        }
        
		grm.Set_Name(tf_name.getText());
		grm.Set_Show_Weights(cb_show_weights.getState());
		
		grm.ChangeNodeTyp(Grf_Node.node_types[ch_graph_type.getSelectedIndex()]);
		
		if(ch_used_algo.getSelectedIndex() != 0)		
    		grm.Set_Alg_Name(ch_used_algo.getSelectedItem());
	    else
    		grm.Set_Alg_Name("");
    		
		switch(ch_node_ident.getSelectedIndex())
		{
		    case 0:
        	    grm.Set_Node_Ident(Grf_Node.NODE_IDENT_NUMERIC);
	            break;

		    case 1:
        	    grm.Set_Node_Ident(Grf_Node.NODE_IDENT_ALPHA);
	            break;
	    }
                
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
