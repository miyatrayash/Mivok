package com.example.mivok;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class ColorsFragment extends Fragment {

    private MediaPlayer mediaPlayer;
    AudioManager audioManager;
    private ArrayList<Word> colours;

    private final MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            releaseMediaPlayer();
        }
    };

    private final AudioManager.OnAudioFocusChangeListener changeListener = new AudioManager.OnAudioFocusChangeListener() {

        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    //restart/resume your sound
                    mediaPlayer.start();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    //Loss of audio focus for a long time
                    //Stop playing the sound
                    releaseMediaPlayer();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:            //Loss of audio focus for a short time
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    //Pause playing the sound
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(0);
                    break;
            }
        }
    };


    public ColorsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.word_list, container, false);

        TabLayout tabLayout = (TabLayout) inflater.inflate(R.layout.activity_main, container, false).findViewById(R.id.tabs);
        tabLayout.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.category_colors));

        colours = new ArrayList<>(8);
        ArrayList<String> english = new ArrayList<>(8);
        ArrayList<String> miwok = new ArrayList<>(8);
        ArrayList<Integer> images = new ArrayList<>(8);
        ArrayList<Integer> audio = new ArrayList<>(8);


        english.addAll(Arrays.asList("red", "green", "brown", "gray", "black", "white", "dusty yellow", "mustard yellow"));

        miwok.addAll(Arrays.asList("weṭeṭṭi", "chokokki", "ṭakaakki", "ṭopoppi", "kululli", "kelelli", "ṭopiisә", "chiwiiṭә"));

        images.addAll(Arrays.asList(R.drawable.color_red, R.drawable.color_green, R.drawable.color_brown, R.drawable.color_gray, R.drawable.color_black, R.drawable.color_white, R.drawable.color_dusty_yellow, R.drawable.color_mustard_yellow));

        audio.addAll(Arrays.asList(R.raw.color_red, R.raw.color_green, R.raw.color_brown, R.raw.color_gray, R.raw.color_black, R.raw.color_white, R.raw.color_dusty_yellow, R.raw.color_mustard_yellow));


        for (int i = 0; i < 8; i++)
            colours.add(new Word(english.get(i), miwok.get(i), images.get(i), audio.get(i)));


        WordAdapter numbersAdapter = new WordAdapter(getActivity(), colours, R.color.category_colors);


        ListView listView = (ListView) rootView.findViewById(R.id.rootView);

        listView.setAdapter(numbersAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                releaseMediaPlayer();

                Word word = colours.get(position);

                boolean isGranted = requestAudioFocus();
                if (isGranted) {

                    mediaPlayer = MediaPlayer.create(getActivity(), word.getAudioResourceId());

                    mediaPlayer.start();

                    mediaPlayer.setOnCompletionListener(mCompletionListener);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    private void releaseMediaPlayer() {

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;

            audioManager.abandonAudioFocus(changeListener);
        }
    }

    private boolean requestAudioFocus() {

        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        int result = audioManager.requestAudioFocus(changeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

}