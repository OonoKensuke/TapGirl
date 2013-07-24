package jp.lolipop.dcc.TapGirl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;

import android.app.Activity;
import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.util.Log;
import jp.lolipop.dcc.lib.IGLShader;
import jp.lolipop.dcc.lib.MyGLUtil;

public class MyGLShader extends IGLShader {
	static final int VA_POSITION = 0;
	static final int VA_TEXCOORD = 1;
	
	private int mVertexBuffer = -1;
	private FloatBuffer mPositions = null;
	private int mNumVertices = 0;
	
	public boolean build(Activity activity)
	{
		boolean result = false;
		try {
			String[] attrs = 
				{
					"a_Position",
					//"a_TexCoord"					
			};
			String[] uniforms = null;
			/*
				{
				"u_Sampler"
			};
			*/
        	String vshSrc = loadTextAsset("vshader.txt", activity);
        	String fshSrc = loadTextAsset("fshader.txt", activity);
        	
        	
        	result = buildWithVsh(vshSrc, fshSrc, attrs, uniforms);
		}
		catch (Exception exp)
		{
			Log.d("exception", exp.getMessage());
			assert(false);
		}
		
		return result;
	}
	
	public void testPoint(float x, float y)
	{
  	    mPositions = MyGLUtil.makeFloatBuffer(new float[] { x, y });

 		mNumVertices = 1;
		// バッファオブジェクトを作成する
		int[] vertexBuffer = new int[1];
		GLES20.glGenBuffers(1, vertexBuffer, 0);

		mVertexBuffer = vertexBuffer[0];
		assert(mVertexBuffer >= 0);
	}
	
	public void testUseMyPosition()
	{
 		final int FSIZE = Float.SIZE / Byte.SIZE; // floatのバイト数
		// バッファオブジェクトをターゲットにバインドする
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexBuffer);
		// バッファオブジェクトにデータを書き込む
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, FSIZE * mPositions.limit(), mPositions, GLES20.GL_STATIC_DRAW);
		// a_Position変数にバッファオブジェクトを割り当てる
		GLES20.glVertexAttribPointer(VA_POSITION, 2, GLES20.GL_FLOAT, false, 0, 0);

		// a_Position変数でのバッファオブジェクトの割り当てを有効にする
		GLES20.glEnableVertexAttribArray(VA_POSITION);
	}
		
	/**
	 * @param fileName 読み込むファイルのパス
	 * @param activity リソースマネージャーを使うのに必要なアクティビティのインスタンス
	 * @return
	 */
	public static String loadTextAsset(String fileName, Activity activity)
	{
		String result = null;
      	AssetManager assets = activity.getResources().getAssets();
      	try {
				InputStream in = assets.open(fileName);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				StringBuilder strBuilder = new StringBuilder();
				String tmpStr;
				while ((tmpStr = reader.readLine()) != null) {
					strBuilder.append(tmpStr);
				}
				reader.close();
				result = strBuilder.toString();
			} catch (IOException exp) {
				exp.printStackTrace();
			}
		return result;
	}

	@Override
	protected void onDispose() {
		// TODO 自動生成されたメソッド・スタブ
		
	}

}
