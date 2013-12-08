/*
 * $Id: FileParameter.java 279 2008-03-19 21:06:20Z rbair $
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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import org.jdesktop.http.Parameter;

/**
 * <p>A special {@link Parameter} intended to be used as a Form Parameter on a
 * FormRequest for uploading files.</p>
 * 
 * <p>FileParameter adds the contentType and fileName properties. These properties
 * are used to construct the multipart/form-data body of a form request. It is
 * important that the fileName refer to an actual existing file object, and that
 * the contentType be accurate. If either of these two constraints are not met,
 * then the request will likely fail.</p>
 * 
 * <p>The fileName must be formatted such that creating a new {@link java.io.File}
 * will succeed in locating the file. We use the same rules as the URLConnection
 * class for determining the contentType automatically for the file. In the common
 * case, it will be unnecessary to indicate the contentType manually. However,
 * in special cases you may need to specify the content type manually. The format
 * of the contentType is a mime type, such as text/plain.</p>
 * 
 * <p>If the fileName is correctly specified, then the InputStream returned from
 * getValueStream() will be non-null. You are responsible for closing this
 * stream when finished reading from it.</p>
 * 
 * <p>The "value" of this Parameter is specified to be the fileName. This is
 * enforced by overridding setValue to be a null-op, and by setFilename() calling
 * super.setValue().</p>
 * 
 * @author Richard
 */
public class FileParameter extends Parameter {
    private static FileNameMap MIMES;
    private String contentType;
    private String fileName;
    private boolean contentTypeSetManually = false;

    /**
     * Create a new FileParameter. You will need to specify a name and fileName
     * at some point for this parameter to be useful.
     */
    public FileParameter() { }
    
    /**
     * Create a new FileParameter using the given name as the name of this
     * parameter (the key, if you will), and the given File as the file to
     * be uploaded by the FormRequest.
     * 
     * @param name
     * @param file may be null.
     */
    public FileParameter(String name, File file) {
        this(name, file == null ? null : file.getAbsolutePath());
    }
    
    /**
     * Create a new FileParameter using the given name as the name of this
     * parameter (the key if you will) and the given fileName as the absolute
     * path to the file to be uploaded by the FormRequest.
     * 
     * @param name
     * @param fileName may be null.
     */
    public FileParameter(String name, String fileName) {
        super(name, fileName);
        this.fileName = fileName;
        contentType = computeMimeType(fileName);
    }
    
    /**
     * Sets the mime-type of the content being uploaded. Generally this is
     * determined automatically when the fileName is set. In rare cases where
     * the mime-type cannot be determined, or when you want to force the mime
     * type, you can set the content type manually with this method.
     * 
     * @param contentType if set to null, then the mime type "content/unknown" is
     * used.
     */
    public void setContentType(String contentType) {
        String old = this.contentType;
        this.contentType = contentType == null ? "content/unknown" : contentType;
        contentTypeSetManually = contentType != null;
        firePropertyChange("contentType", old, this.contentType);
    }
    
    /**
     * Gets the content type of the data being uploaded. This is never null, and
     * defaults to "content/unknown" when the mime type of the data couldn't be
     * determined and was not set manually.
     * 
     * @return
     */
    public final String getContentType() {
        return contentType;
    }

    /**
     * Sets the absolute path to the file to be uploaded. If null, then the
     * filename is cleared and the parameter becomes useless for uploading.
     * Invoking this method causes the content type to be set automatically,
     * unless it has been set manually.
     * 
     * @param name
     */
    public void setFilename(String name) {
        String old = fileName;
        this.fileName = name;
        firePropertyChange("filename", old, this.fileName);
        super.setValue(this.fileName);
        if (!contentTypeSetManually)
            setContentType(computeMimeType(fileName));
    }
    
    /**
     * Gets the absolute path of the file being uploaded.
     * @return
     */
    public final String getFilename() {
        return fileName;
    }
    
    /*
     * overridden to disallow clients from setting the value manually.
     * They must set the filename instead.
     */
    @Override public void setValue(String value) {
        //no-op
    }

    /**
     * Creates and returns a new InputStream based on the underlying file
     * data. If it is not possible to create such an InputStream due to an
     * incorrect or missing file, then null is returned.
     * 
     * @return
     */
    public InputStream getValueStream() {
        if (fileName == null) return null;
        try {
            return new FileInputStream(fileName);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override public FileParameter clone() {
        FileParameter clone = new FileParameter(getName(), fileName);
        clone.contentType = contentType;
        return clone;
    }
    
    /**
     * Static utility method for computing the mime type based on a filename.
     * This is made package private solely for the sake of testing.
     * 
     * @param filename
     * @return
     */
    static String computeMimeType(String filename) {
        try {
            return getMimeMap().getContentTypeFor(filename);
        } catch (Exception e) {
            return "content/unknown";
        }
    }
    
    /**
     * Static utility method for getting the FileNameMap used to map file names
     * to mime types.
     * 
     * @return
     */
    private synchronized static final FileNameMap getMimeMap() {
        if (MIMES == null) {
            MIMES = URLConnection.getFileNameMap();
        }
        return MIMES;
    }
}
