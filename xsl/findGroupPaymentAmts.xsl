<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    exclude-result-prefixes="xs"
    version="2.0"
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output
        method="text"/>
    <xsl:template
        match="/">
        <xsl:apply-templates
            select="//tei:measure[@ana]"/>
    </xsl:template>
    
    
    <xsl:template
        match="tei:ab"
        mode="overview">
        <xsl:choose>
            <xsl:when
                test="count(tei:placeName) > 1">
                <xsl:variable
                    name="ref">
                    <xsl:value-of
                        select="ancestor::tei:div[@type = 'face']/@n"/>
                    <xsl:text>.</xsl:text>
                    <xsl:value-of
                        select="parent::tei:div/@n"/>
                    <xsl:text>.</xsl:text>
                    <xsl:value-of
                        select="./@n"/>
                </xsl:variable>
                
                <xsl:value-of select="$ref"></xsl:value-of>
                <xsl:apply-templates
                    mode="cite"
                    select="tei:placeName"/>
                <xsl:text>
</xsl:text>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    
    <xsl:template
        match="tei:measure"
       >
        <xsl:value-of
            select="./@ana"/>
        <xsl:text>
</xsl:text>
    </xsl:template>
    
    
</xsl:stylesheet>
