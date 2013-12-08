/*
 * $Id: DefaultInput.java 147 2006-12-19 23:36:48Z rbair $
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

import org.jdesktop.beans.AbstractBean;

/**
 * A default implementation of the Input interface.
 *
 * @author rbair
 */
public class DefaultInput extends AbstractBean implements Input {
    private String name;
    private String value;
    
    /** Creates a new instance of DefaultInput */
    public DefaultInput() {
    }
    
    public DefaultInput(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        String old = getName();
        this.name = name;
        firePropertyChange("name", old, getName());
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        String old = getValue();
        this.value = value;
        firePropertyChange("value", old, getValue());
    }
}
