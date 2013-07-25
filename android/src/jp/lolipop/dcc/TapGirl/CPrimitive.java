package jp.lolipop.dcc.TapGirl;

import java.nio.FloatBuffer;

import android.opengl.GLES20;

import jp.lolipop.dcc.lib.*;

public class CPrimitive {
	private int mVertexBufferId = -1;

	public int getVertexBufferId() {
		return mVertexBufferId;
	}
	
	private int mPrimitiveType = -1;

	public int getPrimitiveType() {
		return mPrimitiveType;
	}

	public void setPrimitiveType(int primitiveType) {
		mPrimitiveType = primitiveType;
	}
	
	private int mNumVertices = 0;
	
	public int getNumVertices() {
		return mNumVertices;
	}

	private FloatBuffer mFloatBuffer = null;
	
	void testSetUp(float x, float y)
	{
		mFloatBuffer = MyGLUtil.makeFloatBuffer(new float[] { x, y });
		int [] vertexBufVal = new int [1];
		GLES20.glGenBuffers(1, vertexBufVal, 0);
		mVertexBufferId = vertexBufVal[0];
		assert(mVertexBufferId >= 0);
		// バッファオブジェクトをターゲットにバインドする
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexBufferId);
		// バッファオブジェクトにデータを書き込む
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, MyGLShader.FSIZE * mFloatBuffer.limit(), mFloatBuffer, GLES20.GL_DYNAMIC_DRAW);
		setPrimitiveType(GLES20.GL_POINTS);
		mNumVertices = 1;
	}

}
