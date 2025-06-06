package visualgraph.swing;
import java.awt.*;
import java.awt.event.*;

import javax.swing.JFrame;

public class Apl_VisualGraph extends JFrame
{   
    /**Frame-Set Manager*/
    private Frs_Manager frs;
    
    /**Basis-Panels*/
	private Panel pa_top;
	private Panel pa_frame_set;
	private Panel pa_left;
	private Panel pa_right;
	private Panel pa_bottom;
	private Panel pa_frames;

    static final long serialVersionUID = 904108304153563562L;
    
    /**init: Applet-Initialisierung*/
	public void init()
	{
		setLayout(new BorderLayout(0,0));
		//setSize(700,500);
		setFont(new Font("Helvetica", Font.PLAIN, 12));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setSize(getInsets().left + getInsets().right + 700,getInsets().top + getInsets().bottom + 500);
		setTitle("Visual Graph - Framework for Visualization of Program-Structures");
		
		pa_top = new java.awt.Panel(null);
		pa_top.setBounds(0,0,620,32);
		pa_top.setBackground(new Color(12632256));
		add("North", pa_top);
		pa_frame_set = new Panel(null);
		pa_frame_set.setBounds(10,10,760,22);
		pa_frame_set.setBackground(new Color(12632256));
		pa_top.add(pa_frame_set);
		pa_left = new Panel(null);
		pa_left.setBounds(0,32,10,278);
		pa_left.setBackground(new Color(12632256));
		add("West", pa_left);
		pa_right = new Panel(null);
		pa_right.setBounds(610,32,10,278);
		pa_right.setBackground(new Color(12632256));
		add("East", pa_right);
		pa_bottom = new Panel(null);
        pa_bottom.setBounds(0,310,620,10);
		pa_bottom.setBackground(new Color(12632256));
		add("South", pa_bottom);
		pa_frames = new Panel(null);
		pa_frames.setBounds(10,32,600,278);
		pa_frames.setBackground(new Color(16777215));
		add("Center", pa_frames);
	
	    /**Component-Listener registrieren*/
    	addComponentListener(new Apl_VisGr_ComponentAdapter());
        
        /**Panels-Resizen*/
   	    pa_frame_set.setBounds(10,10,getSize().width-20,22);  
    }
		
	/**start: Startet das Applet*/
	public void start ()
	{	
	    /**Applet-Context initialisieren*/
        Apl_Context.Init(this);
        
        /**neuen FrameSet Manager anlegen*/
	    frs = new Frs_Manager(pa_frame_set, pa_frames);        	        

        /**Frame-Set Manager registrieren*/
	    Apl_Context.Set_FrsManager(frs);
	    
	    /**Algorithmen registrieren*/
	    /**Graph-Algorithmen:*/
	    frs.RegisterAlgorithm("visualgraph.algorithms.Alg_TopSort","Topsort","visualgraph.swing.Grf_Manager");
	    frs.RegisterAlgorithm("visualgraph.algorithms.Alg_Dijkstra","Dijkstra","visualgraph.swing.Grf_Manager");	    
        frs.RegisterAlgorithm("visualgraph.algorithms.Alg_Warshall","Warshall","visualgraph.swing.Grf_Manager");	    
        frs.RegisterAlgorithm("visualgraph.algorithms.Alg_Kruskal","Kruskal","visualgraph.swing.Grf_Manager");	                            
        frs.RegisterAlgorithm("visualgraph.algorithms.Alg_Ford_Fulkerson","Ford-Fulkerson","visualgraph.swing.Grf_Manager");	        

	    /**Sequence-Algorithmen:*/
        frs.RegisterAlgorithm("visualgraph.algorithms.Alg_BubbleSort","Bubble-Sort","visualgraph.swing.Gse_Manager");	        	            
        frs.RegisterAlgorithm("visualgraph.algorithms.Alg_MergeSort","Merge-Sort","visualgraph.swing.Gse_Manager");	        	                    
        frs.RegisterAlgorithm("visualgraph.algorithms.Alg_QuickSort","Quick-Sort","visualgraph.swing.Gse_Manager");	        	                    
	}

    /**Component-Resize Adapter*/
	class Apl_VisGr_ComponentAdapter extends java.awt.event.ComponentAdapter
	{
		public void componentResized(java.awt.event.ComponentEvent event)
		{		
		    Apl_Handle_ComponentResized(event);
		}
	}
    
    /**Apl_Handle_ComponentResized: Component-Resize verarbeiten*/
    public synchronized void Apl_Handle_ComponentResized (ComponentEvent event)
    {
        if(frs != null)
            frs.dispatchEvent(new ComponentEvent(pa_frames,ComponentEvent.COMPONENT_RESIZED));                            
    }
	//{{DECLARE_CONTROLS
	//}}
}
