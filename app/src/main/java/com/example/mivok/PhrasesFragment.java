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
 * Use the {@link PhrasesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhrasesFragment extends Fragment {

    private MediaPlayer mediaPlayer;
    AudioManager audioManager;
    private ArrayList<Word> phrases;

    // When playing is complete this onCompletionListener's onCompletion method will be called
    private final MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            releaseMediaPlayer();
        }
    };

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


    public PhrasesFragment() {
        // Required empty public constructor
    }

  @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.word_list, container, false);


      phrases = new ArrayList<>(10);
      ArrayList<String> english = new ArrayList<>(10);
      ArrayList<String> miwok = new ArrayList<>(10);
      ArrayList<Integer> audio = new ArrayList<>(10);

      english.addAll(Arrays.asList("Where are you going?", "What is your name?", "My name is...", "How are you feeling?", "I’m feeling good.","Are you coming ?","Yes, I’m coming.","I’m coming.","Let’s go.","Come here."));

      miwok.addAll(Arrays.asList("minto wuksus","tinnә oyaase'nә","oyaaset...","michәksәs?","kuchi achit","әәnәs'aa?","hәә’ әәnәm","әәnәm","yoowutis","әnni'nem"));

      audio.addAll(Arrays.asList(R.raw.phrase_where_are_you_going,R.raw.phrase_what_is_your_name,R.raw.phrase_my_name_is,R.raw.phrase_how_are_you_feeling,R.raw.phrase_im_feeling_good,R.raw.phrase_are_you_coming,R.raw.phrase_yes_im_coming,R.raw.phrase_im_coming,R.raw.phrase_lets_go,R.raw.phrase_come_here));


      for (int i = 0; i < 10; i++) {
          phrases.add(new Word(english.get(i), miwok.get(i),audio.get(i)));
      }

      WordAdapter numbersAdapter = new WordAdapter(getActivity(), phrases,R.color.category_phrases);

      ListView listView = (ListView) rootView.findViewById(R.id.rootView);
      listView.setAdapter(numbersAdapter);

      listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

              Word word = phrases.get(position);
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