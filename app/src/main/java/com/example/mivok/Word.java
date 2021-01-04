package com.example.mivok;

public class Word {

    private final String mDefaultTranslation;
    private final String mMiwokTranslation;
    private final int mImageResourceId;
    private final int mAudioResourceId;
    public  Word(String defaultTranslation, String miwokTranslation,int imageResourceId, int audioResourceId){
        mDefaultTranslation = defaultTranslation;
        mMiwokTranslation = miwokTranslation;
        mImageResourceId = imageResourceId;
        mAudioResourceId = audioResourceId;
    }

    public  Word(String defaultTranslation, String miwokTranslation,int audioResourceId) {
        this(defaultTranslation,miwokTranslation,0,audioResourceId);
    }

    public String getDefaultTranslation() {
        return mDefaultTranslation;
    }

    public String getMiwokTranslation() {
        return mMiwokTranslation;
    }

    public int getImageResourceId() { return mImageResourceId; }

    public int getAudioResourceId() {
        return mAudioResourceId;
    }
}
