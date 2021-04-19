<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" encoding="utf-8" omit-xml-declaration="yes" indent="yes"/>

    <xsl:param name="appCodeOrigin" />
    <xsl:param name="transactionId" />
    <xsl:param name="remoteAddr" />

    <xsl:template match="/">
        
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" >
    <soapenv:Header/>
	<soapenv:Body>
        <v1:Request xmlns:v1="http://www.claro.com.br/EBS/Claro/v1" xmlns:v11="http://www.claro.com.br/EBO/Claro/v1" >
            <v1:avaliacao-credito>
            </v1:avaliacao-credito>
            <v1:meta-informacao>
                <v11:codigo-aplicacao-origem><xsl:value-of select="$appCodeOrigin"/>-[APIGEE]</v11:codigo-aplicacao-origem>
                <v11:codigo-operacao-origem>${verbo} ${path}</v11:codigo-operacao-origem>
                <v11:correlacao-id-origem><xsl:value-of select="$transactionId"/></v11:correlacao-id-origem>
                <v11:endereco-aplicacao-origem><xsl:value-of select="$remoteAddr"/></v11:endereco-aplicacao-origem>
                <v11:usuario-id-origem><xsl:value-of select="$appCodeOrigin"/>-[APIGEE]</v11:usuario-id-origem>
                <v11:data-hora-origem><xsl:value-of select="current-dateTime()"/></v11:data-hora-origem>
                <v11:canal-id-origem><xsl:value-of select="$appCodeOrigin"/>-[APIGEE]</v11:canal-id-origem>
            </v1:meta-informacao>
        </v1:Request>
	</soapenv:Body>
</soapenv:Envelope>

    </xsl:template>
</xsl:stylesheet>