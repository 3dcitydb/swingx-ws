/*
 * $Id: Form.java 283 2008-03-19 21:23:13Z rbair $
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

import org.jdesktop.http.Method;

/**
 * Represents the model portion of the HTML &lt;form&gh; element. It does not
 * represent any information related to the structure of the HTML document (for
 * example, there is no id or name property), or any visual information.
 * 
 * @author rbair
 */
public interface Form {
    /**
     * Returns the base url. The getAction() method is appended to this base
     * url to form the target url, if the action is a relative url.
     *
     * @return a string representing the base url, or null if there is not one.
     */
    public String getBaseUrl();
    /**
     * Returns the url that should be used when the form is submitted.
     * 
     * @return a string representing the url to use. This may be a relative
     *         or absolute url.
     */
    public String getAction();
    
    /**
     * Gets the HTTP {@link Method} to use when the form is submitted.
     * 
     * @return the <code>Method</code> to use on submit.
     */
    public Method getMethod();
    
    /**
     * Gets the array of {@link Input}s for this <code>Form</code>.
     * 
     * @return an array of <code>Input</code>s. The ordering of elements in
     *         this array is not guaranteed.
     */
    public Input[] getInputs();
    
    /**
     * Gets the <code>Input</code> with the given name. If more than one 
     * <code>Input</code> has this name, it is unspecified which will be returned.
     * 
     * @return the <code>Input</code> with the given name. May be null.
     */
    public Input getInput(String name);
    
    /**
     * Returns the {@link Encoding} used with this Form. By default, this is
     * UrlEncoded. The encoding is honored when submitting the form.
     * 
     * @return a non-null value for the Encoding for this Form.
     */
    public Encoding getEncoding();
}
