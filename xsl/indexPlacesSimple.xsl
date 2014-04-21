<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    exclude-result-prefixes="xs"
    version="2.0">
    <xsl:output method="text"></xsl:output>
    <xsl:template match="/">
        <xsl:apply-templates select="//tei:placeName"></xsl:apply-templates>
    </xsl:template>
    
    
    <xsl:template match="tei:ab" mode="place">
        <xsl:value-of select="ancestor::tei:div[@type = 'face']/@n"/>
        <xsl:text>.</xsl:text>
        <xsl:value-of  select="parent::tei:div/@n"></xsl:value-of>
        <xsl:text>.</xsl:text>
       
            <xsl:value-of select="./@n"></xsl:value-of>
    </xsl:template>
    
    
    <xsl:template match="tei:placeName">
        
        <xsl:apply-templates select="ancestor::tei:ab" mode="place"></xsl:apply-templates>
        <xsl:text>=</xsl:text>
        <xsl:value-of select="./@n"></xsl:value-of>
        <xsl:text>
</xsl:text>
    </xsl:template>
</xsl:stylesheet>