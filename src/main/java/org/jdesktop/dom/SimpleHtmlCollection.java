/*
 * $Id: SimpleHtmlCollection.java 134 2006-12-19 18:39:48Z rbair $
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

import java.util.Iterator;

import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLCollection;

/**
 * <p>An implementation of {@link HTMLCollection} which also implements
 * {@link Iterable}. This allows you to use a SimpleHtmlCollection in an enhanced
 * for loop, such as:
 * <pre><code>
 *  SimpleHtmlCollection nodes = ...;
 *  for (Node n : nodes) {
 *      System.out.println(n.getTextContent());
 *  }
 * </code></pre></p>
 * 
 * <p>SimpleHtmlCollection wraps a source HtmlCollection. So any normal HTMLCollection
 * can be adapted for use with enhanced for loops by wrapping it in a SimpleHtmlCollection.</p>
 * 
 * @author rbair
 */
public class SimpleHtmlCollection implements HTMLCollection, Iterable<Node> {
    private HTMLCollection list;
    
    /** 
     * Creates a new instance of SimpleHtmlCollection.
     * 
     * @param list the HTMLCollection to wrap.
     */
    public SimpleHtmlCollection(HTMLCollection list) {
        if (list == null) {
            throw new NullPointerException();
        }
        this.list = list;
    }

    /**
     * @inheritDoc
     */
    public Node item(int index) {
        return list.item(index);
    }

    /**
     * @inheritDoc
     */
    public int getLength() {
        return list.getLength();
    }

    /**
     * @inheritDoc
     */
    public Iterator<Node> iterator() {
        return new Iterator<Node>() {
            int index = 0;
            
            public boolean hasNext() {
                return index < getLength();
            }

            public Node next() {
                return item(index++);
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public Node namedItem(String name) {
        return list.namedItem(name);
    }
}
