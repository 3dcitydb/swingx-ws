/*
 * $Id: FormRequest.java 294 2008-04-09 21:05:11Z rbair $
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jdesktop.http.Method;
import org.jdesktop.http.Parameter;
import org.jdesktop.http.Request;
import org.w3c.dom.Document;

/**
 * <p>A FormRequest is a Request specifically designed for emulating HTML forms.
 * In particular, a FormRequest does not specifically allow the setting of
 * the body of the Request. Instead, one of several "Form Parameters" are set,
 * which then become the body. Since FormRequest extends request, you can
 * also specify "normal" or "GET" parameters using the setParameter() methods.</p>
 * 
 * <p>When you create a new FormRequest, by default the HTTP method is POST
 * instead of GET (which is the default for Request). In all other ways
 * FormRequest has the same defaults as its parent class.</p>
 * 
 * <p>FormRequest allows you to specify the encoding used for the form. Valid
 * entries are of type {@link Encoding}. The default value is Encoding.UrlEncoding.
 * When uploading files to the server, you would typically want to use
 * Encoding.MultipartFormData.</p>
 * 
 * <p>Here is a typical usage example:</p>
 * 
 * <pre><code>
 *      FormRequest request = new FormRequest("http://www.example.com/doFileUpload");
 *      request.setEncoding(Encoding.MultipartFormData);
 *      request.setFormParameter("username", "richard");
 *      request.setFormParameter("compression", "none");
 *      request.setFormParameter("file", new File("/usr/local/desktop/photo.png"));
 * 
 *      Session s = new Session();
 *      Response response = s.execute(request);
 * </code></pre>
 * 
 * <p>In this example our fictitious doFileUpload resource is expecting a
 * multipart/form-data upload with username, compression, and file parameters
 * in the body of the form. Notice the use of the setFormParameter that takes
 * a File. Invoking setFormParameter in this way constructs a FileParameter
 * and sets it on the FormRequest. See {@link FileParameter} for more
 * details.</p>
 * 
 * <p>Since the body of a FormRequest is comprised exclusively of the content
 * of the Form parameters, the various setBody() methods declared in Request are
 * overridden in this subclass to be no-ops. It is therefore only possible to
 * specify the body by using Form parameters.</p>
 * 
 * @author Richard
 */
public class FormRequest extends Request {
    private List<Parameter> formParams = new ArrayList<Parameter>();
    private Encoding encoding;
    private String boundary = null;
    
    /**
     * Creates a new FormRequest, which defaults its HTTP method to POST.
     */
    public FormRequest() {
        super(Method.POST, null);
        setEncoding(Encoding.UrlEncoded);
    }

    /**
     * Creates a new FormRequest for the specified URL. It defaults its
     * HTTP method to POST.
     * 
     * @param url
     */
    public FormRequest(String url) {
        super(Method.POST, url);
        setEncoding(Encoding.UrlEncoded);
    }
    
    /**
     * Creates a new FormRequest based on the given FormRequest. All data is
     * copied from the src to the new instance, including all form parameters
     * (which comprise the body of the request).
     * 
     * @param source cannot be null.
     */
    public FormRequest(FormRequest source) {
        super(source);
        formParams.addAll(source.formParams);
        setEncoding(Encoding.UrlEncoded);
    }
    
    /**
     * Returns the Form Parameter with the given name, or null if there is no
     * such Form Parameter.
     * 
     * @param name the name to look for. If null, null is returned.
     * @return the Parameter with the given name.
     */
    public final Parameter getFormParameter(String name) {
        if (name == null) return null;
        
        for (Parameter p : formParams) {
            if (name.equals(p.getName())) {
                return p;
            }
        }
        
        return null;
    }
    
    /**
     * Creates a Form Parameter using the given key and value and then
     * adds it to the set of form parameters.
     * 
     * @param key must not be null
     * @param value
     */
    public final void setFormParameter(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        setFormParameter(new Parameter(key, value));
    }
    
    /**
     * Creates a Form Parameter using the given key and File and then
     * adds it to the set of form parameters.
     * 
     * @param key must not be null
     * @param value
     */
    public final void setFormParameter(String key, File file) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        setFormParameter(new FileParameter(key, file));
    }
    
    /**
     * Adds the given parameter to the set of Form parameters.
     * 
     * @param parem the Parameter to add. This must not be null.
     */
    public void setFormParameter(Parameter param) {
        if (param == null) {
            throw new NullPointerException("param cannot be null");
        }
        formParams.add(param);
    }
    
    /**
     * Gets an array of all the Form Parameters for this FormRequest. This
     * array will never be null. Ordering of items is guaranteed based on the
     * order in which the params were added.
     * 
     * @return the array of Parameters for this request
     */
    public final Parameter[] getFormParameters() {
        return formParams.toArray(new Parameter[0]);
    }
    
    /**
     * Sets the Form parameters to use with this FormRequest. This replaces
     * whatever Form parameters may have been previously defined. If null, this
     * array is treated as an empty array, causing the list of Form params to
     * be removed.
     * 
     * @param params the Parameters to set for this Request. May be null.
     */
    public final void setFormParameters(Parameter... params) {
        this.formParams.clear();
        if (params != null) {
            for (Parameter p : params) {
                setFormParameter(p);
            }
        }
    }
    
    /**
     * Gets the encoding to use with this FormRequest. The Encoding will never
     * be null. It specifies how the Form parameters should be encoded into the
     * body of the request.
     * 
     * @return a non-null Encoding for this FormRequest
     */
    public final Encoding getEncoding() {
        return encoding;
    }
    
    /**
     * Specifies the encoding to use with this FormRequest. By default, this
     * property is set to Encoding.UrlEncoded. It cannot ever be set to null.
     * Calling this method with null results in Encoding.UrlEncoded.
     * 
     * @param enc
     */
    public void setEncoding(Encoding enc) {
        Encoding old = encoding;
        encoding = enc == null ? Encoding.UrlEncoded : enc;
        firePropertyChange("encoding", old, encoding);

        if (encoding == Encoding.UrlEncoded) {
            setHeader("Content-Type", encoding.toString());
        } else {
            // this should probably be a truly random sequence, though realistically
            // it is probably easier to debug when using a constant seemingly "random"
            // sequence.
            String randomSequence = "7d44e178b043433ff";
            // again, the boundary length (the number of dashes) could be random,
            // but I don't see a strong reason for it. This is probably good enough.
            boundary = "++-----------------------" + randomSequence;
            // be sure to set the Content-Type header. This has to be set here,
            // as opposed to when the encoding is set, because the boundary is
            // determined here. It could be set when the encoding is set if I
            // made the constant boundary choice permanent.
            setHeader("Content-Type", encoding + "; boundary=" + boundary);
        }
    }

    @Override public void setBody(String body) {}
    @Override public void setBody(byte[] body) {}
    @Override public void setBody(Document body) {}
    @Override public void setBody(InputStream body) {}

    @Override protected InputStream getBody() throws Exception {
        // create a cloned array of all the form parameters. This is necessary
        // because this method may return before the data for all the form
        // params are computed, and we don't want a race condition where
        // the params are modified externally after this call
        final Parameter[] params = getFormParameters();
        for (int i=0; i<params.length; i++) {
            params[i] = params[i].clone();
        }
        
        // if we have a multipart/form-data encoding, then create and return
        // a MultipartInputStream. This input stream is constructed from a series
        // of "chunks", each chunk being either a String or FormParameter
        // (representing a File to be uploaded). In this way, we have a reference
        // to the file to be uploaded embedded in the stream such that the
        // InputStream to the file data is only opened when necessary, when we
        // get to it, and only as much of it is read into memory as necessary.
        if (encoding == Encoding.MultipartFormData) {
            List<Object> chunks = new ArrayList<Object>();
            StringBuilder buffer = new StringBuilder();
            for (int index = 0; index < params.length; index++) {
                if (index > 0) {
                    buffer.append("\r\n");
                }
                buffer.append("--" + boundary);
                buffer.append("\r\n");
                
                Parameter param = params[index];
                buffer.append("Content-Disposition: form-data; name=\"" + param.getName() + "\"");
                if (param instanceof FileParameter) {
                    FileParameter fileParam = (FileParameter)params[index];
                    String filename = fileParam.getFilename();
                    if (filename == null) continue;
                    
                    buffer.append("; filename=\"" + filename + "\"");
                    buffer.append("\r\n");
                    buffer.append("Content-Type: " + fileParam.getContentType());
                    buffer.append("\r\n\r\n");
                    //clear the buffer
                    chunks.add(buffer.toString());
                    buffer.delete(0, buffer.length());
                    //write out the file
                    chunks.add(fileParam);
                } else {
                    buffer.append("\r\n\r\n");
                    buffer.append(param.getValue());
                }
            }

            buffer.append("\r\n");
            buffer.append("--" + boundary);
            buffer.append("--");
            chunks.add(buffer.toString());
            
            return new MultipartInputStream(chunks);
        } else {
            //default to UrlEncoded
            StringBuffer b = new StringBuffer();
            for (int i=0; i<params.length; i++) {
                if (i > 0) b.append("&");
                Parameter p = params[i];
                String name = URLEncoder.encode(p.getName(), "UTF-8");
                String value = URLEncoder.encode(p.getValue(), "UTF-8");
                b.append(name);
                b.append("=");
                b.append(value);
            }
            return new ByteArrayInputStream(b.toString().getBytes());
        }
    }
    
    @Override public String toString() {
        String s = super.toString();
        if (encoding == Encoding.MultipartFormData) {
            return s + "\nMultipart Form Data Encoded";
        } else {
            try {
                StringBuffer b = new StringBuffer();
                Parameter[] params = getFormParameters();
                for (int i=0; i<params.length; i++) {
                    if (i > 0) b.append("&");
                    Parameter p = params[i];
                    String name = URLEncoder.encode(p.getName(), "UTF-8");
                    String value = URLEncoder.encode(p.getValue(), "UTF-8");
                    b.append(name);
                    b.append("=");
                    b.append(value);
                }
                return s + "\n" + b;
            } catch (Exception e) {
                return s;
            }
        }
    }

    /**
     * This InputStream allows me to construct and return an InputStream which 
     * will be efficient in terms of memory usage with large numbers of files,
     * or with large files. The actual input stream to the files are not
     * opened until necessary.
     */
    private static final class MultipartInputStream extends InputStream {
        /**
         * A list of "chunks" which make up this stream. These chunks will
         * either be Strings or FileParameters. If FileParameters, then they are
         * converted to the file contents when actually written out.
         */
        private List<Object> chunks = new ArrayList<Object>();
        
        /**
         * If reading a File chunk, then this will be non-null.
         */
        private InputStream file = null;
        
        /**
         * If reading a String chunk, then this will be non-null.
         */
        private String string = null;
        private int index = -1;
        
        public MultipartInputStream(List<Object> chunks) {
            this.chunks = chunks;
        }
        
        @Override
        public int read() throws IOException {
            final boolean tryagain = true;
            while (tryagain) {
                // if string and file are both null then we need to read a
                // new chunk
                if (string == null && file == null) {
                    // if there are no more chunks then we're at the end
                    // and need to return -1
                    if (chunks.isEmpty()) return -1;
                    
                    // read a chunk. If it is null, then we start the loop
                    // over. Otherwise, we check whether it is a String or File
                    // and setup for reading the data accordingly
                    Object o = chunks.remove(0);
                    if (o == null) continue;
                    if (o instanceof String) {
                        string = (String)o;
                        index = 0;
                    } else if (o instanceof FileParameter) {
                        file = ((FileParameter)o).getValueStream();
                    } else {
                        throw new AssertionError("Cannot happen");
                    }
                }
                
                // now we ostensibly have data. If it is a string, then make
                // sure the index < string.length. If it is not, then we need
                // to null out string and try again. Otherwise we return a value
                if (string != null) {
                    if (index >= string.length()) {
                        string = null;
                        continue; // try again!
                    } else {
                        return string.charAt(index++);
                    }
                }
                
                if (file != null) {
                    int data = file.read();
                    if (data == -1) {
                        try {
                            file.close();
                        } catch (Exception e) {}
                        file = null;
                        continue; // try again!
                    } else {
                        return data;
                    }
                }
                
                throw new AssertionError("You should never reach this line");
            }
        }
    }
}