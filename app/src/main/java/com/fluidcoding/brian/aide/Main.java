package com.fluidcoding.brian.aide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Map;

public class Main extends AppCompatActivity implements Button.OnClickListener{

    TextSpeak speaker;
    boolean liveSpeak = false;
    private ArrayList<Button> sentence;
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

        speaker = new TextSpeak(this);
        sentence = new ArrayList<>();
        speakView = (LinearLayout)findViewById(R.id.speakView);
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
    protected void onPostResume() {
        liveSpeak = sp.getBoolean("auto_speak", false);
        super.onPostResume();
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
        if(sentence.size()!=0){
            speakView.removeViewAt(sentence.size()-1);
            sentence.remove(sentence.size()-1);
            speaker.removeLastWord();
        }
    }

    public void onSpeakClick(View v){
        if(sentence.size()!=0){
            speaker.speak();
        }
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
        sentence.add(wordView);
        speaker.addWord(text);
        speakView.addView(wordView);
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
