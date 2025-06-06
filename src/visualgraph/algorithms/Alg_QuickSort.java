package visualgraph.algorithms;
import visualgraph.core.Id_Vector;
import visualgraph.swing.Frm_Documentation;
import visualgraph.swing.Gse_Manager;

import java.awt.*;

/**Quick-Sort Algorithmus (rekursiv-Implementierung)*/

public class Alg_QuickSort extends Base_Algorithm
{
    /**Info: Ausgabe v. Informationen �ber diesen Alg.*/
    public void info (Frm_Documentation frm_doc) 
    {
        frm_doc.AppendLine("how this algorithm works (pseudo-code):\n");    
        frm_doc.AppendLine("void run()");
        frm_doc.AppendLine("{");        
        frm_doc.AppendLine("    set A = current sequence of elements");        
        frm_doc.AppendLine("    set N = |A|\n");                
        frm_doc.AppendLine("    quicksort (1,N);");        
        frm_doc.AppendLine("}\n");                
        frm_doc.AppendLine("void quicksort (l,r)  {item range left and right}");
        frm_doc.AppendLine("{");                
        frm_doc.AppendLine("    if (r > l)");
        frm_doc.AppendLine("    {");        
        frm_doc.AppendLine("        i = l-1;");
        frm_doc.AppendLine("        j = r;");        
        frm_doc.AppendLine("        v = r; (setting pivot item v)\n");        
        frm_doc.AppendLine("        for(;;)");
        frm_doc.AppendLine("        {");        
        frm_doc.AppendLine("            do { i++; } while ( A[i] <= A[v] );");
        frm_doc.AppendLine("            do { j--; } while ( A[j] >= A[v] );\n");
        frm_doc.AppendLine("            if (i >= j)");                                
        frm_doc.AppendLine("                break; {exit the loop}\n");                                        
        frm_doc.AppendLine("            exchange item A[i] with A[j]");
        frm_doc.AppendLine("        }\n");
        frm_doc.AppendLine("        exchange item A[i] with A[r]\n");
        frm_doc.AppendLine("        quicksort (l,i-1); {further division of current left range}");
        frm_doc.AppendLine("        quicksort (i+1,r); {further division of current right range}");        
        frm_doc.AppendLine("    }");                                
        frm_doc.AppendLine("}");                                                   
    }

    Gse_Manager gsm,egm;            
    Id_Vector elem_id;            
    
    /**Run: Start des Algorithmus*/
    public void run ()
    {   
        //Kontext-Dokument ermitteln
        gsm = (Gse_Manager) Get_Context();
        
        //Ergebnis-Sequence darstellen
        egm = new Gse_Manager("Sorted Sequence",gsm);
        fm.AddResultFrame(egm);
        fm.SwitchToFrame(egm);                        
        
        //Variable-Monitoring ?
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
  
        //Alg. starten        
        quicksort(1,elem_id.Size());        
        
        egm.SetStdInfo("No further range division possible");       
        
        egm.UnHighliteAllElements();                                                    
    }    
    
    /**quicksort: QuickSort-Alg. Methode rekursiv*/
    private void quicksort (int l, int r)
    {
        int i,j,v;

        fv.Set_Int_Variable(6,"Current left range index l",l);            
        fv.Set_Int_Variable(7,"Current right range index r",r);            
        
        if(r > l)
        {
            i = l-1;
            j = r;
            v = r; //Pivotelement
                        
            egm.UnHighliteAllElements();                                            
            egm.HighliteElement(elem_id.Get(v),Color.green,null);            

            fv.Set_Int_Variable(3,"Left item index i",i+1);            
            fv.Set_Int_Variable(4,"Right item index j",j-1);            
            fv.Set_Int_Variable(5,"Current pivot item index v",v);            
            
            egm.SetStdInfo("Setting comparison range: item index "+(i+1)+".."+(j-1)+"\nPivot item index (green) "+v);
            
            Pause();
            
            for(;;)
            {
                for(;;)
                {
                    i ++;
                                        
                    if(i > r)
                        break;

                    fv.Set_Int_Variable(3,"",i);            

                    egm.HighliteElement(elem_id.Get(i),Color.yellow,null);            

                    egm.SetStdInfo("Comparing left item\nindexed by "+i+" with pivot");
            
                    Pause();
                    
                    if(egm.CompareElements(elem_id.Get(i),elem_id.Get(v)) >= 0)
                    {
                        egm.SetStdInfo("Left item is greater than pivot\nChecking no further left items");                        

                        Pause();
                        
                        break; 
                    }
                    
                    egm.SetStdInfo("Left item indexed by "+i+"\nis less than pivot\nChecking next item");
                    
                    Pause();
                    
                    //weitermachen solange A[i]>=A[v]                                 
                    egm.UnHighliteElement(elem_id.Get(i));
                    egm.HighliteElement(elem_id.Get(v),Color.green,null);                                
                }
                
                for(;;)
                {
                    j --;

                    if(j < 1)
                        break;
                    
                    fv.Set_Int_Variable(4,"",j);            
                    
                    egm.HighliteElement(elem_id.Get(j),Color.orange,null);            

                    egm.SetStdInfo("Comparing right item\nindexed by "+j+" with pivot");                    
                    
                    Pause();
                    
                    if(egm.CompareElements(elem_id.Get(j),elem_id.Get(v)) <= 0)
                    {
                        egm.SetStdInfo("Right item is less than pivot\nChecking no further right items");                        
                    
                        Pause();
                        
                        break;
                    }
                    
                    egm.SetStdInfo("Right item indexed by "+j+"\nis greater than pivot\nChecking next item");
                    
                    Pause();                    
                    
                    //weitermachen solange A[j]<=A[v]     
                    egm.UnHighliteElement(elem_id.Get(j));                    
                    egm.HighliteElement(elem_id.Get(v),Color.green,null);                                                    
                }    
                
                if( i >=j ) // ist i Pivotposition
                {
                    egm.SetStdInfo("Left index i ("+i+") is greater\nor equal right index j ("+j+")");
                    
                    Pause();                    
                    
                    break;                    
                }
                
                egm.HighliteElement(elem_id.Get(i),Color.red,Color.white);
                egm.HighliteElement(elem_id.Get(j),Color.red,Color.white);                

                egm.SetStdInfo("Exchanging current left and right item");                
                
                Pause();
                
                //Swap a[i] und a[j]                               
                egm.SwapElements(elem_id.Get(i),elem_id.Get(j));                
                
                //Elementnummern-Menge neu ermitteln
                elem_id = egm.ElementNrVector();  
                                
                fv.Set_PdtVector_Variable(2,"",egm.Get_Pdt_Manager());                
                
                egm.SetStdInfo("Current left and right item exchanged\nRepeat with next items");                
                
                Pause();

                egm.UnHighliteAllElements();                                
                egm.HighliteElement(elem_id.Get(v),Color.green,null);                            
            }

            egm.HighliteElement(elem_id.Get(i),Color.red,Color.white);
            egm.HighliteElement(elem_id.Get(r),Color.red,Color.white);                

            egm.SetStdInfo("Exchanging current left and pivot item");                
            
            Pause();
            
            //Swap a[i] und a[r]
            egm.SwapElements(elem_id.Get(i),elem_id.Get(r));                            

            //Elementnummern-Menge neu ermitteln
            elem_id = egm.ElementNrVector();

            egm.SetStdInfo("Current left and pivot item exchanged");
            
            fv.Set_PdtVector_Variable(2,"",egm.Get_Pdt_Manager());                            
            
            Pause();
            
            //und weitersortieren...
            egm.UnHighliteAllElements();                                            
            egm.SetStdInfo("Repeat recursive with left range l ("+l+")\nand right range i-1 ("+(i-1)+")");
            Pause();
            
            quicksort(l,i-1);

            egm.UnHighliteAllElements();                                
            egm.SetStdInfo("Repeat recursive with left range i+1 ("+(i+1)+")\nand right range r ("+r+")");
            Pause();
            
            quicksort(i+1,r);            
        }
        else
        {
            egm.UnHighliteAllElements();                                            
            egm.SetStdInfo("Right range r ("+r+") is greater than left range l ("+l+")\nComparing no items");
            Pause();            
        }
    }
}




