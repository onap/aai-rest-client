package org.openecomp.restclient.client;

import org.junit.Before;
import org.junit.Test;
import org.openecomp.restclient.client.OperationResult;
import org.openecomp.restclient.client.RestClient;
import org.openecomp.restclient.rest.RestClientBuilder;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;

import com.sun.jersey.api.client.Client;


/**
 * This suite of tests is intended to exercise the behaviour of the {@link RestClient}.
 */
public class RESTClientTest extends JerseyTest {

  private static final String GOOD_AAI_ENDPOINT = "testaai/good";
  private static final String FAIL_ALWAYS_AAI_ENDPOINT = "testaai/failalways";
  private static final String FAIL_THEN_SUCCEED_ENDPOINT = "testaai/failthensucceed";
  private static final String INVALID_AAI_ENDPOINT = "testaai/bad";

  private static final String AAI_GET_REPLY_PAYLOAD = "Reply from AAI";

  private static final int SUCCESS_RESULT_CODE = 200;
  private static final int INVALID_END_POINT_RESULT_CODE = 404;
  private static final int INTERNAL_ERR_RESULT_CODE = 500;
  private static final int TIMEOUT_RESULT_CODE = 504;


  /**
   * Creates a new instance of the {@link RESTClientTest} test suite.
   */
  public RESTClientTest() throws Exception {

    // Tell our in memory container to look here for resource endpoints.
    super("org.openecomp.restclient.client");
  }


  @Override
  protected AppDescriptor configure() {
    return new WebAppDescriptor.Builder().build();
  }


  /**
   * Perform common initialization actions that need to run before every unit test.
   */
  @Before
  public void setup() {

    // Initialize our test endpoints to make sure that all of their
    // counters have valid starting values
    AAI_FailAlways_Stub.initialize();
    AAI_FailThenSucceed_Stub.initialize();
  }


  /**
   * This test validates that all of the {@link RestClient}'s configurable parameters can be set via
   * its fluent interface and that those values are successfully passed down to the underlying
   * {@link RestClientBuilder} instance.
   */
  @Test
  public void configureAAIClientTest() {

    final boolean VALIDATE_SERVER = true;
    final boolean VALIDATE_CERT_CHAIN = true;
    final String CLIENT_CERT_FILE = "myCertFile";
    final String CLIENT_CERT_PASSWORD = "My voice is my password";
    final String TRUST_STORE = "myTrustStore";
    final int CONNECT_TIMEOUT = 5000;
    final int READ_TIMEOUT = 5000;

    // Create an instance of our test version of the REST client builder.
    TestRestClientBuilder clientBuilder = new TestRestClientBuilder();

    // Now, create a new instance of the {@link AAIClient} and configure
    // its parameters.
    RestClient testClient =
        new RestClient(clientBuilder).validateServerHostname(true).validateServerCertChain(true)
            .clientCertFile("myCertFile").clientCertPassword("My voice is my password")
            .trustStore("myTrustStore").connectTimeoutMs(5000).readTimeoutMs(5000);

    // Validate that the parameters of the test REST client builder that
    // we passed to the AAI client have been set according to what we
    // passed in when we instantiated the AAI client.
    assertEquals("Unexpected 'validate server host name' value", VALIDATE_SERVER,
        clientBuilder.isValidateServerHostname());
    assertEquals("Unexpected 'validate certificat chain' value", VALIDATE_CERT_CHAIN,
        clientBuilder.isValidateServerCertChain());
    assertTrue("Unexpected client certificate filename",
        CLIENT_CERT_FILE.equals(clientBuilder.getClientCertFileName()));
    assertTrue("Unexpected client certificate password",
        CLIENT_CERT_PASSWORD.equals(clientBuilder.getClientCertPassword()));
    assertTrue("Unexpected trust store filename",
        TRUST_STORE.equals(clientBuilder.getTruststoreFilename()));
    assertEquals("Unexpected connection timeout value", CONNECT_TIMEOUT,
        clientBuilder.getConnectTimeoutInMs());
    assertEquals("Unexpected read timeout value", READ_TIMEOUT, clientBuilder.getReadTimeoutInMs());
  }


  /**
   * This test validates that the {@link RestClient} can submit a GET request to a valid REST
   * endpoint and receive a valid response.
   */
  @Test
  public void queryAAI_SuccessTest() {

    // Create an instance of the AAIClient that uses our test version of
    // the REST client builder.
    RestClient testClient = new RestClient(new TestRestClientBuilder());

    // Query our stubbed out AAI with a URL that we expecte to get a successful
    // reply from.
    OperationResult or =
        testClient.get(getBaseURI() + GOOD_AAI_ENDPOINT, null, MediaType.APPLICATION_JSON_TYPE);

    // Validate that a successful query returns a result code of 200.
    assertEquals("Unexpected result code", SUCCESS_RESULT_CODE, or.getResultCode());

    // Validate that no error cause gets set on a successful query.
    assertNull("Operation result failure code should not be set for successful GET",
        or.getFailureCause());

    // Validate that our query returned the expected payload from our dummy
    // AAI.
    assertTrue("Incorrect payload returned from AAI query",
        AAI_GET_REPLY_PAYLOAD.equals(or.getResult()));
  }


  /**
   * This test validates that the {@link RestClient} behaves as expected when query requests are
   * unsuccessful.
   * <p>
   * Specifically, the following scenarios are covered:<br>
   * 1) Submitting a GET request to an invalid REST endpoint 2) Submitting a GET request to a valid
   * endpoint which throws an error rather than replying successfully.
   * <p>
   * Note that this test exercises the 'single attempt' variant of the query method.
   */
  @Test
  public void queryAAI_FailureTest() {

    // Create an instance of the AAIClient that uses our test version of
    // the REST client builder.
    RestClient testClient = new RestClient(new TestRestClientBuilder());

    // Query our stubbed out AAI with a URL that we expecte to get a successful
    // reply from.
    OperationResult or =
        testClient.get(getBaseURI() + INVALID_AAI_ENDPOINT, null, MediaType.APPLICATION_JSON_TYPE);

    // Validate that an attempt to query a non-existing endpoint results in
    // a 404 error.
    assertEquals("Unexpected result code", INVALID_END_POINT_RESULT_CODE, or.getResultCode());

    // Validate that no payload was set since the query failed.
    assertNull("Payload should not be set on 404 error", or.getResult());

    // Now, submit a query request to the stubbed AAI.
    or = testClient.get(getBaseURI() + FAIL_ALWAYS_AAI_ENDPOINT, null,
        MediaType.APPLICATION_JSON_TYPE);

    // Validate that a query to a avalid returns a result code of 500.
    assertEquals("Unexpected result code", INTERNAL_ERR_RESULT_CODE, or.getResultCode());
  }


  /**
   * This test validates the behaviour of querying the AAI with a number of retries requested in the
   * case where we never get a successful reply.
   */
  @Test
  public void queryAAIWithRetries_TimeoutTest() {

    int NUM_RETRIES = 3;


    // Create an instance of the AAIClient that uses our test version of
    // the REST client builder.
    RestClient testClient = new RestClient(new TestRestClientBuilder());

    // Initialize our test endpoint to make sure that all of its
    // counters have valid starting values
    // AAI_FailAlways_Stub.initialize();

    // Perform a query against the stubbed AAI, specifying a number of times
    // to retry in the event of an error.
    OperationResult or = testClient.get(getBaseURI() + FAIL_ALWAYS_AAI_ENDPOINT, null,
        MediaType.APPLICATION_JSON_TYPE, NUM_RETRIES);

    // Validate that failing for all of our retry attempts results in a
    // 504 error.
    assertEquals("Unexpected result code", TIMEOUT_RESULT_CODE, or.getResultCode());

    // Validate that our stubbed AAI actually received the expected number
    // of retried requests.
    assertEquals("Unexpected number of retries", NUM_RETRIES, AAI_FailAlways_Stub.getCount);
  }


  /**
   * This test validates the behaviour of querying the AAI with a number of retries requested in the
   * case where our query initially fails but then succeeds on one of the subsequent retries.
   */
  @Test
  public void queryAAIWithRetries_FailThenSucceedTest() {

    int num_retries = AAI_FailThenSucceed_Stub.MAX_FAILURES + 2;

    // Create an instance of the AAIClient that uses our test version of
    // the REST client builder.
    RestClient testClient = new RestClient(new TestRestClientBuilder());

    // Initialize our test endpoint to make sure that all of its
    // counters have valid starting values.
    // AAI_FailThenSucceed_Stub.initialize();

    // Perform a query against the stubbed AAI, specifying a number of times
    // to retry in the event of an error.
    OperationResult or = testClient.get(getBaseURI() + FAIL_THEN_SUCCEED_ENDPOINT, null,
        MediaType.APPLICATION_JSON_TYPE, num_retries);

    // Validate that after failing a few attempts we finally got back a
    // success code.
    assertEquals("Unexpected result code", SUCCESS_RESULT_CODE, or.getResultCode());

    // Validate that our stubbed AAI actually received the expected number
    // of retried requests.
    assertEquals("Unexpected number of retries", AAI_FailThenSucceed_Stub.MAX_FAILURES + 1,
        AAI_FailThenSucceed_Stub.getCount);
  }


  /**
   * This class provides a simple in-memory REST end point to stand in for a real AAI.
   * <p>
   * This endpoint always returns a valid reply to a GET request and is used for success path
   * testing.
   */
  @Path(GOOD_AAI_ENDPOINT)
  public static class AAI_Success_Stub {

    /**
     * This is the end point for GET requests. It just returns a simple, pre-canned response
     * payload.
     * 
     * @return - A pre-canned response.
     */
    @GET
    public String getEndpoint() {
      return AAI_GET_REPLY_PAYLOAD;
    }
  }


  /**
   * This class provides a simple in-memory REST end point to stand in for a real AAI.
   * <p>
   * This endpoint always returns throws an error instead of responding successfully and is used for
   * certain failure path tests.
   */
  @Path(FAIL_ALWAYS_AAI_ENDPOINT)
  public static class AAI_FailAlways_Stub {

    /**
     * Maintains a running count of the number of GET requests that have been received.
     */
    public static int getCount;


    /**
     * Resets all of the endpoints counters.
     */
    public static void initialize() {
      getCount = 0;
    }


    /**
     * This is the end point for GET requests. It just throws an error instead of returning a valid
     * response.
     * 
     * @return - NONE. We actually throw an exception intentionally instead of returning.
     */
    @GET
    public String getEndpoint() {

      // Keep track of the number of get requests that we have received
      // so that this value can be used for validation purposes later.
      getCount++;

      // Always just throw an error instead of replying successfully.
      throw new UnsupportedOperationException("Intentional Failure");
    }
  }


  /**
   * This class provides a simple in-memory REST end point to stand in for a real AAI.
   * <p>
   * This end point will throw errors instead of responding for a certain number of requests, after
   * which it will return a valid, pre-canned response.
   * 
   * @return - A pre-canned response.
   */
  @Path(FAIL_THEN_SUCCEED_ENDPOINT)
  public static class AAI_FailThenSucceed_Stub {

    /**
     * The number of requests for which we should throw errors before responding successfully.
     */
    public static int MAX_FAILURES = 2;

    /**
     * Maintains a running count of the number of GET requests that have been received.
     */
    public static int getCount;

    /**
     * Maintains a running count of the number of requests which we have failed, so that we will
     * know when to stop failing and return a valid response.
     */
    private static int failCount;


    /**
     * Resets all of the endpoints counters.
     */
    public static void initialize() {
      getCount = 0;
      failCount = 0;
    }


    /**
     * This is the end point for GET requests. It will throw errors for a certain number of requests
     * and then return a valid response.
     * 
     * @return - A pre-canned response string.
     */
    @GET
    public String getEndpoint() {

      // Keep track of the number of get requests that we have received
      // so that this value can be used for validation purposes later.
      getCount++;

      // We only want to fail a set number of times, so check now to
      // see what we should do.
      if (failCount < MAX_FAILURES) {
        failCount++;
        throw new UnsupportedOperationException("Intentional Failure");

      } else {
        // We've failed as often as we need to. Time to reply
        // successfully.
        failCount = 0;
        return AAI_GET_REPLY_PAYLOAD;
      }
    }
  }


  /**
   * This class overrides the behaviour of the {@link RestClientBuilder} used by the
   * {@link RestClient} to just return the in memory client provided by the JerseyTest framework.
   */
  private class TestRestClientBuilder extends RestClientBuilder {

    @Override
    public Client getClient() throws Exception {
      return client();
    }
  }
}
