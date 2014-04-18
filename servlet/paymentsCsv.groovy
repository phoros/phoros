/* Retrieves comprehensive record of payments using QueryGenerator's
 * getPhorosWChange() query.
 */

import groovyx.net.http.*
import groovyx.net.http.HttpResponseException
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

String sparql = "@sparqls@"

  String phorosWChangeQuery() {
    return phorosWChangeQuery("urn")
  } 

  String phorosWChangeQuery(String sortBy) {
    String sortFields = "?urn"
    if (sortBy == "change") {
      sortFields = "?yr ?seq"
    }  

    return """
SELECT ?urn ?lab ?yr ?obs  ?txturn ?txt ?lon ?lat ?chg ?chgamt ?seq ?payrec WHERE {

?urn <http://www.w3.org/1999/02/22-rdf-syntax-ns#label>  ?lab .
?urn <http://shot.holycross.edu/phoros/rdf/paid>  ?payrec .
?payrec <http://www.homermultitext.org/hmt/citedata/payrec_Year> ?yr .
?payrec <http://www.homermultitext.org/hmt/citedata/payrec_TextPassage> ?txturn .
?payrec <http://www.homermultitext.org/hmt/citedata/payrec_TextContent> ?txt .
?payrec <http://purl.org/ontology/olo/core#item> ?seq .

OPTIONAL {
?payrec <http://www.homermultitext.org/hmt/citedata/payrec_ChangeStatus> ?chg .
?payrec <http://www.homermultitext.org/hmt/citedata/payrec_ChangeAmount> ?chgamt .
?payrec <http://www.homermultitext.org/hmt/citedata/payrec_Obols>  ?obs .
}


OPTIONAL {
?urn  <http://shot.holycross.edu/phoros/rdf/locatedAt>  ?loc .
?loc <http://www.homermultitext.org/hmt/citedata/loc_Lat> ?lat .
?loc <http://www.homermultitext.org/hmt/citedata/loc_Lon> ?lon 
}


}
ORDER BY ${sortFields}

"""
  }



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

String q =  phorosWChangeQuery("change")

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

