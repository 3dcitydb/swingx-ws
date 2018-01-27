/*
 * $Id: Forms.java 283 2008-03-19 21:23:13Z rbair $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jdesktop.html.form;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdesktop.dom.SimpleDocument;
import org.jdesktop.http.Method;
import org.jdesktop.http.Parameter;
import org.jdesktop.http.Response;
import org.jdesktop.http.Session;
import org.w3c.dom.Node;

/**
 * <p>Utility class used for automatically creating a Form instance directly from
 * an HTTP {@link org.jdesktop.http.Response}. Also contains utility methods for
 * encoding form bodies for transmission, and other Form related functions.</p>
 *
 * <p>Because HTML is not always well formed, this utility class does not support
 * parsing HTML directly. Rather, it supports parsing HTML documents which have
 * been previously made well-formed. It is then parsed just as any XML document.</p>
 *
 * <p>Those methods which take a Response directly will take the response text
 * and pass it through some routines to attempt to clean the HTML. If this fails,
 * parsing will fail.</p>
 *
 * <p>If you want to run your own cleanup routines, do so prior to calling one of
 * the methods which take a SimpleDocument.</p>
 *
 * @author rbair
 */
public final class Forms {
    private Forms() {}

    /**
     * Gets a {@link Form} from the given DOM Document, based on the given
     * XPath expression.
     *
     * @param dom the DOM Document to parse the HTML Form from.
     * @param baseUrl the baseUrl which generated this DOM document. This is required
     *                if you later want to submit the HTML Form, since url's in the
     *                Form may be relative.
     * @param expression the XPath expression to use to find the Form tag to be parsed.
     */
    public static Form getForm(SimpleDocument dom, final String baseUrl, String expression) {
        try {
            //find the <form> element with the specified id
            Node n = dom.getElement(expression);
            if (n != null) {
                final String name = dom.getString("@name", n);
                final String action = dom.getString("@action", n);
                final String method = dom.getString("@method", n);

                Set<Input> inputs = new HashSet<Input>();
                Map<String, Set<String>> radioInputs = new HashMap<String, Set<String>>();
                //now dig out all of the input fields
                for (Node inputNode : dom.getElements(expression + "//input | " + expression + "//select")) {
                    /*
                     * If the input type is "textfield" or "checkbox" or "password"
                     *      no need to do anything
                     * If the input type is "radiobutton"
                     *      collesce it with all other "radiobutton" types with
                     *      the same name
                     * If the node name is "select"
                     *      Save the "name" attribute, and "id" if there is one
                     *      Dig out the values for each of the <option> sub tags
                     */

                    if ("input".equalsIgnoreCase(inputNode.getNodeName())) {
                        final String inputName = dom.getString("@name", inputNode);
                        final String inputValue = dom.getString("@value", inputNode);
                        String inputType = dom.getString("@type", inputNode);

                        if ("radio".equals(inputType)) {
                            Set<String> values = radioInputs.get(inputName);
                            if (values == null) {
                                values = new HashSet<String>();
                                radioInputs.put(inputName, values);
                            }
                            values.add(inputValue);
                        } else {
                            inputs.add(new Input() {
                                private String value = inputValue;
                                public String getName() { return inputName; }
                                public String getValue() { return value; }
                                public void setValue(String value) { this.value = value; }
                            });
                        }
                    } else {
                        final String selectName = dom.getString("@name", inputNode);
                        String selected = null;
                        Set<String> options = new HashSet<String>();
                        for (Node optionValue : dom.getElements("option/@value", inputNode)) {
                            String optionSelected = dom.getString("@selected", optionValue);
                            if ("selected".equals(optionSelected)) {
                                selected = optionValue.getTextContent();
                            }
                            options.add(optionValue.getTextContent());
                        }
                        Select select = new AbstractSelect(selectName, options) {};
                        if (selected != null) {
                            select.setValue(selected);
                        }
                        inputs.add(select);
                    }
                }

                //now add all the form inputs for the radios
                for (String radName : radioInputs.keySet()) {
                    inputs.add(new AbstractRadioInput(radName, radioInputs.get(radName)));
                }

                return new AbstractForm(baseUrl, inputs) {
                    public String getAction() { return action; }
                    public Method getMethod() { return Method.valueOf(method.toUpperCase()); }
                    public String getName() { return name; }
                };
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Gets a {@link Form} from the given DOM Document by finding the Form in
     * the document which has a name attribute equal to the given <code>name</code>
     * parameter.
     *
     * @param dom the DOM Document to parse the HTML Form from.
     * @param baseUrl the baseUrl which generated this DOM document. This is required
     *                if you later want to submit the HTML Form, since url's in the
     *                Form may be relative.
     * @param name the name of the Form element within the DOM which should be parsed
     */
    public static Form getFormByName(SimpleDocument dom, String baseUrl, String name) {
        return getForm(dom, baseUrl, "//form[@name='" + name + "']");
    }

    /**
     * Gets a {@link Form} from the given DOM Document by finding the
     * <code>index</code>'th Form in the document.
     *
     * @param dom the DOM Document to parse the HTML Form from.
     * @param baseUrl the baseUrl which generated this DOM document. This is required
     *                if you later want to submit the HTML Form, since url's in the
     *                Form may be relative.
     * @param index the index of the Form to parse. For example, if there are 3 &lt;form&gt;
     *        tags in the document, and <code>index</code> is 2, then the second form will be
     *        selected. <code>index</code> is 1 based.
     */
    public static Form getFormByIndex(SimpleDocument dom, String baseUrl, int index) {
        return getForm(dom, baseUrl, "//form[" + index + "]");
    }

    /**
     * Gets a {@link Form} from the given DOM Document by finding the Form in
     * the document which has an id attribute equal to the given <code>id</code>
     * parameter.
     *
     * @param dom the DOM Document to parse the HTML Form from.
     * @param baseUrl the baseUrl which generated this DOM document. This is required
     *                if you later want to submit the HTML Form, since url's in the
     *                Form may be relative.
     * @param id the id of the Form element within the DOM which should be parsed
     */
    public static Form getFormById(SimpleDocument dom, String baseUrl, String formId) {
        return getForm(dom, baseUrl, "//form[@id='" + formId + "']");
    }

    /**
     * Given a {@link Form}, return the Parameters that would be used to construct
     * an HTTP request for this form.
     *
     * @return the array of Parameters needed to construct an HTTP request for this form
     * @param f the Form to use. If null, an empty parameter array is returned.
     */
    public static Parameter[] getParameters(Form f) {
        List<Parameter> params = new ArrayList<Parameter>();
        for (Input input : f.getInputs()) {
            if (input.getName() != null && input.getValue() != null) {
                params.add(new Parameter(input.getName(), input.getValue()));
            }
        }
        return params.toArray(new Parameter[0]);
    }

    /**
     * Submits this form using the given {@link Session}. This method blocks.
     * 
     * @param session the Session to execute this form through. This must not be
     *        null.
     * @return The {@link Response} for this form submition.
     * @throws Exception if some client error occurs while processing the submition.
     */
    public static Response submit(Form f, Session session) throws Exception {
        String url = f.getAction();
        String baseUrl = f.getBaseUrl();
        if (!isValidUrl(url)) {
            //try to fix the url using baseUrl
            if (baseUrl != null) {
                if (url.startsWith("/")) {
                    String protocol = baseUrl.substring(0, baseUrl.indexOf("//")) + "//";
                    String restUrl = baseUrl.substring(baseUrl.indexOf("//") + 2);
                    String newBaseUrl = restUrl.substring(0, restUrl.indexOf("/"));
                    url = protocol + newBaseUrl + url;
                } else {
                    String s = baseUrl;
                    int index = s.indexOf('?');
                    if (index > 0) {
                        s = s.substring(0, index);
                        index = s.lastIndexOf("/");
                        url = s.substring(0, index) + "/" + url;
                    } else {
                        url = s + "/" + url;
                    }
                }
            }
        }

        if (!isValidUrl(url)) {
            throw new IllegalStateException("The action url: '" + url +
                    "' does not form a valid url, even when combined with the " +
                    "base url '" + baseUrl + "'");
        }

        FormRequest req = new FormRequest();
        req.setMethod(f.getMethod());
        req.setUrl(url);
        for (Input i : f.getInputs()) {
            if (i instanceof FileInput) {
                final FileInput fi = (FileInput)i;
                FileParameter param = new FileParameter() {
                    @Override public InputStream getValueStream() {
                        return fi.getValueStream();
                    }
                };
                param.setName(fi.getName());
                param.setFilename(fi.getFilename());
                param.setValue(fi.getFilename());
                param.setContentType(fi.getContentType());
                req.setFormParameter(param);
            } else {
                Parameter param = new Parameter(i.getName(), i.getValue());
                req.setFormParameter(param);
            }
        }
        
        return session.execute(req);
    }

    private static boolean isValidUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
