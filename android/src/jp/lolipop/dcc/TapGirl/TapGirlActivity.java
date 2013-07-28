package jp.lolipop.dcc.TapGirl;

import jp.lolipop.dcc.lib.CVec2D;
import jp.lolipop.dcc.lib.MyGLUtil;
import jp.lolipop.dcc.*;

import jp.lolipop.dcc.lib.IGLRenderer;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TapGirlActivity extends Activity {
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
	
	private CVec2D mPosActivity = new CVec2D();
	

	
	
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
	
	protected int getPixelsWidth()
	{
		return mWidthPixels;
	}
	
	protected int getPixelsHeight()
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
        	final float widthOfIOS = 320.0f;
        	final float heightOfIOS = 568.0f;
        	WidthRatio = (float)getPixelsWidth() / widthOfIOS;
        	HeightRatio = (float)getPixelsHeight() / heightOfIOS;
    		float margin  = 0.0f;
        	if (WidthRatio < HeightRatio) {
        		MagRatio = WidthRatio;
        		UIMargin.setX(0.0f);
        		//上だけ開ける
        		margin = (float)getPixelsHeight() - (heightOfIOS * MagRatio);
        		UIMargin.setY(margin);
        	}
        	else {
        		MagRatio = HeightRatio;
        		UIMargin.setY(0.0f);
        		//左右に同じだけ開けるので、2で割る
        		margin =  ((float)getPixelsWidth() - (widthOfIOS * MagRatio) / 2.0f);
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
	
	private RelativeLayout.LayoutParams getLayoutFrom_iOSSize(float widthOfIOS, float heightOfIOS)
	{
		widthOfIOS *= getMagRatio();
		heightOfIOS *= getMagRatio();
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
		//cap Heightが3/4
		textView.setTextSize(fontSize  * 0.75f *  getMagRatio());
		textView.setGravity(gravity);
		RelativeLayout.LayoutParams layoutCountLable = getLayoutFrom_iOSSize(widthOfIOS, heightOfIOS);
		layoutCountLable.setMargins((int)getXofLayoutMargin(xOfIOS), (int)getYofLayoutMargin(yOfIOS), 0, 0);
		mUILayout.addView(textView, layoutCountLable);
		return textView;
	}

	
	private void initUI()
	{
		RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		mUILayout = new RelativeLayout(this);
		addContentView(mUILayout, layout);
		//****** countLabel *******
		//座標とサイズはXCodeから
		float xOfIOS = 130;
		float yOfIOS = 40;
		float widthOfIOS = 160;
		float heightOfIOS = 61;
		mCountLabel = initLabel(xOfIOS, yOfIOS, widthOfIOS, heightOfIOS, 42.0f, Gravity.RIGHT, "100");
		//****** roundLabel *******
		xOfIOS = 213;
		yOfIOS = 148;
		widthOfIOS = 80;
		heightOfIOS = 21;
		mRoundLabel = initLabel(xOfIOS, yOfIOS, widthOfIOS, heightOfIOS, 17.0f, Gravity.LEFT, "1周目");
	}
    android.os.Handler mHandler = null;
    
    
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
		resId = getResources().getIdentifier(
				fileName,
				"raw",
				"jp.lolipop.dcc.TapGirl");
		Log.v("info", fileName + " resId = " + String.valueOf(resId));
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
        
        initUI();
        //UIアクセスのためのハンドル
        mHandler  = new android.os.Handler();
        mRenderer.setHandlerForUI(mHandler);
    }
    
    protected void onDestroy()
    {
    	Log.v("info", "TapGirlActivity#onDestroy");
    	s_Instance = null;
    	super.onDestroy();
    }
    /*
	@Override
	protected void onPause()
	{
		super.onPause();
		if (mGLSurfaceView != null)
		{
			mGLSurfaceView.onPause();
		}
		Log.v("info", "activity.onPause");
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
	*/
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
}