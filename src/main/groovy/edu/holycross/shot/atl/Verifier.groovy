package edu.holycross.shot.atl

import edu.harvard.chs.cite.CiteUrn


/**
*/
class Verifier {


  enum RelationType {
    ONE_TO_ONE, SUPERSET, COMPLETE
  }

  enum IndexSide {
    DOMAIN, RANGE
  }


  // Already configured in index inventory:
  File indexFile

  // These could be configured in index inventory:
  File domainCollectionFile = null
  File rangeCollectionFile = null
  RelationType relation = null
  
  

  Verifier(String indexFileName) 
  throws Exception {
    try {
      indexFile = new File(indexFileName)
    } catch (Exception e) {
      throw e
    }
  }


  // should not really assume that IDs are in col[0] ...
  // nor should assume .csv ....
  def getCollectionValues(File f) {
    def vals = []
    f.eachLine { l ->
      def cols = l.split(/,/)
      vals.add(cols[0])
    }
    return vals
  }


  // do no assume csv ...
  def getIndexDomain() {
    def vals = []
    domainCollectionFile.eachLine { l ->
      def cols = l.split(/,/)
      vals.add(cols[0])
    }
    return vals
  }

  def getIndexRange() {
    def vals = []
    rangeCollectionFile.eachLine { l ->
      def cols = l.split(/,/)
      vals.add(cols[0])
    }
    return vals
  }


  // all values must be initialized before calling this...
  void verify(IndexSide side) {
    if (relation == null) {
      throw new Exception("Verifier.verifyDomain: relation must be configured.")
    }

    def collectionValues
    def indexValues
    if (side == IndexSide.DOMAIN) {
      if (domainCollectionFile == null)  {
	throw new Exception("Verifier.verifyDomain: domainCollectionFile must be configured.")
      }

      collectionValues = getCollectionValues(domainCollectionFile)
      indexValues = getIndexDomain()
      
      
    } else if (side == IndexSide.RANGE) {
      if (rangeCollectionFile == null)  {
	throw new Exception("Verifier.verifyDomain: rangeCollectionFile must be configured.")
      }

      collectionValues = getCollectionValues(rangeCollectionFile)
      indexValues = getIndexRange()
    } 


    switch (relation) {

    case RelationType.ONE_TO_ONE:
    assert collectionValues as Set == indexValues as Set
    break


    // Index domain is a subset of Collection:
    case RelationType.COMPLETE:
    indexValues.each { idx ->
      assert collectionValues.contains(idx)
    }
    break


    // Index domain is a superset of Collection:
    case RelationType.SUPERSET:
    collectionValues.each { urn ->
      assert indexValues.contains(urn)
    }
    break


    default:
    System.err.println "UNIMPLEMENTED RELATION TYPE: " + relation
    break
    }
  }



}
