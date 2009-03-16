#! /bin/bash

cd target

mv grisu-0.2-beta-SNAPSHOT-grisu-local-backend.jar grisu-local-backend.jar
cp ../backend/grisu-ws/target/grisu-ws.war .

mkdir swing
cd swing
unzip -o ../grisu-0.2-beta-SNAPSHOT-grisu-client-swing.jar
rm ../grisu-0.2-beta-SNAPSHOT-grisu-client-swing.jar
rm META-INF/INDEX.LIST
jar cmf ../../frontend/grisu-client-swing/MANIFEST.MF ../grisu.jar .

cd ..
mkdir cmdline
cd cmdline
unzip -o ../grisu-0.2-beta-SNAPSHOT-grisu-client-commandline.jar
rm ../grisu-0.2-beta-SNAPSHOT-grisu-client-commandline.jar
rm META-INF/INDEX.LIST
jar cmf ../../frontend/grisu-client-commandline/MANIFEST.MF ../grisu-client.jar .






