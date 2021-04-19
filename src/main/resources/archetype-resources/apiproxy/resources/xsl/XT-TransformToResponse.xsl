<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:soap="http://www.w3.org/2003/05/soap-envelope" 
                xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                xmlns:v1="http://www.claro.com.br/EBS/Claro/v1" 
                xmlns:v11="http://www.claro.com.br/EBO/Claro/v1"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    
   <xsl:output method="xml" omit-xml-declaration="no" encoding="utf-8" indent="yes" />

   <xsl:param name="apiVersion" />
   <xsl:param name="transactionId" />

   <xsl:template match="/">
        <xml>
            <apiVersion><xsl:value-of select="$apiVersion"/></apiVersion>
            <transactionId><xsl:value-of select="$transactionId"/></transactionId>
            <data>
                
            </data>
        </xml>
    </xsl:template>
</xsl:stylesheet>