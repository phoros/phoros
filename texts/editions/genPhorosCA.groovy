/*
 * genPhorosCA.groovy: generate ORCA collection analysis of phoros.
 * 
 * Requirements: xalan.jar and serializer.jar need to be on CLASSPATH
 *
 * Usage: groovy genPhorosCA.groovy XSLTFILE
 *
*/
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

File xsl  = new File(args[0])


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

    String txtUrn
    String placeUrn
    String psg
    String obols = ""
    switch (cols.size()) {

    case 4:
    obols = cols[3]
    // fall through:
    case 3:
    txtUrn = textBase + yr + ":" + cols[0]
    placeUrn = cols[1]
    psg = cols[2]
    break

    default:
    System.err.println "Bad input : " + l
    break
    }
    println "${txtUrn},${placeUrn},${obols},${psg.replaceAll(/[ ]+/,' ')}"
  }
}
