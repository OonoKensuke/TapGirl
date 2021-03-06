package jp.lolipop.dcc.TapGirl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.concurrent.CountDownLatch;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import jp.lolipop.dcc.lib.AndroidFileIO;
import jp.lolipop.dcc.lib.CColor;
import jp.lolipop.dcc.lib.CTexture;
import jp.lolipop.dcc.lib.CVec2D;
import jp.lolipop.dcc.lib.MyGLUtil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import jp.lolipop.dcc.lib.IGLRenderer;

public class MyRenderer extends IGLRenderer {
	enum Step {
		//初期化
		STEP_INIT,
		//通常表示
		STEP_NORMAL,
		//画像変更前半
		STEP_CHANGE_IN,
		//画像変更後半
		STEP_CHANGE_OUT,
		//クロスフェード
		STEP_CROSS_FADE,
		//音声終了待ち
		STEP_WAIT_SE_END,
		//おめでとう表示
		STEP_DISP_CONGRATULATIONS,
		//パスワード表示
		STEP_DISP_PASSWORD,
		//サイト表示
		STEP_SHOW_SITE,
	}
	private Step mStep = Step.STEP_INIT;

	//シェーダー配列
	private MyGLShader[] mShaders = null;
	//シェーダーインデックス
	//通常表示
	public static final int FRSH_NORMAL = 0;
	//アルファブレンド表示
	public static final int FRSH_FADE = 1;
	//シェーダーの数
	public static final int FRSH_MAX = 2;
	//浮動小数点で誤差が生じる可能性があるとき、十分小さな値として使う
	public final static float NEAR0 = ((float)(1.0f / 65536.0f));
	//スコアに換算する最低距離(cm)
	public final static float  _MIN_LEN	= 0.3f;
	
	
	CPrimitive mPrimCurrent = null;
	CPrimitive mPrimNext = null;
	
	private CTexture mTextureCurrent = null;
	private CTexture mTextureNext = null;
	
	private CColor mColor = new CColor(1.0f, 1.0f, 1.0f, 1.0f);

	private CChangeData mChangeData = new CChangeData(0.0f);
	
	private float mTouchLength = 0.0f;
	
	class CChangeWork
	{
		private  CChangeParam changeParam = null;
		public final CChangeParam getChangeParam() {
			return changeParam;
		}

		public void setChangeParam(final CChangeParam changeParam) {
			this.changeParam = changeParam;
		}

		long timeBegin = 0;
		
		public void init()
		{
			changeParam = null;
			timeBegin = 0;
		}
	}
	
	private CChangeWork mChangeWork = new CChangeWork();
	
	public MyRenderer()
	{
		Log.v("info", "MyRender#MyRender");
	}

	/**
	 * アクティビティのUIにアクセスする時に描画スレッドなど
	 * mainスレッド以外からアクセスする時に使う
	 */
	private Handler mHandlerActivity = null;
	public void setHandlerForUI(Handler handler)
	{
		mHandlerActivity = handler;
	}
	
	private Runnable mRunSetText;
	private CountDownLatch mLatchForButton = null;
	private void setTextInfoToView(final TextView textView, final String string)
	{
		try {
			if (Thread.currentThread().getName().equalsIgnoreCase("main"))
			{
				//同じスレッドからrunをpostするとデッドロックになるようだ
				//Log.v("info", "call from main");
				textView.setText(string);
			}
			else if (mHandlerActivity != null) {
				Log.v("info", "current thread name is " + Thread.currentThread().getName());
				mLatchForButton = new CountDownLatch(1);
				mRunSetText  = new Runnable() {
					
					@Override
					public void run() {
						textView.setText(string);
						mLatchForButton.countDown();
					}
				};
				mHandlerActivity.post(mRunSetText);
				//UIスレッドでの画像設定がすむのを待つ
				mLatchForButton.await();
				mLatchForButton = null;
			}
		}
		catch (Exception exp)
		{
			Log.d("exception", exp.getMessage());
		}
	}
	
	private void updateLabelInfo()
	{
		TapGirlActivity activity = TapGirlActivity.getInstance();
		int iLength = (int) (mChangeData.getObjectiveLength() - (int)(mTouchLength));
		String strTouch = String.valueOf(iLength);
		setTextInfoToView(activity.getCountLabel(), strTouch);
	}
	public void resetTouchLength(float length)
	{
		mTouchLength = length;
		//インスタンスを作り直すので、値を一時保管する
		int loopNumber = 1;
		if (mChangeData != null) {
			Log.v("info", "changeData will be renewal");
			loopNumber = mChangeData.getLoopNumber();
			mChangeData = null;
		}
		mChangeData = new CChangeData(mTouchLength);
		mChangeData.setLoopNumber(loopNumber);
	}
	
	private CVec2D mPosTouch = new CVec2D();
	
	public CVec2D getPosTouch() {
		return mPosTouch;
	}

//	private void setPosTouch(CVec2D posTouch) {
//		mPosTouch = posTouch;
//	}

	public void reqUpdateTouchPos(MotionEvent posUpdate)
	{
		if (mChangeData.getRestLength() == 0.0f)
		{
			return;
		}
		if ((mPosTouch.getX() < 0.0f) && (mPosTouch.getY() < 0.0f)) {
			mPosTouch.setParam(posUpdate);
		}
		else {
			float curLen = 0.0f;
			{
				float curLenX = mPosTouch.getX() - posUpdate.getX();
				curLenX *= curLenX;
				float curLenY = mPosTouch.getY() - posUpdate.getY();
				curLenY *= curLenY;
				curLen = (float) Math.sqrt(curLenX + curLenY);
				curLen /= TapGirlActivity.getInstance().pixelPerCM();
			}
			if (_MIN_LEN < curLen)
			{
				mTouchLength += curLen;
				mTouchLength = Math.min(mTouchLength, CChangeData.getObjectiveLength());
				//Log.v("info", "touch length = " + String.valueOf( mTouchLength));
				mPosTouch.setParam(posUpdate);
				updateLabelInfo();
			}
			else {
				//Log.v("info", "距離が短すぎる:" + curLen);
			}
		}
	}
	
	private void drawTextureCurrent(boolean isCurrent, int indexShader)
	{
		CPrimitive prim = null;
		CTexture texture = null;
		if (isCurrent) {
			prim = mPrimCurrent;
			texture = mTextureCurrent;
		}
		else {
			prim = mPrimNext;
			texture = mTextureNext;
		}
		MyGLShader shader = mShaders[indexShader];
		shader.drawArraysMy(prim.getPrimitiveType(), prim.getVertexBufferId(), texture.getTextureId(), prim.getNumVertices(), mColor, 1.0f);
	}
	
	private int getIndexOfTexSize()
	{
		return 3;
	}
	
	private void setupDispPasswordWithTime(long time)
	{
		TapGirlActivity activity = TapGirlActivity.getInstance();
		setTextInfoToView(activity.getCountLabel(), CDefines.SHOP_PASSWORD);
		mChangeWork.timeBegin = time;
	}
	
	private boolean loadTextureFromIndex(int index, boolean bLoadCurrent)
	{
		boolean result = false;
		try {
			final int texSize = getIndexOfTexSize();
			final String prev = "graphic/texture/image";
			String assetName = prev + String.format("%02d_%02d", texSize, index) + ".jpg";
			Log.v("info", assetName + "をテクスチャとして読み込みます");
			int id = -1;
			if (bLoadCurrent)
			{
				if (mTextureCurrent != null)
				{
					mTextureCurrent.dispose();
					mTextureCurrent = null;
				}
				mTextureCurrent = new CTexture();
				id = mTextureCurrent.loadTexture(assetName, TapGirlActivity.getInstance());
			}
			else
			{
				if (mTextureNext != null)
				{
					mTextureNext.dispose();
					mTextureNext = null;
				}
				mTextureNext = new CTexture();
				id = mTextureNext.loadTexture(assetName, TapGirlActivity.getInstance());
			}
			Log.v("info", "texture id = " + id);
			result = true;
		}
		catch (Exception exp)
		{
			Log.d("excpetion", exp.getMessage());
		}
		
		return result;
	}
	
	private void drawMain()
	{
		boolean bDrawNormal = true;
		long time = System.currentTimeMillis();
		TapGirlActivity activity = TapGirlActivity.getInstance();
		switch (mStep)
		{
			case STEP_INIT:
			{
				if (mChangeData.getRestLength() == 0.0f)
				{
					Log.v("info", "タッチ距離を初期化します");
					resetTouchLength(0.0f);
					updateLabelInfo();
				}
				mStep = Step.STEP_NORMAL;
				final CChangeParam changeParam = mChangeData.getChangeParam();
				loadTextureFromIndex(changeParam.indexImage, true);
			}
			break;
			case STEP_NORMAL:
			{
				float processedLenght = CChangeData.getObjectiveLength() - mChangeData.getRestLength();
				float restTemp = mTouchLength - processedLenght;
				//サウンド
				if (restTemp > NEAR0)
				{
					mChangeData.requestAddTouchLength(restTemp);
					if (mChangeData.isChange())
					{
						mChangeData.setIsChange(false);
						mChangeWork.init();
						mChangeWork.setChangeParam(mChangeData.getChangeParam());
						loadTextureFromIndex(mChangeWork.getChangeParam().indexImage, false);
						mStep = Step.STEP_CROSS_FADE;
						if (!activity.getBtnToggleSound().isChecked()) {
							activity.prepareSE(mChangeData.getChangeParam().indexSE);
						}
						Log.v("info", "change effect");
						//アルファなどの変更に使う基準となる現在時刻
						mChangeWork.timeBegin = System.currentTimeMillis();
					}
				}
			}
			break;
			case STEP_CROSS_FADE:
			{
				boolean bChangeStep = false;
				double timeDelta = (double)(time - mChangeWork.timeBegin) / 1000.0;
				if (timeDelta >= mChangeWork.getChangeParam().delayIn)
				{
					bChangeStep = true;
				}
				float theta = (float) (((Math.PI / 2.0) * timeDelta) / mChangeWork.getChangeParam().delayIn);
				mColor.setAlpha((float) Math.sin(theta));
				if (bChangeStep)
				{
					mColor.setAlpha(1.0f);
				}
				Log.v("info", "delta:" + timeDelta + ", alpha" + mColor.getAlpha());
				
				drawTextureCurrent(true, FRSH_NORMAL);
				drawTextureCurrent(false, FRSH_FADE);
				
				bDrawNormal = false;
				
				if (bChangeStep)
				{
					mStep = Step.STEP_WAIT_SE_END;
					mChangeWork.timeBegin = time;
					mTextureCurrent.dispose();
					mTextureCurrent  = null;
					mTextureCurrent = mTextureNext;
					mTextureNext = null;
				}
			}
			break;
			case STEP_WAIT_SE_END:
			{
				//サウンド終了待ち
				if (!activity.isSoundPlaying())
				{
					if (mChangeData.getRestLength() == 0.0f)
					{
						Log.v("info", "to end");
						mChangeWork.init();
						mChangeWork.timeBegin = time;
						mStep = Step.STEP_DISP_CONGRATULATIONS;
						setTextInfoToView(activity.getCountLabel(), CDefines.CONGRATURATIONS_MESSAGE);
					}
					else
					{
						mStep = Step.STEP_NORMAL;
					}
				}
			}
			break;
			case STEP_DISP_CONGRATULATIONS:
			{
				double timeDelta = (double)(time - mChangeWork.timeBegin) / 1000.0;
				if (timeDelta >= CDefines.CONGRATULATIONS_WAIT_TO_PASSWORD)
				{
					setupDispPasswordWithTime(time);
					mStep = Step.STEP_DISP_PASSWORD;
				}
			}
			break;
			case STEP_DISP_PASSWORD:
			{
				double timeDelta = (double)(time - mChangeWork.timeBegin) / 1000.0;
				if (timeDelta >= CDefines.CONGRATULATIONS_WAIT_TO_SHOP)
				{
					//周回数インクリメント
					{
						int loopNum = mChangeData.getLoopNumber();
						loopNum++;
						mChangeData.setLoopNumber(loopNum);
					}
					mStep = Step.STEP_SHOW_SITE;
					try {
						if (Thread.currentThread().getName().equalsIgnoreCase("main"))
						{
							//同じスレッドからrunをpostするとデッドロックになるようだ
							TapGirlActivity.getInstance().showSite(CDefines.SHOP_SITE_URL);
						}
						else if (mHandlerActivity != null) {
							Log.v("info", "current thread name is " + Thread.currentThread().getName());
							mLatchForButton = new CountDownLatch(1);
							mRunSetText  = new Runnable() {
								
								@Override
								public void run() {
									TapGirlActivity.getInstance().showSite(CDefines.SHOP_SITE_URL);
									mLatchForButton.countDown();
								}
							};
							mHandlerActivity.post(mRunSetText);
							//UIスレッドでの画像設定がすむのを待つ
							mLatchForButton.await();
							mLatchForButton = null;
						}
					}
					catch (Exception exp)
					{
						Log.d("exception", exp.getMessage());
					}
				}
			}
			break;
			case STEP_SHOW_SITE:
			{
				//特に何も決まっていない
			}
			break;
			default:
				break;
		}
		if (bDrawNormal) {
			drawTextureCurrent(true, FRSH_NORMAL);
		}
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		
		drawMain();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.v("info", "MyRenderer#onSurfaceChanged width " + width + " height " + height);
		GLES20.glViewport(0, 0, width, height);
		
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		Log.v("info", "MyRenderer#onSurfaceCreated ");
		GLES20.glClearColor(CDefines.CLEAR_RED, CDefines.CLEAR_GREEN, CDefines.CLEAR_BLUE, 1.0f);
		mPrimCurrent = new CPrimitive();
		mPrimCurrent.testSetUp(0.0f, 0.0f);
		
		mPrimNext = new CPrimitive();
		mPrimNext.testSetUp(0.0f, 0.0f);
		
		String[] attrs = 
			{
				"a_Position",
				"a_TexCoord",
		};
		String[] uniformsNormal =
			{
			"u_texture",
		};
		String[] uniformsFade =
			{
			"u_texture",
			"u_color",
		};
		
		
		MyGLShader shaderNormal = new MyGLShader();
		shaderNormal.build(TapGirlActivity.getInstance(), "vshader.txt", "fshader_normal.txt", attrs, uniformsNormal);

	
		MyGLShader shaderFade = new MyGLShader();
		shaderFade.build(TapGirlActivity.getInstance(), "vshader.txt", "fshader_fade.txt", attrs, uniformsFade);
		
		mShaders = new MyGLShader[FRSH_MAX];
		mShaders[FRSH_NORMAL] =  shaderNormal;
		mShaders[FRSH_FADE] = shaderFade;
		
		if (mTextureCurrent != null) 
		{
			mTextureCurrent.reloadTexture(TapGirlActivity.getInstance());
		}
		if (mTextureNext != null) 
		{
			mTextureNext.reloadTexture(TapGirlActivity.getInstance());
		}
	
	}

}
