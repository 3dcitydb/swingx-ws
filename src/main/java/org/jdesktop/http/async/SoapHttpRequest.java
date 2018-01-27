/*
 * SoapHttpRequest.java
 *
 * Created on October 13, 2006, 3:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.http.async;

//import javax.xml.soap.MessageFactory;
//import javax.xml.soap.SOAPMessage;
//import javax.xml.ws.Dispatch;
//import javax.xml.ws.Service;
import org.jdesktop.dom.SimpleDocument;
import org.jdesktop.http.Method;
import org.jdesktop.http.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author rbair
 */
public class SoapHttpRequest extends XmlHttpRequest {
    private String port;
    private String serviceName;
    private String wsdl;
    private Document sendDom;
    
    /** Creates a new instance of SoapHttpRequest */
    public SoapHttpRequest() {
    }

    public void send(Document dom) {
        sendDom = dom;
        //convert the dom to a String, and send that
        send((String)null);
    }
    
    protected AsyncHttpRequest.AsyncWorker createAsyncWorker(String content) {
        return new SoapAsyncWorker();
    }
    
    private final class SoapAsyncWorker extends AsyncWorker {
        protected Object doInBackground() throws Exception {
            try {
                Session session = new Session();
                System.out.println(session.get(wsdl).getBody());
                
//                Service s = Service.create(new URL(wsdl), new QName("http://webservices.amazon.com/AWSECommerceService/2006-11-14", serviceName));
//                Dispatch<SOAPMessage> dispatch = s.createDispatch(new QName("http://webservices.amazon.com/AWSECommerceService/2006-11-14", port), SOAPMessage.class, Service.Mode.MESSAGE);
//                MessageFactory mf = MessageFactory.newInstance();
//                SOAPMessage msg = mf.createMessage();
//                if (sendDom != null) {
//                    msg.getSOAPBody().addDocument(sendDom);
//                }
//                SOAPMessage response = dispatch.invoke(msg);
//                SimpleDocument dom = new SimpleDocument(response.getSOAPBody().extractContentAsDocument());
//                System.out.println(dom.toXML());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    
    public static void main(String[] args) {
        try {
            SoapHttpRequest soap = new SoapHttpRequest();
            soap.port = "AWSECommerceServicePort";
            soap.serviceName = "AWSECommerceService";
            soap.wsdl = "http://webservices.amazon.com/AWSECommerceService/AWSECommerceService.wsdl";
            soap.open(Method.GET, "http://www.google.com", false);
            
            SimpleDocument dom = new SimpleDocument();
            Element itemSearch = dom.createElement("ItemSearch");
            dom.appendChild(itemSearch);
            
            Element e = dom.createElement("SubscriptionId");
            itemSearch.appendChild(e);
            e.setTextContent("asdfasdfasdfasdf");
            
            e = dom.createElement("Operation");
            itemSearch.appendChild(e);
            e.setTextContent("ItemSearch");
            
            e = dom.createElement("Keywords");
            itemSearch.appendChild(e);
            e.setTextContent("Running Stroller");
            
            e = dom.createElement("SearchIndex");
            itemSearch.appendChild(e);
            e.setTextContent("Baby");
            
//            System.out.println(dom.toXML());
            
            soap.send(dom);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
