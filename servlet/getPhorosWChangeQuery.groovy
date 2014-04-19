
import groovyx.net.http.*
import groovyx.net.http.HttpResponseException
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

response.setContentType("text/text")
response.setCharacterEncoding('UTF-8')
response.setHeader( "Access-Control-Allow-Origin", "*")

String sortFields = "?urn"

if (params.type == "change") {
  sortFields = "?yr ?seq"  
}

println """
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
