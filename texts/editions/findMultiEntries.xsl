<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    exclude-result-prefixes="xs"
    version="2.0">
    <xsl:output method="text"></xsl:output>
    <xsl:template match="/">
        <xsl:apply-templates select="//tei:ab" mode="overview"></xsl:apply-templates>
    </xsl:template>
    
    
    <xsl:template match="tei:ab" mode="overview">
    <xsl:choose>
        <xsl:when test="count(tei:placeName) > 1">
            <xsl:value-of select="ancestor::tei:div[@type = 'face']/@n"/>
            <xsl:text>.</xsl:text>
            <xsl:value-of  select="parent::tei:div/@n"></xsl:value-of>
            <xsl:text>.</xsl:text>
            
            <xsl:value-of select="./@n"></xsl:value-of>
        <xsl:text>: multiple place names.  </xsl:text>
            <xsl:apply-templates select="tei:placeName" mode="cite"></xsl:apply-templates>
       
        <xsl:text>
</xsl:text>
        </xsl:when>
    </xsl:choose>
    </xsl:template>
    
    <xsl:template match="tei:placeName" mode="cite">
    <xsl:value-of select="./@n"></xsl:value-of>
    </xsl:template>
    
    
    <xsl:template match="tei:ab" mode="place">
        <xsl:value-of select="ancestor::tei:div[@type = 'face']/@n"/>
        <xsl:text>.</xsl:text>
        <xsl:value-of  select="parent::tei:div/@n"></xsl:value-of>
        <xsl:text>.</xsl:text>
       
            <xsl:value-of select="./@n"></xsl:value-of>
    </xsl:template>
    
    
    <xsl:template match="tei:ab" mode="amount">
        <xsl:text>=</xsl:text>
        
        <xsl:apply-templates select="descendant::tei:measure"/>
    </xsl:template>
    
    <xsl:template match="tei:measure">
        <xsl:apply-templates select="tei:num"></xsl:apply-templates>
   
    </xsl:template>
   
   
   <xsl:template match="tei:num">
       <xsl:value-of select="./@value"></xsl:value-of>
   </xsl:template>
    
    
    <xsl:template match="tei:placeName">
                <xsl:if test="./@n">
        <xsl:apply-templates select="ancestor::tei:ab" mode="place"></xsl:apply-templates>
        <xsl:text>=</xsl:text>
                    <xsl:choose>
                        <xsl:when  test="./@ana">
                            <xsl:value-of select="./@ana"></xsl:value-of>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="./@n"></xsl:value-of>
                        </xsl:otherwise>
                    </xsl:choose>
                    
        
                    <xsl:text>=</xsl:text>  
                    <xsl:variable name="raw" select="."></xsl:variable>
                    <xsl:variable name="tidy" select="translate($raw,'&#x0A;',' ')"></xsl:variable>
                    <xsl:value-of select="$tidy"/>


                    <xsl:apply-templates select="ancestor::tei:ab" mode="amount"></xsl:apply-templates>
                    
        <xsl:text>
</xsl:text>
                    
               </xsl:if>
    </xsl:template>
</xsl:stylesheet>