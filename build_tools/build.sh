#!/bin/bash

# goto and save path to this scripts location in filesystem
ORIGINAL_DIR=`pwd`;
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )";
cd $DIR;

CORE_COUNT=$(grep -c ^processor /proc/cpuinfo);

./prepare.sh

mkdir -p build;
cd build;
cat ../android.toolchain.cmake;
cmake -DCMAKE_TOOLCHAIN_FILE=../android.toolchain.cmake ../../botscript-server;
make -j$CORE_COUNT botscript-server;
cd $DIR;
cp build/libbotscript-server.so ../jni/libs/libbotscript-server.so;
cd ..;
ndk-build;
ant clean release;
