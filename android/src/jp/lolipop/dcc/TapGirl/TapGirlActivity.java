package jp.lolipop.dcc.TapGirl;

import jp.lolipop.dcc.lib.MyGLUtil;

import jp.lolipop.dcc.lib.IGLRenderer;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

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
	}
	//１インチ＝2.54cm
	private final float _CM_PER_INCH =	2.54f;
	//1cm当たりのピクセル数
	public float pixelPerCM()
	{
		float dpi = (getXDpi() + getYDpi()) / 2.0f;
		return (dpi / _CM_PER_INCH);
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
		if (isFinishing())
		{
			Log.v("info", "acitvity will be finished");
			s_Instance = null;
		}
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		if (mGLSurfaceView != null)
		{
			mGLSurfaceView.onResume();
		}
	}
}