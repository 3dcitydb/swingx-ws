/*
 * CookieSurfer.java
 *
 * Created on February 26, 2007, 11:06 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.http;

/**
 * Hits a bunch of websites and prints out the Cookies from those sites.
 * 
 * @author rbair
 */
public class CookieSurfer {
    /** Creates a new instance of CookieSurfer */
    private CookieSurfer() {}
    
    public static void main(String[] args) throws Exception {
        Session s = new Session();
        s.get("http://www.amazon.com");
        s.get("http://www.yahoo.com");
        s.get("http://www.hotmail.com");
        s.get("http://www.ebay.com");
        s.get("http://www.google.com/analytics");
        s.get("http://www.nike.com");
        
        System.out.println("All Cookies:");
        for (Cookie c : CookieManager.getCookies()) {
            System.out.println(c);
        }
    }
}
