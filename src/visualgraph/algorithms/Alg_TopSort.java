package visualgraph.algorithms;
import visualgraph.core.Adj_Matrix;
import visualgraph.core.Adj_Vector;
import visualgraph.core.Adj_VectorItem;
import visualgraph.core.Id_Vector;
import visualgraph.swing.Frm_Documentation;
import visualgraph.swing.Grf_Manager;

import java.awt.*;

/**Top-Sort Algorithmus*/

public class Alg_TopSort extends Base_Algorithm
{
    /**Quellen d. Graphen*/
    private Adj_Vector Q;
    private Grf_Manager gm;
    private Grf_Manager egm;            

    /**Info: Ausgabe v. Informationen �ber diesen Alg.*/
    public void info (Frm_Documentation frm_doc) 
    {
        frm_doc.AppendLine("how this algorithm works (pseudo-code):\n");    
        frm_doc.AppendLine("void run()");
        frm_doc.AppendLine("{");        
        frm_doc.AppendLine("    Q = vector of all graph source nodes");
        frm_doc.AppendLine("    for all q element of Q");
        frm_doc.AppendLine("    {");        
        frm_doc.AppendLine("        mark q");
        frm_doc.AppendLine("        add q to result-vector");
        frm_doc.AppendLine("        topsort(q) Annotation: recursive call with source node q");
        frm_doc.AppendLine("    }");
        frm_doc.AppendLine("}\n");
        
        frm_doc.AppendLine("void topsort (node i)");
        frm_doc.AppendLine("{");                
        frm_doc.AppendLine("    for all nodes j element of successors of i");        
        frm_doc.AppendLine("    {");        
        frm_doc.AppendLine("        if all nodes k element of predecessors of j are marked");                
        frm_doc.AppendLine("        {");                
        frm_doc.AppendLine("            mark j");
        frm_doc.AppendLine("            add j to result-vector");
        frm_doc.AppendLine("            topsort(j) Annotation: further recursive call with node j");        
        frm_doc.AppendLine("        }");                        
        frm_doc.AppendLine("    }");                                
        frm_doc.AppendLine("}");                                        
    }     
    
    /**Run: Start des Algorithmus*/
    public void run ()
    {           
        int i;
        Adj_Vector result;
        Adj_VectorItem q;
        Adj_Matrix trans_cover;
        Id_Vector V;
        boolean zyclic;
        
        //Kontext-Dokument ermitteln
        gm = (Grf_Manager) Get_Context();
        
        gm.SetStdInfo("initialize...");
        
        Q = gm.SrcVector();        

        /**Ergebnis-Vektor - initialisieren*/
        result = new Adj_Vector(gm);

        /**Ergebnis-Graf initialisieren*/
        egm = new Grf_Manager("TopSort",gm,false);
        egm.Set_Show_Weights(false); /**ohne Gewichtungen*/
        fm.AddResultFrame(egm);
        fm.AddFrameToView(egm);                
        
        if(Get_Monitor_Vars())
            fm.AddFrameToView(fv);                                    
        
        /**Quellen als Source beschriften*/
        for (i=1;i<=Q.Size();i++)
            gm.SetNodeInfo(Q.Get(i).GetNodeNr(),"source");
        
        fv.Set_AdjVector_Variable(1,"Graph source(s)",Q);        
        
        gm.SetStdInfo("ready");
        Pause();
    
        //Zyklentest: Berechnen der transitive H�lle des Graphen
        trans_cover = gm.GetTransitiveCover(false);
        
        V = gm.NodeNrVector();
        
        zyclic = false;
        
        for(i=1;i<=V.Size();i++)
        {
            if(trans_cover.Get(V.Get(i),V.Get(i)) != 0)
            {
                zyclic = true;
                break;
            }
        }    
        
        //Graph zyklisch: Information ausgeben und Ende
        if(zyclic)
        {
            gm.SetStdInfo("No directed acyclic graph.\nTopSort stopped");
        
            Stop();
            
            return;
        }
        
        /**Alle Quellen markieren und in Ergebnisliste aufnehmen*/
        for (i=1;i<=Q.Size();i++)
        {                                    
            /**Markiere Quelle*/
            q = Q.Get(i);
            q.SetNodeMarker();
            
            fv.Set_String_Variable(5,"Current source node",q.GetId());                        
            
            /**Element in Ergebnismenge aufnehmen*/
            result.Add(q);

            fv.Set_AdjVector_Variable(2,"Result node vector",result);
                        
            egm.HighliteNode(q.GetNodeNr(),Color.lightGray,null);
            gm.SetStdInfo("mark source: "+gm.GetId(q.GetNodeNr())+" \nand add to result");                            
                        
            egm.BlockOrderByVector(result,5);
            
            Pause();    
            
            /**nun rekursiv mit jew. Quelle weitermachen*/
            topsort(q,result);            
        }
        
        egm.ReconnectByVector(result);                                        
    }
    
    private void topsort (Adj_VectorItem i, Adj_Vector result)
    {
        Adj_Vector J,K;
        Adj_VectorItem j,k;
        int n,ii;
        boolean all_marked;
        
        /**Alle Nachfolger von i ermitteln*/
        J = gm.DescVector(i.GetNodeNr());
        
        gm.SetStdInfo("checking direct successors of "+i.GetId()+"\n (orange nodes)");                            
        Pause();
        
        fv.Set_AdjVector_Variable(3,"Direct successors of "+i.GetId(),J);        
        
        /**Gibt es Nachfolger ?*/
        if(J.Size()!=0)
        {            
            /**f�r alle Nachfolger von i:*/
            for (n=1;n<=J.size();n++)
            {
                j = J.Get(n);
            
                gm.HighliteNode(j.GetNodeNr(),Color.orange,null);

                fv.Set_String_Variable(6,"Current direct successor\nof node "+i.GetId(),j.GetId());                        
                
                /**sind alle Vorg�nger des Nachfolgers von i markiert ?*/
                all_marked = true;
                
                K = gm.AscVector(j.GetNodeNr()); /**Vorg�nger ermitteln*/
                
                gm.SetStdInfo("checking mark of direct predecessor of "+gm.GetId(j.GetNodeNr())+"\n(red nodes)");                                            
                Pause();
                
                fv.Set_AdjVector_Variable(4,"Direct predecessor\nof node "+gm.GetId(j.GetNodeNr()),K);                                    
                
                /**Gibt es Vorg�nger ?            */
                if(K.Size()!=0)
                {
                    for(ii=1;ii<=K.Size();ii++)
                    {
                        k = K.Get(ii);                    

                        fv.Set_String_Variable(7,"Current direct predecessor\nof node "+j.GetId(),k.GetId());                        
                
                        gm.HighliteNode(k.GetNodeNr(),Color.red,null);
                    
                        /**Vorg�nger markiert*/
                        if (!k.IsNodeMarked())
                        {
                            all_marked = false;
                            break;
                        }
                        
                        Pause();
                        
                        gm.UnHighliteNode(k.GetNodeNr());
                    }

                    /**falls alle Vorg�nger markiert:*/
                    
                    if(all_marked == true)
                    {
                        /**falls Knoten noch nicht markiert*/
                        if(j.IsNodeMarked() == false)
                        {
                            /**Knoten markieren*/
                            j.SetNodeMarker();

                            /**Knoten in Ergebnismenge aufnehmen*/
                            result.Add(j);
                
                            fv.Set_AdjVector_Variable(2,"",result);
            
                            egm.HighliteNode(j.GetNodeNr(),Color.lightGray,null);                             
                            gm.SetStdInfo("no umarked direct predecessors found\nmark node: "+gm.GetId(j.GetNodeNr())+" \nand add to result");                            
                            
                            egm.BlockOrderByVector(result,5);
                            
                            Pause();                                                            
                        }
                        
                        /**und weitersuchen*/
                        topsort(j,result);
                    }
                }
                else
                {
                    /**Keine Vorg�nger:*/
                    gm.SetStdInfo("no direct predecessor detected");                                            
                    Pause();

                    /**falls Knoten noch nicht markiert*/
                    if(j.IsNodeMarked() == false)
                    {
                        /**Knoten markieren*/
                        j.SetNodeMarker();

                        /**Knoten in Ergebnismenge aufnehmen*/
                        result.Add(j);                    

                        fv.Set_AdjVector_Variable(2,"",result);
                            
                        egm.HighliteNode(j.GetNodeNr(),Color.lightGray,null);
                        gm.SetStdInfo("mark node: "+gm.GetId(j.GetNodeNr())+" \nand add to result");                            
                            
                        egm.BlockOrderByVector(result,5);                        
                        
                        Pause();                                                
                    }
                }

                gm.UnHighliteNode(j.GetNodeNr());
                
                /**Vorg�ngerliste l�schen*/
                K = null;                           
            }                                               
        }
   }
}
