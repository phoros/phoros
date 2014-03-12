package edu.holycross.shot.atl

import static org.junit.Assert.*
import org.junit.Test


class TestVerifier extends GroovyTestCase {





  def oneToOnes = [
    ["indices/stoneToImage.csv", "collections/stones.csv"]
  ]


  // domain relations
  def domSubsets = [
    ["indices/tbsToDefaultImage.csv","collections/tbs.csv"]
  ]

  def domSupersets = [
    ["indices/tbsToImage.csv","collections/tbs.csv"]
  ]


  // range relations
  

  
  void testOneToOnes() {
    oneToOnes.each { pair ->
      Verifier v = new Verifier(pair[0])
      v.domainCollectionFile = new File(pair[1])
      v.relation = Verifier.RelationType.ONE_TO_ONE
      v.verify(Verifier.IndexSide.DOMAIN)
    }
  }

  void testSubsets() {
    domSubsets.each { pair ->
      Verifier v = new Verifier(pair[0])
      v.domainCollectionFile = new File(pair[1])
      v.relation = Verifier.RelationType.SUBSET
      v.verify(Verifier.IndexSide.DOMAIN)
    }

  }


  void testSupersets() {
    domSupersets.each { pair ->
      Verifier v = new Verifier(pair[0])
      v.domainCollectionFile = new File(pair[1])
      v.relation = Verifier.RelationType.SUPERSET
      v.verify(Verifier.IndexSide.DOMAIN)
    }
  }


}
