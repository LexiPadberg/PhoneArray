package com.wavesciences.phonearray;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.wavesciences.phonearray.databinding.FragmentFirstBinding;
import com.wavesciences.phonearray.helpers.PermissionsHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class FirstFragment extends Fragment {
    static final String TAG = FirstFragment.class.getSimpleName();
    private FragmentFirstBinding binding;

    AudioRecord recorder1;
    AudioRecord recorder2;

    boolean isRecording = false;


    private static final int SAMPLING_RATE_IN_HZ = 44100;

    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;

    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private Thread recordingThread1;
    private Thread recordingThread2;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {



        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    private int getPcmBufferSize() {
        int pcmBufSize =
                AudioRecord.getMinBufferSize(SAMPLING_RATE_IN_HZ, CHANNEL_CONFIG, AudioFormat.ENCODING_PCM_16BIT) + 8191;
        return pcmBufSize - (pcmBufSize % 8192);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonTestRecording.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                Log.d(TAG, "[buttonTestRecording] BINDING CLICKED");
                if (recorder1 == null || !isRecording) {
                    recorder1 = new AudioRecord(MediaRecorder.AudioSource.MIC,
                            SAMPLING_RATE_IN_HZ,
                            CHANNEL_CONFIG,
                            AUDIO_FORMAT,
                            getPcmBufferSize());

                    recorder2 = new AudioRecord(MediaRecorder.AudioSource.CAMCORDER,
                            SAMPLING_RATE_IN_HZ,
                            CHANNEL_CONFIG,
                            AUDIO_FORMAT,
                            getPcmBufferSize());


                    recorder1.startRecording();
                    recorder2.startRecording();
                    isRecording = true;


                    //Start the recording threads.
                    //TODO: Clean this up by encapsulating the thread method. Fine tune the buffer size.

                    recordingThread1 = new Thread(new Runnable() {
                        public void run() {

                            byte Data[] = new byte[getPcmBufferSize()];
                            Log.d(TAG, ">>>>>>>>>>> STARTING THREAD 1");

                            try (FileOutputStream os = new FileOutputStream(getContext().getExternalFilesDir(null) + "/record1.1.pcm")) {
                                long totalBytes = 0;

                                while(isRecording) {
                                    int bytesRead = recorder1.read(Data, 0, Data.length);
                                    if (bytesRead > 0) {
                                        totalBytes += bytesRead;
                                        os.write(Data, 0, bytesRead);
                                    }
                                }

                                os.flush();
                                Log.d(TAG, ">>>>>>>>>>> Total Bytes 1: " + totalBytes);
                            } catch (Exception e) {
                                Log.e(TAG, "[recordingThread1] Error saving recording", e);
                            }

                        }
                    });


                    recordingThread2 = new Thread(new Runnable() {
                        public void run() {
                            byte Data[] = new byte[getPcmBufferSize()];
                            long totalBytes = 0;
                            Log.d(TAG, ">>>>>>>>>>> STARTING THREAD 2");

                            try (FileOutputStream os = new FileOutputStream(getContext().getExternalFilesDir(null) + "/record2.1.pcm")) {
                                while(isRecording) {
                                    int bytesRead = recorder2.read(Data, 0, Data.length);

                                    if (bytesRead > 0) {
                                        totalBytes += bytesRead;
                                        os.write(Data, 0, bytesRead);
                                    }
                                }

                                Log.d(TAG, ">>>>>>>>>>> Total Bytes 2: " + totalBytes);
                                os.flush();
                            } catch (Exception e) {
                                Log.e(TAG, "[recordingThread1] Error saving recording", e);
                            }

                        }
                    });

                    Log.d(TAG, "[buttonTestRecording] Starting the Recording");
                    recordingThread1.start();
                    recordingThread2.start();

                } else {
                    Log.d(TAG, "[buttonTestRecording] Stopping the Recording");
                    isRecording = false;
                    recorder1.stop();
                    recorder1.release();
                    recorder2.stop();
                    recorder2.release();
                    recorder1 = null; //Release for GC
                    recorder2 = null; //Release for GC


                }




            }
        });

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}