package edu.holycross.shot.atl

import static org.junit.Assert.*
import org.junit.Test


class TestVerifier extends GroovyTestCase {


    void testOneToOne() {
      Verifier v = new Verifier("indices/stoneToImage.csv")
      v.domainCollectionFile = new File("collections/stones.csv")
      v.relation = Verifier.RelationType.ONE_TO_ONE
      v.verifyDomain()



    }


    void testSubset() {
      Verifier v = new Verifier("indices/tbsToDefaultImage.csv")
      v.domainCollectionFile = new File("collections/tbs.csv")
      v.relation = Verifier.RelationType.SUBSET
      v.verifyDomain()
    }
}
