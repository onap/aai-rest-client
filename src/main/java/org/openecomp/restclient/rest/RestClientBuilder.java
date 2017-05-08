/**
 * ============LICENSE_START=======================================================
 * RestClient
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property.
 * Copyright © 2017 Amdocs
 * All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 *
 * ECOMP and OpenECOMP are trademarks
 * and service marks of AT&T Intellectual Property.
 */
package org.openecomp.restclient.rest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * This is a generic REST Client builder with flexible security validation. Sometimes it's nice to
 * be able to disable server chain cert validation and hostname validation to work-around lab
 * issues, but at the same time be able to provide complete validation with client cert + hostname +
 * server cert chain validation.  
 * I used the ModelLoader REST client as a base and merged in the TSUI client I wrote which also
 * validates the server hostname and server certificate chain.
 * 
 * @author DAVEA
 *
 */
public class RestClientBuilder {

  public static final boolean DEFAULT_VALIDATE_SERVER_HOST = false;
  public static final boolean DEFAULT_VALIDATE_CERT_CHAIN = false;
  public static final String DEFAULT_CLIENT_CERT_FILENAME = null;
  public static final String DEFAULT_CERT_PASSWORD = null;
  public static final String DEFAULT_TRUST_STORE_FILENAME = null;
  public static final int DEFAULT_CONNECT_TIMEOUT_MS = 60000;
  public static final int DEFAULT_READ_TIMEOUT_MS = 60000;

  private static final String SSL_PROTOCOL = "TLS";
  private static final String KEYSTORE_ALGORITHM = "SunX509";
  private static final String KEYSTORE_TYPE = "PKCS12";

  /*
   * TODO: implement fluent interface?
   */

  private boolean validateServerHostname;
  private boolean validateServerCertChain;
  private String clientCertFileName;
  private String clientCertPassword;
  private String truststoreFilename;
  private int connectTimeoutInMs;
  private int readTimeoutInMs;

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

  /**
   * Returns Client.
   */
  public Client getClient() throws Exception {

    ClientConfig clientConfig = new DefaultClientConfig();

    // Check to see if we need to perform proper validation of
    // the certificate chains.
    TrustManager[] trustAllCerts = null;
    if (validateServerCertChain) {
      if (truststoreFilename != null) {
        System.setProperty("javax.net.ssl.trustStore", truststoreFilename);
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
    SSLContext ctx = SSLContext.getInstance(SSL_PROTOCOL);
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

    // Finally, create and initialize our client...
    Client client = null;
    client = Client.create(clientConfig);
    client.setConnectTimeout(connectTimeoutInMs);
    client.setReadTimeout(readTimeoutInMs);

    // ...and return it to the caller.
    return client;
  }
}
