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
