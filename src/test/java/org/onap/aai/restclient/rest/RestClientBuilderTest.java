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
package org.onap.aai.restclient.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Client;
import org.junit.Before;
import org.junit.Test;
import org.onap.aai.restclient.enums.RestAuthenticationMode;

/**
 * This suite of tests is intended to exercise the functionality of the generice REST client builder.
 */
public class RestClientBuilderTest {

    /**
     * Test case initialization
     *
     * @throws Exception the exception
     */
    @Before
    public void init() throws Exception {}

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
    }


    @Test
    public void validateUnknownModeCreateNoAuthClient() throws Exception {

        RestClientBuilder restClientBuilder = new RestClientBuilder();

        restClientBuilder.setAuthenticationMode(RestAuthenticationMode.UNKNOWN_MODE);
        restClientBuilder.setConnectTimeoutInMs(12345);
        restClientBuilder.setReadTimeoutInMs(54321);

        Client client = restClientBuilder.getClient();
        assertNotNull(client);
    }

    @Test
    public void validateBasicAuthSslClient() throws Exception {

        RestClientBuilder restClientBuilder = new RestClientBuilder();

        restClientBuilder.setAuthenticationMode(RestAuthenticationMode.SSL_BASIC);
        restClientBuilder.setConnectTimeoutInMs(12345);
        restClientBuilder.setReadTimeoutInMs(54321);
        restClientBuilder.setBasicAuthUsername("username");
        restClientBuilder.setBasicAuthPassword("password");
        restClientBuilder.setTruststoreFilename("truststore");

        Client client = restClientBuilder.getClient();
        assertNotNull(client.getHostnameVerifier());
        assertEquals("truststore", System.getProperty("javax.net.ssl.trustStore"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateSslCertClient_noHostOrCertChainValidation() throws Exception {

        RestClientBuilder restClientBuilder = new RestClientBuilder();

        restClientBuilder.setAuthenticationMode(RestAuthenticationMode.SSL_CERT);
        restClientBuilder.setConnectTimeoutInMs(12345);
        restClientBuilder.setReadTimeoutInMs(54321);
        restClientBuilder.setValidateServerCertChain(false);
        restClientBuilder.setValidateServerHostname(false);

        restClientBuilder.getClient();
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateSslCertClient_hostOnlyValidation() throws Exception {

        RestClientBuilder restClientBuilder = new RestClientBuilder();

        restClientBuilder.setAuthenticationMode(RestAuthenticationMode.SSL_CERT);
        restClientBuilder.setConnectTimeoutInMs(12345);
        restClientBuilder.setReadTimeoutInMs(54321);
        restClientBuilder.setValidateServerCertChain(false);
        restClientBuilder.setValidateServerHostname(true);

        restClientBuilder.getClient();

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
        // TODO
        assertNotNull(client.getHostnameVerifier());
        assertEquals("truststore", System.getProperty("javax.net.ssl.trustStore"));
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
        // TODO
        assertNull(client.getHostnameVerifier());
        assertEquals("truststore", System.getProperty("javax.net.ssl.trustStore"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateSslCertClient_illegalArgumentExceptionWhenTruststoreIsNull() throws Exception {

        RestClientBuilder restClientBuilder = new RestClientBuilder();

        restClientBuilder.setAuthenticationMode(RestAuthenticationMode.SSL_CERT);
        restClientBuilder.setConnectTimeoutInMs(12345);
        restClientBuilder.setReadTimeoutInMs(54321);
        restClientBuilder.setValidateServerCertChain(true);
        restClientBuilder.setValidateServerHostname(true);
        restClientBuilder.setTruststoreFilename(null);

        /*
         * Creating the client in this scenario will cause an IllegalArgumentException caused by the truststore being
         * null
         */
        restClientBuilder.getClient();

    }

    @Test
    public void validateSslProtocolConfiguration() throws Exception {

        RestClientBuilder restClientBuilder = new RestClientBuilder();
        assertEquals(RestClientBuilder.DEFAULT_SSL_PROTOCOL, restClientBuilder.getSslProtocol());

        restClientBuilder.setSslProtocol("TLSv1.2");
        assertEquals("TLSv1.2", restClientBuilder.getSslProtocol());

    }

}
