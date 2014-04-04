package com.sbag.whatsmynextarticle.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        // UI modifications...apply custom font...unfortunately this still cant be done
        // via style properties :(
        Typeface font = Typeface.createFromAsset(getAssets(), "SenticoSansDT-Regular.otf");
        TextView tvQuestion = (TextView)findViewById(R.id.tv_question);
        tvQuestion.setTypeface(font);

        TextView tvPercentageTopic1 = (TextView)findViewById(R.id.tv_topic1Percentage);
        TextView tvPercentageTopic2 = (TextView)findViewById(R.id.tv_topic2Percentage);
        TextView tvPercentageTopic3 = (TextView)findViewById(R.id.tv_topic3Percentage);
        tvPercentageTopic1.setTypeface(font);
        tvPercentageTopic2.setTypeface(font);
        tvPercentageTopic3.setTypeface(font);

        Button topic1 = (Button)findViewById(R.id.but_topic1);
        Button topic2 = (Button)findViewById(R.id.but_topic2);
        Button topic3 = (Button)findViewById(R.id.but_topic3);
        Button butVote = (Button)findViewById(R.id.but_vote);
        topic1.setTypeface(font);
        topic2.setTypeface(font);
        topic3.setTypeface(font);
        butVote.setTypeface(font);

        // apply fomt to action bar
        int titleId = getResources().getIdentifier("action_bar_title", "id",
                "android");
        TextView abTitle = (TextView) findViewById(titleId);
        abTitle.setTypeface(font);

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

    // ------------------------------------------------------------------------
    // ui actions
    // ------------------------------------------------------------------------

    /**
     * Open twitter...
     * @param view
     */
    public void onSelectTwitterButton(View view)
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/_flomueller"));
        startActivity(browserIntent);
    }

    /**
     * Open Facebook...
     * @param view
     */
    public void onSelectFacebookButton(View view)
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/florian.muller.961993"));
        startActivity(browserIntent);
    }

    /**
     * Open GooglePlus...
     * @param view
     */
    public void onSelectGooglePlusButton(View view)
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/u/0/101285869711757072641/posts"));
        startActivity(browserIntent);
    }

    /**
     * Open JAX enter...
     * @param view
     */
    public void onSelectJaxEnter(View view)
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://jaxenter.com/"));
        startActivity(browserIntent);
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
        // duplicate vote check...lightweight...
        SharedPreferences prefs = getSharedPreferences("WhatsMyNextArticle", 0);
        boolean voted = prefs.getBoolean("voted", false);
        if (voted)
        {
            TextView tvMessage = (TextView) findViewById(R.id.tv_message);
            tvMessage.setVisibility(View.VISIBLE);
            tvMessage.setText(getText(R.string.messageHackerAlert));

            startAnimation(tvMessage);
        }
        else
        {
            // avoid hacker alert...
            prefs.edit().putBoolean("voted",true).commit();

            // topic selected?
            if (currentlySelectedButton == null)
            {
                TextView tvMessage = (TextView) findViewById(R.id.tv_message);
                tvMessage.setVisibility(View.VISIBLE);
                tvMessage.setText(getText(R.string.messageSelectTopic));

                startAnimation(tvMessage);
            } else
            {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Topic");
                switch (currentlySelectedButton.getId())
                {
                    case R.id.but_topic1:
                    {
                        query.whereEqualTo("objectId", "CrCEljQhGZ");
                        break;
                    }
                    case R.id.but_topic2:
                    {
                        query.whereEqualTo("objectId", "P5FTIcwpt1");
                        break;
                    }
                    case R.id.but_topic3:
                    {
                        query.whereEqualTo("objectId", "P7tZd805iQ");
                        break;
                    }
                }

                query.findInBackground(new FindCallback<ParseObject>()
                {
                    @Override
                    public void done(List<ParseObject> parseObjects, ParseException e)
                    {
                        ParseObject topic = parseObjects.get(0);
                        topic.put("votes", (Integer) topic.get("votes") + 1);
                        topic.saveInBackground(new SaveCallback()
                        {
                            @Override
                            public void done(ParseException e)
                            {
                                // display message...
                                TextView tvAuthorBox = (TextView) findViewById(R.id.tv_question);

                                AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
                                tvAuthorBox.startAnimation(fadeOut);
                                fadeOut.setDuration(500);
                                fadeOut.setFillAfter(true);

                                tvAuthorBox.setText(getText(R.string.text_thanx));
                                AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
                                tvAuthorBox.startAnimation(fadeIn);
                                fadeIn.setDuration(500);
                                fadeIn.setFillAfter(true);
                                fadeIn.setStartOffset(500 + fadeOut.getStartOffset());

                                // display voting results...
                                displayVotingResults();
                            }
                        });
                    }
                });
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

    private void displayVotingResults()
    {
        // set certain layouts to visible/invisible...
        LinearLayout buttonContainer = (LinearLayout)findViewById(R.id.ll_buttonContainer);
        LinearLayout resultContainer = (LinearLayout)findViewById(R.id.ll_votingResult);

        buttonContainer.setVisibility(View.GONE);
        resultContainer.setVisibility(View.VISIBLE);

        Button butSend = (Button)findViewById(R.id.but_vote);
        Button butFollow = (Button)findViewById(R.id.but_follow);
        butSend.setVisibility(View.GONE);
        butFollow.setVisibility(View.VISIBLE);

        // get votin results from server...
        final Map<String,Integer> votingResult = new HashMap<String, Integer>();

        // fetch votes...
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Topic");

        query.findInBackground(new FindCallback<ParseObject>()
        {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e)
            {
                for (ParseObject topic : parseObjects)
                {
                    String objectId = topic.getObjectId();
                    int votes = (Integer) topic.get("votes");

                    votingResult.put(objectId, votes);
                }

                DecimalFormat df = new DecimalFormat("##.00");

                int springVotes = votingResult.get("CrCEljQhGZ");
                int patternVotes = votingResult.get("P5FTIcwpt1");
                int fragmentVotes = votingResult.get("P7tZd805iQ");

                // do some complex maths... ;)
                int totalVotes = springVotes + patternVotes +fragmentVotes;
                float springpercentage = (float)springVotes/(float)totalVotes;
                float patternpercentage = (float)patternVotes/(float)totalVotes;
                float fragmentpercentage = (float)fragmentVotes/(float)totalVotes;

                LinearLayout topic1Percentage = (LinearLayout)findViewById(R.id.topic1_percentage);
                LinearLayout topic1CounterPercentage = (LinearLayout)findViewById(R.id.topic1_counterpercentage);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)(topic1Percentage.getLayoutParams());
                params.weight = springpercentage;
                topic1Percentage.setLayoutParams(params);
                params = (LinearLayout.LayoutParams)(topic1CounterPercentage.getLayoutParams());
                params.weight = 1-springpercentage;
                topic1CounterPercentage.setLayoutParams(params);
                TextView tvTopic1Percentage = (TextView)findViewById(R.id.tv_topic1Percentage);
                tvTopic1Percentage.setText(""+df.format(springpercentage*100) +" %");

                LinearLayout topic2Percentage = (LinearLayout)findViewById(R.id.topic2_percentage);
                LinearLayout topic2CounterPercentage = (LinearLayout)findViewById(R.id.topic2_counterpercentage);
                params = (LinearLayout.LayoutParams)(topic2Percentage.getLayoutParams());
                params.weight = patternpercentage;
                topic2Percentage.setLayoutParams(params);
                params = (LinearLayout.LayoutParams)(topic2CounterPercentage.getLayoutParams());
                params.weight = 1-patternpercentage;
                topic2CounterPercentage.setLayoutParams(params);
                TextView tvTopic2Percentage = (TextView)findViewById(R.id.tv_topic2Percentage);
                tvTopic2Percentage.setText(""+df.format(patternpercentage*100) +" %");

                LinearLayout topic3Percentage = (LinearLayout)findViewById(R.id.topic3_percentage);
                LinearLayout topic3CounterPercentage = (LinearLayout)findViewById(R.id.topic3_counterpercentage);
                params = (LinearLayout.LayoutParams)(topic3Percentage.getLayoutParams());
                params.weight = fragmentpercentage;
                topic3Percentage.setLayoutParams(params);
                params = (LinearLayout.LayoutParams)(topic3CounterPercentage.getLayoutParams());
                params.weight = 1-fragmentpercentage;
                topic3CounterPercentage.setLayoutParams(params);
                TextView tvTopic3Percentage = (TextView)findViewById(R.id.tv_topic3Percentage);
                tvTopic3Percentage.setText(""+df.format(fragmentpercentage*100) +" %");
            }
        });
    }

    /**
     * Animate textview...
     * @param textViewToAnimate
     */
    private void startAnimation(TextView textViewToAnimate)
    {
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        textViewToAnimate.startAnimation(fadeIn);
        textViewToAnimate.startAnimation(fadeOut);
        fadeIn.setDuration(2200);
        fadeIn.setFillAfter(true);
        fadeOut.setDuration(1200);
        fadeOut.setFillAfter(true);
        fadeOut.setStartOffset(3200 + fadeIn.getStartOffset());
    }

}
