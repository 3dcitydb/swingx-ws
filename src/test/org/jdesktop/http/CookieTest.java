/*
 * $Id: CookieTest.java 213 2007-02-27 17:44:40Z rbair $
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

package org.jdesktop.http;

import junit.framework.TestCase;

/**
 *
 * @author rbair
 */
public class CookieTest extends TestCase {
    
    public CookieTest(String testName) {
        super(testName);
    }

    public void testParseCookie() {
        Cookie c = Cookie.parseCookie("JSESSIONID=asdf2342300asdf09asd0f9");
        assertEquals("JSESSIONID", c.getName());
        assertEquals("asdf2342300asdf09asd0f9", c.getValue());
        assertFalse(c.isSecure());
        
        c = Cookie.parseCookie(" JSESSIONID=asdf2342300asdf09asd0f9 ");
        assertEquals("JSESSIONID", c.getName());
        assertEquals("asdf2342300asdf09asd0f9", c.getValue());
        assertFalse(c.isSecure());
        
        c = Cookie.parseCookie(" JSESSIONID = asdf2342300asdf09asd0f9 ");
        assertEquals("JSESSIONID", c.getName());
        assertEquals("asdf2342300asdf09asd0f9", c.getValue());
        assertFalse(c.isSecure());
        
        c = Cookie.parseCookie("JSESSIONID=asdf2342300asdf09asd0f9;foo=\"bar;bar\";secure");
        assertEquals("JSESSIONID", c.getName());
        assertEquals("asdf2342300asdf09asd0f9", c.getValue());
        assertTrue(c.isSecure());
        
        c = Cookie.parseCookie("session-id=000-0000000-0000000; path=/; domain=.amazon.com; expires=Mon Mar 05 08:00:00 2007 GMT");
        assertEquals("session-id", c.getName());
        assertEquals("000-0000000-0000000", c.getValue());
        assertFalse(c.isSecure());
        assertEquals("/", c.getPath());
        assertEquals(".amazon.com", c.getDomain());
        
        c = Cookie.parseCookie("B=9ta9mt92u6c95&b=3&s=kg; expires=Tue, 02-Jun-2037 20:00:00 GMT; path=/; domain=.yahoo.com");
        assertEquals("B", c.getName());
        assertEquals("9ta9mt92u6c95&b=3&s=kg", c.getValue());
        assertFalse(c.isSecure());
        assertEquals("/", c.getPath());
        assertEquals(".yahoo.com", c.getDomain());
        
        c = Cookie.parseCookie("MSPOK=uuid-21d927cf-1db4-4293-aace-b0447934e618; domain=login.live.com;path=/;version=1");
        assertEquals("MSPOK", c.getName());
        assertEquals("uuid-21d927cf-1db4-4293-aace-b0447934e618", c.getValue());
        assertFalse(c.isSecure());
        assertEquals("/", c.getPath());
        assertEquals(".login.live.com", c.getDomain());
        assertEquals(1, c.getVersion());
        
        c = Cookie.parseCookie("ebay=%5Esbf%3D0%5Epim%3D-1%5Erda%3D1172517158256.%5Esgj%3Dff77fd611100a06748147364ffd60815%5E; Domain=.ebay.com; Path=/");
        assertEquals("ebay", c.getName());
        assertEquals("%5Esbf%3D0%5Epim%3D-1%5Erda%3D1172517158256.%5Esgj%3Dff77fd611100a06748147364ffd60815%5E", c.getValue());
        assertFalse(c.isSecure());
        assertEquals("/", c.getPath());
        assertEquals(".ebay.com", c.getDomain());
        
        c = Cookie.parseCookie("PREF=ID=81cbf510de46a462:TM=1172517158:LM=1172517158:S=eKbn3ZQyDp-hoj7Q; expires=Sun, 17-Jan-2038 19:14:07 GMT; path=/; domain=.google.com");
        assertEquals("PREF", c.getName());
        assertEquals("ID=81cbf510de46a462:TM=1172517158:LM=1172517158:S=eKbn3ZQyDp-hoj7Q", c.getValue());
        assertFalse(c.isSecure());
        assertEquals("/", c.getPath());
        assertEquals(".google.com", c.getDomain());
        
        c = Cookie.parseCookie("AnalysisUserId=37371172517158; domain=.nike.com; path=/; expires=Friday, 31-Dec-2010 23:59:59 GMT");
        assertEquals("AnalysisUserId", c.getName());
        assertEquals("37371172517158", c.getValue());
        assertFalse(c.isSecure());
        assertEquals("/", c.getPath());
        assertEquals(".nike.com", c.getDomain());
        
    }

}
