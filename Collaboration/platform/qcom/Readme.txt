1,说明：
.
|-- 0001-modify-to-make-the-audio-can-transfer-normallly-in-q.patch //在高通上实现语音传递应用层的patch，对应目录为/Telephony_Projects_777/
|-- device
|   `-- qcom
|       `-- msm8916_32
|           `-- 0001-Feature-enable-the-incall-music-delivery-function.patch //与qcom A 8916 32平台相关的patch,修改的是与系统配置相关的文件。
|-- hardware
|   `-- qcom
|       `-- audio
|           `-- 0001-Feature-Audio-modify-to-support-the-incall-music-del.patch //与qcom A 8916 32平台相关的patch,修改是audio hal层，通过修改使得A双卡手机两个卡槽在插入sim卡通过过程中，incall music delivery功能均能正常。


a,请与平台相关的两个patch按照文件目录结构拷贝到qcom A对应的目录下并使用git am打上patch。之后编译这两个模块。push到qcom A手机中。
b,将0001-modify-to-make-the-audio-can-transfer-normallly-in-q.patch 拷贝到Telephony_Projects_777/目录下并使用git am打入patch。

2,编译方法和push方法：
a,
mmm hardware/qcom/audio/
对应生成的文件为：audio.primary.msm8916.so audio_policy.msm8916.so
使用如下命令push到手机上
adb push out/target/product/msm8916_32/system/lib/hw/audio.primary.msm8916.so system/lib/hw/
adb push out/target/product/msm8916_32/system/lib/hw/audio_policy.msm8916.so system/lib/hw/
b,
adb push LINUX/android/device/qcom/msm8916_32/audio_policy.conf system/etc/
adb push LINUX/android/device/qcom/msm8916_32/mixer_paths_qrd_skui.xml system/etc/

c,
将打入0001-modify-to-make-the-audio-can-transfer-normallly-in-q.patch的Telephony_Projects_777目录编译，生成CollClient.apk及CollServer.apk
 adb push CollClient.apk system/app/
 adb push CollServer.apk system/app/
 
3,注意事项：
a,只pushCollClient.apk及CollServer.apk会报错找不到一个libxxx.so，使用的方法是直接push libxxx.so到system/lib/。
b,由于qcom的编译系统会做精简，因此在qcom编译环境中编译出的CollServer及CollClient运行时会报错找不到一个方法。因此采用的方法是直接将Telephony_Projects_777目录拷贝到mtk的packages/app/下进行的编译。
