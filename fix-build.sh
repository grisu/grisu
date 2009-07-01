#! /bin/bash

cd target

# fix local backend
mkdir local
cd local
unzip -o ../grisu-0.3-SNAPSHOT-grisu-local-backend.jar
rm ../grisu-0.3-SNAPSHOT-grisu-local-backend.jar
rm -f META-INF/INDEX.LIST
jar cmf ../../backend/grisu-core/MANIFEST.MF ../grisu-local-backend.jar .
cd ..
#jar -i grisu-local-backend.jar

# copy web service backend into this folder for convenience
cp ../backend/grisu-ws/target/grisu-ws.war .
jar -i grisu-ws.war

# fix swing frontend
mkdir swing
cd swing
unzip -o ../grisu-0.3-SNAPSHOT-grisu-client-swing.jar
rm ../grisu-0.3-SNAPSHOT-grisu-client-swing.jar
rm -f META-INF/INDEX.LIST
cp ../../frontend/grisu-client-swing/src/main/resources/log4j.properties .
jar cmf ../../frontend/grisu-client-swing/MANIFEST.MF ../grisu.jar .
cp ../../frontend/grisu-client-cxf/src/main/resources/META-INF/spring.handlers META-INF
jar -uf ../gricli.jar META-INF/spring.handlers
cd ..
#jar -i grisu.jar

# fix commandline frontend
mkdir cmdline
cd cmdline
unzip -o ../grisu-0.3-SNAPSHOT-gricli.jar
#rm ../grisu-0.3-SNAPSHOT-gricli.jar
rm -f META-INF/INDEX.LIST
cp ../../frontend/gricli/src/main/resources/log4j.properties .
cp ../../frontend/gricli/MANIFEST.MF META-INF/
jar -cf  ../gricli.jar .
cp ../../frontend/grisu-client-cxf/src/main/resources/META-INF/spring.handlers META-INF
jar -uf ../gricli.jar META-INF/spring.handlers
cd ..
#jar -i grisu-client.jar

# fix batch client
mkdir batch
cd batch
unzip -o ../grisu-0.3-SNAPSHOT-grisu-client-batch.jar
rm ../grisu-0.3-SNAPSHOT-grisu-client-batch.jar
rm -f META-INF/INDEX.LIST
cp ../../frontend/grisu-client-batch/src/main/resources/log4j.properties .
jar cmf ../../frontend/grisu-client-batch/MANIFEST.MF ../grisu-client-batch.jar .
cd ..

# fix grisu-grid-tests
#mkdir grisu-grid-tests
#cd grisu-grid-tests
#unzip -o ../grisu-0.3-SNAPSHOT-grisu-grid-tests.jar
##rm ../grisu-0.3-SNAPSHOT-grisu-client-swing.jar
#rm -f META-INF/INDEX.LIST
#cp ../../frontend/grisu-grid-tests/src/main/resources/log4j.properties .
#jar cmf ../../frontend/grisu-grid-tests/MANIFEST.MF ../grisu-grid-tests.jar .
#cd ..


