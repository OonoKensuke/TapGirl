//
//  IGL.h
//  TapGirl
//
//  Created by nakano_michiharu on 7/4/13.
//  Copyright (c) 2013 oono. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface IGL : NSObject
// インスタンス取得（シングルトン）
+(IGL*)sharedGL;
//ビューサイズ設定
-(void)setViewSize:(CGSize)size;
//ビュー行列取得
-(const float*)viewMatrix;

@end
