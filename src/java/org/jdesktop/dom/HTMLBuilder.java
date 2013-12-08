/*
 * $Id: HTMLBuilder.java 134 2006-12-19 18:39:48Z rbair $
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


import java.util.Vector;
import org.apache.html.dom.HTMLDocumentImpl;

import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.dom.ProcessingInstructionImpl;
import org.apache.xerces.dom.TextImpl;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLDocument;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;


/**
 * This is a SAX document handler that is used to build an HTML document.
 * It can build a document from any SAX parser.
 * 
 * NOTE: I'm not sure how copyright works in this case. The original code was
 * BSD. It was copied here and somewhat altered. Hopefully in the future we can
 * just use Xerces, and it will implement the HTML parsing for us.
 * 
 * @version $Revision: 134 $ $Date: 2006-12-19 19:39:48 +0100 (Di, 19 Dez 2006) $
 * @author <a href="mailto:arkin@openxml.org">Assaf Arkin</a>
 */
class HTMLBuilder extends DefaultHandler2 {
    /**
     * The document that is being built.
     */
    protected HTMLDocumentImpl    _document;
    
    /**
     * The current node in the document into which elements, text and
     * other nodes will be inserted. This starts as the document iself
     * and reflects each element that is currently being parsed.
     */
    protected ElementImpl        _current;
    
    /**
     * A reference to the current locator, this is generally the parser
     * itself. The locator is used to locate errors and identify the
     * source locations of elements.
     */
    private Locator         _locator;

    /**
     * Applies only to whitespace appearing between element tags in element content,
     * as per the SAX definition, and true by default.
     */
    private boolean         _ignoreWhitespace = true;

    /**
     * Indicates whether finished building a document. If so, can start building
     * another document. Must be initially true to get the first document processed.
     */
    private boolean         _done = true;

    /**    
     * The document is only created the same time as the document element, however, certain
     * nodes may precede the document element (comment and PI), and they are accumulated
     * in this vector.
     */
    protected Vector         _preRootNodes;

    public void startDocument()
        throws SAXException
    {
        if ( ! _done )
	    throw new SAXException( "HTM001 State error: startDocument fired twice on one builder." );
	_document = null;
	_done = false;
    }


    public void endDocument()
        throws SAXException
    {
        if ( _document == null )
            throw new SAXException( "HTM002 State error: document never started or missing document element." );
	if ( _current != null )
	    throw new SAXException( "HTM003 State error: document ended before end of document element." );
        _current = null;
	_done = true;
    }


    public synchronized void startElement(String uri, String localName,
			      String qName, Attributes attrList)
        throws SAXException
    {
        ElementImpl elem;
        int         i;
        String tagName = getName(qName, localName);
        
	if ( tagName == null )
	    throw new SAXException( "HTM004 Argument 'tagName' is null." );

	// If this is the root element, this is the time to create a new document,
	// because only know we know the document element name and namespace URI.
	if ( _document == null )
	{
	    // No need to create the element explicitly.
	    _document = new HTMLDocumentImpl();
	    elem = (ElementImpl) _document.getDocumentElement();
	    _current = elem;
	    if ( _current == null )
		throw new SAXException( "HTM005 State error: Document.getDocumentElement returns null." );

	    // Insert nodes (comment and PI) that appear before the root element.
	    if ( _preRootNodes != null )
	    {
		for ( i = _preRootNodes.size() ; i-- > 0 ; )
		    _document.insertBefore( (Node) _preRootNodes.elementAt( i ), elem );
		_preRootNodes = null;
	    }
	     
	}
	else
	{
	    // This is a state error, indicates that document has been parsed in full,
	    // or that there are two root elements.
	    if ( _current == null )
		throw new SAXException( "HTM006 State error: startElement called after end of document element." );
	    elem = (ElementImpl) _document.createElement( tagName );
	    _current.appendChild( elem );
	    _current = elem;
	}

	// Add the attributes (specified and not-specified) to this element.
        if ( attrList != null )
        {
            for ( i = 0 ; i < attrList.getLength() ; ++ i ) {
                elem.setAttribute(getName(attrList.getQName(i), attrList.getLocalName(i)), attrList.getValue( i ) );
            }
        }
    }

    
    public void endElement(String uri, String localName,
			    String qName)
        throws SAXException
    {
        String tagName = getName(qName, localName);
        if ( _current == null )
            throw new SAXException( "HTM007 State error: endElement called with no current node." );
	if ( ! _current.getNodeName().equalsIgnoreCase( tagName ))
	    throw new SAXException( "HTM008 State error: mismatch in closing tag name " + tagName + "\n" + tagName);

	// Move up to the parent element. When you reach the top (closing the root element).
	// the parent is document and current is null.
	if ( _current.getParentNode() == _current.getOwnerDocument() )
	    _current = null;
	else
	    _current = (ElementImpl) _current.getParentNode();
    }


//    public void characters( String text )
//        throws SAXException
//    {
//	if ( _current == null )
//            throw new SAXException( "HTM009 State error: character data found outside of root element." );
//	_current.appendChild( new TextImpl( _document, text ) );
//    }

    
    public void characters( char[] text, int start, int length )
        throws SAXException
    {
	if ( _current == null )
            throw new SAXException( "HTM010 State error: character data found outside of root element." );
	_current.appendChild( new TextImpl( _document, new String( text, start, length ) ) );
    }
    
    
    public void ignorableWhitespace( char[] text, int start, int length )
        throws SAXException
    {
        Node    node;
        
        if ( ! _ignoreWhitespace )
	    _current.appendChild( new TextImpl( _document, new String( text, start, length ) ) );
     }
    
    
    public void processingInstruction( String target, String instruction )
        throws SAXException
    {
        Node    node;
        
	// Processing instruction may appear before the document element (in fact, before the
	// document has been created, or after the document element has been closed.
        if ( _current == null && _document == null )
	{
	    if ( _preRootNodes == null )
		_preRootNodes = new Vector();
	    _preRootNodes.addElement( new ProcessingInstructionImpl( null, target, instruction ) );
	}
	else
        if ( _current == null && _document != null )
	    _document.appendChild( new ProcessingInstructionImpl( _document, target, instruction ) );
	else
	    _current.appendChild( new ProcessingInstructionImpl( _document, target, instruction ) );
    }
    
    
    public HTMLDocument getHTMLDocument()
    {
        return (HTMLDocument) _document;
    }

    
    public void setDocumentLocator( Locator locator )
    {
        _locator = locator;
    }
    
    private String getName(String qName, String localName) {
        return localName == null || "".equals(localName) ? qName : localName;
    }

}
