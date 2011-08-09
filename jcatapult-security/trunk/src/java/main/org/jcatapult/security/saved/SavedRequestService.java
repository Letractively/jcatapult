/*
 * Copyright (c) 2001-2007, JCatapult.org, All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.jcatapult.security.saved;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import com.google.inject.ImplementedBy;

/**
 * <p> This interface defines the service methods to handling Saved HTTP requests. This is used by the {@link
 * org.jcatapult.security.servlet.saved.DefaultSavedRequestWorkflow} but can also be used by registration services for
 * applications that would like to also handle Saved HTTP requests. </p>
 *
 * @author Brian Pontarelli
 */
@ImplementedBy(DefaultSavedRequestService.class)
public interface SavedRequestService {
  /**
   * Allows programmatic saved requests to be added to the session. This is useful for returnTo URIs.
   *
   * @param request    Used to get the session to store the saved request.
   * @param uri        The URI.
   * @param parameters The request parameters (if any - leave null for none).
   */
  void saveRequest(HttpServletRequest request, String uri, Map<String, String[]> parameters);

  /**
   * The first step of the saved request processing. This saves the current request into the session.
   *
   * @param request The current request which is to be saved into the session.
   */
  void saveRequest(HttpServletRequest request);

  /**
   * The second step of the saved request processing. This fetches a saved request from the session and then handles
   * determining what the saved requests URI was. It also sets up the saved request so that after a the caller has
   * redirected to the URI saved request URI, which is the return value of this method, the saved request workflow
   * will handle the final leg of the saved request processing.
   *
   * @param request The request used to get the saved request from the session and process it.
   * @return The saved request URI, if there is one, or null if there is no saved request.
   */
  String processSavedRequest(HttpServletRequest request);

  /**
   * The final step in the saved request process. This occurs after the redirect has happened and the saved request
   * workflow needs to mock out the savaed request. This method returns the HttpServletRequest that mocks the saved
   * request.
   *
   * @param request The request used to fetch the saved request from (if there is one).
   * @return The mocked out HttpServletRequest or the parameter if there is no saved request.
   */
  HttpServletRequest mockSavedRequest(HttpServletRequest request);

  /**
   * This returns the current saved request.
   *
   * @param request Used to get the session to lookup the saved request.
   * @return The saved request or null if there isn't one currently.
   */
  SavedHttpRequest getSavedRequest(HttpServletRequest request);
}