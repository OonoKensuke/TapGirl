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

public class TapGirlActivity extends Activity implements View.OnClickListener, OnCheckedChangeListener{
	private GLSurfaceView mGLSurfaceView = null;
	private MyRenderer mRenderer = null;
	private static TapGirlActivity s_Instance = null;
	
	public static TapGirlActivity getInstance()
	{
		return s_Instance;
	}
	
	private static int mWidthPixels = 0;
	private static int mHeightPixels = 0;
	private static float mXDpi = 0f;
	private static float mYDpi = 0f;
	private static float mDensity = 0f;
	
	
	
	protected void setPixelsWidth(int pixelsWidth)
	{
		mWidthPixels = pixelsWidth;
	}
	protected void setPixelsHeight(int pixelsHeight)
	{
		mHeightPixels = pixelsHeight;
	}
	protected void setXDpi(float xdpi)
	{
		mXDpi = xdpi;
	}
	protected void setYDpi(float ydpi)
	{
		mYDpi = ydpi;
	}
	
	public int getPixelsWidth()
	{
		return mWidthPixels;
	}
	
	public int getPixelsHeight()
	{
		return mHeightPixels;
	}
	
	protected float getXDpi()
	{
		return mXDpi;
	}
	
	protected float getYDpi()
	{
		return mYDpi;
	}
	public static float getDensity() {
		return mDensity;
	}
	public static void setDensity(float density) {
		mDensity = density;
	}
	/**
	 * iOSに対する幅の拡大率
	 */
	private static float WidthRatio = 0.0f;
	public static float getWidthRatio() {
		return WidthRatio;
	}

	/**
	 * iOSに対する高さの拡大率
	 */
	private static float HeightRatio = 0.0f;
	public static float getHeightRatio() {
		return HeightRatio;
	}
	
	/**
	 * 拡大率。幅と高さのうち、小さい方を用いる
	 */
	private static float MagRatio = 0.0f;
	public static float getMagRatio() {
		return MagRatio;
	}
	
	/**
	 * iOSにあわせてUIを配置するとき上あるいは左に開ける間隔
	 */
	private static CVec2D UIMargin = new CVec2D(); 
	public static CVec2D getUIMargin() {
		return UIMargin;
	}
	
	public static boolean isUseWRatio()
	{
		if (getWidthRatio() == getMagRatio())
		{
			return true;
		}
		return false;
	}
	private void initDispInfo()
	{
        //画面解像度の取得
        WindowManager windowManager = getWindowManager();

        Display display = windowManager.getDefaultDisplay();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        // 以下は、HT-03A の値
        Log.v("xdpi",            String.valueOf(displayMetrics.xdpi));            // 180.62193
        Log.v("ydpi",            String.valueOf(displayMetrics.ydpi));            // 181.96814
        Log.v("widthPixels",    String.valueOf(displayMetrics.widthPixels));    // 480
        Log.v("heightPixels",    String.valueOf(displayMetrics.heightPixels));    // 320
        Log.v("density",        String.valueOf(displayMetrics.density));        // 1.0
        Log.v("scaledDensity",    String.valueOf(displayMetrics.scaledDensity));    // 1.0

        Log.v("width",            String.valueOf(display.getWidth()));            // 480
        Log.v("height",            String.valueOf(display.getHeight()));            // 320
        Log.v("orientation",    String.valueOf(display.getOrientation()));        // 1
        Log.v("refreshRate",    String.valueOf(display.getRefreshRate()));        // 60.0
        Log.v("pixelFormat",    String.valueOf(display.getPixelFormat()));
        

        this.setPixelsWidth(displayMetrics.widthPixels);
        this.setPixelsHeight(displayMetrics.heightPixels);
        this.setXDpi(displayMetrics.xdpi);
        this.setYDpi(displayMetrics.ydpi);
        TapGirlActivity.setDensity(displayMetrics.density);
        
        {
        	//座標はRetinaディスプレイの半分の解像度
        	final float widthOfIOS = CDefines.IOS_SCREEN_WIDTH;
        	final float heightOfIOS = CDefines.IOS_SCREEN_HEIGHT;
        	WidthRatio = (float)getPixelsWidth() / widthOfIOS;
        	HeightRatio = (float)getPixelsHeight() / heightOfIOS;
    		float margin  = 0.0f;
        	if (WidthRatio < HeightRatio) {
        		MagRatio = WidthRatio;
        		Log.v("info", "幅を元に拡大率を決定" + String.valueOf(MagRatio));
        		UIMargin.setX(0.0f);
        		//上だけ開ける
        		margin = (float)getPixelsHeight() - (heightOfIOS * MagRatio);
        		UIMargin.setY(margin);
        	}
        	else {
        		MagRatio = HeightRatio;
        		Log.v("info", "高さを元に拡大率を決定" + String.valueOf(MagRatio));
        		UIMargin.setY(0.0f);
        		//左右に同じだけ開けるので、2で割る
        		margin =  ((float)getPixelsWidth() - (widthOfIOS * MagRatio)) / 2.0f;
        		UIMargin.setX(margin);
        	}
        	Log.v("info", "wr = " + String.valueOf(WidthRatio)  + " hr = " + String.valueOf(HeightRatio) + " mag = " + String.valueOf(MagRatio));
        }
	}
	//１インチ＝2.54cm
	private final float _CM_PER_INCH =	2.54f;
	//1cm当たりのピクセル数
	public float pixelPerCM()
	{
		float dpi = (getXDpi() + getYDpi()) / 2.0f;
		return (dpi / _CM_PER_INCH);
	}
	
	private RelativeLayout.LayoutParams getLayoutFrom_iOSSize(float widthOfIOS, float heightOfIOS, float minimumHeight)
	{
		widthOfIOS *= getMagRatio();
		heightOfIOS *= getMagRatio();
		heightOfIOS = Math.max(heightOfIOS, minimumHeight);
		RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams((int)widthOfIOS, (int)heightOfIOS);
		return layout;
	}
	
	private float getXofLayoutMargin(float xOfIOS)
	{
		xOfIOS *= getMagRatio();
		xOfIOS += UIMargin.getX();
		return xOfIOS;
	}
	
	private float getYofLayoutMargin(float yOfIOS)
	{
		yOfIOS *= getMagRatio();
		yOfIOS += UIMargin.getY();
		return yOfIOS;
	}
	
	private RelativeLayout mUILayout = null;
	private TextView mCountLabel = null;
	public TextView getCountLabel() {
		return mCountLabel;
	}
	private TextView mRoundLabel = null;
	public TextView getmRoundLabel() {
		return mRoundLabel;
	}
	private TextView initLabel(float xOfIOS, float yOfIOS, float widthOfIOS, float heightOfIOS, float fontSize, int gravity, String strInit)
	{
		TextView textView= new TextView(this);
		textView.setText(strInit);
		textView.setTextColor(Color.BLACK);
		//フォントサイズはある程度小さくしないと下部が消える
		float _fontSize = fontSize  * 0.6f *  getMagRatio();
		textView.setTextSize(_fontSize);
		textView.setGravity(gravity);
		textView.setPadding(0, 0, 0, 0);
		//レイアウトをフォントよりもある程度大きく取らないとフォントの下部が表示されない
		RelativeLayout.LayoutParams layoutCountLable = getLayoutFrom_iOSSize(widthOfIOS, heightOfIOS, _fontSize + 16.0f);
		int iX = (int)getXofLayoutMargin(xOfIOS);
		int iY = (int)getYofLayoutMargin(yOfIOS);
		layoutCountLable.setMargins(iX, iY, 0, 0);
		
		
		textView.setBackgroundColor(0);
		textView.setSingleLine(true);
		mUILayout.addView(textView, layoutCountLable);
		return textView;
	}
	
	private ImageButton mBtnTweet = null;
	private ImageButton mBtnMoreApps = null;
	private ImageButton mBtnFacebook = null;
	
	private ImageButton initButton(float xOfIOS, float yOfIOS, float widthOfIOS, float heightOfIOS, String fileName, boolean setClickListener )
	{
		ImageButton imgBtn = null;
		try {
			imgBtn = new ImageButton(this);
			int resId = getResIdOfRaw(fileName);
			assert(resId > 0);
			imgBtn.setImageResource(resId);
			imgBtn.setScaleType(ScaleType.FIT_XY);
			imgBtn.setPadding(0, 0, 0, 0);
			imgBtn.setBackgroundColor(0);
			RelativeLayout.LayoutParams layoutButton = getLayoutFrom_iOSSize(widthOfIOS, heightOfIOS, 0.0f);
			int iX = (int)getXofLayoutMargin(xOfIOS);
			int iY = (int)getYofLayoutMargin(yOfIOS);
			layoutButton.setMargins(iX, iY, 0, 0);
			mUILayout.addView(imgBtn, layoutButton);
			if (setClickListener)
			{
				imgBtn.setOnClickListener(this);
			}
		}
		catch (Exception exp)
		{
			Log.d("exception", exp.getMessage());
		}
		return imgBtn;
	}
	
	private MyToggleButton mBtnToggleSound = null;
	
	public MyToggleButton getBtnToggleSound() {
		return mBtnToggleSound;
	}
	private MyToggleButton initToggleButton(float xOfIOS, float yOfIOS, float widthOfIOS, float heightOfIOS, String fileNameOnImage, String fileNameOffImage, boolean setClickListener )
	{
		MyToggleButton tglBtn = null;
		try {
			tglBtn = new MyToggleButton(this);
			RelativeLayout.LayoutParams layoutButton = getLayoutFrom_iOSSize(widthOfIOS, heightOfIOS, 0.0f);
			int iX = (int)getXofLayoutMargin(xOfIOS);
			int iY = (int)getYofLayoutMargin(yOfIOS);
			layoutButton.setMargins(iX, iY, 0, 0);
			int resIdOn = getResIdOfRaw(fileNameOnImage);
			int resIdOff = getResIdOfRaw(fileNameOffImage);
			tglBtn.setResIdOff(resIdOff);
			tglBtn.setResIdOn(resIdOn);
			tglBtn.setBackgroundResource(resIdOff);
			tglBtn.setChecked(false);
			tglBtn.setTextOn("");
			tglBtn.setTextOff("");
			tglBtn.setText("");
			mUILayout.addView(tglBtn, layoutButton);
			if (setClickListener) {
				tglBtn.setOnCheckedChangeListener(this);
			}
		}
		catch (Exception exp)
		{
			Log.d("exception", exp.getMessage());
		}
		return tglBtn;
		
	}

	
	private void initUI()
	{
		RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		mUILayout = new RelativeLayout(this);
		addContentView(mUILayout, layout);
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
    
    private int getResIdOfRaw(String fileName)
    {
    	int resId = 0;
    	try {
    		resId = getResources().getIdentifier(
    				fileName,
    				"raw",
    				"jp.lolipop.dcc.TapGirl");
    		Log.v("info", fileName + " resId = " + String.valueOf(resId));
    	}
    	catch (Exception exp)
    	{
    		Log.d("exception", exp.getMessage());
    	}
    	return resId;
    }
    
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
	//twitter4j
	private static SharedPreferences mSharedPreferences;
	private static Twitter twitter;
	private static RequestToken requestToken;
	private AtomicInteger mAtomTweet = new AtomicInteger(0);
	/**
	 * check if the account is authorized
	 * @return
	 */
	private boolean isConnected() {
		return mSharedPreferences.getString(CDefines.PREF_KEY_TOKEN, null) != null;
	}
	private void askOAuth() {
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setOAuthConsumerKey(CDefines.CONSUMER_KEY);
		configurationBuilder.setOAuthConsumerSecret(CDefines.CONSUMER_SECRET);
		twitter4j.conf.Configuration configuration = configurationBuilder.build();
		twitter = new TwitterFactory(configuration).getInstance();
		try {
			requestToken = twitter.getOAuthRequestToken(CDefines.CALLBACK_URL);
			this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}
	private String getSNSString()
	{
		String result = null;
		CChangeData changeData = CChangeData.getInstance();
		int iLength = changeData.getTouchLength();
		result = String.format("%dcmこすった", iLength);
		return result;
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
        {
        	// Android3.0以後、ネットワーク利用はメインスレッドでできない
        	// http://rainbowdevil.jp/?p=967
        	Thread trd = new Thread(new Runnable() {
				
				@Override
				public void run() {
					mAtomTweet.incrementAndGet();
		        	//twitter4j
		    		mSharedPreferences = getSharedPreferences(CDefines.PREFERENCE_NAME, MODE_PRIVATE);
		    		Uri uri = getIntent().getData();
		    		if (uri != null) {
		    			Log.v("info", "uri = " + uri.toString());
		    		}
		    		if (uri != null && uri.toString().startsWith(CDefines.CALLBACK_URL)) {
		    			String verifier = uri.getQueryParameter(CDefines.IEXTRA_OAUTH_VERIFIER);
		                try {
		                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier); 
		                    Editor e = mSharedPreferences.edit();
		                    e.putString(CDefines.PREF_KEY_TOKEN, accessToken.getToken()); 
		                    Log.v("info", "token = " + accessToken.getToken());
		                    e.putString(CDefines.PREF_KEY_SECRET, accessToken.getTokenSecret());
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
				if (mAtomTweet.get() > 0)
				{
					return;
				}
				String strTweet = getSNSString();
				Log.v("info", "strTweet = " + strTweet);
				{
					mAtomTweet.incrementAndGet();
					boolean bAsync = true;
					String oauthAccessToken = mSharedPreferences.getString(CDefines.PREF_KEY_TOKEN, "");
					Log.v("info", "oauthAccessToken = " + oauthAccessToken);
					String oAuthAccessTokenSecret = mSharedPreferences.getString(CDefines.PREF_KEY_SECRET, "");
					Log.v("info", "oAuthAccessTokenSecret = " + oAuthAccessTokenSecret);

					ConfigurationBuilder confbuilder = new ConfigurationBuilder();
					twitter4j.conf.Configuration conf = confbuilder
										.setOAuthConsumerKey(CDefines.CONSUMER_KEY)
										.setOAuthConsumerSecret(CDefines.CONSUMER_SECRET)
										.setOAuthAccessToken(oauthAccessToken)
										.setOAuthAccessTokenSecret(oAuthAccessTokenSecret)
										.build();
					if (bAsync)
					{
						Log.v("info", "in async");
						AsyncTwitterFactory factory = new AsyncTwitterFactory(conf);
						AsyncTwitter twitter = factory.getInstance();
						twitter.addListener(new TwitterAdapter() {
							@Override
							public void updatedStatus(Status status)
							{
								Log.v("info", "Successfully updated the status to [" +
				                        status.getText() + "].");
								mAtomTweet.decrementAndGet();
							}
							@Override
							public void onException(TwitterException exp, twitter4j.TwitterMethod method)
							{
								if (method == TwitterMethod.UPDATE_STATUS)
								{
									exp.printStackTrace();
									mAtomTweet.decrementAndGet();
								}
								else {
									mAtomTweet.decrementAndGet();
									throw new AssertionError("Should not happen");
								}
							}
						});
						Log.v("info", "before update");
						twitter.updateStatus(strTweet);
						Log.v("info", "after update");
					}
					else {
						TwitterFactory factory = new TwitterFactory(conf);
						Twitter tmpTwitter = factory.getInstance();
						Status status = null;
						try {
							status = tmpTwitter.updateStatus(strTweet);
						}
						catch (TwitterException exp)
						{
							exp.printStackTrace();
						}
					}
				}
			}
			else {
				Thread trd = new Thread(new Runnable() {
					
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