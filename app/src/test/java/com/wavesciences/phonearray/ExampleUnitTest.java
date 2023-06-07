package com.wavesciences.phonearray;

import static com.wavesciences.phonearray.ManageRecordings.AUDIO_FORMAT;
import static com.wavesciences.phonearray.ManageRecordings.SAMPLING_RATE_IN_HZ;

import org.junit.Test;

import static org.junit.Assert.*;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
public void startRecordingIsCorrect(){
    AddRecordings addRecordings = new AddRecordings();
    int pcmBufferSize = getPcmBufferSize();

    AudioRecord recorderTest = new AudioRecord(MediaRecorder.AudioSource.MIC,
            44100,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            pcmBufferSize);

        byte[] buffer = new byte[pcmBufferSize];

        // Start recording
        recorderTest.startRecording();


        // Read the recorded data
        int bytesRead = recorderTest.read(buffer, 0, pcmBufferSize);

        // Stop recording
        recorderTest.stop();

        // Assert that data was recorded successfully
        assert(bytesRead != AudioRecord.ERROR_INVALID_OPERATION);

        // Assert that some data was actually read
        assert(bytesRead > 0);


    }
    private int getPcmBufferSize() {
        int pcmBufSize = AudioRecord.getMinBufferSize(
                44100,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
        ) + 8191;
        return pcmBufSize - (pcmBufSize % 8192);
    }



}

//test viewmodels and data
//test every method??