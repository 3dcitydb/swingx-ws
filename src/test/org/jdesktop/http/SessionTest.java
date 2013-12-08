/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdesktop.http;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Richard
 */
public class SessionTest extends TestCase {
    private MockSession s;
    
    public SessionTest(String testName) {
        super(testName);
    }            

    @Override
    protected void setUp() throws Exception {
        s = new MockSession();
    }

    /**
     * Test of setSslSecurityLevel method, of class Session.
     */
    public void testSetSslSecurityLevel() {
        //TODO the thing to test here is what I can do unpriviledged.
        //I'm afraid by going with medium level security, I'm already
        //breaking things.
    }

    /**
     * Test of setMediumSecurityHandler method, of class Session.
     */
    public void testSetMediumSecurityHandler() {
    }

    public void testStateTransitions() throws Exception {
        Request req = new Request("http://www.google.com");
        
        s.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                String property = evt.getPropertyName();
                if ("state".equals(property)) {
                    State old = (State)evt.getOldValue();
                    State state = (State)evt.getNewValue();
                    if (old == State.READY) {
                        assertEquals(State.CONNECTING, state);
                        assertEquals(0, s.getBytesSoFar());
                        assertEquals(-1, s.getTotalBytes());
                    } else if (old == State.CONNECTING) {
                        assertEquals(State.SENDING, state);
                        assertEquals(0, s.getBytesSoFar());
                        assertEquals(-1, s.getTotalBytes());
                    } else if (old == State.SENDING) {
                        assertEquals(State.SENT, state);
                    } else if (old == State.SENT) {
                        assertEquals(State.RECEIVING, state);
                        assertEquals(0, s.getBytesSoFar());
                        assertEquals(-1, s.getTotalBytes());
                    } else if (old == State.RECEIVING) {
                        assertEquals(State.DONE, state);
                    }
                }
            }
        });
        s.execute(req);
    }
    
    // there are a matrix of methods that need to be tested:
    // unknown size         known size
    // totalBytes       bytesSoFar      Progress        State
    // sending      receiving
    
    
    
    /**
     * Test of getTotalBytes method, of class Session.
     */
    public void testGetTotalBytesAccurateWhenSendingDataOfUnknownSize() throws Exception {
        Request req = new Request("http://www.google.com");
        req.setBody(generateRandomBody(32000));
        
        // this property change listener keeps track of the changes in "total bytes"
        s.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                String property = evt.getPropertyName();
                if ("totalBytes".equals(property)) {
                    if (s.getState() == State.SENDING) {
                        long total = (Long)evt.getNewValue();
                        assertEquals(-1, total);
                    }
                }
            }
        });
        s.execute(req);
    }
    
    public void testGetTotalBytesAccurateWhenSendingDataOfKnownSize() throws Exception {
        Request req = new Request("http://www.google.com");
        req.setBody(generateRandomBody(32000));
        req.setHeader("Content-Length", "32000");
        
        // this property change listener keeps track of the changes in "total bytes"
        s.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                String property = evt.getPropertyName();
                if ("totalBytes".equals(property)) {
                    if (s.getState() == State.SENDING) {
                        long bytes = (Long)evt.getNewValue();
                        assertEquals(32000, bytes);
                    }
                }
            }
        });
        s.execute(req);
    }

    /**
     * Test of getBytesSoFar method, of class Session.
     */
    public void testGetBytesSoFarAccurateWhenSendingData() throws Exception {
        Request req = new Request("http://www.google.com");
        // ensure that the body of the request is plenty large, such
        // that the buffer used in the execute() method is filled and
        // emptied a couple times
        StringBuilder buffer = new StringBuilder();
        Random rand = new Random();
        for (int i=0; i<32000; i++) {
            buffer.append((char)(rand.nextInt(70) + 48));
        }
        req.setBody(buffer.toString());
        
        // this property change listener keeps track of the number of bytes
        // sent so far according to the session
        s.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                String property = evt.getPropertyName();
                if ("bytesSoFar".equals(property)) {
                    if (s.getState() == State.SENDING) {
                        long bytes = (Long)evt.getNewValue();
                        if (bytes >= 0) {
                            assertEquals(bytes, s.getNumBytesSent());
                        }
                    }
                }
            }
        });
        s.execute(req);
    }

    /**
     * Test of getProgress method, of class Session.
     */
    public void testGetProgress() {
    }

    /**
     * Test of getState method, of class Session.
     */
    public void testGetState() {
    }

    /**
     * Test of get method, of class Session.
     */
    public void testGet() throws Exception {
    }

    /**
     * Test of post method, of class Session.
     */
    public void testPost() throws Exception {
    }

    /**
     * Test of put method, of class Session.
     */
    public void testPut() throws Exception {
    }

    /**
     * Test of execute method, of class Session.
     */
    public void testExecute() throws Exception {
    }
    
    private static String generateRandomBody(int length) {
        StringBuilder buffer = new StringBuilder();
        Random rand = new Random();
        for (int i=0; i<length; i++) {
            buffer.append((char)(rand.nextInt(70) + 48));
        }
        return buffer.toString();
    }
}
