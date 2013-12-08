package org.jdesktop.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

final class MockSession extends Session {

    private MockHttpURLConnection conn;
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    public MockSession() {
        super(false);
    }

    @Override
    protected URL createURL(String surl) throws MalformedURLException {
        URL tmp = new URL(surl);
        URL url = new URL(tmp.getProtocol(), tmp.getHost(), tmp.getPort(), tmp.getFile(), new URLStreamHandler() {
            @Override protected URLConnection openConnection(URL u) throws IOException {
                conn = new MockHttpURLConnection(u);
                out = conn.out;
                return conn;
            }
        });
        return url;
    }

    public int getNumBytesSent() {
        return out.size();
    }
}
