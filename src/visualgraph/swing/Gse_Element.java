package visualgraph.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.io.Serializable;

import visualgraph.core.Pdt_Element;

/** GUI-Klasse f�r Atomar-Datentypen in Sequenz-Darstellung */
public class Gse_Element extends Component implements Serializable {

	private static final long serialVersionUID = -413645301503145651L;

	/** Initialgr��en */
	private static final int font_size = 9;

	private static final Dimension elem_dimensions[] = { new Dimension(20, 20), new Dimension(20, 20),
			new Dimension(60, 20) };

	/** interaktive Editier-Modi */
	private static final int EDIT_NONE = 0;
	private static final int EDIT_ELEMENT = 1;

	/** Verweis auf Sequence-Mgmt. */
	private Gse_Manager gse_mgr;

	/** Verweis auf Pdt_Element */
	private Pdt_Element pdt_elem;

	/** Locking */
	private boolean is_locked;

	/** Eingabe-TextArea */
	private TextArea w_text;

	/** aktueller Editier-Modus */
	private int edit_mode;

	/** Popup */
	private PopupMenu gse_popup;

	/** Action-Listener-Objects */
	private Grf_ButtonAction gse_button_action;

	/** Highlitening */
	private transient boolean highlite;
	private transient Color fill_color;
	private transient Color text_color;

	private int node_typ;

	/** Serialize write */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
	}

	/** Serialize read */
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
	}

	/** PrepareSerializedObject: Serialisierungsverarbeitung */
	public void PrepareSerializedObject(int prepare_mode) {
		switch (prepare_mode) {
		case Frs_Manager.SER_BEFORE_WRITE:
			remove(gse_popup);

			/** Property-Dialog noch ge�ffnet -> schlie�en */
			// if(prop != null)
			// {
			// prop.Remove();
			// }
			break;

		case Frs_Manager.SER_AFTER_WRITE:
			add(gse_popup);
			break;

		case Frs_Manager.SER_AFTER_READ:
			add(gse_popup);

			/** neue Listeners hinzuf�gen */
			addMouseListener(new Gse_ElementMouse());
			addMouseMotionListener(new Gse_ElementMouseMotion());
			break;
		}
	}

	/** Konstruktor */
	public Gse_Element(Gse_Manager gse_mgr, Pdt_Element pdt_elem) {
		MenuItem mi;

		/** Verweis auf Seq.-Mgmt setzen */
		this.gse_mgr = gse_mgr;

		/** Verweis auf Atomartyp setzen */
		this.pdt_elem = pdt_elem;

		/** Initial-Size setzen */
		setSize(InitialDimension());

		/** Font-Setzen */
		setFont(InitialFont());

		/** Event-Listeners erzeugen */
		gse_button_action = new Grf_ButtonAction();

		addMouseListener(new Gse_ElementMouse());
		addMouseMotionListener(new Gse_ElementMouseMotion());

		/** Einagbe-TextArea erzeugen */
		edit_mode = EDIT_NONE;

		/** Popup-Menu einrichten */
		gse_popup = new PopupMenu();

		mi = new MenuItem("Delete Item");
		mi.addActionListener(gse_button_action);
		mi.setActionCommand("DEL_ITEM");
		gse_popup.add(mi);

		add(gse_popup);
	}

	/** Zugriffsfunktionen */

	/** Set_Lock: Locking setzen */
	public void Set_Lock(boolean lock) {
		is_locked = lock;
	}

	/** SetMarker: Kennzeichnet Element als markiert */
	public void SetMarker() {
		pdt_elem.SetMarker();

		/** Element neu zeichnen */
		repaint();
	}

	/** DeleteMarker: Kennzeichnet Element als nicht markiert */
	public void DeleteMarker() {
		pdt_elem.DeleteMarker();

		/** Element neu zeichnen */
		repaint();
	}

	/** IsMarked: */
	public boolean IsMarked() {
		return (pdt_elem.IsMarked());
	}

	/** Mouse-Motion-Listener-Adapter */
	class Gse_ElementMouseMotion extends MouseMotionAdapter {
		@Override
		public void mouseDragged(MouseEvent e) {
			Handle_MouseDragged(e);
		}
	}

	public void Handle_MouseDragged(MouseEvent e) {
		Gse_Element gse;
		int xm, ym;
		boolean shift_pressed;
		Point grid_loc, new_loc;

		gse = (Gse_Element) e.getSource();

		shift_pressed = (e.getModifiers() & InputEvent.SHIFT_MASK) != 0 ? true : false;

		if (!shift_pressed) {
			if (is_locked == true)
				return;
		}

		if (shift_pressed) {
			xm = gse.getSize().width / 2;
			ym = gse.getSize().height / 2;

			// Grid-Alignment pr�fen
			if (!gse_mgr.Get_Align_Grid()) {
				if ((gse.getLocation().x + e.getX() - xm >= 0) && (gse.getLocation().y + e.getY() - ym >= 0)) {
					/** Element-Move relativ zu Dragging verschieben */
					gse.setLocation(gse.getLocation().x + e.getX() - xm, gse.getLocation().y + e.getY() - ym);

					/** Element-Move verarbeiten, ScrollPane-Size evtl. �ndern */
					// TODO: gse_mgr.HandleElementMotion(gse);
				}
			} else {
				if ((gse.getLocation().x + e.getX() >= 0) && (gse.getLocation().y + e.getY() >= 0)) {
					// Grid-Snapping
					grid_loc = gse_mgr.KoordToGrid(gse.getLocation().x + e.getX(), gse.getLocation().y + e.getY(), 0,
							0);

					new_loc = gse_mgr.GridToKoord(grid_loc.x, grid_loc.y, 0, 0);

					gse.setLocation(new_loc.x, new_loc.y);

					/** Element-Move verarbeiten, ScrollPane-Size evtl. �ndern */
					// TODO: gse_mgr.HandleElementMotion(gse);
				}
			}
		}
	}

	/** Mouse-Listener-Adapter */
	class Gse_ElementMouse extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			Handle_MouseClicked(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			Handle_MouseReleased(e);
		}
	}

	public void Handle_MouseClicked(MouseEvent e) {
		Gse_Element gse;
		Rectangle rect, vgl_rect;
		boolean right_pressed;
		if (is_locked == true)
			return;

		right_pressed = (e.getModifiersEx() & InputEvent.META_MASK) != 0 ? true : false;

		gse = (Gse_Element) e.getSource();

		if (!right_pressed) {
			/** Evtl. Eingabe-Field anzeigen */
			if (e.getClickCount() == 2) {
				/** Eingabe-Text-Field erzeugen und positionieren */
				w_text = new TextArea("", 1, 1, TextArea.SCROLLBARS_NONE);
				w_text.setFont(getFont());
				w_text.setVisible(false);

				w_text.addKeyListener(new Grf_TextAreaKeyAdapter());
				w_text.addFocusListener(new Grf_TextAreaFocusAdapter());

				/** ...und anzeigen */
				gse_mgr.Get_Container().add(w_text);

				/*
				 * switch(node_typ) { case SIMPLE_GRAPH: case EVENT_GRAPH:
				 * 
				 * edit_mode = EDIT_NUMBER;
				 * 
				 * w_text.setSize(getSize().width-2,getSize().height-2);
				 * w_text.setLocation(getLocation().x+1,getLocation().y+1);
				 * 
				 * switch(gse_mgr.Get_Element_Ident()) { case NODE_IDENT_NUMERIC:
				 * w_text.setText(""+Get_Element_Number()); break;
				 * 
				 * case NODE_IDENT_ALPHA: w_text.setText(Get_Element_Alpha_Number()); break;
				 * 
				 * default: break; }
				 * 
				 * w_text.setVisible(true);
				 * 
				 * /**Focus-request
				 */
				/*
				 * 
				 * w_text.requestFocus(); w_text.selectAll(); break;
				 * 
				 * case TODO_GRAPH:
				 * 
				 * /**Pr�fung auf Nummer
				 */
				/*
				 * vgl_rect = new Rectangle(1,1,3*getSize().width/8-1,getSize().height/5-1);
				 * 
				 * if(vgl_rect.contains(e.getX(),e.getY())) { edit_mode = EDIT_NUMBER;
				 * 
				 * vgl_rect.setLocation(1+getLocation().x,1+getLocation().y);
				 * w_text.setBounds(vgl_rect);
				 * 
				 * switch(gse_mgr.Get_Element_Ident()) { case NODE_IDENT_NUMERIC:
				 * w_text.setText(""+Get_Element_Number()); break;
				 * 
				 * case NODE_IDENT_ALPHA: w_text.setText(Get_Element_Alpha_Number()); break;
				 * 
				 * default: break; }
				 * 
				 * w_text.setVisible(true);
				 * 
				 * /**Focus-request
				 */
				/*
				 * w_text.requestFocus(); w_text.selectAll(); }
				 * 
				 * /**Pr�fung auf Gewichtung
				 */
				/*
				 * vgl_rect = null;
				 * 
				 * vgl_rect = new
				 * Rectangle(1,4*getSize().height/5+1,3*getSize().width/8-1,getSize().height/5-2
				 * );
				 * 
				 * if(vgl_rect.contains(e.getX(),e.getY())) { edit_mode = EDIT_WEIGHT;
				 * 
				 * vgl_rect.setLocation(1+getLocation().x,getLocation().y+4*getSize().height/5+1
				 * ); w_text.setBounds(vgl_rect);
				 * 
				 * dbl = Double.valueOf(Get_Element_Weight());
				 * 
				 * w_text.setText(dbl.toString());
				 * 
				 * w_text.setVisible(true);
				 * 
				 * /**Focus-request
				 */
				/*
				 * w_text.requestFocus(); w_text.selectAll(); }
				 * 
				 * /**Pr�fung auf Bezeichnung
				 */
				/*
				 * vgl_rect = null;
				 * 
				 * vgl_rect = new
				 * Rectangle(1,getSize().height/5,gse.getSize().width-2,3*gse.getSize().height/5
				 * );
				 * 
				 * if(vgl_rect.contains(e.getX(),e.getY())) { edit_mode = EDIT_CAPTION;
				 * 
				 * vgl_rect.setLocation(1+getLocation().x,getLocation().y+getSize().height/5);
				 * w_text.setBounds(vgl_rect);
				 * 
				 * w_text.setText(Get_Element_Caption());
				 * 
				 * w_text.setVisible(true);
				 * 
				 * /**Focus-request
				 */
				/*
				 * w_text.requestFocus(); w_text.selectAll(); } break;
				 * 
				 * default: edit_mode = EDIT_NONE; }
				 */
			}
		}

		/** Popup-Menu darstellen */
		if (right_pressed) {
			gse_popup.show(gse, e.getX(), e.getY());
		}
	}

	public void Handle_MouseReleased(MouseEvent e) {
		if (is_locked == true)
			return;

//		(Gse_Element) e.getSource();

		/** Element neu zeichnen */
		repaint();
	}

	/** Grf_ButtonAction: PopupMenu Action-Verabeitung */
	class Grf_ButtonAction implements java.awt.event.ActionListener, Serializable {
		private static final long serialVersionUID = 4428231363527893454L;

		@Override
		public void actionPerformed(java.awt.event.ActionEvent event) {
			Handle_ActionPerformed(event);
		}

		private void writeObject(java.io.ObjectOutputStream out) throws IOException {
			out.defaultWriteObject();
		}

		private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
			in.defaultReadObject();
		}
	}

	public void Handle_ActionPerformed(ActionEvent event) {
		String command;
		Object object = event.getSource();

		if (is_locked == true)
			return;

		command = event.getActionCommand();

		/** Element l�schen */
		if (command.equals("DEL_ITEM")) {
			// TODO: gse_mgr.DeleteElement();
		}
	}

	/** KeyEvent-Handling TextArea */
	class Grf_TextAreaKeyAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			Handle_TextAreaKeyEvent(e);
		}
	}

	public void Handle_TextAreaKeyEvent(KeyEvent e) {
//		(TextArea) e.getSource();

		/*
		 * switch(edit_mode) { case EDIT_NUMBER: case EDIT_WEIGHT:
		 * 
		 * if( (e.getKeyCode() == KeyEvent.VK_TAB) || (e.getKeyCode() ==
		 * KeyEvent.VK_ESCAPE) || (e.getKeyCode() == KeyEvent.VK_ENTER) ) dispatch =
		 * true;
		 * 
		 * break;
		 * 
		 * case EDIT_CAPTION:
		 * 
		 * if( (e.getKeyCode() == KeyEvent.VK_TAB) || (e.getKeyCode() ==
		 * KeyEvent.VK_ESCAPE) ) dispatch = true;
		 * 
		 * break;
		 * 
		 * default: dispatch = true; break; }
		 * 
		 * if(dispatch) act_text.dispatchEvent(new
		 * FocusEvent(act_text,FocusEvent.FOCUS_LOST));
		 */
		gse_mgr.Set_Edited();
	}

	/** FocusEvent-Handling TextArea */
	class Grf_TextAreaFocusAdapter extends FocusAdapter {
		@Override
		public void focusLost(FocusEvent e) {
			Handle_TextAreaFocusLost(e);
		}
	}

	public void Handle_TextAreaFocusLost(FocusEvent e) {
//		(TextArea) e.getSource();

		/*
		 * switch(edit_mode) { case EDIT_NUMBER:
		 * 
		 * /**neusetzen der Element-Nummern
		 */
		/*
		 * gse_mgr.RenumberElements (this,act_text.getText()); break;
		 * 
		 * case EDIT_WEIGHT:
		 * 
		 * try { d_val = Double.valueOf(act_text.getText()).doubleValue();
		 * 
		 * /**neusetzen der Gewichtung
		 */
		/*
		 * Set_Element_Weight(d_val); } catch(NumberFormatException exc) {}; break;
		 * 
		 * case EDIT_CAPTION:
		 * 
		 * Set_Element_Caption(act_text.getText()); break;
		 * 
		 * default: break; }
		 */
		edit_mode = EDIT_NONE;

		/** TextArea entfernen */
		gse_mgr.Get_Container().remove(w_text);

		/** und neu zeichnen */
		repaint();
	}

	/** InitialDimension: Initialisierungsgr��e des Element bereitstellen */
	private Dimension InitialDimension() {
		Dimension new_dim = new Dimension();

		new_dim.width = (int) (Grf_Node.node_type_dimensions[node_typ].width * gse_mgr.GetZoom());
		new_dim.height = (int) (Grf_Node.node_type_dimensions[node_typ].height * gse_mgr.GetZoom());

		return (new_dim);
	}

	/** InitialFont: Initialisierungsgr��e Fonts zur�ckgeben */
	private Font InitialFont() {
		return (new Font("SansSerif", Font.PLAIN, (int) (Grf_Node.node_type_font_size[node_typ] * gse_mgr.GetZoom())));
	}

	/** InitialFont: Initialisierungsgr��en aktualisieren */
	public void UpdateDimension() {
		/** Location aktualisieren */
		setLocation((int) (getLocation().x * (1 / gse_mgr.GetLastZoom()) * gse_mgr.GetZoom()),
				(int) (getLocation().y * (1 / gse_mgr.GetLastZoom()) * gse_mgr.GetZoom()));

		/** Initial-Size setzen */
		setSize(InitialDimension());

		/** Font-Setzen */
		setFont(InitialFont());
	}

	/** ChangeElementTyp: Elementtyp ver�ndern */
	public void ChangeElementTyp(int new_type) {
		/** Noch im Edit-Modus ? dann raus */
		if ((edit_mode != EDIT_NONE) && (w_text != null))
			w_text.dispatchEvent(new FocusEvent(w_text, FocusEvent.FOCUS_LOST));

		/** Element-Typ setzen */
		node_typ = new_type;

		/** Font-Setzen */
		setFont(InitialFont());

		/** neue Gr��e setzen */
		setSize(InitialDimension());

		/** neu zeichnen */
		repaint();
	}

	/** HighliteElement */
	public void HighliteElement(Color fill_color, Color text_color) {
		highlite = true;

		if (fill_color != null)
			this.fill_color = fill_color;
		else
			this.fill_color = Color.white;

		if (text_color != null)
			this.text_color = text_color;
		else
			this.text_color = Color.black;

		repaint();
	}

	/** UnHighliteElement */
	public void UnHighliteElement() {
		highlite = false;

		repaint();
	}

	/** PaintElementInt: zeichnen des Element */
	private void PaintElementInt(Graphics g) {
		int i, row, anz_rows, t_width, t_height, i_weight;
		Rectangle rect;
		String s = "", out_s;
		FontMetrics fm;
		double f_weight;
		Color txt_color;

		rect = getBounds();
		g.clearRect(0, 0, rect.width - 1, rect.height - 1);
		g.setFont(getFont());

    	fm = getFontMetrics(getFont());
        //fm = getToolkit().getFontMetrics(getFont());

		/** Highlite �berpr�fen */
		if (highlite)
			txt_color = text_color;
		else
			txt_color = Color.black;

		switch (node_typ) {
		case Grf_Node.SIMPLE_GRAPH:

			/** Highlite �berpr�fen */
			if (highlite) {
				g.setColor(fill_color);
				g.fillOval(0, 0, rect.width - 1, rect.height - 1);
			}

			g.setColor(Color.black);
			g.drawOval(0, 0, rect.width - 1, rect.height - 1);

//			switch (gse_mgr.Get_Element_Ident()) {
//			case Grf_Node.NODE_IDENT_NUMERIC:
//				s = "" + pdt_elem.Get_Element_Number();
//				break;
//
//			case Grf_Node.NODE_IDENT_ALPHA:
//				//s = pdt_elem.Get_Element_Alpha_Number();
//				s = "" + pdt_elem.Get_Element_Number();
//				break;
//
//			default:
//				s = "";
//			}

			t_width = fm.stringWidth(s);
			t_height = fm.getAscent();

			g.setColor(txt_color);
			g.drawString(s, rect.width / 2 - t_width / 2, rect.height / 2 + t_height / 2 - 1);

			break;

		case Grf_Node.EVENT_GRAPH:

			g.clearRect(0, 0, rect.width - 1, rect.height - 1);

			/** Highlite �berpr�fen */
			if (highlite) {
				g.setColor(fill_color);
				g.fillOval(0, 0, rect.width - 1, rect.height - 1);
			}

			g.setColor(Color.black);
			g.drawOval(0, 0, rect.width - 1, rect.height - 1);

//			switch (gse_mgr.Get_Element_Ident()) {
//			case Grf_Node.NODE_IDENT_NUMERIC:
//				s = "" + pdt_elem.Get_Element_Number();
//				break;
//
//			case Grf_Node.NODE_IDENT_ALPHA:
//				//s = Get_Element_Alpha_Number();
//				s = "" + pdt_elem.Get_Element_Number();
//				break;
//
//			default:
//				s = "";
//			}

			t_width = fm.stringWidth(s);
			t_height = fm.getAscent();

			g.setColor(txt_color);
			g.drawString(s, rect.width / 2 - t_width / 2, rect.height / 2 + t_height / 2 - 1);

			break;

		case Grf_Node.TODO_GRAPH:

			/** Highlite �berpr�fen */
			if (highlite) {
				g.setColor(fill_color);
				g.fillRect(0, 0, rect.width - 1, rect.height - 1);
			}

			g.setColor(Color.black);
			g.drawRect(0, 0, rect.width - 1, rect.height - 1);

			/** unterteilen */
			g.drawLine(0, rect.height / 5, rect.width - 1, rect.height / 5);
			g.drawLine(0, 4 * rect.height / 5, rect.width - 1, 4 * rect.height / 5);

			g.drawLine(3 * rect.width / 8, 0, 3 * rect.width / 8, rect.height / 5);
			g.drawLine(3 * rect.width / 8, 4 * rect.height / 5, 3 * rect.width / 8, rect.height);

			/** Elementnummer zeichnen */
//			switch (gse_mgr.Get_Element_Ident()) {
//			case Grf_Node.NODE_IDENT_NUMERIC:
//				s = "" + pdt_elem.Get_Element_Number();
//				break;
//
//			case Grf_Node.NODE_IDENT_ALPHA:
//				//s = Get_Element_Alpha_Number();
//				s = "" + pdt_elem.Get_Element_Number();
//				break;
//
//			default:
//				s = "";
//			}

			t_width = fm.stringWidth(s);
			t_height = fm.getAscent();

			g.setColor(txt_color);
			g.drawString(s, (3 * rect.width / 8) / 2 - t_width / 2, (rect.height / 5) / 2 + t_height / 2 - 1);

			/** Elementgewicht zeichnen */
			f_weight = 0; //Get_Element_Weight();
			i_weight = (int) f_weight;

			/** Nachkommastellen vorhanden ? */
			if ((i_weight) == f_weight)
				s = "" + i_weight;
			else
				s = "" + f_weight;

			t_width = fm.stringWidth(s);
			t_height = fm.getAscent();

			g.setColor(txt_color);
			g.drawString(s, (3 * rect.width / 8) / 2 - t_width / 2,
					4 * rect.height / 5 + (rect.height / 5) / 2 + t_height / 2 - 1);

			/** Bezeichnung ausgeben */
//			s = Get_Element_Caption();
			s = s.replace('\n', ' ');

			if (!s.equals("")) {
				t_width = fm.stringWidth(s);
				t_height = fm.getAscent();

				/** Zeilenumbruch berechnen */
				if (t_width > (rect.width - 4)) {
					anz_rows = t_width / (rect.width - 4) + 1;

					if (anz_rows >= 4)
						anz_rows = 3;

					i = 0;

					for (row = 0; row < anz_rows; row++) {
						out_s = "";

						for (; i < s.length(); i++) {
							out_s = out_s + s.charAt(i);

							t_width = fm.stringWidth(out_s);

							if ((t_width >= (rect.width - 10)) || ((i + 1) == s.length())) {
								g.setColor(txt_color);
								g.drawString(out_s, rect.width / 2 - t_width / 2,
										(row + 1) * (rect.height / 5) + t_height + 1);

								i++;
								break;
							}
						}
					}
				} else {
					g.setColor(txt_color);
					g.drawString(s, rect.width / 2 - t_width / 2, rect.height / 2 + t_height / 2 - 1);
				}
			}
			break;

		default:
			break;
		}

		/** Marker �berpr�fen */
		if (pdt_elem.IsMarked() == true) {
			s = "*";
			t_width = fm.stringWidth(s);
			t_height = fm.getAscent();

			g.setColor(Color.blue);
			g.fillRect(1, 1, t_width + 1, t_height - 1);

			g.setColor(Color.white);
			g.drawString(s, 1, t_height);
		}
	}

	/** GUI-Override Paint */
	@Override
	public void paint(Graphics g) {
		Rectangle bnds;

		/** aktuelle Gr��e bereitstellen */
		bnds = getBounds();
		//getToolkit().getFontMetrics(getFont());

		/** Drawing in Buffer bereitstellen */
		g = gse_mgr.Get_DrawGraphics().create(bnds.x, bnds.y, bnds.width, bnds.height);

		/** interne Zeichenroutine ansto�en */
		PaintElementInt(g);

		/** Paint-Overridable */
		switch (node_typ) {
		case Grf_Node.SIMPLE_GRAPH:
			break;

		case Grf_Node.EVENT_GRAPH:
			break;

		case Grf_Node.TODO_GRAPH:
			break;
		}
	}
}