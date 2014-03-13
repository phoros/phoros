
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

File xsl  = new File(args[0])


def years =
[
  1,2,3,4,5,7,8
]

String stoneBase = "urn:cite:phoros:stones.atl"
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
    println "${textBase}${yr}:${cols[0]},${stoneBase}${cols[1]}"
  }
}

