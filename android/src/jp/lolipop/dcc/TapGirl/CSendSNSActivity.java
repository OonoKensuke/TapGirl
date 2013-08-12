package jp.lolipop.dcc.TapGirl;

import com.facebook.Session;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;
import twitter4j.conf.ConfigurationBuilder;
import jp.lolipop.dcc.lib.CBaseActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

public class CSendSNSActivity extends FbActivity implements View.OnClickListener {
	public static final int SNS_NONE = 0;
	public static final int SNS_Twitter = 1;
	public static final int SNS_Facebook = 2;

	private EditText mEdtText = null;
	private Button mBtnSend = null;
	TapGirlActivity mMainActivity = null;
	
	private int mSnsType = SNS_NONE;
	
	private void sendTwitter()
	{
		if (mMainActivity.getAtomTweet().get() > 0)
		{
			return;
		}
		String strTweet = mEdtText.getText().toString();
		Log.v("info", "strTweet = " + strTweet);
		{
			mMainActivity.getAtomTweet().incrementAndGet();
			String oauthAccessToken = TapGirlActivity.getSharedPreferences().getString(CDefines.TWITTER_PREF_KEY_TOKEN, "");
			Log.v("info", "oauthAccessToken = " + oauthAccessToken);
			String oAuthAccessTokenSecret = TapGirlActivity.getSharedPreferences().getString(CDefines.TWITTER_PREF_KEY_SECRET, "");
			Log.v("info", "oAuthAccessTokenSecret = " + oAuthAccessTokenSecret);

			ConfigurationBuilder confbuilder = new ConfigurationBuilder();
			twitter4j.conf.Configuration conf = confbuilder
								.setOAuthConsumerKey(CDefines.TWITTER_CONSUMER_KEY)
								.setOAuthConsumerSecret(CDefines.TWITTER_CONSUMER_SECRET)
								.setOAuthAccessToken(oauthAccessToken)
								.setOAuthAccessTokenSecret(oAuthAccessTokenSecret)
								.build();
			{
				AsyncTwitterFactory factory = new AsyncTwitterFactory(conf);
				AsyncTwitter twitter = factory.getInstance();
				twitter.addListener(new TwitterAdapter() {
					@Override
					public void updatedStatus(Status status)
					{
						Log.v("info", "Successfully updated the status to [" +
		                        status.getText() + "].");
						mMainActivity.getAtomTweet().decrementAndGet();
					}
					@Override
					public void onException(TwitterException exp, twitter4j.TwitterMethod method)
					{
						if (method == TwitterMethod.UPDATE_STATUS)
						{
							exp.printStackTrace();
							mMainActivity.getAtomTweet().decrementAndGet();
						}
						else {
							mMainActivity.getAtomTweet().decrementAndGet();
							throw new AssertionError("Should not happen");
						}
					}
				});
				Log.v("info", "before update");
				twitter.updateStatus(strTweet);
				Log.v("info", "after update");
			}
		}
	}
	
	private void sendFacebook()
	{
		Log.v("info", "CSendSNSActivity#sendFacebook");
		publishFacebookStory();
	}
	@Override
	protected String getPublishName() {
		return CDefines.SNS_APP_NAME;
	}
	@Override
	protected String getPublishCaption() {
		String strTweet = mEdtText.getText().toString();
		return strTweet;
	}
	@Override
	protected String getPublishDescription() {
		return null;
	}
	@Override
	protected String getPublishLink() {
		return CDefines.SNS_URL;
	}
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	mMainActivity = TapGirlActivity.getInstance();
    	LinearLayout mainLayout = new LinearLayout(this);
    	mainLayout.setOrientation(LinearLayout.VERTICAL);
    	setContentView(mainLayout);
    	
    	mEdtText = new EditText(this);
    	int pixels = (int)(100.0f * TapGirlActivity.getDensity());
    	mEdtText.setHeight(pixels);
    	mEdtText.setWidth(mMainActivity.getPixelsWidth());
    	mEdtText.setText(TapGirlActivity.getSNSString());
    	
    	mBtnSend = new Button(this);
    	mBtnSend.setText("送信");
//    	pixels = (int)(50.0f * CBaseActivity.getDensity());
//    	mBtnSend.setWidth(pixels);
    	pixels = (int)(25.0f * CBaseActivity.getDensity());
    	mBtnSend.setHeight(pixels);
    	mBtnSend.setOnClickListener(this);
    	
    	mainLayout.addView(mEdtText);
    	mainLayout.addView(mBtnSend);
    	{
    		Intent intent = getIntent();
    		mSnsType = intent.getIntExtra(CDefines.INTENT_EXTRA_SNSTYPE, SNS_NONE);
    		switch (mSnsType) {
    			case SNS_Facebook:
    			{
    				Session session = Session.getActiveSession();
    				if (session.isOpened()) {
    					Log.v("info", "open");
    				}
    				else {
    					Log.v("info", "close");
    				}
    			}
    				break;
    		}
    	}
    }
	@Override
	public void onClick(View v) {
		if (v.equals(mBtnSend))
		{
			switch (mSnsType) {
				case SNS_Twitter:
					sendTwitter();
					break;
				case SNS_Facebook:
					sendFacebook();
					break;
			}
		}
		
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

}
