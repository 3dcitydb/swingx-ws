/*
 * $Id: Input.java 134 2006-12-19 18:39:48Z rbair $
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
 * <p>An input value on a {@link Form}. This corrosponds to an &lt;input&gt; sub
 * element of a &lt;form&gt; element.</p>
 * 
 * <p>The <code>name</code> property is read only, whereas the value (which changes
 * frequently) is read-write.</p>
 * 
 * @author rbair
 */
public interface Input {
    /**
     * Gets the name of this Input. 
     * 
     * @return the name. This will never be null.
     */
    public String getName();
    
    /**
     * Gets the value of the Input.
     * 
     * @return the value. This may be null.
     */
    public String getValue();
    
    /**
     * Sets the value of the Input. This value may be null.
     * 
     * @param value the value. May be null.
     */
    public void setValue(String value);
}
