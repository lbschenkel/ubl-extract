#!/bin/sh -e

NAME="ubl-extract"
OUTPUT_DIR="out"
JAVAC_ARGS="-Werror -Xlint:all"

mkdir -p ${OUTPUT_DIR}/classes
javac ${JAVAC_ARGS} -d ${OUTPUT_DIR}/classes src/*.java
jar cfm ${OUTPUT_DIR}/${NAME}.tmp.jar src/MANIFEST.MF -C ${OUTPUT_DIR}/classes .
cat src/run.sh ${OUTPUT_DIR}/${NAME}.tmp.jar > ${OUTPUT_DIR}/${NAME}.jar
chmod +x ${OUTPUT_DIR}/${NAME}.jar
echo Done, executable is at ${OUTPUT_DIR}/${NAME}.jar
