/*
 * AsyncTest.java
 *
 * Created on December 19, 2006, 1:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.http.async;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.jdesktop.html.form.AbstractForm;
import org.jdesktop.html.form.DefaultInput;
import org.jdesktop.html.form.Form;
import org.jdesktop.html.form.Forms;
import org.jdesktop.html.form.Input;
import org.jdesktop.http.Method;
import org.jdesktop.http.Parameter;
import org.jdesktop.http.async.AsyncHttpRequest.ReadyState;

/**
 * Runs some basic tests and demos of AsyncHttpRequest. Exercises the headers,
 * parameters, async flags, timeout, and other such things.
 *
 * @author rbair
 */
public class AsyncTest {
    /** Creates a new instance of AsyncTest */
    private AsyncTest() {}
    
    public static void main(String[] args) {
        testSimpleForm2();
    }
    
    public static void simpleTest() {
        final AsyncHttpRequest req = new AsyncHttpRequest();
        req.open(Method.GET, "http://www.google.com");
        req.setOnReadyStateChange(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (req.getReadyState() == ReadyState.LOADED) {
                    System.out.println("Loaded!");
                }
            }
        });
        req.send();
    }
    
    public static void testSimpleParameter() {
        final AsyncHttpRequest req = new AsyncHttpRequest();
        req.open(Method.GET, "http://www.jroller.com/page/jmn");
        req.setParameter(new Parameter("entry", "swingx_wants_you"));
        req.setOnReadyStateChange(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (req.getReadyState() == ReadyState.LOADED) {
                    System.out.println(req.getResponseText());
                }
            }
        });
        req.send();
    }
    
    public static void testSimpleHeader() {
        final AsyncHttpRequest req = new AsyncHttpRequest();
        req.open(Method.GET, "http://www.google.com");
        req.setRequestHeader("Content-Type", "text");
        req.setOnReadyStateChange(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (req.getReadyState() == ReadyState.LOADED) {
                    System.out.println(req.getResponseHeader("Content-Type"));
                }
            }
        });
        req.send();
    }
    
    public static void testSimpleForm() {
        final AsyncHttpRequest req = new AsyncHttpRequest();
        req.open(Method.GET, "http://www.google.com/search");
        req.setOnReadyStateChange(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (req.getReadyState() == ReadyState.LOADED) {
                    System.out.println(req.getResponseText());
                }
            }
        });
        req.setParameter("h1", "en");
        req.setParameter("q", "SwingLabs");
        req.send();
    }

    public static void testSimpleForm2() {
        Input lang = new DefaultInput("h1", "en");
        Input query = new DefaultInput("q", "SwingLabs");
        Form form = new AbstractForm("http://www.google.com", lang, query) {
            public String getAction() {return "/search";}
            public Method getMethod() {return Method.GET;}
        };
        
        final AsyncHttpRequest req = new AsyncHttpRequest();
        req.open(form.getMethod(), form.getBaseUrl() + form.getAction());
        req.setParameters(Forms.getParameters(form));
        req.setOnReadyStateChange(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (req.getReadyState() == ReadyState.LOADED) {
                    System.out.println(req.getResponseText());
                }
            }
        });
        req.send();
    }
}
