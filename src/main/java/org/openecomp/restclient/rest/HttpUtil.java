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
package org.openecomp.restclient.rest;

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
    return isExpectedHttpResponseClass(response, '1');
  }

  /**
   * Determines if the provided http response is of the success class.
   *
   * @param response the http response we got from our request
   * @return true if the response is of the success class and false otherwise
   */
  public static boolean isHttpResponseClassSuccess(int response) {
    return isExpectedHttpResponseClass(response, '2');
  }

  /**
   * Determines if the provided http response is of the redirection class.
   *
   * @param response the http response we got from our request
   * @return true if the response is of the redirection class and false otherwise
   */
  public static boolean isHttpResponseClassRedirection(int response) {
    return isExpectedHttpResponseClass(response, '3');
  }

  /**
   * Determines if the provided http response is of the client error class.
   *
   * @param response the http response we got from our request
   * @return true if the response is of the client error class and false otherwise
   */
  public static boolean isHttpResponseClassClientError(int response) {
    return isExpectedHttpResponseClass(response, '4');
  }

  /**
   * Determines if the provided http response is of the server error class.
   *
   * @param response the http response we got from our request
   * @return true if the response is of the server error class and false otherwise
   */
  public static boolean isHttpResponseClassServerError(int response) {
    return isExpectedHttpResponseClass(response, '5');
  }

  /**
   * Helper method to determine if we have received the response class we are expecting.
   *
   * @param response the http response we got from our request
   * @param expectedClass the expected http response class ie: 1, 2, 3, 4, 5 which maps to 1xx, 2xx,
   *        3xx, 4xx, 5xx respectively
   * @return true if the response if of our expected class and false if not
   */
  private static boolean isExpectedHttpResponseClass(int response, char expectedClass) {
    if (response < 100 || response >= 600) {
      return false;
    }

    if (Integer.toString(response).charAt(0) == expectedClass) {
      return true;
    }

    return false;
  }
}
