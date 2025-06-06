package visualgraph.algorithms;
import visualgraph.core.Id_Vector;
import visualgraph.swing.Frm_Documentation;
import visualgraph.swing.Gdt_Element;
import visualgraph.swing.Gdt_Manager;
import visualgraph.swing.Gse_Manager;

import java.awt.*;

/**2-Wege Merge-Sort Algorithmus (rekursiv)*/
public class Alg_MergeSort extends Base_Algorithm
{
    /**Info: Ausgabe v. Informationen �ber diesen Alg.*/    
    public void info (Frm_Documentation frm_doc) 
    {
        frm_doc.AppendLine("how this algorithm works (pseudo-code):\n");    
        frm_doc.AppendLine("void run()");
        frm_doc.AppendLine("{");        
        frm_doc.AppendLine("    set A = current sequence of elements");        
        frm_doc.AppendLine("    set N = |A|\n");                
        frm_doc.AppendLine("    mergesort (1,N); {starting two-way mergesort}");                        
        frm_doc.AppendLine("}\n");                                
        frm_doc.AppendLine("void mergesort (l,r)  {item range left and right}");
        frm_doc.AppendLine("{");        
        frm_doc.AppendLine("    m = (l+r) / 2; {Divide item set}\n");
        frm_doc.AppendLine("    mergesort(l,m); {recursive call, left item set}");                
        frm_doc.AppendLine("    mergesort(m+1,r); {recursive call, right item set}\n");                        
        frm_doc.AppendLine("    set i = l;");
        frm_doc.AppendLine("    set j = m+1;");        
        frm_doc.AppendLine("    set k = l;\n");                
        frm_doc.AppendLine("    set tmp = empty sequence;\n");                        
        frm_doc.AppendLine("    while( (i <= m) && (j <= r) )");        
        frm_doc.AppendLine("    {"); 
        frm_doc.AppendLine("        if (A[i] <= A[j])");                                
        frm_doc.AppendLine("        {"); 
        frm_doc.AppendLine("            tmp[k] = A[i];");         
        frm_doc.AppendLine("            i++;");                 
        frm_doc.AppendLine("        }");         
        frm_doc.AppendLine("        else");         
        frm_doc.AppendLine("        {"); 
        frm_doc.AppendLine("            tmp[k] = A[j];");         
        frm_doc.AppendLine("            j++;");                 
        frm_doc.AppendLine("        }");         
        frm_doc.AppendLine("        k++;");                 
        frm_doc.AppendLine("    }\n"); 
        frm_doc.AppendLine("    if (i > m)");         
        frm_doc.AppendLine("    {");                 
        frm_doc.AppendLine("        for(h = j; h < r; h++)");                         
        frm_doc.AppendLine("            tmp[k+h-j] = A[h];");
        frm_doc.AppendLine("    }");                 
        frm_doc.AppendLine("    else");              
        frm_doc.AppendLine("    {");                         
        frm_doc.AppendLine("        for(h = i; h < m; h++)");                         
        frm_doc.AppendLine("            tmp[k+h-i] = A[h];");
        frm_doc.AppendLine("    }\n");                 
        frm_doc.AppendLine("    {writing back values}");                         
        frm_doc.AppendLine("    for(h = l; h < r; h++)");                         
        frm_doc.AppendLine("        A[h] = tmp[h];");        
        frm_doc.AppendLine("}\n");                        
    }
    
    Gse_Manager gsm,egm,tmp_sm;
    Gdt_Manager tgm;
    Id_Vector elem_id;    
    int N;
    
    /**Run: Start des Algorithmus*/
    public void run ()
    {   
        int i;
            
        //Kontext-Dokument ermitteln
        gsm = (Gse_Manager) Get_Context();
        
        //Ergebnis-Sequence darstellen
        egm = new Gse_Manager("Sorted Sequence",gsm);
        fm.AddResultFrame(egm);
        fm.SwitchToFrame(egm);                        

        //Teilsequencen darstellen
        tgm = new Gdt_Manager("Partial Sequences",gsm);
        fm.AddResultFrame(tgm);
        fm.AddFrameToView(tgm);                                            

        //Tempor�r-Sequence bereitstellen
        tmp_sm = new Gse_Manager("Temporary Sequence",gsm);
        fm.AddResultFrame(tmp_sm);
        
        //Variable-Monitoring ?
        if(Get_Monitor_Vars())
            fm.AddFrameToView(fv);                                        
            
        egm.SetStdInfo("initialize...");        
        
        //Elementnummern-Menge ermitteln
        elem_id = egm.ElementNrVector();
        N = elem_id.Size();
        
        //Temp-Element nullstellen
        for(i=1;i<=elem_id.Size();i++)
            tmp_sm.Set(elem_id.Get(i),(Object)null);
        
        fv.Set_Int_Variable (1,"N (number of items)",elem_id.Size());                
        fv.Set_PdtVector_Variable(2,"A (Vector of items)",egm.Get_Pdt_Manager());
        
        egm.SetStdInfo("ready");       
        
        //Haltepunkt       
        Pause();
  
        //Alg. starten
        mergesort(1,N);        

        egm.SetStdInfo("No further range division possible");       
        
        egm.UnHighliteAllElements();                                                            
    }    
    
    public void mergesort (int l, int r)
    {
        int i,m,h,j,k,ii;
        Gdt_Element gdt,egdt;
        Point grid,loc;
        
        if(r > l)
        {
            //Mittelpunkt-Index der Sequenz ermitteln
            m = (int) ( (l+r) / 2);

            fv.Set_Int_Variable(6,"Current division element m",m);        
        
            egm.SetStdInfo("Waiting for devision");
            
            //Elemente der Bereich anzeigen                        
            tgm.UnHighliteAllElements();            
                        
            for(i=l;i<=m;i++)
                tgm.HighliteElement(elem_id.Get(i),Color.yellow,null);

            for(i=m+1;i<=r;i++)
                tgm.HighliteElement(elem_id.Get(i),Color.orange,null);

            //Elemente verschieben bei einzeiliger Ansicht
            if(gsm.Get_Sequence_View() == Gse_Manager.SEQ_VIEW_ONE_ROW)
            {
                for(i=1;i<=N;i++)
                {
                    egdt = egm.LookupIdentElement(elem_id.Get(i));                
                    
                    gdt = tgm.LookupIdentElement(elem_id.Get(i));
                    loc = egdt.getLocation();
                    
                    if( (i>=(m+1)) && (i<=N))
                    {
                        grid = tgm.KoordToGrid(loc.x,loc.y,0,0);
                        grid.x ++;
                        loc = tgm.GridToKoord(grid.x,grid.y,0,0);                
                    } 
                                    
                    gdt.setLocation(loc);                
                    tgm.HandleElementMotion(gdt);                
                    tgm.Repaint();
                }
            }
            
            tgm.SetStdInfo("Current division of item set");
            
            Pause();

            tgm.SetStdInfo("Recall division with\nitem range "+l+" to "+m+" (yellow)");

            Pause();

            //Links teilen            
            mergesort(l,m);

            egm.SetStdInfo("Waiting for devision");
            
            //Elemente der Bereich anzeigen              
            tgm.UnHighliteAllElements();            
                        
            for(i=l;i<=m;i++)
                tgm.HighliteElement(elem_id.Get(i),Color.yellow,null);
                
            for(i=m+1;i<=r;i++)
                tgm.HighliteElement(elem_id.Get(i),Color.orange,null);

            //Elemente verschieben, bei einzeiliger Ansicht
            if(gsm.Get_Sequence_View() == Gse_Manager.SEQ_VIEW_ONE_ROW)
            {            
                for(i=1;i<=N;i++)
                {
                    egdt = egm.LookupIdentElement(elem_id.Get(i));                
                    
                    gdt = tgm.LookupIdentElement(elem_id.Get(i));
                    loc = egdt.getLocation();
                    
                    if( (i>=(m+1)) && (i<=N))
                    {
                        grid = tgm.KoordToGrid(loc.x,loc.y,0,0);
                        grid.x ++;
                        loc = tgm.GridToKoord(grid.x,grid.y,0,0);                
                    } 
                                    
                    gdt.setLocation(loc);                
                    tgm.HandleElementMotion(gdt);                
                    tgm.Repaint();                
                }
            }
            
            tgm.SetStdInfo("Current division of item set");
            
            Pause();

            tgm.SetStdInfo("Recall division with\nitem range "+(m+1)+" to "+r+" (orange)");
            
            Pause();
            
            //rechts teilen
            mergesort(m+1,r);            
            
            //Elemente der Bereich anzeigen              
            tgm.UnHighliteAllElements();            
                        
            for(i=l;i<=m;i++)
                tgm.HighliteElement(elem_id.Get(i),Color.yellow,null);
                
            for(i=m+1;i<=r;i++)
                tgm.HighliteElement(elem_id.Get(i),Color.orange,null);

            //Elemente verschieben, bei einzeiliger Ansicht
            if(gsm.Get_Sequence_View() == Gse_Manager.SEQ_VIEW_ONE_ROW)
            {
                for(i=1;i<=N;i++)
                {
                    egdt = egm.LookupIdentElement(elem_id.Get(i));                
                    
                    gdt = tgm.LookupIdentElement(elem_id.Get(i));
                    loc = egdt.getLocation();
                    
                    if( (i>=(m+1)) && (i<=N))
                    {
                        grid = tgm.KoordToGrid(loc.x,loc.y,0,0);
                        grid.x ++;
                        loc = tgm.GridToKoord(grid.x,grid.y,0,0);                
                    } 
                                    
                    gdt.setLocation(loc);                
                    tgm.HandleElementMotion(gdt);                
                    tgm.Repaint();                
                }
            }
            
            tgm.SetStdInfo("Current division of item set");
            
            Pause();

            //und Teilfolgen verschmelzen (merge)                        

            fv.Set_Int_Variable(4,"Current range left index l",l);
            fv.Set_Int_Variable(5,"Current range right index r",r);
            fv.Set_Int_Variable(6,"Current division element m",m);        

            egm.SetStdInfo("Merging elements "+l+" to "+r);
            egm.UnHighliteAllElements();

            //Elemente der Bereich anzeigen
            for(i=l;i<=m;i++)
                egm.HighliteElement(elem_id.Get(i),Color.yellow,null);

            for(i=m+1;i<=r;i++)
                egm.HighliteElement(elem_id.Get(i),Color.orange,null);
            
            Pause();
            
            i = l;
            j = m+1;
            k = l;

            //Elemente aus Tempor�r Sequence nullstellen                
            for(ii=1;ii<=tmp_sm.Size();ii++)
                tmp_sm.Set(elem_id.Get(ii),(Object)null);
            
            fv.Set_PdtVector_Variable(3,"Temporary Sequence",tmp_sm.Get_Pdt_Manager());
            Pause();
            
            fv.Set_Int_Variable(7,"Current left range index i=(l..m)",i);
            fv.Set_Int_Variable(8,"Current right range index j=(m+1..r)",j);
            fv.Set_Int_Variable(9,"Current temporary index k",k);
            
            while( (i<=m) && (j<=r) )
            {
                egm.HighliteElement(elem_id.Get(i),Color.green,null);
                egm.HighliteElement(elem_id.Get(j),Color.green,null);
                
                egm.SetStdInfo("Comparing item indexed by "+i+"\nwith item indexed by "+j);
                
                Pause();
                
                if (egm.CompareElements(elem_id.Get(i),elem_id.Get(j)) <= 0)
                {
                    tmp_sm.HighliteElement(elem_id.Get(k),Color.red,Color.white);
                    
                    tmp_sm.Set(elem_id.Get(k),egm.Get(elem_id.Get(i)));
                                                    
                    fv.Set_Int_Variable(7,"",i);                                
                    fv.Set_PdtVector_Variable(3,"",tmp_sm.Get_Pdt_Manager());                
                    egm.SetStdInfo("Item "+i+" less than item "+j+"\nadding item "+i+" to temporary sequence");                                                               
                    Pause();                
                    
                    tmp_sm.UnHighliteElement(elem_id.Get(k));
                    
                    i ++;
                }
                else
                {   
                    tmp_sm.HighliteElement(elem_id.Get(k),Color.red,Color.white);
                    
                    tmp_sm.Set(elem_id.Get(k),egm.Get(elem_id.Get(j)));
                    
                    fv.Set_Int_Variable(8,"",j);                                
                    fv.Set_PdtVector_Variable(3,"",tmp_sm.Get_Pdt_Manager());
                    egm.SetStdInfo("Item "+i+" greater than item "+j+"\nadding item "+j+" to temporary sequence");                                
                    Pause();                
                    
                    tmp_sm.UnHighliteElement(elem_id.Get(k));
                    
                    j ++;                
                }
                
                egm.UnHighliteAllElements();
                
                k ++;
                
                fv.Set_Int_Variable(9,"",k);                                            
            }
            
            egm.SetStdInfo("Left range index i or right\nrange index j left defined range");
            
            Pause();        
                    
            if( i > m)
            {
                for(h=j;h<=r;h++)
                {                
                    egm.HighliteElement(elem_id.Get(h),Color.green,null);                
                    tmp_sm.HighliteElement(elem_id.Get(k+h-j),Color.red,Color.white);
                    
                    tmp_sm.Set(elem_id.Get(k+h-j),egm.Get(elem_id.Get(h)));

                    fv.Set_PdtVector_Variable(3,"",tmp_sm.Get_Pdt_Manager());                
                    
                    egm.SetStdInfo("Set item "+(k+h-j)+" of temporary\nsequence to item "+h);                                                               
                    Pause();                                
        
                    egm.UnHighliteElement(elem_id.Get(h));                            
                    tmp_sm.UnHighliteElement(elem_id.Get(k+h-j));
                }
            }
            else
            {
                for(h=i;h<=m;h++)
                {
                    egm.HighliteElement(elem_id.Get(h),Color.green,null);                
                    tmp_sm.HighliteElement(elem_id.Get(k+h-i),Color.red,Color.white);
                    
                    tmp_sm.Set(elem_id.Get(k+h-i),egm.Get(elem_id.Get(h)));

                    fv.Set_PdtVector_Variable(3,"",tmp_sm.Get_Pdt_Manager());                
                    
                    egm.SetStdInfo("Set item "+(k+h-i)+" of temporary\nsequence to item "+h);                                                               
                    Pause(); 
        
                    egm.UnHighliteElement(elem_id.Get(h));                                                        
                    tmp_sm.UnHighliteElement(elem_id.Get(k+h-i));
                }            
            }

            //und zur�chschreiben        
            for(h=l;h<=r;h++)        
            {
                egm.SetStdInfo("Writing back item "+h+"\nof temporary sequence");
                
                egm.HighliteElement(elem_id.Get(h),Color.red,Color.white);
                tmp_sm.HighliteElement(elem_id.Get(h),Color.green,null);
                
                egm.Set(elem_id.Get(h),tmp_sm.Get(elem_id.Get(h)));
                tgm.Set(elem_id.Get(h),tmp_sm.Get(elem_id.Get(h)));
                            
                fv.Set_PdtVector_Variable(2,"",egm.Get_Pdt_Manager());            
                                      
                Pause();
                
                egm.UnHighliteElement(elem_id.Get(h));            
                tmp_sm.UnHighliteElement(elem_id.Get(h));            
            }    
        }
        else
        {
            tgm.SetStdInfo("No further division possible\nExit recursive loop");            
            Pause();
        }        
    }
}




