/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdesktop.ws.events;

import java.util.EventObject;
import org.jdesktop.ws.BaseService;

/**
 *
 * @author Richard
 */
public class DoneEvent extends EventObject {
    public DoneEvent(BaseService source) {
        super(source);
    }
}
