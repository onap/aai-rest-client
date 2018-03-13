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
package org.onap.aai.restclient.rest;

public class HttpUtil {

  /**
   * Determines if the provided HTTP response is present in the provided list of acceptable response
   * codes.
   *
   * @param response the http response we got from our request
   * @param list the list of acceptable response codes
   * @return true if the http response is in the provided list
   */
  public static boolean isHttpResponseInList(int response, int... list) {
    for (int checkCode : list) {
      if (checkCode == response) {
        return true;
      }
    }
    return false;
  }

  /**
   * Determines if the provided http response is of the information class.
   *
   * @param response the http response we got from our request
   * @return true if the response is of the informational class and false otherwise
   */
  public static boolean isHttpResponseClassInformational(int response) {
    return ( response >= 100 && response <= 199);
  }

  /**
   * Determines if the provided http response is of the success class.
   *
   * @param response the http response we got from our request
   * @return true if the response is of the success class and false otherwise
   */
  public static boolean isHttpResponseClassSuccess(int response) {
    return ( response >= 200 && response <= 299);

  }

  /**
   * Determines if the provided http response is of the redirection class.
   *
   * @param response the http response we got from our request
   * @return true if the response is of the redirection class and false otherwise
   */
  public static boolean isHttpResponseClassRedirection(int response) {
    return ( response >= 300 && response <= 399);
  }

  /**
   * Determines if the provided http response is of the client error class.
   *
   * @param response the http response we got from our request
   * @return true if the response is of the client error class and false otherwise
   */
  public static boolean isHttpResponseClassClientError(int response) {
    return ( response >= 400 && response <= 499);
  }

  /**
   * Determines if the provided http response is of the server error class.
   *
   * @param response the http response we got from our request
   * @return true if the response is of the server error class and false otherwise
   */
  public static boolean isHttpResponseClassServerError(int response) {
    return ( response >= 500 && response <= 599);
  }

}
