package jp.lolipop.dcc.lib;

import android.opengl.GLES20;
import android.util.Log;

public abstract class IGLShader {
	private int mProgramId = -1;
	public int getProgramId() {
		return mProgramId;
	}
	private int mVertexShader = -1;
	public int getVertexShader() {
		return mVertexShader;
	}
	private int mFragmentShader = -1;
	public int getFragmentShader() {
		return mFragmentShader;
	}
	private int [] mUniformLocations = null;
	public int getUniformLocationOf(int index)
	{
		int result = -1;
		assert(mUniformLocations != null);
		assert((index >= 0)&&(index < mUniformLocations.length));
		result = mUniformLocations[index];
		return result;
	}
	
	public IGLShader()
	{
		
	}
	
	public void use()
	{
		assert(getProgramId() >= 0);
		GLES20.glUseProgram(getProgramId());
	}
	
	protected abstract void onDispose();
	
	/**
	 * 後始末
	 */
	public void dispose()
	{
		if (getProgramId() > 0) {
			GLES20.glDeleteProgram(getProgramId());
		}
		onDispose();
	}
	
	/**
	 * @param vshSrc 頂点シェーダーソースコード
	 * @param fshSrc フラグメントシェーダーソースコード
	 * @param attrs 1つ以上のアトリビュート変数名の名前が入った配列
	 * @param uniforms 1つ以上のユニフォーム変数名の名前が入った配列
	 * @return
	 */
	public boolean buildWithVsh(String vshSrc, String fshSrc, String[] attrs, String [] uniforms)
	{
		boolean result = false;
		try {
			//作り直さないとする
			assert(getProgramId() < 0);
			//シェーダーのコンパイルとリンク
			mProgramId = MyGLUtil.createProgram(vshSrc, fshSrc);
			assert(mProgramId > 0);
			// アトリビュート変数（頂点フォーマットメンバ）の設定
			if (attrs != null) {
				assert(attrs.length <= 16);
				for (int i = 0; i < attrs.length; i++) {
					GLES20.glBindAttribLocation(getProgramId(), i, attrs[i]);
				}
			}
			if (uniforms != null) {
				// ユニフォーム変数の位置を取得
				mUniformLocations = new int[uniforms.length];
				for (int i = 0; i < uniforms.length; i++) {
					mUniformLocations[i] = GLES20.glGetUniformLocation(getProgramId(), uniforms[i]);
					if (mUniformLocations[i] < 0) {
						throw new Exception("can't find uniform " + uniforms[i]);
					}
				}
			}
			result = true;
		}
		catch (Exception exp) {
			Log.d("exception", exp.getMessage());
			assert(false);
		}
		return result;
	}

}
