//
//  gameDefs.h
//  TapGirl
//
//  Created by nakano_michiharu on 7/13/13.
//  Copyright (c) 2013 oono. All rights reserved.
//

#ifndef TapGirl_gameDefs_h
#define TapGirl_gameDefs_h

#pragma mark -SNS

//SNS拡散URL
#define SNS_URL	@"http://www.apple.com/"
//SNSアプリ名
#define SNS_APP_NAME	@"アプリ名"
//SNSハッシュタグ
#define SNS_HASHTAG	@"ハッシュタグ"
//Twitterフォーマット
#define SNS_TWEET_FORMAT	@"アプリを%dcmこすった。%@ #%@ "

//Facebookキャプション
#define FACEBOOK_CAPTION	@"キャプション"
//Facebook説明
#define FACEBOOK_DESCRIPTION	@"アプリ説明"

#pragma mark -広告

//**********必ずアプリ専用のものに書き換える**********
//Admob用の広告枠コード
#define _ADMOB_PUBLISHER_ID  @"a150ff2a3b55ce0"
//App Bankの広告枠コード、広告ID
#define _NEND_ID	@"a6eca9dd074372c898dd1df549301f277c53f2b9"
//App Bankの広告枠コード、SpotID
#define _NEND_SPOT_ID	@"3172"
//**********必ずアプリ専用のものに書き換える**********


#pragma mark -congratulations おめでとう画面
//最後に数字の場所に表示するメッセージ
#define CONGRATURATIONS_MESSAGE	@"Congratulations!"
#define SHOP_PASSWORD	@"password"
//パスワードを表示しにいくまでの時間
#define CONGRATULATIONS_WAIT_TO_PASSWORD	10.0f
//ショップサイトを表示しにいくまでの時間
#define CONGRATULATIONS_WAIT_TO_SHOP	15.0f
//表示するサイト
#define SHOP_SITE_URL	@"http://www.google.co.jp/"
#endif
