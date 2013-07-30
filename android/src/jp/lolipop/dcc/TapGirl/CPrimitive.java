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
		TapGirlActivity activity = TapGirlActivity.getInstance();
		float wSize = (CDefines.IOS_SCREEN_WIDTH * TapGirlActivity.getMagRatio()) / (float)(activity.getPixelsWidth());
		float ratio = ((CDefines.IOS_SCREEN_HEIGHT - CDefines.IOS_ADS_HEIGHT) / CDefines.IOS_SCREEN_HEIGHT);
		float hSize = wSize * ratio;
		float yAdjust = 1.0f - hSize;
		//boolean isWRatio = TapGirlActivity.isUseWRatio();

		float u = (CDefines.IOS_SCREEN_WIDTH * CDefines.IOS_RETINA_RATIO) / CDefines.TEXTURE_WIDTH;
		float texturePixelsOfY = (CDefines.IOS_SCREEN_HEIGHT - CDefines.IOS_ADS_HEIGHT) * CDefines.IOS_RETINA_RATIO;
		float v = texturePixelsOfY / CDefines.TEXTURE_HEIGHT;
		mFloatBuffer = MyGLUtil.makeFloatBuffer(new float[] {
				x - wSize, y + hSize + yAdjust,  0.0f, 0.0f,
				x - wSize, y - hSize + yAdjust,  0.0f, v,
				x + wSize, y + hSize + yAdjust,  u,    0.0f,
				x + wSize, y - hSize + yAdjust,  u,    v,
				});
		int [] vertexBufVal = new int [1];
		GLES20.glGenBuffers(1, vertexBufVal, 0);
		mVertexBufferId = vertexBufVal[0];
		assert(mVertexBufferId >= 0);
		// バッファオブジェクトをターゲットにバインドする
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexBufferId);
		// バッファオブジェクトにデータを書き込む
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, MyGLShader.FSIZE * mFloatBuffer.limit(), mFloatBuffer, GLES20.GL_DYNAMIC_DRAW);
		
		
		
		setPrimitiveType(GLES20.GL_TRIANGLE_STRIP);
		mNumVertices = 4;
	}

}
