package com.sbag.whatsmynextarticle.app;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;
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

public class MainActivity extends Activity
{

    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private Button currentlySelectedButton = null; // keep track off current selection

    // ------------------------------------------------------------------------
    // public usage
    // ------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI modifications...
        Typeface font = Typeface.createFromAsset(getAssets(), "SenticoSansDT-Regular.otf");
        TextView tvQuestion = (TextView)findViewById(R.id.tv_question);
        tvQuestion.setTypeface(font);

        Button topic1 = (Button)findViewById(R.id.but_topic1);
        Button topic2 = (Button)findViewById(R.id.but_topic2);
        Button topic3 = (Button)findViewById(R.id.but_topic3);
        topic1.setTypeface(font);
        topic2.setTypeface(font);
        topic3.setTypeface(font);

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

    /**
     * Topic selected...display checkbox...
     * @param view
     */
    public void selectTopic(View view)
    {
        // reset all icons...
        Button butSpring = (Button)findViewById(R.id.but_topic1);
        Drawable icon = getResources().getDrawable(R.drawable.spring_60);
        butSpring.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);

        Button butUIPatterns = (Button)findViewById(R.id.but_topic2);
        icon = getResources().getDrawable(R.drawable.uipatterns_60);
        butUIPatterns.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);

        Button butFragments = (Button)findViewById(R.id.but_topic3);
        icon = getResources().getDrawable(R.drawable.fragments_60);
        butFragments.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);

        // apply checkmark...
        currentlySelectedButton = (Button)view;
        icon = getResources().getDrawable(R.drawable.check_60);
        currentlySelectedButton.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
    }

    public void vote(final View view)
    {
        // topic selected?
        if (currentlySelectedButton == null)
        {
            TextView tvMessage = (TextView)findViewById(R.id.tv_message);
            tvMessage.setVisibility(View.VISIBLE);
            tvMessage.setText(getText(R.string.messageSelectTopic));

            AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
            AlphaAnimation fadeOut = new AlphaAnimation( 1.0f , 0.0f ) ;
            tvMessage.startAnimation(fadeIn);
            tvMessage.startAnimation(fadeOut);
            fadeIn.setDuration(2200);
            fadeIn.setFillAfter(true);
            fadeOut.setDuration(1200);
            fadeOut.setFillAfter(true);
            fadeOut.setStartOffset(3200+fadeIn.getStartOffset());
        }
        else
        {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Topic");
            switch (currentlySelectedButton.getId())
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
                topic.put("votes", (Integer) topic.get("votes") + 1);
                topic.saveInBackground(new SaveCallback()
                {
                    @Override
                    public void done(ParseException e)
                    {
                        // display message...
                        TextView tvAuthorBox = (TextView)findViewById(R.id.tv_question);

                        AlphaAnimation fadeOut = new AlphaAnimation( 1.0f , 0.0f ) ;
                        tvAuthorBox.startAnimation(fadeOut);
                        fadeOut.setDuration(500);
                        fadeOut.setFillAfter(true);

                        tvAuthorBox.setText(getText(R.string.text_thanx));
                        AlphaAnimation fadeIn = new AlphaAnimation( 0.0f , 1.0f ) ;
                        tvAuthorBox.startAnimation(fadeIn);
                        fadeIn.setDuration(500);
                        fadeIn.setFillAfter(true);
                        fadeIn.setStartOffset(500+fadeOut.getStartOffset());
                    }
                });
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
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
