Changelog
=========

0.5
--- 

* new internal information manager interface, move away from mds
* added method to be able to clear the users session & filesystem cache
* checking target when archiving jobs to make sure no data is lost before deleting source
* better client-side file caching
* fixes for GT4 & GT5 connectors
* using refactored Credential classes from jgrith
* integration of grid-session in client library
* many bugfixes

0.4
---

* Refactoring of user session, much cleaner now
* Refactoring of authentication part in grisu-client, more generic now
* method for killing jobs in parallel -- performance improvement
* preparing for GlobusOnline as file transfer backend
* exporting grisu properties to environment of the job (Prepending with GRISU_)
* debian * rpm packaging for local backend
* added support for multipe MyProxy servers
* many other bugfixes, small features

0.3.7
-----

* GT5 submission fixes
* Improved gridftp / multithreading stability and performance
* Bugfixes
* Changes in API: made most file related and also killJob methods asynchronous
* Fix for possible deadlock issue becaue of spinning cursor on commandline login

0.3.6
-----

* Copying to/from virtual urls now works
* Updated dependencies
* Frontend-/Backend-incompatiblity check

0.3.5.2
-------

* fixed potential endless loop when waiting for a task to execute on cli

0.3.5.1
-------

* added logging statements for shibboleth logins to track down failed shib logins

0.3.5
-----

* virtual urls now supported as job input files
* progress indicator for commandline clients
* filemanager supports ~ now

0.3.4.3
-------

* fix for file upload problem with bigger files

0.3.4.2
-------

* disabled gt5 lookup again because of some regression on prod backend
* another fix for open connections issue

0.3.4.1
-------

* fix for open connections issue
* fix for gt5 job status lookup after GT5 upgrade
* caching of voms credentials for job status lookups / should improve performance
* fix for x509 commandline login issue
