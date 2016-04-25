LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)
LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_STATIC_JAVA_LIBRARIES := CommonProtocol-1.0.1

LOCAL_MODULE := PhoneProtocol-1.0.0

include $(BUILD_STATIC_JAVA_LIBRARY)
