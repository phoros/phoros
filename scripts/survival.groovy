import au.com.bytecode.opencsv.CSVReader
		

/*
 * Formats the csv payment records collection as an HTML table.
 * 
 * Requires: opencsv.jar on the CLASSPATH.
 *
 * Usage:  groovy survival.groovy <FILENAME>
 *
 */


File f = new File(args[0])
File placeNames = new File("places.csv")
CSVReader places = new CSVReader(new FileReader(placeNames))
def nameMap = [:]
def reverseMap = [:]
places.readAll().each { pl ->
  nameMap[pl[1]] = pl[2]
  reverseMap[pl[2]] = pl[1]
}


// map of city to years its name survives
def survives = [:]
// map of city to years its payment survives
def paymentAmount = [:]
// map of city to obol amounts
def obolsSurvive = [:]


CSVReader reader = new CSVReader(new FileReader(f))
reader.readAll().each { ln ->

  if (ln.size() != 7) {
    println "Error. Size of LINE = " + ln.size()

  } else {

    String yearStr = ln[4]
    Integer yr = null
    try {
      yr = yearStr.toInteger()
    } catch (Exception e) {
      System.err.println "${yearStr} is not an integer year."
    }
    if (yr != null) {
      String place = ln[5]
      String plName = nameMap[place]

      if (survives[plName]) {
	def survivalList = survives[plName]
	survivalList.add(yr)
	survives[plName] = survivalList
      } else {
	def initList = [yr]
	survives[plName] = initList
      }

      
      Integer obols
      if (ln[5] != "") {
	String obolStr = ln[6]

	try { 
	  obols = obolStr.toFloat()
	} catch (Exception ex) {
	  System.err.println "Num obols wrong : " + obolStr
	}


	if (paymentAmount[plName]) {
	  def amtList = paymentAmount[plName]
	  amtList.add(yr)
	  paymentAmount[plName] = amtList

	  def obList = obolsSurvive[plName]
	  obList.add(obols)
	  obolsSurvive[plName] = obList

	} else {
	  def initList = [yr]
	  paymentAmount[plName] = initList

	  def initObol = [obols]
	  obolsSurvive[plName] = initObol
	}
      }
    }
  }
}

def colKeys = [1: "Year 1", 2: "Year 2", 3:"Year 3", 4: "Year 4", 5: "Year 5", 7: "Year 7",8: "Year 8"]

def yrList = colKeys.keySet().sort()

print "<table>\n<tr><th>Site</th>"
colKeys.keySet().sort().each { lab ->
  print "<th>${colKeys[lab]}</th>"
}
println "</tr>"

survives.keySet().sort().each {  k ->
  if (k == null) {
    System.err.println "NULL KEY!"
  } else {
  print "<tr><td><a href='place?urn=" + reverseMap[k] + "'>" + k + "</a></td> " 
  def surviving = survives[k]
  def paid = paymentAmount[k]
  def obList = obolsSurvive[k]

  yrList.each { col->

    if (surviving?.contains(col)) {
      if (paid?.contains(col)) {


	  System.err.println "${k}, col ${col}: amount = " + paid
	  
	  Integer idx = 0
	  paid.eachWithIndex { p, i ->
	    if (p == col) {
	      idx = i
	    }
	  }
	  if (obList[idx]) {
	    print "<td class='green'>"
	    print "${obList[idx]}"
	    print "</td>"
	  } else {
	  print "<td class='yellow'/>"
	  }
	} else {
	  print "<td class='yellow'/>"
	}
      } else {
	println "<td class='red'/>"
      }
  }

  println "</tr>"
}
}
println "</table>"