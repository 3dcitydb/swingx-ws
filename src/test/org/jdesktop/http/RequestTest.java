/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdesktop.http;

import junit.framework.TestCase;
import sun.misc.BASE64Encoder;

/**
 *
 * @author Richard
 */
public class RequestTest extends TestCase {
    
    public RequestTest(String testName) {
        super(testName);
    }            

    public void testnewRequestMethodSetting() {
        Request req = new Request();
        assertEquals(Method.GET, req.getMethod());
        
        req = new Request("http://Hello.com");
        assertEquals(Method.GET, req.getMethod());
        
        req = new Request(Method.PUT, "http://Hello.com");
        assertEquals(Method.PUT, req.getMethod());
        
        req = new Request(null, "http://Hello.com");
        assertEquals(Method.GET, req.getMethod());
    }
    
    public void testnewRequestURLSetting() {
        Request req = new Request();
        assertNull(req.getUrl());
        
        req = new Request("http://Hello.com");
        assertEquals("http://Hello.com", req.getUrl());
        
        req = new Request(Method.PUT, "http://Hello.com");
        assertEquals("http://Hello.com", req.getUrl());

        req = new Request(Method.PUT, null);
        assertNull(req.getUrl());
    }
    
    public void testnewRequestHasAcceptEncodingSet() {
        Request req = new Request();
        Header h = req.getHeader("Accept-Encoding");
        assertNotNull(h);
        assertEquals("gzip", h.getValue());
    }
    
    public void testnewRequestHasContentEncodingSet() {
        Request req = new Request();
        Header h = req.getHeader("Content-Type");
        assertNotNull(h);
        assertEquals("text/plain; charset=UTF-8", h.getValue());
    }
    
    public void testCopyConstructor() throws Exception {
        Request req = new Request();
        req.setBody("Body");
        req.setFollowRedirects(true);
        req.setHeader("X-App-Foo", "Bar");
        req.setMethod(Method.TRACE);
        req.setParameter("Apples", "Oranges");
        req.setUrl("http://foo.com");
        req.setUsername("richard");
        req.setPassword("secret");
        
        Request copy = new Request(req);
        assertNull(copy.getBody());
        assertTrue(copy.getFollowRedirects());
        Header h = copy.getHeader("X-App-Foo");
        assertNotNull(h);
        assertEquals("Bar", h.getValue());
        assertEquals(Method.TRACE, copy.getMethod());
        Parameter p = copy.getParameter("Apples");
        assertNotNull(p);
        assertEquals("Oranges", p.getValue());
        assertEquals("http://foo.com", copy.getUrl());
        assertEquals("richard", copy.getUsername());
        assertEquals("secret", copy.getPassword());
    }
    
    /**
     * Test of getHeader method, of class Request.
     */
    public void testReadWriteHeader() {
        Request req = new Request();
        //test that Accept-Encoding is set to gzip
        Header acceptEncoding = req.getHeader("Accept-Encoding");
        assertEquals("gzip", acceptEncoding.getValue());
        
        req = new Request();
        Header h = new Header("Foo", "Bar");
        req.setHeader(h);
        assertEquals(h, req.getHeader("Foo"));
        req.setHeader("Faz", "Baz");
        assertEquals("Baz", req.getHeader("Faz").getValue());
    }

    /**
     * Test of removeHeader method, of class Request.
     */
    public void testRemoveHeader() {
        Request req = new Request();
        Header h = new Header("Foo", "Bar");
        req.setHeader(h);
        assertEquals(h, req.getHeader("Foo"));
        req.removeHeader(h);
        assertNull(req.getHeader("Foo"));
    }
    
    /**
     * Test that setting the authentication header directly sets the username
     * and password fields
     */
    public void testSetAuthHeaderSetsUsernameAndPassword() throws Exception {
        Request req = new Request();
        Header h = new Header("Authentication", "Basic " + new BASE64Encoder().encode("richard:secret".getBytes()));
        req.setHeader(h);
        assertEquals("richard", req.getUsername());
        assertEquals("secret", req.getPassword());
    }
    
    /**
     * Test of getParameter method, of class Request.
     */
    public void testGetSetParameter() {
        Request req = new Request();
        req.setParameter("Foo", "Bar");
        assertEquals("Bar", req.getParameter("Foo").getValue());
    }

    /**
     * Test of setUrl method, of class Request.
     */
    public void testSetUrl() {
        //test setting a URL without query params
        String url = "http://www.example.com/foo.html";
        Request req = new Request();
        req.setUrl(url);
        assertEquals(url, req.getUrl());
        
        //test setting a URL WITH query params
        String url2 = "http://www.example.com/foo.html?a=b+b&c=d+d";
        req.setUrl(url2);
        //the url returned from getUrl should be url, not url2
        assertEquals(url, req.getUrl());
        //both a and c should be params
        assertNotNull(req.getParameter("a"));
        assertEquals("b b", req.getParameter("a").getValue());
        assertNotNull(req.getParameter("c"));
        assertEquals("d d", req.getParameter("c").getValue());
    }

    /**
     * Test of setUsername method, of class Request.
     */
    public void testSetUsernameSetsHeader() {
        Request req = new Request();
        // there is no Authentication header if there is no username
        req.setPassword("secret");
        assertNull(req.getHeader("Authentication"));
        req.setUsername("Richard");
        assertNotNull(req.getHeader("Authentication"));
    }
}
