Grisu
=====
 
Grisu is an open source framework to help grid admins and developers to support end users in a grid environment. Grisu publishes an easy-to-use service interface which by default sits behind a web service. This service interface contains a set of methods that are usually needed to submit jobs to the grid, including providing information about the grid and the staging of input/output files from/to the users desktop.

Grisu-core
=========

This is the main package of the Grisu framework. It is written entirely in Java and contains the backend compoent (**/backend/grisu-core**), a frontend library (**/frontend/grisu-client** and **/frontend/grisu-client-swing**) and a **grisu-common** which contains shared code of both components.

Documentation
------------------------

- [Wiki](https://github.com/grisu/grisu/wiki)
- [Javadoc](http://grisu.github.com/grisu/javadoc/)
- [SOAP/REST API documentaion](https://compute.services.bestgrid.org/)

Prerequisites
--------------------

In order to build Grisu from the git sources, you need: 

- Java development kit (version ≥ 6)
- [git](http://git-scm.com) 
- [Apache Maven](http://maven.apache.org) (version >=2)





