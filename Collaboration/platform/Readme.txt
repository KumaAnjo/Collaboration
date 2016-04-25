1,说明：
该platform文件夹下是实现通话背景音修改的两个与MTK xskk平台相关的文件。
请将目录内的文件按照文件目录结构拷贝到xskk对应的目录下并替换。之后编译这两个模块。push到xskk手机中。
其中AudioMTKStreamOut.cpp文件中的修改目的是使能通话背景音。
MediaFocusControl.java文件中的修改是使得在通话中，其他应用仍然可以播放音乐。
2,编译方法和push方法：
./mk -t mm frameworks/base/
对应生成的文件为：framework2.jar framework.jar
使用如下命令push到手机上
adb push \mt6592_kk\out\target\product\ckt92_we_kk\system\framework\framework2.jar system/framework/
adb push \mt6592_kk\out\target\product\ckt92_we_kk\system\framework\framework.jar system/framework/


./mk -t mm mediatek/platform/mt6592/hardware/audio/
对应生成的文件为：libaudio.primary.default.so
使用如下命令push到手机上
adb push \mt6592_kk\out\target\product\ckt92_we_kk\system\lib\libaudio.primary.default.so system/lib/
