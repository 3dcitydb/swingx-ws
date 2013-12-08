/*
 * $Id: AbstractSelect.java 89 2006-09-29 23:34:29Z rbair $
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.jdesktop.beans.AbstractBean;

/**
 * Abstract implementation of the {@link Select} interface.
 * 
 * @author rbair
 */
public abstract class AbstractSelect extends AbstractBean implements Select {
    private Set<String> values = new HashSet<String>();
    private String name;
    private String selected;
    
    /** 
     * Creates a new instance of AbstractSelect.
     * 
     * @param name the name, corrosponding to the "name" property of Input.
     * @param values the set of acceptable values
     */
    protected AbstractSelect(String name, Set<String> values) {
        this.name = name;
        this.values.addAll(values);
    }

    /**
     * @inheritDoc
     */
    public String[] getOptions() {
        return values.toArray(new String[0]);
    }

    /**
     * @inheritDoc
     */
    public String getName() {
        return name;
    }

    /**
     * @inheritDoc
     */
    public String getValue() {
        return selected;
    }
    
    /**
     * @inheritDoc
     */
    public void setValue(String value) {
        if (values.contains(value)) {
            selected = value;
        } else {
            throw new IllegalArgumentException("Value '" + value + 
                    "' is not in the set of acceptable values " + new ArrayList(values));
        }
    }
}
