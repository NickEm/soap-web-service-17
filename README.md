# SOAP web server with message signing
Testing SOAP web server that checks message signature (SOAP with signing)
Project generated based on [Spring Boot Guide](https://spring.io/guides/gs/producing-web-service)


# Stack:
- Java 17
- Spring Boot 2.7
- Spring WS Security 3.1
- Maven


## Generate your keypair for sign and verification process
From the root of this repo
```shell
export KEYSTORE_FILE="src/main/resources/keystore/emsnewkeystore.jks";
export ALIAS="emsnewcert";
export KEYSTORE_PASS="emsnewpassword";
export PRIVATE_KEY_PASS="emsnewpassword";

keytool -genkey -alias "${ALIAS}" -keyalg RSA -keysize 2048 -keystore "${KEYSTORE_FILE}" -storepass "${KEYSTORE_PASS}" -keypass "${PRIVATE_KEY_PASS}" \
  -dname "CN=localhost, OU=Home, O=Home, L=New York, ST=New York, C=US";
```

## You are good to run
If you are using Intellij IDEA the run configuration should be picked up automatically [from](./.run/application.run.xml) 

## To verify, execute
```shell
curl --header "content-type: text/xml" -d @src/test/resources/request.xml http://localhost:8080/ws > target/response.xml && xmllint --format target/response.xml
```
