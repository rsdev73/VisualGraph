package visualgraph.algorithms;
import visualgraph.core.Id_Vector;
import visualgraph.swing.Frm_Documentation;
import visualgraph.swing.Gse_Manager;
import visualgraph.util.Uti_SortAdapter;

import java.awt.*;

/**Bubble-Sort Algorithmus*/
public class Alg_BubbleSort extends Base_Algorithm
{
    /**Info: Ausgabe v. Informationen �ber diesen Alg.*/    
    public void info (Frm_Documentation frm_doc) 
    {
        frm_doc.AppendLine("how this algorithm works (pseudo-code):\n");    
        frm_doc.AppendLine("void run()");
        frm_doc.AppendLine("{");        
        frm_doc.AppendLine("    set A = current sequence of elements");        
        frm_doc.AppendLine("    set N = |A|\n");                
        frm_doc.AppendLine("    do");        
        frm_doc.AppendLine("    {");        
        frm_doc.AppendLine("        for (i=1; i<=(N-1); i++)");
        frm_doc.AppendLine("        {");                
        frm_doc.AppendLine("            if (A[i].key > A[i+1].key)");                        
        frm_doc.AppendLine("            {"); 
        frm_doc.AppendLine("                exchange A[i] and A[i+1]");                                
        frm_doc.AppendLine("            }"); 
        frm_doc.AppendLine("        }"); 
        frm_doc.AppendLine("    } while no further exchange possible");
        frm_doc.AppendLine("}\n");                
    }
    
    /**Run: Start des Algorithmus*/
    public void run ()
    {   
        Gse_Manager gsm;        
        Id_Vector elem_id;        
        int i;
        boolean exchanged;
        
        //Kontext-Dokument ermitteln
        gsm = (Gse_Manager) Get_Context();
        
        //Ergebnis-Sequence darstellen
        Gse_Manager egm = new Gse_Manager("Sorted Sequence",gsm);
        fm.AddResultFrame(egm);
        fm.SwitchToFrame(egm);                        
        
        if(Get_Monitor_Vars())
            fm.AddFrameToView(fv);                                    
    
        egm.SetStdInfo("initialize...");        
        
        //Elementnummern-Menge ermitteln
        elem_id = egm.ElementNrVector();
        
        fv.Set_Int_Variable (1,"N (number of items)",elem_id.Size());                
        fv.Set_PdtVector_Variable(2,"A (Vector of items)",egm.Get_Pdt_Manager());
        
        egm.SetStdInfo("ready");       
        
        //Haltepunkt       
        Pause();
        
        do
        {
            exchanged = false;            
            
            for(i=1;i<=(elem_id.Size()-1);i++)
            {
                //Highlite
                egm.HighliteElement(elem_id.Get(i),Color.orange,null);
                egm.HighliteElement(elem_id.Get(i+1),Color.orange,null);                                             
                
                egm.SetStdInfo("Comparing value\n"+egm.Get(elem_id.Get(i))+" with "+egm.Get(elem_id.Get(i+1)));

                fv.Set_Int_Variable(3,"Current loop index i",i);
                fv.Set_PdtElement_Variable(4,"Current A[i]",egm.Get_Element(elem_id.Get(i)));
                fv.Set_PdtElement_Variable(5,"Current A[i+1]",egm.Get_Element(elem_id.Get(i+1)));
                
                Pause();
    
                //Schl�sselvergleich A[i] und A[i+1] durchf�hren
                if(egm.CompareElements(elem_id.Get(i),elem_id.Get(i+1)) == Uti_SortAdapter.COMP_GRTR)
                {                    
                    egm.HighliteElement(elem_id.Get(i),Color.red,Color.white);
                    egm.HighliteElement(elem_id.Get(i+1),Color.red,Color.white);                

                    egm.SetStdInfo("Value "+egm.Get(elem_id.Get(i+1))+" greater then "+egm.Get(elem_id.Get(i))+"\n exchanging values");
                    
                    Pause();
                    
                    //Elemente vertauschen
                    egm.SwapElements(elem_id.Get(i),elem_id.Get(i+1));
                    
                    fv.Set_PdtVector_Variable(2,"A (Vector of items)",egm.Get_Pdt_Manager());
        
                    exchanged = true;

                    //Elementnummern-Menge neu ermitteln
                    elem_id = egm.ElementNrVector();

                    fv.Set_PdtElement_Variable(4,"Current A[i]",egm.Get_Element(elem_id.Get(i)));
                    fv.Set_PdtElement_Variable(5,"Current A[i+1]",egm.Get_Element(elem_id.Get(i+1)));

                    Pause();
                }
                
                egm.UnHighliteAllElements();
            }
            
        } while(exchanged);
        
        egm.SetStdInfo("No further exchange possible");
    }    
}




