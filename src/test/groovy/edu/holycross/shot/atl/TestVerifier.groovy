package edu.holycross.shot.atl

import static org.junit.Assert.*
import org.junit.Test


class TestVerifier extends GroovyTestCase {



  def oneToOnes = [
    ["indices/stoneToImage.csv", "collections/stones.csv"]
  ]

  def subsets = [
    ["indices/tbsToDefaultImage.csv","collections/tbs.csv"]
  ]

  def supersets = [
    ["indices/tbsToImage.csv","collections/tbs.csv"]
  ]

  
  void testOneToOnes() {
    oneToOnes.each { pair ->
      Verifier v = new Verifier(pair[0])
      v.domainCollectionFile = new File(pair[1])
      v.relation = Verifier.RelationType.ONE_TO_ONE
      v.verifyDomain()
    }
  }

  void testSubsets() {
    subsets.each { pair ->
      Verifier v = new Verifier(pair[0])
      v.domainCollectionFile = new File(pair[1])
      v.relation = Verifier.RelationType.SUBSET
      v.verifyDomain()
    }
  }


  void testSupersets() {
    supersets.each { pair ->
      Verifier v = new Verifier(pair[0])
      v.domainCollectionFile = new File(pair[1])
      v.relation = Verifier.RelationType.SUPERSET
      v.verifyDomain()
    }
  }


}
