/*
 * $Id: YahooVideoSearchBeanInfo.java 37 2006-07-17 20:30:21Z rbair $
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

package org.jdesktop.swingx.ws.yahoo.search.videosearch;

import java.beans.BeanDescriptor;

import org.jdesktop.beans.BeanInfoSupport;
import org.jdesktop.beans.EnumerationValue;
import org.jdesktop.swingx.ws.yahoo.search.Utils;

/**
 *
 * @author rbair
 */
public class YahooVideoSearchBeanInfo extends BeanInfoSupport {
    
    /**
     * Creates a new instance of YahooVideoSearchBeanInfo
     */
    public YahooVideoSearchBeanInfo() {
        super(YahooVideoSearch.class);
    }

    protected void initialize() {
        BeanDescriptor bd = getBeanDescriptor();
        bd.setName("Yahoo! Video Search");
        bd.setShortDescription("A JavaBean for searching for video's using Yahoo!");
        
        setHidden(true, "propertyChangeListeners", "class");
        setEnumerationValues(getFileFormatEnumValues(), "fileFormat");
        setEnumerationValues(Utils.getTypeEnumValues(), "type");
    }
    
    public static EnumerationValue[] getFileFormatEnumValues() {
        FileFormat[] values = FileFormat.values();
        EnumerationValue[] results = new EnumerationValue[values.length+1];
        results[0] = new EnumerationValue("<default>", FileFormat.MPEG, "null");
        for (int i=0; i<values.length; i++) {
            results[i+1] = new EnumerationValue(values[i].
                    name(), values[i], "org.jdesktop.swingx.ws.yahoo.search.videosearch.FileFormat" + values[i].name());
        }
        return results;
    }
}