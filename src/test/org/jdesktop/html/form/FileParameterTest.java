/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdesktop.html.form;

import java.io.InputStream;
import junit.framework.TestCase;

/**
 *
 * @author Richard
 */
public class FileParameterTest extends TestCase {
    
    public FileParameterTest(String testName) {
        super(testName);
    }            

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of computeMimeType method, of class FileParameter.
     */
    public void testComputeMimeType() {
        assertEquals("text/plain", FileParameter.computeMimeType("test.text"));
        assertEquals("image/png", FileParameter.computeMimeType("file.png"));
        assertEquals("text/plain", FileParameter.computeMimeType("file.txt"));
        assertEquals("text/plain", FileParameter.computeMimeType("foo/file.text"));
        assertEquals("text/plain", FileParameter.computeMimeType("/usr/local/foo.txt"));
    }
}
