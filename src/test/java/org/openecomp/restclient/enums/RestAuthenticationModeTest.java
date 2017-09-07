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
