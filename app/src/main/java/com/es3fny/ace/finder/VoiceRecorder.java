package com.es3fny.ace.finder;

import android.media.MediaRecorder;
import java.io.IOException;

public class VoiceRecorder {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;

    private MediaRecorder recorder = null;

    public void recording(String fileName) {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException ignore) {

        }

        recorder.start();
    }

    public void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }
}
