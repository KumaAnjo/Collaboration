LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

# Use the following include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))
