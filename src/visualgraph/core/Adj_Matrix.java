package visualgraph.core;
import visualgraph.swing.Grf_Manager;

import java.io.*;

public class Adj_Matrix extends Object implements Serializable
{
    /**Verweis auf Grf_Manager, zu setzen wenn GUI verwendet wird*/
    private Grf_Manager grm;

    /**Anzahl Knoten*/
    private int anz_rows;
    
    /**Matrix*/
    private double[][] matrix;

    static final long serialVersionUID = -3117962286137899878L;
    
    /**Serialize write*/
    private void writeObject(java.io.ObjectOutputStream out) throws IOException
    {
        out.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();        
    }

    /**Konstruktor*/
    public Adj_Matrix (int rows)
    {
        if(rows == 0)
            rows = 1;

        anz_rows = rows;
        
        matrix = new double[rows][rows];
    }
    
    /**Copy-Kosntruktor*/
    public Adj_Matrix (Adj_Matrix src)
    {
        int i,ii;
        
        anz_rows = src.Rows();

        matrix = new double[anz_rows][anz_rows];
        
        for(i=1;i<=anz_rows;i++)
        {
            for(ii=1;ii<=anz_rows;ii++)
            {
                Set(i,ii,src.Get(i,ii));
            }    
        }
    }

    /**LinkToGUI: Verweis auf GUI setzen*/
    public void LinkToGUI (Grf_Manager grm)
    {
        this.grm = grm;
    }
    
    /**Get: Inhalt von Matrixelement (r,c) zur�ckgeben*/
    public double Get(int r, int c) 
    {
        return(matrix[r-1][c-1]);
    }    

    /**Set: Inhalt von Matrixlement (r,c) setzen*/
    public void Set(int r, int c, double value) 
    {
        matrix[r-1][c-1] = value;
    }

    /**Rows: Anzahl der Knoten der Adj-Matrix zur�ckgeben*/
    public int Rows() 
    {
        return(anz_rows);
    }  
    
    /**FillInfinite: Matrix mit Infiniten initialisieren */
    public void FillInfinite()
    {
        int i,ii;

        for(i=1;i<=Rows();i++)
        {
            for(ii=1;ii<=Rows();ii++)
            {
                Set(i,ii,Double.POSITIVE_INFINITY);
            }
        }
    }
    
    /**String-Repr�sentation der Adj-Matrix zur�ckgeben*/
    public String GetString ()
    {
        int i,ii;
        String s="";
        double value;

        if(Rows() == 0)
            return("empty");
            
        for(i=1;i<=Rows();i++)
        {
            if(s.equals("") == false)
                s = s+"\n";
            
            for(ii=1;ii<=Rows();ii++)
            {
                value = Get(i,ii);
 
                if(grm == null)
                {
                    if (ii == 1)
                        s=s+"("+i+","+ii+"): "+value;
                    else
                        s=s+", ("+i+","+ii+"): "+value;
                }
                else
                {
                    if (ii == 1)
                        s=s+"("+grm.GetId(i)+","+grm.GetId(ii)+"): "+value;
                    else
                        s=s+", ("+grm.GetId(i)+","+grm.GetId(ii)+"): "+value;
                }
            }
        }
       
        return(s);
    }    
}
