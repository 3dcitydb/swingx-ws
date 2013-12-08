/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdesktop.ws;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingWorker;

import org.jdesktop.http.Session;
import org.jdesktop.http.State;

/**
 * An abstact implementation of an HTTP based service. This BaseService extension
 * provides a Session object to subclasses for working with HTTP based services
 * such as REST or some SOAP operations. This class handles the job of synchronizing
 * state between a Session and the State for this class.
 * 
 * @author Richard
 */
public abstract class AbstractHttpService extends BaseService {
    /*
     * IMPLEMENTATION DETAILS
     * 
     * AbstractHttpService is backed by an org.jdesktop.http.Session object.
     * The <code>progress</code> and <code>state</code> properties are tied
     * directly to the session object, such that whenever those properties
     * change on the session they are propogated through this class. Of course,
     * clients of this class need not be aware of this at all.
     * 
     * Initially the state of BaseService/AbstractHttpService was just a
     * reflection of the state of the Session, but this was found to be
     * inadequate because the session would be set to "DONE" firing a 
     * property change event and so forth before the service subclasses
     * had a chance to handle the result and parse it etc. As a result,
     * both the done() function and the DONE PCE would be fired and the
     * BaseService would be in the wrong state. Hence, the BaseService
     * must maintain its own state for certain stages, yet be influenced
     * by the state of the Session.
     */
    
    /**
     * The Session object. This reference is kept in the AbstractHttpService
     * so as to enable the service to keep in sync with it both in terms
     * of State management and progress.
     */
    private WSSession session = new WSSession();
    
    /**
     * The background worker. It is non null when a background task is running,
     * null otherwise.
     */
    private AtomicReference<SwingWorker> workerRef = new AtomicReference<SwingWorker>();

    /**
     * Creates a new AbstractHttpService which is tied to one Session.
     */
    protected AbstractHttpService() {
        /*
         * Setup a property change listener for the session such that
         * the progress and state property change events are forwarded
         */
        session.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                String property = evt.getPropertyName();
                if ("progress".equals(property)) {
                    setProgress((Float)evt.getNewValue());
                } else if ("state".equals(property)) {
                    State state = (State)evt.getNewValue();
                    switch (state) {
                        case READY:
                        case CONNECTING:
                        case SENDING:
                        case SENT:
                        case RECEIVING:
                            // pass it on
                            setState(state);
                            break;
                        case DONE:
                        case FAILED:
                        case ABORTED:
                        default:
                            // ignore all the others since they are set
                            // by subclasses when invoking the abort,
                            // done, or failed methods of BaseService
                    }
                }
            }
        });
    }
    
    /**
     * Overridden to ensure that the state of the Session is kept in sync with
     * the state of this AbstractHttpService.
     * 
     * @param s
     */
    @Override protected void setState(State s) {
        session.setState(s);
        super.setState(s);
    }
    
    /**
     * Gets the session of this AbstractHttpService. Subclasses may invoke
     * this method when they need to get hold of the session to execute
     * requests, and so forth.
     * 
     * @return
     */
    protected Session getSession() { return session; }
    
    protected abstract SwingWorker createWorker();
    
    /**
     * @inheritDoc
     */
    @Override protected void doSend() {
        SwingWorker worker = createWorker();
        if (!workerRef.compareAndSet(null, worker)) {
            throw new IllegalStateException("Cannot send while request/response in process");
        } else {
            worker.execute();
        }
    }

    /**
     * @inheritDoc
     */
    @Override protected void doAbort() {
        // only clear the worker if by the time we get to compareAndSet
        // the worker hasn't already been cleared. This avoids calling
        // cancel more than once for a single worker by guarding against
        // multiple threads calling abort concurrently
        SwingWorker worker = workerRef.get();
        if (workerRef.compareAndSet(worker, null)) {
            worker.cancel(true);
        }
    }

    /**
     * This class simply exposes the setState() method such that the AbstractHttpService
     * can manipulate the state to keep it in synch with this class. Personally
     * I find this solution apalling since it exposes the ability in Session
     * to mess with the state, which I would prefer to keep private to Session.
     * Nevertheless, this appears the best solution to the problem of keeping
     * states in sync. While it is certainly possible to ensure that this
     * works in this case, it opens the possiblity for abuse by other Session
     * subclasses and also makes it tricky to keep all the invariants correct
     * between this AbstractHttpSession class and the Session. Only sufficient
     * unit testing can guard against it.
     */
    private static final class WSSession extends Session {
        @Override public void setState(State s) {
            super.setState(s);
        }
    }
}

