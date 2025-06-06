package visualgraph.swing;
import java.awt.*;
import visualgraph.util.Uti_MultiLabel;

/**Klasse Grf_Label: Double-Buffered static Label*/
public class Grf_Label extends Uti_MultiLabel
{   
    /**Verweis auf Graph-Manager*/
    private Frm_Frame frm;
    
    static final long serialVersionUID = -2403105812118952049L;
    
    /**Konstruktoren*/
    public Grf_Label(Frm_Frame frm, String text)
    {
        this(frm,text,Uti_MultiLabel.LEFT,true,false);
    }
    
    public Grf_Label(Frm_Frame frm, String text, int alignment)
    {        
        this(frm,text,alignment,true,false);                
	}    

    public Grf_Label(Frm_Frame frm, String text, int alignment, 
                        boolean transparent_bckg, boolean framed)
    {        
        super(text,alignment);        
        
        /**Frame einh�ngen*/
        this.frm = frm;
        this.transparent_bckg = transparent_bckg;
        this.framed = framed;
	}    
             
    /**GUI-override: paint*/
    public void paint(Graphics g)
    {
        Rectangle bnds;
 
        bnds = getBounds();
        
        /**Drawing in Buffer bereitstellen*/
        g = frm.Get_DrawGraphics().create(bnds.x,bnds.y, bnds.width, bnds.height);        
               
        super.paint(g);
    }
}
