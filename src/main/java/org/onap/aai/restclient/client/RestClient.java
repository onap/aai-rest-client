/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017-2018 Amdocs
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
 */
package org.onap.aai.restclient.client;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.onap.aai.cl.api.LogLine;
import org.onap.aai.cl.mdc.MdcContext;
import org.onap.aai.cl.mdc.MdcOverride;
import org.onap.aai.restclient.enums.RestAuthenticationMode;
import org.onap.aai.restclient.logging.RestClientMsgs;
import org.onap.aai.restclient.rest.RestClientBuilder;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;



/**
 * This class provides a general client implementation that micro services can use for communicating with the endpoints
 * via their exposed REST interfaces.
 * 
 */

public class RestClient {

    /**
     * This is a generic builder that is used for constructing the REST client that we will use to communicate with the
     * REST endpoint.
     */
    private RestClientBuilder clientBuilder;

    private final ConcurrentMap<String, InitializedClient> CLIENT_CACHE = new ConcurrentHashMap<>();
    private static final String REST_CLIENT_INSTANCE = "REST_CLIENT_INSTANCE";

    /** Standard logger for producing log statements. */
    private static EELFLogger logger = EELFManager.getLogger(RestClient.class.getName());

    /** Standard logger for producing metric statements. */
    private static EELFLogger metricsLogger = EELFManager.getMetricsLogger();
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    /** Reusable function call for GET REST operations. */
    private final RestOperation getOp = new GetRestOperation();

    /** Reusable function call for PUT REST operations. */
    private final RestOperation putOp = new PutRestOperation();

    /** Reusable function call for POST REST operations. */
    private final RestOperation postOp = new PostRestOperation();

    /** Reusable function call for DELETE REST operations. */
    private final RestOperation deleteOp = new DeleteRestOperation();

    /** Reusable function call for HEAD REST operations. */
    private final RestOperation headOp = new HeadRestOperation();

    /** Reusable function call for PATCH REST operations. */
    private final RestOperation patchOp = new PatchRestOperation();

    /**
     * Creates a new instance of the {@link RestClient}.
     */
    public RestClient(ClientBuilder builder) {
        clientBuilder = new RestClientBuilder(builder);
    }

    /**
     * Creates a new instance of the {@link RestClient} using the supplied {@link RestClientBuilder}.
     *
     * @param rcBuilder - The REST client builder that this instance of the {@link RestClient} should use.
     */
    public RestClient(RestClientBuilder rcBuilder) {
        clientBuilder = rcBuilder;
    }

    public RestClient authenticationMode(RestAuthenticationMode mode) {
        logger.debug("Set rest authentication mode= " + mode);
        clientBuilder.setAuthenticationMode(mode);
        return this;
    }

    public RestClient basicAuthUsername(String username) {
        logger.debug("Set SSL BasicAuth username = " + username);
        clientBuilder.setBasicAuthUsername(username);
        return this;
    }

    public RestClient basicAuthPassword(String password) {
        /*
         * purposely not logging out the password, I guess we could obfuscate it if we really want to see it in the logs
         */
        clientBuilder.setBasicAuthPassword(password);
        return this;
    }

    /**
     * Sets the flag to indicate whether or not validation should be performed against the host name of the server we
     * are trying to communicate with.
     *
     * @parameter validate - Set to true to enable validation, false to disable
     *
     * @return The AAIRESTClient instance. This is useful for chaining parameter assignments.
     */
    public RestClient validateServerHostname(boolean validate) {
        logger.debug("Set validate server hostname = " + validate);
        clientBuilder.setValidateServerHostname(validate);
        return this;
    }

    /**
     * Sets the flag to indicate whether or not validation should be performed against the certificate chain.
     *
     * @parameter validate - Set to true to enable validation, false to disable.
     *
     * @return The AAIRESTClient instance. This is useful for chaining parameter assignments.
     */
    public RestClient validateServerCertChain(boolean validate) {
        logger.debug("Set validate server certificate chain = " + validate);
        clientBuilder.setValidateServerCertChain(validate);
        return this;
    }

    /**
     * Assigns the client certificate file to use.
     *
     * @param filename - The name of the certificate file.
     *
     * @return The AAIRESTClient instance. This is useful for chaining parameter assignments.
     */
    public RestClient clientCertFile(String filename) {
        logger.debug("Set client certificate filename = " + filename);
        clientBuilder.setClientCertFileName(filename);
        return this;
    }

    /**
     * Assigns the client certificate password to use.
     *
     * @param password - The certificate password.
     *
     * @return The AAIRESTClient instance. This is useful for chaining parameter assignments.
     */
    public RestClient clientCertPassword(String password) {
        clientBuilder.setClientCertPassword(password);
        return this;
    }

    /**
     * Assigns the name of the trust store file to use.
     *
     * @param filename - the name of the trust store file.
     *
     * @return The AAIRESTClient instance. This is useful for chaining parameter assignments.
     */
    public RestClient trustStore(String filename) {
        logger.debug("Set trust store filename = " + filename);
        clientBuilder.setTruststoreFilename(filename);
        return this;
    }

    /**
     * Assigns the connection timeout (in ms) to use when connecting to the target server.
     *
     * @param timeout - The length of time to wait in ms before timing out.
     *
     * @return The AAIRESTClient instance. This is useful for chaining parameter assignments.
     */
    public RestClient connectTimeoutMs(int timeout) {
        logger.debug("Set connection timeout = " + timeout + " ms");
        clientBuilder.setConnectTimeoutInMs(timeout);
        return this;
    }

    /**
     * Assigns the read timeout (in ms) to use when communicating with the target server.
     *
     * @param timeout The read timeout in milliseconds.
     *
     * @return The AAIRESTClient instance. This is useful for chaining parameter assignments.
     */
    public RestClient readTimeoutMs(int timeout) {
        logger.debug("Set read timeout = " + timeout + " ms");
        clientBuilder.setReadTimeoutInMs(timeout);
        return this;
    }

    /**
     * Configures the client for a specific SSL protocol
     *
     * @param sslProtocol - protocol string constant such as TLS, TLSv1, TLSv1.1, TLSv1.2
     *
     * @return The AAIRESTClient instance.
     */
    public RestClient sslProtocol(String sslProtocol) {
        logger.debug("Set sslProtocol = " + sslProtocol);
        clientBuilder.setSslProtocol(sslProtocol);
        return this;
    }

    private boolean shouldRetry(OperationResult operationResult) {

        if (operationResult == null) {
            return true;
        }

        int resultCode = operationResult.getResultCode();

        if (resultCode == 200) {
            return false;
        }

        if (resultCode == 404) {
            return false;
        }

        return true;

    }

    /**
     * This method operates on a REST endpoint by submitting an HTTP operation request against the supplied URL. This
     * variant of the method will perform a requested number of retries in the event that the first request is
     * unsuccessful.
     *
     * @param operation - the REST operation type to send to the url
     * @param url - The REST endpoint to submit the REST request to.
     * @param payload - They payload to provide in the REST request, if applicable
     * @param headers - The headers that should be passed in the request
     * @param contentType - The content type of the payload
     * @param responseType - The expected format of the response.
     * 
     * @return The result of the REST request.
     */
    protected OperationResult processRequest(RestOperation operation, String url, String payload,
            Map<String, List<String>> headers, MediaType contentType, MediaType responseType, int numRetries) {


        OperationResult result = null;

        long startTimeInMs = System.currentTimeMillis();
        for (int retryCount = 0; retryCount < numRetries; retryCount++) {

            logger.info(RestClientMsgs.HTTP_REQUEST_WITH_RETRIES, operation.getRequestType().toString(), url,
                    Integer.toString(retryCount + 1));

            // Submit our query to the AAI.
            result = processRequest(operation, url, payload, headers, contentType, responseType);

            // If the submission was successful then we're done.

            if (!shouldRetry(result)) {

                logger.info(RestClientMsgs.HTTP_REQUEST_TIME_WITH_RETRIES, operation.getRequestType().toString(), url,
                        Long.toString(System.currentTimeMillis() - startTimeInMs), Integer.toString(retryCount));

                result.setNumRetries(retryCount);

                return result;
            }

            // Our submission was unsuccessful...
            try {
                // Sleep between re-tries to be nice to the target system.
                Thread.sleep(50);

            } catch (InterruptedException e) {
                logger.error(RestClientMsgs.HTTP_REQUEST_INTERRUPTED, url, e.getLocalizedMessage());
                Thread.currentThread().interrupt();
                break;
            }
        }

        // If we've gotten this far, then we failed all of our retries.
        if (result == null) {
            result = new OperationResult();
        }

        result.setNumRetries(numRetries);
        result.setResultCode(504);
        result.setFailureCause("Failed to get a successful result after multiple retries to target server.");


        return result;
    }

    /**
     * This method operates on a REST endpoint by submitting an HTTP operation request against the supplied URL.
     *
     * @param operation - the REST operation type to send to the url
     * @param url - The REST endpoint to submit the REST request to.
     * @param payload - They payload to provide in the REST request, if applicable
     * @param headers - The headers that should be passed in the request
     * @param contentType - The content type of the payload
     * @param responseType - The expected format of the response.
     *
     * @return The result of the REST request.
     */
    protected OperationResult processRequest(RestOperation operation, String url, String payload,
            Map<String, List<String>> headers, MediaType contentType, MediaType responseType) {

        Response clientResponse = null;
        OperationResult operationResult = new OperationResult();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        String requestType = operation.getRequestType().name();

        // Grab the current time so that we can log how long the
        // query took once we are done.
        long startTimeInMs = System.currentTimeMillis();
        MdcOverride override = new MdcOverride();
        override.addAttribute(MdcContext.MDC_START_TIME, formatter.format(startTimeInMs));

        logger.info(RestClientMsgs.HTTP_REQUEST, requestType, url);

        try {

            // Get a REST client instance for our request.
            Client client = getClient();

            // Debug log the request
            debugRequest(url, payload, headers, responseType);

            // Get a client request builder, and submit our GET request.
            Builder builder = getClientBuilder(client, url, headers, responseType);
            clientResponse = operation.processOperation(builder, payload, contentType);

            populateOperationResult(clientResponse, operationResult);

            // Debug log the response
            if (clientResponse != null) {
                debugResponse(operationResult, clientResponse.getHeaders());
            }

        } catch (Exception ex) {

            logger.error(RestClientMsgs.HTTP_REQUEST_ERROR, requestType, url, ex.getLocalizedMessage());
            operationResult.setResultCode(500);
            operationResult
                    .setFailureCause("Error during GET operation to AAI with message = " + ex.getLocalizedMessage());

        } finally {

            if (logger.isDebugEnabled()) {
                logger.debug(baos.toString());
            }

            // Not every valid response code is actually represented by the Response.Status
            // object, so we need to guard against missing codes, otherwise we throw null
            // pointer exceptions when we try to generate our metrics logs...
            Response.Status responseStatus = Response.Status.fromStatusCode(operationResult.getResultCode());
            String responseStatusCodeString = "";
            if (responseStatus != null) {
                responseStatusCodeString = responseStatus.toString();
            }
            Map<String, String> logFields = new HashMap<String, String>();
            logFields.put(LogLine.DefinedFields.STATUS_CODE.name(), responseStatusCodeString);
            logFields.put(LogLine.DefinedFields.RESPONSE_CODE.name(), String.valueOf(operationResult.getResultCode()));
            logFields.put(LogLine.DefinedFields.RESPONSE_DESCRIPTION.name(), operationResult.getResult());
            metricsLogger.info(RestClientMsgs.HTTP_REQUEST_TIME, logFields.toString(), requestType, Long.toString(System.currentTimeMillis() - startTimeInMs), url);
            logger.info(RestClientMsgs.HTTP_REQUEST_TIME, requestType,
                    Long.toString(System.currentTimeMillis() - startTimeInMs), url);
            logger.info(RestClientMsgs.HTTP_RESPONSE, url,
                    operationResult.getResultCode() + " " + responseStatusCodeString);
        }

        return operationResult;
    }

    /**
     * This method submits an HTTP PUT request against the supplied URL.
     *
     * @param url - The REST endpoint to submit the PUT request to.
     * @param payload - the payload to send to the supplied URL
     * @param headers - The headers that should be passed in the request
     * @param contentType - The content type of the payload
     * @param responseType - The expected format of the response.
     *
     * @return The result of the PUT request.
     */
    public OperationResult put(String url, String payload, Map<String, List<String>> headers, MediaType contentType,
            MediaType responseType) {
        return processRequest(putOp, url, payload, headers, contentType, responseType);
    }

    /**
     * This method submits an HTTP POST request against the supplied URL.
     *
     * @param url - The REST endpoint to submit the POST request to.
     * @param payload - the payload to send to the supplied URL
     * @param headers - The headers that should be passed in the request
     * @param contentType - The content type of the payload
     * @param responseType - The expected format of the response.
     *
     * @return The result of the POST request.
     */
    public OperationResult post(String url, String payload, Map<String, List<String>> headers, MediaType contentType,
            MediaType responseType) {
        return processRequest(postOp, url, payload, headers, contentType, responseType);
    }

    /**
     * This method submits an HTTP POST request against the supplied URL, and emulates a PATCH operation by setting a
     * special header value
     *
     * @param url - The REST endpoint to submit the POST request to.
     * @param payload - the payload to send to the supplied URL
     * @param headers - The headers that should be passed in the request
     * @param contentType - The content type of the payload
     * @param responseType - The expected format of the response.
     *
     * @return The result of the PATCH request.
     */
    public OperationResult patch(String url, String payload, Map<String, List<String>> headers, MediaType contentType,
            MediaType responseType) {
        return processRequest(patchOp, url, payload, headers, contentType, responseType);
    }

    /**
     * This method submits an HTTP HEAD request against the supplied URL
     *
     * @param url - The REST endpoint to submit the POST request to.
     * @param headers - The headers that should be passed in the request
     * @param responseType - The expected format of the response.
     *
     * @return The result of the HEAD request.
     */
    public OperationResult head(String url, Map<String, List<String>> headers, MediaType responseType) {
        return processRequest(headOp, url, null, headers, null, responseType);
    }

    /**
     * This method submits an HTTP GET request against the supplied URL.
     *
     * @param url - The REST endpoint to submit the GET request to.
     * @param headers - The headers that should be passed in the request
     * @param responseType - The expected format of the response.
     *
     * @return The result of the GET request.
     */
    public OperationResult get(String url, Map<String, List<String>> headers, MediaType responseType) {
        return processRequest(getOp, url, null, headers, null, responseType);
    }

    /**
     * This method submits an HTTP GET request against the supplied URL. This variant of the method will perform a
     * requested number of retries in the event that the first request is unsuccessful.
     * 
     * @param url - The REST endpoint to submit the GET request to.
     * @param headers - The headers that should be passed in the request
     * @param responseType - The expected format of the response.
     * @param numRetries - The number of times to try resubmitting the request in the event of a failure.
     * 
     * @return The result of the GET request.
     */
    public OperationResult get(String url, Map<String, List<String>> headers, MediaType responseType, int numRetries) {
        return processRequest(getOp, url, null, headers, null, responseType, numRetries);
    }

    /**
     * This method submits an HTTP DELETE request against the supplied URL.
     *
     * @param url - The REST endpoint to submit the DELETE request to.
     * @param headers - The headers that should be passed in the request
     * @param responseType - The expected format of the response.
     *
     * @return The result of the DELETE request.
     */
    public OperationResult delete(String url, Map<String, List<String>> headers, MediaType responseType) {
        return processRequest(deleteOp, url, null, headers, null, responseType);
    }

    /**
     * This method does a health check ("ping") against the supplied URL.
     *
     * @param url - The REST endpoint to attempt a health check.
     * @param srcAppName - The name of the application using this client.
     * @param destAppName - The name of the destination app.
     *
     * @return A boolean value. True if connection attempt was successful, false otherwise.
     *
     */
    public boolean healthCheck(String url, String srcAppName, String destAppName) {
        return healthCheck(url, srcAppName, destAppName, MediaType.TEXT_PLAIN_TYPE);

    }

    /**
     * This method does a health check ("ping") against the supplied URL.
     *
     * @param url - The REST endpoint to attempt a health check.
     * @param srcAppName - The name of the application using this client.
     * @param destAppName - The name of the destination app.
     * @param responseType - The response type.
     *
     * @return A boolean value. True if connection attempt was successful, false otherwise.
     *
     */
    public boolean healthCheck(String url, String srcAppName, String destAppName, MediaType responseType) {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put(Headers.FROM_APP_ID, Arrays.asList(new String[] {srcAppName}));
        headers.put(Headers.TRANSACTION_ID, Arrays.asList(new String[] {UUID.randomUUID().toString()}));

        try {
            logger.info(RestClientMsgs.HEALTH_CHECK_ATTEMPT, destAppName, url);
            OperationResult result = get(url, headers, responseType);

            if (result != null && result.getFailureCause() == null) {
                logger.info(RestClientMsgs.HEALTH_CHECK_SUCCESS, destAppName, url);
                return true;
            } else {
                logger.error(RestClientMsgs.HEALTH_CHECK_FAILURE, destAppName, url,
                        result != null ? result.getFailureCause() : null);
                return false;
            }
        } catch (Exception e) {
            logger.error(RestClientMsgs.HEALTH_CHECK_FAILURE, destAppName, url, e.getMessage());
            return false;
        }
    }

    /**
     * This method constructs a client request builder that can be used for submitting REST requests to the supplied URL
     * endpoint.
     *
     * @param client - The REST client we will be using to talk to the server.
     * @param url - The URL endpoint that our request will be submitted to.
     * @param headers - The headers that should be passed in the request
     * @param responseType - The expected format of the response.
     *
     * @return A client request builder.
     */
    private Builder getClientBuilder(Client client, String url, Map<String, List<String>> headers,
            MediaType responseType) {

        WebTarget target = client.target(url);

        Builder builder = target.request().accept(responseType);

        if (headers != null) {
            for (Entry<String, List<String>> header : headers.entrySet()) {
                builder.header(header.getKey(), String.join(";", header.getValue()));
            }

            // Added additional check to prevent adding duplicate authorization header if client is already sending the
            // authorization header
            // AAI-1097 - For AAI calls when Rest authentication mode is selected as SSL_BASIC getting 403 error
            if (clientBuilder.getAuthenticationMode() == RestAuthenticationMode.SSL_BASIC
                    && headers.get(Headers.AUTHORIZATION) == null) {
                builder = builder.header(Headers.AUTHORIZATION, clientBuilder.getBasicAuthenticationCredentials());
            }

        }

        return builder;
    }

    private void debugRequest(String url, String payload, Map<String, List<String>> headers, MediaType responseType) {
        if (!logger.isDebugEnabled()) {
            return;
        }

        StringBuilder debugRequest = new StringBuilder("REQUEST:\n");
        debugRequest.append("URL: ").append(url).append("\n");
        debugRequest.append("Payload: ").append(payload).append("\n");
        debugRequest.append("Response Type: ").append(responseType).append("\n");

        if (headers == null) {
            logger.debug(debugRequest.toString());
            return;
        }

        debugRequest.append("Headers: ");
        for (Entry<String, List<String>> header : headers.entrySet()) {
            debugRequest.append("\n\t").append(header.getKey()).append(":");
            for (String headerEntry : header.getValue()) {
                debugRequest.append("\"").append(headerEntry).append("\" ");
            }
        }

        logger.debug(debugRequest.toString());

    }

    private void debugResponse(OperationResult operationResult, MultivaluedMap<String, Object> headers) {

        if (!logger.isDebugEnabled()) {
            return;
        }

        StringBuilder debugResponse = new StringBuilder("RESPONSE:\n");
        debugResponse.append("Result: ").append(operationResult.getResultCode()).append("\n");
        debugResponse.append("Failure Cause: ").append(operationResult.getFailureCause()).append("\n");
        debugResponse.append("Payload: ").append(operationResult.getResult()).append("\n");

        if (headers == null) {
            logger.debug(debugResponse.toString());
            return;
        }

        debugResponse.append("Headers: ");
        for (Entry<String, List<Object>> header : headers.entrySet()) {
            debugResponse.append("\n\t").append(header.getKey()).append(":");
            for (Object headerEntry : header.getValue()) {
                debugResponse.append("\"").append(headerEntry).append("\" ");
            }
        }

        logger.debug(debugResponse.toString());
    }

    /**
     * This method creates an instance of the low level REST client to use for communicating with the AAI, if one has
     * not already been created, otherwise it returns the already created instance.
     *
     * @return A {@link Client} instance.
     */
    protected Client getClient() throws Exception {

        /*
         * Attempting a new way of doing non-blocking thread-safe lazy-initialization by using Java 1.8 computeIfAbsent
         * functionality. A null value will not be stored, but once a valid mapping has been established, then the same
         * value will be returned.
         * 
         * One awkwardness of the computeIfAbsent is the lack of support for thrown exceptions, which required a bit of
         * hoop jumping to preserve the original exception for the purpose of maintaining the pre-existing this API
         * signature.
         */

        final InitializedClient clientInstance =
                CLIENT_CACHE.computeIfAbsent(REST_CLIENT_INSTANCE, k -> loggedClientInitialization());

        if (clientInstance.getCaughtException() != null) {
            throw new InstantiationException(clientInstance.getCaughtException().getMessage());
        }

        return clientInstance.getClient();

    }

    /**
     * This method will only be called if computerIfAbsent is true. The return value is null, then the result is not
     * stored in the map.
     * 
     * @return a new client instance or null
     */
    private InitializedClient loggedClientInitialization() {

        if (logger.isDebugEnabled()) {
            logger.debug("Instantiating REST client with following parameters:");
            logger.debug(clientBuilder.toString());
        }

        InitializedClient initClient = new InitializedClient();

        try {
            initClient.setClient(clientBuilder.getClient());
        } catch (Exception error) {
            initClient.setCaughtException(error);
        }

        return initClient;

    }


    /**
     * This method populates the fields of an {@link OperationResult} instance based on the contents of a
     * {@link Response} received in response to a REST request.
     */
    private void populateOperationResult(Response response, OperationResult opResult) {

        // If we got back a NULL response, then just produce a generic
        // error code and result indicating this.
        if (response == null) {
            opResult.setResultCode(500);
            opResult.setFailureCause("Client response was null");
            return;
        }

        int statusCode = response.getStatus();
        opResult.setResultCode(statusCode);

        if (opResult.wasSuccessful()) {
            if (statusCode != Response.Status.NO_CONTENT.getStatusCode()) {
                opResult.setResult(response.readEntity(String.class));
            }
        } else {
            opResult.setFailureCause(response.readEntity(String.class));
        }

        opResult.setHeaders(convertHeaderObjectsToString(response.getHeaders()));
    }

    private MultivaluedMap<String, String> convertHeaderObjectsToString(MultivaluedMap<String, Object> headers) {
        MultivaluedMap<String, String> result = new MultivaluedHashMap<>();
        headers.forEach((k, v) -> result.addAll(k, v.stream().map(Object::toString).collect(Collectors.toList())));
        return result;
    }

    private class GetRestOperation implements RestOperation {
        @Override
        public Response processOperation(Builder builder, String payload, MediaType contentType) {
            return builder.get();
        }

        @Override
        public RequestType getRequestType() {
            return RequestType.GET;
        }
    }

    private class PutRestOperation implements RestOperation {
        @Override
        public Response processOperation(Builder builder, String payload, MediaType contentType) {
            return builder.put(Entity.entity(payload, contentType));
        }

        @Override
        public RequestType getRequestType() {
            return RequestType.PUT;
        }
    }

    private class PostRestOperation implements RestOperation {
        @Override
        public Response processOperation(Builder builder, String payload, MediaType contentType) {
            return builder.post(Entity.entity(payload, contentType));
        }

        @Override
        public RequestType getRequestType() {
            return RequestType.POST;
        }
    }

    private class DeleteRestOperation implements RestOperation {
        @Override
        public Response processOperation(Builder builder, String payload, MediaType contentType) {
            return builder.delete();
        }

        @Override
        public RequestType getRequestType() {
            return RequestType.DELETE;
        }
    }

    private class HeadRestOperation implements RestOperation {
        @Override
        public Response processOperation(Builder builder, String payload, MediaType contentType) {
            return builder.head();
        }

        @Override
        public RequestType getRequestType() {
            return RequestType.HEAD;
        }
    }

    private class PatchRestOperation implements RestOperation {

        /**
         * Technically there is no standarized PATCH operation for the jersey client, but we can use the method-override
         * approach instead.
         */
        @Override
        public Response processOperation(Builder builder, String payload, MediaType contentType) {
            builder = builder.header("X-HTTP-Method-Override", "PATCH");
            return builder.post(Entity.entity(payload, contentType));
        }

        @Override
        public RequestType getRequestType() {
            return RequestType.PATCH;
        }
    }


    /**
     * Interface used wrap a Jersey REST call using a functional interface.
     */
    private interface RestOperation {

        /**
         * Method used to wrap the functionality of making a REST call out to the endpoint.
         * 
         * @param builder the Jersey builder used to make the request
         * @param payload the request payload
         * @param contentType the content type of the payload
         * @return the response from the REST endpoint
         */
        public Response processOperation(Builder builder, String payload, MediaType contentType);

        /**
         * Returns the REST request type.
         */
        public RequestType getRequestType();

        /**
         * The supported REST request types.
         */
        public enum RequestType {
            GET, PUT, POST, DELETE, PATCH, HEAD
        }
    }

    /*
     * An entity to encapsulate an expected result and a potential failure cause when returning from a functional
     * interface during the computeIfAbsent call.
     */
    private class InitializedClient {
        private Client client;
        private Throwable caughtException;

        public InitializedClient() {
            client = null;
            caughtException = null;
        }

        public Client getClient() {
            return client;
        }

        public void setClient(Client client) {
            this.client = client;
        }

        public Throwable getCaughtException() {
            return caughtException;
        }

        public void setCaughtException(Throwable caughtException) {
            this.caughtException = caughtException;
        }

    }

}
