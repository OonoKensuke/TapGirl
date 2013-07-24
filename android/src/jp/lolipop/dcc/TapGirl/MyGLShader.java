package jp.lolipop.dcc.TapGirl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.util.Log;
import jp.lolipop.dcc.lib.IGLShader;
import jp.lolipop.dcc.lib.MyGLUtil;

public class MyGLShader extends IGLShader {
	static final int VA_POSITION = 0;
	static final int VA_TEXCOORD = 1;
	
	private boolean mTest = true;
	public boolean build(Activity activity)
	{
		boolean result = false;
		if (mTest) {
        	String vshSrc = loadTextAsset("vshader.txt", activity);
        	String fshSrc = loadTextAsset("fshader.txt", activity);
			// シェーダを初期化する
			int program = MyGLUtil.initShaders(vshSrc, fshSrc);

			// attribute変数の格納場所を取得する
			int a_Position = GLES20.glGetAttribLocation(program, "a_Position");
			if (a_Position == -1) {
				throw new RuntimeException("attribute変数の格納場所の取得に失敗");
			}
			// attribute変数に点の座標を代入する
			//GLES20.glVertexAttrib3f(a_Position, 0.0f, 0.0f, 0.0f);
			// 表示されない場合は、
			MyGLUtil.glVertexAttrib3f(a_Position, 0.0f, 0.0f, 0.0f);

			// 画面をクリアする色を設定する
			GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
		}
		else {
			try {
				String[] attrs = null;
				/*
					{
						"a_Position",
						//"a_TexCoord"					
				};
				*/
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
		}
		
		return result;
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
