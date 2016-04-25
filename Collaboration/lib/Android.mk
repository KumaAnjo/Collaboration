LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := libgsonjar:gson-2.2.4.jar
include $(BUILD_MULTI_PREBUILT)