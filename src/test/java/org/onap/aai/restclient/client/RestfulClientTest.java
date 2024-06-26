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

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.onap.aai.restclient.enums.RestAuthenticationMode;
import org.onap.aai.restclient.rest.RestClientBuilder;

public class RestfulClientTest {

    private static final String TEST_URL = "http://localhost:9000/aai/v7";

    private final MultivaluedMap<String, String> emptyMap = new MultivaluedHashMap<>();
    private final ClientBuilder clientBuilder = ClientBuilder.newBuilder();

    private RestClientBuilder mockClientBuilder;
    private Client mockedClient;
    private WebTarget mockedWebTarget;
    private Builder mockedBuilder;
    private Response mockedClientResponse;

    /**
     * Test case initialization
     *
     * @throws Exception the exception
     */
    @Before
    public void init() throws Exception {
        mockedClientResponse = Mockito.mock(Response.class);
        setResponseStatus(Response.Status.OK);
        Mockito.when(mockedClientResponse.getHeaders()).thenReturn(new MultivaluedHashMap<>());
        Mockito.when(mockedClientResponse.readEntity(String.class)).thenReturn("hello");

        mockedBuilder = Mockito.mock(Builder.class);
        Mockito.when(mockedBuilder.get()).thenReturn(mockedClientResponse);
        Mockito.when(mockedBuilder.post(Mockito.any())).thenReturn(mockedClientResponse);
        Mockito.when(mockedBuilder.put(Mockito.any())).thenReturn(mockedClientResponse);
        Mockito.when(mockedBuilder.delete()).thenReturn(mockedClientResponse);
        Mockito.when(mockedBuilder.head()).thenReturn(mockedClientResponse);
        Mockito.when(mockedBuilder.accept(Mockito.any(MediaType.class))).thenReturn(mockedBuilder);

        mockedWebTarget = Mockito.mock(WebTarget.class);
        Mockito.when(mockedWebTarget.request()).thenReturn(mockedBuilder);

        mockedClient = Mockito.mock(Client.class);
        Mockito.when(mockedClient.target(Mockito.anyString())).thenReturn(mockedWebTarget);

        mockClientBuilder = Mockito.mock(RestClientBuilder.class);
        Mockito.when(mockClientBuilder.getClient()).thenReturn(mockedClient);
    }

    @Test
    public void validateConstructors() {
        RestClient restClient = new RestClient(clientBuilder);
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

        MultivaluedMap<String, String> headerMap = new MultivaluedHashMap<>();

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
        Mockito.when(mockedClientResponse.readEntity(String.class)).thenReturn("RNF");

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
        Mockito.when(mockedBuilder.get()).thenThrow(new IllegalArgumentException("error"));

        boolean targetServiceHealthy =
                buildClient().healthCheck("http://localhost:9000/aai/util/echo", "startSerice", "targetService");

        assertEquals(false, targetServiceHealthy);
    }

    @Test
    public void validateSuccessfulGetWithRetries() throws Exception {
        Mockito.when(mockedClientResponse.getStatus()).thenReturn(408).thenReturn(Response.Status.OK.getStatusCode());
        Mockito.when(mockedClientResponse.readEntity(String.class)).thenReturn("error").thenReturn("ok");

        OperationResult result = buildClient().get(TEST_URL, emptyMap, MediaType.APPLICATION_JSON_TYPE, 3);

        assertEquals(Response.Status.OK.getStatusCode(), result.getResultCode());
        assertNotNull(result.getResult());
        assertNull(result.getFailureCause());

    }

    @Test
    public void validateFailedGetWithRetriesCausedByResourceNotFound() throws Exception {
        setResponseStatus(Response.Status.NOT_FOUND);
        Mockito.when(mockedClientResponse.readEntity(String.class)).thenReturn("error").thenReturn("ok");

        OperationResult result = buildClient().get(TEST_URL, emptyMap, MediaType.APPLICATION_JSON_TYPE, 3);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), result.getResultCode());
        assertNull(result.getResult());
        assertNotNull(result.getFailureCause());

    }

    @Test
    public void validateFailedGetAfterMaxRetries() throws Exception {
        setResponseStatus(Response.Status.INTERNAL_SERVER_ERROR);
        Mockito.when(mockedClientResponse.readEntity(String.class)).thenReturn("error");

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

    @Test
    public void testGetClient() throws Exception {
        RestClientBuilder restClientBuilder = new RestClientBuilder(clientBuilder);
        restClientBuilder.setAuthenticationMode(RestAuthenticationMode.SSL_BASIC);
        restClientBuilder.setTruststoreFilename("truststore");
        assertTrue(restClientBuilder.getClient() instanceof Client);
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
        // The Jersey client throws an exception when readEntity() is called following a 204 response
        ProcessingException processingException = new ProcessingException("No content");
        Mockito.when(mockedClientResponse.readEntity(String.class)).thenThrow(processingException);
    }

    /**
     * @return a mocked Rest Client object using standard SSL settings
     */
    private RestClient buildClient() {
        return new RestClient(mockClientBuilder).authenticationMode(RestAuthenticationMode.SSL_CERT)
                .connectTimeoutMs(1000).readTimeoutMs(500).clientCertFile("cert").clientCertPassword("password");
    }
}
