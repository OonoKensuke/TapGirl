package jp.lolipop.dcc.TapGirl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import jp.lolipop.dcc.lib.AndroidFileIO;
import jp.lolipop.dcc.lib.CColor;
import jp.lolipop.dcc.lib.CTexture;
import jp.lolipop.dcc.lib.MyGLUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

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
	
//	private MyGLShader mShaderCurrent = null;
//	private MyGLShader mShaderNext = null;
	private MyGLShader[] mShaders = null;
	private final int FRSH_NORMAL = 0;
	private final int FRSH_FADE = 1;
	private final int FRSH_MAX = 2;
	
	CPrimitive mPrimCurrent = null;
	CPrimitive mPrimNext = null;
	
	private CTexture mTextureCurrent = null;
	private CTexture mTextureNext = null;
	
	private CColor mColor = new CColor(1.0f, 1.0f, 1.0f, 1.0f);
	
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
	
	@Override
	public void onDrawFrame(GL10 gl) {
		boolean bDrawNormal = true;
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		
		switch (mStep)
		{
			case STEP_INIT:
			{
				
			}
			break;
			case STEP_NORMAL:
			{
				
			}
			break;
		}
		if (bDrawNormal) {
			drawTextureCurrent(false, FRSH_NORMAL);
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.v("info", "MyRenderer#onSurfaceChanged width " + width + " height " + height);
		GLES20.glViewport(0, 0, width, height);
		
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		Log.v("info", "MyRenderer#onSurfaceCreated ");
		GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
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
	
		{
			mTextureCurrent= new CTexture();
			int id = mTextureCurrent.loadTexture("graphic/texture/sky.jpg", TapGirlActivity.getInstance());
			Log.v("info", "texture id = " + id);
			
			mTextureNext= new CTexture();
			id = mTextureNext.loadTexture("graphic/texture/image03_01.jpg", TapGirlActivity.getInstance());
			Log.v("info", "texture id = " + id);
		}
	}

}
