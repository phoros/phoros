package edu.holycross.shot.atl

import edu.harvard.chs.cite.CiteUrn


/**  Class representing the Athenian Tribute Quota Lists.
*/
class QuotaList {

    groovy.xml.Namespace tei = new groovy.xml.Namespace("http://www.tei-c.org/ns/1.0")

    CiteUrn document

    QuotaList(String yearString) 
    throws Exception {
        try {
            CiteUrn urn = new CiteUrn(yearString)
            this.document = urn
        } catch (Exception e) {
            System.err.println "QuotaList: unable to make URN from ${yearString}"
            throw e
        }
    }

    QuotaList(CiteUrn yearUrn) {
        this.document = yearUrn
    }

    /** Analyzes records for a single year's diplomatic edition.
    */
    void readYear (File f, String urnBase)  {
        def records = []
        def root = new XmlParser().parse(f)

        System.err.println "Year,Source,Place,Obols"
        // physical lines:
        root[tei.text][tei.body][tei.div].each { d1 ->
            String top = d1.'@n'
            d1[tei.div].each { d2 ->
                String sect = d2.'@n'
                d2[tei.ab].each { ln ->
                    String lineNum = ln.'@n'
                    if ((ln[tei.placeName].size() > 0) && (ln[tei.measure].size() > 0)) {
                        def place = ln[tei.placeName][0]
                        def amount = ""
                        ln[tei.measure][tei.num].each {
                            amount = it.'@value'
                        }
                        System.err.println "${document},${urnBase}:${top}.${sect}.${lineNum},${place.'@n'},${amount}"
                    }
                }
            }
        }
    }
}
