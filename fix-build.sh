#! /bin/bash

cd target

# fix local backend
mkdir local
cd local
unzip -o ../grisu-0.2-beta-SNAPSHOT-grisu-local-backend.jar
rm ../grisu-0.2-beta-SNAPSHOT-grisu-local-backend.jar
rm -f META-INF/INDEX.LIST
jar cmf ../../backend/grisu-local/MANIFEST.MF ../grisu-local-backend.jar
cd ..
#jar -i grisu-local-backend.jar

# copy web service backend into this folder for convenience
cp ../backend/grisu-ws/target/grisu-ws.war .
jar -i grisu-ws.war

# fix swing frontend
mkdir swing
cd swing
unzip -o ../grisu-0.2-beta-SNAPSHOT-grisu-client-swing.jar
rm ../grisu-0.2-beta-SNAPSHOT-grisu-client-swing.jar
rm -f META-INF/INDEX.LIST
cp ../../frontend/grisu-client-swing/src/main/resources/log4j.properties .
jar cmf ../../frontend/grisu-client-swing/MANIFEST.MF ../grisu.jar .
cd ..
#jar -i grisu.jar

# fix commandline frontend
mkdir cmdline
cd cmdline
unzip -o ../grisu-0.2-beta-SNAPSHOT-grisu-client-commandline.jar
rm ../grisu-0.2-beta-SNAPSHOT-grisu-client-commandline.jar
rm -f META-INF/INDEX.LIST
cp ../../frontend/grisu-client-swing/src/main/resources/log4j.properties .
jar cmf ../../frontend/grisu-client-commandline/MANIFEST.MF ../grisu-client.jar .
cd ..
#jar -i grisu-client.jar

# fix batch client
mkdir batch
cd batch
unzip -o ../grisu-0.2-beta-SNAPSHOT-grisu-client-batch.jar
rm ../grisu-0.2-beta-SNAPSHOT-grisu-client-batch.jar
rm -f META-INF/INDEX.LIST
cp ../../frontend/grisu-client-batch/src/main/resources/log4j.properties .
jar cmf ../../frontend/grisu-client-batch/MANIFEST.MF ../grisu-client-batch.jar .
cd ..




