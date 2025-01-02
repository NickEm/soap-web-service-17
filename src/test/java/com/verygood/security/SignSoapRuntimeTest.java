package com.verygood.security;


import static org.assertj.core.api.Assertions.assertThat;

import com.sun.xml.messaging.saaj.soap.ver1_1.SOAPFactory1_1Impl;
import com.sun.xml.messaging.saaj.soap.ver1_1.SOAPMessageFactory1_1Impl;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import org.junit.jupiter.api.Test;

public class SignSoapRuntimeTest {

  @Test
  public void checkRuntime() throws SOAPException {
    final SOAPFactory soapFactory = SOAPFactory.newInstance();
    boolean soapFactoryIsCorrectType = (soapFactory instanceof SOAPFactory1_1Impl);
    assertThat(soapFactoryIsCorrectType).isTrue();

    MessageFactory messageFactory = MessageFactory.newInstance();
    boolean messageFactoryIsCorrectType = (messageFactory instanceof SOAPMessageFactory1_1Impl);
    assertThat(messageFactoryIsCorrectType).isTrue();
  }

}
