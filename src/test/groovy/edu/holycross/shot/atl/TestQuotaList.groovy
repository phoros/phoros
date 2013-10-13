package edu.holycross.shot.atl

import static org.junit.Assert.*
import org.junit.Test


class TestQuotaList extends GroovyTestCase {

    File yr1File = new File("texts/editions/stele1-year1.xml")
    String yr1CtsUrn = "urn:cts:phoros:stele1.year1.hc"
    String yr1DocumentObject = "urn:cite:phoros:documents.stele1_year1"
    void testReadYear() {
        QuotaList ql = new QuotaList(yr1DocumentObject)
        ql.readYear(yr1File, yr1CtsUrn)
    }
}
