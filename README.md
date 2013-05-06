# grails-logtraffic

Grails plugin for logging HTTP traffic.

It logs:

* request information (url, headers, cookies, method, body),
* grails dispatch information (controller, action, parameters),
* response information (elapsed time and body).

## Installation

Add the following to your `BuildConfig.groovy`

```
runtime ":httplogger:1.0"
```

And be sure to enable logging for `grails.plugins.httlogger` at INFO level in `Config.groovy`:

```
    info   'grails.plugins.httplogger'
```

## Configuration

You can configure it with your `Config.groovy` like this:

```groovy
grails.plugins.httplogger.enabled = true
grails.plugins.httplogger.headers = 'Cookie, Accept-Language'
grails.plugins.httplogger.urlPattern = '/rest/*'
```

## Usage

Plugin should work transparently without any actions. Example output looks like this:

```
2013-05-06 17:16:00,331 [http-bio-8080-exec-9] INFO  filters.LogRawRequestInfoFilter  - << #1 GET http://localhost:8080/riddle/rest/index?username=admin&search=foo
2013-05-06 17:16:00,340 [http-bio-8080-exec-9] INFO  filters.LogRawRequestInfoFilter  - << #1 headers Cookie: 'JSESSIONID=DF4EA5725AC4A4990281BD96963739B0; splashShown1.6=1', Accept-Language: 'en-US,en;q=0.8,pl;q=0.6', X-MyHeader: 'null'
2013-05-06 17:16:00,342 [http-bio-8080-exec-9] INFO  filters.LogGrailsUrlsInfoFilter  - << #1 dispatched to rest/index with parsed params [username:[admin], search:[foo]].
2013-05-06 17:16:00,731 [http-bio-8080-exec-9] INFO  filters.LogOutputResponseFilter  - >> #1 returned 200, took 405 ms.
2013-05-06 17:16:00,745 [http-bio-8080-exec-9] INFO  filters.LogOutputResponseFilter  - >> #1 responded with '{count:0}'
```

```
2013-05-06 17:18:55,799 [http-bio-8080-exec-8] INFO  filters.LogRawRequestInfoFilter  - << #2 POST http://localhost:8080/riddle/third/login
2013-05-06 17:18:55,799 [http-bio-8080-exec-8] INFO  filters.LogRawRequestInfoFilter  - << #2 headers Cookie: 'JSESSIONID=DF4EA5725AC4A4990281BD96963739B0; splashShown1.6=1', Accept-Language: 'en-US,en;q=0.8,pl;q=0.6', X-MyHeader: 'null'
2013-05-06 17:18:55,800 [http-bio-8080-exec-8] INFO  filters.LogRawRequestInfoFilter  - << #2 body: 'username=admin&password=password'
2013-05-06 17:18:55,801 [http-bio-8080-exec-8] INFO  filters.LogOutputResponseFilter  - >> #2 returned 404, took 3 ms.
2013-05-06 17:18:55,802 [http-bio-8080-exec-8] INFO  filters.LogOutputResponseFilter  - >> #2 responded with ''
```


