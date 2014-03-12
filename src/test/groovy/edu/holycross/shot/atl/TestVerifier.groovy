package edu.holycross.shot.atl

import static org.junit.Assert.*
import org.junit.Test


class TestVerifier extends GroovyTestCase {





  def domOneToOnes = [
    ["indices/stoneToImage.csv", "collections/stones.csv"],
    ["indices/pointToPlace.csv","collections/lls.csv"],
    ["indices/paymentToTbs.csv","collections/phoros.csv"]
  ]
  





  // domain relations
  def domCompletes = [
    ["indices/tbsToDefaultImage.csv","collections/tbs.csv"]
  ]

  def domSupersets = [
    ["indices/tbsToImage.csv","collections/tbs.csv"]
  ]


  // range relations
  def rgeCompletes = [
    ["indices/pointToPlace.csv","collections/lls.csv"],
    ["indices/paymentToTbs.csv","collections/phoros.csv"]  
  ]  
  def rgeSupersets = []  

  
  void testOneToOnes() {
    domOneToOnes.each { pair ->
      Verifier v = new Verifier(pair[0])
      v.domainCollectionFile = new File(pair[1])
      v.relation = Verifier.RelationType.ONE_TO_ONE
      v.verify(Verifier.IndexSide.DOMAIN)
    }
  }

  void testCompletes() {
    domCompletes.each { pair ->
      Verifier v = new Verifier(pair[0])
      v.domainCollectionFile = new File(pair[1])
      v.relation = Verifier.RelationType.COMPLETE
      v.verify(Verifier.IndexSide.DOMAIN)
    }

    rgeCompletes.each { pair ->
      Verifier v = new Verifier(pair[0])
      v.rangeCollectionFile = new File(pair[1])
      v.relation = Verifier.RelationType.COMPLETE
      v.verify(Verifier.IndexSide.RANGE)
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
