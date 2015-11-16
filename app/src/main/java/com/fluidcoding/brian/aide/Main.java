package com.fluidcoding.brian.aide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Map;

public class Main extends AppCompatActivity implements Button.OnClickListener{

    TextSpeak speaker;
    boolean liveSpeak = false;
    private HorizontalScrollView scrollSpeakView;
    private LinearLayout speakView;
    GridLayout tblWords;
    WordBase dbHelper;
    SQLiteDatabase dbHandle;
    ArrayList<Button> btns;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.d("Lifecycle", "OnCreate");

        speakView = (LinearLayout)findViewById(R.id.speakView);
        scrollSpeakView = (HorizontalScrollView)findViewById(R.id.horizontalScrollView);
        tblWords = (GridLayout)findViewById(R.id.controlGrid);
        btns = new ArrayList<>();

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sp.registerOnSharedPreferenceChangeListener(new PrefChanged());
        Map<String, ?> keys = sp.getAll();
        if(keys.isEmpty()){
            sp.edit().putBoolean("auto_speak", false).apply();
        }else {
            liveSpeak=(Boolean)keys.get("auto_speak");
            /*Iterator<String> keyIt = keys.keySet().iterator();
            while (keyIt.hasNext()) {
                String s = keyIt.next();
                Log.d("Prefs: ", s + " : " + keys.get(s));
            }
           */
        }

        // Load last sentence
        if(savedInstanceState!=null && savedInstanceState.containsKey("speaker")){
            speaker = savedInstanceState.getParcelable("speaker");
            try{
                speaker.init(this);
                addSentenceViews();
            }
            catch(NullPointerException ex){speaker = new TextSpeak(this);}
        }
        else {
            speaker = new TextSpeak(this);
        }


        dbHelper = new WordBase(this);
        dbHandle = dbHelper.getWritableDatabase();


        Cursor curse = dbHandle.query("WORDS", new String[]{"ID", "TYPE", "DISPLAY_TEXT"}, null, null, null, null, null, null);
//        Cursor curse = dbHandle.query("WORDS", null, null, null, null, null, null);

        Log.d("Cursor", String.valueOf(curse.getCount()));

        Button b;
        while(curse.moveToNext()){
            int i=0;
            while (i<curse.getColumnCount()) {
                Log.d("DB: ", String.valueOf(curse.getString(i)));
                if(i%3==2){
                    b = new Button(this);
//                    params = new TableRow.LayoutParams();
                   /* GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.columnSpec = GridLayout.spec(row);
                    params.rowSpec = GridLayout.spec(row);
                    params.setGravity(Gravity.CENTER);
                    params.width = GridLayout.LayoutParams.WRAP_CONTENT;
                    params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                    b.setLayoutParams(params);
                    */
                    b.setOnClickListener(this);
//                    b.setLayoutParams(new TableRow.LayoutParams(1));
                    b.setText(curse.getString(i));

                    btns.add(b);
                    tblWords.addView(b);
                }
                i++;
            }
        }

        curse.close();
    }

    @Override
    protected void onStart() {
        liveSpeak = sp.getBoolean("auto_speak", false);

        Log.d("Lifecycle", "OnStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d("Lifecycle", "OnResume");
        super.onResume();
    }

    @Override
    protected void onPostResume() {
        Log.d("Lifecycle", "OnPostResume");
        super.onPostResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d("Lifecycle", "OnPause");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d("Lifecycle", "OnStop");
        speaker.release();
    }

    @Override
    protected void onSaveInstanceState(Bundle saveInstanceState){
        Log.d("Lifecycle", "OnSaveInstanceState");
        if(speaker.getWords().size()>0)
            saveInstanceState.putParcelable("speaker", speaker);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // call settings activity
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onDeleteLastClick(View v){
        int wordSize = speaker.getWords().size();
        if(wordSize!=0){
            speakView.removeViewAt(wordSize-1);
            speaker.removeLastWord();
        }
    }

    public void onSpeakClick(View v){
        if(speaker.getWords().size()!=0){
            speaker.speak();
        }
    }

    public void addSentenceViews(){
        for(String word: speaker.getWords()) {
            Button wordView = new Button(this);
            //wordView.setLayoutParams(b.getLayoutParams());
            wordView.setText(word);
            speakView.addView(wordView);
        }
        scrollSpeakView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollSpeakView.fullScroll(View.FOCUS_RIGHT);
            }
        }, 100);

    }

    @Override
    public void onClick(View v) {
        Button b = (Button)v;
        String text = b.getText().toString();
        if(liveSpeak)
            speaker.speak( text );


        Button wordView = new Button(this);
        wordView.setLayoutParams(b.getLayoutParams());
        wordView.setText(text);
        speaker.addWord(text);
        speakView.addView(wordView);
        scrollSpeakView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollSpeakView.fullScroll(View.FOCUS_RIGHT);
            }
        }, 100);
    }

    public class PrefChanged implements SharedPreferences.OnSharedPreferenceChangeListener{
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Log.d("PREF CHANG:", key);
            if(key.equals("auto_speak")){
                liveSpeak = sharedPreferences.getBoolean(key, false);
            }
        }
    }
}
