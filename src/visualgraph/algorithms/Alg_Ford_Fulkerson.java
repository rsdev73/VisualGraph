/** Datei: Alg_Ford_Fulkerson.java*/
/** Berechnung des maximalen Flusses eines gerichteten Graphen*/
package visualgraph.algorithms;

import java.awt.*;
import java.util.*;

import visualgraph.core.Adj_ConVector;
import visualgraph.core.Adj_ConVectorItem;
import visualgraph.core.Adj_Manager;
import visualgraph.core.Id_Vector;
import visualgraph.swing.Frm_Documentation;
import visualgraph.swing.Grf_Manager;

public class Alg_Ford_Fulkerson extends Base_Algorithm
{
    /**Info: Ausgabe v. Informationen �ber diesen Alg.*/
    public void info (Frm_Documentation frm_doc) 
    {
        frm_doc.AppendLine("how this algorithm works (pseudo-code):\n");    
        frm_doc.AppendLine("void run()");
        frm_doc.AppendLine("{");        
        frm_doc.AppendLine("    calculate all simple paths from source to hollow nodes\n");        
        frm_doc.AppendLine("    while there is a flow-enlarging path");        
        frm_doc.AppendLine("    {");        
        frm_doc.AppendLine("        calculate amount of flow-enlargement");                
        frm_doc.AppendLine("        calculate new total flow\n");                
        frm_doc.AppendLine("        if new amount of total flow > old amount of total flow");                        
        frm_doc.AppendLine("        {"); 
        frm_doc.AppendLine("            set the new flow along the path");                                
        frm_doc.AppendLine("        }"); 
        frm_doc.AppendLine("    }");
        frm_doc.AppendLine("}\n");        
    }
    
    /**Run: Start des Algorithmus*/
    public void run ()
    {       
        Grf_Manager gm,egm;        
        Adj_Manager calc,zyc_calc,new_flow;        
        Adj_ConVector act_path,con_vec,zyc_vec;
        Adj_ConVectorItem aci,zyc_aci,vgl_aci;
        Id_Vector src_nodes,dest_nodes,path_nodes;        
        Vector paths;
        int i,ii,j,start_node,end_node;
        boolean found,possible;        
        double edge_flow,edge_cap,delta, old_amount,new_amount;
        
        //Kontext-Dokument ermitteln
        gm = (Grf_Manager) Get_Context();
        
        gm.SetStdInfo("initialize...");
                                
        /**Ergebnisgraphen initialisieren*/
        egm = new Grf_Manager("Maximum Flow",gm,true);
        egm.Set_Show_Weights(true); /**Gewichtungen immer anzeigen*/
        fm.AddResultFrame(egm);     /**Graph-Dokument hinzuf�gen */                        
                
        //Alle Gewichtungen im Ergebnisgraph nullstellen
        con_vec = egm.ConVector(false);
        for(i=1;i<=con_vec.Size();i++)
            con_vec.Get(i).Set_Weight(0);    
        
        fm.AddFrameToView(egm);     /**und zur Ansicht hinzuf�gen*/
        
        if(Get_Monitor_Vars())
            fm.AddFrameToView(fv);                                    
        
        //Quellen und Senken des Graphen ermitteln
        src_nodes = gm.SrcNrVector();
        dest_nodes = gm.DestNrVector();
        
        //Pr�fung ob Quellen und Senken vorhanden sind
        if( (src_nodes.Size() == 0) || (dest_nodes.Size() == 0) )
        {
            gm.SetStdInfo("No source or hollow nodes existing.\nFord-Fulkerson stopped");
        
            Stop();
            
            return;            
        }
        
        //Kalkulationsgraph bereitstellen
        calc = new Adj_Manager(egm.Get_Adm_Manager(),true);

        //Kalkulationsgraph f�r die Berechnung zyklischer Pfade bereitstellen
        zyc_calc = new Adj_Manager(egm.Get_Adm_Manager(),true);
        
        //Alle Kanten �ber Knoten pr�fen
        zyc_vec = zyc_calc.ConVector(false);
        
        for(i=1;i<=zyc_vec.Size();i++)
        {
            zyc_aci = zyc_vec.Get(i);
            
            //Suche nach Gegenkante
            found = false;
            for(ii=1;ii<=zyc_vec.Size();ii++)
            {
                vgl_aci = zyc_vec.Get(ii);
                
                if( (vgl_aci.Get_Src_Node_Number() == zyc_aci.Get_Dest_Node_Number())
                && (vgl_aci.Get_Dest_Node_Number() == zyc_aci.Get_Src_Node_Number()) )
                {
                    //Gegenkante gefunden
                    found = true;
                    break;
                }
            }
            
            if(!found)
            {
                //Keine Gegenkante gefunden -> Gegenkante erzeugen und markieren
                (zyc_calc.ConnectNode(zyc_aci.Get_Dest_Node_Number(),
                    zyc_aci.Get_Src_Node_Number())).SetMarker();                          
            }
        }
        
        //Pfade des Zyklen-Graphen berechnen
        paths = zyc_calc.PathVector(src_nodes,dest_nodes);
        
        //Variable-Frame Ausgabe
        fv.Set_IdVector_Variable (1,"Source nodes",src_nodes);                
        fv.Set_IdVector_Variable (2,"Hollow nodes",dest_nodes);                

        //Node-Info: Quellen, Senken setzen
        for(i=1;i<=src_nodes.Size();i++)
        {
            gm.SetNodeInfo (src_nodes.Get(i),"Source");
            egm.SetNodeInfo (src_nodes.Get(i),"Source");
        }
        
        for(i=1;i<=dest_nodes.Size();i++)
        {
            gm.SetNodeInfo (dest_nodes.Get(i),"Hollow");
            egm.SetNodeInfo (dest_nodes.Get(i),"Hollow");            
        }
        
        //Initialer Flu� berechnen und anzeigen                
        fv.Set_Double_Variable (3,"Maximum amount of flow",recon_flow(egm.Get_Adm_Manager()));                        
                    
        gm.SetStdInfo("ready");
        
        /**Haltepunkt*/        
        Pause();

        /**Alg-Start*/               
        possible = true;
        
        do
        {            
            //Suche nach Verbesserungspfaden
            for(i=0;i<paths.size();i++)
            {
                act_path = (Adj_ConVector) paths.elementAt(i); //Pfad aus zyc_calc bereitstellen
                
                possible = true;       
                
                delta = 0;
            
                //f�r alle Kanten des Pfades aus zyc_calc
                for(ii=1;ii<=act_path.Size();ii++)
                {
                     aci = act_path.Get(ii);  //Kante aus zyc_calc bereitstellen
             
                     start_node = aci.Get_Src_Node_Number();
                     end_node = aci.Get_Dest_Node_Number();                 
                     
                     //Pr�fen auf zyklische Gegenkante
                     if(!aci.IsConnectionMarked())
                     {
                         edge_flow = calc.Get_Weight(start_node, end_node); //aktueller Flu� aus calc                 
                         edge_cap  = gm.Get_Weight(start_node, end_node); //Kapazit�t aus Ausgangsgraph
                        
                         //Flu�ver�nderung berechnen
                         if(delta == 0)
                         {
                            //Startwert delta setzen
                            delta = edge_cap - edge_flow;
                         }
                         else
                         {
                            //delta bereits gesetzt, evtl. der neuen Kap. anpassen
                            if(delta > (edge_cap-edge_flow))
                            {
                                delta = edge_cap-edge_flow;
                            }
                         }
                                    
                         //kein Flu�ver�nderung ? -> Abbrechen
                         if(delta <= 0)
                         {                                            
                            possible = false;
                            break;
                         }
                     }
                     else
                     {                 
                         //Gegenkante, die in calc nicht enthalten ist, start und end-Knoten vertauschen
                         edge_flow = calc.Get_Weight(end_node, start_node); //aktueller Flu� aus calc                 
                         edge_cap  = gm.Get_Weight(end_node, start_node); //Kapazit�t aus Ausgangsgraph                     
                         
                         //Flu�ver�nderung berechnen
                         if(delta == 0)
                         {
                            //Startwert delta setzen
                            delta = edge_cap - edge_flow;
                         }
                         else
                         {
                            //delta bereits gesetzt, evtl. der neuen Kap. anpassen
                            if(edge_flow < delta)
                            {
                                delta = edge_flow;
                            }
                         }
                         
                         //kein Flu�ver�nderung ? -> Abbrechen
                         if(delta <= 0)
                         {                                            
                            possible = false;
                            break;
                         }                     
                     }                    
                }

                if(possible)
                {                                        
                    //Pr�fen ob Gesamtflu� verbessert wird
                    new_flow = new Adj_Manager(calc,true);
                    
                    //Flu�verbesserungen eintragen
                    for(ii=1;ii<=act_path.Size();ii++)
                    {
                         aci = act_path.Get(ii);
            
                         start_node = aci.Get_Src_Node_Number();
                         end_node = aci.Get_Dest_Node_Number();                 
                     
                         //Pr�fen auf zyklische Gegenkante
                         if(!aci.IsConnectionMarked())
                         {
                             edge_flow = calc.Get_Weight(start_node, end_node); //aktueller Flu� aus calc                                         
                             new_flow.Set_Weight(start_node,end_node,edge_flow+delta);
                         }
                         else
                         {
                             edge_flow = calc.Get_Weight(end_node, start_node); //aktueller Flu� aus calc                                          
                             new_flow.Set_Weight(end_node,start_node,edge_flow-delta);                         
                         }
                    }
                    
                    //Wurde Flu� verbessert ?
                    old_amount = recon_flow(calc);
                    new_amount = recon_flow(new_flow);
                    
                    if(new_amount > old_amount)
                    {
                        path_nodes = new Id_Vector(gm);

                        for(ii=1;ii<=act_path.Size();ii++)
                        {
                             aci = act_path.Get(ii);

                             start_node = aci.Get_Src_Node_Number();
                             end_node = aci.Get_Dest_Node_Number();                 

                             if(!path_nodes.Contains(start_node))
                                path_nodes.Add(start_node);

                             if(!path_nodes.Contains(end_node))
                                path_nodes.Add(end_node);
                        }
                        
                        fv.Set_IdVector_Variable (4,"Flow-enlarging path",path_nodes);                                                    
                        fv.Set_Double_Variable (5,"Amount of flow-enlarging",delta);                                                    

                        gm.SetStdInfo("Flow enlarging path "+path_nodes.GetString()+" found");                        
                        
                        fv.Set_Double_Variable (6,"Last amount of flow",old_amount);                                                                            
                        fv.Set_Double_Variable (7,"New amount of flow",new_amount);                                                                            
                        
                        //Kanten markieren
                        for(ii=1;ii<=act_path.Size();ii++)
                        {
                             aci = act_path.Get(ii); //Kante aus zyc_calc bereitstellen

                             start_node = aci.Get_Src_Node_Number();
                             end_node = aci.Get_Dest_Node_Number();                 
                             
                             //Pr�fen auf zyklische Gegenkante
                             if(!aci.IsConnectionMarked())
                                 egm.HighliteConnection(start_node,end_node,Color.orange,null);
                             else                             
                                 egm.HighliteConnection(end_node,start_node,Color.orange,null);                                                                               
                        }                        

                        Pause();
                        
                        //f�r alle Kanten des Pfades
                        for(ii=1;ii<=act_path.Size();ii++)
                        {
                             aci = act_path.Get(ii); //Kante aus zyc_calc bereitstellen

                             start_node = aci.Get_Src_Node_Number();
                             end_node = aci.Get_Dest_Node_Number();                 

                             //Pr�fen auf zyklische Gegenkante
                             if(!aci.IsConnectionMarked())
                             {
                                 edge_flow = new_flow.Get_Weight(start_node,end_node);
                                 
                                 //neuer Flu� in calc eintragen
                                 calc.Set_Weight(start_node,end_node,edge_flow);                                                              
                                 
                                 //neuer Flu� in egm eintragen
                                 egm.Set_Weight(start_node,end_node,edge_flow);                                                                                          
                             }
                             else
                             {
                                 edge_flow = new_flow.Get_Weight(end_node,start_node);

                                 //neuer Flu� in calc eintragen
                                 calc.Set_Weight(end_node,start_node,edge_flow);                                                              
                                 
                                 //neuer Flu� in egm eintragen
                                 egm.Set_Weight(end_node,start_node,edge_flow);                                                                                                                       
                             }
                        }
                        
                        gm.SetStdInfo("Flow of path enlarged by "+delta);
                        
                        Pause();                    
                        
                        egm.UnHighliteAllConnections();
                        
                        break;                    
                    }
                    else
                        possible = false;
                }
            }
            
        } while(possible);       

        gm.SetStdInfo("No further flow-enlarging path found.\nMaximum flow "+recon_flow(egm.Get_Adm_Manager())+" reached");
        
        Pause();
        
        fv.Set_Double_Variable (3,"",recon_flow(egm.Get_Adm_Manager()));                        
   }    
      
   /**Berechnung des aktuellen Gesamt-Flusses eines Graphen*/   
   private double recon_flow (Adj_Manager graph)
   {
        int i;
        Id_Vector hollows;
        Adj_ConVector edges;
        Adj_ConVectorItem aci;
        double flow;
        
        flow = 0;
        
        edges = graph.ConVector(false);
        
        hollows = graph.DestNrVector();
        
        if(edges != null)
        {
            for(i=1;i<=edges.Size();i++)
            {
                aci = edges.Get(i);
                
                if(hollows.Contains(aci.Get_Dest_Node_Number()))
                {
                    flow += aci.Get_Weight();
                }
            }
        }               
        return(flow);
   }   
}