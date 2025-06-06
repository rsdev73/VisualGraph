package visualgraph.swing;
import java.awt.Insets;

import javax.swing.JFrame;

public class Apl_Context extends java.lang.Object
{
    /**Applet-Base Peer*/
    //private static JFrame app_loader_base = null;
    
    /**Applet-Base Peer*/
    private static JFrame app_base = null;

    /**Applet-Frame insets*/
    private static Insets app_insets = null;
    
    /**Applet-Pfade*/
    private static String app_path = "";
    private static String img_path = ""; 
    private static String prj_path = ""; 
        
    private static boolean context_set = false;
    private static boolean net_context;

    /**Verweis auf FrameSet-Manager*/
    private static Frs_Manager frs;
    
    /**Init: Context initialisieren    */
    public static void Init(JFrame apl)
    {        
	    /**Applet-Peer setzen*/
	    app_base = apl;	   
	    
        if(!context_set)
        {
            try
            {
                /**local-Disk-Pfade                    */
                app_path = System.getProperty("user.dir")+System.getProperty("file.separator"); 

                img_path = app_path+"Images"+System.getProperty("file.separator");                        

                prj_path = System.getProperty("user.dir")+System.getProperty("file.separator")+
                            "Projects"+System.getProperty("file.separator");                    
                
                net_context = false;
            }

            catch(SecurityException se) {};
            
//            app_loader_base = apl;    
//
//            /**Pfade setzen*/
//            try
//            {
//                /**wwww-Pfade*/
//                app_path =  System.getProperty("user.dir");
//                //"./"; //app_loader_base.getCodeBase().toString();
//                
//                img_path = app_path+"Images/";                        
//                prj_path = app_path+"Projects/";                
//                
//                net_context = true;                
//            }
//            catch(Exception e) 
//            {
//
//            };
//

            context_set = true;            
        }    
    }

    /**Set_Insets*/
    public static void Set_Insets(int top, int bottom)
    {        
        if(app_insets == null)
           app_insets = new Insets(top,0,bottom,0);
        else
        {
            app_insets.top = top;
            app_insets.bottom = bottom;        
        }    
    }

    /**Get_Insets*/
    public static Insets Get_Insets()
    {        
        if(app_insets == null)
        {
           app_insets = new Insets(0,0,0,0);
        }
        
        return(new Insets(app_insets.top,0,app_insets.bottom,0));
    }
        
    public static String Get_Img_Path()
    {
        return(img_path);
    }
    
    public static String Get_App_Path()
    {
        return(app_path);
    }

    public static String Get_Prj_Path()
    {
        return(prj_path);
    }
    
    public static JFrame Get_Applet()
    {
        return(app_base);
    }    

//    public static JFrame Get_Loader_Applet()
//    {
//        return(app_loader_base);
//    }    

    public static boolean Is_Net_Context()
    {
        return(net_context);
    }    

    /**Set_FrsManager: Verweis auf Frame-Set-Manager setzen*/
    public static void Set_FrsManager(Frs_Manager frs_mgr)    
    {
        frs = frs_mgr;        
    }   

    /**Get_FrsManager: Project-Manager bereitstellen*/
    public static Frs_Manager Get_FrsManager()
    {
        return(frs);
    }
}
