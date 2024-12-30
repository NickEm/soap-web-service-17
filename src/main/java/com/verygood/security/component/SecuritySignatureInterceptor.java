package com.verygood.security.component;

import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.XWSSProcessor;
import com.sun.xml.wss.XWSSProcessorFactory;
import java.io.InputStream;
import javax.security.auth.callback.CallbackHandler;
import javax.xml.soap.SOAPMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

public class SecuritySignatureInterceptor implements EndpointInterceptor {

  private final XWSSProcessor processor;

  public SecuritySignatureInterceptor(CallbackHandler keyStoreCallbackHandler) {
    try (InputStream policyStream = new ClassPathResource("soap/server-security-policy.xml").getInputStream()) {
      XWSSProcessorFactory factory = XWSSProcessorFactory.newInstance();
      this.processor = factory.createProcessorForSecurityConfiguration(policyStream, keyStoreCallbackHandler);
    } catch (Exception e) {
      throw new RuntimeException("Failed to initialize XWSSProcessor", e);
    }
  }

  @Override
  public boolean handleRequest(MessageContext messageContext, Object endpoint) throws Exception {
    try {
      SaajSoapMessage soapMessage = (SaajSoapMessage) messageContext.getRequest();
      SOAPMessage saajMessage = soapMessage.getSaajMessage();
      // Verify the inbound SOAP message
      ProcessingContext context = processor.createProcessingContext(saajMessage);
      processor.verifyInboundMessage(context);
      return true;
    } catch (Exception e) {
      throw new RuntimeException("SOAP signature verification failed", e);
    }
  }

  @Override
  public boolean handleResponse(MessageContext messageContext, Object endpoint) throws Exception {
    return true;
  }

  @Override
  public boolean handleFault(MessageContext messageContext, Object endpoint) throws Exception {
    return true;
  }

  @Override
  public void afterCompletion(MessageContext messageContext, Object endpoint, Exception ex) throws Exception {

  }

}
