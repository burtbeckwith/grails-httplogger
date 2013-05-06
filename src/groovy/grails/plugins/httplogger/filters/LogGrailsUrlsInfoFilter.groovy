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

package grails.plugins.httplogger.filters
import grails.plugins.httplogger.HttpLogger
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.springframework.web.filter.GenericFilterBean

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
/**
 * @author Tomasz Kalkosiński <tomasz.kalkosinski@gmail.com>
 */
class LogGrailsUrlsInfoFilter extends GenericFilterBean {

    @Override
    void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Long requestNumber = servletRequest[HttpLogger.REQUEST_NUMBER_ATTRIBUTE] as Long
        // this filter has urlPattern '/*' so I need to determine if this request is marked with requestNumber
        if (requestNumber) {
            String controllerName = servletRequest[GrailsApplicationAttributes.CONTROLLER_NAME_ATTRIBUTE]
            String actionName = servletRequest[GrailsApplicationAttributes.ACTION_NAME_ATTRIBUTE]
            Object params = servletRequest.parameterMap

            logger.info "<< #$requestNumber dispatched to $controllerName/$actionName with parsed params $params."
        }
        filterChain.doFilter(servletRequest, servletResponse)
    }
}
