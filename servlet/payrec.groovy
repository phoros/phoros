import edu.harvard.chs.cite.CiteUrn
import edu.harvard.chs.cite.CtsUrn

import groovyx.net.http.*
import groovyx.net.http.HttpResponseException
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*



String sparql = "@sparqls@"

boolean done = false

CiteUrn payrecUrn
String doc 
String yr
String txtUrn
String payer
String siteName
String obols = ""
String chg = ""
String chgamount = ""

def imgs = []


if (params.urn) {
  try {

    payrecUrn = new CiteUrn(params.urn)
  } catch (Exception e) {
    response.setStatus(400)
    println "payrecCsv:  Error. Could not make URN from " + params.urn
    done = true
  }
} else {
  response.setStatus(400)
  println "payrecCsv:  Error. No URN parameter given."
  done = true
}



  String payrecQuery(String urn) {

return """
SELECT  ?yr  ?seq ?txturn ?txt ?payer ?sitename ?obols ?chg ?chgamount ?doc WHERE {

<${urn}>  <http://www.homermultitext.org/hmt/citedata/payrec_Year>       ?yr .
<${urn}> <http://www.homermultitext.org/hmt/rdf/appearsOn>  ?doc .
<${urn}> <http://www.homermultitext.org/hmt/citedata/payrec_Sequence>    ?seq .
<${urn}> <http://www.homermultitext.org/hmt/citedata/payrec_TextPassage>  ?txturn .
<${urn}> <http://www.homermultitext.org/hmt/citedata/payrec_TextContent>  ?txt .
<${urn}>  <http://www.homermultitext.org/hmt/citedata/payrec_Place>       ?payer .
?payer <http://www.w3.org/1999/02/22-rdf-syntax-ns#label> ?sitename .


OPTIONAL {
<${urn}>  <http://www.homermultitext.org/hmt/citedata/payrec_ChangeAmount>  ?chgamount .
<${urn}> <http://www.homermultitext.org/hmt/citedata/payrec_Obols>        ?obols .
<${urn}> <http://www.homermultitext.org/hmt/citedata/payrec_ChangeStatus>   ?chg .

}
}
"""
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
  //response.setContentType("text/plain")
  response.setCharacterEncoding('UTF-8')
  response.setHeader( "Access-Control-Allow-Origin", "*")

  String q =  payrecQuery("${payrecUrn}")
  
  def slurper = new groovy.json.JsonSlurper()
  def payrecReply = slurper.parseText(getSparqlReply("application/json", q))


  payrecReply.results.bindings.each { b ->
    doc = b.doc.value
    yr = b.yr.value
    txtUrn = b.txturn.value
    payer = b.payer.value
    siteName = b.sitename.value
    if (b.obols) {
      obols = b.obols?.value
    }
    if (b.chg) {
      chg = b.chg?.value
    }
    if (b.chgamount) {
      chgamount = b.chgamount?.value
    }
  }



  String imgQ = """SELECT ?img WHERE {
  <${doc}> <http://www.homermultitext.org/cite/rdf/illustratedBy>  ?img . }"""

  def slurp2 = new groovy.json.JsonSlurper()
  def txtReply = slurp2.parseText(getSparqlReply("application/json",imgQ))
  txtReply.results.bindings.each { b ->
    imgs.add(b.img.value)
  }
}


html.html {
  head {
    title("Payment record ${payrecUrn}")
    link(type : "text/css", rel : "stylesheet", href : "@coreCss@", title : "CSS stylesheet")
    link(type : "text/css", rel : "stylesheet", href : "front.css", title : "CSS stylesheet")
    script(type: "text/javascript", src : "js/jquery.min.js", "  ")
      script(type: "text/javascript", src : "@citekit@", "  ")
  }
    
  body {
    
    header (role : "banner"){
      h1("Payment record ${payrecUrn}")
      nav(role : "navigation") {
	a (href : "@homeUrl@", "home")
      }
    }    	

    article (role : "main") {

      p ("year ${yr}, ${siteName}")
      if (obols != "") {
	p("Amount in obols: ${obols}.")
	p("Relation to previous payment (if any): ${chg} (${chgamount} obols difference)")
      }
      
      h2("Text")
      blockquote(class: "cite-text", cite : "${txtUrn}", "${txtUrn}")

      h2("Images of year ${yr} document")

      imgs.each { imgUrn ->
	blockquote(class: "cite-image", cite : "${imgUrn}", "${imgUrn}")
      }
    }

    // citekit magic:
    ul (id: "citekit-sources") {
      li (class : "citekit-source cite-text citekit-default", id : "defaulttext", "@texts@")
      li (class : "citekit-source cite-image citekit-default", id : "defaultimage", "data-image-w" : "@ckImgSize@",  "@images@" )
      li (class : "citekit-source cite-collection citekit-default", id : "defaultcollection", "@collections@" )
    }
    footer("@htmlfooter@")
  }
}