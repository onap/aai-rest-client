# Common REST Client Library

This library provides a single client implementation to be used by micro services for communicating via its REST API.

---

## Usage
In order to make the _REST Client_ library available to your microservice, include the following dependency in your service's pom.xml:

    <!-- Common REST Client Library -->
    <dependency>
        <groupId>org.openecomp.aai</groupId>
        <artifactId>rest-client</artifactId>
        <version>1.2.0-SNAPSHOT</version>
    </dependency>
    
## Code Examples

### Creating and Configuring a Client Instance
In order to start talking to a service, you need to create a client instance and configure it.  The _RestClient_ uses a fluent interface which allows it to be both instantiated and configured as in the following examples:

i)  A client using an SSL Client Certificate:

    // Create an instance of the Rest Client and configure it.
    RestClient myClient = new RestClient()
        .authenticationMode(RestAuthenticationMode.SSL_CERT)
        .validateServerHostname(false)
        .validateServerCertChain(true)
        .clientCertFile("certificate_filename")
        .trustStroe("trust_store_filename")
        .connectTimeoutMs(1000)
        .readTimeoutMs(1000)
        
ii) A client using SSL Basic-Auth:

    // Create an instance of the Rest Client and configure it.
    RestClient myClient = new RestClient()
        .authenticationMode(RestAuthenticationMode.SSL_BASIC)
        .basicAuthUsername("username")
        .basicAuthPassword("password")
        .connectTimeoutMs(1000)
        .readTimeoutMs(1000)

iii) A client using non-authentication HTTP:

    // Create an instance of the Rest Client and configure it.
    RestClient myClient = new RestClient()
        .authenticationMode(RestAuthenticationMode.HTTP_NOAUTH)
        .connectTimeoutMs(1000)
        .readTimeoutMs(1000)
        
Note, that all of the above configuration parameters are optional and will be set to default values if they are not specified.

### Querying The A&AI
Once your service has a client instance, it can query the _Active & Available Inventory_ by specifying an HTTP endpoint, headers, and the expected response format:

	MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
	headers.put("Accept", Arrays.asList(new String[]{"application/json"}));
	headers.put("X-FromAppId", Arrays.asList(new String[]{"APP-ID"}));
	headers.put("X-TransactionId", Arrays.asList(new String[]{UUID.randomUUID().toString()}));

    OperationResult result = myClient.queryActiveInventory("http://some/endpoint", headers, RestClient.RESPONSE_MIME_TYPE.JSON);
    
    // You can also specify number of re-tries:
    int retries = 3
    OperationResult result = myClient.queryActiveInventory("http://some/endpoint", headers, RestClient.RESPONSE_MIME_TYPE.JSON, retries);

         
The result of the query is returned as an _OperationResult_ object, which can be unpacked in the following manner:

The standard HTTP result code received back from the _A&AI_ is accessible as follows:

    int resultCode = getResultCode()

The actual result payload is accessible in the following manner:

    String resultPayload = result.getResult()

Finally, in the event of a failure, a failure cause message will be populated and can be accessed as follows:

    String failureCause = result.getFailureCause() 
