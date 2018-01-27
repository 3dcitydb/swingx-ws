/*
 * $Id: Select.java 134 2006-12-19 18:39:48Z rbair $
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
 * <p>Represents a combobox/list type input field.</p>
 * 
 * <p>An HTML form with such a combo might look like:
 * <pre><code>
 *  &lt;form&gt;
 *      &lt;select name="letter"&gt;
 *          &lt;option value="a" /&gt;
 *          &lt;option value="b" /&gt;
 *          &lt;option value="c" selected="selected" /&gt;
 *          &lt;option value="d" /&gt;
 *          &lt;option value="e" /&gt;
 *      &lt;/select&gt;
 *  &lt;/form&gt;
 * </code></pre></p>
 * 
 * <p>The above block of HTML would corrospond to a <code>Select</code> with the
 * following values:
 * <ul>
 *      <li>select.getOptions(): [a, b, c, d, e]</li>
 *      <li>select.getName(): "letter"</li>
 *      <li>select.getValue(): c</li>
 * </ul></p>
 * 
 * <p>Note: There is essentially no difference between this interface and the RadioInput
 * interface. HTML separates them for presentation purposes. So do we. By having
 * a separate interface we don't lose important information from within the HTML
 * form. We can use this information to auto-create Swing UI representations.</p>
 * 
 * @author rbair
 */
public interface Select extends Input {
    /**
     * Gets the set of valid values. Any call to #setValue(String) must reference
     * one of these.
     * 
     * @return the array of valid values.
     */
    public String[] getOptions();
}
