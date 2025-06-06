package visualgraph.swing;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import visualgraph.core.Adj_ConVector;
import visualgraph.core.Adj_Matrix;
import visualgraph.core.Adj_Vector;
import visualgraph.core.Id_Vector;
import visualgraph.core.Pdt_Element;
import visualgraph.core.Pdt_Manager;
import visualgraph.core.Weight_Vector;
import visualgraph.util.Uti_MultiLabel;

/**Frm_Variable: Monitor-Frame f. Alg.-Steuervariablen*/
public class Frm_Variable extends Frm_Frame
{
    /**Ursprungsg��en-Font*/
    private static final int font_size = 9;    
    private static final int space_betcol = 5;
    private static final int top_offset   = 30;
    public static final int max_value_chars = 45;    
    
    /**Variablen-Typen*/
    static final int VAR_TYPE_INT            = 0;
    static final int VAR_TYPE_DOUBLE         = 1;
    static final int VAR_TYPE_STRING         = 2;
    static final int VAR_TYPE_ADJ_VECTOR     = 3;
    static final int VAR_TYPE_ID_VECTOR      = 4;
    static final int VAR_TYPE_WEIGHT_VECTOR  = 5;
    static final int VAR_TYPE_ADJ_MATRIX     = 6;
    static final int VAR_TYPE_ADJ_CON_VECTOR = 7;
    static final int VAR_TYPE_PDT_VECTOR     = 8;    
    static final int VAR_TYPE_PDT_ELEMENT    = 9;    
    
    /**ScrollPane*/
    private ScrollPane frm_scroll;
    
    /**zugeh�riges Panel*/
    private Grf_DoublePanel frm_panel;

    /**Labels und Value Vector*/
    private Vector vars;

    /**Title*/
    private Grf_Label title_label;
    
    static final long serialVersionUID = -4757968691321086721L;
    
    /**Frm_VarItem: Variable Datenstruktur*/
    class Frm_VarItem extends Object implements Serializable
    {
		private static final long serialVersionUID = -6993157205910180607L;

		/**Variable-Identifactor*/
        private int id;
        
        private String name;
        private Object value;
        private int typ;

        private Grf_Label  out_name;
        private Grf_Label  out_value;        

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
        public Frm_VarItem(Frm_Frame frm)
        {
            name = "";
            value = null;
            
            /**Labels erzeugen*/
            out_name = new Grf_Label(frm,"",Uti_MultiLabel.LEFT);
            out_name.setFont(InitialFont());

            out_value = new Grf_Label(frm,"",Uti_MultiLabel.LEFT);            
            out_value.setFont(InitialFont());
        }

        public int Get_Id()
        {
            return(id);
        }
        
        public String Get_Name()
        {
            return(name);
        }

        public Object Get_Value()
        {
            return(value);
        }

        public Grf_Label Get_Out_Name()
        {
            return(out_name);
        }

        public Grf_Label Get_Out_Value()
        {
            return(out_value);
        }

        public int Get_Typ()
        {
            return(typ);
        }
        
        public void Set_Value(int id, String name, Object value, int typ)
        {
            int int_val;
            Integer int_obj;
            Double double_obj;
            double double_val;
            String str_val;
            Adj_Vector adj_vec;
            Adj_ConVector adj_con_vec;            
            Id_Vector id_vec;
            Weight_Vector weight_vec;
            Adj_Matrix adj_mat;
            Pdt_Manager pdm;
            Pdt_Element pde;
            
            if( (name == null) || (value == null) )
                return;
                
            this.id = id;                
            this.value = value;
            this.typ = typ;

            if(name.equals("") == false)
            {
                if(this.name.equals(name) == false) 
                {
                    this.name = name;            
                    out_name.setLabel(name);
                }
            }
            
            switch(typ)
            {
                case VAR_TYPE_INT:
            
                    int_obj = (Integer) value;
                    int_val = int_obj.intValue();
                    
                    out_value.setLabel(""+int_val);
                    out_value.delimitWidth(max_value_chars);
                    
                    break;                    
                    
                case VAR_TYPE_DOUBLE:
            
                    double_obj = (Double) value;
                    double_val = double_obj.doubleValue();
                    
                    out_value.setLabel(""+double_val);
                    out_value.delimitWidth(max_value_chars);
                    
                    break;                                        

                case VAR_TYPE_STRING:
            
                    str_val = (String) value;
                    out_value.setLabel(str_val);
                    out_value.delimitWidth(max_value_chars);

                    break;                                        

                case VAR_TYPE_ADJ_VECTOR:
            
                    adj_vec = (Adj_Vector) value;
                    out_value.setLabel(adj_vec.GetString());
                    out_value.delimitWidth(max_value_chars);
                    
                    break;                                                            

                case VAR_TYPE_ADJ_CON_VECTOR:
            
                    adj_con_vec = (Adj_ConVector) value;
                    out_value.setLabel(adj_con_vec.GetString());
                    out_value.delimitWidth(max_value_chars);
                    
                    break;                                                            

                case VAR_TYPE_ID_VECTOR:
            
                    id_vec = (Id_Vector) value;
                    out_value.setLabel(id_vec.GetString());
                    out_value.delimitWidth(max_value_chars);
                    
                    break;                                                            

                case VAR_TYPE_WEIGHT_VECTOR:
            
                    weight_vec = (Weight_Vector) value;
                    out_value.setLabel(weight_vec.GetString());
                    out_value.delimitWidth(max_value_chars);
                    
                    break;                     
                    
                case VAR_TYPE_ADJ_MATRIX:
                    
                    adj_mat = (Adj_Matrix) value;
                    out_value.setLabel(adj_mat.GetString());
                    break;
                
                case VAR_TYPE_PDT_VECTOR:
                
                    pdm = (Pdt_Manager) value;
                    out_value.setLabel(pdm.GetString());
                    out_value.delimitWidth(max_value_chars);
                    break;

                case VAR_TYPE_PDT_ELEMENT:
                
                    pde = (Pdt_Element) value;
                    out_value.setLabel(pde.toString());
                    out_value.delimitWidth(max_value_chars);
                    break;
                    
                default:
                    break;
            }            
        }        
    }    
    
    /**Konstruktoren*/
    public Frm_Variable (Frm_Manager mgr)
    {
        this("",mgr);
  	}

    public Frm_Variable (String name, Frm_Manager mgr)
    {   
        Dimension vp_size;
        
        /**Verweise setzen*/
        frm_mgr = mgr;
           		
        /**Name initialisieren*/
        Set_Name(name);
        
        /**Font setzen        */
        setFont(InitialFont());
        
        /**Neues ScrollPane erzeugen*/
        frm_scroll = new ScrollPane();
        add(frm_scroll);
        
        /**Neues Panel erzeugen, Layout Manager verwerfen*/
        frm_panel = new Grf_DoublePanel();

        /**Title-Label bereitstellen*/
        title_label = new Grf_Label(this,name,Uti_MultiLabel.LEFT);        
        title_label.setFont(InitialFont());
        frm_panel.add(title_label);
        
        /**Panel-Resize u. der Scroll-Pane hinzuf�gen*/
        vp_size = frm_scroll.getViewportSize();        
        vp_size.width = vp_size.width - 16;
        vp_size.height = vp_size.height - 16;

        frm_panel.setSize(vp_size.width,vp_size.height);
        frm_scroll.add(frm_panel);
        
        /**Event-Listeners registrieren*/
		addComponentListener(new Frm_ComponentEvent());			
		
		/**Label-Vektor erzeugen*/
		vars = new Vector();
		
		Set_Edited();
    }

    /**InitialFont: Initialisierungsgr��e Fonts zur�ckgeben*/
    public Font InitialFont()
    {
        return(new Font("SansSerif",Font.PLAIN,(int) font_size));     
    }    

    /**Set_Title*/
    public void SetTitle(String text)
    {
        if(text == null)
            return;
            
        title_label.setLabel(text);
    }
    
    /**InitialSize: Initial Size setzen*/
    public void InitialSize(int width, int height)
    {
        Dimension new_dim;
        
        new_dim = RecalcNeededDimension();
        
        /**Panel Resize*/
        setSize(width,height);        

        /**Scroll-Pane Resize*/
        synchronized(frm_scroll)
        {
            /**Scroller Resize*/
            frm_scroll.setSize(width,height);
        
            /**Adm-Panel Resize*/
            frm_panel.setSize(new_dim);                            
            
            frm_scroll.doLayout();                                        
        }    
    }
    
    /**RecalcNeededDimension: Berechnet die ben�tigte Gr��e des inneren Panels*/
    private Dimension RecalcNeededDimension ()
    {
        Dimension dim,vp_size;
        
        /**Preferred Dimension berechnen*/
        dim = RecalcPreferredDimension();        
        
        /**Minimale Gr��e ist ViewPort*/
        vp_size = frm_scroll.getViewportSize();
        
        if(dim.width < vp_size.width)
            dim.width = vp_size.width;

        if(dim.height < vp_size.height)
            dim.height = vp_size.height;

        return(dim);
    }
    
    /**RecalcNeededDimension: Berechnet die aktuell ben�tigte Gr��e des Frames*/
    public Dimension RecalcPreferredDimension () 
    { 
        int i,height,width,tn_width,tn_height,tv_width,tv_height;
        Grf_Label out_name,out_value;        
        Frm_VarItem act_item;
        FontMetrics fm;
        
        fm = getFontMetrics(getFont());
        
        width = 2*fm.charWidth('-')*max_value_chars;            
        height = fm.getHeight();
                
        /**Name-Labels pr�fen        */
        for(i=0;i<vars.size();i++)
        {
            act_item = (Frm_VarItem) vars.elementAt(i);
            
            out_name  = act_item.Get_Out_Name();
            out_value = act_item.Get_Out_Value();            
            
            tn_width = out_name.getSize().width;
            tn_height = out_name.getSize().height;

            tv_width = out_value.getSize().width;            
            
            if(tv_width < (fm.charWidth('-')*max_value_chars))
            {
                tv_width = fm.charWidth('-')*max_value_chars;
            }
            
            tv_height = out_value.getSize().height;
            
            height += Math.max(tn_height,tv_height);
            
            if( (tn_width+tv_width) > width)
                width = tn_width+tv_width;
        }
       
        return(new Dimension(width+space_betcol+16,height+16));
    }   
    
    /**Handle_ComponentResized: overrides Frm_Frame */
    public void Handle_ComponentResized (ComponentEvent event)
    {
        Container cont_panel;
        Dimension new_dim;
        
        /**�bergeordnetes Panel bereitstellen*/
        cont_panel = (Container) getParent();
        if(cont_panel == null)
            return;
            
        new_dim = RecalcNeededDimension();
         
        if( (cont_panel.getSize().equals(getSize()) == false) ||
            (frm_panel.getSize().equals(new_dim) == false) )
        {
            /**Panel-Resize*/
            setSize(cont_panel.getSize().width,cont_panel.getSize().height);        
            
            /**Scroll-Pane Resize*/
            synchronized(frm_scroll)
            {
                frm_scroll.setSize(cont_panel.getSize().width,cont_panel.getSize().height);        
            
                /**Adm-Panel Resize*/
                frm_panel.setSize(new_dim);
                            
                frm_scroll.doLayout();			                            
            }    
        }
    }
    
    /**LookupItem: Suche nach Variable*/
    private Frm_VarItem LookupItem(int id)
    {
        int i;
        Frm_VarItem act_item;
       
        for(i=0;i<vars.size();i++)
        {
            act_item = (Frm_VarItem) vars.elementAt(i);

            if(act_item.Get_Id() == id)
            {
                return(act_item);
            }
        }
        return(null);
    }
    
    /**Get_Container: Container f�r Items zur�ckgeben*/
    public Panel Get_Container()
    {
        return(frm_panel);
    }    
 
    public Graphics Get_DrawGraphics ()
    {
        return(frm_panel.getDblGraphics());
    }    
 
    /**Get_ScrollPane: Aktueller Scroll-Container zur�ckgeben*/
    public ScrollPane Get_ScrollPane()
    {
        return(frm_scroll);
    }
 
    private int GetDividPos()
    {
        int i,divid,width;
        Grf_Label out_name;        
        Frm_VarItem act_item;
        
        divid = 0;        
        for(i=0;i<vars.size();i++)
        {
            act_item = (Frm_VarItem) vars.elementAt(i);
            
            out_name  = act_item.Get_Out_Name();
            
            width = out_name.getSize().width;
            
            if(width > divid)
                divid = width;
        }        
        
        return(divid+(int)(space_betcol/2));
    }
    
    /**RealignLabels: Labels neu anordnen*/
    private void RealignLabels()
    {
        int i,act_offset,divid,width;
        Grf_Label out_name,out_value;        
        Frm_VarItem act_item;
        
        divid = GetDividPos();
        
        act_offset = 0;        
        for(i=0;i<vars.size();i++)
        {
            act_item = (Frm_VarItem) vars.elementAt(i);
            
            out_name  = act_item.Get_Out_Name();
            out_value = act_item.Get_Out_Value();            
        
            /**Labels posiotionieren*/
            out_name.setLocation(0,act_offset+top_offset);
            out_value.setLocation(divid+(int)(space_betcol/2),act_offset+top_offset);
            
            act_offset += Math.max(out_name.getSize().height,out_value.getSize().height);
        }        
    }

    /**Set_Variable: Setzen eines Variablenwerts intern*/
    private void Set_Variable (int id, String name, Object value, int typ)
    {
        Frm_VarItem act_item;
        Grf_Label out_name,out_value;        
        
        act_item = LookupItem(id);   
        if(act_item == null)
        {
            /**neue Variable anlegen*/
            act_item = new Frm_VarItem(this);

            act_item.Set_Value(id,name,value,typ);
            
            vars.addElement(act_item);
            
            /**Labels einh�ngen*/
            out_name  = act_item.Get_Out_Name();
            out_value = act_item.Get_Out_Value();                        
            
            frm_panel.add(out_name);
            frm_panel.add(out_value);            
        }
        else
        {
            /**Variable schon vorhanden -> value austauschen*/
            act_item.Set_Value(id,name,value,typ);
        }

        if(getParent() != null)
        {
            /**und Gr��e neu berechnen*/
            Handle_ComponentResized (new ComponentEvent(getParent(),
                    ComponentEvent.COMPONENT_RESIZED));
        }
        
        /**und neues Align setzen...*/
        RealignLabels();
    }                
    
    /**Set_Int_Variable: Setzen eines integer Variablenwerts*/
    public void Set_Int_Variable (int id, String name, int value)
    {
        Set_Variable (id,name,Integer.valueOf(value),VAR_TYPE_INT);
    }                

    /**Set_Double_Variable: Setzen eines Variablenwerts*/
    public void Set_Double_Variable (int id, String name, double value)
    {
        Set_Variable (id,name,Double.valueOf(value),VAR_TYPE_DOUBLE);
    }                
    
    /**Set_String_Variable: Setzen eines Variablenwerts*/
    public void Set_String_Variable (int id, String name, String value)
    {
        Set_Variable (id,name,value,VAR_TYPE_STRING);
    }                

    /**Set_AdjVector_Variable: Setzen eines Adj-Vector Variablenwerts*/
    public void Set_AdjVector_Variable (int id, String name, Adj_Vector value)
    {
        Set_Variable (id,name,value,VAR_TYPE_ADJ_VECTOR);        
    }                

    /**Set_IdVector_Variable: Setzen eines Id-Vector Variablenwerts*/
    public void Set_IdVector_Variable (int id, String name, Id_Vector value)
    {
        Set_Variable (id,name,value,VAR_TYPE_ID_VECTOR);        
    }                

    /**Set_WeightVector_Variable: Setzen eines Weight-Vector Variablenwerts*/
    public void Set_WeightVector_Variable (int id, String name, Weight_Vector value)
    {
        Set_Variable (id,name,value,VAR_TYPE_WEIGHT_VECTOR);        
    }                   

    /**Set_AdjMatrix_Variable: Setzen eines Adj_Matrix Variablenwerts*/
    public void Set_AdjMatrix_Variable (int id, String name, Adj_Matrix value)
    {
        Set_Variable (id,name,value,VAR_TYPE_ADJ_MATRIX);        
    }                   

    /**Set_AdjConVector_Variable: Setzen eines Adj_ConVector Variablenwerts*/
    public void Set_AdjConVector_Variable (int id, String name, Adj_ConVector value)
    {
        Set_Variable (id,name,value,VAR_TYPE_ADJ_CON_VECTOR);        
    }                   

    /**Set_PdtVector_Variable: Setzen eines Pdt_Manager Variablenwerts*/
    public void Set_PdtVector_Variable (int id, String name, Pdt_Manager value)
    {
        Set_Variable (id,name,value,VAR_TYPE_PDT_VECTOR);        
    }                   

    /**Set_PdtElement_Variable: Setzen eines Pdt_Element Variablenwerts*/
    public void Set_PdtElement_Variable (int id, String name, Pdt_Element value)
    {
        Set_Variable (id,name,value,VAR_TYPE_PDT_ELEMENT);        
    }                   
}
