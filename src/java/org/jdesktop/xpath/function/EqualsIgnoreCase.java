/*
 * EndsWith.java
 *
 * Created on September 27, 2006, 10:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.xpath.function;

import java.util.List;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;
import org.w3c.dom.NodeList;

/**
 *
 * @author richardallenbair
 */
public class EqualsIgnoreCase extends AbstractFunction {
    
    /** Creates a new instance of EndsWith */
    public EqualsIgnoreCase() {
        super("equals-ignore-case", 2);
    }

    public Object evaluate(List args) throws XPathFunctionException {
        NodeList nodes = (NodeList)args.get(0);
        String s1 = nodes.getLength() > 0 ? nodes.item(0).getLocalName() : "";
        String s2 = getStringParam(args.get(1));
        return s1.equalsIgnoreCase(s2);
    }
}
