1,˵����
.
|-- 0001-modify-to-make-the-audio-can-transfer-normallly-in-q.patch //�ڸ�ͨ��ʵ����������Ӧ�ò��patch����ӦĿ¼Ϊ/Telephony_Projects_777/
|-- device
|   `-- qcom
|       `-- msm8916_32
|           `-- 0001-Feature-enable-the-incall-music-delivery-function.patch //��qcom A 8916 32ƽ̨��ص�patch,�޸ĵ�����ϵͳ������ص��ļ���
|-- hardware
|   `-- qcom
|       `-- audio
|           `-- 0001-Feature-Audio-modify-to-support-the-incall-music-del.patch //��qcom A 8916 32ƽ̨��ص�patch,�޸���audio hal�㣬ͨ���޸�ʹ��A˫���ֻ����������ڲ���sim��ͨ�������У�incall music delivery���ܾ���������


a,����ƽ̨��ص�����patch�����ļ�Ŀ¼�ṹ������qcom A��Ӧ��Ŀ¼�²�ʹ��git am����patch��֮�����������ģ�顣push��qcom A�ֻ��С�
b,��0001-modify-to-make-the-audio-can-transfer-normallly-in-q.patch ������Telephony_Projects_777/Ŀ¼�²�ʹ��git am����patch��

2,���뷽����push������
a,
mmm hardware/qcom/audio/
��Ӧ���ɵ��ļ�Ϊ��audio.primary.msm8916.so audio_policy.msm8916.so
ʹ����������push���ֻ���
adb push out/target/product/msm8916_32/system/lib/hw/audio.primary.msm8916.so system/lib/hw/
adb push out/target/product/msm8916_32/system/lib/hw/audio_policy.msm8916.so system/lib/hw/
b,
adb push LINUX/android/device/qcom/msm8916_32/audio_policy.conf system/etc/
adb push LINUX/android/device/qcom/msm8916_32/mixer_paths_qrd_skui.xml system/etc/

c,
������0001-modify-to-make-the-audio-can-transfer-normallly-in-q.patch��Telephony_Projects_777Ŀ¼���룬����CollClient.apk��CollServer.apk
 adb push CollClient.apk system/app/
 adb push CollServer.apk system/app/
 
3,ע�����
a,ֻpushCollClient.apk��CollServer.apk�ᱨ���Ҳ���һ��libxxx.so��ʹ�õķ�����ֱ��push libxxx.so��system/lib/��
b,����qcom�ı���ϵͳ�������������qcom���뻷���б������CollServer��CollClient����ʱ�ᱨ���Ҳ���һ����������˲��õķ�����ֱ�ӽ�Telephony_Projects_777Ŀ¼������mtk��packages/app/�½��еı��롣
