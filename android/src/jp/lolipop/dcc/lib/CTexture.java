package jp.lolipop.dcc.lib;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

public class CTexture {
	private int mTextureId = -1;

	public int getTextureId() {
		return mTextureId;
	}
	
	public void dispose()
	{
		int [] handles = {mTextureId};
		if (getTextureId() >= 0) {
			GLES20.glDeleteTextures(1, handles, 0);
			mTextureId = -1;
		}
	}
	
	public int loadTexture(String nameAsset, Activity activity)
	{
		assert(mTextureId < 0);
		mTextureId = -1;
		AndroidFileIO fileIO = new AndroidFileIO(activity.getResources().getAssets());
		InputStream is = null;
		try {
			is = fileIO.readAsset(nameAsset);
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
		    mTextureId = textures[0];
		    assert(mTextureId >= 0);
		}
	    // テクスチャユニット0を有効にする
	    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	    // テクスチャオブジェクトをターゲットにバインドする
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);

	    // テクスチャパラメータを設定する
	    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
	    // テクスチャ画像を書き込む
	    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
		return mTextureId;
	}

}
