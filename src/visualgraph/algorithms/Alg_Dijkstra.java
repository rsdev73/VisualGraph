package visualgraph.algorithms;
import visualgraph.core.Adj_Matrix;
import visualgraph.core.Id_Vector;
import visualgraph.core.Weight_Vector;
import visualgraph.swing.Frm_Documentation;
import visualgraph.swing.Grf_Manager;

import java.awt.*;

/**Dijkstra Algorithmus*/

public class Alg_Dijkstra extends Base_Algorithm
{
    /**Info: Ausgabe v. Informationen �ber diesen Alg.*/
    public void info (Frm_Documentation frm_doc) 
    {
        frm_doc.AppendLine("how this algorithm works (pseudo-code):\n");    
        frm_doc.AppendLine("annotation: dist(i,j) = distance (cost) to reach node j from node i\n");
        frm_doc.AppendLine("void run()");
        frm_doc.AppendLine("{");
        frm_doc.AppendLine("    set j = start node");
        frm_doc.AppendLine("    set vector V = all nodes");
        frm_doc.AppendLine("    initialize path-vector P with j");
        frm_doc.AppendLine("    initialize weight-vector D with 0");
        frm_doc.AppendLine("    set vector Visited = empty");
        frm_doc.AppendLine("    set vector Checked = empty");
        
        frm_doc.AppendLine("    add j to vector Checked\n");        
        frm_doc.AppendLine("    remove nodes with no connectivity from V\n");
        
        frm_doc.AppendLine("    while Size of V != Size of Visited");        
        frm_doc.AppendLine("    {");                
        frm_doc.AppendLine("        check successors of j with minimal distance");
        frm_doc.AppendLine("        set w = successor of j with minimal distance");                                       
        frm_doc.AppendLine("        remove w from vector Checked");                                
        frm_doc.AppendLine("        add w to vector Visited\n");                                
        frm_doc.AppendLine("        for all k element of successors of w");                                
        frm_doc.AppendLine("        {");
        frm_doc.AppendLine("            if k not in Vector Visited and not in Vector Checked");                                
        frm_doc.AppendLine("            {");
        frm_doc.AppendLine("                add k to vector Checked");                                        
        frm_doc.AppendLine("                set D[k] = D[w]+dist(w,k)");                                        
        frm_doc.AppendLine("                set P[k] = w");                                                
        frm_doc.AppendLine("            }");                                                        
        frm_doc.AppendLine("            else");                                                                
        frm_doc.AppendLine("            {");                                                                
        frm_doc.AppendLine("                if k in vector Checked");                                                                        
        frm_doc.AppendLine("                {");                                                                                
        frm_doc.AppendLine("                    if D[k] > D[w] + dist(w,k)");                                                                                
        frm_doc.AppendLine("                    {");                                                                                        
        frm_doc.AppendLine("                        set D[k] = D[w]+dist(w,k)");                                                                                                
        frm_doc.AppendLine("                        set P[k] = w");                                                                                                        
        frm_doc.AppendLine("                    }");                                                                                                        
        frm_doc.AppendLine("                }");                                                                                                                
        frm_doc.AppendLine("            }");                                                                                                                
        frm_doc.AppendLine("        }");                                                                                                                        
        frm_doc.AppendLine("    }");                                                                                                                                
        frm_doc.AppendLine("}\n");        
    }     
 
    /**Hilfsvektoren*/
    Id_Vector P,V,desc,path;
    Weight_Vector D;
    
    Grf_Manager gm;        
    Grf_Manager egm;
    Grf_Manager pgm;
    
    /**Deijkstra Graph-Teilmengen*/
    Id_Vector Visited, Checked;

    /**Transitive H�lle d. Graphen*/
    Adj_Matrix trans_cover;
    
    /**SSSP(start_node j)*/
    public void run ()
    {
        int i,ii,w,k,j;
        double vgl;

        //Kontext-Dokument ermitteln
        gm = (Grf_Manager) Get_Context();

        gm.SetStdInfo("initialize...");
        
        /**Knotennummernvektor intialisieren*/
        V = gm.NodeNrVector(); 

        /**Pfadvektoren initialisieren*/
        P = new Id_Vector(gm);
        D = new Weight_Vector();

        /**Teilgraphen initialisieren*/
        Visited = new Id_Vector(gm);
        Checked = new Id_Vector(gm);        
        
        /**Startknoten setzen*/
        j = gm.Get_Start_Node_Id();
                
        /**Pfadvektor mit j initialisieren*/
        for(i=1;i<=V.Size();i++)
            P.Add(j);
        
        /**Gewichtungsvektor initialisieren*/
        for(i=1;i<=V.Size();i++)
            D.Add(0);

        /**Cost-Ergebnisgraph erzeugen*/
        egm = new Grf_Manager("Cost SSSP("+gm.GetId(j)+")",gm,false);
        egm.CircleOrder(j);        
        egm.Set_Show_Weights(true); /**Gewichtungen immer anzeigen        */
        fm.AddResultFrame(egm);                
        fm.AddFrameToView(egm);                                        

        /**Path-Ergebnisgraph aufbereiten*/
        pgm = new Grf_Manager("Route SSSP("+gm.GetId(j)+")",gm,false);
        pgm.Set_Show_Weights(true); /**Gewichtungen immer anzeigen*/
        fm.AddResultFrame(pgm);        

        //Variable-Monitor ggf. anzeigen
        if(Get_Monitor_Vars())
            fm.AddFrameToView(fv);                                    

        gm.SetStdInfo("ready");
        Pause();        
        
        /**nicht erreichbare Knoten ermitteln*/
        gm.SetStdInfo("checking unreachable nodes");
        
        trans_cover = gm.GetTransitiveCover(false);                

        fv.Set_IdVector_Variable(1,"Reachable nodes",V);                
        
        Pause();        
        
        for(i=1;i<=V.Size();i++)
        {
            if(V.Get(i) != j)
            {
                k = V.Get(i);
                
                if(trans_cover.Get(j,k) == 0)
                {
                    gm.HighliteNode(k,Color.red,null);                                                                 
                    
                    gm.SetStdInfo("node "+gm.GetId(k)+" not reachable");                
                    
                    V.Remove(k);
                                        
                    fv.Set_IdVector_Variable(1,"",V);                                         
                    
                    Pause();

                    gm.UnHighliteNode(k);                                                                 
                    
                    /**Knoten aus Ergebnisgraph ausblenden*/
                    egm.SetNodeVisible(k,false);
                    egm.CircleOrder(j);                    
                            
                    i = 0; /**Rewind: V erneut durchsuchen*/
                }
            }    
        }
                                                       
        /**Initial-Connection setzen*/
        for(i=1;i<=V.Size();i++)
            egm.ConnectNode(j,V.Get(i),D.Get(V.Get(i)));                                
       
        /**Startknoten in zu pr�fenden Knotenmenge aufnehmen*/
        Checked.Add(j); 

        fv.Set_IdVector_Variable(2,"Path vector",P);
        fv.Set_WeightVector_Variable(3,"Weight vector",D);
        fv.Set_IdVector_Variable(4,"Vector Checked",Checked);
        fv.Set_IdVector_Variable(5,"Vector Visited",Visited);
               
        while(Visited.Size() != V.Size())  
        {
            /**w�hle w ele. Checking, so da� f�r alle w' ele. Checking gilt D[w] <= D[w']*/
        
            gm.SetStdInfo("Checking successor(s) of "+gm.GetId(j)+" with shortest path");
        
            vgl = Double.POSITIVE_INFINITY;
            w = j;
            
            for(i=1;i<=Checked.Size();i++)
            {
                k = Checked.Get(i);
                
                if( (D.Get(k) < vgl) && (k != j) )
                {
                    vgl = D.Get(k);
                    w = k;
                }
            }                                
            
            gm.HighliteNode(w,Color.lightGray,null);
            
            if(w != j)
                gm.SetStdInfo("Checking successor(s) of "+gm.GetId(w));
             
            Checked.Remove(w);
            Visited.Add(w);

            fv.Set_IdVector_Variable(4,"",Checked);
            fv.Set_IdVector_Variable(5,"",Visited);
                                    
            desc = gm.DescNrVector(w); 
            
            Pause();            
                        
            for(i=1;i<=desc.Size();i++)
            {
                k = desc.Get(i);
                
                if( (Checked.Contains(k) == false) &&
                    (Visited.Contains(k) == false) ) /**noch nicht besuchter Knoten*/
                {
                    gm.HighliteConnection(w,k,Color.red,null);                                                                                
            
                    gm.HighliteNode(k,Color.orange,null);                                             
                    Checked.Add(k);

                    fv.Set_IdVector_Variable(4,"",Checked);
                    
                    D.Set(k,D.Get(w)+ gm.Get_Weight(w,k));
                    P.Set(k,w);
                    
                    egm.Set_Weight(j,k,D.Get(k));
                    
                    gm.SetStdInfo("successor "+gm.GetId(k)+" found, cost to reach "+
                                    D.Get(k)+"\nadd "+gm.GetId(k)+" to checklist");

                    fv.Set_IdVector_Variable(2,"",P);
                    fv.Set_WeightVector_Variable(3,"",D);
                       
                    Pause();
                }
                else
                {
                    if(Checked.Contains(k) == true)  /**k erneut erreicht*/
                    {
                        if(D.Get(k) > D.Get(w) + gm.Get_Weight(w,k))
                        {
                            gm.HighliteConnection(w,k,Color.red,null);                                                                                                            

                            /**F�rbe bisherige Kante zu k orange;*/
                            path = null;
                            path = new Id_Vector(gm);
                
                            resolve_path(k,false);      

                            if((path.Size()-1) >= 1)
                                gm.HighliteConnection(path.Get(path.Size()-1),k,Color.orange,null);                                            

                            path = null;
                            
                            /**neues Gewicht setzen*/
                            D.Set(k,D.Get(w) + gm.Get_Weight(w,k));

                            /**Pfad aktualisieren*/
                            P.Set(k,w);                            
                            
                            egm.Set_Weight(j,k,D.Get(k));
                            
                            gm.SetStdInfo("successor "+gm.GetId(k)+" checked, found shorter path\ncost to reach "+gm.GetId(k)+" now "+D.Get(k));                            
                            
                            fv.Set_IdVector_Variable(2,"",P);
                            fv.Set_WeightVector_Variable(3,"",D);

                            Pause();
                        }
                        else
                        {                            
                            gm.HighliteConnection(w,k,Color.orange,null);                                                                                                                                       

                            gm.SetStdInfo("successor "+gm.GetId(k)+" checked, found no shorter path");
                            
                            Pause();                            
                        }    
                    }
                    else
                    {
                        /**k ele. Visited*/
                        gm.HighliteConnection(w,k,Color.orange,null);                                                                                                                                                               

                        gm.SetStdInfo("successor "+gm.GetId(k)+" checked, node already visited");
                        
                        Pause();
                    }
                }   
            }            
        }
        
        
        protocol.AppendLine("\nAlgorithm results:");
        protocol.AppendLine("-----------------------------");        
        protocol.AppendLine("Shortest Path:");
        
        /**Pfadaufl�sung �bert alle Knoten ohne Startknoten*/
        for(i=1;i<=V.Size();i++)
        {
            if(V.Get(i) != gm.Get_Start_Node_Id())
            {
                protocol.Append("to "+gm.GetId(V.Get(i))+":  ");

                path = null;
                path = new Id_Vector(gm);
                
                resolve_path(V.Get(i),true);
                
                path.Add(V.Get(i));
                
                protocol.AppendLine("  cost: "+D.Get(V.Get(i)));
                
                for(ii=1;ii<=(path.Size()-1);ii++)
                {
                    pgm.ConnectNode(path.Get(ii),path.Get(ii+1),
                        D.Get(path.Get(ii+1)));             
                }
            }                
        }
                        
        protocol.AppendLine("");                        
        
        path = null;
    }
 
    void resolve_path (int node_nr, boolean do_output)
    {
        if(P.Get(node_nr) != node_nr)
            resolve_path(P.Get(node_nr),do_output);
        
        if(do_output)
            protocol.Append(gm.GetId(node_nr)+"  ");    
        
        if(path != null)
            path.Add(node_nr);        
    }
}
