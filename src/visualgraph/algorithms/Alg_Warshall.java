/** Datei: Alg_Warshall.java*/
/** Berechnung der transitiven H�lle einer (n,n)-Matrix*/
/** nach dem Alg. von Warshall*/
package visualgraph.algorithms;

import visualgraph.core.Adj_Matrix;
import visualgraph.swing.Frm_Documentation;
import visualgraph.swing.Grf_Manager;

import java.awt.*;

public class Alg_Warshall extends Base_Algorithm
{
    /**Info: Ausgabe v. Informationen �ber diesen Alg.*/
    public void info (Frm_Documentation frm_doc) 
    {
        frm_doc.AppendLine("how this algorithm works (pseudo-code):\n");    
        frm_doc.AppendLine("void run()");
        frm_doc.AppendLine("{");        
        frm_doc.AppendLine("    A = adjacency-matrix of graph\n");	
        frm_doc.AppendLine("    for(int i=1;i<=n;i++)");
        frm_doc.AppendLine("    {");
        frm_doc.AppendLine("        for(int j=1;j<=n;j++)");
        frm_doc.AppendLine("        {");
        frm_doc.AppendLine("            if(A[j][i])");
        frm_doc.AppendLine("            {");
        frm_doc.AppendLine("                for(int k=1;k<=n;k++)");
        frm_doc.AppendLine("                {");
        frm_doc.AppendLine("                    A[j][k] = A[j][k] || A[i][k];");
        frm_doc.AppendLine("                }");
        frm_doc.AppendLine("            }");
        frm_doc.AppendLine("        }");
        frm_doc.AppendLine("    }"); 
        frm_doc.AppendLine("    now A contains the transitive cover"); 
        frm_doc.AppendLine("}");                                                
    }     
    
    /**Run: Start des Algorithmus*/
    public void run ()
    {           
        Adj_Matrix A;
        int i,j,k;
        Grf_Manager gm,egm;
        
        //Kontext-Dokument ermitteln
        gm = (Grf_Manager) Get_Context();
        
        /**Adjazenzmatrix des Graphen bereitstellen*/
        A = gm.GetMatrix (false);
        
        gm.SetStdInfo("initialize...");
        
        /**Ergebnisgraphen initialisieren*/
        egm = new Grf_Manager("Transitive Cover",gm,false);
        egm.Set_Show_Weights(false); /**Gewichtungen ausblenden        */
        egm.CircleOrder(gm.Get_Start_Node_Id()); /**Kreis-Anordnung der Knoten*/
        fm.AddResultFrame(egm);      /**Graph-Dokument hinzuf�gen          */
        fm.AddFrameToView(egm);      /**und zur Ansicht hinzuf�gen*/

        if(Get_Monitor_Vars())
            fm.AddFrameToView(fv);                                    

        fv.Set_AdjMatrix_Variable(1,"Trans.-Cover A'",A);                                                            
        
        gm.SetStdInfo("ready");

        /**Haltepunkt        */
        Pause();
        
        /**Alg. Start*/
        for(i=1;i<=A.Rows();i++) /**Zeilenschleife*/
        {
            fv.Set_Int_Variable(2,"Current row i",i);                                                                                
            
            for(j=1;j<=A.Rows();j++) /**Spaltenschleife*/
            {
                fv.Set_Int_Variable(3,"Current column j",j);                                                                                                

                fv.Set_Int_Variable(4,"Current A(j,i)",(int)A.Get(j,i));                                                                                                
                
                if(A.Get(j,i) != 0)
                {
                    gm.SetStdInfo("Connection (red) between "+gm.GetId(j)+","+gm.GetId(i)+" found");
                    
                    gm.HighliteConnection(j,i,Color.red,null);

                    /**Haltepunkt                            */
                    Pause();
                    
                    for(k=1;k<=A.Rows();k++)
                    {                        
                        fv.Set_Int_Variable(5,"Current k",k);                                                                                                

                        fv.Set_Int_Variable(6,"Current A(j,k)",(int)A.Get(j,k));                                                                                                
                        fv.Set_Int_Variable(7,"Current A(i,k)",(int)A.Get(i,k));                                                                                                
                                                
                        if( (A.Get(j,k) !=0 ) || (A.Get(i,k)!= 0) )
                        {
                            if (A.Get(j,k) !=0 )
                                gm.SetStdInfo("New connection between "+gm.GetId(j)+","+gm.GetId(k)+" found");
                            else
                                gm.SetStdInfo("New connection between "+gm.GetId(i)+","+gm.GetId(k)+" found");
                            
                            A.Set(j,k,1);
            
                            gm.HighliteNode(k,Color.orange,null);                                
                            
                            /**Kante im Ergebnisgraph erzeugen            */
                            egm.ConnectNode(j,k); 
                            
                            fv.Set_AdjMatrix_Variable(1,"",A);                                                    
                            
                            /**Haltepunkt*/
                            Pause();
                        }
                    }
                    
                    gm.UnHighliteAllNodes();
                }
            }    
        }
    }
}
