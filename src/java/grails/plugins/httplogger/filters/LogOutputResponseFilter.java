/**
 * Copyright 2013 TouK
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package grails.plugins.httplogger.filters;
import grails.plugins.httplogger.HttpLogger;
import grails.plugins.httplogger.MultiReadHttpServletResponse;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.GenericFilterBean;

/**
 * @author Tomasz Kalkosiński <tomasz.kalkosinski@gmail.com>
 */
public class LogOutputResponseFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        MultiReadHttpServletResponse responseWrapper = new MultiReadHttpServletResponse((HttpServletResponse) servletResponse);

        filterChain.doFilter(servletRequest, responseWrapper);

        responseWrapper.flushBuffer();

        if (!logger.isInfoEnabled()) {
            return;
        }

        Long requestNumber = (Long) servletRequest.getAttribute(HttpLogger.REQUEST_NUMBER_ATTRIBUTE);
        Long startTime = (Long) servletRequest.getAttribute(HttpLogger.START_TIME_ATTRIBUTE);
        long elapsedTime = System.currentTimeMillis() - startTime;

        logger.info(">> #" + requestNumber + " returned " + responseWrapper.getStatus() + ", took " + elapsedTime + " ms.");
        logger.info(">> #" + requestNumber + " responded with '" + responseWrapper.getCopiedOutput() + "'");
    }
}
