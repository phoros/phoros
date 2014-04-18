/* Retrieves comprehensive record of payments using QueryGenerator's
 * getPhorosWChange() query.
 */

import edu.holycross.shot.phoros.QueryGenerator



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



response.setContentType("application/json")
response.setCharacterEncoding('UTF-8')
response.setHeader( "Access-Control-Allow-Origin", "*")



def slurper = new groovy.json.JsonSlurper()

String q =  qg.phorosWChangeQuery("change")

def siteReply = slurper.parseText(getSparqlReply("application/json", q))

StringBuffer buff = new StringBuffer("siteUrn,siteName,year,obols,change,chgamt,lon,lat,payrec,seq\n")
siteReply.results.bindings.each { b ->
  // Guaranteed to be non-null when query succeeds:
  String urn = b.urn.value
  String siteName = b.lab.value
  String yr = b.yr.value
  String payrec = b.payrec.value
  String seq = b.seq.value


  // Possibly null payment data:
  String obols = ""
  String chg = ""
  String chgamt = ""
  if (b.obs?.value) {
  obols = b.obs.value
  }
  if (b.chgamt?.value) {
    lat = b.chgamt.value
  } 
  if (b.chg?.value) {
    chg = b.chg.value
  }


  // Possibly null geo data:
  String lat = ""
  String lon = ""
  if (b.lat?.value) {
    lat = b.lat.value
  } 
  if (b.lon?.value) {
    lon = b.lon.value
  }


  buff.append( "${urn},${siteName},${yr},${obols},${chg},${chgamt},${lon},${lat},${payrec},${seq}\n")
}

println buff.toString()

