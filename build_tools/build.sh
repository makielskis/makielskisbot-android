#!/bin/bash

# goto and save path to this scripts location in filesystem
ORIGINAL_DIR=`pwd`;
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )";
cd $DIR;

./prepare.sh

mkdir -p build;
cd build;
cat ../android.toolchain.cmake;
cmake -DCMAKE_TOOLCHAIN_FILE=../android.toolchain.cmake ../../botscript-server;
make -j botscript-server;
cd $DIR;
cp build/libbotscript-server.so ../jni/libs/libbotscript-server.so;
cd ..;
ndk-build;
ant clean release;
mv bin/MainActivity-release.apk makielskisbot-0.0.0.apk
