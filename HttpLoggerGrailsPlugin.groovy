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
import grails.plugins.httplogger.filters.LogGrailsUrlsInfoFilter
import grails.plugins.httplogger.filters.LogOutputResponseFilter
import grails.plugins.httplogger.filters.LogRawRequestInfoFilter
import org.springframework.util.StringUtils

class HttpLoggerGrailsPlugin {
    def version = "1.0"
    def grailsVersion = "2.0 > *"
    def pluginExcludes = ["grails-app/views/error.gsp"]
    def title = "Http Logger Plugin"
    def author = "Tomasz Kalkosiński"
    def authorEmail = "tomasz.kalkosinski@gmail.com"
    def description = '''\
Grails plugin for logging HTTP traffic.

It logs:
* request information (url, headers, cookies, method, body),
* grails dispatch information (controller, action, parameters),
* response information (elapsed time and body).

'''
    def documentation = "https://github.com/TouK/grails-httplogger"
    def license = "APACHE"
    def organization = [ name: "TouK", url: "http://www.touk.pl/" ]
    def developers = [ [ name: "Tomasz Kalkosiński", email: "tomasz.kalkosinski@gmail.com" ]]
    def issueManagement = [ system: "GitHub", url: "https://github.com/TouK/grails-httplogger/issues" ]
    def scm = [ url: "https://github.com/TouK/grails-httplogger" ]

    Map getConfiguration() {
        application.config.grails.plugins.httplogger
    }

    def getWebXmlFilterOrder() {
        def FilterManager = getClass().getClassLoader().loadClass('grails.plugin.webxml.FilterManager')
        [
            logRawRequestInfoFilter: FilterManager.GRAILS_WEB_REQUEST_POSITION - 10,
            logGrailsUrlsInfoFilter: FilterManager.DEFAULT_POSITION + 10,
            logOutputResponseFilter: FilterManager.DEFAULT_POSITION
        ]
    }

    def doWithWebDescriptor = { webXml ->
        Map configuration = application.config.grails.plugins.httplogger
        Boolean enabled = configuration.enabled == null ? true : configuration.enabled
        String headers = configuration.headers ?: 'Cookie'
        String urlPattern = configuration.urlPattern ?: '/*'
        if (!enabled) return

        def contextParam = webXml.'context-param'

        contextParam[contextParam.size() - 1] + {
            'filter' {
                'filter-name'(StringUtils.uncapitalize(LogRawRequestInfoFilter.simpleName))
                'filter-class'(LogRawRequestInfoFilter.name)
                'init-param' {
                    'param-name'('headers')
                    'param-value'(headers)
                }
            }
        }

        [LogGrailsUrlsInfoFilter, LogOutputResponseFilter].each { def filterClass ->
            contextParam[contextParam.size() - 1] + {
                'filter' {
                    'filter-name'(StringUtils.uncapitalize(filterClass.simpleName))
                    'filter-class'(filterClass.name)
                }
            }
        }

        def filter = webXml.'filter'
        [LogRawRequestInfoFilter, LogOutputResponseFilter].each { def filterClass ->
            filter[filter.size() - 1] + {
                'filter-mapping'{
                    'filter-name'(StringUtils.uncapitalize(filterClass.simpleName))
                    'url-pattern'(urlPattern)
                    'dispatcher'('REQUEST')
                }
            }
        }
        [LogGrailsUrlsInfoFilter].each { def filterClass ->
            filter[filter.size() - 1] + {
                'filter-mapping'{
                    'filter-name'(StringUtils.uncapitalize(filterClass.simpleName))
                    'url-pattern'('/*')
                    'dispatcher'('FORWARD')
                    'dispatcher'('ERROR')
                }
            }
        }
    }

    def doWithSpring = {}
    def doWithDynamicMethods = { ctx -> }
    def doWithApplicationContext = { applicationContext -> }
    def onChange = { event -> }
    def onConfigChange = { event -> }
    def onShutdown = { event -> }
}
