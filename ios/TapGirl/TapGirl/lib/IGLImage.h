//
//  IGLImage.h
//  TapGirl
//
//  Created by nakano_michiharu on 7/4/13.
//  Copyright (c) 2013 oono. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface IGLImage : NSObject
// 初期化
-(id)initWithUIImage:(UIImage*)uiImage;
// ロード
-(BOOL)loadImage:(UIImage*)uiImage;

// 画像の幅（単位：px）
@property int width;
// 画像の高さ（単位：px）
@property int height;
// GLのテクスチャID
@property uint textureId;

@end
