package jp.lolipop.dcc.TapGirl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.opengl.GLES20;
import android.util.Log;
import jp.lolipop.dcc.lib.CColor;
import jp.lolipop.dcc.lib.IGLShader;
import jp.lolipop.dcc.lib.MyGLUtil;

public class MyGLShader extends IGLShader {
	static final int VA_POSITION = 0;
	static final int VA_TEXCOORD = 1;

	static final int UL_TEXTURE = 0;
	static final int UL_COLOR = 1;
//	static final int UL_ALPHA = 2;
	
//	private int mVertexBuffer = -1;
//	private FloatBuffer mPositions = null;
//	private int mNumVertices = 0;

	public static final int FSIZE = Float.SIZE / Byte.SIZE; // floatのバイト数
	
	public boolean build(Activity activity, String fileVertexShader, String fileFragShader,
			String[] attrs , String [] uniforms)
	{
		boolean result = false;
		try {
			/*
			String[] attrs = 
				{
					"a_Position",
					"a_TexCoord",
			};
			String[] uniforms =
				{
				"u_texture",
//				"u_color",
//				"u_alpha",
			};
			*/
        	String vshSrc = loadTextAsset(fileVertexShader, activity);
        	String fshSrc = loadTextAsset(fileFragShader, activity);
        	
        	
        	result = buildWithVsh(vshSrc, fshSrc, attrs, uniforms);
		}
		catch (Exception exp)
		{
			Log.d("exception", exp.getMessage());
			assert(false);
		}
		
		return result;
	}
	
	/**
	 * @param primitive 表示するプリミティブタイプ。点、トライアングルストリップなど
	 * @param vertexBuffer 別の場所で確保されている頂点バッファ
	 * @param textureId　テクスチャID
	 * @param count　頂点の数
	 * @param color　描画の際に設定される色
	 * @param alpha　アルファ
	 */
	public void drawArraysMy(int primitive, int vertexBuffer,
			int textureId, int count, CColor color, float alpha)
	{
		{
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
			GLES20.glUniform1i(getUniformLocationOf(UL_TEXTURE), 0);
		}
		if (getUniformCount() > UL_COLOR) {
			assert(color != null);
			GLES20.glUniform4f(getUniformLocationOf(UL_COLOR),
					color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
		}
		// バッファオブジェクトをターゲットにバインドする
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBuffer);
		// a_Position変数にバッファオブジェクトを割り当てる
		GLES20.glVertexAttribPointer(VA_POSITION, 2, GLES20.GL_FLOAT, false, FSIZE * 4, 0);
		GLES20.glEnableVertexAttribArray(VA_POSITION);
		
		// a_Texcoord
		GLES20.glVertexAttribPointer(VA_TEXCOORD, 2, GLES20.GL_FLOAT, false, FSIZE * 4,  FSIZE * 2);
		GLES20.glEnableVertexAttribArray(VA_TEXCOORD);
		
		GLES20.glDrawArrays(primitive, 0, count);
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
