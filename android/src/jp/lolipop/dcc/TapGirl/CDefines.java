package jp.lolipop.dcc.TapGirl;

public class CDefines {
// mark -SNS

//SNS拡散URL
static public final String SNS_URL =	"http://sekai-seifuku.com/";
//Twitterフォーマット
//#define SNS_TWEET_FORMAT	@"アプリを%dcmこすった。%@ #%@ "

//Facebookキャプション
static public final String FACEBOOK_CAPTION =	"キャプション";
//Facebook説明
static public final String FACEBOOK_DESCRIPTION =	"アプリ説明";

// mark -広告

//**********必ずアプリ専用のものに書き換える**********
//Admob用の広告枠コード
static public final String _ADMOB_PUBLISHER_ID =  "a150ff2a3b55ce0";
//App Bankの広告枠コード、広告ID
static public final String _NEND_ID =	"a6eca9dd074372c898dd1df549301f277c53f2b9";
//App Bankの広告枠コード、SpotID
static public final String _NEND_SPOT_ID =	"3172";
//**********必ずアプリ専用のものに書き換える**********

static public final String NAME_SAVE_FILE = "tapgirlsavedata";
// mark -More Apps
static public final String MORE_APPS_SITE =	"http://sekai-seifuku.com/";
//IOS デバイス関連の値
static public final float IOS_SCREEN_WIDTH = 320.0f;
static public final float IOS_SCREEN_HEIGHT = 568.0f;
static public final float IOS_ADS_HEIGHT = 50.0f;
static public final float IOS_RETINA_RATIO = 2.0f;
//テクスチャ
static public final float TEXTURE_WIDTH = 1024.0f;
static public final float TEXTURE_HEIGHT = 2048.0f;
//画面クリアの色関係 0 ~ 1.0f
static public final float CLEAR_RED = 0.0f;
static public final float CLEAR_GREEN = 0.0f;
static public final float CLEAR_BLUE = 1.0f;

//********** twitter4j **********
static String TWITTER_CONSUMER_KEY = "lE1AOoS0x2XtSAVTRSVw";
static String TWITTER_CONSUMER_SECRET = "UjDeJYnClD520OCiWBnQgcnGSMweejphJJIRoUulgyw";
static String TWITTER_PREFERENCE_NAME = "twitter_oauth";
static final String TWITTER_PREF_KEY_SECRET = "oauth_token_secret";
static final String TWITTER_PREF_KEY_TOKEN = "oauth_token";
static final String TWITTER_PREF_KEY_INTENT_URI = "intent_uri";

static final String TWITTER_CALLBACK_URL = "oauth://tapgirltwitter";

static final String TWITTER_IEXTRA_AUTH_URL = "auth_url";
static final String TWITTER_IEXTRA_OAUTH_VERIFIER = "oauth_verifier";
static final String TWITTER_IEXTRA_OAUTH_TOKEN = "oauth_token";

//SNSアプリ名
static public final String SNS_APP_NAME =	"アプリ名";
//SNSハッシュタグ
static public final String SNS_HASHTAG =	"ハッシュタグ";
static final String SNS_TWEET_FORMAT = "アプリを%dcmこすった。%s #%s ";



// mark -congratulations おめでとう画面
//最後に数字の場所に表示するメッセージ
static public final String CONGRATURATIONS_MESSAGE =	"Congratulations!";
static public final String SHOP_PASSWORD =	"password";
//パスワードを表示しにいくまでの時間
static public final float CONGRATULATIONS_WAIT_TO_PASSWORD =	10.0f;
//ショップサイトを表示しにいくまでの時間
static public final float  CONGRATULATIONS_WAIT_TO_SHOP =	15.0f;
//表示するサイト
static public final String SHOP_SITE_URL =	"http://sekai-seifuku.com/";
}
