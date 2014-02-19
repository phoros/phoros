
/* read a list of place names id'ed by pleiades number,
and retrieve coordinate data from pleiades
*/

def f = new File(args[0])

def atom = new groovy.xml.Namespace("http://www.w3.org/2005/Atom")
def georss = new groovy.xml.Namespace("http://www.georss.org/georss")
def gml = new groovy.xml.Namespace("http://www.opengis.net/gml")


def getCount = 0
def locCount = 0
def polyCount = 0

f.eachLine { 
  def cols = it.split(",")
  String siteId = cols[0]
  if (siteId ==~ /^http.+/) {
    getCount++;
    URL pleiadesUrl = new URL("${cols[0]}/atom")
    def pleiadesRoot = new XmlParser().parseText(pleiadesUrl.getText())
    pleiadesRoot[atom.entry].each { ent ->

      def title =  ent[atom.title][0]
      println "Pleiades label: ${title.text()}"
      if (ent[georss.where]) {
	ent[georss.where].each { wh ->
	  if (wh[gml.Point]) {
	    def pt = wh[gml.Point][0]
	    def ll = pt[gml.pos].text().split(/ /)
	    println "lat ${ll[0]}, lon ${ll[1]}"
	    //                            println "GML POINT data " + wh[gml.Point][0] + " = " + pt[gml.pos].text().split(/ /)

	    locCount++;
	  }
	  if (wh[gml.Polygon]) {
	    println "GML Polygon data."
	    def poly =  wh[gml.Polygon][0]
	    def dataVals = poly[gml.exterior][gml.LinearRing][gml.posList][0].text().split()
	    dataVals.eachWithIndex { v, i ->
	      if (i % 2) {
		println "\tTrue mod = lon: " + v + ", so make a point ..\n"
		
	      } else {
		println "\tfalse mod = lat " + v
	      }
	    }
	    polyCount++;
	  }
	}

      }
    }
  }
}

/*


            // should be one entry only ...
    }
}
*/

println "Retrieved data for " + getCount + " sites; ${locCount} had point data, ${polyCount} had polygons."

