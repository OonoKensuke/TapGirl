package jp.lolipop.dcc.TapGirl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import jp.lolipop.dcc.lib.CBaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

public abstract class FbActivity extends CBaseActivity {
	private Session.StatusCallback mCallback = new Session.StatusCallback() {
		
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);			
		}
	};
	protected Session.StatusCallback getFbCallback() {
		return mCallback;
	}
	
	protected void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (state.isOpened()) {
			Log.v("info", "Logged In..");
			if (pendingPublishReauthorization && state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
				pendingPublishReauthorization = false;
				publishFacebookStory();
			}
		}
		else if (state.isClosed()) {
			Log.v("info", "Logged out..");
		}
	}
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;
	
	protected void publishFacebookStory() {
		Session session = Session.getActiveSession();
		
		if (session != null) {
			// Check for permission
			List<String> permissions = session.getPermissions();
			if (!isSubSetOf(PERMISSIONS, permissions)) {
				pendingPublishReauthorization = true;
				Session.NewPermissionsRequest newPermissionsRequest = new Session
						.NewPermissionsRequest(this, PERMISSIONS);
				session.requestNewPublishPermissions(newPermissionsRequest);
				return;
			}
			
			Bundle postParams = new Bundle();
			postParams.putString("name", getPublishName());
			postParams.putString("caption", getPublishCaption());
			postParams.putString("description", getPublishDescription());
			postParams.putString("link", getPublishLink());
			//postParams.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");
			
			Request.Callback callback = new Request.Callback() {
				
				@Override
				public void onCompleted(Response response) {
					if (response.getError() != null) {
						Log.d("error", "Request.Callback " + response.getError().getErrorMessage());
					}
					JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
					String postId = null;
					try {
						postId = graphResponse.getString("id");
					}
					catch (JSONException exp) {
						Log.d("exception", "JSON error " + exp.getMessage());
					}
					FacebookRequestError error = response.getError();
					//TestFacebook003Activity activity = TestFacebook003Activity.getInstance();
					if (error != null) {
						onPublishFail(error, null);
					}
					else {
						onPublishSucceed(postId, null);
					}
					
				}
			};
			
			Request request = new Request(session, "me/feed", postParams, HttpMethod.POST, callback);
			RequestAsyncTask task = new RequestAsyncTask(request);
			task.execute();
		}
	}
	
	protected void onPublishFail(FacebookRequestError error, Activity activity)
	{
		if (activity != null) {
			Toast.makeText(activity, error.getErrorMessage(), Toast.LENGTH_SHORT).show();		
		}
		else {
			Log.d("fbfail", error.getErrorMessage());
		}
	}
	
	protected void onPublishSucceed(String postId, Activity activity)
	{
		if (activity != null) {
			Toast.makeText(activity, postId, Toast.LENGTH_LONG).show();
		}
		else {
			Log.v("info", "fb publish " + postId);
		}
	}
	
	private boolean isSubSetOf(Collection<String> subset, Collection<String>superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				Log.v("info", string + " was not contained");
				return false;
			}
		}
		return true;
	}
	private UiLifecycleHelper mUiHelper = null;
	
	protected String getPublishName() {
		return "androidアプリからの投稿テスト２";
	}
	protected String getPublishCaption() {
		return "これはテストです";
	}
	protected String getPublishDescription() {
		return "すみませんが無視してください";
	}
	protected String getPublishLink() {
		return "https://developers.facebook.com/android";
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUiHelper = new UiLifecycleHelper(this, mCallback);
        mUiHelper.onCreate(savedInstanceState);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	mUiHelper.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onPause() {
    	super.onPause();
    	mUiHelper.onPause();
    }
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	mUiHelper.onDestroy();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
    	mUiHelper.onSaveInstanceState(outState);
    }
}
