package com.goldsand.collaboration.voice;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
    private NativeWebRtcContextRegistry re;
    private VoiceEngine voe;
    private int audioChannel=4;
    private int audioRxPort=11113;
    private boolean voeRunning=false;
    private int audioTxPort=11113;
    private String remoteIp="127.0.0.1";
    //private AudioManagerAndroid ad;
    private Button button1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1=(Button)findViewById(R.id.button1);
        re=new NativeWebRtcContextRegistry();
        
        re.register(getApplicationContext());
        voe=new VoiceEngine();
        initVoice();
        button1.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startCall();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    private void startCall()
    {
      //  this.remoteIp=Ip;
        startVoE();
        
    }
    private void initVoice()    
    {       
        check(voe.init() == 0, "Failed voe Init");
        this.audioChannel = voe.createChannel();
        check(audioChannel >= 0, "Failed voe CreateChannel");
        check(voe.setSpeakerVolume(204) == 0,
                "Failed setSpeakerVolume");
       // check(voe.setAecmMode(VoiceEngine.AecmModes.SPEAKERPHONE, false) == 0,
        //        "VoE set Aecm speakerphone mode failed");
       // AudioManager audioManager =
          //      ((AudioManager) this.getSystemService(this.AUDIO_SERVICE));
          //  audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        
        
        setAudioCodec(getIsacIndex());
        setRemoteIp();
        setAudioRxPort();
      //  setRemoteIp();
     //   setAudioRxPort();
        
    }
    private void stopCall()
    {
        
        stopVoe();
        
    }
    public void startVoE() {
        
        check(!voeRunning, "VoE already started");
        check(voe.startListen(audioChannel) == 0, "Failed StartListen");
        check(voe.startPlayout(audioChannel) == 0, "VoE start playout failed");
        check(voe.startSend(audioChannel) == 0, "VoE start send failed");
        voeRunning = true;
      }

      private void stopVoe() {
        check(voeRunning, "VoE not started");
        check(voe.stopSend(audioChannel) == 0, "VoE stop send failed");
        check(voe.stopPlayout(audioChannel) == 0, "VoE stop playout failed");
        check(voe.stopListen(audioChannel) == 0, "VoE stop listen failed");
        voeRunning = false;
        
      }
      private void check(boolean value, String message) {
          if (value) {
            return;
          }
          Log.e("WEBRTC-CHECK", message);
          AlertDialog alertDialog = new AlertDialog.Builder(this).create();
          alertDialog.setTitle("WebRTC Error");
          alertDialog.setMessage(message);
          alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,
              "OK",
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                  return;
                }
              }
                                );
          alertDialog.show();
        }
      public void dispose() {
          check(!voeRunning, "Engines must be stopped before dispose"); 
          check(voe.deleteChannel(audioChannel) == 0, "VoE delete channel failed");
          voe.dispose();
        }
      public void setAudioCodec(int codecNumber) {
        //  audioCodecIndex = codecNumber;
          CodecInst codec = voe.getCodec(codecNumber);
          check(voe.setSendCodec(audioChannel, codec) == 0, "Failed setSendCodec");
          codec.dispose();
        }
      public void setAudioRxPort() {
          check(voe.setLocalReceiver(audioChannel, audioRxPort) == 0,
              "Failed setLocalReceiver");
   
        }
      public void setRemoteIp() {
   
          UpdateSendDestination();
        }
      private void UpdateSendDestination() {

            check(voe.setSendDestination(audioChannel, audioTxPort,
                    remoteIp) == 0, "VoE set send destination failed");
   

        }
      public int getIsacIndex() {
          CodecInst[] codecs = defaultAudioCodecs();
          for (int i = 0; i < codecs.length; ++i) {
            if (codecs[i].name().contains("ISAC")) {
              return i;
            }
          }
          return 0;
        }
      private CodecInst[] defaultAudioCodecs() {
          CodecInst[] retVal = new CodecInst[voe.numOfCodecs()];
           for (int i = 0; i < voe.numOfCodecs(); ++i) {
            retVal[i] = voe.getCodec(i);
          }
          return retVal;
        }
}
