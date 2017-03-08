package com.findafun.twitter;


import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.findafun.R;
import com.findafun.bean.events.Event;
import com.findafun.bean.gamification.GamificationDataHolder;
import com.findafun.servicehelpers.ShareServiceHelper;
import com.findafun.utils.FindAFunConstants;
import com.findafun.utils.PreferenceStorage;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Created by bxdc46 on 12/23/2015.
 */
public class TwitterActivity extends AppCompatActivity {
   public static final String TAG = TwitterActivity.class.getName();
    private TextView mpostText = null;
    private EditText postmessage = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter_post_layout);

       // postmessage = (EditText) findViewById(R.id.post_message);

       /* ActionBar actionbar = getActionBar();
        actionbar.setDisplayShowHomeEnabled(false);
        actionbar.setHomeButtonEnabled(false);
        //getActionBar().setIcon(this.getResources().getDrawable(R.drawable.ic_drawer));



        //TextView actionBarTitle = (TextView) view.findViewById(R.id.delivery_actionBarTextView);
       // actionBarTitle.setText("Phone Veri");

        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setCustomView(R.layout.fairplay_actionbar);

        View view = actionbar.getCustomView();
        //  = getLayoutInflater().inflate(R.layout.delivery_actionbar, null);
        ImageView backImage = (ImageView) view.findViewById(R.id.custom_back_image);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button postBtn = (Button) findViewById(R.id.post_btn);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                boolean loggedIn = sharedPreferences.getBoolean(FairplayConstants.TWITTER_LOGGEDIN,false);
                if(loggedIn) {
                    String message = getResources().getString(R.string.twitter_content);
                    if ((message != null) && !message.isEmpty()) {

                        new TwitterUpdateStatusTask().execute(message);
                    }else{
                        Toast.makeText(TwitterActivity.this,"Enter tweet to be shared", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(TwitterActivity.this, "Not logged In to Twitter",Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        });*/
        initControl();
    }

    private void initControl() {
        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(FindAFunConstants.TWITTER_CALLBACK_URL)) {
            String verifier = uri.getQueryParameter( FindAFunConstants.URL_TWITTER_OAUTH_VERIFIER );
            new TwitterGetAccessTokenTask().execute(verifier);
        } else
            new TwitterGetAccessTokenTask().execute("");
    }


    class TwitterGetAccessTokenTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String userName) {
           // mpostText.setText(Html.fromHtml("<b> Welcome " + userName + "</b>"));
            //String message = getResources().getString(R.string.share_content);
            Event event = GamificationDataHolder.getInstance().getmCurrentEvent();
            String message = "http://www.Hobbistan.com "+event.getEventName()+ "\n"+ event.getDescription();
            String shortText = "";
            if(message.length() > 140){
                shortText = message.substring(0,139);
                message = shortText;
            }

            if ((message != null) && !message.isEmpty()) {

                new TwitterUpdateStatusTask().execute(message);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            Twitter twitter = TwitterUtil.getInstance().getTwitter();
            RequestToken requestToken = TwitterUtil.getInstance().getRequestToken();
            String param0 = params[0];
            if ((param0 != null) && (param0.length() > 0)) {
                try {

                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, params[0]);
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(FindAFunConstants.PREFERENCE_TWITTER_OAUTH_TOKEN, accessToken.getToken());
                    editor.putString(FindAFunConstants.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, accessToken.getTokenSecret());

                    editor.commit();
                    //store TwitterLogged in
                    PreferenceStorage.saveTwitterLoggedIn(TwitterActivity.this,true);
                    return twitter.showUser(accessToken.getUserId()).getName();
                } catch (TwitterException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            } else {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String accessTokenString = sharedPreferences.getString(FindAFunConstants.PREFERENCE_TWITTER_OAUTH_TOKEN, "");
                String accessTokenSecret = sharedPreferences.getString(FindAFunConstants.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, "");
                AccessToken accessToken = new AccessToken(accessTokenString, accessTokenSecret);
                try {
                    TwitterUtil.getInstance().setTwitterFactory(accessToken);
                    return TwitterUtil.getInstance().getTwitter().showUser(accessToken.getUserId()).getName();
                } catch (TwitterException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }
    private void sendShareStatus(){
        ShareServiceHelper serviceHelper = new ShareServiceHelper(this);
        Event event = GamificationDataHolder.getInstance().getmCurrentEvent();
        if(event != null) {
            int eventId = Integer.parseInt(event.getId());
            int ruleid = 1;
            int ticketcount = 0;
            String activitydetail = "You have shared photo" + event.getEventName();
            serviceHelper.postShareDetails(String.format(FindAFunConstants.SHARE_EVENT_URL, eventId, Integer.parseInt(PreferenceStorage.getUserId(this)),
                    ruleid, Uri.encode(activitydetail), event.getEventLogo(), ticketcount), null);
        }
    }

    class TwitterUpdateStatusTask extends AsyncTask<String, String, Boolean> {

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                sendShareStatus();
                Toast.makeText(getApplicationContext(), "Tweet successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
            else {
                Toast.makeText(getApplicationContext(), "Tweet failed", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String accessTokenString = sharedPreferences.getString(FindAFunConstants.PREFERENCE_TWITTER_OAUTH_TOKEN, "");
                String accessTokenSecret = sharedPreferences.getString(FindAFunConstants.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, "");

                if ( ((accessTokenString != null) && (accessTokenString.length() > 0))
                   && ( (accessTokenSecret != null) && (accessTokenSecret.length() > 0))) {
                    AccessToken accessToken = new AccessToken(accessTokenString, accessTokenSecret);
                    Log.d(TAG,"Sending twitter message with length"+ params[0].length());
                    twitter4j.Status status = TwitterUtil.getInstance().getTwitterFactory().getInstance(accessToken).updateStatus(params[0]);
                    return true;
                }

            } catch (TwitterException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            return false;  //To change body of implemented methods use File | Settings | File Templates.

        }
    }

}
