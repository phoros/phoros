@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.5.0-RC2' )
import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*



File f = new File(args[0])


def pleiadesPathMap = [:]

f.eachLine { l ->
    def cols = l.split(",")
    String siteId = cols[0]
    String urn = cols[1]
    def uriParts = []
    if (siteId ==~ /^http.+/) {
      uriParts = siteId.split("http://pleiades.stoa.org")
      pleiadesPathMap[urn] = uriParts[1]
    }
}


EncoderRegistry encoders = new EncoderRegistry();
encoders.setCharset('UTF-8')

println "Place,Lon,Lat"
pleiadesPathMap.keySet().each { u ->
  def http = new HTTPBuilder()
  http.setEncoderRegistry(encoders)
  http.request( 'http://pleiades.stoa.org', GET, TEXT ) { req ->
    uri.path = "${pleiadesPathMap[u]}/json"
    headers.'User-Agent' = "Mozilla/5.0 Firefox/3.0.4"
    headers.Accept = 'application/json'
 
    response.success = { resp, reader ->
      assert resp.statusLine.statusCode == 200
      def jsonString = reader.text
      def json = new groovy.json.JsonSlurper().parseText(jsonString)
      if (json.reprPoint) {
	println "${u},${json.reprPoint[0]},${json.reprPoint[1]}"
      }

  }
 
    response.'404' = {
      println 'Not found'
    }
  }
}
