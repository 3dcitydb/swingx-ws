/*
 * $Id: RadioInput.java 134 2006-12-19 18:39:48Z rbair $
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

/**
 * <p>Represents a collection of HTML "radio" type input fields with the same name.</p>
 * 
 * <p>An HTML form with such inputs might look like:
 * <pre><code>
 *  &lt;form&gt;
 *      &lt;input type="radio" name="letter" value="a" /&gt;
 *      &lt;input type="radio" name="letter" value="b" /&gt;
 *      &lt;input type="radio" name="letter" value="c" checked="checked" /&gt;
 *      &lt;input type="radio" name="letter" value="d" /&gt;
 *      &lt;input type="radio" name="letter" value="e" /&gt;
 *  &lt;/form&gt;
 * </code></pre></p>
 * 
 * <p>The above block of HTML would corrospond to a <code>RadioInput</code> with the
 * following values:
 * <ul>
 *      <li>radioInput.getValues(): [a, b, c, d, e]</li>
 *      <li>radioInput.getName(): "letter"</li>
 *      <li>radioInput.getValue(): c</li>
 * </ul></p>
 * 
 * @author rbair
 */
public interface RadioInput extends Input {
    /**
     * Gets the set of valid values. Any call to #setValue(String) must reference
     * one of these.
     * 
     * @return the array of valid values.
     */
    public String[] getValues();
}
