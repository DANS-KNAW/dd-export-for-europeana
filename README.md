dd-export-for-europeana
===========
[![Build Status](https://travis-ci.org/DANS-KNAW/dd-export-for-europeana.png?branch=master)](https://travis-ci.org/DANS-KNAW/dd-export-for-europeana)

SYNOPSIS
--------

    dd-export-for-europeana [-d,--doi|-l,--list] [-t,--transform] [-o,--output]


DESCRIPTION
-----------

Tool to transform Dataverse metadata into Europeana format


ARGUMENTS
---------

    Options:

       -d, --doi   <arg>        The doi for which to transform the metadata
       -l, --list  <arg>        A file containing a newline separated list of doi's for which to transform the
                                metadata
       -o, --output  <arg>      The directory in which to output the resulting metadata. If '-d' is used, this is
                                optional (default to stdout); if '-l' is used, this argument is mandatory.
       -t, --transform  <arg>   The file containing an XSLT to be applied to the metadata of the given doi(s); if
                                not provided, no transformation will be performed, but the input for the
                                transformation will be returned.
       -h, --help               Show help message
       -v, --version            Show version of this program


EXAMPLES
--------

    dd-export-for-europeana -d doi:10.5072/DAR/VZP5W1 -t my-transformation.xslt
    dd-export-for-europeana -l my-dois.txt -t my-transformation.xslt -o transformation-output/


INSTALLATION AND CONFIGURATION
------------------------------
Currently this project is built only as an RPM package for RHEL7/CentOS7 and later. The RPM will install the binaries to
`/opt/dans.knaw.nl/dd-export-for-europeana ` and the configuration files to `/etc/opt/dans.knaw.nl/dd-export-for-europeana`.

To install the module on systems that do not support RPM, you can copy and unarchive the tarball to the target host.
You will have to take care of placing the files in the correct locations for your system yourself. For instructions
on building the tarball, see next section.


BUILDING FROM SOURCE
--------------------
Prerequisites:

* Java 8 or higher
* Maven 3.3.3 or higher
* RPM

Steps:
    
    git clone https://github.com/DANS-KNAW/dd-export-for-europeana.git
    cd dd-export-for-europeana 
    mvn clean install

If the `rpm` executable is found at `/usr/local/bin/rpm`, the build profile that includes the RPM 
packaging will be activated. If `rpm` is available, but at a different path, then activate it by using
Maven's `-P` switch: `mvn -Pprm install`.

Alternatively, to build the tarball execute:

    mvn clean install assembly:single
