package visualgraph.swing;

import java.awt.*;
import java.util.Vector;
import java.awt.event.*;
import java.lang.reflect.*;
import java.io.*;
import java.net.*;
import java.util.zip.*;

import visualgraph.algorithms.Base_Algorithm;
import visualgraph.util.Uti_PictureButton;

/** Project-Set Manager */
public class Frs_Manager extends java.awt.Panel implements FilenameFilter {
	/** Serialize-Modes */
	public static final int SER_BEFORE_WRITE = 0;
	public static final int SER_AFTER_WRITE = 1;
	public static final int SER_AFTER_READ = 2;

	private static final String file_extension = ".vgp";

	/** Konstanten */
	private static final int frs_height = 22;
	private static final int pb_width = 25;
	private static final int pb_scroll_width = 16;
	private static final int scroll_step = 50;
	private static final int pb_font_size = 9;

	/** �bergeordnetes Container-Panel */
	private Panel frs_top_panel;

	/** Project-Manager Panel */
	private Panel frm_mgr_panel;

	/** Project Scroller */
	private ScrollPane frs_scroll;
	private Panel frs_scroll_panel;
	private Uti_PictureButton frs_pb_left;
	private Uti_PictureButton frs_pb_right;

	private Uti_PictureButton frs_pb_new;
	private Uti_PictureButton frs_pb_load;
	private Uti_PictureButton frs_pb_save;

	/** Project-List */
	private Vector frs_frame_mgrs;

	/** Algorithmen-Registratur */
	private Vector frs_algorithms;
	private Vector frs_algorithm_names;
	private Vector frs_algorithm_doks;

	/** Button-List */
	private Vector frs_pbs;

	/** Aktuell sichtbare Frames */
	private Vector frs_act_vis;

	/** Unknown Count */
	private static int unknown_cnt;

	/** Popup */
	private PopupMenu frs_popup;

	/** Action-Listener-Object */
	Frs_ButtonActionAdapter frs_button_action;
	Frs_MouseEventAdapter frs_mouse_events;

	static final long serialVersionUID = 7014908382384589708L;

	/** Konstruktor */
	public Frs_Manager(Panel top_panel, Panel mgr_panel) {
		super(null);
		Dimension vp_size;
		Font ft;
		MenuItem mi;

		if (top_panel == null)
			return;

		frs_top_panel = top_panel;
		frm_mgr_panel = mgr_panel;

		/** Alogrithmen-Registratur bereitstellen */
		frs_algorithms = new Vector();
		frs_algorithm_names = new Vector();
		frs_algorithm_doks = new Vector();

		/** TopPanel-Resize und Location richtig setzen */
		frs_top_panel.setSize(frm_mgr_panel.getBounds().width, frs_height);
		frs_top_panel.setLocation(frm_mgr_panel.getLocation().x, frs_top_panel.getLocation().y);

		/** Gr��e und Position einstellen */
		setLocation(0, 0);
		setSize(frm_mgr_panel.getBounds().width, frs_height);

		/** Buttons einf�gen */
		ft = new Font("SansSerif", Font.PLAIN, pb_font_size);

		frs_pb_left = new Uti_PictureButton(Apl_Context.Get_Img_Path() + "left.gif", "<");
		frs_pb_left.setLocation(frm_mgr_panel.getBounds().width - 2 * pb_scroll_width, 0);
		frs_pb_left.setSize(pb_scroll_width, frs_height - 1);
		frs_pb_left.setFont(ft);
		this.add(frs_pb_left);
		frs_pb_left.setEnabled(false);

		frs_pb_right = new Uti_PictureButton(Apl_Context.Get_Img_Path() + "right.gif", ">");
		frs_pb_right.setLocation(frm_mgr_panel.getBounds().width - pb_scroll_width, 0);
		frs_pb_right.setSize(pb_scroll_width, frs_height - 1);
		frs_pb_right.setFont(ft);
		this.add(frs_pb_right);
		frs_pb_right.setEnabled(false);

		frs_pb_new = new Uti_PictureButton(Apl_Context.Get_Img_Path() + "new.gif", "New");
		frs_pb_new.setLocation(0, 0);
		frs_pb_new.setSize(pb_width, frs_height - 1);
		frs_pb_new.setFont(ft);
		this.add(frs_pb_new);
		frs_pb_new.setEnabled(true);

		frs_pb_load = new Uti_PictureButton(Apl_Context.Get_Img_Path() + "load.gif", "Load");
		frs_pb_load.setLocation(pb_width, 0);
		frs_pb_load.setSize(pb_width, frs_height - 1);
		frs_pb_load.setFont(ft);
		this.add(frs_pb_load);
		frs_pb_load.setEnabled(true);

		frs_pb_save = new Uti_PictureButton(Apl_Context.Get_Img_Path() + "save.gif", "Save");
		frs_pb_save.setLocation(2 * pb_width, 0);
		frs_pb_save.setSize(pb_width, frs_height - 1);
		frs_pb_save.setFont(ft);
		this.add(frs_pb_save);
		frs_pb_save.setEnabled(false);

		/** Scroller bereitstellen */
		frs_scroll = new ScrollPane(ScrollPane.SCROLLBARS_NEVER);
		frs_scroll.setLocation(3 * pb_width, 0);
		frs_scroll.setSize(frm_mgr_panel.getBounds().width - 3 * pb_width - 2 * pb_scroll_width, frs_height);
		this.add(frs_scroll);

		/** Scroll Panel-Resize */
		frs_scroll_panel = new Panel(null);
		frs_scroll_panel.setSize(frs_scroll.getBounds().width, frs_scroll.getBounds().height);
		frs_scroll.setBackground(Color.lightGray);
		/** .. und einh�ngen */
		frs_scroll.add(frs_scroll_panel);

		/** Project-List bereitstellen */
		frs_frame_mgrs = new Vector();

		/** Button-List bereitstellen */
		frs_pbs = new Vector();

		/** Sichtbare Frames */
		frs_act_vis = new Vector();

		/** Event-Listeners registrieren */
		addComponentListener(new Frs_ComponentEventAdapter());

		frs_button_action = new Frs_ButtonActionAdapter();
		frs_pb_left.addActionListener(frs_button_action);
		frs_pb_right.addActionListener(frs_button_action);
		frs_pb_new.addActionListener(frs_button_action);
		frs_pb_load.addActionListener(frs_button_action);
		frs_pb_save.addActionListener(frs_button_action);

		/** Popup-Menu einrichten */
		frs_popup = new PopupMenu();

		mi = new MenuItem("New Project");
		mi.addActionListener(frs_button_action);
		mi.setActionCommand("NEW_PROJECT");
		frs_popup.add(mi);

		mi = new MenuItem("Load Project");
		mi.addActionListener(frs_button_action);
		mi.setActionCommand("LOAD_PROJECT");
		frs_popup.add(mi);

		mi = new MenuItem("Save Project");
		mi.addActionListener(frs_button_action);
		mi.setActionCommand("SAVE_PROJECT");
		mi.setEnabled(false);
		frs_popup.add(mi);

		mi = new MenuItem("Save As...");
		mi.addActionListener(frs_button_action);
		mi.setActionCommand("SAVE_AS_PROJECT");
		mi.setEnabled(false);
		frs_popup.add(mi);

		mi = new MenuItem("-");
		frs_popup.add(mi);

		mi = new MenuItem("Close Project");
		mi.addActionListener(frs_button_action);
		mi.setActionCommand("CLOSE_PROJECT");
		frs_popup.add(mi);

		/** Popup dem Scroll-Panel hinzuf�gen */

		frs_mouse_events = new Frs_MouseEventAdapter();

		frs_scroll_panel.add(frs_popup);
		frs_scroll_panel.addMouseListener(frs_mouse_events);

		top_panel.add(this);

		LayoutButtonEnabling();
	}

	/** LayoutButtonEnabling: Button-enabling zeichnen */
	public void LayoutButtonEnabling() {
		int i;
		Button act_pb;
		Frm_Manager act_frame_mgr;

		for (i = 0; i < frs_pbs.size(); i++) {
			act_pb = (Button) frs_pbs.elementAt(i);
			act_frame_mgr = (Frm_Manager) frs_frame_mgrs.elementAt(i);

			if (frs_act_vis.contains((Object) act_frame_mgr) == true) {
				act_pb.setForeground(Color.gray);
			} else {
				act_pb.setForeground(Color.black);
			}
		}

		if (Apl_Context.Is_Net_Context() == false) {
			/** Save-pr�fen */
			if (frs_act_vis.size() != 0) {
				if (((Frm_Manager) frs_act_vis.elementAt(0)).Get_Edited() == true)
					frs_pb_save.setEnabled(true);
				else
					frs_pb_save.setEnabled(false);
			} else
				frs_pb_save.setEnabled(false);
		} else {
			/** Save sperren */
			frs_pb_save.setEnabled(false);
		}
	}

	/** LayoutButtonList: Button-List zeichnen */
	private void LayoutButtonList() {
		int i, act_x, pb_width;
		Button act_pb;
		Font ft;
		Frm_Manager act_frame_mgr;

		/** Alle Buttons entfernen */
		frs_scroll_panel.removeAll();

		act_x = 0;

		/** ..und neu aufbauen */
		for (i = 0; i < frs_pbs.size(); i++) {
			act_pb = (Button) frs_pbs.elementAt(i);
			act_frame_mgr = (Frm_Manager) frs_frame_mgrs.elementAt(i);

			/** Button einh�ngen und sichtbar machen */
			ft = new Font("SansSerif", Font.PLAIN, pb_font_size);
			act_pb.setFont(ft);
			pb_width = act_pb.getFontMetrics(ft).stringWidth(act_frame_mgr.Get_Name()) + 15;
			act_pb.setSize(pb_width, frs_height - 4);
			act_pb.setLocation(act_x, 0);
			act_x += pb_width;

			frs_scroll_panel.add(act_pb);
		}

		/** Panel-Resize */
		frs_scroll_panel.setSize(act_x, frs_scroll_panel.getSize().height);

		frs_scroll.doLayout();

		/** Button-aktivieren */
		LayoutButtonEnabling();

		/** Scrollers aktualisieren */
		LayoutScrollButtonEnabling();
	}

	/** LayoutScrollButtonEnabling: Scroll-Button enabling */
	private void LayoutScrollButtonEnabling() {
		int act_scp, last_x;

		act_scp = frs_scroll.getScrollPosition().x;

		last_x = frs_scroll_panel.getSize().width;

		if (act_scp > 0)
			frs_pb_left.setEnabled(true);
		else
			frs_pb_left.setEnabled(false);

		if ((act_scp + frs_scroll.getViewportSize().width) < last_x)
			frs_pb_right.setEnabled(true);
		else
			frs_pb_right.setEnabled(false);
	}

	/** NewProject: neues Project aufnehmen */
	public Frm_Manager NewProject(String frm_mgr_name) {
		Button pb;
		Panel new_panel;
		Frm_Manager frm_mgr;

		/** neuen Project-Manager erzeugen */
		frm_mgr = new Frm_Manager(frm_mgr_panel, this);

		frm_mgr.setVisible(false);

		/** FrameMgr Name gesetzt ? */
		if (frm_mgr_name == "") {
			unknown_cnt += 1;
			frm_mgr.Set_Name("Project " + unknown_cnt);
		} else
			frm_mgr.Set_Name(frm_mgr_name);

		/** Project-Mgr einh�ngen */
		frs_frame_mgrs.addElement(frm_mgr);

		/** neuer Button erzeugen */
		pb = new Button(frm_mgr.Get_Name());
		frs_pbs.addElement(pb);

		/** Listener f�r Button registrieren */
		pb.addMouseListener(frs_mouse_events);

		/** ist noch kein Project dargestellt ? -> darstellen */
		if (frs_act_vis.size() == 0) {
			frm_mgr.setVisible(true);

			/** sichtbare Project-List erweitern */
			frs_act_vis.addElement(frm_mgr);
		}

		/** Button-List neu zeichnen */
		LayoutButtonList();

		return (frm_mgr);
	}

	/** SwitchToProject: Anzeige wechseln auf Project */
	public void SwitchToProject(Frm_Manager frm_mgr) {
		Frm_Manager act_mgr;
		MouseEvent event;
		int i;
		boolean found;

		if (frm_mgr == null)
			return;

		found = false;

		for (i = 0; i < frs_frame_mgrs.size(); i++) {
			act_mgr = (Frm_Manager) frs_frame_mgrs.elementAt(i);

			if (act_mgr == frm_mgr) {
				found = true;
				break;
			}
		}

		if (found == false)
			return;

		/** Gew�nschte Ansicht ansteuern */
		event = null;

		event = new MouseEvent((Button) frs_pbs.elementAt(i), MouseEvent.MOUSE_RELEASED, 0l, InputEvent.BUTTON1_MASK, 0,
				0, 1, false);

		((Button) frs_pbs.elementAt(i)).dispatchEvent(event);
	}

	/** DeleteProject: Project aus Anzeige l�schen */
	private void DeleteProject(Frm_Manager frm_mgr) {
		int i, idx;
		Frm_Manager new_mgr;
		Dlg_Confirm conf;

		if (frs_frame_mgrs.contains(frm_mgr) == true) {
			idx = frs_frame_mgrs.lastIndexOf(frm_mgr);
			if (idx != -1) {
				if ((frm_mgr.Get_Edited() == true) && (Apl_Context.Is_Net_Context() == false)) {
					conf = new Dlg_Confirm(frm_mgr.Get_Name(), "Close project without saving changes ?",
							Dlg_Confirm.DLG_YES_NO, frm_mgr_panel);

					if (conf.Get_Result() == Dlg_Confirm.RES_NO)
						return;
				}

				/** Project noch sichtbar ? */
				if (frs_act_vis.size() != 0) {
					if (frs_act_vis.contains(frm_mgr) == true) {
						/** wird angezeigt dann umschalten auf anderes Project */
						if (frs_frame_mgrs.size() > 1) {
							for (i = 0;; i++) {
								new_mgr = (Frm_Manager) frs_frame_mgrs.elementAt(i);

								if (new_mgr != frm_mgr)
									break;
							}

							SwitchToProject(new_mgr);
						} else {
							/** wird angezeigt aber keine Umschaltalternative vorhanden */
							frs_act_vis = null;
							frs_act_vis = new Vector();

							frm_mgr.setVisible(false);
						}
					}
				}

				/** Button entfernen */
				frs_pbs.removeElementAt(idx);

				/** Aus Project-List entfernen */
				frs_frame_mgrs.removeElementAt(idx);

				/** Button Re-Layout */
				LayoutButtonList();
			}
		}
	}

	/** CheckOnClose: pr�fen ob Exit zul�ssig */
	public boolean CheckOnClose() {
		boolean can_close;
		int i;
		Frm_Manager act_mgr;
		Dlg_Confirm conf;

		can_close = true;

		for (i = 0; i < frs_frame_mgrs.size(); i++) {
			act_mgr = (Frm_Manager) frs_frame_mgrs.elementAt(i);

			if (act_mgr.Get_Edited() == true) {
				can_close = false;
				break;
			}
		}

		if (can_close == false) {
			if (Apl_Context.Is_Net_Context() == false) {
				conf = new Dlg_Confirm("Visual Graph", "Exit without saving changes ?", Dlg_Confirm.DLG_YES_NO,
						frm_mgr_panel);

				if (conf.Get_Result() == Dlg_Confirm.RES_YES)
					can_close = true;
			} else
				can_close = true;
		}

		return (can_close);
	}

	/** File-Name Filter-Interface */
	public boolean accept(File dir, String name) {
		if (name.endsWith(file_extension))
			return (true);

		return (false);
	}

	/** SaveProject: Projekt lokal sichern */
	public void SaveProject(Frm_Manager frm_mgr, boolean save_as) {
		int i, idx;
		FileOutputStream fouts = null;
		GZIPOutputStream zip_out = null;
		ObjectOutputStream obj_out;
		File fn = null;
		Frs_Serialize fser;
		FileDialog sdialog;
		String s_file;
		Frame parent_frame;
		Vector frm_frames;
		Frm_Frame act_frame;
		URL ufn = null;
		URLConnection ufn_con = null;
		OutputStream ufouts = null;
		Dlg_Confirm cdlg;

		if (frs_frame_mgrs.contains(frm_mgr) == false)
			return;

		idx = frs_frame_mgrs.indexOf(frm_mgr);
		if (idx == -1)
			return;

		if ((save_as) || (frm_mgr.Get_Frm_File().equals(""))) {
			if (Apl_Context.Is_Net_Context() == false) {
				/** Parent-Frame erzeugen */
				parent_frame = new Frame();
				parent_frame.setLocation(getLocation());

				/** File-Dialog �ffnen */
				try {
					sdialog = new FileDialog(parent_frame, "Save Project", FileDialog.SAVE);
					sdialog.setFilenameFilter(this);
					sdialog.setDirectory(Apl_Context.Get_Prj_Path());
					sdialog.setVisible(true);
				} catch (NullPointerException exc) {
					parent_frame.dispose();
					return;
				}

				parent_frame.dispose();

				s_file = sdialog.getFile();

				if (s_file.endsWith(file_extension) == false)
					s_file = s_file + file_extension;

				fn = new File(sdialog.getDirectory(), s_file);

				frm_mgr.Set_Frm_File(fn.toString());

				frm_mgr.Set_Name(fn.getName().substring(0, fn.getName().length() - file_extension.length()));

				((Button) frs_pbs.elementAt(idx)).setLabel(frm_mgr.Get_Name());

				LayoutButtonList();
			} else {
				cdlg = new Dlg_Confirm("Save Project ...", "Filename:", Dlg_Confirm.DLG_INPUT_TXT, frm_mgr_panel);

				s_file = cdlg.Get_Result_Text();

				if ((s_file.equals("")) || (cdlg.Get_Result() == Dlg_Confirm.RES_CANCEL))
					return;

				/** s_file = "TestDatei"; */

				if (s_file.endsWith(file_extension) == false)
					s_file = s_file + file_extension;

				try {
					ufn = new URI(Apl_Context.Get_Prj_Path() + s_file).toURL();
				} catch (MalformedURLException | URISyntaxException exc) {
					System.err.println("MalformedUrlException (SaveProject): " + exc.getMessage());
					return;
				}

				frm_mgr.Set_Frm_File(ufn.toString());

				frm_mgr.Set_Name(s_file.substring(0, s_file.length() - file_extension.length()));

				((Button) frs_pbs.elementAt(idx)).setLabel(frm_mgr.Get_Name());

				LayoutButtonList();
			}
		} else {
			s_file = frm_mgr.Get_Frm_File();

			if (Apl_Context.Is_Net_Context() == false) {
				fn = new File(s_file);
			} else {
				try {
					ufn = new URI(s_file).toURL();
				} catch (MalformedURLException | URISyntaxException exc) {
					System.err.println("MalformedUrlException (SaveProject): " + exc.getMessage());
					return;
				}
			}
		}

		if (Apl_Context.Is_Net_Context() == false) {
			/** OutputStream erzeugen */
			try {
				fouts = new FileOutputStream(fn);
				zip_out = new GZIPOutputStream(fouts);
			} catch (IOException exc) {
				System.err.println("IOException (SaveProject): " + exc.getMessage());
				return;
			}

			/** ObjectOutputStream erzeugen, Project sichern */
			try {
				obj_out = new ObjectOutputStream(zip_out);
			} catch (IOException exc) {
				System.err.println("IOException (SaveProject): " + exc.getMessage());
				return;
			}
		} else {
			try {
				ufn_con = ufn.openConnection();

				/** Interaktion unerw�nscht */
				ufn_con.setAllowUserInteraction(false);

				/** output-Verbindung gew�nscht */
				ufn_con.setDoOutput(true);
				/** ufn_con.setDoInput(false); */

				/** und �ffnen */
				ufn_con.connect();

				/** Output-Stream �ffnen */
				ufouts = (OutputStream) ufn_con.getOutputStream();
				zip_out = new GZIPOutputStream(ufouts);

				/** ObjectOutputStream erzeugen, Project sichern */
				try {
					obj_out = new ObjectOutputStream(zip_out);
				} catch (IOException exc) {
					System.err.println("IOException (SaveProject): " + exc.getMessage());
					return;
				}
			} catch (IOException exc) {
				System.err.println("IOException (SaveProject): " + exc.getMessage());
				return;
			}
		}

		/** Stream-Object anlegen */
		fser = new Frs_Serialize(frm_mgr);
		/** Serialisierung vorbereiten */
		frm_frames = frm_mgr.Get_Frm_Frames();

		for (i = 0; i < frm_frames.size(); i++) {
			act_frame = (Frm_Frame) frm_frames.elementAt(i);

			act_frame.PrepareSerializedObject(SER_BEFORE_WRITE);
		}

		/** Stream out objects */
		try {
			try {
				obj_out.writeObject(fser);
			} catch (NotSerializableException exc) {
				System.err.println("NotSerializableException (SaveProject): " + exc.getMessage());
				return;
			}
		} catch (IOException exc) {
			System.err.println("IOException (SaveProject): " + exc.getMessage());
			return;
		}

		/** Stream-Flush */
		try {
			obj_out.flush();
		} catch (IOException exc) {
			System.err.println("IOException (SaveProject): " + exc.getMessage());
			return;
		}

		/** Stream schlie�en */
		try {
			obj_out.close();
		} catch (IOException exc) {
			System.err.println("IOException (SaveProject): " + exc.getMessage());
			return;
		}

		/** Serialisierung nachbereiten */
		for (i = 0; i < frm_frames.size(); i++) {
			act_frame = (Frm_Frame) frm_frames.elementAt(i);

			act_frame.PrepareSerializedObject(SER_AFTER_WRITE);
		}

		frm_mgr.Set_Edited(false);
	}

	/** LoadProject: Projekt lokal laden */
	public void LoadProject() {
		int i;
		FileInputStream fins = null;
		ObjectInputStream obj_in;
		GZIPInputStream zip_in = null;
		File fn;
		Frs_Serialize fser;
		FileDialog ldialog;
		String s_file;
		Frame parent_frame;
		Frm_Manager fmgr;
		Frm_Frame act_frame;
		URL ufn = null;
		URLConnection ufn_con = null;
		InputStream ufins = null;
		Dlg_Confirm cdlg;

		if (Apl_Context.Is_Net_Context() == false) {
			/** Parent-Frame erzeugen */
			parent_frame = new Frame();
			parent_frame.setLocation(getLocation());

			/** File-Dialog �ffnen */
			try {
				ldialog = new FileDialog(parent_frame, "Load Project", FileDialog.LOAD);
				ldialog.setFilenameFilter(this);
				ldialog.setDirectory(Apl_Context.Get_Prj_Path());
				ldialog.setVisible(true);
			} catch (NullPointerException exc) {
				parent_frame.dispose();
				return;
			}

			parent_frame.dispose();

			s_file = ldialog.getFile();

			if (s_file.endsWith(file_extension) == false)
				return;

			fn = new File(ldialog.getDirectory(), s_file);

			/** pr�fen ob Project schon geladen */
			for (i = 0; i < frs_frame_mgrs.size(); i++) {
				fmgr = (Frm_Manager) frs_frame_mgrs.elementAt(i);

				if (fmgr.Get_Frm_File().equals(fn.toString()) == true) {
					SwitchToProject(fmgr);
					return;
				}
			}

			/** InputStream erzeugen */
			try {
				fins = new FileInputStream(fn);
				zip_in = new GZIPInputStream(fins);
			} catch (IOException exc) {
				System.err.println("IOException (LoadProject): " + exc.getMessage());
				return;
			}

			/** ObjectInputStream erzeugen, Project laden */
			try {
				obj_in = new ObjectInputStream(zip_in);
			} catch (IOException exc) {
				System.err.println("IOException (LoadProject): " + exc.getMessage());
				return;
			}
		} else {
			cdlg = new Dlg_Confirm("Load Project ...", "Filename:", Dlg_Confirm.DLG_INPUT_TXT, frm_mgr_panel);

			s_file = cdlg.Get_Result_Text();

			if ((s_file.equals("")) || (cdlg.Get_Result() == Dlg_Confirm.RES_CANCEL))
				return;

			if (s_file.endsWith(file_extension) == false)
				s_file = s_file + file_extension;

			try {
				ufn = new URI(Apl_Context.Get_Prj_Path() + s_file).toURL();
			} catch (MalformedURLException | URISyntaxException exc) {
				System.err.println("MalformedUrlException (LoadProject): " + exc.getMessage());
				return;
			}

			/** pr�fen ob Project schon geladen */
			for (i = 0; i < frs_frame_mgrs.size(); i++) {
				fmgr = (Frm_Manager) frs_frame_mgrs.elementAt(i);

				if (fmgr.Get_Frm_File().equals(ufn.toString()) == true) {
					SwitchToProject(fmgr);
					return;
				}
			}

			/** Connection aufbauen */
			try {
				ufn_con = ufn.openConnection();

				/** Interaktion unerw�nscht */
				ufn_con.setAllowUserInteraction(false);

				/** input-Verbindung gew�nscht */
				ufn_con.setDoInput(true);

				/** und �ffnen */
				ufn_con.connect();

				/** Input-Stream �ffnen */
				ufins = (InputStream) ufn_con.getInputStream();
				zip_in = new GZIPInputStream(ufins);
			} catch (IOException exc) {
				System.err.println("IOException (LoadProject): " + exc.getMessage());
				return;
			}

			/** ObjectInputStream erzeugen, Project laden */
			try {
				obj_in = new ObjectInputStream(zip_in);
			} catch (IOException exc) {
				System.err.println("IOException (LoadProject): " + exc.getMessage());
				return;
			}
		}

		/** Stream in objects */
		try {
			fser = (Frs_Serialize) obj_in.readObject();
		} catch (Exception exc) {
			System.err.println("Exception (LoadProject): " + exc.getMessage());
			return;
		}

		/** Stream schlie�en */
		try {
			obj_in.close();
		} catch (IOException exc) {
			System.err.println("IOException (LoadProject): " + exc.getMessage());
			return;
		}

		/** geladenes Project einh�ngen */

		if (fser == null)
			return;

		/** Neues Project erzeugen */
		fmgr = NewProject(fser.Get_Name());
		if (fmgr == null)
			return;

		/** Filename setzen */
		fmgr.Set_Frm_File(fser.Get_Frm_File());

		/** Set_Frm_Alg_Infos: */
		fmgr.Set_Frm_Alg_Infos(fser.Get_Frm_Alg_Infos());
		fmgr.Set_Frm_Alg_Info_Classes(fser.Get_Frm_Alg_Info_Classes());

		/** Serialisierung nachbereiten */
		for (i = 0; i < fser.Get_Frm_Frames().size(); i++) {
			act_frame = (Frm_Frame) fser.Get_Frm_Frames().elementAt(i);

			act_frame.PrepareSerializedObject(SER_AFTER_READ);

			/** neuer Frame-Manager setzen */
			act_frame.Set_Frm_Mgr(fmgr);

			/** Frame einh�ngen */
			fmgr.AddFrame(act_frame);
		}

		/** Frame-Ansicht wiederherstellen */
		if (fser.Get_Vis_Frames().size() == 1) {
			/** einfache Ansicht */
			fmgr.SwitchToFrame((Frm_Frame) fser.Get_Vis_Frames().elementAt(0));
		} else {
			/** Multiple Ansicht */
			fmgr.SwitchToFrame((Frm_Frame) fser.Get_Vis_Frames().elementAt(0));

			for (i = 1; i < fser.Get_Vis_Frames().size(); i++) {
				fmgr.AddFrameToView((Frm_Frame) fser.Get_Vis_Frames().elementAt(i));
			}
		}

		fmgr.Set_Edited(false);

		/** geladenes Project anzeigen */
		SwitchToProject(fmgr);
	}

	/** Frs_ButtonActionAdapter: ButtonActionAdapter */
	class Frs_ButtonActionAdapter implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent event) {
			Handle_ActionPerformed(event);
		}
	}

	public void Handle_ActionPerformed(ActionEvent event) {
		int act_scp, last_x;
		Frm_Manager frm_mgr;
		String command;
		Object object = event.getSource();

		/** Popup-Commands auswerten */
		command = event.getActionCommand();

		/** Project l�schen */
		if (command.equals("CLOSE_PROJECT")) {
			DeleteProject((Frm_Manager) frs_act_vis.elementAt(0));
		}

		/** Scroll-Button Event */
		if (object == frs_pb_left) {
			act_scp = frs_scroll.getScrollPosition().x;

			if ((act_scp - scroll_step) > 0)
				act_scp -= scroll_step;
			else
				act_scp = 0;

			frs_scroll.setScrollPosition(act_scp, 0);

			LayoutScrollButtonEnabling();
		}

		if (object == frs_pb_right) {
			act_scp = frs_scroll.getScrollPosition().x;

			last_x = frs_scroll_panel.getSize().width;

			if ((act_scp + frs_scroll.getViewportSize().width + scroll_step) <= last_x)
				act_scp += scroll_step;
			else
				act_scp = last_x - frs_scroll.getViewportSize().width;

			frs_scroll.setScrollPosition(act_scp, 0);

			LayoutScrollButtonEnabling();
		}

		if ((object == frs_pb_new) || (command.equals("NEW_PROJECT"))) {
			/** Neuen Project-Manager bereitstellen */
			frm_mgr = NewProject("");

			/** und anzeigen */
			SwitchToProject(frm_mgr);
		}

		if ((object == frs_pb_save) || (command.equals("SAVE_PROJECT"))) {
			SaveProject((Frm_Manager) frs_act_vis.elementAt(0), false);
		}

		if (command.equals("SAVE_AS_PROJECT")) {
			SaveProject((Frm_Manager) frs_act_vis.elementAt(0), true);
		}

		if ((object == frs_pb_load) || (command.equals("LOAD_PROJECT"))) {
			LoadProject();
		}
	}

	/** Resize-Event-Verarbeitung */
	class Frs_ComponentEventAdapter extends java.awt.event.ComponentAdapter {
		public void componentResized(java.awt.event.ComponentEvent event) {
			Handle_ComponentResized(event);
		}
	}

	public synchronized void Handle_ComponentResized(ComponentEvent event) {
		int i, act_x;
		Frm_Manager act_mgr;
		Button act_pb;

		/** TopPanel-Resize */
		frs_top_panel.setSize(frm_mgr_panel.getBounds().width, frs_height);

		/** Panel Resize */
		setSize(frm_mgr_panel.getBounds().width, frs_height);

		frs_pb_left.setLocation(frm_mgr_panel.getBounds().width - 2 * pb_scroll_width, 0);
		frs_pb_right.setLocation(frm_mgr_panel.getBounds().width - pb_scroll_width, 0);

		frs_scroll.setLocation(3 * pb_width, 0);
		frs_scroll.setSize(frm_mgr_panel.getBounds().width - 3 * pb_width - 2 * pb_scroll_width, frs_height);

		/** Scroll-Panel neu setzen */
		act_x = 0;
		for (i = 0; i < frs_pbs.size(); i++) {
			act_pb = (Button) frs_pbs.elementAt(i);
			act_x += act_pb.getSize().width;
		}
		/** Panel-Resize */
		frs_scroll_panel.setSize(act_x, frs_scroll_panel.getSize().height);

		frs_scroll.doLayout();

		/** Scroll-Buttons �berpr�fen */
		LayoutScrollButtonEnabling();

		/** Project-Managers Resize Events dispatchen */
		for (i = 0; i < frs_frame_mgrs.size(); i++) {
			act_mgr = (Frm_Manager) frs_frame_mgrs.elementAt(i);

			act_mgr.dispatchEvent(new ComponentEvent(frm_mgr_panel, ComponentEvent.COMPONENT_RESIZED));
		}
	}

	/** Class Frs_MouseEventAdapter: Adapter f�r MouseEvents */
	class Frs_MouseEventAdapter extends MouseAdapter {
		public void mouseReleased(MouseEvent event) {
			Handle_MouseReleased(event);
		}
	}

	public void Handle_MouseReleased(MouseEvent event) {
		int i, idx;
		boolean right_pressed;
		Object object = event.getSource();
		MenuItem mi;
		Frm_Manager frm_mgr, act_frame_mgr;

		right_pressed = (event.getModifiers() & InputEvent.META_MASK) != 0 ? true : false;

		/** Popup-Menu darstellen */
		if (right_pressed) {
			/** MenuItem-Enabling setzen */
			for (i = 0; i < frs_popup.getItemCount(); i++) {
				mi = frs_popup.getItem(i);

				if (mi.getActionCommand() == "CLOSE_PROJECT") {
					if (frs_act_vis.size() == 1)
						mi.setEnabled(true);
					else
						mi.setEnabled(false);
				}

				if (mi.getActionCommand() == "LOAD_PROJECT") {
					if (Apl_Context.Is_Net_Context() == false)
						mi.setEnabled(true);
					else
						mi.setEnabled(false);
				}

				if (mi.getActionCommand() == "SAVE_PROJECT") {
					if (Apl_Context.Is_Net_Context() == false) {
						if (frs_act_vis.size() == 1) {
							if (((Frm_Manager) frs_act_vis.elementAt(0)).Get_Edited() == true)
								mi.setEnabled(true);
							else
								mi.setEnabled(false);
						} else
							mi.setEnabled(false);
					} else
						mi.setEnabled(false);
				}

				if (mi.getActionCommand() == "SAVE_AS_PROJECT") {
					if (Apl_Context.Is_Net_Context() == false) {
						if (frs_act_vis.size() == 1)
							mi.setEnabled(true);
						else
							mi.setEnabled(false);
					} else
						mi.setEnabled(false);
				}

			}

			frs_popup.show((Component) object, event.getX(), event.getY());
			return;
		}

		if (!right_pressed) {
			/** Project-Button Event */
			if (frs_pbs.contains(object) == true) {
				idx = frs_pbs.lastIndexOf(object);
				if (idx != -1) {
					/** sichtbaren Frames verbergen */
					for (i = 0; i < frs_act_vis.size(); i++) {
						act_frame_mgr = (Frm_Manager) frs_act_vis.elementAt(i);
						act_frame_mgr.setVisible(false);
					}

					/** sichtbare Project-List l�schen */
					frs_act_vis = null;
					frs_act_vis = new Vector();

					frm_mgr = (Frm_Manager) frs_frame_mgrs.elementAt(idx);

					/** Sicht switchen */
					frm_mgr.setVisible(true);

					/** einen Resize durchf�hren */
					frm_mgr.dispatchEvent(new ComponentEvent(frm_mgr_panel, ComponentEvent.COMPONENT_RESIZED));

					/** sichtbare Project-List erweitern */
					frs_act_vis.addElement(frm_mgr);

					/** Button-Enabling neu zeichnen */
					LayoutButtonEnabling();
				}
			}
		}
	}

	/** RegisterAlgorithm: Registratur eines Algorithmus' */
	public void RegisterAlgorithm(String class_name, String name, String dok_name) {
		Class alg;
		Method mts[];
		boolean found;

		/** Pr�fen ob Algorithmen-Klasse in Ordnung */
		try {
			alg = Class.forName(class_name);
		} catch (ClassNotFoundException e) {
			System.err.println("ClassNotFoundException (RegisterAlgorithm): No Class available!");
			return;
		}
		;

		String super_class = alg.getSuperclass().getName();

		if (!super_class.equals("visualgraph.algorithms.Base_Algorithm"))
			return;

		/** Methoden pr�fen */

		try {
			mts = alg.getDeclaredMethods();

			found = false;
			for (int i = 0; i < mts.length; i++) {
				if (mts[i].getName().equals("run")) {
					found = true;
					break;
				}
			}

			if (!found)
				return;
		} catch (SecurityException e) {
			System.err.println("SecurityException (RegisterAlgorithm): No Methods available - trusting!");
		}

		/** ..und registrieren */
		frs_algorithms.addElement(class_name);

		if (name.equals(""))
			name = class_name;

		frs_algorithm_names.addElement(name);
		frs_algorithm_doks.addElement(dok_name);
	}

	/** GetAlgorithms: Stellt die registrierten Algorithmen-Klassennamen bereit */
	public Vector GetAlgorithms() {
		return (frs_algorithms);
	}

	/** GetAlgorithmNames: Stellt die registrierten Algorithmen-Namen bereit */
	public Vector GetAlgorithmNames() {
		return (frs_algorithm_names);
	}

	/** GetAlgorithmDoks: Stellt die registrierten Algorithmen-Dokumente bereit */
	public Vector GetAlgorithmDoks() {
		return (frs_algorithm_doks);
	}
}
