import edu.harvard.chs.cite.CiteUrn
import edu.holycross.shot.phoros.QueryGenerator


import groovyx.net.http.*
import groovyx.net.http.HttpResponseException
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*



String sparql = "@sparqls@"
QueryGenerator qg = new QueryGenerator()

boolean done = false
CiteUrn placeUrn
if (params.urn) {
  try {

    placeUrn = new CiteUrn(params.urn)
  } catch (Exception e) {
    response.setStatus(400)
    println "placecsv:  Error. Could not make URN from " + params.urn
    done = true
  }
} else {
  response.setStatus(400)
  println "placecsv:  Error. No URN parameter given."
  done = true
}


/**
 * Submits an encoded query to the configured SPARQL endpoint,
 * and returns the reply.
 * @param acceptType MIME type to specify for reply.
 * @param query SPARQL query to submit.
 * @returns SPARQL reply, as a String.
 */
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


if (!done) {
  //  response.setContentType("text/csv")
  response.setContentType("text/plain")
  response.setCharacterEncoding('UTF-8')
  response.setHeader( "Access-Control-Allow-Origin", "*")

  String q =  qg.siteQuery("${placeUrn}")
  
  def slurper = new groovy.json.JsonSlurper()
  def siteReply = slurper.parseText(getSparqlReply("application/json", q))

  System.err.println "\nQuery: " + q + "\n"
  StringBuffer sb = new StringBuffer("SiteName,Urn,Lon,Lat,Paymt,Yr,Obols,Src\n")

  siteReply.results.bindings.each { b ->
    sb.append("${b.lab.value},${placeUrn},${b.lon?.value},${b.lat?.value},${b.payrec.value},${b.yr.value},${b.obs?.value},${b.txt.value}\n")
  }
  print sb.toString()
}
