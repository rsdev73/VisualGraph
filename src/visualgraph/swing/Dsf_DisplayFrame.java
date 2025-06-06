/*
	A basic extension of the java.awt.Frame class
 */
package visualgraph.swing;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JFrame;

public class Dsf_DisplayFrame extends JFrame
{
	boolean fComponentsAdjusted = false;
    
    static final long serialVersionUID = -764879840995338865L;
    
    /**Konstruktoren*/
	public Dsf_DisplayFrame()
	{
		/**{{INIT_CONTROLS*/
		setLayout(null);
		setVisible(false);
		setSize(getInsets().left + getInsets().right + 700,getInsets().top + getInsets().bottom + 500);
		setBackground(new Color(12632256));
		setTitle("Visual Graph - Framework for Visualization of Program-Structures");
		/**}}*/

		/**{{INIT_MENUS*/
		/**}}*/

		addWindowListener(new DispFrame_WindowAdapter());
		addComponentListener(new DispFrame_ComponentAdapter());
    }
    
	public void addNotify()
	{
	    Dimension d = getSize();

		super.addNotify();

		if (fComponentsAdjusted)
			return;

		setSize(getInsets().left + getInsets().right + d.width, getInsets().top + getInsets().bottom + d.height);
		Component components[] = getComponents();
		for (int i = 0; i < components.length; i++)
		{
			Point p = components[i].getLocation();
			p.translate(getInsets().left, getInsets().top);
			components[i].setLocation(p);
		}
		fComponentsAdjusted = true;
	}

	class DispFrame_WindowAdapter extends java.awt.event.WindowAdapter
	{
		public void windowClosing(java.awt.event.WindowEvent event)
		{
//			Object object = event.getSource();
//
//			if (object == Dsf_DisplayFrame.this)
//       		{
//       		    if(Apl_Context.Get_FrsManager().CheckOnClose() == true)
//       		    {       		    
//               		Dsf_DisplayFrame.this.dispose(); /**Ende*/
//               		
//                    if(Apl_Context.Get_Loader_Applet() == null)
//                    {
//                        System.exit(0);
//                    }
//                }    
//           	}
		}
	}

    /**Component-Resize Adapter*/
	class DispFrame_ComponentAdapter extends java.awt.event.ComponentAdapter
	{
	    public void componentResized(ComponentEvent e)
	    {
            DispFrame_Handle_ComponentResized(e);
        }
	}

    public synchronized void DispFrame_Handle_ComponentResized (ComponentEvent e)
    {
    	if(Apl_Context.Get_Applet() != null)
    	{
    		/**Insets setzen*/
    		Apl_Context.Set_Insets(getInsets().top,getInsets().bottom);		

    	    Apl_Context.Get_Applet().setSize(getSize().width-getInsets().right-getInsets().left,
    	                                        getSize().height-getInsets().top-getInsets().bottom);

            Apl_Context.Get_Applet().doLayout();

            Apl_Context.Get_Applet().dispatchEvent(new ComponentEvent(Dsf_DisplayFrame.this,
                                ComponentEvent.COMPONENT_RESIZED));
        }
    }
	/**{{DECLARE_CONTROLS*/
	/**}}*/
	/**{{DECLARE_MENUS*/
	/**}}*/
}
