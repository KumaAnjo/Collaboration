1,˵����
��platform�ļ�������ʵ��ͨ���������޸ĵ�������MTK xskkƽ̨��ص��ļ���
�뽫Ŀ¼�ڵ��ļ������ļ�Ŀ¼�ṹ������xskk��Ӧ��Ŀ¼�²��滻��֮�����������ģ�顣push��xskk�ֻ��С�
����AudioMTKStreamOut.cpp�ļ��е��޸�Ŀ����ʹ��ͨ����������
MediaFocusControl.java�ļ��е��޸���ʹ����ͨ���У�����Ӧ����Ȼ���Բ������֡�
2,���뷽����push������
./mk -t mm frameworks/base/
��Ӧ���ɵ��ļ�Ϊ��framework2.jar framework.jar
ʹ����������push���ֻ���
adb push \mt6592_kk\out\target\product\ckt92_we_kk\system\framework\framework2.jar system/framework/
adb push \mt6592_kk\out\target\product\ckt92_we_kk\system\framework\framework.jar system/framework/


./mk -t mm mediatek/platform/mt6592/hardware/audio/
��Ӧ���ɵ��ļ�Ϊ��libaudio.primary.default.so
ʹ����������push���ֻ���
adb push \mt6592_kk\out\target\product\ckt92_we_kk\system\lib\libaudio.primary.default.so system/lib/
