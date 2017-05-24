package org.openecomp.restclient.rest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class HttpUtilTest {

  /**
   * Test case initialization
   * 
   * @throws Exception the exception
   */
  @Before
  public void init() throws Exception {
  }
  
  @Test
  public void validateAccesors() {
    
    assertFalse(HttpUtil.isHttpResponseClassInformational(-1));
    assertFalse(HttpUtil.isHttpResponseClassInformational(99));
    assertTrue(HttpUtil.isHttpResponseClassInformational(183));
    assertFalse(HttpUtil.isHttpResponseClassInformational(200));

    assertFalse(HttpUtil.isHttpResponseClassSuccess(199));
    assertTrue(HttpUtil.isHttpResponseClassSuccess(202));
    assertFalse(HttpUtil.isHttpResponseClassSuccess(300));

    assertFalse(HttpUtil.isHttpResponseClassRedirection(299));
    assertTrue(HttpUtil.isHttpResponseClassRedirection(307));
    assertFalse(HttpUtil.isHttpResponseClassRedirection(401));

    assertFalse(HttpUtil.isHttpResponseClassClientError(399));
    assertTrue(HttpUtil.isHttpResponseClassClientError(404));
    assertFalse(HttpUtil.isHttpResponseClassClientError(555));

    assertFalse(HttpUtil.isHttpResponseClassServerError(499));
    assertTrue(HttpUtil.isHttpResponseClassServerError(504));
    assertFalse(HttpUtil.isHttpResponseClassServerError(662));
    
    int[] successCodes = { 201, 202, 205, 299 };
    
    assertTrue(HttpUtil.isHttpResponseInList(201, successCodes));
    assertFalse(HttpUtil.isHttpResponseInList(301, successCodes));
    
  }
    
}
