package org.openecomp.restclient.enums;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class RestAuthenticationModeTest {

  /**
   * Test case initialization
   * 
   * @throws Exception the exception
   */
  @Before
  public void init() throws Exception {
  }
  
  @Test
  public void validateEnumMappings() {
    
    assertEquals(RestAuthenticationMode.getRestAuthenticationMode(null), RestAuthenticationMode.UNKNOWN_MODE);
    assertEquals(RestAuthenticationMode.getRestAuthenticationMode("OAuth"), RestAuthenticationMode.UNKNOWN_MODE);
    assertEquals(RestAuthenticationMode.getRestAuthenticationMode("SSL_BASIC"), RestAuthenticationMode.SSL_BASIC);
    assertEquals(RestAuthenticationMode.getRestAuthenticationMode("SSL_CERT"), RestAuthenticationMode.SSL_CERT);
    assertEquals(RestAuthenticationMode.getRestAuthenticationMode("HTTP_NOAUTH"), RestAuthenticationMode.HTTP_NOAUTH);
    
    assertEquals(RestAuthenticationMode.SSL_BASIC.getAuthenticationModeLabel(),"SSL_BASIC");
    
    
  }
    
}
