package jp.lolipop.dcc.TapGirl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import jp.lolipop.dcc.lib.AndroidFileIO;
import jp.lolipop.dcc.lib.CTexture;
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
	
//	private int mTextureCurrent = -1;
//	private int mTextureNext = -1;
	private CTexture mTextureCurrent = null;
	private CTexture mTextureNext = null;
	
	@Override
	public void onDrawFrame(GL10 gl) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

		mShaderCurrent.use();
		mShaderCurrent.drawArraysMy(mPrimCurrent.getPrimitiveType(), mPrimCurrent.getVertexBufferId(), mTextureCurrent.getTextureId(), mPrimCurrent.getNumVertices(), null, 1.0f);
		
		mShaderNext.use();
		mShaderNext.drawArraysMy(mPrimNext.getPrimitiveType(), mPrimNext.getVertexBufferId(), mTextureNext.getTextureId(), mPrimNext.getNumVertices(), null, 1.0f);
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
			mTextureCurrent= new CTexture();
			int id = mTextureCurrent.loadTexture("graphic/texture/sky.jpg", TapGirlActivity.getInstance());
			
			mTextureNext= new CTexture();
			id = mTextureNext.loadTexture("graphic/texture/image02_01.jpg", TapGirlActivity.getInstance());
		}
	}

}
