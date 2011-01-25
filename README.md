Grisu
====

Grisu is an open source framework to help grid admins and developers to support end users in a grid environment. Grisu publishes an easy-to-use service interface which by default sits behind a web service. This service interface contains a set of methods that are usually needed to submit jobs to the grid, including providing information about the grid and the staging of input/output files from/to the users desktop.

Prerequisites
-------------------

In order to build Grisu from the svn sources, you need: 

- Sun Java Development Kit (version ≥ 6)
- git 
- maven (version >=2)

Checking out sourcecode
-------------------------------------

For the core package:

 `git clone git://github.com/grisu/grisu.git`

For the connectors package:

`git clone git://github.com/grisu/grisu-connectors.git`

For the exmple client implementation package:

`git clone git://github.com/grisu/grisu-clients.git`

Building Grisu using Maven
-----------------------------------------

To build one of the above modules, cd into the module root directory of the module to build and execute: 

    cd <grisu-module>
    mvn clean install


