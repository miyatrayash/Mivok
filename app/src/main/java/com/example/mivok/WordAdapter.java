package com.example.mivok;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class WordAdapter extends ArrayAdapter<Word> {

    private  static final String LOG_TAG = WordAdapter.class.getSimpleName();
    private  final int mBackgroundColor;
    public WordAdapter(Activity numbersActivity, ArrayList<Word> words, int backgroundColor) {
        super(numbersActivity, 0,words);
        mBackgroundColor =backgroundColor;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }


        Word currentWord = getItem(position);

        TextView miwokTextView = (TextView) listItemView.findViewById(R.id.mivok_text_view);
        miwokTextView.setText(currentWord.getMiwokTranslation());


        TextView defaultTextView = (TextView) listItemView.findViewById(R.id.english_text_view);
        defaultTextView.setText(currentWord.getDefaultTranslation());

        ImageView imageView = (ImageView) listItemView.findViewById(R.id.image);

        if(currentWord.getImageResourceId() == 0) {
            imageView.setVisibility(View.GONE);
        }
        else {
            imageView.setImageResource(currentWord.getImageResourceId());
        }


        View textContainer = (View) listItemView.findViewById(R.id.text_container);
        int color = ContextCompat.getColor(getContext(), mBackgroundColor);
        textContainer.setBackgroundColor(color);

        View playButton = (View) listItemView.findViewById(R.id.play);
        playButton.setBackgroundColor(color);


        return listItemView;
    }
}