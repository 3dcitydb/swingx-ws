/*
 * $Id: HtmlHttpRequest.java 158 2006-12-20 01:28:38Z rbair $
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
package org.jdesktop.http.async;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.jdesktop.dom.SimpleDocument;
import org.jdesktop.dom.SimpleDocumentBuilder;
import org.w3c.tidy.Tidy;

/**
 * NOTE: This class requires JTidy!
 */
public final class HtmlHttpRequest extends AsyncHttpRequest {
    //responseXML: DOM-compatible document object of data returned from server process
    private SimpleDocument responseHtml;
    /** Creates a new instance of XmlHttpRequest */
    public HtmlHttpRequest() {
    }

    /**
     * If the readyState attribute has a value other than LOADED, then this method
     * will return null. Otherwise, if the Content-Type contains text/html, application/html,
     * or ends in +html then a Document will be returned. Otherwise, null is returned.
     */
    public final SimpleDocument getResponseHtml() {
        if (getReadyState()  == ReadyState.LOADED) {
            return responseHtml;
        } else {
            return null;
        }
    }

    protected void reset() {
        setResponseHtml(null);
        super.reset();
    }

    protected void handleResponse(String responseText) throws Exception {
        if (responseText == null) {
            setResponseHtml(null);
        } else {
            try {
                //apparently JTidy isn't escaping content within <script> blocks.
                //time to get a little dirty
                StringBuffer buffer = new StringBuffer(responseText);
                int startIndex = 0;
                while ((startIndex = buffer.indexOf("<script", startIndex)) != -1) {
                    startIndex = buffer.indexOf(">", startIndex) + 1;
                    int endIndex = buffer.indexOf("</script>", startIndex);
                    String temp = buffer.substring(startIndex, endIndex);
                    if (temp.contains("&") && !temp.startsWith("<![CDATA[")) {
                        temp = "<![CDATA[" + temp + "]]>";
                        buffer.replace(startIndex, endIndex, temp);
                    }
                }
                responseText = buffer.toString();
                
                Tidy tidy = new Tidy();
                tidy.setXHTML(true);
                tidy.setSmartIndent(true);
                tidy.setQuoteAmpersand(true);
                ByteArrayInputStream in = new ByteArrayInputStream(responseText.getBytes());
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                tidy.parse(in, out);
                DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
                SimpleDocumentBuilder builder = new SimpleDocumentBuilder(f);
                SimpleDocument dom = builder.parseString(out.toString());
                setResponseHtml(dom);
            }  catch (Exception e) {
                setResponseHtml(null);
                throw e;
            }
        }
    }

    private void setResponseHtml(SimpleDocument dom) {
        SimpleDocument old = this.responseHtml;
        this.responseHtml = dom;
        firePropertyChange("responseHtml", old, this.responseHtml);
    }
}