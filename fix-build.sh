#! /bin/bash

cd target

# fix local backend
mkdir local
cd local
unzip -o ../grisu-0.3-SNAPSHOT-grisu-local-backend.jar
rm ../grisu-0.3-SNAPSHOT-grisu-local-backend.jar
rm -f META-INF/INDEX.LIST
cp ../../backend/grisu-core/src/main/resources/gluev12r2-ext-class-map.properties .
jar cmf ../../backend/grisu-core/MANIFEST.MF ../grisu-local-backend.jar .
cd ..
#jar -i grisu-local-backend.jar


# fix grid-tests client
mkdir grisu-grid-tests
cd grisu-grid-tests
unzip -o ../grisu-0.3-SNAPSHOT-grisu-grid-tests.jar
#rm ../grisu-0.3-SNAPSHOT-grisu-client-swing.jar
rm -f META-INF/INDEX.LIST
cp ../../frontend/grisu-grid-tests/src/main/resources/log4j.properties .
cp ../../backend/grisu-core/src/main/resources/gluev12r2-ext-class-map.properties .
jar cmf ../../frontend/grisu-grid-tests/MANIFEST.MF ../grisu-grid-tests.jar .
cd ..
rm -fr grisu-grid-tests/*
cp grisu-grid-tests.jar grisu-grid-tests/
cp ../bcprov.jar grisu-grid-tests/
cp -r ../frontend/grisu-grid-tests/tests grisu-grid-tests
rm -rf grisu-grid-tests/tests/.svn
cp ../frontend/grisu-grid-tests/grid-tests-hibernate-file.cfg.xml grisu-grid-tests
zip -r grisu-grid-tests.zip grisu-grid-tests

cd ..




