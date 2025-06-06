package visualgraph.swing;

public class Dsf_Main 
{
    //private static Dsf_DisplayFrame dspf;
    private static Apl_VisualGraph adj;
    
    public static void main(String args[])
    {   
	    /**Kontext dieses Applets setzen*/
//	    Apl_Context.Init(null);
                
        /**Neuen Frame erzeugen    */
//        dspf = new Dsf_DisplayFrame();
        
        /**Neues Adjazenz-Applet erzeugen*/
        adj = new Apl_VisualGraph();

        /**Initialisieren*/
        adj.init();       

        /**Starten*/
        adj.start();

        /**..und anzeigen*/
        adj.setVisible(true);

//        /**In Frame einh�ngen*/
//        dspf.add(adj);
//        
//        /**..und anzeigen*/
//        dspf.setVisible(true);                
    }
}
