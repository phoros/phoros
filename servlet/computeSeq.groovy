import edu.holycross.shot.phoros.QueryGenerator
/*
  write TTL statements describing relation of each extant payment record to 
  previous extant record for that city.  Initial record is considered 'same'
  as (non-existent) predecessor.
 */

import groovyx.net.http.*
import groovyx.net.http.HttpResponseException
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

String sparql = "@sparqls@"
QueryGenerator qg = new QueryGenerator()


String getSparqlReply(String acceptType, String query) {
  String replyString
  def encodedQuery = URLEncoder.encode(query)
  def q = "@sparqls@query?query=${encodedQuery}"
  if (acceptType == "application/json") {
    q +="&output=json"
  }

  def http = new HTTPBuilder(q)
  http.request( Method.GET, ContentType.TEXT ) { req ->
    headers.Accept = acceptType
    response.success = { resp, reader ->
      replyString = reader.text
    }
  }
  return replyString
}



//response.setContentType("application/json")
response.setContentType("text/plain")
response.setCharacterEncoding('UTF-8')
response.setHeader( "Access-Control-Allow-Origin", "*")

def lastSeen = [:]

def slurper = new groovy.json.JsonSlurper()
String q =  qg.phorosSeqQuery()
def siteReply = slurper.parseText(getSparqlReply("application/json", q))

siteReply.results.bindings.each { b ->
  String site = b.site.value
  String rec = b.payrec.value
  String obols = b.obs?.value

  if (obols != null) {
    if (!lastSeen.keySet().contains(site)) {

      println "<${rec}> <http://shot.holycross.edu/phoros/rdf/change>" + ' "same" .'
      lastSeen[site] = obols.toBigDecimal()

    } else {
      def lastVal = lastSeen[site]
      def obolNum = obols.toBigDecimal()
      //println "Compare current " + obols + " with last seen ${lastVal} of class "  + lastVal.getClass()
      if (obolNum < lastVal) {
	println "<${rec}> <http://shot.holycross.edu/phoros/rdf/change> " + '"down" .'
      } else if (obolNum > lastVal) {
	println "<${rec}> <http://shot.holycross.edu/phoros/rdf/change> " + '"up" .'

      } else {
	println "<${rec}> <http://shot.holycross.edu/phoros/rdf/change> " + '"same" .'
      }
      lastSeen[site] = obolNum
    }
  }
}