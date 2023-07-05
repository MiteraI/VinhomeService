package app.vinhomes.security.fibo_sms;


import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

import javax.xml.soap.*;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.Node;


public class SMS {
    private String endpoint;
    private String clientNo;
    private String clientPass;
    private int serviceType;
    private Object client;

    public SMS(String endpoint, String clientNo, String clientPass, int serviceType) {
        this.endpoint = endpoint;
        this.clientNo = clientNo;
        this.clientPass = clientPass;
        this.serviceType = serviceType;
        this.client = null;
    }

    public SMS(String clientNo, String clientPass) {
        this.endpoint = "http://center.fibosms.com/service.asmx";
        this.clientNo = clientNo;
        this.clientPass = clientPass;
        this.serviceType = 1;
        this.client = null;
    }

    public boolean sendSMS(Message msg) throws SOAPException {
        HashMap<String, String> params = new HashMap<>();
        params.put("clientNo", this.clientNo);
        params.put("clientPass", this.clientPass);
        params.put("serviceType", this.serviceType + "");
        params.put("senderName", msg.getSenderName());
        params.put("phoneNumber", msg.getPhoneNumber());
        params.put("smsGUID", msg.getSmsGUID());
        SOAPMessage response = this.callSoapWebService(this.toUri("SendMaskedSMS", this.buildQuery(params)),
                "SendMaskedSMS");
        // get the body
        SOAPBody soapBody = response.getSOAPBody();
        // find your node based on tag name
        NodeList nodes = soapBody.getElementsByTagName("Message");
        String result = null;
        Node node = (Node) nodes.item(0);
        result = node != null ? node.getTextContent() : "";
        return result == "Sending...";
    }

    private void createSoapEnvelope(SOAPMessage soapMessage) throws SOAPException {
        SOAPPart soapPart = soapMessage.getSOAPPart();

        String myNamespace = this.endpoint + "?wsdl";
        String myNamespaceURI = "http://www.webserviceX.NET/";

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);
    }

    private SOAPMessage callSoapWebService(String soapEndpointUrl, String soapAction) throws SOAPException {
        SOAPConnection soapConnection = null;
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(soapAction), soapEndpointUrl);

            return soapResponse;
        } catch (Exception e) {
            System.err.println(
                    "\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
            e.printStackTrace();
            return null;
        } finally {
            //if (soapConnection.) {
                soapConnection.close();
            //}
        }
    }

    private SOAPMessage createSOAPRequest(String soapAction) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        createSoapEnvelope(soapMessage);

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", soapAction);

        soapMessage.saveChanges();

        /* Print the request message, just for debugging purposes */
        System.out.println("Request SOAP Message:");
        soapMessage.writeTo(System.out);
        System.out.println("\n");

        return soapMessage;
    }

    private String toUri(String service, String query) {
        return String.format("%s/%s?%s", this.endpoint, service, query);
    }

    private String buildQuery(Map<String, String> params) {
        return params.entrySet().stream().map(p -> p.getKey() + "=" + p.getValue()).reduce((p1, p2) -> p1 + "&" + p2)
                .map(s -> "?" + s).orElse("");
    }
}
