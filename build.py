#! /usr/bin/python

import subprocess
import tempfile
import os
import shutil
from time import sleep
from sys import exit


BUILD_DIR="/home/markus/grisu-build/"
SRC_DIR="/home/markus/src/"
BUILD_LOG="/home/markus/grisu-build/last-build.log"
ERROR_LOG="/home/markus/grisu-build/last-build-err.log"

log = open(BUILD_LOG, 'a')
logErr = open(ERROR_LOG, 'a')

def mvn(dir, command="clean install"):
    try:
        print "Building maven project: "+dir
        commandline = "mvn -f "+SRC_DIR+dir+"/pom.xml "+command
        print "Executing: "+commandline
        child = subprocess.Popen(commandline.split(), stdout=log,stderr=logErr)
        child.wait()   
        print "Execution finished."
    
    except KeyboardInterrupt:   # when Ctrl-C is pressed
        print "Interrupted..."
        exit(1)

def delete(file):

    try:
        os.remove(file)
    except:
        pass

def package(file, outputFileName=None):

    try:
        tmpDir = tempfile.gettempdir()+"/grisu-build-tmp/"
        print "Deleting temp dir..."
        shutil.rmtree(tmpDir, True)
        
        file = SRC_DIR + file
        print "Extracting file: "+file+"..."
        child = subprocess.Popen(["unzip", "-o", file, "-d", tmpDir], stdout=log,stderr=logErr)
        child.wait()

        delete(tmpDir+"META_INF/INDEX.LIST")
        
        if (outputFileName == None):
            outputFileName = os.path.basename(file)
        
        newFile = BUILD_DIR+outputFileName
        delete(newFile)
        print "Creating new jar file: "+newFile
        os.chdir(tmpDir)
        child = subprocess.Popen(["jar", "cmf", "META-INF/MANIFEST.MF", newFile, "."], stdout=log,stderr=logErr)
        child.wait()
        print "Creation finished."
        print "Signing..."
        child = subprocess.Popen(["jarsigner", "-storepass", "FxAKs3p6", newFile, "arcs"], stdout=log, stderr=logErr)
        
    except KeyboardInterrupt:
        print "Interrupted..."
        exit(1)

def copy(file, dest):
    file = SRC_DIR + file
    delete(dest)
    dest = BUILD_DIR+dest
    shutil.copyfile(file, dest)

print "Building grisu..."

mvn("grisu")
mvn("grisu-connectors")
mvn("grisu-virtscreen")

package("grisu/frontend/grisu-client/target/grisu-client-dependencies.jar")
package("grisu/frontend/client-side-mds/target/client-side-mds.jar")
package("grisu/backend/grisu-core/target/local-backend.jar")
copy("grisu-connectors/backend-modules/enunciate-backend/target/enunciate-backend-0.3-SNAPSHOT.war", "grisu-ws.war")
package("grisu-virtscreen/target/grisu-virtscreen-binary.jar", "grisu-virtscreen.jar")




