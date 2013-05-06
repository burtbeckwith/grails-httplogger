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

package grails.plugins.httplogger

import java.util.concurrent.atomic.AtomicLong

/**
 * @author Tomasz Kalkosiński <tomasz.kalkosinski@gmail.com>
 */
class HttpLogger {
    public static final AtomicLong REQUEST_NUMBER_COUNTER = new AtomicLong()
    public static final String START_TIME_ATTRIBUTE = 'httplogger.startTime'
    public static final String REQUEST_NUMBER_ATTRIBUTE = 'httplogger.requestNumber'
}
