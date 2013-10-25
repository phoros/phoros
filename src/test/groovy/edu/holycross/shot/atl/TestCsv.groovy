package edu.holycross.shot.atl

import static org.junit.Assert.*
import org.junit.Test


class TestCsv extends GroovyTestCase {


    void testReadAllTriples() {
        QuotaList ql = new QuotaList()
        ql.readAllYears()
        System.err.println ql.recordsToCsv()
    }
}
