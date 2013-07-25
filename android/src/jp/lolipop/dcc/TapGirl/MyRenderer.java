package jp.lolipop.dcc.TapGirl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import jp.lolipop.dcc.lib.AndroidFileIO;
import jp.lolipop.dcc.lib.MyGLUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import jp.lolipop.dcc.lib.IGLRenderer;

public class MyRenderer extends IGLRenderer {
	
	private MyGLShader mShaderCurrent = null;
	private MyGLShader mShaderNext = null;
	
	CPrimitive mPrimCurrent = null;
	CPrimitive mPrimNext = null;
	
	private int mTextureCurrent = -1;
	private int mTextureNext = -1;
	
	@Override
	public void onDrawFrame(GL10 gl) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

		mShaderCurrent.use();
		mShaderCurrent.drawArraysMy(mPrimCurrent.getPrimitiveType(), mPrimCurrent.getVertexBufferId(), mTextureCurrent, mPrimCurrent.getNumVertices(), null, 1.0f);
		
		mShaderNext.use();
		mShaderNext.drawArraysMy(mPrimNext.getPrimitiveType(), mPrimNext.getVertexBufferId(), mTextureCurrent, mPrimNext.getNumVertices(), null, 1.0f);
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
		mPrimCurrent.testSetUp(0.0f, 0.5f);
		
		mPrimNext = new CPrimitive();
		mPrimNext.testSetUp(0.0f, -0.5f);
		
		mShaderCurrent = new MyGLShader();
		mShaderCurrent.build(TapGirlActivity.getInstance(), "vshader.txt", "fshader_normal.txt");

	
		mShaderNext = new MyGLShader();
		mShaderNext.build(TapGirlActivity.getInstance(), "vshader.txt", "fshader_normal.txt");
	
		{
			//String fileName = "graphic/texture/image02_01.jpg";
			String fileName = "graphic/texture/sky.jpg";
			AndroidFileIO fileIO = new AndroidFileIO(TapGirlActivity.getInstance().getResources().getAssets());
			InputStream is = null;
			try {
				is = fileIO.readAsset(fileName);
			}
			catch (IOException ioExp)
			{
				Log.d("exception", ioExp.getMessage());
				throw new AssertionError();
			}
			// Bitmapの作成
			Bitmap bmp = BitmapFactory.decodeStream(is);
			if (bmp == null) {
				  throw new RuntimeException("画像の読み込みに失敗");
			}
			{
			    // テクスチャオブジェクトを作成する
			    int[] textures = new int[1];
			    GLES20.glGenTextures(1, textures, 0);
			    mTextureCurrent = textures[0];
			    assert(mTextureCurrent >= 0);
			}
		    // テクスチャユニット0を有効にする
		    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		    // テクスチャオブジェクトをターゲットにバインドする
		    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureCurrent);

		    // テクスチャパラメータを設定する
		    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		    // テクスチャ画像を書き込む
		    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
		}
	}

}
