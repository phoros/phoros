import edu.harvard.chs.f1k.GreekNode


/*
 * Write a csv collection of payment records analyzed from all
 * normalized XML editions.  
 * 
 * Requires: f1k.jar on the CLASSPATH.
 *
 * Usage:  groovy phorosca.groovy <XMLFILES>
 *
 * Output format: csv with the following header line:
 *
 * Record,TextPsg,OrcaText,Year,Place,Obols
 *
 */


groovy.xml.Namespace tei = new groovy.xml.Namespace("http://www.tei-c.org/ns/1.0")


String urnBase = "urn:cite:phoros:payrec"
Integer recordCount = 0

println "Payment,TextPassage,TextContent,Year,Place,Obols"

String ctsUrnBase = "urn:cts:phoros:stele1.year"
args.each { fName ->
  String yrNum = fName.replaceFirst("stele1-year","")
  yrNum = yrNum.replaceFirst("-normalized.xml", "")
  String ctsUrn = "${ctsUrnBase}${yrNum}.hc"

  File f = new File(fName)
  groovy.util.Node root = new XmlParser().parse(f)
  String psg = ""

  root[tei.text][tei.body][tei.div].each { face ->
    String faceId = face.'@n'
    face[tei.div].each { sect ->
      String sectId = faceId + "." + sect.'@n'
      // test if there is either a placeName or 
      sect[tei.ab].each { ln ->

	if ((ln[tei.placeName].size() > 0) || (ln[tei.measure].size() > 0)) {
	  recordCount++;
	  print "${urnBase}.${recordCount},"
	  print "${ctsUrn}:${psg},"

	  GreekNode n = new GreekNode(ln)
	  txt = n.collectText()
	  String txt2 = txt.replaceAll(/[\t\n ]+/, " ")
	  print "${txt2},${yrNum},"


	  psg = sectId + "." + ln.'@n'

	  ln[tei.placeName].each { pl ->
	    if (pl.'@n') {
	      print pl.'@n'
	    }
	  }
	  print ","
	  ln[tei.measure][tei.num].each { num ->
	    if (num.'@value') {
	      print num.'@value'
	    }
	  }
	  println ""
	}
      }
    }
  }
}


// [tei.TEI][tei.text][tei.body][tei.div][tei.div][tei.ab]


//[tei.place]