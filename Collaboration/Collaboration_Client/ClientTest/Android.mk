LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

# include res directory from timepicker
LOCAL_RESOURCE_DIR := $(addprefix $(LOCAL_PATH)/, res)

LOCAL_MODULE_TAGS := optional

LOCAL_STATIC_JAVA_LIBRARIES := collaboration_connection_lib
LOCAL_STATIC_JAVA_LIBRARIES += android-support-v4

server_src := ../src_common

src_dirs := src $(server_src)

LOCAL_SRC_FILES := $(call all-java-files-under, $(src_dirs))

LOCAL_PACKAGE_NAME := ClientConnectionTest

# LOCAL_SDK_VERSION := current

include $(BUILD_PACKAGE)

# Use the following include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))
