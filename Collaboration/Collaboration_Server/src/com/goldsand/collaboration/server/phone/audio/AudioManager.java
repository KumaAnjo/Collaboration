package com.goldsand.collaboration.server.phone.audio;

import org.webrtc.videoengineapp.ViEAndroidJavaAPI;

import android.content.Context;
import android.util.Log;

public class AudioManager {
    private ViEAndroidJavaAPI vieAndroidAPI;
    private int voiceChannel=4;
    private int audioRxPort=11113;
    public boolean voERunning=false;
    private int audioTxPort=11113;
    private String remoteIp="127.0.0.1";
    private static final String TAG = "AudioManager_WEBRTC";
    private boolean enableTrace=true;
    private Context context;

    public AudioManager(Context context){
      this.context=context;
      initVoice();
    }
    public void setRemoteIP(String ipAddr) {
        this.remoteIp=ipAddr;
    }

    public void prepareAudio() {
        setupVoE();
    }
    public void startAudio() {
        startVoiceEngine();
    }

    public void stopAudio() {
        stopVoiceEngine();
    }
    private void initVoice()
    {
        vieAndroidAPI=new ViEAndroidJavaAPI(context);
        vieAndroidAPI.Init(enableTrace);
    }
    private int setupVoE() {
        // Create VoiceEngine
        // Error logging is done in native API wrapper
        vieAndroidAPI.VoE_Create(context);

        // Initialize
        if (0 != vieAndroidAPI.VoE_Init(enableTrace)) {
            Log.d(TAG, "VoE init failed");
            return -1;
        }
        // Suggest to use the voice call audio stream for hardware volume controls
       // setVolumeControlStream(android.media.AudioManager.STREAM_VOICE_CALL);
        return 0;
    }
    private int startVoiceEngine() {
        // Create channel
        voiceChannel = vieAndroidAPI.VoE_CreateChannel();
        if (0 > voiceChannel) {
            Log.d(TAG, "VoE create channel failed");
            return -1;
        }

        // Set local receiver
        if (0 != vieAndroidAPI.VoE_SetLocalReceiver(voiceChannel,
                        audioRxPort)) {
            Log.d(TAG, "VoE set local receiver failed");
        }

        if (0 != vieAndroidAPI.VoE_StartListen(voiceChannel)) {
            Log.d(TAG, "VoE start listen failed");
        }

        // Route audio
        //   routeAudio(cbEnableSpeaker.isChecked());

        // set volume to default value
        if (0 != vieAndroidAPI.VoE_SetSpeakerVolume(204)) {
            Log.d(TAG, "VoE set speaker volume failed");
        }

        // Start playout
        if (0 != vieAndroidAPI.VoE_StartPlayout(voiceChannel)) {
            Log.d(TAG, "VoE start playout failed");
        }

        if (0 != vieAndroidAPI.VoE_SetSendDestination(voiceChannel,
                                                      audioTxPort,
                                                      remoteIp)) {
            Log.d(TAG, "VoE set send  destination failed"+remoteIp);
        }

        SetCoder();

        if (0 != vieAndroidAPI.VoE_SetECStatus(false)) {
            Log.d(TAG, "VoE set EC Status failed");
        }

        if (0 != vieAndroidAPI.VoE_SetAGCStatus(false)) {
            Log.d(TAG, "VoE set AGC Status failed");
        }

        if (0 != vieAndroidAPI.VoE_SetNSStatus(false)) {
            Log.d(TAG, "VoE set NS Status failed");
        }
        if (0 != vieAndroidAPI.VoE_StartSend(voiceChannel)) {
            Log.d(TAG, "VoE start send failed");
        }
        voERunning = true;
        return 0;
    }
    public void SetCoder(){
        String[] mVoiceCodecsStrings = null;
        int index=0;
        mVoiceCodecsStrings = vieAndroidAPI.VoE_GetCodecs();
        for (int i = 0; i < mVoiceCodecsStrings.length; ++i) {
            if (mVoiceCodecsStrings[i].contains("ISAC")) {
                index=i;
                break;
            }
        }
        if (0 != vieAndroidAPI.VoE_SetSendCodec(voiceChannel, index)) {
            Log.d(TAG, "VoE set send codec failed");
        }
    }
    private void stopVoiceEngine() {
        // Stop send
        voERunning = false;
        if (0 != vieAndroidAPI.VoE_StopSend(voiceChannel)) {
            Log.d(TAG, "VoE stop send failed");
        }

        // Stop listen
        if (0 != vieAndroidAPI.VoE_StopListen(voiceChannel)) {
            Log.d(TAG, "VoE stop listen failed");
        }

        // Stop playout
        if (0 != vieAndroidAPI.VoE_StopPlayout(voiceChannel)) {
            Log.d(TAG, "VoE stop playout failed");
        }

        if (0 != vieAndroidAPI.VoE_DeleteChannel(voiceChannel)) {
            Log.d(TAG, "VoE delete channel failed");
        }
        voiceChannel = -1;

        // Terminate
        if (0 != vieAndroidAPI.VoE_Terminate()) {
            Log.d(TAG, "VoE terminate failed");
        }
    }
}
