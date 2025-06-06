package visualgraph.util;
import java.awt.*;
import java.awt.image.*;
import java.net.*;
import java.nio.file.Paths;

public class Uti_PictureButton extends java.awt.Button
{
    /**Objektvariablen*/
    private transient Image img; 
    private transient Image dis_img; 

    private String img_file_name;
    
    private MediaTracker mtrack;
    
    static final long serialVersionUID = -792898177870399724L;
    
    /**Konstruktoren*/
    public Uti_PictureButton (String img_file, String label)
    {
        super("");
        
        img_file_name="";
        
        LoadImage(img_file,label);
    }

    public void ChangeImage (String img_file, String label)
    {
        if( (img_file.equals(img_file_name)) || (getLabel().equals(label)) )
            return;

        /**altes Image l�schen*/

        img=null;         
        dis_img=null; 
        img_file_name="";
        mtrack=null;

        LoadImage(img_file,label);

        repaint();
    }

    /**LoadImage: l�dt das Image*/
    private void LoadImage (String img_file, String label)
    {
        URL im_url;
        Toolkit tk;
        
        img_file_name = img_file;
        
        try
        {
            /**URL erzeugen*/
    		try
    		{
                im_url = Paths.get(img_file).toUri().toURL();        
    	    }
    	    catch (MalformedURLException e) 
    	    { 
                setLabel(label);                
                e.printStackTrace();
    	        return;
    	    }
                                
            /**neuen Mediatracker erzeugen*/
            mtrack = new MediaTracker(this);

//            if(Apl_Context.Get_Loader_Applet() == null)
//            {
//                /**local laden*/
                tk = getToolkit();

                img = tk.getImage(im_url);            
                if(img == null)
                {                
                    setLabel(label);                
                    return;
                }    
//            }
//            else
//            {
                /**net load*/
//                img = Apl_Context.Get_Loader_Applet().getImage(im_url);            
//                if(img == null)
//                {
//                    setLabel(label);                
//                    
//           	        System.err.println("IMG-Loading: null!");
//                    return;
//                }        
//            }
            
            /**Img-Registrierung*/
            mtrack.addImage(img,1);
            
            /**..Laden*/
            try
            {
                mtrack.waitForAll();
            }
            catch (InterruptedException e) {;}                               
            
            /**Disable-Image erzeugen*/
            dis_img	= createImage(new FilteredImageSource(img.getSource(), 
                         new Uti_FadingFilter()));                     
        }
        catch(SecurityException sec)
        {
            System.err.println("SecurityException (Uti_PictureButton): Img-loading");
            setLabel(label);
        } 
    }
    
    /**Update: L�schen der Zeichenfl�che verhindern und neu zeichnen*/
    public void update(Graphics g)
    {
        paint(g);
    }
    
    /**paint: Zeichnen des Images*/
    public void paint(Graphics g)
    {
        Dimension dim,im_dim;
        Point loc;
        Image out_img;
        
        if(img == null)
            return;
            
        /**Lade-Fehler pr�fen*/
        if(mtrack.isErrorID(1))
            return;
            
       /**Bild anzeigen -> zentrieren*/
       dim = getSize();

       /**Enabling pr�fen */
       if(isEnabled() == true)
           out_img = img;
       else
            out_img = dis_img;

       im_dim = new Dimension(out_img.getWidth(this),out_img.getHeight(this));     
       loc = new Point(0,0);
               
       /**Image evtl. skalieren*/
       if( (im_dim.width > dim.width) || (im_dim.height > dim.height) )
       {
            g.drawImage(out_img,2,2,dim.width-2,dim.height-2,0,0,im_dim.width,im_dim.height, 
                        Color.lightGray,this);
       }
       else
       {
           loc.x = (int) (dim.width-im_dim.width)/2;
           loc.y = (int) (dim.height-im_dim.height)/2;
                  
           g.drawImage(out_img,loc.x,loc.y,im_dim.width,im_dim.height,Color.lightGray,this);
       }        
       
       loc = null;
       im_dim = null;       
    } 
}
