package com.sbag.whatsmynextarticle.app;

import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Parse.initialize(this, "4CfbC5rUBuNC1FmfkJi3f56KAgn586Lwg2ibGPgf", "aaOeAEqueum3j80Lh36BXq74GfaBV2gdwYJRiazP");
        Log.i("MAIN_ACTIVITY", "Hello Parse! App initialized successfully...!");

        // check whether user needs to be created...:
        String deviceId = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        ParseUser.logInInBackground(deviceId,"",new LogInCallback()
        {
            @Override
            public void done(ParseUser parseUser, ParseException e)
            {
                if (parseUser != null)
                {
                    Toast.makeText(getApplicationContext(),"User logged in successfully...",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    signupSilently();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Create three voting topics...used for JavaMagazin demo topic
     * generation...
     * @param view
     */
    public void createTopics(View view)
    {
        ParseObject topic1 = new ParseObject("Topic");
        topic1.put("name", "Spring für Android");
        topic1.put("description", "Kurzeinführung in die Verwendung von Spring & Android.");
        topic1.put("votes", 0);
        topic1.saveInBackground();

        ParseObject topic2 = new ParseObject("Topic");
        topic2.put("name", "Android UI Patterns");
        topic2.put("description", "Einfache UI Patterns die das Leben leichter machen.");
        topic2.put("votes", 0);
        topic2.saveInBackground();

        ParseObject topic3 = new ParseObject("Topic");
        topic3.put("name", "Android Fragments");
        topic3.put("description", "Fragment oder Aktivität? Wir bringen Licht ins Dunkle...");
        topic3.put("votes", 0);
        topic3.saveInBackground(new SaveCallback()
        {
            @Override
            public void done(ParseException e)
            {
                Toast.makeText(getApplicationContext(),"Saved topic3 successfully...",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void vote(View view)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Topic");
        switch (view.getId())
        {
            case R.id.but_topic1:
            {
                query.whereEqualTo("name", "Spring für Android");
                break;
            }
            case R.id.but_topic2:
            {
                query.whereEqualTo("name", "Android UI Patterns");
                break;
            }
            case R.id.but_topic3:
            {
                query.whereEqualTo("name", "Android Fragments");
                break;
            }
        }

        try
        {
            List<ParseObject> result = query.find();
            ParseObject topic = result.get(0);
            topic.put("votes",(Integer)topic.get("votes")+1);
            topic.save();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void signupSilently()
    {
        String deviceId = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        ParseUser user = new ParseUser();
        user.setUsername(deviceId);
        user.setPassword("");
        user.put("displayName", "tbd");

        // update user...
        user.saveInBackground();
        user.signUpInBackground(new SignUpCallback()
        {
            public void done(ParseException e)
            {
                if (e == null)
                {
                    Toast.makeText(getApplicationContext(),"User created successfully...",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
