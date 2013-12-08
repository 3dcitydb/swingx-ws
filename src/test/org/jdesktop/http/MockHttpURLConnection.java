/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdesktop.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a fake HttpURLConnection which does not require a connection to the
 * internet for the sake of testing. Simply supply the text of the "resource"
 * (which will form the body of the response), the status code that should
 * be returned, and any headers that should be returned.
 * 
 * Note that this version does NOT care about the content-length header set
 * by the client. It really doesn't do anything smart, just trap the data
 * and let you test it.
 * 
 * @author Richard
 */
public class MockHttpURLConnection extends HttpURLConnection {
    /**
     * based on the disconnect()/connect() methods
     */
    boolean connected = false;
    /**
     * The bytes that are sent TO the server (used for uploading)
     */
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    /**
     * The bytes that are coming FROM the server (used for downloading)
     */
    ByteArrayInputStream in = new ByteArrayInputStream(
            "<html><head></head><body>Hello!</body></html>".getBytes());
    /**
     * The pretend headers for the connection
     */
    List<Header> headers = new ArrayList<Header>();
    
    /**
     * The response status code
     */
    StatusCode status = StatusCode.OK;
    
    public MockHttpURLConnection(URL u) {
        super(u);
    }
    
    @Override public void disconnect() {
        connected = false;
    }

    @Override public boolean usingProxy() {
        return false;
    }

    @Override public void connect() throws IOException {
        connected = true;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return in;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return out;
    }

    @Override
    public String getHeaderField(int n) {
        if (n == 0) return "HTTP/1.1 " + status.getCode() + " " + status.getDescription();
        n--;
        return n < headers.size() ? headers.get(n).toString() : null;
    }
}
