/*
 * FormSubmitter.java
 *
 * Created on August 3, 2006, 3:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx;

import java.awt.Component;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.text.JTextComponent;
import org.jdesktop.http.async.AsyncHttpRequest;
import org.jdesktop.html.form.Input;
import org.jdesktop.http.Method;

/**
 * This widget allows a Swing UI (or other Java code) to simulate and send an
 * HTML submit.
 * 
 * The JXHtmlForm iterates through all of its child components, looking for
 * any components that have a FormSubmitter.FORM_ID client property. If it finds
 * one, it includes the component's data in the submit.
 * 
 * <b>Requires the optional libraries HTTP-Client, Commons-Codec, Commons-Logging</b>
 * @author rbair
 */
public class JXHtmlForm extends JPanel {
    public static final String NAME = "JXHtmlForm-form-name-id";
    public static final String VALUE = "JXHtmlForm-form-value-id";
    
    private URI action;
    private Method method;
    
    private static void blar() throws URISyntaxException {
        JXHtmlForm form = new JXHtmlForm("http://vin.stronghold.com/VINResult.aspx",
                Method.GET);
        /*
        JTextField tf = new JTextField();
        form.add(tf);
        form.addInput(tf, "firstname");
        
        tf = new JTextField();
        form.add(tf);
        form.addInput(tf, "lastname");
        */
        
        /*
        JTextField tf = new JTextField();
        tf.putClientProperty(JXHtmlForm.NAME, "firstname");
        form.add(tf);
        
        tf = new JTextField();
        tf.putClientProperty(JXHtmlForm.NAME, "lastname");
        form.add(tf);
        */
        
        /*
        JTextField tf = new JTextField();
        tf.setInputName("firstname");
        form.add(tf);
        
        tf = new JTextField();
        tf.setInputName("lastname");
        form.add(tf);
        */
        
        /*
        ButtonGroup bg = new ButtonGroup();
        JRadioButton rb = new JRadioButton("Male");
        bg.add(rb);
        form.add(rb);
        form.addInput(rb, "sex", "male");
        
        rb = new JRadioButton("Female");
        bg.add(rb);
        form.add(rb);
        form.addInput(rb, "sex", "female");
        */
        
        /*
        ButtonGroup bg = new ButtonGroup();
        JRadioButton rb = new JRadioButton("Male");
        rb.putClientProperty(JXHtmlForm.NAME, "sex");
        rb.putClientProperty(JXHtmlForm.VALUE, "male");
        bg.add(rb);
        form.add(rb);
        
        rb = new JRadioButton("Female");
        rb.putClientProperty(JXHtmlForm.NAME, "sex");
        rb.putClientProperty(JXHtmlForm.VALUE, "female");
        bg.add(rb);
        form.add(rb);
        */
        
        /*
        ButtonGroup bg = new ButtonGroup();
        JRadioButton rb = new JRadioButton("Male");
        rb.setInputName("sex");
        rb.setInputValue("male");
        bg.add(rb);
        form.add(rb);
        
        rb = new JRadioButton("Female");
        rb.setInputName("sex");
        rb.setInputValue("female");
        bg.add(rb);
        form.add(rb);
        */
        
        /*
        JCheckBox cb = new JCheckBox("I have a bike");
        form.add(cb);
        form.addInput(cb, "bike"); //name
        
        cb = new JCheckBox("I have a car");
        form.add(cb);
        form.addInput(cb, "car"); //name
        */
        
        /*
        JCheckBox cb = new JCheckBox("I have a bike");
        cb.putClientProperty(JXHtmlForm.NAME, "bike");
        form.add(cb);
        
        cb = new JCheckBox("I have a car");
        cb.putClientProperty(JXHtmlForm.NAME, "car");
        form.add(cb);
        */
        
        /*
        JCheckBox cb = new JCheckBox("I have a bike");
        cb.setInputName("bike");
        form.add(cb);
        
        cb = new JCheckBox("I have a car");
        cb.setInputName("car");
        form.add(cb);
        */
        
        /*
        JComboBox cb = new JComboBox(new Object[] {
            "Volvo", "Saab", "Fiat", "Audi"});
        form.add(cb);
        form.addInput(cb, "cars", new JXHtmlForm.Select(
                new JXHtmlForm.Option("Volvo", "volvo"),
                new JXHtmlForm.Option("Saab", "saab"),
                new JXHtmlForm.Option("Fiat", "fiat"),
                new JXHtmlForm.Option("Audi", "audi")
                ));
        */

        /*
        JComboBox cb = new JComboBox(new Object[] {
                new JXHtmlForm.Option("volvo", "Volvo"),
                new JXHtmlForm.Option("saab", "Saab"),
                new JXHtmlForm.Option("fiat", "Fiat"),
                new JXHtmlForm.Option("audi", "Audi")});
        cb.putClientProperty(JXHtmlForm.NAME, "cars");
        form.add(cb);
        */
        
        /*
        JComboBox cb = new JComboBox(new Object[] {
                new JXHtmlForm.Option("volvo", "Volvo"),
                new JXHtmlForm.Option("saab", "Saab"),
                new JXHtmlForm.Option("fiat", "Fiat"),
                new JXHtmlForm.Option("audi", "Audi")});
        cb.setInputName("cars");
        form.add(cb);
        */
        
        //Rules:
        //To determine if a component participates: it will have a client property
        //value for JXHtmlForm.NAME OR a non-null value for component.getName().
        //
        //To extract the value:
        //First, check to see if there is a JXHtmlForm.VALUE client property. If
        //there is, use it.
        //If not, check to see if the component implements FormInput. If it does,
        //ask for the value.
        //If not, check to see if the component is one of the known types. If it
        //is a text component, extract the text as the value. Etc for the other
        //basic types. If it is a JList or JComboBox, get the selected value. If
        //it is of type JXHtmlForm.Option, get the value. Otherwise, call toString
        //on it.
        //If the component is of an unknown type, then do not include this key/value pair.
    }
    
    /** Creates a new instance of FormSubmitter */
    public JXHtmlForm() {
        method = Method.GET;
    }
    
    public JXHtmlForm(String actionUri, Method method) throws URISyntaxException {
        setAction(new URI(actionUri));
        setMethod(method);
    }
    
    public void setAction(URI action) {
        URI old = getAction();
        this.action = action;
        firePropertyChange("action", old, getAction());
    }
    
    public URI getAction() {
        //TODO may need to create a copy
        return action;
    }
    
    public void setMethod(Method m) {
        if (m == null) {
            throw new NullPointerException("Method must be GET or POST");
        }
        
        Method old = getMethod();
        this.method = m;
        firePropertyChange("method", old, getMethod());
    }
    
    /**
     * @return the method. Must not be null.
     */
    public Method getMethod() {
        return method;
    }
    
    public List<Parameter> getParameters() {
        List<Parameter> params = new ArrayList<Parameter>();
        
        for (Component c : getComponents()) {
//            if (c instanceof MultiFormInput) {
//                params.addAll(((MultiFormInput)c).getParameters());
//            } else {
                String name = c.getName();
                String value = c instanceof Input ? ((Input)c).getValue() : null;

                if ((name == null || value == null) && c instanceof JComponent) {
                    //try the JComponent path
                    JComponent comp = (JComponent)c;
                    if (name == null) {
                        name = (String)comp.getClientProperty(JXHtmlForm.NAME);
                    }
                    if (name != null && value == null) {
                        //go ahead and try to get a value
                        //try to see if comp is one of the known types
                        if (comp instanceof AbstractButton) {
                            boolean selected = ((AbstractButton)comp).isSelected();
                            if (selected) {
                                value = (String)comp.getClientProperty(JXHtmlForm.VALUE);
                            }
                        } else if (comp instanceof JPasswordField) {
                            value = new String(((JPasswordField)comp).getPassword());
                        } else if (comp instanceof JTextComponent) {
                            value = ((JTextComponent)comp).getText();
                        } else if (comp instanceof JList) {
                            Object sel = ((JList)comp).getSelectedValue();
                            if (sel != null) {
                                value = sel instanceof Option ? ((Option)sel).getValue() : sel.toString();
                            }
                        } else if (comp instanceof JComboBox) {
                            Object sel = ((JComboBox)comp).getSelectedItem();
                            if (sel != null) {
                                value = sel instanceof Option ? ((Option)sel).getValue() : sel.toString();
                            }
                        } else {
                            value = (String)comp.getClientProperty(JXHtmlForm.VALUE);
                        }
                    }

                    if (name != null && value != null) {
                        params.add(new Parameter(name, value));
                    }
                }
//            }
        }
        
        return params;
    }
    
    //use the given HttpRequest for sending this request asynchronously
    public void submit(AsyncHttpRequest req) throws Exception {
        URI uri = getAction();
        if (uri != null) {
            //construct the HTML form submit, and submit it
            req.open(getMethod(), uri.toString());
            for (Parameter p : getParameters()) {
                req.setParameter(p.getName(), p.getValue());
            }
            req.send();
        }
    }
    
    public String submit() throws Exception {
        URI uri = getAction();
        if (uri != null) {
            //construct the HTML form submit, and submit it
            AsyncHttpRequest req = new AsyncHttpRequest();
            req.open(getMethod(), uri.toString(), false);
            for (Parameter p : getParameters()) {
                req.setParameter(p.getName(), p.getValue());
            }
            req.send();
            return req.getResponseText();
        }
        return null;
    }
    
    public static class Option {
        private String value;
        private String display;
        
        public Option(String value, String display) {
            this.value = value;
            this.display = display;
        }
        
        public String getValue() {
            return value;
        }
        
        public String toString() {
            return display;
        }
    }
    
    public static final class Parameter {
        private String name;
        private String value;
        
        Parameter(String n, String v) {
            this.name = n;
            this.value = v;
        }
        
        public String getName() {
            return name;
        }
        
        public String getValue() {
            return value;
        }
        
        public String toParamString() {
            try {
                return URLEncoder.encode(name, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
                return name + "=" + value;
            }
        }
    }
}
