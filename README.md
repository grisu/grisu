Grisu
=====

_Grisu_ is an open source framework intended to sit on top of grid middleware (Globus being the only supported one at the moment -- others possible via plugins)  in order to make development of user/workflow/application specific clients for grid resources as easy as possible. _Grisu_ also comes with two implementations of general purpose grid clients, one commandline based called [griclish](https://github.com/grisu/gricli), the other one a desktop gui application called the [Grisu Template client](https://github.com/grisu/grisu-template):

![Grisu Template client - job creation](https://raw.github.com/grisu/grisu/develop/doc/images/template_client_job_create.png)

_Grisu_ is written in Java and is split up in a [backend](https://github.com/grisu/grisu/wiki/Backend) and a [frontend] (https://github.com/grisu/grisu/wiki/Frontpage) part. The connection between those two parts is done via an API, which is implemented in a class called [ServiceInterface](https://github.com/grisu/grisu/wiki/ServiceInterface).
 

Documentation
------------------------

- [Wiki](https://github.com/grisu/grisu/wiki)
- [Javadoc](https://code.ceres.auckland.ac.nz/jenkins/job/Grisu-SNAPSHOT-Javadoc/javadoc/)
- [SOAP/REST API documentaion for web-service based backend](https://compute.services.bestgrid.org/)

Prerequisites
--------------------

In order to build _Grisu_ from the git sources, you need: 

- Java development kit (version 6)
- [git](http://git-scm.com) 
- [Apache Maven](http://maven.apache.org) (version >=3)

Building
------------

Clone the git repository:

    git clone git://github.com/grisu/grisu.git
	
Build using maven:

    cd grisu
	mvn clean install
	
Package structure
--------------------------

The _Grisu_ maven parent project consists of several child projects. Those are:

 * _grisu-commons_: common code that is shared between front- and backend
 * _grisu-core_: the backend code, including a local backend that can be connected to directly if in the same classpath as a grisu client
 * _grisu-client_: main client code, if you want to write a grisu client, this is the main dependency
 * _grisu-client-swing_: extends _grisu-client_, adds Swing based widgets and application struts
 
 
Important dependencies
----------------------------------

 * [grid_commons](https://github.com/grisu/grid-jcommons): Library containing commonly used code for different types of grid clients
 * [jgrith](https://github.com/grith/jgrith): collection of methods and classes related to grid authentication
 * [grid-session](https://github.com/grith/grid-session): little daemon-type application that holds authentication tokens and caches them in memory for use by different clients, can auto-renew some types of credentials
 * [grin](https://github.com/makkus/Grin): Grid information manager written in Java/Groovy, trying to be as easy to configure as possible

Available _Grisu_ clients
------------------------------------

 * [griclish](https://github.com/grisu/gricli): generic command line client
 * [Grisu template client](https://github.com/grisu/grisu-template): generic GUI client
 * [grython](https://github.com/grisu/grython): a wrapper to enable scripting grid submission workflows using python syntax
 * [Grisu-Virtscreen](https://github.com/grisu/grisu-virtscreen): GUI application for a Virtual screening pipeling (using Gold & Gromacs)
 * [grisu-benchmark](https://github.com/grisu/grisu-benchmark): commandline client to submit the same job using different cpu configurations and plot out benchmark results

Other
--------

 * [enunciate-backend](https://github.com/grisu/enunciate-backend): web service based _Grisu_ backend
 * [grisu-archetypes](https://github.com/grisu/grisu-archetypes): maven archetypes to assist creating _Grisu_ clients
 * [grisu-integration-tests](https://github.com/grisu/grisu-integration-tests): End-to-end tests for the _Grisu_ framework
 * [combined client](https://github.com/grisu/combinedClient): project to combine several _Grisu_ clients in order to make them installable (plus auto-update) via:
 * [nesi-tools-installer](https://github.com/nesi/nesi-tools-installer): izpack based installer to make deployment and auto-updating of client tools easy
	






