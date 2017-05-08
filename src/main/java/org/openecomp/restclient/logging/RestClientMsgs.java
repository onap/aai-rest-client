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
package org.openecomp.restclient.logging;

import com.att.eelf.i18n.EELFResourceManager;
import org.openecomp.cl.eelf.LogMessageEnum;

public enum RestClientMsgs implements LogMessageEnum {

  /**
   * Arguments: 
   *    {0} = HTTP operation 
   *    {1} = URL
   */
  HTTP_REQUEST,

  /**
   * Arguments: 
   *    {0} = HTTP operation 
   *    {1} = URL 
   *    {2} = Attempt count.
   */
  HTTP_REQUEST_WITH_RETRIES,

  /**
   * Arguments: 
   *    {0} = HTTP operation 
   *    {1} - URL 
   *    {2} - Operation time in ms.
   */
  HTTP_REQUEST_TIME,

  /**
   * Arguments: 
   *    {0} = HTTP operation 
   *    {1} - URL 
   *    {2} - Operation time in ms. 
   *    {3} - Retry count.
   */
  HTTP_REQUEST_TIME_WITH_RETRIES,

  /**
   * Arguments: 
   *    {0} = HTTP operation 
   *    {1} - URL 
   *    {2} - Error message.
   */
  HTTP_REQUEST_INTERRUPTED,

  /**
   * Arguments: 
   *    {0} = HTTP operation 
   *    {1} - URL 
   *    {2} - Error message.
   */
  HTTP_REQUEST_ERROR,

  /**
   * . Arguments: 
   *    {0} = Target URL
   */
  HEALTH_CHECK_ATTEMPT,

  /**
   * . Arguments: 
   *    {0} = Target URL
   */
  HEALTH_CHECK_SUCCESS,

  /**
   * . Arguments: 
   *    {0} = Target URL 
   *    {1} = failure cause
   */
  HEALTH_CHECK_FAILURE,


  /**
   * . Arguments: 
   *    {0} = URL 
   *    {1} - Response code
   */
  HTTP_RESPONSE;


  /**
   * Static initializer to ensure the resource bundles for this class are loaded...
   */
  static {
    EELFResourceManager.loadMessageBundle("logging/RESTClientMsgs");
  }
}
