package com.verygood.security;

import com.verygood.security.component.SoapSignatureSecurityInterceptor;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.stream.Stream;
import javax.security.auth.callback.CallbackHandler;
import javax.xml.soap.SOAPMessage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

@SpringBootTest
public class SoapSignatureVerificationTest {

  @Autowired
  private CallbackHandler keyStoreCallbackHandler;

  private static Stream<Arguments> requestFilePathProvider() {
    return Stream.of(
      Arguments.of("request-signed.xml", false),
      Arguments.of("request-signed-inline.xml", false),
      Arguments.of("request-signed.xml", true),
      Arguments.of("request-signed-inline.xml", true)
    );
  }

  @ParameterizedTest
  @MethodSource("requestFilePathProvider")
  void verifySignedSoapRequestFromFile(String filePath, boolean interceptorInDebugMode)
    throws IOException {

    SoapSignatureSecurityInterceptor interceptor = new SoapSignatureSecurityInterceptor(keyStoreCallbackHandler,
      interceptorInDebugMode);
    final ClassPathResource resource = new ClassPathResource(filePath);
    final byte[] signedMessage = resource.getInputStream().readAllBytes();
    final String stringified = new String(signedMessage, Charset.defaultCharset());
    System.out.println("=== +++ ===");
    System.out.println(stringified);
    System.out.println("=== +++ ===");
    final SOAPMessage soapMessage = interceptor.convertToSoapMessage(signedMessage);
    interceptor.verifyMessage(soapMessage);
  }

}
