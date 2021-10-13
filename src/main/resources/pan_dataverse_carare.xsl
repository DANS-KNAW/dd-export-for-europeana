<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
        xmlns="http://www.carare.eu/carareSchema"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:str="http://exslt.org/strings"
        exclude-result-prefixes="xs xsi str"
        version="2.0">

    <xsl:variable name="doi" select="substring-after(/dataset/datasetPersistentId, 'doi:')"/>
    <xsl:variable name="doi-url" select="concat('https://doi.org/', $doi)"/>

    <xsl:template match="/">
        <xsl:call-template name="metadata-root"/>
    </xsl:template>


    <!-- ==================================================== -->
    <!--                    Carare wrap                       -->
    <!-- ==================================================== -->
    <xsl:template name="metadata-root">
        <xsl:element name="carareWrap">
            <xsl:element name="carare">
                <xsl:attribute name="id">
                    <xsl:value-of select="$doi"/>
                </xsl:attribute>

                <!-- collectionInformation -->
                <xsl:call-template name="collectionInformation"/>

                <!-- heritageAssetIdentification -->
                <xsl:apply-templates select="dataset"/>

                <!-- digitalResource -->
                <xsl:apply-templates select="dataset/files/file"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--            Carare collectionInformation              -->
    <!-- ==================================================== -->
    <xsl:template name="collectionInformation">
        <xsl:element name="collectionInformation">

            <title preferred="true" lang="en">Portable Antiquities of The Netherlands (DANS-PAN)</title>
            <source>DANS-KNAW</source>
            <contacts>
                <name>Drs. Hella Hollander</name>
                <role lang="en">data archivist archaeology</role>
                <organization>Data Archiving and Networked Services (DANS)</organization>
                <address>
                    <numberInRoad>51</numberInRoad>
                    <roadName>Anna van Saksenlaan</roadName>
                    <townOrCity>The Hague</townOrCity>
                    <postcodeOrZipcode>2593 HW</postcodeOrZipcode>
                    <country>the Netherlands</country>
                </address>
                <phone>+31 70 3494450</phone>
                <email>hella.hollander@dans.knaw.nl</email>
                <email>info@dans.knaw.nl</email>
            </contacts>
            <rights>
                <reproductionRights lang="en">
                    This collection is published under the following licence: Creative Commons - Attribution, Non-Commercial, ShareAlike (BY-NC-SA)
                </reproductionRights>
                <licence>http://creativecommons.org/licenses/by-nc-sa/4.0/</licence>
            </rights>
            <language>en</language>
            <keywords lang="en">data archive; datasets; publications; archaeological research; Archaeology; the Netherlands</keywords>
            <coverage>
                <spatial>
                    <locationSet>
                        <!-- add (general area) or (undisclosed location) to tell Europeana not to show the location as a Point on a map -->
                        <geopoliticalArea lang="en">the Netherlands (general area)</geopoliticalArea>
                    </locationSet>
                </spatial>
            </coverage>

        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--          Carare heritageAssetIdentification          -->
    <!-- ==================================================== -->
    <xsl:template match="dataset">
        <xsl:element name="heritageAssetIdentification">

            <!-- recordInformation -->
            <xsl:call-template name="heritageRecordInformation"/>

            <!-- appellation -->
            <xsl:call-template name="appellation"/>

            <!-- description -->
            <xsl:apply-templates select="dsDescription"/>

            <!-- generalType -->
            <xsl:call-template name="generalType"/>

            <!-- actors -->
            <xsl:apply-templates select="author"/>

            <!-- characters -->
            <xsl:call-template name="characters"/>

            <!-- spatial -->
            <xsl:apply-templates select="dansSpatialCoverageText"/>

            <!-- publicationStatement -->
            <xsl:apply-templates select="distributor"/>

            <!-- rights -->
            <xsl:call-template name="rights"/>

            <!-- references -->
            <xsl:call-template name="references"/>

            <!-- hasRepresentation -->
            <xsl:call-template name="hasRepresentation"/>

        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--   heritageAssetIdentification / recordInformation    -->
    <!-- ==================================================== -->
    <xsl:template name="heritageRecordInformation">
        <xsl:element name="recordInformation">

            <!-- id -->
            <id>
                <xsl:value-of select="$doi"/>
            </id>

            <!-- creation -->
            <creation>
                <date>
                    <xsl:value-of select="dateOfDeposit"/>
                </date>
            </creation>

            <!-- language -->
            <xsl:apply-templates select="language"/>

        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                       language                       -->
    <!-- ==================================================== -->
    <xsl:template match="language">
        <xsl:element name="language">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                     appellation                      -->
    <!-- ==================================================== -->
    <xsl:template name="appellation">
        <xsl:variable name="title" select="title"/>
        <xsl:element name="appellation">
            <xsl:element name="name">
                <xsl:attribute name="lang">
                    <xsl:value-of select="'en'" />
                </xsl:attribute>
                <xsl:value-of select="$title"/>
            </xsl:element>
            <xsl:element name="id">
                <xsl:value-of select="$doi"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                     description                      -->
    <!-- ==================================================== -->
    <xsl:template match="dsDescription">
         <description lang="en">
            <xsl:value-of select="dsDescriptionValue" />
        </description>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                    generalType                       -->
    <!-- ==================================================== -->
    <xsl:template name="generalType">
        <generalType>
            <xsl:value-of select="'Artefact'" />
        </generalType>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                      actors                          -->
    <!-- ==================================================== -->
    <xsl:template match="author">
        <xsl:element name="actors">
            <!-- name -->
            <xsl:for-each select="./authorName">
                <xsl:element name="name">
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:for-each>

            <!-- actorType -->
            <xsl:if test="./authorAffiliation">
                <xsl:element name="actorType">
                     <xsl:value-of select="'organization'"/>
                </xsl:element>
            </xsl:if>
            <xsl:if test="not(./authorAffiliation)">
                <xsl:element name="actorType">
                    <xsl:value-of select="'individual'"/>
                </xsl:element>
            </xsl:if>

            <!-- roles -->
            <xsl:for-each select="/dataset/contributor">
                <xsl:element name="roles">
                    <xsl:value-of select="contributorType"/>
                </xsl:element>
            </xsl:for-each>

        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                     characters                       -->
    <!-- ==================================================== -->
    <xsl:template name="characters">
        <xsl:element name="characters">

            <!-- heritageAssetType -->
            <xsl:apply-templates select="dansAbrComplex"/>

            <!-- temporal -->
            <xsl:apply-templates select="dansAbrPeriod"/>

            <!-- materials -->
            <xsl:apply-templates select="keyword"/>

        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                 heritageAssetType                    -->
    <!-- ==================================================== -->
    <xsl:template match="dansAbrComplex">
        <xsl:element name="heritageAssetType">
<!--            <xsl:attribute name="namespace">-->
<!--                <xsl:value-of select="." />-->
<!--            </xsl:attribute>-->
            <xsl:attribute name="termUID">
                <xsl:value-of select="." />
            </xsl:attribute>
<!--            <xsl:attribute name="term">-->
<!--                <xsl:value-of select="." />-->
<!--            </xsl:attribute>-->
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                     temporal                         -->
    <!-- ==================================================== -->
    <xsl:template match="dansAbrPeriod">
        <xsl:element name="temporal">
            <xsl:element name="displayDate">
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                    materials                         -->
    <!-- ==================================================== -->
    <xsl:template match="keyword">
        <xsl:element name="materials">
            <xsl:value-of select="keywordValue"/>
        </xsl:element>
    </xsl:template>

    <!-- ======================================================== -->
    <!--                        spatial                           -->
    <!-- add (general area) or (undisclosed location) to tell     -->
    <!-- Europeana not to show the location as a Point on a map   -->
    <!-- ======================================================== -->
    <xsl:template match="dansSpatialCoverageText">
        <xsl:element name="spatial">
            <xsl:element name="locationSet">
                <xsl:element name="namedLocation">
                    <xsl:value-of select="concat(., ' (undisclosed location)')"/>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                 publicationStatement                 -->
    <!-- ==================================================== -->
    <xsl:template match="distributor">
        <xsl:element name="publicationStatement">
            <xsl:element name="publisher">
                <xsl:value-of select="distributorName"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                        rights                        -->
    <!-- ==================================================== -->
    <xsl:template name="rights">
        <xsl:element name="rights">

            <xsl:if test="dansRightsHolder">
                <copyrightCreditLine>
                    <xsl:value-of select="dansRightsHolder"/>
                </copyrightCreditLine>
            </xsl:if>

            <accessRights>
                <xsl:variable name="restricted" select="/dataset/files/file/restricted[. = 'true'][1]"/>
                <xsl:choose>
                    <xsl:when test="$restricted">
                        <xsl:value-of select="'Restricted Access'"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="'Open Access'"/>
                    </xsl:otherwise>
                </xsl:choose>
            </accessRights>

            <xsl:variable name="uri" select="license/uri"/>

            <licence>
                <xsl:value-of select="$uri"/>
            </licence>

            <europeanaRights>
                <xsl:choose>
                    <xsl:when test="contains($uri, 'publicdomain/zero/1.0')">
                        <xsl:value-of select="'The Creative Commons CC0 1.0 Universal Public Domain Dedication (CC0)'"/>
                    </xsl:when>
                    <xsl:when test="contains($uri, 'licenses/by/4.0')">
                        <xsl:value-of select="'Creative Commons - Attribution (BY)'"/>
                    </xsl:when>
                    <xsl:when test="contains($uri, 'licenses/by-sa/4.0')">
                        <xsl:value-of select="'Creative Commons - Attribution, ShareAlike (BY-SA)'"/>
                    </xsl:when>
                    <xsl:when test="contains($uri, 'licenses/by-nc/4.0')">
                        <xsl:value-of select="'Creative Commons - Attribution, Non-Commercial (BY-NC)'"/>
                    </xsl:when>
                    <xsl:when test="contains($uri, 'licenses/by-nc/3.0')">
                        <xsl:value-of select="'Creative Commons - Attribution, Non-Commercial (BY-NC)'"/>
                    </xsl:when>
                    <xsl:when test="contains($uri, 'licenses/by-nd/4.0')">
                        <xsl:value-of select="'Creative Commons - Attribution, No Derivatives (BY-ND)'"/>
                    </xsl:when>
                    <xsl:when test="contains($uri, 'licenses/by-nc-nd/4.0')">
                        <xsl:value-of select="'Creative Commons - Attribution, Non-Commercial, No Derivatives (BY-NC-ND)'"/>
                    </xsl:when>
                    <xsl:when test="contains($uri, 'licenses/by-nc-sa/4.0')">
                        <xsl:value-of select="'Creative Commons - Attribution, Non-Commercial, ShareAlike (BY-NC-SA)'"/>
                    </xsl:when>
                    <xsl:when test="contains($uri, 'licenses/by-nc-sa/3.0')">
                        <xsl:value-of select="'Creative Commons - Attribution, Non-Commercial, ShareAlike (BY-NC-SA)'"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="'Copyright Not Evaluated (CNE)'"/>
                    </xsl:otherwise>
                </xsl:choose>
            </europeanaRights>

        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                      references                      -->
    <!-- ==================================================== -->
    <xsl:template name="references">
        <xsl:element name="references">
            <xsl:element name="appellation">
                <xsl:element name="name">
                    <xsl:attribute name="lang">
                        <xsl:value-of select="'en'" />
                    </xsl:attribute>
                    <xsl:value-of select="alternativeTitle"/>
                </xsl:element>
                <xsl:element name="id">
                    <xsl:value-of select="alternativeURL"/>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                  hasRepresentation                   -->
    <!-- ==================================================== -->
    <xsl:template name="hasRepresentation">
        <xsl:variable name="imageFilepath" select="files/file/directoryLabel[. = 'data/images'][1]"/>
        <xsl:if test="$imageFilepath">
            <xsl:variable name="fileName" select="$imageFilepath/../filename"/>
            <xsl:element name="hasRepresentation">
                <xsl:value-of select="concat($doi, substring($imageFilepath, 5) , '/', $fileName)"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <!-- ==================================================== -->
    <!--               Carare digitalResource                 -->
    <!-- ==================================================== -->
    <xsl:template match="dataset/files/file">

        <xsl:if test="not(file/restricted)">

            <xsl:element name="digitalResource">

                <xsl:variable name="fileName" select="filename"/>

                <!-- recordInformation -->
                <recordInformation>
                    <id><xsl:value-of select="concat($doi, '/', $fileName)"/></id>
                </recordInformation>

                <!-- appellation -->
                <appellation>
                    <name lang="en"><xsl:value-of select="$fileName"/></name>
                    <id><xsl:value-of select="$fileName"/></id>
                </appellation>

                <!-- description -->
                <description lang="en">
                    <xsl:choose>
                        <xsl:when test="contains($fileName, 'thesaurus-nl')">
                            <xsl:value-of select="'Detailed information about the classification of this object in xml format, in Dutch'"/>
                        </xsl:when>
                        <xsl:when test="contains($fileName, 'thesaurus-en')">
                            <xsl:value-of select="'Detailed information about the classification of this object in xml format, in English'"/>
                        </xsl:when>
                        <xsl:when test="contains($fileName, 'object')">
                            <xsl:value-of select="'Technical description of the object in xml format'"/>
                        </xsl:when>
                        <xsl:when test="contains(./directoryLabel, 'images')">
                            <xsl:value-of select="'Photo of the object'"/>
                        </xsl:when>
                    </xsl:choose>
                </description>

                <!-- format -->
                <format>
                    <xsl:value-of select="contentType"/>
                </format>

                <!-- link -->
                <link>
                </link>

                <!-- object -->
                <object>
                </object>

                <!-- isShownAt -->
                <isShownAt>
                    <xsl:value-of select="$doi-url"/>
                </isShownAt>

                <!-- rights -->
                <rights>
                    <accessRights>
                        <xsl:value-of select="'Open Access'"/>
                    </accessRights>
                    <licence>
                        <xsl:value-of select="/dataset/license/uri"/>
                    </licence>
                </rights>

            </xsl:element>

        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
