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

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FamilyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FamilyFragment extends Fragment {

    private MediaPlayer mediaPlayer;
    AudioManager audioManager;
    private ArrayList<Word> family;

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
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:    //Loss of audio focus for a short time
                    //Pause playing the sound
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(0);
                    break;
            }
        }
    };
    public FamilyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.word_list, container, false);

        family = new ArrayList<>(10);
        ArrayList<String> english = new ArrayList<>(10);
        ArrayList<String> miwok = new ArrayList<>(10);
        ArrayList<Integer> images = new ArrayList<>(10);
        ArrayList<Integer> audio = new ArrayList<>(10);


        miwok.addAll(Arrays.asList("әpә", "әṭa", "angsi", "tune", "taachi", "chalitti", "teṭe", "kolliti", "ama", "paapa"));

        english.addAll(Arrays.asList("father", "mother", "son", "daughter", "older brother", "younger brother", "older sister", "younger sister", "grandmother", "grandfather"));

        images.addAll(Arrays.asList(R.drawable.family_father,R.drawable.family_mother,R.drawable.family_son,R.drawable.family_daughter,R.drawable.family_older_brother,R.drawable.family_younger_brother,R.drawable.family_older_sister,R.drawable.family_younger_sister,R.drawable.family_grandfather,R.drawable.family_grandmother));

        audio.addAll(Arrays.asList(R.raw.family_father,R.raw.family_mother,R.raw.family_son,R.raw.family_daughter,R.raw.family_older_brother,R.raw.family_younger_brother,R.raw.family_older_sister,R.raw.family_younger_sister,R.raw.family_grandfather,R.raw.family_grandmother));


        for (int i = 0; i < 10; i++) {
            family.add(new Word(english.get(i), miwok.get(i),images.get(i),audio.get(i)));
        }

        WordAdapter numbersAdapter = new WordAdapter(getActivity(), family,R.color.category_family);

        ListView listView = (ListView) rootView.findViewById(R.id.rootView);
        listView.setAdapter(numbersAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Word word = family.get(position);
                releaseMediaPlayer();
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

        return  result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

}