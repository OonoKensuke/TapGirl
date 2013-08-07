package jp.lolipop.dcc.TapGirl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.CRC32;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterMethod;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import jp.lolipop.dcc.lib.CBaseActivity;
import jp.lolipop.dcc.lib.CVec2D;
import jp.lolipop.dcc.lib.MyGLUtil;
import jp.lolipop.dcc.lib.MyToggleButton;
import jp.lolipop.dcc.*;

import jp.lolipop.dcc.lib.IGLRenderer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class TapGirlActivity extends CBaseActivity {
	private GLSurfaceView mGLSurfaceView = null;
	private MyRenderer mRenderer = null;

	//****** UI *******
	private ImageButton mBtnTweet = null;
	private ImageButton mBtnMoreApps = null;
	private ImageButton mBtnFacebook = null;

	private MyToggleButton mBtnToggleSound = null;
	public MyToggleButton getBtnToggleSound() {
		return mBtnToggleSound;
	}
	private TextView mCountLabel = null;
	public TextView getCountLabel() {
		return mCountLabel;
	}
	private TextView mRoundLabel = null;
	public TextView getmRoundLabel() {
		return mRoundLabel;
	}
	private void initUI()
	{
		RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		RelativeLayout uiLayout = new RelativeLayout(this);
		setmUILayout(uiLayout);
		addContentView(getUILayout(), layout);
		CChangeData changeData = CChangeData.getInstance();
		//****** ラベル *******
		//****** countLabel *******
		//座標とサイズはXCodeから
		float xOfIOS = 130;
		float yOfIOS = 40;
		float widthOfIOS = 160;
		float heightOfIOS = 61;
		{
			int iLen = (int)(changeData.getRestLength());
			String strRest = String.valueOf(iLen);
			mCountLabel = initLabel(xOfIOS, yOfIOS, widthOfIOS, heightOfIOS, 42.0f, Gravity.RIGHT | Gravity.TOP, strRest);
		}
		//****** roundLabel *******
		xOfIOS = 213;
		yOfIOS = 148;
		widthOfIOS = 80;
		heightOfIOS = 21;
		{
			int loopNum = changeData.getLoopNumber();
			String strLoop = loopNum + "周目";
			mRoundLabel = initLabel(xOfIOS, yOfIOS, widthOfIOS, heightOfIOS, 17.0f, Gravity.LEFT | Gravity.TOP, strLoop);
		}
		//****** ♪ *******
		xOfIOS = 14;
		yOfIOS = 10;
		widthOfIOS = 42;
		heightOfIOS = 21;
		initLabel(xOfIOS, yOfIOS, widthOfIOS, heightOfIOS, 24.0f, Gravity.LEFT | Gravity.TOP, "♪");

		//****** ボタン *******
		//****** Twitter *******
		xOfIOS = 5;
		yOfIOS = 39;
		widthOfIOS = 31;
		heightOfIOS = 31;
		mBtnTweet = initButton(xOfIOS, yOfIOS, widthOfIOS, heightOfIOS, "graphic_twitter1", true);
		//****** facebook *******
		xOfIOS = 6;
		yOfIOS = 73;
		widthOfIOS = 29;
		heightOfIOS = 29;
		mBtnFacebook = initButton(xOfIOS, yOfIOS, widthOfIOS, heightOfIOS, "graphic_facebook1", true);
		xOfIOS = 48;
		yOfIOS = 215;
		widthOfIOS = 44;
		heightOfIOS = 28;
		mBtnMoreApps = initButton(xOfIOS, yOfIOS, widthOfIOS, heightOfIOS, "graphic_moreapps", true);
		
		//****** トグルボタン *******
		xOfIOS = 56;
		yOfIOS = 142;
		widthOfIOS = 46;
		heightOfIOS = 34;
		mBtnToggleSound = initToggleButton(xOfIOS, yOfIOS, widthOfIOS, heightOfIOS, "graphic_sound_on", "graphic_sound_off", true);
	}
    android.os.Handler mHandler = null;
	
	
    // **********　Instance **********
	
	private static TapGirlActivity s_Instance = null;
	public static TapGirlActivity getInstance()
	{
		return s_Instance;
	}
	
    
    
    // **********　Sound **********
    /**
     * 音声再生用プレイヤー
     */
    private MediaPlayer mPlayer = null;
    public void prepareSE(int indexSE)
    {
    	if (mPlayer != null)
    	{
    		mPlayer.release();
    		mPlayer = null;
    	}
    	int resId = 0;
    	String fileName = "sound_" + String.format("%02d", indexSE);
    	resId = getResIdOfRaw(fileName);
		mPlayer = MediaPlayer.create(this, resId);
		mPlayer.start();
    }
    
    public boolean isSoundPlaying()
    {
    	boolean result = false;
    	if (mPlayer != null) {
    		if (mPlayer.isPlaying()) {
    			result = true;
    		}
    		else {
    			mPlayer.release();
    			mPlayer = null;
    		}
    	}
    	return result;
    }
    
    public void showSite(String url)
    {
    	Uri uri = Uri.parse(url);
    	Intent i = new Intent(Intent.ACTION_VIEW, uri);
    	startActivity(i);
    }
    // **********　Save & Load **********
    public boolean saveData()
    {
    	boolean result = false;
    	try {
			FileOutputStream outStream = openFileOutput(CDefines.NAME_SAVE_FILE, Context.MODE_PRIVATE);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			CChangeData changeData = CChangeData.getInstance();
			{
				int iLength = changeData.getTouchLength();
				byte [] array = intToByteArray(iLength);
				bos.write(array);
			}
			{
				int loopNumber = changeData.getLoopNumber();
				byte [] array = intToByteArray(loopNumber);
				bos.write(array);
			}
			byte [] dataArray = bos.toByteArray();
			CRC32 crc = new CRC32();
			crc.reset();
			crc.update(dataArray);
			long lCrc = crc.getValue();
			byte [] arrayCrc = longToByteArray(lCrc);
			
			outStream.write(dataArray);
			outStream.write(arrayCrc);

			outStream.close();
			Log.v("info", "save was end");
			result = true;
    	}
    	catch (Exception exp)
    	{
    		Log.d("exception", exp.getMessage());
    	}
    	return result;
    }
    
    public boolean loadData()
    {
    	boolean result = false;
    	try {
			boolean bExists = isExistsSaveData();
			if (bExists) {
				FileInputStream file = getApplicationContext().openFileInput(CDefines.NAME_SAVE_FILE);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				//
				byte [] buf = new byte[1024];
				int byteRead = 0;
				while ((byteRead = file.read(buf)) != -1) {
					bos.write(buf, 0, byteRead);
				}
				byte[] bufAll = bos.toByteArray();
				int indexOfBuffer = 0;
				{
					byte [] arrayForLength = new byte [4];
					for (int i = 0; i < arrayForLength.length; i++)
					{
						arrayForLength[i] = bufAll[indexOfBuffer];
						indexOfBuffer++;
					}
					int iLen = byteArrayToInt(arrayForLength);
					mRenderer.resetTouchLength((float)iLen);
				}
				CChangeData changeData = CChangeData.getInstance();
				{
					byte [] arrayForLoopNumber = new byte [4];
					for (int i = 0; i < arrayForLoopNumber.length; i++)
					{
						arrayForLoopNumber[i] = bufAll[indexOfBuffer];
						indexOfBuffer++;
					}
					int loopNum = byteArrayToInt(arrayForLoopNumber);
					changeData.setLoopNumber(loopNum);
				}
				{
					//crc
					byte [] arrayForConvert = new byte[8];
					for (int indexForConv = 0; indexForConv < arrayForConvert.length; indexForConv++)
					{
						arrayForConvert[indexForConv] = bufAll[indexOfBuffer];
						indexOfBuffer++;
					}
					long crcFromBuf = byteArrayToLong(arrayForConvert);
					
					CRC32 crc = new CRC32();
					crc.reset();
					crc.update(bufAll, 0, bufAll.length - 8);
					long crcFromData = crc.getValue();
					if (crcFromBuf != crcFromData) {
						Log.d("error", "from buffer = " + String.valueOf(crcFromBuf) + " from data = " + String.valueOf(crcFromData));
						assert(false);
					}
					else {
						Log.v("info", "crc check succeed");
					}
				}
				file.close();
				Log.v("info", "load was end");
				result = true;
			}
    	}
    	catch (Exception exp)
    	{
    		Log.d("exception", exp.getMessage());
    	}
    	return result;
    }
	public boolean isExistsSaveData()
	{
		boolean result = false;
		try {
			File fEx = getFileStreamPath(CDefines.NAME_SAVE_FILE);
			result = fEx.exists();
		}
		catch (Exception exp) {
			Log.d("exception", exp.getMessage());
		}
		return result;
	}
	public static byte[] intToByteArray(int target)
	{
		byte[] result = null;
		try {
			 byte[] bs = new byte[4];
			 bs[3] = (byte) (0x000000ff & (target));
			 bs[2] = (byte) (0x000000ff & (target >>> 8));
			 bs[1] = (byte) (0x000000ff & (target >>> 16));
			 bs[0] = (byte) (0x000000ff & (target >>> 24));
			 result = bs;
		}
		catch (Exception exp) {
			Log.d("exception", exp.getMessage());
		}
		return result;
	}
	public static byte [] longToByteArray(long target)
	{
		byte[] result = null;
		try {
			int dig8, indexArray;
			byte [] array = new byte[8];
			for (dig8 = 0; dig8 < array.length; dig8++)
			{
				indexArray = array.length - (1 + dig8);
				array[indexArray] =(byte)( target & 0xff);
				target >>= 8;
			}
			result = array;
		}
		catch (Exception exp) {
			Log.d("exception", exp.getMessage());
		}
		return result;
	}
	public static int byteArrayToInt(byte[] array)
	{
		return ByteBuffer.wrap(array).asIntBuffer().get();
	}
	public static long byteArrayToLong(byte[] array)
	{
		return ByteBuffer.wrap(array).asLongBuffer().get();
	}
	// *************** SNS ***************
	public static String getSNSString()
	{
		String result = null;
		CChangeData changeData = CChangeData.getInstance();
		int iLength = changeData.getTouchLength();
		result = String.format(CDefines.SNS_STR_FORMAT, iLength);
		return result;
	}

	private static SharedPreferences mSharedPreferences;
	public static SharedPreferences getSharedPreferences() {
		return mSharedPreferences;
	}
	private static Twitter twitter;
	private static RequestToken requestToken;
	// 排他処理用のAtomicなカウンタ
	private AtomicInteger mAtomTweet = new AtomicInteger(0);
	public AtomicInteger getAtomTweet() {
		return mAtomTweet;
	}
	private void saveTwitterToken()
	{
		mAtomTweet.incrementAndGet();
    	// Android3.0以後、ネットワーク利用はメインスレッドでできない
    	// http://rainbowdevil.jp/?p=967
    	Thread trd = new Thread(new Runnable() {
			
			@Override
			public void run() {
	    		{
	                try {
	                	String strUri = mSharedPreferences.getString(CDefines.TWITTER_PREF_KEY_INTENT_URI, "");
	                	Uri uri = Uri.parse(strUri);
	            		if (uri != null) {
	            			Log.v("info", "saveTwitterToken uri = " + uri.toString());
	            		}
	        			String verifier = uri.getQueryParameter(CDefines.TWITTER_IEXTRA_OAUTH_VERIFIER);
	                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier); 
	                    Editor e = mSharedPreferences.edit();
	                    e.putString(CDefines.TWITTER_PREF_KEY_TOKEN, accessToken.getToken()); 
	                    Log.v("info", "token = " + accessToken.getToken());
	                    e.putString(CDefines.TWITTER_PREF_KEY_SECRET, accessToken.getTokenSecret());
	                    Log.v("info", "tokenSecret = " + accessToken.getTokenSecret());
	                    e.commit();
	    	        } catch (Exception e) { 
		                Log.e("exception", e.getMessage());
	    	        }
	    		}
	    		mAtomTweet.decrementAndGet();
				
			}
		});
    	trd.start();
	}
	/**
	 * check if the account is authorized
	 * @return
	 */
	private boolean isConnected() {
		return mSharedPreferences.getString(CDefines.TWITTER_PREF_KEY_TOKEN, null) != null;
	}
	private void askOAuth() {
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setOAuthConsumerKey(CDefines.TWITTER_CONSUMER_KEY);
		configurationBuilder.setOAuthConsumerSecret(CDefines.TWITTER_CONSUMER_SECRET);
		twitter4j.conf.Configuration configuration = configurationBuilder.build();
		twitter = new TwitterFactory(configuration).getInstance();
		try {
			requestToken = twitter.getOAuthRequestToken(CDefines.TWITTER_CALLBACK_URL);
			this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.v("info", "TapGirlActivity#onCreate");
        super.onCreate(savedInstanceState);
        assert(s_Instance == null);
        s_Instance = this;
        //全画面表示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //横向きにした時でも、縦型のレイアウトのままになるように
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        //画面情報を保存
        initDispInfo();

        mRenderer = new MyRenderer();
        mGLSurfaceView = MyGLUtil.initGLES20(this, mRenderer);
        mGLSurfaceView.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR	 | 	GLSurfaceView.DEBUG_LOG_GL_CALLS);
        setContentView(mGLSurfaceView);
        
        //UIアクセスのためのハンドル
        mHandler  = new android.os.Handler();
        mRenderer.setHandlerForUI(mHandler);
        
        //データがあればロード
        if (isExistsSaveData())
        {
        	boolean resultLoad = loadData();
        	assert(resultLoad);
        }
        initUI();
		mSharedPreferences = getSharedPreferences(CDefines.TWITTER_PREFERENCE_NAME, MODE_PRIVATE);
        {
    		Uri uri = getIntent().getData();
    		if (uri != null) {
    			Log.v("info", "onCreate uri = " + uri.toString());
    		}
    		if (uri != null && uri.toString().startsWith(CDefines.TWITTER_CALLBACK_URL)) {
        		{
                    Editor e = mSharedPreferences.edit();
                    String strUri = uri.toString();
                    e.putString(CDefines.TWITTER_PREF_KEY_INTENT_URI, strUri); 
                    e.commit();
        		}
            	saveTwitterToken();
    		}
        }
    }
    
    protected void onDestroy()
    {
    	Log.v("info", "TapGirlActivity#onDestroy");
    	s_Instance = null;
    	super.onDestroy();
    }
	@Override
	protected void onPause()
	{
		super.onPause();
		if (mGLSurfaceView != null)
		{
			mGLSurfaceView.onPause();
		}
		Log.v("info", "activity.onPause");
		saveData();
		if (isFinishing())
		{
			Log.v("info", "acitvity will be finished");
			s_Instance = null;
		}
	}
	@Override
	protected void onResume()
	{
		Log.v("info", "activity.onResume");
		super.onResume();
		if (mGLSurfaceView != null)
		{
			mGLSurfaceView.onResume();
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		try {
			switch (event.getAction())
			{
			case MotionEvent.ACTION_DOWN:
			{
				mRenderer.getPosTouch().setX(event.getX());
				mRenderer.getPosTouch().setY(event.getY());
			}
			break;
			case MotionEvent.ACTION_MOVE:
			{
				mRenderer.reqUpdateTouchPos(event);
			}
			break;
			case MotionEvent.ACTION_UP:
			{
				mRenderer.reqUpdateTouchPos(event);
				mRenderer.getPosTouch().init();
			}
			break;
			}
		}
		catch (Exception exp)
		{
			
		}
		return super.onTouchEvent(event);
	}
	@Override
	public void onClick(View v) {
		Log.v("info", "TapGirlActivity#onClick");
		if (v.equals(mBtnTweet)) {
			Log.v("info", "tweet");
			if (isConnected()) {
				if (true)
				{
					Intent intent = new Intent(TapGirlActivity.this, CSendSNSActivity.class);
					startActivity(intent);
				}
				else
				{
				}
			}
			else {
				Thread trd = new Thread(new Runnable() {
					// 通信を含むのでスレッドにした
					@Override
					public void run() {
						askOAuth();
						Log.v("info", "to finish");
						finish();
						
					}
				});
				trd.start();
			}
		}
		else if (v.equals(mBtnFacebook)) {
			Log.v("info", "facebook");
		}
		else if (v.equals(mBtnMoreApps)) {
			showSite(CDefines.MORE_APPS_SITE);
		}
		
	}
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Log.v("info", "TapGirlActivity#onCheckedChanged :" + String.valueOf(isChecked));
		if (buttonView.equals(mBtnToggleSound))
		{
			mBtnToggleSound.onCheckedChanged(isChecked);
		}
	}
}