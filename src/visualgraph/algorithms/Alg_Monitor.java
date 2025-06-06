package visualgraph.algorithms;
/** Alg_Monitor: Monitor Thread, �berwacht das Ende eines Alg.Durchlaufs */
public class Alg_Monitor extends java.lang.Thread {
    /** Verweis auf Alg. */
    private Base_Algorithm alg_base;

    public void Init_Monitor(Base_Algorithm alg_base) {
	/** Priorit�t setzen */
	setPriority(Thread.MIN_PRIORITY);

	this.alg_base = alg_base;
    }

    @Override
    public void run() {
	if (alg_base != null) {
	    do {
		/** Monitoring 10 millis delay */
		try {
		    sleep(10);
		} catch (Exception exc) {
		}
		// yield( );

	    } while (alg_base.isAlive());

	    if (alg_base.isAlive() == false) {
		/** Finisher aufrufen */
		alg_base.Finished();
	    }
	}
    }
}
