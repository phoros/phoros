package edu.holycross.shot.atl

import static org.junit.Assert.*
import org.junit.Test


class TestQuotaTriples extends GroovyTestCase {


    void testReadAllTriples() {
        QuotaList ql = new QuotaList()
        ql.readAllYears()
        if (ql.quotaRecords) {
            System.err.println "Total records: " + ql.quotaRecords.size()
        } else {
            System.err.println "quotaRecords is null"
        }
    }
}
