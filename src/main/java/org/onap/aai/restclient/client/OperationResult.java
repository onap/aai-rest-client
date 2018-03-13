/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017-2018 Amdocs
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
 */
package org.onap.aai.restclient.client;

import javax.ws.rs.core.MultivaluedMap;

public class OperationResult {

  private String requestedLink;
  private String result;
  private String failureCause;
  private boolean fromCache;
  private int resultCode;
  private int numRetries;
  private MultivaluedMap<String, String> responseHeaders;


  public OperationResult() {
    super();
    this.numRetries = 0;
    this.fromCache = false;
  }

  /**
   * Instantiates a new operation result.
   *
   * @param resultCode the result code
   * @param result the result
   */
  public OperationResult(int resultCode, String result) {
    this();
    this.resultCode = resultCode;
    this.result = result;
  }

  /**
   * Get the HTTP headers of the response.
   *
   * @return the HTTP headers of the response.
   */
  public MultivaluedMap<String, String> getHeaders() {
    return responseHeaders;
  }

  /**
   * Returns true if the HTTP Status Code 200 <= x <= 299
   *
   * @return true, if successful
   */
  public boolean wasSuccessful() {
    return (resultCode > 199 && resultCode < 300);
  }

  public void setHeaders(MultivaluedMap<String, String> headers) {
    this.responseHeaders = headers;
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
  
  /**
   * Sets the result.
   *
   * @param resultCode the result code
   * @param result the result
   */
  public void setResult(int resultCode, String result) {
    this.resultCode = resultCode;
    this.result = result;
  }
  
  public void setFailureCause(String failureCause) {
    this.failureCause = failureCause;
  }

  /**
   * Sets the failure cause.
   *
   * @param resultCode the result code
   * @param failureCause the result error
   */
  public void setFailureCause(int resultCode, String failureCause) {
    this.resultCode = resultCode;
    this.failureCause = failureCause;
  }

  
  public void setResultCode(int resultCode) {
    this.resultCode = resultCode;
  }

  public String getRequestedLink() {
    return requestedLink;
  }

  public void setRequestedLink(String requestedLink) {
    this.requestedLink = requestedLink;
  }

  public boolean isFromCache() {
    return fromCache;
  }

  public void setFromCache(boolean fromCache) {
    this.fromCache = fromCache;
  }

  public int getNumRetries() {
    return numRetries;
  }

  public void setNumRetries(int numRetries) {
    this.numRetries = numRetries;
  }

  @Override
  public String toString() {
    return "OperationResult [result=" + result + ", requestedLink=" + requestedLink
        + ", failureCause=" + failureCause + ", resultCode=" + resultCode + ", numRetries="
        + numRetries + ", responseHeaders=" + responseHeaders + "]";
  }

}
