package com.fluidcoding.brian.aide;

import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;
import java.util.PriorityQueue;

/**
 * Created by User on 10/31/2015.
 */
public class TextSpeak implements TextToSpeech.OnInitListener, Parcelable {
    private TextToSpeech txtS;
    private ArrayList<String> words;
//    Voice voice;

    public TextSpeak(Context c) {
        init(c);
        words = new ArrayList<>();
        //voice = new Voice("V1", Locale.US, Voice.QUALITY_HIGH, Voice.LATENCY_NORMAL, false, null);

    }

    public TextSpeak(Parcel p){
        words = p.createStringArrayList();
    }

    public void init(Context c){
        txtS = new TextToSpeech(c, this);
    }

    public void release(){
        txtS.shutdown();
    }

    // getter
    public ArrayList<String> getWords() {
        return words;
    }

    // setter
    public void setWords(ArrayList<String> words) {
        this.words = words;
    }

    // push to end
    public void addWord(String word) {
        words.add(word + " ");
    }

    // pop off end
    public void removeLastWord() {
        if (words.size() > 0) {
            words.remove(words.size() - 1);
        } else
            words.clear();
    }

    public void clearWords() {
        words.clear();
    }

    public void speak(String s) {
        s = s.toLowerCase();
        txtS.speak(s, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void speak() {
        String say = "";
        for (String s : words)
            say += (s.toLowerCase());

        Log.d("SAY: ", say);
        txtS.speak(say, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            txtS.setLanguage(Locale.US);
            txtS.setSpeechRate(.8f);
           /*
            if(Build.VERSION.SDK_INT>20){
                Voice v = new Voice("v1", Locale.US, Voice.QUALITY_VERY_HIGH, Voice.LATENCY_NORMAL, false, false );

            }
//            txtS.setVoice()
//            txtS.setVoice(new Voice(Voice.QUALITY_VERY_HIGH);'

*/
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (words.size() > 0) {
            dest.writeStringArray(words.toArray(new String[words.size()]));
        }
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public TextSpeak createFromParcel(Parcel source) {
            return new TextSpeak(source);
        }

        @Override
        public TextSpeak[] newArray(int size) {
            return new TextSpeak[0];
        }

    };
}