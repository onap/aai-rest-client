package org.openecomp.restclient.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openecomp.restclient.enums.RestAuthenticationMode;
import org.openecomp.restclient.rest.RestClientBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class RestClientTest {

  private RestClientBuilder mockClientBuilder;
  private Client mockedClient;
  
  /**
   * Test case initialization
   * 
   * @throws Exception the exception
   */
  @Before
  public void init() throws Exception {
    mockClientBuilder = Mockito.mock( RestClientBuilder.class );
    mockedClient = Mockito.mock( Client.class );
  }
  
  @Test
  public void validateConstructors() {
    
    RestClient restClient = new RestClient();
    assertNotNull(restClient);
    
    restClient = null;
    restClient = new RestClient( mockClientBuilder );
    assertNotNull(restClient);
    
  }
  
  @Test
  public void validateBasicClientConstruction() throws Exception {
    
    Mockito.when( mockClientBuilder.getClient() ).thenReturn(mockedClient);
    
    RestClient restClient = new RestClient( mockClientBuilder );
    assertNotNull(restClient);
    
    Client client = restClient.authenticationMode(RestAuthenticationMode.HTTP_NOAUTH)
        .connectTimeoutMs(1000).readTimeoutMs(500).getClient();
   
    assertNotNull(client);
    
  }
  
  @Test
  public void validateClientWithSslBasicAuthConstruction() throws Exception {

    Mockito.when( mockClientBuilder.getClient() ).thenReturn(mockedClient);
    
    RestClient restClient = new RestClient( mockClientBuilder );
    assertNotNull(restClient);
    
    Client client = restClient.authenticationMode(RestAuthenticationMode.SSL_BASIC)
        .connectTimeoutMs(1000).readTimeoutMs(500).basicAuthPassword("password")
        .basicAuthUsername("username").getClient();
   
    assertNotNull(client);
    
  }
  
  @Test
  public void validateClientWithSslCertConstruction() throws Exception {
    
    Mockito.when( mockClientBuilder.getClient() ).thenReturn(mockedClient);
    
    RestClient restClient = new RestClient( mockClientBuilder );
    assertNotNull(restClient);
    
    Client client =
        restClient.authenticationMode(RestAuthenticationMode.SSL_CERT).connectTimeoutMs(1000)
            .readTimeoutMs(500).clientCertFile("cert").clientCertPassword("password").getClient();
   
    assertNotNull(client);
    
    client = null;
    client = restClient.authenticationMode(RestAuthenticationMode.SSL_CERT).connectTimeoutMs(1000)
        .readTimeoutMs(500).clientCertFile("cert").clientCertPassword("password")
        .validateServerCertChain(true).validateServerHostname(true).getClient();

    assertNotNull(client);

    client = null;
    client = restClient.authenticationMode(RestAuthenticationMode.SSL_CERT).connectTimeoutMs(1000)
        .readTimeoutMs(500).clientCertFile("cert").clientCertPassword("password")
        .trustStore("truststore").getClient();

    assertNotNull(client);
    
  }
  
  @Test
  public void validateSuccessfulPut() throws Exception {
    
    Mockito.when( mockClientBuilder.getClient() ).thenReturn(mockedClient);
    
    WebResource mockedWebResource = Mockito.mock(WebResource.class);
    Builder mockedBuilder = Mockito.mock(Builder.class);
    ClientResponse mockedClientResponse = Mockito.mock(ClientResponse.class);
    
    Mockito.when( mockedClient.resource(Mockito.anyString())).thenReturn( mockedWebResource );
    Mockito.when(mockedWebResource.accept(Mockito.<MediaType>anyVararg())).thenReturn( mockedBuilder );
    Mockito.when(mockedBuilder.put(Mockito.any(Class.class))).thenReturn(mockedClientResponse);

    /*
     * Finally the elements we want to validate
     */
    
    Mockito.when(mockedClientResponse.getStatus()).thenReturn(200);
    Mockito.when(mockedClientResponse.getEntity(String.class)).thenReturn("hello");
    Mockito.when(mockedClientResponse.getHeaders()).thenReturn(new MultivaluedMapImpl());

    RestClient restClient = new RestClient( mockClientBuilder );
    
    assertNotNull(restClient);
    
    restClient =
        restClient.authenticationMode(RestAuthenticationMode.SSL_CERT).connectTimeoutMs(1000)
            .readTimeoutMs(500).clientCertFile("cert").clientCertPassword("password");
    
    assertNotNull(restClient);
    
    MultivaluedMap<String, String> headers = new MultivaluedMapImpl();

    OperationResult result = restClient.put("http://localhost:9000/aai/v7", "", headers, MediaType.APPLICATION_JSON_TYPE,
        MediaType.APPLICATION_JSON_TYPE);
    
    assertEquals(200, result.getResultCode());
    assertNotNull(result.getResult());
    assertNull(result.getFailureCause());
    
  }
  
  @Test
  public void validateSuccessfulPost() throws Exception {
    
    Mockito.when( mockClientBuilder.getClient() ).thenReturn(mockedClient);
    
    WebResource mockedWebResource = Mockito.mock(WebResource.class);
    Builder mockedBuilder = Mockito.mock(Builder.class);
    ClientResponse mockedClientResponse = Mockito.mock(ClientResponse.class);
    
    Mockito.when( mockedClient.resource(Mockito.anyString())).thenReturn( mockedWebResource );
    Mockito.when(mockedWebResource.accept(Mockito.<MediaType>anyVararg())).thenReturn( mockedBuilder );
    Mockito.when(mockedBuilder.post(Mockito.any(Class.class))).thenReturn(mockedClientResponse);

    /*
     * Finally the elements we want to validate
     */
    
    Mockito.when(mockedClientResponse.getStatus()).thenReturn(200);
    Mockito.when(mockedClientResponse.getEntity(String.class)).thenReturn("hello");
    Mockito.when(mockedClientResponse.getHeaders()).thenReturn(new MultivaluedMapImpl());

    RestClient restClient = new RestClient( mockClientBuilder );
    
    assertNotNull(restClient);
    
    restClient =
        restClient.authenticationMode(RestAuthenticationMode.SSL_CERT).connectTimeoutMs(1000)
            .readTimeoutMs(500).clientCertFile("cert").clientCertPassword("password");
    
    assertNotNull(restClient);
    
    MultivaluedMap<String, String> headers = new MultivaluedMapImpl();

    OperationResult result = restClient.post("http://localhost:9000/aai/v7", "", headers, MediaType.APPLICATION_JSON_TYPE,
        MediaType.APPLICATION_JSON_TYPE);
    
    assertEquals(200, result.getResultCode());
    assertNotNull(result.getResult());
    assertNull(result.getFailureCause());
    
  }
  
  @Test
  public void validateSuccessfulGet() throws Exception {
    
    Mockito.when( mockClientBuilder.getClient() ).thenReturn(mockedClient);
    
    WebResource mockedWebResource = Mockito.mock(WebResource.class);
    Builder mockedBuilder = Mockito.mock(Builder.class);
    ClientResponse mockedClientResponse = Mockito.mock(ClientResponse.class);
    
    Mockito.when( mockedClient.resource(Mockito.anyString())).thenReturn( mockedWebResource );
    Mockito.when(mockedWebResource.accept(Mockito.<MediaType>anyVararg())).thenReturn( mockedBuilder );
    Mockito.when(mockedBuilder.get(Mockito.any(Class.class))).thenReturn(mockedClientResponse);

    /*
     * Finally the elements we want to validate
     */
    
    Mockito.when(mockedClientResponse.getStatus()).thenReturn(200);
    Mockito.when(mockedClientResponse.getEntity(String.class)).thenReturn("hello");
    Mockito.when(mockedClientResponse.getHeaders()).thenReturn(new MultivaluedMapImpl());

    RestClient restClient = new RestClient( mockClientBuilder );
    
    assertNotNull(restClient);
    
    restClient =
        restClient.authenticationMode(RestAuthenticationMode.SSL_CERT).connectTimeoutMs(1000)
            .readTimeoutMs(500).clientCertFile("cert").clientCertPassword("password");
    
    assertNotNull(restClient);
    
    MultivaluedMap<String, String> headers = new MultivaluedMapImpl();

    OperationResult result =
        restClient.get("http://localhost:9000/aai/v7", headers, MediaType.APPLICATION_JSON_TYPE);
    
    assertEquals(200, result.getResultCode());
    assertNotNull(result.getResult());
    assertNull(result.getFailureCause());
    
  }
  
  @Test
  public void validateSuccessfulGetWithBasicAuth() throws Exception {
    
    Mockito.when( mockClientBuilder.getClient() ).thenReturn(mockedClient);
    
    WebResource mockedWebResource = Mockito.mock(WebResource.class);
    Builder mockedBuilder = Mockito.mock(Builder.class);
    ClientResponse mockedClientResponse = Mockito.mock(ClientResponse.class);
    
    Mockito.when( mockedClient.resource(Mockito.anyString())).thenReturn( mockedWebResource );
    Mockito.when(mockedWebResource.accept(Mockito.<MediaType>anyVararg())).thenReturn( mockedBuilder );
    Mockito.when(mockedBuilder.get(Mockito.any(Class.class))).thenReturn(mockedClientResponse);

    /*
     * Finally the elements we want to validate
     */
    
    Mockito.when(mockedClientResponse.getStatus()).thenReturn(200);
    Mockito.when(mockedClientResponse.getEntity(String.class)).thenReturn("hello");
    Mockito.when(mockedClientResponse.getHeaders()).thenReturn(new MultivaluedMapImpl());

    RestClient restClient = new RestClient( mockClientBuilder );
    
    assertNotNull(restClient);
    
    restClient =
        restClient.authenticationMode(RestAuthenticationMode.SSL_BASIC).connectTimeoutMs(1000)
            .readTimeoutMs(500).basicAuthUsername("username").basicAuthUsername("password");
    
    assertNotNull(restClient);
    
    MultivaluedMap<String, String> headers = new MultivaluedMapImpl();

    OperationResult result =
        restClient.get("http://localhost:9000/aai/v7", headers, MediaType.APPLICATION_JSON_TYPE);
    
    assertEquals(200, result.getResultCode());
    assertNotNull(result.getResult());
    assertNull(result.getFailureCause());
    
  }
  
  @Test
  public void validateResourceNotFoundGet() throws Exception {
    
    Mockito.when( mockClientBuilder.getClient() ).thenReturn(mockedClient);
    
    WebResource mockedWebResource = Mockito.mock(WebResource.class);
    Builder mockedBuilder = Mockito.mock(Builder.class);
    ClientResponse mockedClientResponse = Mockito.mock(ClientResponse.class);
    
    Mockito.when( mockedClient.resource(Mockito.anyString())).thenReturn( mockedWebResource );
    Mockito.when(mockedWebResource.accept(Mockito.<MediaType>anyVararg())).thenReturn( mockedBuilder );
    Mockito.when(mockedBuilder.get(Mockito.any(Class.class))).thenReturn(mockedClientResponse);

    /*
     * Finally the elements we want to validate
     */
    
    Mockito.when(mockedClientResponse.getStatus()).thenReturn(404);
    Mockito.when(mockedClientResponse.getEntity(String.class)).thenReturn("RNF");
    Mockito.when(mockedClientResponse.getHeaders()).thenReturn(new MultivaluedMapImpl());

    RestClient restClient = new RestClient( mockClientBuilder );
    
    assertNotNull(restClient);
    
    restClient =
        restClient.authenticationMode(RestAuthenticationMode.SSL_CERT).connectTimeoutMs(1000)
            .readTimeoutMs(500).clientCertFile("cert").clientCertPassword("password");
    
    assertNotNull(restClient);
    
    MultivaluedMap<String, String> headers = new MultivaluedMapImpl();

    OperationResult result =
        restClient.get("http://localhost:9000/aai/v7", headers, MediaType.APPLICATION_JSON_TYPE);
    
    assertEquals(404, result.getResultCode());
    assertNull(result.getResult());
    assertNotNull(result.getFailureCause());
    
  }
  
  @Test
  public void validateHealthCheck() throws Exception {
    
    Mockito.when( mockClientBuilder.getClient() ).thenReturn(mockedClient);
    
    WebResource mockedWebResource = Mockito.mock(WebResource.class);
    Builder mockedBuilder = Mockito.mock(Builder.class);
    ClientResponse mockedClientResponse = Mockito.mock(ClientResponse.class);
    
    Mockito.when( mockedClient.resource(Mockito.anyString())).thenReturn( mockedWebResource );
    Mockito.when(mockedWebResource.accept(Mockito.<MediaType>anyVararg())).thenReturn( mockedBuilder );
    Mockito.when(mockedBuilder.get(Mockito.any(Class.class))).thenReturn(mockedClientResponse);

    /*
     * Finally the elements we want to validate
     */
    
    Mockito.when(mockedClientResponse.getStatus()).thenReturn(200);
    Mockito.when(mockedClientResponse.getEntity(String.class)).thenReturn("hello");
    Mockito.when(mockedClientResponse.getHeaders()).thenReturn(new MultivaluedMapImpl());

    RestClient restClient = new RestClient( mockClientBuilder );
    
    assertNotNull(restClient);
    
    restClient =
        restClient.authenticationMode(RestAuthenticationMode.SSL_CERT).connectTimeoutMs(1000)
            .readTimeoutMs(500).clientCertFile("cert").clientCertPassword("password");
    
    assertNotNull(restClient);
    
    MultivaluedMap<String, String> headers = new MultivaluedMapImpl();

    boolean targetServiceHealthy =
        restClient.healthCheck("http://localhost:9000/aai/util/echo", "startSerice", "targetService");
    
    assertEquals(true, targetServiceHealthy);
    
  }
  
  @Test
  public void validateHealthCheckFailureWith403() throws Exception {
    
    Mockito.when( mockClientBuilder.getClient() ).thenReturn(mockedClient);
    
    WebResource mockedWebResource = Mockito.mock(WebResource.class);
    Builder mockedBuilder = Mockito.mock(Builder.class);
    ClientResponse mockedClientResponse = Mockito.mock(ClientResponse.class);
    
    Mockito.when( mockedClient.resource(Mockito.anyString())).thenReturn( mockedWebResource );
    Mockito.when(mockedWebResource.accept(Mockito.<MediaType>anyVararg())).thenReturn( mockedBuilder );
    Mockito.when(mockedBuilder.get(Mockito.any(Class.class))).thenReturn(mockedClientResponse);

    /*
     * Finally the elements we want to validate
     */
    
    Mockito.when(mockedClientResponse.getStatus()).thenReturn(403);
    Mockito.when(mockedClientResponse.getEntity(String.class)).thenReturn("hello");
    Mockito.when(mockedClientResponse.getHeaders()).thenReturn(new MultivaluedMapImpl());

    RestClient restClient = new RestClient( mockClientBuilder );
    
    assertNotNull(restClient);
    
    restClient =
        restClient.authenticationMode(RestAuthenticationMode.SSL_CERT).connectTimeoutMs(1000)
            .readTimeoutMs(500).clientCertFile("cert").clientCertPassword("password");
    
    assertNotNull(restClient);
    
    MultivaluedMap<String, String> headers = new MultivaluedMapImpl();

    boolean targetServiceHealthy =
        restClient.healthCheck("http://localhost:9000/aai/util/echo", "startSerice", "targetService");
    
    assertEquals(false, targetServiceHealthy);
    
  }
  
  @Test
  public void validateHealthCheckFailureWithThrownException() throws Exception {

    Mockito.when( mockClientBuilder.getClient() ).thenReturn(mockedClient);
    
    WebResource mockedWebResource = Mockito.mock(WebResource.class);
    Builder mockedBuilder = Mockito.mock(Builder.class);
    ClientResponse mockedClientResponse = Mockito.mock(ClientResponse.class);
    
    Mockito.when( mockedClient.resource(Mockito.anyString())).thenReturn( mockedWebResource );
    Mockito.when(mockedWebResource.accept(Mockito.<MediaType>anyVararg())).thenReturn( mockedBuilder );
    Mockito.when(mockedBuilder.get(Mockito.any(Class.class))).thenThrow(new IllegalArgumentException("error"));

    /*
     * Finally the elements we want to validate
     */
    
/*    Mockito.when(mockedClientResponse.getStatus()).thenReturn(403);
    Mockito.when(mockedClientResponse.getEntity(String.class)).thenReturn("hello");
    Mockito.when(mockedClientResponse.getHeaders()).thenReturn(new MultivaluedMapImpl());*/

    RestClient restClient = new RestClient( mockClientBuilder );
    
    assertNotNull(restClient);
    
    restClient =
        restClient.authenticationMode(RestAuthenticationMode.SSL_CERT).connectTimeoutMs(1000)
            .readTimeoutMs(500).clientCertFile("cert").clientCertPassword("password");
    
    assertNotNull(restClient);
    
    MultivaluedMap<String, String> headers = new MultivaluedMapImpl();

    boolean targetServiceHealthy =
        restClient.healthCheck("http://localhost:9000/aai/util/echo", "startSerice", "targetService");
    
    assertEquals(false, targetServiceHealthy);
    
  }
  @Test  
  public void validateSuccessfulGetWithRetries() throws Exception {
    
    RestClientBuilder myClientBuilder = Mockito.mock(RestClientBuilder.class);
    Client myClient = Mockito.mock(Client.class);
    
    Mockito.when(myClientBuilder.getClient()).thenReturn(myClient).thenReturn(myClient);
    
    WebResource mockedWebResource = Mockito.mock(WebResource.class);
    Builder mockedBuilder = Mockito.mock(Builder.class);
    ClientResponse mockedClientResponse = Mockito.mock(ClientResponse.class);
    
    Mockito.when( myClient.resource(Mockito.anyString())).thenReturn( mockedWebResource );
    Mockito.when(mockedWebResource.accept(Mockito.<MediaType>anyVararg())).thenReturn( mockedBuilder );
    Mockito.when(mockedBuilder.get(Mockito.any(Class.class))).thenReturn(mockedClientResponse);

    
    /*
     * Finally the elements we want to validate
     */

    Mockito.when(mockedClientResponse.getStatus()).thenReturn(408).thenReturn(200);

    Mockito.when(mockedClientResponse.getEntity(String.class)).thenReturn("error").thenReturn("ok");
    
    MultivaluedMap<String, String> emptyHeaderMap = new MultivaluedMapImpl();
    
    // Mockito is smart, the last recorded thenReturn is repeated successively
    // for all subsequent calls to the method.
    Mockito.when(mockedClientResponse.getHeaders()).thenReturn(emptyHeaderMap);

    RestClient restClient = new RestClient( myClientBuilder );
    
    assertNotNull(restClient);
    
    restClient =
        restClient.authenticationMode(RestAuthenticationMode.SSL_CERT).connectTimeoutMs(1000)
            .readTimeoutMs(500).clientCertFile("cert").clientCertPassword("password");
    
    assertNotNull(restClient);
    
    MultivaluedMap<String, String> headers = new MultivaluedMapImpl();

    OperationResult result =
        restClient.get("http://localhost:9000/aai/v7", headers, MediaType.APPLICATION_JSON_TYPE, 3);
    
    assertEquals(200, result.getResultCode());
    assertNotNull(result.getResult());
    assertNull(result.getFailureCause());
    
  }
  
  
  @Test  
  public void validateFailedGetWithRetriesCausedByResourceNotFound() throws Exception {
    
    RestClientBuilder myClientBuilder = Mockito.mock(RestClientBuilder.class);
    Client myClient = Mockito.mock(Client.class);
    
    Mockito.when(myClientBuilder.getClient()).thenReturn(myClient).thenReturn(myClient);
    
    WebResource mockedWebResource = Mockito.mock(WebResource.class);
    Builder mockedBuilder = Mockito.mock(Builder.class);
    ClientResponse mockedClientResponse = Mockito.mock(ClientResponse.class);
    
    Mockito.when( myClient.resource(Mockito.anyString())).thenReturn( mockedWebResource );
    Mockito.when(mockedWebResource.accept(Mockito.<MediaType>anyVararg())).thenReturn( mockedBuilder );
    Mockito.when(mockedBuilder.get(Mockito.any(Class.class))).thenReturn(mockedClientResponse);

    
    /*
     * Finally the elements we want to validate
     */

    Mockito.when(mockedClientResponse.getStatus()).thenReturn(404);

    Mockito.when(mockedClientResponse.getEntity(String.class)).thenReturn("error").thenReturn("ok");
    
    MultivaluedMap<String, String> emptyHeaderMap = new MultivaluedMapImpl();
    
    // Mockito is smart, the last recorded thenReturn is repeated successively
    // for all subsequent calls to the method.
    Mockito.when(mockedClientResponse.getHeaders()).thenReturn(emptyHeaderMap);

    RestClient restClient = new RestClient( myClientBuilder );
    
    assertNotNull(restClient);
    
    restClient =
        restClient.authenticationMode(RestAuthenticationMode.SSL_CERT).connectTimeoutMs(1000)
            .readTimeoutMs(500).clientCertFile("cert").clientCertPassword("password");
    
    assertNotNull(restClient);
    
    MultivaluedMap<String, String> headers = new MultivaluedMapImpl();

    OperationResult result =
        restClient.get("http://localhost:9000/aai/v7", headers, MediaType.APPLICATION_JSON_TYPE, 3);
    
    assertEquals(404, result.getResultCode());
    assertNull(result.getResult());
    assertNotNull(result.getFailureCause());
    
  }
  
  @Test
  public void validateFailedGetAfterMaxRetries() throws Exception {
    
    Mockito.when( mockClientBuilder.getClient() ).thenReturn(mockedClient);
    
    WebResource mockedWebResource = Mockito.mock(WebResource.class);
    Builder mockedBuilder = Mockito.mock(Builder.class);
    ClientResponse mockedClientResponse = Mockito.mock(ClientResponse.class);
    
    Mockito.when( mockedClient.resource(Mockito.anyString())).thenReturn( mockedWebResource );
    Mockito.when(mockedWebResource.accept(Mockito.<MediaType>anyVararg())).thenReturn( mockedBuilder );
    
    Mockito.when(mockedBuilder.get(Mockito.any(Class.class))).thenReturn(null);

    /*
     * Finally the elements we want to validate
     */
    
    Mockito.when(mockedClientResponse.getStatus()).thenReturn(500).thenReturn(500).thenReturn(500);

    Mockito.when(mockedClientResponse.getEntity(String.class)).thenReturn("error")
        .thenReturn("error").thenReturn("error");
    
    MultivaluedMap<String, String> emptyHeaderMap = new MultivaluedMapImpl();
    
    Mockito.when(mockedClientResponse.getHeaders()).thenReturn(emptyHeaderMap)
        .thenReturn(emptyHeaderMap).thenReturn(emptyHeaderMap);

    RestClient restClient = new RestClient( mockClientBuilder );
    
    assertNotNull(restClient);
    
    restClient =
        restClient.authenticationMode(RestAuthenticationMode.SSL_CERT).connectTimeoutMs(1000)
            .readTimeoutMs(500).clientCertFile("cert").clientCertPassword("password");
    
    assertNotNull(restClient);
    
    MultivaluedMap<String, String> headers = new MultivaluedMapImpl();

    OperationResult result =
        restClient.get("http://localhost:9000/aai/v7", headers, MediaType.APPLICATION_JSON_TYPE, 3);
    
    
    assertEquals(504, result.getResultCode());
    assertNull(result.getResult());
    assertNotNull(result.getFailureCause());
    
  }
  
  @Test
  public void validateSuccessfulDelete() throws Exception {
    
    Mockito.when( mockClientBuilder.getClient() ).thenReturn(mockedClient);
    
    WebResource mockedWebResource = Mockito.mock(WebResource.class);
    Builder mockedBuilder = Mockito.mock(Builder.class);
    ClientResponse mockedClientResponse = Mockito.mock(ClientResponse.class);
    
    Mockito.when( mockedClient.resource(Mockito.anyString())).thenReturn( mockedWebResource );
    Mockito.when(mockedWebResource.accept(Mockito.<MediaType>anyVararg())).thenReturn( mockedBuilder );
    Mockito.when(mockedBuilder.delete(Mockito.any(Class.class))).thenReturn(mockedClientResponse);

    /*
     * Finally the elements we want to validate
     */
    
    Mockito.when(mockedClientResponse.getStatus()).thenReturn(200);
    Mockito.when(mockedClientResponse.getEntity(String.class)).thenReturn("hello");
    Mockito.when(mockedClientResponse.getHeaders()).thenReturn(new MultivaluedMapImpl());

    RestClient restClient = new RestClient( mockClientBuilder );
    
    assertNotNull(restClient);
    
    restClient =
        restClient.authenticationMode(RestAuthenticationMode.SSL_CERT).connectTimeoutMs(1000)
            .readTimeoutMs(500).clientCertFile("cert").clientCertPassword("password");
    
    assertNotNull(restClient);
    
    MultivaluedMap<String, String> headers = new MultivaluedMapImpl();

    OperationResult result = restClient.delete("http://localhost:9000/aai/v7", headers, 
        MediaType.APPLICATION_JSON_TYPE);
    
    assertEquals(200, result.getResultCode());
    assertNotNull(result.getResult());
    assertNull(result.getFailureCause());
    
  }
  
  @Test
  public void validateSuccessfulHead() throws Exception {
    
    Mockito.when( mockClientBuilder.getClient() ).thenReturn(mockedClient);
    
    WebResource mockedWebResource = Mockito.mock(WebResource.class);
    Builder mockedBuilder = Mockito.mock(Builder.class);
    ClientResponse mockedClientResponse = Mockito.mock(ClientResponse.class);
    
    Mockito.when( mockedClient.resource(Mockito.anyString())).thenReturn( mockedWebResource );
    Mockito.when(mockedWebResource.accept(Mockito.<MediaType>anyVararg())).thenReturn( mockedBuilder );
    Mockito.when(mockedBuilder.head()).thenReturn(mockedClientResponse);

    /*
     * Finally the elements we want to validate
     */
    
    Mockito.when(mockedClientResponse.getStatus()).thenReturn(200);
    Mockito.when(mockedClientResponse.getEntity(String.class)).thenReturn("hello");
    Mockito.when(mockedClientResponse.getHeaders()).thenReturn(new MultivaluedMapImpl());

    RestClient restClient = new RestClient( mockClientBuilder );
    
    assertNotNull(restClient);
    
    restClient =
        restClient.authenticationMode(RestAuthenticationMode.SSL_CERT).connectTimeoutMs(1000)
            .readTimeoutMs(500).clientCertFile("cert").clientCertPassword("password");
    
    assertNotNull(restClient);
    
    MultivaluedMap<String, String> headers = new MultivaluedMapImpl();

    OperationResult result =
        restClient.head("http://localhost:9000/aai/v7", headers, MediaType.APPLICATION_JSON_TYPE);
    
    assertEquals(200, result.getResultCode());
    assertNotNull(result.getResult());
    assertNull(result.getFailureCause());
    
  }
  
  @Test
  public void validateSuccessfulPatch() throws Exception {
    
    Mockito.when( mockClientBuilder.getClient() ).thenReturn(mockedClient);
    
    WebResource mockedWebResource = Mockito.mock(WebResource.class);
    Builder mockedBuilder = Mockito.mock(Builder.class);
    ClientResponse mockedClientResponse = Mockito.mock(ClientResponse.class);
    
    Mockito.when( mockedClient.resource(Mockito.anyString())).thenReturn( mockedWebResource );
    Mockito.when(mockedWebResource.accept(Mockito.<MediaType>anyVararg())).thenReturn( mockedBuilder );
    Mockito.when(mockedBuilder.post(Mockito.any(Class.class))).thenReturn(mockedClientResponse);
    Mockito.when(mockedBuilder.header("X-HTTP-Method-Override", "PATCH")).thenReturn(mockedBuilder);
    /*
     * Finally the elements we want to validate
     */
    
    Mockito.when(mockedClientResponse.getStatus()).thenReturn(200);
    Mockito.when(mockedClientResponse.getEntity(String.class)).thenReturn("hello");
    Mockito.when(mockedClientResponse.getHeaders()).thenReturn(new MultivaluedMapImpl());

    RestClient restClient = new RestClient( mockClientBuilder );
    
    assertNotNull(restClient);
    
    restClient =
        restClient.authenticationMode(RestAuthenticationMode.SSL_CERT).connectTimeoutMs(1000)
            .readTimeoutMs(500).clientCertFile("cert").clientCertPassword("password");
    
    assertNotNull(restClient);
    
    MultivaluedMap<String, String> headers = new MultivaluedMapImpl();

    OperationResult result = restClient.patch("http://localhost:9000/aai/v7", "", headers, MediaType.APPLICATION_JSON_TYPE,
        MediaType.APPLICATION_JSON_TYPE);
    
    assertEquals(200, result.getResultCode());
    assertNotNull(result.getResult());
    assertNull(result.getFailureCause());
    
  }
    
}
