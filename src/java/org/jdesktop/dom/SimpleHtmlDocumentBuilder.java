/*
 * $Id: SimpleHtmlDocumentBuilder.java 134 2006-12-19 18:39:48Z rbair $
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

package org.jdesktop.dom;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;

import org.apache.html.dom.HTMLDocumentImpl;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.html.HTMLDocument;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <p>An HTML DOM {@link javax.xml.parsers.DocumentBuilder} implementation that does
 * not require the factory pattern for creation. Most of the time calling one of
 * the static <code>simpleParse</code> methods is all that is required.</p>
 *
 * <p>This implementation requires a normal DOM parser. It is not suitable for parsing
 * arbitrary HTML documents, even those documents which conform to the various HTML
 * specifications. Rather, it requires a preproccesor to first clean up the HTML such
 * that it can be parsed into a DOM.</p>
 * 
 * @author rbair
 */
public class SimpleHtmlDocumentBuilder extends DocumentBuilder {
    private static SimpleHtmlDocumentBuilder INSTANCE;
    
    private SAXParserFactory factory;
    
    /**
     * Create a new SimpleHtmlDocumentBuilder. SimpleHtmlDocumentBuilder will delegate
     * parsing to the default DocumentBuilder constructed via the default
     * DocumentBuilderFactory.
     */
    public SimpleHtmlDocumentBuilder() {
        try {
            factory = SAXParserFactory.newInstance();
        } catch (Exception ex) {
            throw new IllegalStateException("Could not create SAXParserFactory", ex);
        }
    }
    
    /**
     * <p>Parse the content of the given String as an XML
     * document and return a new HTML DOM {@link SimpleHtmlDocument} object.
     * An <code>IllegalArgumentException</code> is thrown if the
     * String is null.</p>
     *
     * <p><strong>NOTE:</strong> this implementation requires a normal DOM parser. 
     * It is not suitable for parsing arbitrary HTML documents, even those documents
     * which conform to the various HTML specifications. Rather, it requires a 
     * preproccesor to first clean up the HTML such that it can be parsed into a DOM.</p>
     *
     * @param html String containing the content to be parsed. Must be valid XHTML
     *
     * @return <code>SimpleHtmlDocument</code> result of parsing the
     *  String
     *
     * @throws IOException If any IO errors occur.
     * @throws SAXException If any parse errors occur.
     * @throws IllegalArgumentException When <code>html</code> is <code>null</code>
     *
     * @see org.xml.sax.DocumentHandler
     */
    public SimpleHtmlDocument parseString(String html) throws SAXException, IOException {
        if (html == null) {
            throw new IllegalArgumentException("html cannot be null");
        }
        ByteArrayInputStream in = new ByteArrayInputStream(html.getBytes());
        return parse(in);
    }
    
    //---------------------------------------------- DocumentBuilder methods
    
    /**
     * @inheritDoc
     */
    public SimpleHtmlDocument parse(InputSource is) throws SAXException, IOException {
        try {
            HTMLBuilder builder = new HTMLBuilder();
            SAXParser parser = factory.newSAXParser();
            parser.parse(is, builder);
            return new SimpleHtmlDocument(builder.getHTMLDocument());
        } catch (ParserConfigurationException ex) {
            throw new SAXException(ex);
        }
    }

    /**
     * @inheritDoc
     */
    public SimpleHtmlDocument parse(InputStream is) throws SAXException, IOException {
        return (SimpleHtmlDocument)super.parse(is);
    }

    /**
     * @inheritDoc
     */
    public SimpleHtmlDocument parse(InputStream is, String systemId) throws SAXException, IOException {
        return (SimpleHtmlDocument)super.parse(is, systemId);
    }

    /**
     * @inheritDoc
     */
    public SimpleHtmlDocument parse(String uri) throws SAXException, IOException {
        return (SimpleHtmlDocument)super.parse(uri);
    }

    /**
     * @inheritDoc
     */
    public SimpleHtmlDocument parse(File f) throws SAXException, IOException {
        return (SimpleHtmlDocument)super.parse(f);
    }
    
    /**
     * @inheritDoc
     */
    public boolean isNamespaceAware() {
        return factory.isNamespaceAware();
    }

    /**
     * @inheritDoc
     */
    public boolean isValidating() {
        return factory.isValidating();
    }

    /**
     * @inheritDoc
     */
    public void setEntityResolver(EntityResolver er) {
//        factory.setEntityResolver(er);
    }

    /**
     * @inheritDoc
     */
    public void setErrorHandler(ErrorHandler eh) {
//        factory.setErrorHandler(eh);
    }

    /**
     * @inheritDoc
     */
    public SimpleHtmlDocument newDocument() {
        return new SimpleHtmlDocument(new HTMLDocumentImpl());
    }

    /**
     * @return an unenclosed Document. This is used only by the SimpleDocument
     * no arg constructor
     */
    HTMLDocument newPlainDocument() {
        return new HTMLDocumentImpl();
    }
    
    /**
     * @inheritDoc
     */
    public DOMImplementation getDOMImplementation() {
//        return builder.getDOMImplementation();
        return null;
    }

    /**
     * @inheritDoc
     */
    public void reset() {
//        factory.reset();
    }
    
    /**
     * @inheritDoc
     */
    public Schema getSchema() {
        return factory.getSchema();
    }
    
    /**
     * @inheritDoc
     */
    public boolean isXIncludeAware() {
        return factory.isXIncludeAware();
    }
    
    //------------------------------------------------------- Static methods
    
    private synchronized static SimpleHtmlDocumentBuilder getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SimpleHtmlDocumentBuilder();
        }
        return INSTANCE;
    }
    
    /**
     * <p>Parse the content of the given input source as an XML document
     * and return a new HTML DOM {@link SimpleDocument} object.
     * An <code>IllegalArgumentException</code> is thrown if the
     * <code>InputSource</code> is <code>null</code> null.</p>
     *
     * <p><strong>NOTE:</strong> this implementation requires a normal DOM parser. 
     * It is not suitable for parsing arbitrary HTML documents, even those documents
     * which conform to the various HTML specifications. Rather, it requires a 
     * preproccesor to first clean up the HTML such that it can be parsed into a DOM.</p>
     *
     * @param is InputSource containing the content to be parsed.
     *
     * @return A new DOM SimpleHtmlDocument object.
     *
     * @throws IOException If any IO errors occur.
     * @throws SAXException If any parse errors occur.
     * @throws IllegalArgumentException When <code>is</code> is <code>null</code>
     *
     * @see org.xml.sax.DocumentHandler
     */
    public static SimpleHtmlDocument simpleParse(InputSource is) throws SAXException, IOException {
        return getInstance().parse(is);
    }
    
    /**
     * <p>Parse the content of the given <code>InputStream</code> as an XML
     * document and return a new HTML DOM {@link SimpleHtmlDocument} object.
     * An <code>IllegalArgumentException</code> is thrown if the
     * <code>InputStream</code> is null.</p>
     *
     * <p><strong>NOTE:</strong> this implementation requires a normal DOM parser. 
     * It is not suitable for parsing arbitrary HTML documents, even those documents
     * which conform to the various HTML specifications. Rather, it requires a 
     * preproccesor to first clean up the HTML such that it can be parsed into a DOM.</p>
     *
     * @param is InputStream containing the content to be parsed.
     *
     * @return <code>HtmlSimpleDocument</code> result of parsing the
     *  <code>InputStream</code>
     *
     * @throws IOException If any IO errors occur.
     * @throws SAXException If any parse errors occur.
     * @throws IllegalArgumentException When <code>is</code> is <code>null</code>
     *
     * @see org.xml.sax.DocumentHandler
     */
    public static SimpleHtmlDocument simpleParse(InputStream in) throws SAXException, IOException {
        return getInstance().parse(in);
    }
    
    /**
     * <p>Parse the content of the given URL as an XML document
     * and return a new HTML DOM {@link SimpleHtmlDocument} object.
     * An <code>IllegalArgumentException</code> is thrown if the
     * URI is <code>null</code> null.</p>
     *
     * <p><strong>NOTE:</strong> this implementation requires a normal DOM parser. 
     * It is not suitable for parsing arbitrary HTML documents, even those documents
     * which conform to the various HTML specifications. Rather, it requires a 
     * preproccesor to first clean up the HTML such that it can be parsed into a DOM.</p>
     *
     * @param uri The location of the content to be parsed.
     *
     * @return A new DOM SimpleHtmlDocument object.
     *
     * @throws IOException If any IO errors occur.
     * @throws SAXException If any parse errors occur.
     * @throws IllegalArgumentException When <code>url</code> is <code>null</code>
     *
     * @see org.xml.sax.DocumentHandler
     */
    public static SimpleHtmlDocument simpleParse(URL url) throws SAXException, IOException {
        if (url == null) {
            throw new IllegalArgumentException("url cannot be null");
        }
        InputStream in = url.openStream();
        return simpleParse(in);
    }

    /**
     * <p>Parse the content of the given String as an XML
     * document and return a new HTML DOM {@link SimpleHtmlDocument} object.
     * An <code>IllegalArgumentException</code> is thrown if the
     * String is null.</p>
     *
     * <p><strong>NOTE:</strong> this implementation requires a normal DOM parser. 
     * It is not suitable for parsing arbitrary HTML documents, even those documents
     * which conform to the various HTML specifications. Rather, it requires a 
     * preproccesor to first clean up the HTML such that it can be parsed into a DOM.</p>
     *
     * @param xml String containing the content to be parsed.
     *
     * @return <code>SimpleDocument</code> result of parsing the
     *  String
     *
     * @throws IOException If any IO errors occur.
     * @throws SAXException If any parse errors occur.
     * @throws IllegalArgumentException When <code>xml</code> is <code>null</code>
     *
     * @see org.xml.sax.DocumentHandler
     */
    public static SimpleHtmlDocument simpleParse(String xml) throws SAXException, IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes());
        return simpleParse(in);
    }
}
