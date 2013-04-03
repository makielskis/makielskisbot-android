LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := bs_lib
LOCAL_SRC_FILES := libbotscript-server.so
include $(PREBUILT_SHARED_LIBRARY)
