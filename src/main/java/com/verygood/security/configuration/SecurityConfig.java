package com.verygood.security.configuration;

import com.verygood.security.component.SecuritySignatureInterceptor;
import java.io.InputStream;
import java.security.KeyStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.soap.security.xwss.callback.KeyStoreCallbackHandler;

@Configuration
public class SecurityConfig {

  @Value("${service.security.keystore.location:keystore/signing-keystore.jks}")
  private String keystoreLocation;

  @Value("${service.security.keystore.alias:emsnewcert}")
  private String keystoreAlias;

  @Value("${service.security.keystore.password:emsnewpassword1}")
  private String keystorePassword;

  @Value("${service.security.keystore.private-key-password:emsnewpassword2}")
  private String keystorePrivateKeyPassword;

  @Bean
  public KeyStoreCallbackHandler keyStoreCallbackHandler() {
    KeyStore keyStore = loadKeyStore();
    KeyStoreCallbackHandler callbackHandler = new KeyStoreCallbackHandler();
    callbackHandler.setKeyStore(keyStore);
    callbackHandler.setDefaultAlias(keystoreAlias);
    callbackHandler.setPrivateKeyPassword(keystorePrivateKeyPassword);
    return callbackHandler;
  }

  private KeyStore loadKeyStore() {
    try {
      KeyStore keyStore = KeyStore.getInstance("JKS");
      try (InputStream keyStoreStream = new ClassPathResource(keystoreLocation).getInputStream()) {
        keyStore.load(keyStoreStream, keystorePassword.toCharArray());
      }
      return keyStore;
    } catch (Exception e) {
      throw new RuntimeException("Could not load keystore", e);
    }
  }

  @Bean
  public SecuritySignatureInterceptor securitySignatureInterceptor(KeyStoreCallbackHandler keyStoreCallbackHandler) {
    return new SecuritySignatureInterceptor(keyStoreCallbackHandler);
  }

}
