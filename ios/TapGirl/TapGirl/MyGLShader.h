//
//  MyGLShader.h
//  TapGirl
//
//  Created by nakano_michiharu on 7/4/13.
//  Copyright (c) 2013 oono. All rights reserved.
//

#import "IGLShader.h"
#import "IGLImage.h"

enum FRAG_SHADER {
	FRSH_NORMAL = 0,
	FRSH_FADE,
	FRSH_FADE_COLOR,
	
	FRSH_MAX,
	};

@interface MyGLShader : IGLShader
//シェーダーをロード
-(BOOL)loadFragShader:(FRAG_SHADER)frgShader;
//ビルド
-(NSString*)build;
//描画
-(void)drawArraysMy:(uint)primitive positions:(float*)pos
			glImage:(IGLImage*)image texCoords:(float*)texCoods count:(int)count
			  color:(CColor)color alpha:(float)alpha;


@end
