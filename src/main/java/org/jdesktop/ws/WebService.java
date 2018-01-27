/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jdesktop.ws;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;

import org.jdesktop.beans.AbstractBean;
import org.jdesktop.dom.SimpleDocument;
import org.jdesktop.dom.SimpleDocumentBuilder;
import org.jdesktop.http.Header;
import org.jdesktop.http.Request;
import org.jdesktop.http.Response;
import org.w3c.dom.Document;

/**
 * <p>Represents a SOAP web service client. This client is useful for working with
 * SOAP at a lower level, simply packaging the XML body and sending it, and
 * receiving another XML body. No implicit or explicit binding occurs.</p>
 * 
 * <p>This class relies on the availability of the WSDL file. The
 * <code>wsdl</code> property must be set prior to executing an operation.
 * When an operation is executed, the WSDL is parsed and values for
 * port, service, and targetNamespace are determined from the WSDL file
 * if not explicitly specified on the WebService. In practice many WSDL files
 * have only one service and port, so there is little to be gained by 
 * specifying them manually since they may be inferred. In the case of
 * a WSDL with multiple ports / services, if not declared, then the first
 * service in the file is used.</p>
 * 
 * @author Richard
 */
public class WebService extends AbstractBean {
    /**
     * The URL for the WSDL file to use.
     */
    private String wsdl;
    /**
     * The WebService port to use. This is not the same as a TCP/IP port. See
     * the WSDL spec for details.
     */
    private String port;
    /**
     * The service description within the WSDL file to use with this
     * WebService instance.
     */
    private String service;
    /**
     * The namespace to use with the requests
     */
    private String targetNamespace;
    /**
     * A map of all the operations that are supported by this webservice.
     * These could be inferred...
     */
    private Map<String,Operation> operations = new HashMap<String,Operation>();

    public final String getPort() { return port; }
    public void setPort(String port) {
        String old = this.port;
        firePropertyChange("port", old, this.port = port);
    }
    
    public final String getService() { return service; }
    public void setService(String service) {
        String old = this.service;
        firePropertyChange("service", old, this.service = service);
    }
    
    public final String getWsdl() { return wsdl; }
    public void setWsdl(String wsdl) {
        String old = this.wsdl;
        firePropertyChange("wsdl", old, this.wsdl = wsdl);
    }
    
    public final String getTargetNamespace() { return targetNamespace; }
    public void setTargetNamespace(String ns) {
        String old = this.targetNamespace;
        firePropertyChange("targetNamespace", old, this.targetNamespace = ns);
    }
    
    public final Operation[] getOperations() {
        return operations.values().toArray(new Operation[0]);
    }
    
    public final int getOperationCount() { return operations.size(); }
    
    public final Operation getOperation(String name) {
        return operations.get(name);
    }
    
    public void addOperation(Operation op) {
        operations.put(op.name, op);
    }
    
    /**
     * Represents an operation that can be performed
     */
    public static final class Operation extends AbstractHttpService {
        private Document requestDom; //the payload including the name of the operation being invoked
        private Document responseDom;
        private String name;
        private WebService svc;
        
        public Operation(WebService svc, String name) {
            if (svc == null || name == null) {
                throw new IllegalArgumentException("Both the svc and name must be specified");
            }
            this.svc = svc;
            this.name = name;
        }

        public final Document getRequestDocument() { return requestDom; }
        public void setRequestDocument(Document dom) {
            Document old = this.requestDom;
            firePropertyChange("requestDocument", old, this.requestDom = dom);
        }
        
        public final Document getResponseDocument() { return responseDom; }
        private void setResponseDocument(Document dom) {
            Document old = responseDom;
            firePropertyChange("responseDocument", old, this.responseDom = dom);
        }
        
        public final String getName() { return name; }
        public final WebService getWebService() { return svc; }

        protected SwingWorker createWorker() {
            return new SoapWorker(this);
        }
        
        @Override protected void doSend() {
            super.doSend();
            setResponseDocument(null);
        }

        private static final class SoapWorker extends SwingWorker<SOAPMessage,SOAPMessage> {
            private Operation op;

            SoapWorker(Operation op) {
                this.op = op;
            }

            @Override
            protected SOAPMessage doInBackground() throws Exception {
                //NOTE: I had an extreme amount of difficulty getting this to work
                //at all. Originally I did this:
                //SOAPMessage response = dispatch.invoke(msg);
                //to invoke the message, but I was getting odd errors with the
                //weather.gov web service where it wouldn't parse the result. So what
                //I did was bypass this by using Session, Request, and Response and
                //letting the SOAPMessage write out the soap request.
                //I'm sure this isn't robust enough and that I'll need to repent
                //and figure out how to use dispatch.invoke, but it should at least allow
                //me to get some demo's off the ground.

                //for convenience, several values, if null, will be inferred based on
                //the wsdl file at runtime. Specifically, if targetNamespace is null,
                //then the targetNamespace of the <definitions> tag will be used. If
                //port is null, then the first portType listed will be used. Finally,
                //if service is null then the first service listed will be used.

                //NOTE: The following line has been commented out because now that I'm
                //handling the invocation of the SOAP request myself, I must always
                //parse the WSDL
        //        boolean mustParseWSDL = targetNamespace == null || port == null || service == null;
                boolean mustParseWSDL = true;

                String namespace = op.svc.targetNamespace;
                String portName = op.svc.port;
                String serviceName = op.svc.service;
                String url = null; //Only necessary since I'm invoking the service manually
                String soapAction = null;

                if (mustParseWSDL) {
                    SimpleDocument wsdlDom = SimpleDocumentBuilder.simpleParse(new URL(op.svc.wsdl));
                    if (namespace == null || "".equals(namespace.trim()))
                        namespace = wsdlDom.getString("/definitions/@targetNamespace");
                    if (portName == null || "".equals(portName.trim()))
                        portName = wsdlDom.getString("/definitions/service/port/@name");
                    if (serviceName == null || "".equals(serviceName.trim()))
                        serviceName = wsdlDom.getString("/definitions/service/@name");

                    //only necessary since I'm invoking the service manually
                    url = wsdlDom.getString("/definitions/service[@name='" + serviceName + "']/port/address/@location");
                    soapAction = wsdlDom.getString("/definitions/binding/operation[@name='" + op.name + "']/operation/@soapAction");
                }

        //        Service s = Service.create(new URL(wsdl), new QName(namespace, serviceName));
        //        Dispatch<SOAPMessage> dispatch = s.createDispatch(new QName(namespace, portName), SOAPMessage.class, Service.Mode.MESSAGE);

                MessageFactory mf = MessageFactory.newInstance();
                SOAPMessage msg = mf.createMessage();

                if (op.requestDom != null) {
                    msg.getSOAPBody().addDocument(op.requestDom);
                }

                Request request = new Request();
                request.setUrl(url);
                request.setMethod(org.jdesktop.http.Method.POST);
                request.setHeader("Content-type", "text/xml");
                //depending on what the operation name is, find the operation in the
                //DOM and then look up the SOAPAction for that operation and use it
                if (soapAction != null) {
        //          Map<String,Object> ctx = dispatch.getRequestContext();
        //          ctx.put(Dispatch.SOAPACTION_USE_PROPERTY, true);
        //          ctx.put(Dispatch.SOAPACTION_URI_PROPERTY, soapAction);
                    request.setHeader("SOAPAction", soapAction);
                }

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                msg.writeTo(bytes);
                request.setBody("<?xml version=\"1.0\"?>" + bytes);
                Response response = op.getSession().execute(request);

                MimeHeaders headers = new MimeHeaders();
                for (Header h : response.getHeaders()) {
                    headers.addHeader(h.getName(), h.getValue());
                }

                SOAPMessage rsp = mf.createMessage(headers, response.getBodyAsStream());
        //        SOAPMessage rsp = dispatch.invoke(msg);
                
                return rsp;
            }

            @Override
            protected void done() {
                if (!isCancelled()) {
                    try {
                        SOAPMessage msg = get();
                        if (msg.getSOAPBody().hasFault()) {
                            op.setResponseDocument(msg.getSOAPBody().getFault().getOwnerDocument());
                        } else {
                            op.setResponseDocument(msg.getSOAPBody().extractContentAsDocument());
                        }
                    } catch (CancellationException cancelled) {
                        // it was aborted, nothing to do since the
                        // abort method sets the state
                    } catch (ExecutionException e) {
                        //something went awry
                        op.fail(e.getCause());
                    } catch (Exception e) {
                        //something went awry
                        op.fail(e);
                    }
                }
            }
        }
    }
}
