package org.openecomp.restclient.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.openecomp.restclient.enums.RestAuthenticationMode;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

/**
 * This suite of tests is intended to exercise the functionality of the generice REST client
 * builder.
 */
public class RestClientBuilderTest {

  /**
   * Test case initialization
   * 
   * @throws Exception the exception
   */
  @Before
  public void init() throws Exception {
  }
  
  private String generateAuthorizationHeaderValue(String username, String password) {
    String usernameAndPassword = username + ":" + password;
    return "Basic " + java.util.Base64.getEncoder().encodeToString(usernameAndPassword.getBytes());
  }
  
  @Test
  public void validateAccesors() {
    
    RestClientBuilder restClientBuilder = new RestClientBuilder();
    
    // test defaults
    assertEquals(restClientBuilder.isValidateServerHostname(), RestClientBuilder.DEFAULT_VALIDATE_SERVER_HOST);
    assertEquals(restClientBuilder.isValidateServerCertChain(), RestClientBuilder.DEFAULT_VALIDATE_CERT_CHAIN);
    assertEquals(restClientBuilder.getClientCertFileName(), RestClientBuilder.DEFAULT_CLIENT_CERT_FILENAME);
    assertEquals(restClientBuilder.getClientCertPassword(), RestClientBuilder.DEFAULT_CERT_PASSWORD);
    assertEquals(restClientBuilder.getTruststoreFilename(), RestClientBuilder.DEFAULT_TRUST_STORE_FILENAME);
    assertEquals(restClientBuilder.getConnectTimeoutInMs(), RestClientBuilder.DEFAULT_CONNECT_TIMEOUT_MS);
    assertEquals(restClientBuilder.getReadTimeoutInMs(), RestClientBuilder.DEFAULT_READ_TIMEOUT_MS);
    assertEquals(restClientBuilder.getAuthenticationMode(), RestClientBuilder.DEFAULT_AUTH_MODE);
    assertEquals(restClientBuilder.getBasicAuthUsername(), RestClientBuilder.DEFAULT_BASIC_AUTH_USERNAME);
    assertEquals(restClientBuilder.getBasicAuthPassword(), RestClientBuilder.DEFAULT_BASIC_AUTH_PASSWORD);
    
    restClientBuilder.setAuthenticationMode(RestAuthenticationMode.UNKNOWN_MODE);
    restClientBuilder.setBasicAuthPassword("password");
    restClientBuilder.setBasicAuthUsername("username");
    restClientBuilder.setClientCertFileName("filename");
    restClientBuilder.setClientCertPassword("password");
    restClientBuilder.setConnectTimeoutInMs(12345);
    restClientBuilder.setReadTimeoutInMs(54321);
    restClientBuilder.setTruststoreFilename("truststore");
    restClientBuilder.setValidateServerCertChain(true);
    restClientBuilder.setValidateServerHostname(true);
    
    assertEquals(restClientBuilder.isValidateServerHostname(), true);
    assertEquals(restClientBuilder.isValidateServerCertChain(), true);
    assertEquals(restClientBuilder.getClientCertFileName(), "filename");
    assertEquals(restClientBuilder.getClientCertPassword(), "password");
    assertEquals(restClientBuilder.getTruststoreFilename(), "truststore");
    assertEquals(restClientBuilder.getConnectTimeoutInMs(), 12345);
    assertEquals(restClientBuilder.getReadTimeoutInMs(), 54321);
    assertEquals(restClientBuilder.getAuthenticationMode(), RestAuthenticationMode.UNKNOWN_MODE);
    assertEquals(restClientBuilder.getBasicAuthUsername(), "username");
    assertEquals(restClientBuilder.getBasicAuthPassword(), "password");
    
    assertEquals(restClientBuilder.getBasicAuthenticationCredentials(),
        generateAuthorizationHeaderValue("username", "password"));

    assertTrue(restClientBuilder.toString().contains("RestClientBuilder"));

  }
  
  @Test
  public void validateNoAuthClientCreation() throws Exception {
    
    RestClientBuilder restClientBuilder = new RestClientBuilder();
    
    restClientBuilder.setAuthenticationMode(RestAuthenticationMode.HTTP_NOAUTH);
    restClientBuilder.setConnectTimeoutInMs(12345);
    restClientBuilder.setReadTimeoutInMs(54321);
    
    Client client = restClientBuilder.getClient();
    assertNotNull(client);
    assertNull(client.getProperties().get(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES));
  }
  
  
  @Test
  public void validateUnknownModeCreateNoAuthClient() throws Exception {
    
    RestClientBuilder restClientBuilder = new RestClientBuilder();
    
    restClientBuilder.setAuthenticationMode(RestAuthenticationMode.UNKNOWN_MODE);
    restClientBuilder.setConnectTimeoutInMs(12345);
    restClientBuilder.setReadTimeoutInMs(54321);
    
    Client client = restClientBuilder.getClient();
    assertNotNull(client);
    assertNull(client.getProperties().get(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES));
  }

  @Test
  public void validateBasicAuthSslClient() throws Exception {
    
    RestClientBuilder restClientBuilder = new RestClientBuilder();
    
    restClientBuilder.setAuthenticationMode(RestAuthenticationMode.SSL_BASIC);
    restClientBuilder.setConnectTimeoutInMs(12345);
    restClientBuilder.setReadTimeoutInMs(54321);
    restClientBuilder.setBasicAuthUsername("username");
    restClientBuilder.setBasicAuthPassword("password");
    
    Client client = restClientBuilder.getClient();
   
    Object sslPropertiesObj = client.getProperties().get(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES);
    HTTPSProperties sslProps = null;
    if ( sslPropertiesObj instanceof HTTPSProperties ) {
      sslProps = (HTTPSProperties)sslPropertiesObj;
      assertNotNull(sslProps.getHostnameVerifier());
    } else {
      fail("Unexpected value for https properties object");
    }
    
  }

  @Test
  public void validateSslCertClient_noHostOrCertChainValidation() throws Exception {
    
    RestClientBuilder restClientBuilder = new RestClientBuilder();
    
    restClientBuilder.setAuthenticationMode(RestAuthenticationMode.SSL_CERT);
    restClientBuilder.setConnectTimeoutInMs(12345);
    restClientBuilder.setReadTimeoutInMs(54321);
    restClientBuilder.setValidateServerCertChain(false);
    restClientBuilder.setValidateServerHostname(false);
    
    Client client = restClientBuilder.getClient();
   
    Object sslPropertiesObj = client.getProperties().get(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES);
    HTTPSProperties sslProps = null;
    if ( sslPropertiesObj instanceof HTTPSProperties ) {
      sslProps = (HTTPSProperties)sslPropertiesObj;
      assertNotNull(sslProps.getHostnameVerifier());
    } else {
      fail("Unexpected value for https properties object");
    }  }
  
  @Test
  public void validateSslCertClient_hostOnlyValidation() throws Exception {
    
    RestClientBuilder restClientBuilder = new RestClientBuilder();
    
    restClientBuilder.setAuthenticationMode(RestAuthenticationMode.SSL_CERT);
    restClientBuilder.setConnectTimeoutInMs(12345);
    restClientBuilder.setReadTimeoutInMs(54321);
    restClientBuilder.setValidateServerCertChain(false);
    restClientBuilder.setValidateServerHostname(true);
    
    Client client = restClientBuilder.getClient();
   
    Object sslPropertiesObj = client.getProperties().get(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES);
    HTTPSProperties sslProps = null;
    if ( sslPropertiesObj instanceof HTTPSProperties ) {
      sslProps = (HTTPSProperties)sslPropertiesObj;
      assertNull(sslProps.getHostnameVerifier());
    } else {
      fail("Unexpected value for https properties object");
    }
   }
  
  @Test
  public void validateSslCertClient_certChainOnlyValidation() throws Exception {
    
    RestClientBuilder restClientBuilder = new RestClientBuilder();
    
    restClientBuilder.setAuthenticationMode(RestAuthenticationMode.SSL_CERT);
    restClientBuilder.setConnectTimeoutInMs(12345);
    restClientBuilder.setReadTimeoutInMs(54321);
    restClientBuilder.setValidateServerCertChain(true);
    restClientBuilder.setValidateServerHostname(false);
    restClientBuilder.setTruststoreFilename("truststore");
    restClientBuilder.setClientCertPassword(null);
    
    Client client = restClientBuilder.getClient();
   
    Object sslPropertiesObj = client.getProperties().get(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES);
    HTTPSProperties sslProps = null;
    if ( sslPropertiesObj instanceof HTTPSProperties ) {
      sslProps = (HTTPSProperties)sslPropertiesObj;
      assertNotNull(sslProps.getHostnameVerifier());
    } else {
      fail("Unexpected value for https properties object");
    }
  }
  
  @Test
  public void validateSslCertClient_withHostAndCertChainValidation() throws Exception {
    
    RestClientBuilder restClientBuilder = new RestClientBuilder();
    
    restClientBuilder.setAuthenticationMode(RestAuthenticationMode.SSL_CERT);
    restClientBuilder.setConnectTimeoutInMs(12345);
    restClientBuilder.setReadTimeoutInMs(54321);
    restClientBuilder.setValidateServerCertChain(true);
    restClientBuilder.setValidateServerHostname(true);
    restClientBuilder.setClientCertPassword("password");
    restClientBuilder.setTruststoreFilename("truststore");
    
    Client client = restClientBuilder.getClient();
   
    Object sslPropertiesObj = client.getProperties().get(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES);
    HTTPSProperties sslProps = null;
    if ( sslPropertiesObj instanceof HTTPSProperties ) {
      sslProps = (HTTPSProperties)sslPropertiesObj;
      assertNull(sslProps.getHostnameVerifier());
    } else {
      fail("Unexpected value for https properties object");
    }  }
  
  @Test (expected=IllegalArgumentException.class)
  public void validateSslCertClient_illegalArgumentExceptionWhenTruststoreIsNull() throws Exception {
    
    RestClientBuilder restClientBuilder = new RestClientBuilder();
    
    restClientBuilder.setAuthenticationMode(RestAuthenticationMode.SSL_CERT);
    restClientBuilder.setConnectTimeoutInMs(12345);
    restClientBuilder.setReadTimeoutInMs(54321);
    restClientBuilder.setValidateServerCertChain(true);
    restClientBuilder.setValidateServerHostname(true);
    restClientBuilder.setTruststoreFilename(null);
    
    /*
     * Creating the client in this scenario will cause an IllegalArgumentException caused by the
     * truststore being null
     */
    Client client = restClientBuilder.getClient();
   
  }
  
    
}
