/*
 * genAllPhorosCA.groovy: generate list of all payment records
 * 
 * Requirements: xalan.jar and serializer.jar need to be on CLASSPATH,
 * and should be invoked from within texts/editions directory.
 *
 * Usage: groovy genAllPhorosCA.groovy  indexPlaces.xsl
 *
 *
*/
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

File xsl  = new File(args[0])

def lastPayment = [:]


def years =
[
  1,2,3,4,5,7,8
]


String paymentBase = "urn:cite:phoros:payrec"
String textBase = "urn:cts:phoros:stele1.year"
Integer seq = 0

println "Payment,TextPassage,TextContent,Sequence,Year,Place,Obols,ChangeStatus,ChangeAmount"
years.each { yr ->
  String fName = "stele1-year${yr}.xml"
  File xml = new File(fName)

  def factory = TransformerFactory.newInstance()
  def transformer = factory.newTransformer(new StreamSource(xsl))
  File yrResult = new File("streamYear${yr}")
  transformer.transform(new StreamSource(xml), new StreamResult(yrResult))

  yrResult.eachLine { l ->
    seq++;
    def cols = l.split(/=/)


    String txtUrn
    String placeUrn
    String psg
    String obols = ""

    //    if (l ==~ /.+paygroup.+/) {
    if (1 == 2) {

    } else {


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

      // same, up, down
      String chg = ""
      BigDecimal chgAmt = null
    
      if (obols != "") {
	if (lastPayment[placeUrn]) {
	  BigDecimal prevOb = lastPayment[placeUrn] as BigDecimal
	  BigDecimal currOb = obols as BigDecimal
	  if (currOb > prevOb) {
	    chg = "up"
	    chgAmt = currOb - prevOb
	  } else if (prevOb > currOb) {
	    chg = "down"
	    chgAmt = prevOb - currOb
	  } else {
	    chg = "same"
	    chgAmt = 0
	  }
	} else {
	  lastPayment[placeUrn] = obols
	  chgAmt = 0
	  chg = "same"
	}
      }

      if (obols) {
	println "${paymentBase}.${seq},${txtUrn},${psg.replaceAll(/[ ]+/,' ')},${seq},${yr},${placeUrn},${obols},${chg},${chgAmt}"
      } else {
	println "${paymentBase}.${seq},${txtUrn},${psg.replaceAll(/[ ]+/,' ')},${seq},${yr},${placeUrn},,,"
      }
    }
  }
}
