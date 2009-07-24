#! /bin/bash

cd target

# fix grid-tests client
mkdir grid-grid-tests
cd grid-grid-tests
unzip -o ../grisu-0.3-SNAPSHOT-grisu-grid-tests.jar
#rm ../grisu-0.3-SNAPSHOT-grisu-client-swing.jar
rm -f META-INF/INDEX.LIST
cp ../../frontend/grisu-grid-tests/src/main/resources/log4j.properties .
cp ../../backend/grisu-core/src/main/resources/gluev12r2-ext-class-map.properties .
jar cmf ../../frontend/grisu-grid-tests/MANIFEST.MF ../grisu-grid-tests.jar .
cd ..

cd ..
cp bcprov.jar target/



