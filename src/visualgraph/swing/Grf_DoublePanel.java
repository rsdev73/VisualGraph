package visualgraph.swing;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Rectangle;

/** Grf_DoublePanel: Klasse f�r ein Double-Buffered Panel */
public class Grf_DoublePanel extends Panel {
    /** Offscreen-Buffer */
    private transient Image dbl_buff;

    static final long serialVersionUID = 7956694014609374204L;

    /** Konstruktor */
    public Grf_DoublePanel() {
	super(null);
    }

    @Override
    public void update(Graphics g) {
	paint(g);
    }

    @Override
    public void repaint() {
	paint(getGraphics());
    }

    public Graphics getDblGraphics() {
	return (dbl_buff.getGraphics());
    }

    @Override
    public void paint(Graphics g) {
	boolean drawn = false;

	if (g == null)
	    return;

	if (isShowing()) {
	    int ncomponents = getComponentCount();

	    /** Clip-Rect ermitteln */
	    Rectangle clip = g.getClipBounds();

	    /** Buffer bereitstellen */
	    if (dbl_buff == null) {
		/** neu erzeugen */
		dbl_buff = createImage(getSize().width, getSize().height);
	    } else {
		if ((dbl_buff.getHeight(this) != getSize().height) || (dbl_buff.getWidth(this) != getSize().width)) {
		    /** Buffer alt l�schen */
		    dbl_buff = null;

		    /** und neu erzeugen */
		    dbl_buff = createImage(getSize().width, getSize().height);
		}
	    }

	    if (dbl_buff == null)
		return;

	    Graphics dg = dbl_buff.getGraphics();

	    /** Dbl-Buffer Clip-setzen und l�schen */
	    if (clip != null) {
		dg.setClip(clip);
		dg.clearRect(clip.x, clip.y, clip.width, clip.height);
	    } else
		dg.clearRect(0, 0, getSize().width, getSize().height);

	    Component[] comp_vec = getComponents();

	    for (int i = ncomponents - 1; i >= 0; i--) {
		Component comp = comp_vec[i];

		if (comp != null &&
		// comp.getPeer() instanceof
		// java.awt.peer.LightweightPeer &&
			comp.isVisible() == true) {
		    Rectangle cr = comp.getBounds();

		    if ((clip == null) || cr.intersects(clip)) {
			comp.paint(g);

			drawn = true;
		    }
		}
	    }

	    if (drawn)
		g.drawImage(dbl_buff, 0, 0, this);
	}
    }
}
