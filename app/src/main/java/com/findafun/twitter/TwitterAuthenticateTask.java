package com.findafun.twitter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import twitter4j.auth.RequestToken;

/**
 * Created by bxdc46 on 12/23/2015.
 */
public class TwitterAuthenticateTask extends AsyncTask<String, String, RequestToken> {
    private Context mContext = null;

    public TwitterAuthenticateTask(Context context){
        mContext = context;
    }

    @Override
    protected void onPostExecute(RequestToken requestToken) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL()));
        mContext.startActivity(intent);
    }

    @Override
    protected RequestToken doInBackground(String... params) {
        return TwitterUtil.getInstance().getRequestToken();
    }
}
