package visualgraph.algorithms;
import visualgraph.core.Id_Vector;
import visualgraph.swing.Frm_Documentation;
import visualgraph.swing.Gdt_Element;
import visualgraph.swing.Gdt_Manager;

import java.awt.*;

/**Alg_GdtTest Algorithmus*/

public class Alg_GdtTest extends Base_Algorithm
{
    public void info (Frm_Documentation frm_doc) 
    {
    }
    
    /**Run: Start des Algorithmus*/
    public void run ()
    {   
        int i;
        Gdt_Element gde;
        Id_Vector elem_id;
        Gdt_Manager gdm;
        
        //Kontext-Dokument ermitteln
        gdm = (Gdt_Manager) Get_Context();
       
        //Elementnummern-Menge ermitteln
        elem_id = gdm.ElementNrVector();
        
        //Elementmarkierungen setzen        
        for(i=1;i<=elem_id.Size();i++)
        {            
            gde = (Gdt_Element) gdm.Get(elem_id.Get(i));
            
            gde.SetMarker();
        }
        
        Pause();
        
        //Highlite
        for(i=1;i<=elem_id.Size();i++)
        {
            gde = (Gdt_Element) gdm.Get(elem_id.Get(i));
            
            gde.HighliteElement(Color.red,null);
        }
        Pause();        

        //Element-Nr als Info anh�ngen
        for(i=1;i<=elem_id.Size();i++)
        {
            gdm.SetElementInfo(elem_id.Get(i),""+elem_id.Get(i));
        }
        Pause();        
        
        //Copy        
        Gdt_Manager egm = new Gdt_Manager("Copy",gdm);
        fm.AddResultFrame(egm);
        fm.AddFrameToView(egm);                

        //Elementnummern-Menge ermitteln  
        elem_id = egm.ElementNrVector();
        
        //Element-Nr als Info anh�ngen
        for(i=1;i<=elem_id.Size();i++)
        {
            egm.SetElementInfo(elem_id.Get(i),""+elem_id.Get(i));
        }
        Pause();                

        //Highlite
        for(i=1;i<=elem_id.Size();i++)
        {
            gde = (Gdt_Element) egm.Get(elem_id.Get(i));
            
            gde.HighliteElement(Color.orange,Color.blue);
        }
        Pause();        

        //Marker
        for(i=1;i<=elem_id.Size();i++)
        {
            gde = (Gdt_Element) egm.Get(elem_id.Get(i));
            
            gde.SetMarker();
        }
        Pause();                
        
        //Swapping
        egm.SwapElements(elem_id.Get(1),elem_id.Get(2));

        //Elementnummern-Menge erneuern
        elem_id = egm.ElementNrVector();

        //Element-Nr neu setzen
        for(i=1;i<=elem_id.Size();i++)
        {
            egm.SetElementInfo(elem_id.Get(i),""+elem_id.Get(i));
        }
        Pause();
    }    
}




