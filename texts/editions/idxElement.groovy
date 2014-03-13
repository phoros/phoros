/*
 * idxElement.groovy: apply an indexing XSLT stylesheet to all diplomatic
 * editions.  XSLT output should be reference=value. This script then
 * reformats this as .csv output, optionally prefixing elemBase before the
 * analytical value.
 *  
 * Requirements: xalan.jar and serializer.jar need to be on CLASSPATH
 *
 * Usage: groovy idxElement.groovy XSLTFILE <elemBase string>
 *
*/
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

File xsl  = new File(args[0])
String elemBase  = ""
if (args.size() == 2) {
  elemBase = args[1]
}


def years =
[
  1,2,3,4,5,7,8
]


String textBase = "urn:cts:phoros:stele1.year"

years.each { yr ->
  String fName = "stele1-year${yr}.xml"
  File xml = new File(fName)

  def factory = TransformerFactory.newInstance()
  def transformer = factory.newTransformer(new StreamSource(xsl))
  File yrResult = new File("streamYear${yr}")
  transformer.transform(new StreamSource(xml), new StreamResult(yrResult))
  yrResult.eachLine { l ->
    def cols = l.split(/=/)
    if (cols.size() == 2) {
       println "${textBase}${yr}:${cols[0]},${elemBase}${cols[1]}"
    } else {
      //System.err.println "${textBase}${yr}:${cols[0]},${elemBase}"
    }
  }
}

