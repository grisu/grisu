Changelog
=========


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
