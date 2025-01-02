package com.verygood.security.component;

import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.XWSSProcessor;
import com.sun.xml.wss.XWSSProcessorFactory;
import com.sun.xml.wss.XWSSecurityException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.security.auth.callback.CallbackHandler;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.ws.soap.server.SoapEndpointInterceptor;

public class SoapSignatureSecurityInterceptor implements SoapEndpointInterceptor {

  public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  private final XWSSProcessor processor;
  private final boolean interceptorIsDebugMode;

  public SoapSignatureSecurityInterceptor(CallbackHandler keyStoreCallbackHandler, boolean interceptorIsDebugMode) {
    try (InputStream policyStream = new ClassPathResource("soap/server-security-policy.xml").getInputStream()) {
      XWSSProcessorFactory factory = XWSSProcessorFactory.newInstance();
      this.processor = factory.createProcessorForSecurityConfiguration(policyStream, keyStoreCallbackHandler);
      this.interceptorIsDebugMode = interceptorIsDebugMode;
    } catch (Exception e) {
      throw new RuntimeException("Failed to initialize XWSSProcessor", e);
    }
  }

  @Override
  public boolean handleRequest(MessageContext messageContext, Object endpoint) throws Exception {
    try {
      SaajSoapMessage saajSoapMessage = (SaajSoapMessage) messageContext.getRequest();
      SOAPMessage soapMessage = getSoapMessage(saajSoapMessage);

      return verifyMessage(soapMessage);
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

  @Override
  public boolean understands(SoapHeaderElement header) {
    boolean result = false;
    final QName name = header.getName();
    if (StringUtils.equals(name.getLocalPart(), "Security") && StringUtils.equals(name.getPrefix(), "wsse")) {
      result = true;
    }
    return result;
  }

  public boolean verifyMessage(SOAPMessage soapMessage) {
    try {
      ProcessingContext context = this.processor.createProcessingContext(soapMessage);
      this.processor.verifyInboundMessage(context);
    } catch (XWSSecurityException e) {
      throw new RuntimeException("Verification of the SOAP message has failed.", e);
    }

    return true;
  }

  public SOAPMessage getSoapMessage(SaajSoapMessage saajSoapMessage) throws IOException {
    SOAPMessage soapMessage;
    if (interceptorIsDebugMode) {
      try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
        saajSoapMessage.writeTo(outputStream);
        String fileName = "soap-request-%s.xml".formatted(DATE_TIME_FORMATTER.format(LocalDateTime.now()));
        /*this stores the request payload in a file in the root project folder*/
        try (final FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
          fileOutputStream.write(outputStream.toByteArray());
        }
        soapMessage = convertToSoapMessage(outputStream.toByteArray());
      }
    } else {
      soapMessage = saajSoapMessage.getSaajMessage();
    }

    return soapMessage;
  }

  public SOAPMessage convertToSoapMessage(byte[] message) {
    InputStream is = new ByteArrayInputStream(message);
    try {
      return MessageFactory.newInstance().createMessage(null, is);
    } catch (IOException | SOAPException e) {
      throw new RuntimeException("Could not create SOAP message.", e);
    }
  }

}
