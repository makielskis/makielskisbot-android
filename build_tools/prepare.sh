#!/bin/bash

# goto and save path to this scripts location in filesystem
ORIGINAL_DIR=`pwd`;
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )";
cd $DIR;

function echo_bold {
  local bold=`tput bold`;
  local normal=`tput sgr0`;
  echo -e "\n::: ${bold}$1${normal}\n";
}

function prepareNDK() {
  # Do this only once.
  if  [ -z $NDK ]; then
    # is 'ndk-build' available in PATH?
    if hash ndk-build 2>/dev/null; then
      # get location of 'ndk-build'
      local path=$(command -v ndk-build);
      NDK=${path%/*};
      echo_bold "NDK found: '$NDK'";
    else
      # cannot continue without NDK
      echo_bold "Cannot find NDK. Make sure it is in your PATH.";
      exit 1;
    fi
  fi
}

function prepareToolchain() {
  # is toolchain in expected place?
  if [ -f  $DIR/android-toolchain/bin/arm-linux-androideabi-g++ ] && [ -d  $DIR/android-toolchain/sysroot ]; then
    echo_bold "Toolchain found.";
  else
    echo_bold "Toolchain not found. Creating one.";

    # make sure NDK is set
    prepareNDK;

    # generate a toolchain with the NDK
    $NDK/build/tools/make-standalone-toolchain.sh --toolchain=arm-linux-androideabi-4.8 --install-dir=$DIR/android-toolchain;

  fi
  # save path to toolchain
  TOOLCHAIN_SYSROOT=$DIR/android-toolchain/sysroot;
  TOOLCHAIN_BIN=$DIR/android-toolchain/bin;
}

function prepareBoost() {
  # are boost libs and headers in there expected location?
  if [ -d $DIR/android-boost/build/lib -a -d $DIR/android-boost/build/include/boost-1_55/ ]; then
    # update symlinks
    rm $TOOLCHAIN_SYSROOT/usr/lib/libboost*.a;
    rm $TOOLCHAIN_SYSROOT/usr/include/boost;
    ln -s $DIR/android-boost/build/lib/libboost*.a $TOOLCHAIN_SYSROOT/usr/lib/;
    ln -s $DIR/android-boost/build/include/boost-1_55/boost $TOOLCHAIN_SYSROOT/usr/include/;
    echo_bold "Boost is ready.";
  else
    if [ "$1" == "checkonly" ]; then
      echo_bold "Something went wrong about compiling boost".;
      return;
    fi
    # boost is missing so get it
    echo_bold "Boost not found. Compiling it.";

    # clean up any previous installation
    rm -rf $DIR/android-boost;

    # get a known working copy of the boost for android scripts
    git clone git@bitbucket.org:makielski/boost-for-android.git android-boost;
    cd android-boost; # always remember to go back to $DIR

    # build boost
    ./build-android.sh --boost=1.55.0 --with-libraries=system,thread,date_time,chrono,regex,filesystem,iostreams,random,program_options $NDK;

    # go back to the default dir
    cd $DIR;
    prepareBoost "checkonly";
  fi
}

prepareNDK;
prepareToolchain;
prepareBoost;

cd $ORIGINAL_DIR;

echo_bold "Writing cmake toolchian file to '$DIR/android.toolchain.cmake'";

echo "set(CMAKE_SYSTEM_NAME Linux)
set(CMAKE_C_COMPILER $TOOLCHAIN_BIN/arm-linux-androideabi-gcc)
set(CMAKE_CXX_COMPILER $TOOLCHAIN_BIN/arm-linux-androideabi-g++)
set(CMAKE_FIND_ROOT_PATH $TOOLCHAIN_SYSROOT)
set(CMAKE_FIND_ROOT_PATH_MODE_PROGRAM NEVER)
set(CMAKE_FIND_ROOT_PATH_MODE_INCLUDE ONLY)
set(CMAKE_FIND_ROOT_PATH_MODE_LIBRARY ONLY)

set(Boost_COMPILER -gcc)

set(ANDROID true)" > $DIR/android.toolchain.cmake
