package edu.holycross.shot.atl

import edu.harvard.chs.cite.CiteUrn


/**  Class representing the Athenian Tribute Quota Lists.
*/
class QuotaList {

    groovy.xml.Namespace tei = new groovy.xml.Namespace("http://www.tei-c.org/ns/1.0")

    def fileCtsDocReff = [
        ["texts/editions/stele1-year1.xml",
            "urn:cts:phoros:stele1.year1.hc",
            "urn:cite:phoros:documents.stele1_year1"
        ],
        ["texts/editions/stele1-year2.xml",
            "urn:cts:phoros:stele1.year2.hc",
            "urn:cite:phoros:documents.stele1_year2"
        ],
        ["texts/editions/stele1-year3.xml",
            "urn:cts:phoros:stele1.year3.hc",
            "urn:cite:phoros:documents.stele1_year3"
        ],
        ["texts/editions/stele1-year4.xml",
            "urn:cts:phoros:stele1.year4.hc",
            "urn:cite:phoros:documents.stele1_year4"
        ],
        ["texts/editions/stele1-year5.xml",
            "urn:cts:phoros:stele1.year5.hc",
            "urn:cite:phoros:documents.stele1_year5"
        ]
    ]


    def quotaRecords = []

    CiteUrn currentDocument

    QuotaList() {
    }

    QuotaList(String yearString) 
    throws Exception {
        try {
            CiteUrn urn = new CiteUrn(yearString)
            this.currentDocument = urn
        } catch (Exception e) {
            System.err.println "QuotaList: unable to make URN from ${yearString}"
            throw e
        }
    }

    QuotaList(CiteUrn yearUrn) {
        this.document = yearUrn
    }



    String recordsToCsv() {
        StringBuffer buff = new StringBuffer("Year,Source,Place,Obols")
        this.quotaRecords.each { record ->
            buff.append("${record['document']},")
            buff.append("${record['textUrn']},")
            buff.append("${record['place']},")
            buff.append("${record['obols']}\n")
        }
        return buff.toString()
    }


    // cycle through fileCtsDocReff structure and
    // put read years into quotaRecords structure
    void readAllYears() {
        fileCtsDocReff.each { triple ->
            currentDocument = new CiteUrn(triple[2])
            def yearRecords = readYear(new File(triple[0]), triple[1])
            System.err.println "year ${triple[0]}: ${yearRecords.size()} records"
            yearRecords.each { r ->
                quotaRecords.add(r)
            }
        }
    }


    /** Reads TEI XML of one year's diplomatic edition, and
    * returns an array of record objects.
    */
    ArrayList readYear (File f, String urnBase)  {
        //System.err.println "Year,Source,Place,Obols"
        def records = []
        def root = new XmlParser().parse(f)

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
                        def record = [:]
                        record["document"] = currentDocument
                        record["textUrn"] = "${urnBase}:${top}.${sect}.${lineNum}"
                        record["place"] = place.'@n'
                        record["obols"] = amount
                        records.add(record)
                    }
                }
            }
        }
        return records
    }


}
