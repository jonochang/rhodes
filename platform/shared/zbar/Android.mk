LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libzbarjni
LOCAL_SRC_FILES := config.c \
	error.c \
	symbol.c \
	convert.c \
	processor.c \
	refcnt.c \
	window.c \
	video.c \
	img_scanner.c \
	scanner.c \
	image.c \
	decoder.c \
	decoder/ean.c \
	decoder/code128.c \
	decoder/code39.c \
	decoder/i25.c \
	processor/posix.c \
	processor/lock.c \
	processor/null.c \
	video/null.c \
	window/null.c \
	zbarjni.c \
	rhoextension_init.c

#include $(BUILD_STATIC_LIBRARY)
include $(BUILD_SHARED_LIBRARY)

#include $(CLEAR_VARS)
#
#LOCAL_MODULE    := libzbarjni-fake
#LOCAL_SRC_FILES := empty.c

#LOCAL_STATIC_LIBRARIES := libzbarjni

#include $(BUILD_SHARED_LIBRARY)