package jp.lolipop.dcc.TapGirl;
//構造体の代わりなので、メンバーはpublic
public class CChangeParam {
	//切り替えのきっかけとなる残りの距離
	public float restLength;
	//切り替えるイメージの番号
	public int indexImage;
	//切り替える時のSE
	public int indexSE;
	//シェーダー
	public int shader;
	//切り替え処理前半の時間（単位は秒）
	public float delayIn;
	//切り替え処理後半の時間（単位は秒）
	public float delayOut;
	
	public CChangeParam(
			float _restLength,
			int _indexImage,
			int _indexSE,
			int _shader,
			float _delayIn,
			float _delayOut
			)
	{
		restLength = _restLength;
		indexImage = _indexImage;
		indexSE = _indexSE;
		shader = _shader;
		delayIn = _delayIn;
		delayOut = _delayOut;
	}
}
