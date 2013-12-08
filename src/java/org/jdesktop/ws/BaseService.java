/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdesktop.ws;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import org.jdesktop.beans.AbstractBean;
import org.jdesktop.http.State;
import org.jdesktop.ws.events.AbortEvent;
import org.jdesktop.ws.events.AbortListener;
import org.jdesktop.ws.events.DoneEvent;
import org.jdesktop.ws.events.DoneListener;
import org.jdesktop.ws.events.FailureEvent;
import org.jdesktop.ws.events.FailureListener;

/**
 * <p>An abstact class useful as a base for asynchronous data service classes.
 * For example, HttpService is a useful subclass for executing REST like
 * webservice calls at a low level. WebService is a useful class which embodies
 * one or more Operations (which are instances of BaseService) and which is
 * useful for executing SOAP requests.</p>
 * 
 * <p>One could also imagine the creation of FtpService, SshService, SftpService,
 * DatabaseService, and other such non http-based services.</p>
 * 
 * <p>BaseService serves as a base class for creating asynchronous Swing-friendly
 * web service APIs. All events fired from BaseService are fired on the EDT,
 * and it is assumed that all methods invoked on BaseService are likewise
 * invoked on the Event Dispatching Thread (EDT).</p>
 * 
 * @author Richard
 */
public abstract class BaseService extends AbstractBean {
    /*
     * IMPLEMENTATION DETAILS
     * 
     * Since all events fired from this class are on the EDT, and since it is
     * quite likely that the service instance will be operating on some background
     * thread (in fact, it is mandatory otherwise this would not be an
     * asynchronous class), it is necessary to push these events coming from
     * instances onto the EDT. This class does so in as efficient a manner as I
     * could contrive, by publishing these events to a ConcurrentLinkedQueue and
     * invoking SwingUtilities.invokeLater as little as possible.
     * 
     * When a background task is aborted, the service will be set to the ABORT
     * status and then the aborted() method of any AbortListeners will be invoked.
     * 
     * Likewise, when a task finished successfully, the state will reflect DONE
     * and the done method of any DoneListeners will be invoked. Also, if the
     * task fails for some reason then the FAILED state will be set and the 
     * failure method of FailureListeners will be invoked.
     */
    
    /**
     * This LinkedBlockingQueue holds PropertyChangeListener/PropertyChangeEvent
     * pairs which have not yet been fired on the EDT. This queue is updated
     * whenever new events are fired, or when events are taken out of the queue
     * and processed on the EDT.
     */
    private LinkedBlockingQueue<Event> events = new LinkedBlockingQueue<Event>();
    /**
     * Indicates that an event is in progress, and that there should therefore
     * not be another SwingUtilities.invokeLater call. This is the primary
     * mechanism for limiting the number of invokeLater calls to the minimum
     * necessary.
     */
    private AtomicBoolean eventInProgress = new AtomicBoolean(false);
    
    /**
     * The state that this BaseService is in
     */
    private State state = State.READY;
    
    /**
     * The progress of the state. When State is SENDING this is the progress
     * of the upload, or -1 if it is not known. When State is RECEIVING this
     * is the progress of the download, or -1 if it is not known. In the
     * READY and CONNECTING states it is -1 indicating an indeterminate value.
     * In the SENT state it still reflects the amount of data sent. When
     * DONE it represents the amount of data downloaded. When FAILED or ABORTED
     * it is -1 since a reliable value may not be avialable for what it means.
     */
    private float progress;
    
    /**
     * A list of listeners which are notified whenever the BaseService
     * finishes, aborts, or fails.
     */
    private EventListenerList listeners = new EventListenerList();
    
    /**
     * Creates a new BaseService.
     */
    protected BaseService() {}
    
    //----------- Bean Properties
    
    /**
     * Property indicating the state of the service. PropertyChange events will
     * be fired whenever this state changes
     */
    public final State getState() { return state; }
    
    /**
     * Sets the local state property, firing a property change event on the
     * EventDispatchThread if necessary. The value of the progress property
     * is set according to the rules outlined in the field javadocs for progress.
     * 
     * @param s
     */
    protected void setState(State s) {
        State old = this.state;
        fireOnEDT("state", old, this.state = s);
        
        switch (this.state) {
            case READY:
            case CONNECTING:
            case SENDING:
            case RECEIVING:
            case ABORTED:
            case FAILED:
                setProgress(-1f);
                break;
        }
    }
    
    /**
     * Property indicating the upload or download progress. This is updated
     * automatically based on the {@link state} of the service. A value of
     * -1 indicates that progress is not trackable, either because it is
     * indeterminate or because the State is not Sending or Receiving. All
     * states other than Sending or Receiving will always indicate a value of
     * -1, except for SENT and DONE.
     * This is a float value between 0-1 when it is valid.
     */
    public final float getProgress() { return progress; }
    
    /**
     * Sets the progress. If values are < -1 or > 1, then they are clamped to
     * -1 and 1 respectively. A property change event is fired on the EDT.
     * 
     * @param p
     */
    protected void setProgress(float p) {
        if (p < -1f) p = -1f;
        if (p > 1f) p = 1f;
        float old = this.progress;
        fireOnEDT("progress", old, this.progress = p);
    }
    
    //------------------- public methods
    
    /**
     * Initiates the request/response cycle by sending a request to the remote
     * service. This call returns immediately. Either use a property change
     * listener for the <code>state</code> property or add one or more of
     * DoneListener, FailureListener, AbortListener.
     */
    public final void send() {
        try {
            doSend();
        } catch (Exception e) {
            fail(e);
        }
    }
    
    /**
     * Aborts the current request. If called when in a state that is not
     * abortable (such as Ready or Done) then nothing happens. The final
     * state may not be set to ABORTED, but the aborted function of any
     * AbortListeners will be invoked.
     */
    public final void abort() {
        switch (state) {
            case CONNECTING:
            case SENDING:
            case SENT:
            case RECEIVING:
                doAbort();
                setState(State.ABORTED);
                fireAbort();
                break;
        }
    };
    
    //------------------- extension methods
    
    /**
     * Invoke this method from subclasses to force the connection to fail.
     * Note that this only works if the current state is CONNECTING,
     * SENDING, SENT, or RECEIVING. In all other states invoking this method
     * has no effect.
     * 
     * TODO This method should be protected, but the FX-Script class needs
     * access to it. Not sure what the right choice is here. Do not invoke
     * this method directly. In particular, it doesn't abort first, so
     * if the task hasn't already completed then don't even bother calling
     * this method until it does because there will be problems.
     * 
     * @param th
     */
    public void fail(Throwable th) {
        switch (state) {
            case CONNECTING:
            case SENDING:
            case SENT:
            case RECEIVING:
                setState(State.FAILED);
                fireFailure(th);
                break;
        }
    }
    
    /**
     * Invoked by this AbstractHttpService, this method performs the actual
     * send operation. This method must not block.
     * 
     * @throws java.lang.Exception
     */
    protected abstract void doSend() throws Exception;
    
    /**
     * Invoked by this AbstractHttpService, this method performs the actual
     * chores behind aborting the asynchronous process. Do not bother setting
     * the state, just abort the process.
     */
    protected abstract void doAbort();

    /**
     * Called by the subclass when the background process completes successfully.
     */
    protected final void done() {
        setState(State.DONE);
        fireDone();
    }
    
    /**
     * Fires the given property change event on the EDT in a manner that
     * avoids calling invokeLater for every property change event. This can
     * greatly improve performance in cases where many events happen very
     * quickly.
     * 
     * @param prop
     * @param oldv
     * @param newv
     */
    protected final void fireOnEDT(String prop, Object oldv, Object newv) {
        // only continue if old & new are different
        if (oldv == newv) return;
	if (oldv != null && newv != null && oldv.equals(newv)) return;
        
        // no point continuing if there are not any listeners
        PropertyChangeListener[] pcls = getPropertyChangeListeners();
        if (pcls.length  == 0) return;
        
        // if on the EDT, then go ahead and fire it immediately
        if (SwingUtilities.isEventDispatchThread()) {
            firePropertyChange(prop, oldv, newv);
        } else {
            
            // if not on the EDT, then publish all the property change events
            // to a blocking queue. The queue will have one entry
            // per PropertyChangeListener/PropertyChangeEvent pair. These
            // are the "Events".
            for (PropertyChangeListener pcl : pcls) {
                events.add(new Event(pcl, new PropertyChangeEvent(this, prop, oldv, newv)));
            }

            // if there are items in the queue then invoke a Runnable on the EDT
            // which will then process those events. Note that it is possible
            // that between this if statement and the time the Runnable
            // actually executes that the events will have been drained already.
            // If that happens we get a spurious event, but the behavior will
            // still be correct.
            if (events.size() > 0 && eventInProgress.compareAndSet(false, true)) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        final List<Event> copy = new ArrayList<Event>();
                        events.drainTo(copy);
                        for (Event e : copy) {
                            e.listener.propertyChange(e.event);
                        }
                    }
                });
            }
        }
    }
    
    //------------------- Listener management
    
    /**
     * Adds a DoneListener.
     * 
     * @param listener
     */
    public final void addDoneListener(DoneListener listener) {
        listeners.add(DoneListener.class, listener);
    }
    
    /**
     * Removes a DoneListener
     * @param listener
     */
    public final void removeDoneListener(DoneListener listener) {
        listeners.remove(DoneListener.class, listener);
    }
    
    /**
     * Adds an AbortListener
     * @param listener
     */
    public void addAbortListener(AbortListener listener) {
        listeners.add(AbortListener.class, listener);
    }
    
    /**
     * Removes an AbortListener
     * @param listener
     */
    public void removeAbortListener(AbortListener listener) {
        listeners.remove(AbortListener.class, listener);
    }
    
    /**
     * Adds a FailureListener
     * @param listener
     */
    public void addFailureListener(FailureListener listener) {
        listeners.add(FailureListener.class, listener);
    }
    
    /**
     * Removes a FailureListener
     * @param listener
     */
    public void removeFailureListener(FailureListener listener) {
        listeners.remove(FailureListener.class, listener);
    }
            
    // ---------------- Private implementation classes
    
    /**
     * Fires the done event, but does not change the state. Only fires the
     * event. It is fired on the EDT. The event is only fired if the number of
     * DoneListeners is > 0. If invoked and the Status is <em>not</em> DONE,
     * then an IllegalStateException is thrown.
     */
    private void fireDone() {
        if (getState() != State.DONE) {
            throw new IllegalStateException("Illegal attempt made to fire a " +
                    "done event when not in the DONE state");
        }
        
        int count = listeners.getListenerCount(DoneListener.class);
        if (count == 0) return;
        
        final DoneEvent evt = new DoneEvent(this);
        final DoneListener[] all = listeners.getListeners(DoneListener.class);
        Runnable r = new Runnable() {
            public void run() {
                for (int i=0; i<all.length; i++) {
                    all[i].done(evt);
                }
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }
    
    /**
     * Fires the aborted event, but does not change the state. Only fires the
     * event. It is fired on the EDT. The event is only fired if the number of
     * AbortListeners is > 0. If invoked and the Status is <em>not</em> ABORTED,
     * then an IllegalStateException is thrown.
     */
    private void fireAbort() {
        if (getState() != State.ABORTED) {
            throw new IllegalStateException("Illegal attempt made to fire a " +
                    "abort event when not in the ABORTED state");
        }
        
        int count = listeners.getListenerCount(AbortListener.class);
        if (count == 0) return;
        
        final AbortEvent evt = new AbortEvent(this);
        final AbortListener[] all = listeners.getListeners(AbortListener.class);
        Runnable r = new Runnable() {
            public void run() {
                for (int i=0; i<all.length; i++) {
                    all[i].aborted(evt);
                }
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }
    
    /**
     * Fires the failure event, but does not change the state. Only fires the
     * event. It is fired on the EDT. The event is only fired if the number of
     * FailureListeners is > 0. If invoked and the Status is <em>not</em> FAILED,
     * then an IllegalStateException is thrown.
     */
    private void fireFailure(Throwable th) {
        if (getState() != State.FAILED) {
            throw new IllegalStateException("Illegal attempt made to fire a " +
                    "failure event when not in the FAILED state");
        }
        
        int count = listeners.getListenerCount(FailureListener.class);
        if (count == 0) return;
        
        final FailureEvent evt = new FailureEvent(this, th);
        final FailureListener[] all = listeners.getListeners(FailureListener.class);
        Runnable r = new Runnable() {
            public void run() {
                for (int i=0; i<all.length; i++) {
                    all[i].failure(evt);
                }
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }
    
    /**
     * BaseService uses a smart implementation of publishing property change
     * events onto the EDT to avoid EDT flooding, which most certainly would
     * occur when transfering large files with all the progress notification
     * updates.
     * 
     * Events are stored in a concurrent data structure and then a single
     * invokeLater event occurs. When the EDT gets to handling the event, it
     * retrieves all events that have occurred in the queue and fires them
     * all off at once. It maintains both the order of the events and also
     * reduces the amount of invokeLater() calls by a substantial amount in
     * some cases.
     */
    private static final class Event {
        private PropertyChangeListener listener;
        private PropertyChangeEvent event;
        Event(PropertyChangeListener listener, PropertyChangeEvent event) {
            this.listener = listener;
            this.event = event;
        }
    }
}
