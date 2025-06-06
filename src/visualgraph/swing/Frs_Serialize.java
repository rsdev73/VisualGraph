package visualgraph.swing;
import java.io.*;
import java.util.*;

public class Frs_Serialize extends java.lang.Object implements Serializable
{
    /**Data*/
    private String frm_name;
    private String frm_file;
    
    private Vector frm_frames;
    private Vector vis_frames;

    private Vector frm_alg_infos;
    private Vector frm_alg_info_classes;
    
    /**Manager*/
    private transient Frm_Manager frm_mgr;
    
    static final long serialVersionUID = 8600576393999683238L;
    
    /**Konstruktor*/
    public Frs_Serialize(Frm_Manager fmgr)
    {
        if(fmgr == null)
            return;
        
        /**Verweis auf Frame-Manager setzen    */
        frm_mgr = fmgr;
        
        /**Werte setzen*/
        frm_name = fmgr.Get_Name();
        frm_file = fmgr.Get_Frm_File();
        
        frm_frames = fmgr.Get_Frm_Frames();                
        vis_frames = fmgr.Get_Frm_Act_Vis();
        
        frm_alg_infos = fmgr.Get_Frm_Alg_Infos();        
        frm_alg_info_classes = fmgr.Get_Frm_Alg_Info_Classes();        
    }

    /**Get_Name: Mgr Name*/
    public String Get_Name()
    {
        return(frm_name);
    }
    
    /**Get_Frm_File:*/
    public String Get_Frm_File ()
    {
        return(frm_file);
    }
    
    /**Get_Frm_Frames: */
    public Vector Get_Frm_Frames()
    {
        return(frm_frames);
    }

    /**Get_Vis_Frames:*/
    public Vector Get_Vis_Frames()
    {
        return(vis_frames);
    }
    
    /**Get_Frm_Alg_Infos:*/
    public Vector Get_Frm_Alg_Infos()
    {
        return(frm_alg_infos);
    }

    /**Get_Frm_Alg_Info_Classes:*/
    public Vector Get_Frm_Alg_Info_Classes()
    {
        return(frm_alg_info_classes);
    }
    
    private void writeObject(java.io.ObjectOutputStream out) throws IOException
    {
        out.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();        
    }
}
