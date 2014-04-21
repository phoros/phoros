/*
 * Requirements: xalan.jar and serializer.jar need to be on CLASSPATH
 *
 * Usage: groovy applyXsl.groovy XSLTFILE XMLFILE
 *
*/
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

if (args.size() != 2) {
  System.err.println "Usage: groovy idxElement.groovy XSLTFILE XMLFILE"
  System.exit(-1)
}

File xsl  = new File(args[0])
File xml = new File(args[1])

def factory = TransformerFactory.newInstance()
def transformer = factory.newTransformer(new StreamSource(xsl))
transformer.transform(new StreamSource(xml), new StreamResult(System.out))
