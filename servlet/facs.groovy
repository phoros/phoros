/*
  Simple facsimile display of page image + Iliad text.
  Single urn parameter:  may be either a CTS URN with Iliad line
  to read, or a CITE URN with page to read.
 */
import groovyx.net.http.*
import groovyx.net.http.HttpResponseException
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*


import edu.harvard.chs.cite.CtsUrn
import edu.harvard.chs.cite.CiteUrn


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




/*
<http://www.homermultitext.org/hmt/rdf/hasDefaultImage>
 <http://www.w3.org/1999/02/22-rdf-syntax-ns#label> 

<http://www.homermultitext.org/cite/rdf/hasOnIt> 
 <http://purl.org/ontology/olo/core#next> 
 <http://purl.org/ontology/olo/core#previous> 
 */

String lnQuery(String u) {
return """
SELECT ?cite ?pg ?ln ?seq ?img ?lbl ?nxt ?prv WHERE {
?pg <http://www.homermultitext.org/cite/rdf/hasOnIt> ?cite  .

?pg <http://www.homermultitext.org/cite/rdf/hasOnIt> ?ln .
?ln <http://www.homermultitext.org/cts/rdf/hasSequence> ?seq .
?pg <http://www.homermultitext.org/hmt/rdf/hasDefaultImage> ?img .
?pg <http://www.w3.org/1999/02/22-rdf-syntax-ns#label> ?lbl .


OPTIONAL {
?pg <http://purl.org/ontology/olo/core#next>  ?nxt .
}

OPTIONAL {
?pg <http://purl.org/ontology/olo/core#previous>  ?prv .
}
FILTER (str(?cite) = "${u}")
}
ORDER BY (?seq)
"""
}

String pgQuery(String u) {
return """
SELECT ?lnlab ?ln ?seq ?pg ?lbl ?img  ?nxt ?prv WHERE {
?pg <http://www.homermultitext.org/hmt/rdf/hasDefaultImage> ?img .
?pg <http://www.w3.org/1999/02/22-rdf-syntax-ns#label> ?lbl .

OPTIONAL {


?pg <http://www.homermultitext.org/cite/rdf/hasOnIt> ?ln .
?ln <http://www.homermultitext.org/cts/rdf/hasSequence> ?seq .
?ln <http://www.w3.org/1999/02/22-rdf-syntax-ns#label> ?lnlab .
}

OPTIONAL {
?pg <http://purl.org/ontology/olo/core#next>  ?nxt .
}

OPTIONAL {
?pg <http://purl.org/ontology/olo/core#previous>  ?prv .
}

FILTER (str(?pg) = "${u}")
}
ORDER BY ?seq
"""
}


CtsUrn iliadUrn = null
CiteUrn pgUrn = null
String queryString


String urnStr = request.getParameter("urn")
boolean foundUrn = false
try {
  iliadUrn = new CtsUrn(urnStr)
  queryString = lnQuery(urnStr)
  foundUrn = true

} catch (Exception e) {
}

try {
  pgUrn = new CiteUrn(urnStr)
  queryString = pgQuery(urnStr)
  foundUrn = true
} catch (Exception e) {
}


if (!foundUrn) {
  response.setContentType("text/plain")
  println "Please include a parameter named 'urn' with either a valid reference to a line of the Iliad or to a manuscript page."

} else {
  response.setContentType("text/html")
  response.setCharacterEncoding('UTF-8')
  response.setHeader( "Access-Control-Allow-Origin", "*")

  String msg
  if (iliadUrn) {
    msg = "search by text ${iliadUrn}"
  } else {
    msg = "search by folio ${pgUrn}"
  }

  def slurper = new groovy.json.JsonSlurper()
  def jsonData =  slurper.parseText(getSparqlReply("application/json", queryString))
  String pgLabel
  String imgUrn
  String nxtPg = ""
  String prevPg = ""
  def iliadLines = []
  jsonData.results.bindings.each { b ->
    imgUrn = b.img.value
    pgLabel = b.lbl.value
    if (b.nxt) {
      nxtPg = b.nxt.value
    }
    if (b.prv) {
      prevPg = b.prv.value
    }
    if ((b.ln) && (b.ln.value ==~ /urn:cts:greekLit:tlg0012.+/)) {
      iliadLines.add(b.ln.value)
    }
  }

  

  html.html {
    head {
      title("@projectlabel@: facsimile reader")
      link(type : "text/css", rel : "stylesheet", href : "css/browsers.css", title : "CSS stylesheet")
      link(type : "text/css", rel : "stylesheet", href : "@coreCss@", title : "CSS stylesheet")
      script(type: "text/javascript", src : "js/jquery.min.js", "  ")
      script(type: "text/javascript", src : "@citekit@", "  ")
    }

    body {

      header {

	nav {
	  mkp.yield "@projectlabel@: "
	  a(href : '@homeUrl@', "Home")
	  if (prevPg != "") {
	    mkp.yield " | "
	    a(href: "facs?urn=${prevPg}", "previous page")
	  } else {
	    mkp.yield " | previous page"
	  }

	  if (nxtPg != "") {
	    mkp.yield " | "
	    a(href: "facs?urn=${nxtPg}", "next page")
	  } else {
	    mkp.yield " | next page"
	  }

	}
	h2("${pgLabel}")
      }
      String warn = "(Image linked to view zoomable up to full resolution.)"
      article {
	if (iliadLines.size() > 1) {
	  CtsUrn firstLine = new CtsUrn(iliadLines[0])
	  CtsUrn lastLine = new CtsUrn(iliadLines[iliadLines.size() - 1])
	  p {
	    em("Iliad")
	    mkp.yield "${firstLine.getPassageComponent()}-${lastLine.getPassageComponent()}"
	  }
	  String rng = "${firstLine}-${lastLine.getPassageComponent()}"
	  p(warn)
	  div (class: "citekit-compare") {
	    blockquote(class: "cite-image", cite : "${imgUrn}", "${imgUrn}")
	    blockquote(class: "cite-text", cite : "${rng}", "${rng}")
	  }

	} else {
	  p(warn)
	  blockquote(class: "cite-image", cite : "${imgUrn}", "${imgUrn}")
	}
      }    


      // citekit magic:
      ul (id: "citekit-sources") {
	li (class : "citekit-source cite-text citekit-default", id : "defaulttext", "@texts@") 
	li (class : "citekit-source cite-image citekit-default", id : "defaultimage", "data-image-w" : "@ckImgSize@",  "@images@" )
	li (class : "citekit-source cite-collection citekit-default", id : "defaultcollection", "@collections@" )
      }
    }
    footer("@htmlfooter@")
  }
}


