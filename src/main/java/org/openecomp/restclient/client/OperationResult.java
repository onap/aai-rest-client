/**
 * ============LICENSE_START=======================================================
 * RestClient
 * ================================================================================
 * Copyright © 2017 AT&T Intellectual Property.
 * Copyright © 2017 Amdocs
 * All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 *
 * ECOMP and OpenECOMP are trademarks
 * and service marks of AT&T Intellectual Property.
 */
package org.openecomp.restclient.client;

import javax.ws.rs.core.MultivaluedMap;

public class OperationResult {

  private String result;
  private String failureCause;
  private int resultCode;
  private MultivaluedMap<String, String> headers;

  /**
   * Get the HTTP headers of the response.
   *
   * @return the HTTP headers of the response.
   */
  public MultivaluedMap<String, String> getHeaders() {
    return headers;
  }

  public void setHeaders(MultivaluedMap<String, String> headers) {
    this.headers = headers;
  }


  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public int getResultCode() {
    return resultCode;
  }

  public String getFailureCause() {
    return failureCause;
  }

  public void setFailureCause(String failureCause) {
    this.failureCause = failureCause;
  }

  public void setResultCode(int resultCode) {
    this.resultCode = resultCode;
  }

  public OperationResult() {
    super();
  }

  @Override
  public String toString() {
    return "OperationResult [result=" + result + ", resultCode=" + resultCode + "]";
  }

}
