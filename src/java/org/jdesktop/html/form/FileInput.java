/*
 * $Id: FileInput.java 279 2008-03-19 21:06:20Z rbair $
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

/**
 * Represents an Input for Files.
 * 
 * @author Richard
 */
public interface FileInput extends Input {
    /**
     * Gets the content type of the File. This must be formatted as a mime type,
     * and be something reasonable such as text/plain, text/xml, or image/png
     * 
     * @return The content type represented by this FileInput.
     */
    public String getContentType();
    
    /**
     * The filename associated with this file. This may be null, but it is strongly
     * encouraged to provide a file name for uploaded files.
     * 
     * @return the name of the file being uploaded
     */
    public String getFilename();
    
    /**
     * Returns the value represented by this FileItem as an input stream.
     * If null, then the stream is ignored when constructing the response. If
     * a bad file was chosen, then this value may be null.
     * 
     * @return
     */
    public InputStream getValueStream();
}
