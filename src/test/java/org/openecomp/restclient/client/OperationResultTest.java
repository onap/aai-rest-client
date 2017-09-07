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
package org.openecomp.restclient.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.core.util.MultivaluedMapImpl;

public class OperationResultTest {

  /**
   * Test case initialization
   * 
   * @throws Exception the exception
   */
  @Before
  public void init() throws Exception {
  }
  
  @Test
  public void validateConstruction() {
    
    OperationResult opResult = new OperationResult();
    assertEquals(opResult.getNumRetries(),0);
    assertFalse(opResult.isFromCache());
    assertFalse(opResult.wasSuccessful());
    opResult.setResultCode(612);
    assertFalse(opResult.wasSuccessful());
    assertNull(opResult.getHeaders());
    
    opResult = new OperationResult(204,"no content found");
    assertEquals(opResult.getResultCode(),204);
    assertEquals(opResult.getResult(),"no content found");
    assertTrue(opResult.wasSuccessful());
    
    MultivaluedMap<String,String> multiMap = new MultivaluedMapImpl();
    multiMap.add("p1","v1");
    multiMap.add("p2","v2");
    opResult.setHeaders(multiMap);
    assertNotNull(opResult.getHeaders());
    assertEquals(opResult.getHeaders().size(), 2);
    
  }
  
  @Test
  public void validateAccesors() {
    
    OperationResult opResult = new OperationResult();
    
    opResult.setFailureCause("failure");
    opResult.setFromCache(false);
    opResult.setNumRetries(101);
    opResult.setRequestedLink("http://localhost:1234");
    opResult.setResult("result");
    opResult.setResultCode(555);

    assertEquals(opResult.getFailureCause(), "failure");
    assertFalse(opResult.isFromCache());
    assertEquals(opResult.getNumRetries(),101);
    assertEquals(opResult.getRequestedLink(),"http://localhost:1234");
    assertEquals(opResult.getResult(), "result");
    assertEquals(opResult.getResultCode(),555);
    
    opResult.setResult(212, "mostly successful");
    assertEquals(opResult.getResultCode(),212);
    assertEquals(opResult.getResult(), "mostly successful");
    
    assertTrue(opResult.toString().contains("OperationResult"));
    
    opResult.setFailureCause(511, "things melting");
    assertEquals(opResult.getResultCode(),511);
    assertEquals(opResult.getFailureCause(), "things melting");
    
  }
    
}
