<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output indent="yes"/>
  <xsl:template match="/files">
    <classpath>
      <classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER"/>
      <classpathentry kind="src" path="src/java/main"/>
      <classpathentry kind="src" path="src/conf/main"/>
      <classpathentry kind="src" path="src/java/test/unit" output="target/classes/test/unit"/>
      <classpathentry kind="src" path="src/java/test/integration" output="target/classes/test/integration"/>
      <classpathentry kind="src" path="src/conf/test/unit" output="target/classes/test/unit"/>
      <classpathentry kind="src" path="src/conf/test/integration" output="target/classes/test/integration"/>
      <classpathentry kind="output" path="target/classes/main"/>
      <xsl:apply-templates select="/files/file">
        <xsl:sort select="@path"/>
      </xsl:apply-templates>
    </classpath>
  </xsl:template>
  <xsl:template match="file">
    <xsl:element name="classpathentry">
      <xsl:attribute name="kind">var</xsl:attribute>
      <xsl:attribute name="path">SAVANT_REPOSITORY/<xsl:value-of select="@path"/></xsl:attribute>
      <xsl:attribute name="sourcepath">SAVANT_REPOSITORY/<xsl:value-of select="@src"/></xsl:attribute>
    </xsl:element>
  </xsl:template>
</xsl:stylesheet>
