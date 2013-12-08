/*
 * $Id: AbstractForm.java 278 2008-03-19 21:05:31Z rbair $
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.jdesktop.beans.AbstractBean;
import org.jdesktop.http.Response;
import org.jdesktop.http.Session;
import org.jdesktop.http.Parameter;

/**
 * An abstract implementation of the Form interface.
 * 
 * @author rbair
 */
public abstract class AbstractForm extends AbstractBean implements Form {
    private Set<Input> inputs = new HashSet<Input>();
    private String baseUrl;
    private Encoding encoding; //this is initialized in the various constructors to be not null

    /**
     * Create a new AbstractForm with the given baseUrl and set of Inputs.
     * The baseUrl is necessary for evaluating action urls that are relative.
     * 
     * @param baseUrl the base url. May be null (but then relative action paths
     *        will fail).
     * @param inputs the set of inputs. May be null.
     */
    protected AbstractForm(String baseUrl, Set<Input> inputs) {
        this.baseUrl = baseUrl;
        this.encoding = Encoding.UrlEncoded;
        this.inputs.addAll(inputs);
    }
    
    /**
     * Create a new AbstractForm with the given baseUrl and array of Inputs.
     * The baseUrl is necessary for evaluating action urls that are relative.
     * 
     * @param baseUrl the base url. May be null (but then relative action paths
     *        will fail).
     * @param inputs the array of inputs. May be null.
     */
    protected AbstractForm(String baseUrl, Input... inputs) {
        this.baseUrl = baseUrl;
        this.encoding = Encoding.UrlEncoded;
        if (inputs != null) {
            this.inputs.addAll(Arrays.asList(inputs));
        }
    }
    
    /**
     * Create a new AbstractForm with the given baseUrl and array of Inputs.
     * The baseUrl is necessary for evaluating action urls that are relative.
     * 
     * @param baseUrl the base url. May be null (but then relative action paths
     *        will fail).
     * @param encoding the encoding to use. If null, then UrlEncoded is used
     * @param inputs the array of inputs. May be null.
     */
    protected AbstractForm(String baseUrl, Encoding enc, Input... inputs) {
        this.baseUrl = baseUrl;
        this.encoding = enc == null ? Encoding.UrlEncoded : enc;
        if (inputs != null) {
            this.inputs.addAll(Arrays.asList(inputs));
        }
    }
    
    /**
     * @inheritDoc
     */
    public Input[] getInputs() {
        return inputs.toArray(new Input[0]);
    }
    
    /**
     * @inheritDoc
     */
    public Input getInput(String name) {
        for (Input i : inputs) {
            if (name.equals(i.getName())) {
                return i;
            }
        }
        return null;
    }
    
    /**
     * @inheritDoc
     */
    public Parameter[] getInputParameters() {
        return Forms.getParameters(this);
    }
    
    /**
     * @inheritDoc
     */
    public Response submit(Session session) throws Exception {
        return Forms.submit(this, session);
    }
    
    /**
     * @inheritDoc
     */
    public String getBaseUrl() {
        return baseUrl;
    }
    
    /**
     * @inheritDoc
     */
    public Encoding getEncoding() {
        return encoding;
    }
}
