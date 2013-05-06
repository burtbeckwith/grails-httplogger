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

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

/**
 * @author Tomasz Kalkosiński <tomasz.kalkosinski@gmail.com>
 */
public class LogRawRequestInfoFilter extends GenericFilterBean {

    private String headers;
    private String[] headersArray;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;
        addAttributes(httpServletRequest);
        logRawRequestInfo(httpServletRequest);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    protected void initFilterBean() throws ServletException {
        super.initFilterBean();
        headersArray = StringUtils.tokenizeToStringArray(headers, ",");
    }

    protected void addAttributes(HttpServletRequest servletRequest) {
        long startTime = System.currentTimeMillis();
        long requestNumber = HttpLogger.REQUEST_NUMBER_COUNTER.incrementAndGet();

        servletRequest.setAttribute(HttpLogger.START_TIME_ATTRIBUTE, startTime);
        servletRequest.setAttribute(HttpLogger.REQUEST_NUMBER_ATTRIBUTE, requestNumber);
    }

    protected void logRawRequestInfo(HttpServletRequest httpServletRequest) throws IOException {
        if (!logger.isInfoEnabled()) {
            return;
        }

        Long requestNumber = (Long) httpServletRequest.getAttribute(HttpLogger.REQUEST_NUMBER_ATTRIBUTE);
        String method = httpServletRequest.getMethod();

        logger.info("<< #" + requestNumber + ' ' + method + ' ' + createRequestUrl(httpServletRequest));
        if (headersArray.length > 0) {
            String delimiter = "";
            StringBuilder values = new StringBuilder();
            for (String name : headersArray) {
                values.append(delimiter).append(name).append(": '").append(httpServletRequest.getHeader(name)).append('\'');
                delimiter = ", ";
            }
            logger.info("<< #" + requestNumber + ' ' + " headers " + values);
        }
        if ("POST".equalsIgnoreCase(method)) {
            logger.info("<< #" + requestNumber + ' ' + " body: '" + httpServletRequest.getReader() + "'");
        }
    }

    protected String createRequestUrl(HttpServletRequest request) {
        return request.getRequestURL().toString() + (request.getQueryString() == null ? "" : "?" + request.getQueryString());
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }
}
