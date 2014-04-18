cmake_minimum_required(VERSION 2.6)

set(CMAKE_SYSTEM_NAME Linux)
set(CMAKE_C_COMPILER ${NDK}/toolchains/arm-linux-androideabi-4.8/prebuilt/linux-x86_64/bin/arm-linux-androideabi-gcc)
set(CMAKE_CXX_COMPILER ${NDK}/toolchains/arm-linux-androideabi-4.8/prebuilt/linux-x86_64/bin/arm-linux-androideabi-g++)
set(CMAKE_FIND_ROOT_PATH ${NDK}/platforms/android-9/arch-arm)
set(CMAKE_FIND_ROOT_PATH_MODE_PROGRAM NEVER)
set(CMAKE_FIND_ROOT_PATH_MODE_INCLUDE ONLY)
set(CMAKE_FIND_ROOT_PATH_MODE_LIBRARY ONLY)
set(BOOST_LIBRARYDIR android-boost/build/lib)
set(BOOST_INCLUDEDIR android-boost/build/include)

set(Boost_COMPILER -gcc)

set(ANDROID true)

project(botscript-server-app)

add_subdirectory(botscript-server)

add_custom_target(prepare
  git clone git@bitbucket.org:makielski/boost-for-android.git android-boost;
  cd android-boost;
  ./build-android.sh --boost=1.55.0 --with-libraries=system,thread,date_time,chrono,regex,filesystem,iostreams,random,program_options ${NDK};
)

add_custom_target(build
)
add_dependencies(build prepare botscript-server)
