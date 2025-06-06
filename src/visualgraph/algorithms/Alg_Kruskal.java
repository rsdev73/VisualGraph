/** Datei: Alg_Kruskal.java*/
/** Berechnung des minimalen Spannbaums eines ungerichteten Graphen*/
package visualgraph.algorithms;

import visualgraph.core.Adj_ConVector;
import visualgraph.core.Adj_ConVectorItem;
import visualgraph.core.Id_Vector;
import visualgraph.swing.Frm_Documentation;
import visualgraph.swing.Grf_Manager;

import java.awt.*;

public class Alg_Kruskal extends Base_Algorithm
{
    /**Info: Ausgabe v. Informationen �ber diesen Alg.*/
    public void info (Frm_Documentation frm_doc) 
    {
        frm_doc.AppendLine("how this algorithm works (pseudo-code):\n");    
        frm_doc.AppendLine("void run()");
        frm_doc.AppendLine("{");        
        frm_doc.AppendLine("    set SE = sorted set (by weight) of all graph edges");        
        frm_doc.AppendLine("    set checked = empty node set");        
        frm_doc.AppendLine("    set unchecked = V (set of all graph nodes)");        
        frm_doc.AppendLine("    set index = 1\n");        
        frm_doc.AppendLine("    while |checked| != |V|");                
        frm_doc.AppendLine("    {");                
        frm_doc.AppendLine("        select edge SE[index] of sorted set\n");                
        frm_doc.AppendLine("        if checked contains one node of the selected edge");                
        frm_doc.AppendLine("        {");                
        frm_doc.AppendLine("            add nodes of the selected edge to checked");                
        frm_doc.AppendLine("            set unchecked = V - checked");                        
        frm_doc.AppendLine("            remove edge from set SE");                
        frm_doc.AppendLine("            connect nodes in result graph");                
        frm_doc.AppendLine("            set index = 1");                
        frm_doc.AppendLine("        }");
        frm_doc.AppendLine("        else");
        frm_doc.AppendLine("        {");                        
        frm_doc.AppendLine("            skipping selected edge");                
        frm_doc.AppendLine("            set index = index + 1 for selecting next edge in SE");                
        frm_doc.AppendLine("        }");
        frm_doc.AppendLine("    }");
        frm_doc.AppendLine("}\n");
    }
    
    /**Run: Start des Algorithmus*/
    public void run ()
    {       
        Adj_ConVector edges,sort_edges;
        Grf_Manager gm,egm;
        Id_Vector V, checked, unchecked;
        Adj_ConVectorItem aci;
        int i,j,index;
        double sum;
        
        //Kontext-Dokument ermitteln
        gm = (Grf_Manager) Get_Context();
        
        //**Knotenmenge des Graphen bereitstellen
        V = gm.NodeNrVector();
        
        /**Kantenmenge des Graphen bereitstellen*/
        edges = gm.ConVector(true);
        
        gm.SetStdInfo("initialize...");

        /**Ergebnisgraphen initialisieren*/
        egm = new Grf_Manager("Min. Spanning Tree",gm,false);
        egm.Set_Show_Weights(true); /**Gewichtungen immer anzeigen*/
        fm.AddResultFrame(egm);     /**Graph-Dokument hinzuf�gen */
        fm.AddFrameToView(egm);     /**und zur Ansicht hinzuf�gen*/

        if(Get_Monitor_Vars())
            fm.AddFrameToView(fv);                                    

        sum = 0;       
        
        //Sortieren der Kanten nach Gewichtung        
        sort_edges = edges.SortByWeight(false);

        fv.Set_AdjConVector_Variable (1,"Set of edges (sorted\nby weight)",sort_edges);        
        
        //Knotennummernmengen erzeugen                
        checked = new Id_Vector(gm);
        unchecked = V.Difference(checked);

        fv.Set_IdVector_Variable (2,"Checked nodes",checked);                
        fv.Set_IdVector_Variable (3,"Unchecked nodes",unchecked);                        
        fv.Set_Double_Variable (4,"Total Path Cost",sum);                        
        
        gm.SetStdInfo("ready");
        
        /**Haltepunkt*/        
        Pause();

        /**Alg-Start*/
        index = 1;
        
        while(checked.Size() != V.Size())
        {
            if(sort_edges.Size() == 0)
                break;
                            
            //w�hle erste Kante aus sortierter Kantenmenge
            aci = sort_edges.Get(index);

            i = aci.Get_Src_Node_Number();
            j = aci.Get_Dest_Node_Number();

            gm.HighliteConnection(i,j,Color.orange,null);
            
            if (index == 1)
                gm.SetStdInfo("Selecting first edge ("+gm.GetId(i)+","+gm.GetId(j)+") from sorted set");                
            else
                gm.SetStdInfo("Selecting "+index+". edge ("+gm.GetId(i)+","+gm.GetId(j)+") from sorted set");                
                
            Pause();
            
            if( ((checked.Contains(i) == true) && (checked.Contains(j) == false))
             || ((checked.Contains(j) == true) && (checked.Contains(i) == false)) 
             || (checked.Size() == 0) )
            {
                if( checked.Size() != 0)
                    gm.SetStdInfo("Nodes of selected edge ("+gm.GetId(i)+","+gm.GetId(j)+")\nreachable from set of checked nodes,\nadding nodes to checked");
                else
                    gm.SetStdInfo("Adding nodes ("+gm.GetId(i)+","+gm.GetId(j)+") of selected edge to checked");                
                    
                if(checked.Contains(i) == false)
                {
                    checked.Add(i);  
                
                    gm.HighliteNode(i,Color.lightGray,null);
                }
                
                if(checked.Contains(j) == false)            
                {
                    checked.Add(j);                  

                    gm.HighliteNode(j,Color.lightGray,null);    
                }
                
                unchecked = V.Difference(checked);                

                sum += aci.Get_Weight();
                
                egm.ConnectNode(i,j,aci.Get_Weight());                    

                gm.HighliteConnection(i,j,Color.red,null);                
                
                //Kante aus Kantenmenge entfernen
                sort_edges.removeElement(aci);    

                index = 1;
                
                fv.Set_AdjConVector_Variable (1,"",sort_edges);                
                fv.Set_IdVector_Variable (2,"",checked);                
                fv.Set_IdVector_Variable (3,"",unchecked);                        
                fv.Set_Double_Variable (4,"",sum);                        
        
                Pause();
            }                
            else
            {                
                gm.UnHighliteConnection(i,j);                
                
                index ++;

                if( ((checked.Contains(i) == true) && (checked.Contains(j) == true)) )                
                    gm.SetStdInfo("Nodes of selected edge already checked,\n skipping edge");                                
                else                
                    gm.SetStdInfo("Nodes of selected edge not\nreachable from set of checked nodes,\n skipping edge");                
                
                Pause();
            }
        }
        
        protocol.AppendLine("\nAlgorithm results:");
        protocol.AppendLine("--------------------------------------------");        
        protocol.AppendLine("Total Path Cost of min. Spanning Tree: "+sum);
        protocol.AppendLine("");        
    }    
}