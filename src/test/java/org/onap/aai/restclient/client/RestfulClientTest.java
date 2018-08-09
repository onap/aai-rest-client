/*
 * ============LICENSE_START===========================================================================================
 * Copyright (c) 2017 AT&T Intellectual Property.
 * Copyright (c) 2017 Amdocs 
 * Modification Copyright (c) 2018 IBM. 
 * All rights reserved.
 * =====================================================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 * ============LICENSE_END================================================== ===========================================
 * 
 * ECOMP and OpenECOMP are trademarks and service marks of AT&T Intellectual Property.
 */
package org.onap.aai.restclient.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.onap.aai.restclient.client.OperationResult;
import org.onap.aai.restclient.client.RestClient;
import org.onap.aai.restclient.enums.RestAuthenticationMode;
import org.onap.aai.restclient.rest.RestClientBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class RestfulClientTest {

    private static final String TEST_URL = "http://localhost:9000/aai/v7";

    private final MultivaluedMapImpl emptyMap = new MultivaluedMapImpl();

    private RestClientBuilder mockClientBuilder;
    private Client mockedClient;
    private WebResource mockedWebResource;
    private Builder mockedBuilder;
    private ClientResponse mockedClientResponse;

    /**
     * Test case initialization
     * 
     * @throws Exception the exception
     */
    @SuppressWarnings("unchecked")
    @Before
    public void init() throws Exception {
        mockedClientResponse = Mockito.mock(ClientResponse.class);
        setResponseStatus(Response.Status.OK);
        Mockito.when(mockedClientResponse.getHeaders()).thenReturn(emptyMap);
        Mockito.when(mockedClientResponse.getEntity(String.class)).thenReturn("hello");

        mockedBuilder = Mockito.mock(Builder.class);
        Mockito.when(mockedBuilder.get(Mockito.any(Class.class))).thenReturn(mockedClientResponse);
        Mockito.when(mockedBuilder.post(Mockito.any(Class.class))).thenReturn(mockedClientResponse);
        Mockito.when(mockedBuilder.put(Mockito.any(Class.class))).thenReturn(mockedClientResponse);
        Mockito.when(mockedBuilder.delete(Mockito.any(Class.class))).thenReturn(mockedClientResponse);
        Mockito.when(mockedBuilder.head()).thenReturn(mockedClientResponse);

        mockedWebResource = Mockito.mock(WebResource.class);
        Mockito.when(mockedWebResource.accept(Mockito.<MediaType>anyVararg())).thenReturn(mockedBuilder);

        mockedClient = Mockito.mock(Client.class);
        Mockito.when(mockedClient.resource(Mockito.anyString())).thenReturn(mockedWebResource);

        mockClientBuilder = Mockito.mock(RestClientBuilder.class);
        Mockito.when(mockClientBuilder.getClient()).thenReturn(mockedClient);
    }

    @Test
    public void validateConstructors() {
        RestClient restClient = new RestClient();
        assertNotNull(restClient);
        restClient = new RestClient(mockClientBuilder);
        assertNotNull(restClient);
    }

    @Test
    public void validateBasicClientConstruction() throws Exception {
        Client client = new RestClient(mockClientBuilder).authenticationMode(RestAuthenticationMode.HTTP_NOAUTH)
                .connectTimeoutMs(1000).readTimeoutMs(500).getClient();
        assertNotNull(client);
    }

    @Test
    public void validateClientWithSslBasicAuthConstruction() throws Exception {
        Client client = new RestClient(mockClientBuilder).authenticationMode(RestAuthenticationMode.SSL_BASIC)
                .connectTimeoutMs(1000).readTimeoutMs(500).basicAuthPassword("password").basicAuthUsername("username")
                .getClient();
        assertNotNull(client);
    }

    @Test
    public void validateClientWithSslCertConstruction() throws Exception {
        // This test covers the standard SSL settings, i.e. no validation
        assertNotNull(buildClient());

        RestClient restClient = new RestClient(mockClientBuilder);

        // Test with validation enabled
        Client client = restClient.authenticationMode(RestAuthenticationMode.SSL_CERT).connectTimeoutMs(1000)
                .readTimeoutMs(500).clientCertFile("cert").clientCertPassword("password").validateServerCertChain(true)
                .validateServerHostname(true).getClient();
        assertNotNull(client);

        // Test with a trust store
        client = restClient.authenticationMode(RestAuthenticationMode.SSL_CERT).connectTimeoutMs(1000)
                .readTimeoutMs(500).clientCertFile("cert").clientCertPassword("password").trustStore("truststore")
                .getClient();
        assertNotNull(client);
    }

    @Test
    public void validateSuccessfulPut() throws Exception {
        RestClient restClient = buildClient();

        OperationResult result = restClient.put(TEST_URL, "", emptyMap, MediaType.APPLICATION_JSON_TYPE,
                MediaType.APPLICATION_JSON_TYPE);

        assertEquals(Response.Status.OK.getStatusCode(), result.getResultCode());
        assertNotNull(result.getResult());
        assertNull(result.getFailureCause());

        // Repeat the PUT operation, this time with a return code of 204
        setResponseToNoContent();
        result = restClient.put(TEST_URL, "", emptyMap, MediaType.APPLICATION_JSON_TYPE,
                MediaType.APPLICATION_JSON_TYPE);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), result.getResultCode());
        assertNull(result.getResult());
        assertNull(result.getFailureCause());
    }

    @Test
    public void validateSuccessfulPost() throws Exception {
        RestClient restClient = buildClient();

        OperationResult result = restClient.post(TEST_URL, "", emptyMap, MediaType.APPLICATION_JSON_TYPE,
                MediaType.APPLICATION_JSON_TYPE);

        assertEquals(Response.Status.OK.getStatusCode(), result.getResultCode());
        assertNotNull(result.getResult());
        assertNull(result.getFailureCause());

        // Repeat the POST operation, this time with a return code of 204
        setResponseToNoContent();
        result = restClient.post(TEST_URL, "", emptyMap, MediaType.APPLICATION_JSON_TYPE,
                MediaType.APPLICATION_JSON_TYPE);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), result.getResultCode());
        assertNull(result.getResult());
        assertNull(result.getFailureCause());
    }
    
    @Test
    public void validateSuccessfulPost_withMultivaluedHeader() throws Exception {
        RestClient restClient = buildClient();

        MultivaluedMapImpl headerMap = new MultivaluedMapImpl();
        
        headerMap.add("txnId", "123");
        headerMap.add("txnId", "456");
        headerMap.add("txnId", "789");

        OperationResult result = restClient.post(TEST_URL, "", headerMap, MediaType.APPLICATION_JSON_TYPE,
            MediaType.APPLICATION_JSON_TYPE);

        // capture the txnId header from the outgoing request  
        ArgumentCaptor<String> txnIdHeaderName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> txnIdHeaderValue = ArgumentCaptor.forClass(String.class);
        
        Mockito.verify(mockedBuilder, Mockito.atLeast(1)).header(txnIdHeaderName.capture(), txnIdHeaderValue.capture());
        assertEquals("123;456;789", txnIdHeaderValue.getValue());

        assertEquals(Response.Status.OK.getStatusCode(), result.getResultCode());
        assertNotNull(result.getResult());
        assertNull(result.getFailureCause());
    }

    @Test
    public void validateSuccessfulGet() throws Exception {
        OperationResult result = buildClient().get(TEST_URL, emptyMap, MediaType.APPLICATION_JSON_TYPE);

        assertEquals(Response.Status.OK.getStatusCode(), result.getResultCode());
        assertNotNull(result.getResult());
        assertNull(result.getFailureCause());
    }

    @Test
    public void validateSuccessfulGetWithBasicAuth() throws Exception {
        RestClient restClient = new RestClient(mockClientBuilder).authenticationMode(RestAuthenticationMode.SSL_BASIC)
                .connectTimeoutMs(1000).readTimeoutMs(500).basicAuthUsername("username").basicAuthUsername("password");

        OperationResult result = restClient.get(TEST_URL, emptyMap, MediaType.APPLICATION_JSON_TYPE);

        assertEquals(Response.Status.OK.getStatusCode(), result.getResultCode());
        assertNotNull(result.getResult());
        assertNull(result.getFailureCause());
    }

    @Test
    public void validateResourceNotFoundGet() throws Exception {
        setResponseStatus(Response.Status.NOT_FOUND);
        Mockito.when(mockedClientResponse.getEntity(String.class)).thenReturn("RNF");

        OperationResult result = buildClient().get(TEST_URL, emptyMap, MediaType.APPLICATION_JSON_TYPE);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), result.getResultCode());
        assertNull(result.getResult());
        assertNotNull(result.getFailureCause());
    }

    @Test
    public void validateHealthCheck() throws Exception {
        boolean targetServiceHealthy =
                buildClient().healthCheck("http://localhost:9000/aai/util/echo", "startSerice", "targetService");

        assertEquals(true, targetServiceHealthy);
    }

    @Test
    public void validateHealthCheckFailureWith403() throws Exception {
        Mockito.when(mockedClientResponse.getStatus()).thenReturn(Response.Status.FORBIDDEN.getStatusCode());

        boolean targetServiceHealthy =
                buildClient().healthCheck("http://localhost:9000/aai/util/echo", "startSerice", "targetService");

        assertEquals(false, targetServiceHealthy);
    }

    @Test
    public void validateHealthCheckFailureWithThrownException() throws Exception {
        Mockito.when(mockedBuilder.get(Mockito.any(Class.class))).thenThrow(new IllegalArgumentException("error"));

        boolean targetServiceHealthy =
                buildClient().healthCheck("http://localhost:9000/aai/util/echo", "startSerice", "targetService");

        assertEquals(false, targetServiceHealthy);
    }

    @Test
    public void validateSuccessfulGetWithRetries() throws Exception {
        Mockito.when(mockedClientResponse.getStatus()).thenReturn(408).thenReturn(Response.Status.OK.getStatusCode());
        Mockito.when(mockedClientResponse.getEntity(String.class)).thenReturn("error").thenReturn("ok");

        OperationResult result = buildClient().get(TEST_URL, emptyMap, MediaType.APPLICATION_JSON_TYPE, 3);

        assertEquals(Response.Status.OK.getStatusCode(), result.getResultCode());
        assertNotNull(result.getResult());
        assertNull(result.getFailureCause());

    }

    @Test
    public void validateFailedGetWithRetriesCausedByResourceNotFound() throws Exception {
        setResponseStatus(Response.Status.NOT_FOUND);
        Mockito.when(mockedClientResponse.getEntity(String.class)).thenReturn("error").thenReturn("ok");

        OperationResult result = buildClient().get(TEST_URL, emptyMap, MediaType.APPLICATION_JSON_TYPE, 3);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), result.getResultCode());
        assertNull(result.getResult());
        assertNotNull(result.getFailureCause());

    }

    @Test
    public void validateFailedGetAfterMaxRetries() throws Exception {
        setResponseStatus(Response.Status.INTERNAL_SERVER_ERROR);
        Mockito.when(mockedClientResponse.getEntity(String.class)).thenReturn("error");

        OperationResult result = buildClient().get(TEST_URL, emptyMap, MediaType.APPLICATION_JSON_TYPE, 3);

        assertEquals(504, result.getResultCode());
        assertNull(result.getResult());
        assertNotNull(result.getFailureCause());

    }

    @Test
    public void validateSuccessfulDelete() throws Exception {
        RestClient restClient = buildClient();

        OperationResult result = restClient.delete(TEST_URL, emptyMap, MediaType.APPLICATION_JSON_TYPE);

        assertEquals(Response.Status.OK.getStatusCode(), result.getResultCode());
        assertNotNull(result.getResult());
        assertNull(result.getFailureCause());

        // Repeat the DELETE operation, this time with a return code of 204
        setResponseToNoContent();
        result = restClient.delete(TEST_URL, emptyMap, MediaType.APPLICATION_JSON_TYPE);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), result.getResultCode());
        assertNull(result.getResult());
        assertNull(result.getFailureCause());
    }


    @Test
    public void validateSuccessfulHead() throws Exception {
        OperationResult result = buildClient().head(TEST_URL, emptyMap, MediaType.APPLICATION_JSON_TYPE);

        assertEquals(Response.Status.OK.getStatusCode(), result.getResultCode());
        assertNotNull(result.getResult());
        assertNull(result.getFailureCause());

    }

    @Test
    public void validateSuccessfulPatch() throws Exception {
        Mockito.when(mockedBuilder.header("X-HTTP-Method-Override", "PATCH")).thenReturn(mockedBuilder);
        OperationResult result = buildClient().patch(TEST_URL, "", emptyMap, MediaType.APPLICATION_JSON_TYPE,
                MediaType.APPLICATION_JSON_TYPE);

        assertEquals(Response.Status.OK.getStatusCode(), result.getResultCode());
        assertNotNull(result.getResult());
        assertNull(result.getFailureCause());

    }

    /**
     * Specify the status code of the response object returned by the mocked client
     * 
     * @param status object storing the status code to mock in the ClientResponse
     */
    private void setResponseStatus(Status status) {
        Mockito.when(mockedClientResponse.getStatus()).thenReturn(status.getStatusCode());
    }

    /**
     * Set the mocked client to return a response of "204 No Content"
     */
    private void setResponseToNoContent() {
        setResponseStatus(Response.Status.NO_CONTENT);
        // The Jersey client throws an exception when getEntity() is called following a 204 response
        UniformInterfaceException uniformInterfaceException = new UniformInterfaceException(mockedClientResponse);
        Mockito.when(mockedClientResponse.getEntity(String.class)).thenThrow(uniformInterfaceException);
    }

    /**
     * @return a mocked Rest Client object using standard SSL settings
     */
    private RestClient buildClient() {
        return new RestClient(mockClientBuilder).authenticationMode(RestAuthenticationMode.SSL_CERT)
                .connectTimeoutMs(1000).readTimeoutMs(500).clientCertFile("cert").clientCertPassword("password");
    }
    
    @Test
    public void testGetClient() throws Exception 
    {
        RestClientBuilder restClientBuilder= new RestClientBuilder();
        RestAuthenticationMode SSL_BASIC= RestAuthenticationMode.SSL_BASIC;
        restClientBuilder.setAuthenticationMode(SSL_BASIC);
        assertTrue(restClientBuilder.getClient() instanceof Client);
    }

}
