//
//  MyGLShader.h
//  TapGirl
//
//  Created by nakano_michiharu on 7/4/13.
//  Copyright (c) 2013 oono. All rights reserved.
//

#import "IGLShader.h"
#import "IGLImage.h"

@interface MyGLShader : IGLShader
//ビルド
-(NSString*)build;
//描画
-(void)drawArraysMy:(uint)primitive positions:(float*)pos
			glImage:(IGLImage*)image texCoords:(float*)texCoods count:(int)count
			  color:(CColor)color alpha:(float)alpha;


@end
