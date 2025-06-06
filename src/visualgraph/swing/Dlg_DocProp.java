package visualgraph.swing;
import java.awt.*;
import java.awt.event.*;

public class Dlg_DocProp extends Panel
{
    /**Verweis auf Top-Panel*/
    private Panel top_panel;

    private Frm_Manager frm;
    private Frm_Documentation fdoc;

    private Dlg_ActionAdapter action_adapter;
    
    /**GUI-Elemente*/
	Label label1;
	TextField tf_name;
	Button pb_ok;
	Button pb_cancel;
    
    static final long serialVersionUID = 7500799993193309864L;
    
    /**Konstruktor*/
    public Dlg_DocProp (Frm_Manager frm, Frm_Documentation fdoc)
    {
        super (null);

        top_panel = fdoc.Get_Container();

        this.frm = frm;
        this.fdoc = fdoc;

        /**Lock setzen*/
        fdoc.Set_Lock(true);

		setFont(new Font("SansSerif", Font.PLAIN, 9));
        setBackground(Color.lightGray);

        /**Gr��e*/
        setSize(196,95);

        /**Location setzen*/
        setLocation(top_panel.getSize().width/2-getSize().width/2,
                    top_panel.getSize().height/2-getSize().height/2);

		label1 = new Label("Document Name");
		label1.setBounds(getInsets().left + 12,getInsets().top + 14,84,18);
		add(label1);

		tf_name = new TextField();
		tf_name.setBounds(getInsets().left + 100,getInsets().top + 12,84,20);
		add(tf_name);

		pb_ok = new Button();
		pb_ok.setLabel("Ok");
		pb_ok.setBounds(getInsets().left + 115,getInsets().top + 62,68,19);
		add(pb_ok);
		pb_cancel = new Button();
		pb_cancel.setLabel("Cancel");
		pb_cancel.setBounds(getInsets().left + 39,getInsets().top + 62,68,19);
		add(pb_cancel);

		/**Event-Listeners registrieren*/
		action_adapter = new Dlg_ActionAdapter();

		pb_ok.addActionListener(action_adapter);
		pb_cancel.addActionListener(action_adapter);
    	tf_name.addActionListener(action_adapter);

		/**addMouseMotionListener(new Dlg_MouseMotion());*/

        /**Value-Set*/
        tf_name.setText(fdoc.Get_Name());

        /**..und einh�ngen*/
        top_panel.add(this,0);

        tf_name.requestFocus();
	}

	/**Action-Event Handling*/
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

	    if( (object == tf_name) || (object == pb_ok) )
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
        fdoc.Set_Name(tf_name.getText());

        return(true);
    }

    /**Remove: Panel entfernen*/
	public void Remove ()
	{
	    if(top_panel.isAncestorOf(this))
	    {	    
    	    top_panel.remove(this);

    	    fdoc.Set_Lock(false);
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
