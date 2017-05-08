package org.openecomp.restclient.rest;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.openecomp.restclient.rest.RestClientBuilder;

import static org.junit.Assert.*;

import com.sun.jersey.api.client.Client;

import com.sun.jersey.client.urlconnection.HTTPSProperties;


/**
 * This suite of tests is intended to exercise the functionality of the generice REST client
 * builder.
 */
public class RestClientBuilderTest {

  /**
   * This test validates that we can enable and disable certificate chain verification and that the
   * associated parameters are correctly set.
   */
  @Test
  public void certificateChainVerificationTest() throws Exception {

    final String TRUST_STORE_FILENAME = "myTrustStore";


    // Instantiate a RestClientBuilder with default parameters and
    // get a client instance.
    RestClientBuilder builder = new RestClientBuilder();
    Client client = builder.getClient();

    // Validate that, by default, no trust store has been set.
    assertNull("Trust store filename should not be set for default builder",
        System.getProperty("javax.net.ssl.trustStore"));

    // Now, enable certificate chain verification, but don't specify
    // a trust store filename.
    builder.setValidateServerCertChain(true);

    // Now, get a new client instance. We expect the builder to complain
    // because there is no trust store filename.
    try {
      Client client2 = builder.getClient();
      fail("Expected exception due to no trust store filename.");

    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Trust store filename must be set"));
    }

    // Now, set a value for the trust store filename and try again to
    // get a client instance. This time it should succeed and we should
    // see that our trust name filename was set.
    builder.setTruststoreFilename(TRUST_STORE_FILENAME);
    Client client3 = builder.getClient();

    // Validate that the trust store filename was set.
    assertNotNull("Expected trust store filename to be set",
        System.getProperty("javax.net.ssl.trustStore"));

    // Validate that the filename is set to the value we specified.
    assertTrue(
        "Unexpected trust store filename value " + System.getProperty("javax.net.ssl.trustStore"),
        System.getProperty("javax.net.ssl.trustStore").equals(TRUST_STORE_FILENAME));
  }


  /**
   * This test validates that we can set timeout values in our client builder and that those values
   * are reflected in the client produced by the builder.
   */
  @Test
  public void timeoutValuesTest() throws Exception {

    // Instantiate a RestClientBuilder with default parameters.
    RestClientBuilder builder = new RestClientBuilder();

    // Now, get a client instance and retrieve the client properties.
    Client client = builder.getClient();

    Map<String, Object> props = client.getProperties();

    // Validate that the connection and read timeouts are set to the
    // default values.
    assertEquals("Unexpected connect timeout parameter",
        props.get("com.sun.jersey.client.property.connectTimeout"),
        RestClientBuilder.DEFAULT_CONNECT_TIMEOUT_MS);
    assertEquals("Unexpected read timeout parameter",
        props.get("com.sun.jersey.client.property.readTimeout"),
        RestClientBuilder.DEFAULT_READ_TIMEOUT_MS);

    // Now, change the timeouts in the builder to non-default values.
    builder.setConnectTimeoutInMs(RestClientBuilder.DEFAULT_CONNECT_TIMEOUT_MS + 100);
    builder.setReadTimeoutInMs(RestClientBuilder.DEFAULT_READ_TIMEOUT_MS + 100);

    // Retrieve a new client instance and get the client properties.
    Client client2 = builder.getClient();
    props = client2.getProperties();

    // Validate that the connection and read timeouts are set to the
    // new values.
    assertEquals("Unexpected connect timeout parameter",
        props.get("com.sun.jersey.client.property.connectTimeout"),
        RestClientBuilder.DEFAULT_CONNECT_TIMEOUT_MS + 100);
    assertEquals("Unexpected read timeout parameter",
        props.get("com.sun.jersey.client.property.readTimeout"),
        RestClientBuilder.DEFAULT_READ_TIMEOUT_MS + 100);
  }


  /**
   * This test validates that we can enable and disable host name verification in the clients
   * produced by our builder.
   */
  @Test
  public void hostNameVerifierTest() throws Exception {

    // Instantiate a RestClientBuilder with default parameters.
    RestClientBuilder builder = new RestClientBuilder();

    // Now, get a client instance.
    Client client1 = builder.getClient();

    // Retrieve the client's HTTPS properties.
    HTTPSProperties httpProps = getHTTPSProperties(client1);

    // By default, hostname verification should be disabled, which means
    // that our builder will have injected its own {@link HostnameVerifier}
    // which just always returns true.
    assertNotNull(httpProps.getHostnameVerifier());

    // Verify that the host name verifier returns true regardless of what
    // hostname we pass in.
    assertTrue("Default hostname verifier should always return true",
        httpProps.getHostnameVerifier().verify("not_a_valid_hostname", null));


    // Now, enable hostname verification for our client builder, and
    // get a new client.
    builder.setValidateServerHostname(true);
    Client client2 = builder.getClient();

    // Retrieve the client's HTTPS properties.
    httpProps = getHTTPSProperties(client2);

    // Verify that with hostname verification enabled, our builder did not
    // insert its own stubbed verifier.
    assertNull(httpProps.getHostnameVerifier());
  }


  /**
   * This is a convenience method which extracts the HTTPS properties from a supplied client.
   *
   * @parameter aClient - The client to retrieve the HTTPS properties from.
   */
  private HTTPSProperties getHTTPSProperties(Client aClient) {

    Map<String, Object> props = aClient.getProperties();
    return (HTTPSProperties) props.get(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES);
  }
}
