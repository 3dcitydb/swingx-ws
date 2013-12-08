/*
 * SimpleDocumentBuilderTest.java
 * JUnit based test
 *
 * Created on January 5, 2007, 10:30 AM
 */

package org.jdesktop.dom;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import junit.framework.*;

/**
 *
 * @author rbair
 */
public class SimpleDocumentBuilderTest extends TestCase {
    private URL hamlet;
    
    public SimpleDocumentBuilderTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        hamlet = SimpleDocumentBuilderTest.class.getResource("hamlet.xml");
    }
    
    /**
     *
     */
    public void testSimpleParse() throws Exception {
        //first test by parsing a standard XML document.
        SimpleDocument dom = SimpleDocumentBuilder.simpleParse(hamlet);
        String expectedPersonaeTitle = "Dramatis Personae";
        assertEquals(expectedPersonaeTitle, dom.getString("/PLAY/PERSONAE/TITLE"));
        
        //now test by parsing a standard XML document 20 times concurrently.
        //This is to flush out any bugs related to multithreaded parsing.
        //Use SwingWorker, for the heck of it
        ExecutorService executor = Executors.newCachedThreadPool();
        for (int i=0; i<50; i++) {
            Runnable r = new Runnable() {
                public void run() {
                    try {
                        SimpleDocument dom = SimpleDocumentBuilder.simpleParse(hamlet);
                        String expectedPersonaeTitle = "Dramatis Personae";
                        assertEquals(expectedPersonaeTitle, dom.getString("/PLAY/PERSONAE/TITLE"));
                    } catch (Exception e) {
                        assertTrue(e.getMessage(), false);
                    }
                }
            };
            executor.execute(r);
        }
    }

}
