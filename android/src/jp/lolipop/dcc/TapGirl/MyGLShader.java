package jp.lolipop.dcc.TapGirl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;
import jp.lolipop.dcc.lib.IGLShader;

public class MyGLShader extends IGLShader {
	public boolean build(Activity activity)
	{
		boolean result = false;
		try {
			String[] attrs = {
					"a_Position",
					"a_TexCoord"					
			};
			String[] uniforms = {
				"u_Sampler"
			};
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
