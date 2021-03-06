LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

# include res directory from timepicker
LOCAL_RESOURCE_DIR := $(addprefix $(LOCAL_PATH)/, res)

LOCAL_MODULE_TAGS := optional

LOCAL_STATIC_JAVA_LIBRARIES := collaboration_connection_lib \
                               PhoneProtocol-1.0.0 \
                               android-support-v4
			       
src_dirs := src

LOCAL_SRC_FILES := $(call all-java-files-under, $(src_dirs))

LOCAL_PACKAGE_NAME := CollServer
LOCAL_CERTIFICATE:=platform
# LOCAL_SDK_VERSION := current

include $(BUILD_PACKAGE)

# Use the following include to make our test apk.
#include $(call all-makefiles-under,$(LOCAL_PATH))
