<xsl:stylesheet xmlns="http://www.carare.eu/carareSchema" xmlns:bagmetadata="http://easy.dans.knaw.nl/schemas/bag/metadata/bagmetadata/" xmlns:ddm="http://easy.dans.knaw.nl/schemas/md/ddm/" xmlns:files="http://easy.dans.knaw.nl/schemas/bag/metadata/files/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:dcx-dai="http://easy.dans.knaw.nl/schemas/dcx/dai/" xmlns:gml="http://www.opengis.net/gml" xmlns:dcx-gml="http://easy.dans.knaw.nl/schemas/dcx/gml/" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:str="http://exslt.org/strings" exclude-result-prefixes="not xs xsi dc dcterms dcx-dai gml dcx-gml fn str bagmetadata ddm files" version="2.0">
    <xsl:variable name="doi" select="substring-after(/dataset/datasetPersistentId, 'doi:')"/>
    <xsl:variable name="doi-url" select="concat('https://doi.org/', $doi)"/>
    <xsl:variable name="maxFileSize">
        <xsl:for-each select="/dataset/files/file[contains(restricted, 'false') and not(contains(directoryLabel, 'easy-migration'))]">
            <xsl:sort select="filesize"  data-type="number" order="descending"/>
            <xsl:if test="position() = 1">
                <xsl:value-of select="filesize"/>
            </xsl:if>
        </xsl:for-each>
    </xsl:variable>

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
                <!--   collectionInformation   -->
                <xsl:call-template name="collectionInformation"/>
                <!--   heritageAssetIdentification   -->
                <xsl:apply-templates select="dataset"/>
                <!--   digitalResource   -->
                <xsl:apply-templates select="dataset/files/file[contains(restricted, 'false') and not(contains(directoryLabel, 'easy-migration')) and filesize = $maxFileSize]"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--            Carare collectionInformation              -->
    <!-- ==================================================== -->
    <xsl:template name="collectionInformation">
        <xsl:element name="collectionInformation">
            <title preferred="true" lang="nl">Archeologische datasets in EASY (DANS-EASY)</title>
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
                    This collection is published under the following Creative Commons licences: CC0, CC-BY, CC-BY-ND
                </reproductionRights>
                <licence>http://creativecommons.org/licenses/</licence>
            </rights>
            <language>en</language>
            <keywords lang="en">
                data archive; datasets; publications; archaeological research; Archaeology; the Netherlands
            </keywords>
            <coverage>
                <spatial>
                    <locationSet>
                        <!--

                         add (general area) or (undisclosed location) to tell Europeana not to show the location as a Point on a map
                        -->
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
            <!--   recordInformation   -->
            <xsl:call-template name="heritageRecordInformation"/>
            <!--   appellation   -->
            <xsl:call-template name="appellation"/>
            <!--   description   -->
            <xsl:apply-templates select="dsDescription"/>
            <!--   generalType   -->
            <xsl:call-template name="generalType"/>
            <!--   actors   -->
            <xsl:call-template name="authors"/>
            <!--   characters   -->
            <xsl:call-template name="characters"/>
            <!--   spatial   -->
            <xsl:apply-templates select="dansSpatialCoverageText"/>
            <!--   publicationStatement   -->
            <xsl:apply-templates select="distributor"/>
            <!--   rights   -->
            <xsl:call-template name="rights"/>
            <!--   references   -->
            <xsl:apply-templates select="dansRelation"/>
            <!--   hasRepresentation   -->
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
                    <xsl:value-of select="productionDate"/>
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
        <xsl:for-each select="dsDescriptionValue">
            <description lang="en">
                <xsl:value-of select="." />
            </description>
        </xsl:for-each>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                    generalType                       -->
    <!-- ==================================================== -->
    <!--

     This is a broad classification of the general type of heritage asset and is intended to enable monuments,
            buildings and landscape areas to be distinguished from artefacts, text documents (printed materials, books, articles, etc),
            images (photographs, drawings etc), audio recordings, movies reference and 3D models.
            A fixed controlled vocabulary is proposed for use in which the term “Monument” includes archaeological monuments,
            historic buildings, industrial monuments, archaeological landscape areas and shipwrecks.
            The proposed vocabulary is as follows:
                                        - Monument
                                        - Artefact
                                        - Text
                                        - Image
                                        - Sound
                                        - Movie
                                        - 3D
                                    </xs:documentation>
    -->
    <xsl:template name="generalType">
        <generalType>
            <xsl:value-of select="'Text'"/>
        </generalType>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                      actors                          -->
    <!-- ==================================================== -->
    <xsl:template name="authors">
        <xsl:element name="actors">
            <!-- name -->
            <xsl:for-each select="./author/authorName">
                <xsl:element name="name">
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:for-each>

            <!-- actorType -->
            <xsl:if test="./author/authorAffiliation">
                <xsl:element name="actorType">
                    <xsl:value-of select="'individual'"/>
                </xsl:element>
            </xsl:if>

            <!-- roles -->
            <xsl:for-each select="/dataset/contributor/contributorType">
                <xsl:element name="roles">
                    <xsl:value-of select="."/>
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

        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                 heritageAssetType                    -->
    <!-- ==================================================== -->
    <xsl:template match="dansAbrComplex">
        <xsl:element name="heritageAssetType">
            <xsl:attribute name="namespace">
                <xsl:value-of select="dansAbrComplexScheme" />
            </xsl:attribute>
            <xsl:attribute name="termUID">
                <xsl:value-of select="dansAbrComplexTermUID" />
            </xsl:attribute>
            <xsl:attribute name="term">
                <xsl:value-of select="dansAbrComplexTerm" />
            </xsl:attribute>
            <xsl:value-of select="dansAbrComplexTerm"/>
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

    <!-- ======================================================== -->
    <!--                        spatial                           -->
    <!-- add (general area) or (undisclosed location) to tell     -->
    <!-- Europeana not to show the location as a Point on a map   -->
    <!-- ======================================================== -->
    <xsl:template match="dansSpatialCoverageText">
        <xsl:element name="spatial">
            <xsl:element name="locationSet">
                <xsl:element name="namedLocation">
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                 publicationStatement                 -->
    <!-- ==================================================== -->
    <xsl:template match="distributor">
        <xsl:for-each select="distributorName">
            <xsl:element name="publicationStatement">
                <xsl:element name="publisher">
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:element>
        </xsl:for-each>
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
                <xsl:value-of select="'Open Access'"/>
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
    <xsl:template match="dansRelation">
        <xsl:if test="dansRelationType='references'">
            <xsl:element name="references">
                <xsl:element name="appellation">
                    <xsl:element name="name">
                        <xsl:attribute name="lang">
                            <xsl:value-of select="'en'" />
                        </xsl:attribute>
                        <xsl:value-of select="dansRelationText"/>
                    </xsl:element>
                    <xsl:element name="id">
                        <xsl:value-of select="dansRelationURI"/>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <!-- ==================================================== -->
    <!--                  hasRepresentation                   -->
    <!-- ==================================================== -->
    <xsl:template name="hasRepresentation">
        <xsl:variable name="file" select="files/file[contains(restricted, 'false') and not(contains(directoryLabel, 'easy-migration')) and filesize = $maxFileSize][1]"/>
        <xsl:variable name="filePath" select="$file/directoryLabel"/>
        <xsl:variable name="fileName" select="$file/filename"/>
        <xsl:element name="hasRepresentation">
            <xsl:choose>
                <xsl:when test="$filePath != ''">
                    <xsl:value-of select="concat($doi, '/', $filePath, '/', $fileName)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="concat($doi, '/', $fileName)"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:element>
    </xsl:template>

    <!-- ==================================================== -->
    <!--               Carare digitalResource                 -->
    <!-- ==================================================== -->
    <xsl:template match="dataset/files/file[contains(restricted, 'false') and not(contains(directoryLabel, 'easy-migration')) and filesize = $maxFileSize]">
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

            <!--   description   -->
            <description lang="en">
                <xsl:text>Report</xsl:text>
            </description>

            <!--   format   -->
            <format>
                <xsl:value-of select="contentType"/>
            </format>

            <xsl:variable name="downloadURL" select="/dataset/files/downloadUrl"/>
            <xsl:variable name="fileId" select="id"/>
            <!-- object -->
            <object>
                <xsl:value-of select="concat($downloadURL, 'api/access/datafile/', $fileId)"/>
            </object>

            <!-- isShownAt -->
            <isShownAt>
                <xsl:value-of select="$doi-url"/>
            </isShownAt>

            <!--   rights   -->
            <rights>
                <accessRights>
                    <xsl:value-of select="'Open Access'"/>
                </accessRights>
                <licence>
                    <xsl:value-of select="/dataset/license/uri"/>
                </licence>
            </rights>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>