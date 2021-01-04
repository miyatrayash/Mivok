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
 * Use the {@link NumbersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NumbersFragment extends Fragment {

    private MediaPlayer mediaPlayer;
    AudioManager audioManager;
    private ArrayList<Word> numbers;

    // defining for which type of focus what should we do
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

    // When playing is complete this onCompletionListener's onCompletion method will be called
    private final MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            releaseMediaPlayer();
        }
    };

    public NumbersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.word_list, container, false);

        numbers = new ArrayList<>(10);
        ArrayList<String> english = new ArrayList<>(10);
        ArrayList<String> miwok = new ArrayList<>(10);
        ArrayList<Integer> images = new ArrayList<>(10);
        ArrayList<Integer> audio = new ArrayList<>(10);

        english.addAll(Arrays.asList("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"));

        miwok.addAll(Arrays.asList("lutti", "otiiko", "tolookosu", "oyyisa", "massokka", "temmokka", "kenekaku", "kawinta", "wo'e", "na'aacha"));

        images.addAll(Arrays.asList(R.drawable.number_one,R.drawable.number_two,R.drawable.number_three,R.drawable.number_four,R.drawable.number_five,R.drawable.number_six,R.drawable.number_seven,R.drawable.number_eight,R.drawable.number_nine,R.drawable.number_ten));

        audio.addAll(Arrays.asList(R.raw.number_one,R.raw.number_two,R.raw.number_three,R.raw.number_four,R.raw.number_five,R.raw.number_six,R.raw.number_seven,R.raw.number_eight,R.raw.number_nine,R.raw.number_ten));


        for (int i = 0; i < 10; i++) {
            numbers.add(new Word(english.get(i), miwok.get(i),images.get(i),audio.get(i)));
        }

        WordAdapter numbersAdapter = new WordAdapter(getActivity(), numbers,R.color.category_numbers);

        ListView listView = (ListView) rootView.findViewById(R.id.rootView);
        listView.setAdapter(numbersAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Word word = numbers.get(position);
                releaseMediaPlayer();

                boolean isGranted = requestAudioFocus();
                if (isGranted) {

                    mediaPlayer = MediaPlayer.create(getActivity(), word.getAudioResourceId());

                    mediaPlayer.start();

                    mediaPlayer.setOnCompletionListener(mCompletionListener);
                }
            }
        });

        return  rootView;
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

            // abandoning audio focus when it will be no longer needed
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