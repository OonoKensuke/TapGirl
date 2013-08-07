package jp.lolipop.dcc.lib;

import jp.lolipop.dcc.TapGirl.CDefines;
import jp.lolipop.dcc.TapGirl.TapGirlActivity;
import android.app.Activity;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView.ScaleType;

/**　iOSを元にしたUI配置に関してまとめたクラス
 * @author nakano_michiharu
 *
 */
public abstract class CBaseActivity extends Activity implements View.OnClickListener, OnCheckedChangeListener {
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
	protected void initDispInfo()
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
	protected RelativeLayout getUILayout() {
		return mUILayout;
	}
	protected void setmUILayout(RelativeLayout uiLayout) {
		this.mUILayout = uiLayout;
	}
	protected TextView initLabel(float xOfIOS, float yOfIOS, float widthOfIOS, float heightOfIOS, float fontSize, int gravity, String strInit)
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
	
	protected ImageButton initButton(float xOfIOS, float yOfIOS, float widthOfIOS, float heightOfIOS, String fileName, boolean setClickListener )
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
	
	protected MyToggleButton initToggleButton(float xOfIOS, float yOfIOS, float widthOfIOS, float heightOfIOS, String fileNameOnImage, String fileNameOffImage, boolean setClickListener )
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

    /** ファイル名からリソースIDを取得
     * @param fileName
     * @return
     */
    protected int getResIdOfRaw(String fileName)
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
	
}
