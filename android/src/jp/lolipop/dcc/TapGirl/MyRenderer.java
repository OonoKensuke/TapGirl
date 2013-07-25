package jp.lolipop.dcc.TapGirl;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import jp.lolipop.dcc.lib.MyGLUtil;

import android.opengl.GLES20;
import android.util.Log;

import jp.lolipop.dcc.lib.IGLRenderer;

public class MyRenderer extends IGLRenderer {
	
	private MyGLShader mShaderCurrent = null;
	private MyGLShader mShaderNext = null;
	
	private FloatBuffer mFloatBufferCurrent = null;
	private FloatBuffer mFloatBufferNext = null;
	
	private int mVertexBufferCurrent = -1;
	private int mVertexBufferNext = -1;
	
	@Override
	public void onDrawFrame(GL10 gl) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		mShaderCurrent.use();
//		mShaderCurrent.testUseMyPosition();
//		GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
		mShaderCurrent.drawArraysMy(GLES20.GL_POINTS, mVertexBufferCurrent, 0, 1, null, 1.0f);
		mShaderNext.use();
		mShaderNext.drawArraysMy(GLES20.GL_POINTS, mVertexBufferNext, 0, 1, null, 1.0f);
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
		mFloatBufferCurrent = MyGLUtil.makeFloatBuffer(new float[] { 0.0f, 0.5f });
		mFloatBufferNext = MyGLUtil.makeFloatBuffer(new float[] { 0.0f, -0.5f });
		{
			int [] vertexBufVal = new int [1];
			GLES20.glGenBuffers(1, vertexBufVal, 0);
			mVertexBufferCurrent = vertexBufVal[0];
			assert(mVertexBufferCurrent >= 0);
			GLES20.glGenBuffers(1, vertexBufVal, 0);
			mVertexBufferNext = vertexBufVal[0];
			assert(mVertexBufferNext >= 0);
		}
		
		//
		// バッファオブジェクトをターゲットにバインドする
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexBufferCurrent);
		// バッファオブジェクトにデータを書き込む
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, MyGLShader.FSIZE * mFloatBufferCurrent.limit(), mFloatBufferCurrent, GLES20.GL_DYNAMIC_DRAW);
		// バッファオブジェクトをターゲットにバインドする
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexBufferNext);
		// バッファオブジェクトにデータを書き込む
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, MyGLShader.FSIZE * mFloatBufferNext.limit(), mFloatBufferNext, GLES20.GL_DYNAMIC_DRAW);
		
		mShaderCurrent = new MyGLShader();
		mShaderCurrent.build(TapGirlActivity.getInstance());

	
		mShaderNext = new MyGLShader();
		mShaderNext.build(TapGirlActivity.getInstance());
	
	}

}
