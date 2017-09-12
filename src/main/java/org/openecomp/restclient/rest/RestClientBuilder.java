/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017 Amdocs
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.openecomp.restclient.rest;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.openecomp.restclient.enums.RestAuthenticationMode;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
 
/**
 * This is a generic REST Client builder with flexible security validation. Sometimes it's nice to
 * be able to disable server chain cert validation and hostname validation to work-around lab
 * issues, but at the same time be able to provide complete validation with client cert + hostname +
 * server cert chain validation. I used the ModelLoader REST client as a base and merged in the TSUI
 * client I wrote which also validates the server hostname and server certificate chain.
 */
public class RestClientBuilder {

  public static final boolean DEFAULT_VALIDATE_SERVER_HOST = false;
  public static final boolean DEFAULT_VALIDATE_CERT_CHAIN = false;
  public static final String DEFAULT_CLIENT_CERT_FILENAME = null;
  public static final String DEFAULT_CERT_PASSWORD = null;
  public static final String DEFAULT_TRUST_STORE_FILENAME = null;
  public static final int DEFAULT_CONNECT_TIMEOUT_MS = 60000;
  public static final int DEFAULT_READ_TIMEOUT_MS = 60000;
  public static final RestAuthenticationMode DEFAULT_AUTH_MODE = RestAuthenticationMode.SSL_CERT;
  public static final String DEFAULT_BASIC_AUTH_USERNAME = "";
  public static final String DEFAULT_BASIC_AUTH_PASSWORD = "";
  public static final String DEFAULT_SSL_PROTOCOL = "TLS";

  private static final String KEYSTORE_ALGORITHM = "SunX509";
  private static final String KEYSTORE_TYPE = "PKCS12";
  private static final String TRUST_STORE_PROPERTY = "javax.net.ssl.trustStore";

  private boolean validateServerHostname;
  private boolean validateServerCertChain;
  private String clientCertFileName;
  private String clientCertPassword;
  private String truststoreFilename;
  private int connectTimeoutInMs;
  private int readTimeoutInMs;
  private RestAuthenticationMode authenticationMode;
  private String basicAuthUsername;
  private String basicAuthPassword;
  private String sslProtocol;

  /**
   * Rest Client Builder.
   */
  public RestClientBuilder() {
    validateServerHostname = DEFAULT_VALIDATE_SERVER_HOST;
    validateServerCertChain = DEFAULT_VALIDATE_CERT_CHAIN;
    clientCertFileName = DEFAULT_CLIENT_CERT_FILENAME;
    clientCertPassword = DEFAULT_CERT_PASSWORD;
    truststoreFilename = DEFAULT_TRUST_STORE_FILENAME;
    connectTimeoutInMs = DEFAULT_CONNECT_TIMEOUT_MS;
    readTimeoutInMs = DEFAULT_READ_TIMEOUT_MS;
    authenticationMode = DEFAULT_AUTH_MODE;
    basicAuthUsername = DEFAULT_BASIC_AUTH_USERNAME;
    basicAuthPassword = DEFAULT_BASIC_AUTH_PASSWORD;
    sslProtocol = DEFAULT_SSL_PROTOCOL;
  }

  public boolean isValidateServerHostname() {
    return validateServerHostname;
  }

  public void setValidateServerHostname(boolean validateServerHostname) {
    this.validateServerHostname = validateServerHostname;
  }

  public boolean isValidateServerCertChain() {
    return validateServerCertChain;
  }

  public void setValidateServerCertChain(boolean validateServerCertChain) {
    this.validateServerCertChain = validateServerCertChain;
  }

  public String getClientCertFileName() {
    return clientCertFileName;
  }

  public void setClientCertFileName(String clientCertFileName) {
    this.clientCertFileName = clientCertFileName;
  }

  public String getClientCertPassword() {
    return clientCertPassword;
  }

  public void setClientCertPassword(String clientCertPassword) {
    this.clientCertPassword = clientCertPassword;
  }

  public String getTruststoreFilename() {
    return truststoreFilename;
  }

  public void setTruststoreFilename(String truststoreFilename) {
    this.truststoreFilename = truststoreFilename;
  }

  public int getConnectTimeoutInMs() {
    return connectTimeoutInMs;
  }

  public void setConnectTimeoutInMs(int connectTimeoutInMs) {
    this.connectTimeoutInMs = connectTimeoutInMs;
  }

  public int getReadTimeoutInMs() {
    return readTimeoutInMs;
  }

  public void setReadTimeoutInMs(int readTimeoutInMs) {
    this.readTimeoutInMs = readTimeoutInMs;
  }

  public RestAuthenticationMode getAuthenticationMode() {
    return authenticationMode;
  }

  public void setAuthenticationMode(RestAuthenticationMode authenticationMode) {
    this.authenticationMode = authenticationMode;
  }

  public String getBasicAuthUsername() {
    return basicAuthUsername;
  }

  public void setBasicAuthUsername(String basicAuthUsername) {
    this.basicAuthUsername = basicAuthUsername;
  }

  public String getBasicAuthPassword() {
    return basicAuthPassword;
  }

  public void setBasicAuthPassword(String basicAuthPassword) {
    this.basicAuthPassword = basicAuthPassword;
  }

  public String getSslProtocol() {
    return sslProtocol;
  }

  public void setSslProtocol(String sslProtocol) {
    this.sslProtocol = sslProtocol;
  }

  /**
   * Returns Client configured for SSL
   */
  public Client getClient() throws Exception {

    switch (authenticationMode) {
      case SSL_BASIC:
      case SSL_CERT:
        return getClient(true);

      default:
        // return basic non-authenticating HTTP client
        return getClient(false);
    }

  }

  protected void setupSecureSocketLayerClientConfig(ClientConfig clientConfig) throws Exception {
    // Check to see if we need to perform proper validation of
    // the certificate chains.
    TrustManager[] trustAllCerts = null;
    if (validateServerCertChain) {
      if (truststoreFilename != null) {
        System.setProperty(TRUST_STORE_PROPERTY, truststoreFilename);
      } else {
        throw new IllegalArgumentException("Trust store filename must be set!");
      }

    } else {

      // We aren't validating certificates, so create a trust manager that does
      // not validate certificate chains.
      trustAllCerts = new TrustManager[] {new X509TrustManager() {
        public X509Certificate[] getAcceptedIssuers() {
          return null;
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) {}

        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
      }};
    }

    // Set up the SSL context, keystore, etc. to use for our connection
    // to the AAI.
    SSLContext ctx = SSLContext.getInstance(sslProtocol);
    KeyManagerFactory kmf = KeyManagerFactory.getInstance(KEYSTORE_ALGORITHM);
    KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);

    char[] pwd = null;
    if (clientCertPassword != null) {
      pwd = clientCertPassword.toCharArray();
    }

    if (clientCertFileName != null) {
      FileInputStream fin = new FileInputStream(clientCertFileName);

      // Load the keystore and initialize the key manager factory.
      ks.load(fin, pwd);
      kmf.init(ks, pwd);

      ctx.init(kmf.getKeyManagers(), trustAllCerts, null);
    } else {
      ctx.init(null, trustAllCerts, null);
    }

    // Are we performing validation of the server host name?
    if (validateServerHostname) {
      clientConfig.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
          new HTTPSProperties(null, ctx));

    } else {
      clientConfig.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
          new HTTPSProperties(new HostnameVerifier() {
            @Override
            public boolean verify(String str, SSLSession sslSession) {
              return true;
            }
          }, ctx));
    }
  }


  /**
   * Returns client instance
   * 
   * @param useSsl - used to configure the client with an ssl-context or just plain http
   */
  protected Client getClient(boolean useSsl) throws Exception {

    ClientConfig clientConfig = new DefaultClientConfig();

    if (useSsl) {
      setupSecureSocketLayerClientConfig(clientConfig);
    }

    // Finally, create and initialize our client...
    Client client = null;
    client = Client.create(clientConfig);
    client.setConnectTimeout(connectTimeoutInMs);
    client.setReadTimeout(readTimeoutInMs);

    // ...and return it to the caller.
    return client;
  }

  public String getBasicAuthenticationCredentials() {

    String usernameAndPassword = getBasicAuthUsername() + ":" + getBasicAuthPassword();
    return "Basic " + java.util.Base64.getEncoder().encodeToString(usernameAndPassword.getBytes());
  }

  /* 
   * Added a little bit of logic to obfuscate passwords that could be logged out
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "RestClientBuilder [validateServerHostname=" + validateServerHostname
        + ", validateServerCertChain=" + validateServerCertChain + ", "
        + (clientCertFileName != null ? "clientCertFileName=" + clientCertFileName + ", " : "")
        + (clientCertPassword != null
            ? "clientCertPassword="
                + java.util.Base64.getEncoder().encodeToString(clientCertPassword.getBytes()) + ", "
            : "")
        + (truststoreFilename != null ? "truststoreFilename=" + truststoreFilename + ", " : "")
        + "connectTimeoutInMs=" + connectTimeoutInMs + ", readTimeoutInMs=" + readTimeoutInMs + ", "
        + (authenticationMode != null ? "authenticationMode=" + authenticationMode + ", " : "")
        + (basicAuthUsername != null ? "basicAuthUsername=" + basicAuthUsername + ", " : "")
        + (basicAuthPassword != null ? "basicAuthPassword="
            + java.util.Base64.getEncoder().encodeToString(basicAuthPassword.getBytes()) : "")
        + "]";
  }

}
