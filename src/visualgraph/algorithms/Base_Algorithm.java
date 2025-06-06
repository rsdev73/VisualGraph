package visualgraph.algorithms;

import java.awt.event.ComponentEvent;

import visualgraph.swing.Frm_Documentation;
import visualgraph.swing.Frm_Frame;
import visualgraph.swing.Frm_Manager;
import visualgraph.swing.Frm_Variable;

public class Base_Algorithm extends java.lang.Thread {
	/** Alg.-Ablauf Markers */
	public final static int ALG_READY = 0;
	/** Alg. bereit */
	public final static int ALG_FINISHED = 1;
	/** Alg. beendet */
	public final static int ALG_STOPPED = 2;
	/** gestoppt */
	public final static int ALG_RUNNING = 3;
	/** in Abarbeitung */
	public final static int ALG_RUNNING_STEP = 4;
	/** in Abarbeitung schrittw. */

	/** Verweis auf �bergeordneten Frame-Manager */
	protected Frm_Manager fm;
	protected Frm_Frame frm;

	/** Protocoll Frame */
	protected Frm_Documentation protocol;
	private int protocol_line;

	/** Variable-Monitor */
	protected Frm_Variable fv;

	/** Alg. Status */
	private int status;

	/** Init_Algorithm: Initialisiert den Alg.-Ablauf */
	public void Init_Algorithm(Frm_Frame frame, Frm_Manager fr_mgr) {
		/** Priorit�t setzen: */
		setPriority(Thread.MIN_PRIORITY);

		fm = fr_mgr;
		frm = frame;

		/** Initialisierung der Laufzeitumgebung */
		status = ALG_READY;

		/** Protocoll-Frame erzeugen */
		protocol = new Frm_Documentation("Protocol", fm);

		if (frame.Get_Write_Protocol()) {
			fm.AddResultFrame(protocol);

			/** Protokoll-Header schreiben */
			protocol.AppendLine(frm.Get_Alg_Name() + "-Algorithm Protocol:\n");
			protocol_line = 0;
		}

		/** Variable-Monitor erzeugen */
		fv = new Frm_Variable("Monitor", fm);

		if (frame.Get_Monitor_Vars()) {
			fv.SetTitle(frm.Get_Alg_Name() + "-Algorithm Variable-Monitor:");
			fm.AddResultFrame(fv);
		}

		// yield();
	}

	/** Get_Context: Ermittlung des Kotext-Dokuments */
	public Frm_Frame Get_Context() {
		return (frm);
	}

	/** R�ckgabe ob Variable-Monitor verwendet wird */
	public boolean Get_Monitor_Vars() {
		return (frm.Get_Monitor_Vars());
	}

	/** reset: Zur�cksetzen des Algorithmus */
	public void Rewind() {
		status = ALG_READY;

		if (fm == null)
			return;

		/** Ergebnis-Frames des letzten Durchlaufs l�schen */
		fm.DeleteResultFrames(frm);

		fm.LayoutButtonEnabling();
	}

	/** Stop: Die Abarbeitung des A.g wird angehalten */
	public void Stop() {
		if ((status == ALG_RUNNING) || (status == ALG_RUNNING_STEP)) {
			if (isAlive()) {
				try {
					status = ALG_STOPPED;

					fm.LayoutButtonEnabling();

					while (status == ALG_STOPPED) {
						sleep(10);
					}
				} catch (InterruptedException exc) {
				}
			}
		}
	}

	/** Pause: Bei schrittweiser Abarbeitung wir d. Ausf�hrung des A. angehalten */
	public void Pause() {
		if (status == ALG_RUNNING_STEP) {
			if (isAlive()) {
				try {
					status = ALG_STOPPED;

					fm.LayoutButtonEnabling();

					while (status == ALG_STOPPED) {
						sleep(10);
					}
				} catch (InterruptedException e) {
				}
			}
		} else
			Delay(frm.Get_Delay_Millis());
	}

	/**
	 * Resume: Bei schrittweise Abarbeitung des A.g wird d. Ausf�hrung fortgesetzt
	 */
	public void Resume(int run_mode) {
		if (status == ALG_STOPPED) {
			if (isAlive()) {
				status = run_mode;

				fm.LayoutButtonEnabling();
			}
		}
	}

	/** Finished: setzt d. Endemarkierung des Alg. */
	public void Finished() {
		status = ALG_FINISHED;

		frm.SetStdInfo(frm.Get_Alg_Name() + " finished");

		/** Resize-Event feuern -> Frames auf Manager-Ebene aktualisieren */
		fm.Handle_ComponentResized(new ComponentEvent(fm, ComponentEvent.COMPONENT_RESIZED));

		fm.LayoutButtonEnabling();
	}

	/** Delay: Alg. in d. Ausf�hrung verlangsamen */
	public void Delay() {
		Delay(frm.Get_Delay_Millis());
	}

	/** Delay: Alg. um Millisekunden in d. Ausf�hrung verlangsamen */
	public void Delay(long millis) {
		if (millis <= 0)
			return;

		try {
			sleep(millis);
		} catch (InterruptedException e) {
			;
		}
	}

	/** Get_Status: */
	public int Get_Status() {
		return (status);
	}

	/** Set_Status: */
	public void Set_Status(int stat) {
		status = stat;
	}

	/** Get_Protocol: */
	public Frm_Documentation Get_Protocol() {
		return (protocol);
	}

	/** Get_Next_Protocol_Line: N�chste lfd-Protkoll-Zeilennr zur�ckgeben */
	public int Get_Next_Protocol_Line() {
		protocol_line++;
		return (protocol_line);
	}

	/** Override info */
	public void info(Frm_Documentation frm_doc) {
		;
	}
}
