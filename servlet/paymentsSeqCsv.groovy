import edu.holycross.shot.phoros.QueryGenerator

import au.com.bytecode.opencsv.CSVWriter

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

String[] hdr = new String[8]
def idx = 0
["site","name","year","obols","txt","change","sequence","payrecord" ].each {
  hdr[idx] = it
  idx++
}



def sw = new StringWriter()
CSVWriter writer = new CSVWriter(sw)
writer.writeNext(hdr)

def slurper = new groovy.json.JsonSlurper()
String q =  qg.phorosWChangeQuery("change")
def siteReply = slurper.parseText(getSparqlReply("application/json", q))

siteReply.results.bindings.each { b ->
  //["site","name","year","obols","txt","change","sequence" ].each {
  /* ?urn ?lab ?lon ?lat ?payrec ?yr ?obs  ?txt ?chg ?seq */
  String[] record = new String[8]
  record[0] = b.urn.value
  record[1] = b.lab.value
  record[2] = b.yr.value
  record[3] = b.obs?.value
  record[4] = b.txt?.value
  record[5] = b.chg?.value
  record[6] = b.seq?.value
  record[7] = b.payrec.value

  writer.writeNext(record)
}
writer.close();

println sw.toString()

