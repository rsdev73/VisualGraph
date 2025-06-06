package visualgraph.util;
import java.util.*;
import java.awt.*;

/**Uti_MultiLabel: Mehrzeilen-Label*/
public class Uti_MultiLabel extends java.awt.Component
{
    /**Alginment u. Margin Finals    */
    public static final int LEFT = 0, CENTER = 1, RIGHT =2;
    protected static final int margin = 2;

    protected String label;
    protected int num_lines;
    protected String[] lines;
    protected int[] line_widths;
    protected int max_width;
    protected int line_height;
    protected int line_ascent;
    
    protected int alignment;
    protected boolean transparent_bckg;
    protected boolean framed;
    
    protected boolean measured = false;
    
    static final long serialVersionUID = 3338733320112654526L;
    
    /**Konstruktoren*/
    public Uti_MultiLabel (String label, int alignment)
    {
        super.setForeground(Color.black);
        super.setBackground(Color.white);

        this.label = label;
        this.alignment = alignment;

        transparent_bckg = false;
        framed = false;
        
        super.setFont(new Font("SansSerif",Font.PLAIN,11));        
        
        newLabel();            
        
        measure();                
    }

    public Uti_MultiLabel (String label)
    {
        this(label,LEFT);
    }

    public Uti_MultiLabel ()
    {
        this("",LEFT);
    }
    
    /**setLabel: Label neu setzen*/
    public void setLabel(String label)
    {
        this.label = label;        
        
        newLabel();        
        
        measure();
        
        repaint();
    }
    
    /**setText: s. setLabel*/
    public void setText(String label)
    {
        setLabel(label);
    }    

    public void setFont(Font f)
    {
        super.setFont(f);

        measure();

        repaint();
    }

    public void setAlignment(int a)
    {
        alignment = a;
                
        repaint();
    }

    /**getLabel: Label-Text zur�ckgeben*/
    public String getLabel()
    {
        String txt="";
        
        for(int i=0;i<num_lines;i++)
        {
            if(i != (num_lines-1))
                txt = txt + lines[i]+"\n";
            else    
                txt = txt + lines[i];            
        }
        
        return(txt);
    }

    /**getText: s. getLabel*/
    public String getText()
    {
        return(getLabel());
    }

    /**override*/
    public Dimension getPreferredSize()
    {
        if(!measured) 
            measure();

        return(new Dimension(max_width+2*margin,
                num_lines*line_height+2*margin));
    }
    
    /**override*/
    public Dimension getMinimumSize()
    {
        return(getPreferredSize());
    }

    protected synchronized void newLabel()
    {
        StringTokenizer t = new StringTokenizer(label,"\n");

        num_lines = t.countTokens();
        
        lines = new String[num_lines];
        line_widths = new int[num_lines];

        for(int i=0;i<num_lines;i++)
            lines[i] = t.nextToken();
            
        repaint();    
    }

    protected synchronized void measure()
    {
        FontMetrics fm;
        
        if(getFont() == null)
            return;
            
        fm = getFontMetrics(getFont());
            
        line_height = fm.getHeight();
        line_ascent = fm.getAscent();
        
        max_width = 0;

        for(int i=0;i<num_lines;i++)
        {
            line_widths[i] = fm.stringWidth(lines[i]);
        
            if(line_widths[i] > max_width)
                max_width = line_widths[i];
        }

        if( (getSize().width != max_width+2*margin) ||
          (getSize().height != num_lines*line_height+2*margin) )
        {          
            setSize(max_width+2*margin,
                num_lines*line_height+2*margin);
        }
        
        measured = true;
    }
        
    /**delimitWidth: Begrenzung der Zeilenl�nge auf Anzahl Zeichen*/
    public void delimitWidth(int anz_chars)
    {
        boolean found;
        
        found = false;

        for(int i=1;i<label.length();i++)
        {
            if( (i%anz_chars) == 0)
            {
                label = label.substring(0,i-1)+"\n"+
                        label.substring(i-1,label.length());                    
                        
                found = true;                        
            }
        }

        if(found)
        {
            newLabel();        
            
            measure();
            
            repaint();            
       }
    }
    
    /**GUI-override: paint    */
    public void paint(Graphics g)
    {
        int x,y;
        Dimension size = this.getSize();

        if(!measured)
            measure();

        g.setFont(getFont());
        
        if(transparent_bckg == false)
        {            
            g.clearRect(0,0,size.width,size.height);

            g.setColor(getBackground());
            g.fillRect(0,0,size.width-1,size.height-1);
        }
        
        if(framed == true)        
        {
            g.setColor(Color.black);            
            g.drawRect(0,0,size.width-1,size.height-1);
        }
        
        g.setColor(getForeground());            
        
        y = line_ascent+(size.height - num_lines*line_height)/2;
        
        for(int i=0;i<num_lines;i++,y+=line_height)
        {
            switch(alignment)
            {
                case LEFT:
                default:
                    x = margin;
                    break;
                case CENTER:
                    x = (size.width-line_widths[i])/2;
                    break;
                case RIGHT:
                    x = size.width - margin - line_widths[i];
                    break;
            }
            if(lines[i] != null)
            {
            	g.drawString(lines[i],x,y);
            }
        }
    }    
}
