//
//  IGLView.h
//  TapGirl
//
//  Created by nakano_michiharu on 7/4/13.
//  Copyright (c) 2013 oono. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface IGLView : UIView
// アニメーションを開始する
- (void)startAnimation;
// アニメーションを停止する
- (void)stopAnimation;
// 描画（中身は空、オーバーライドして使う）
- (void)drawMain;
// fps描画（現バージョンでは、debug_NSLog()で出力のみ）
- (void)drawFps;
// 幅（単位：ピクセル）
@property int width;
// 高さ（単位：ピクセル）
@property int height;

@end
