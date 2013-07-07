//
//  MyGLShader.mm
//  TapGirl
//
//  Created by nakano_michiharu on 7/4/13.
//  Copyright (c) 2013 oono. All rights reserved.
//

#import "IGLKit.h"
#import "IGL.h"
#import "MyGLShader.h"

@implementation MyGLShader
enum VertexAttribute {
	VA_POSITION,
	VA_TEXCOORD,
};

enum UniformLocation {
	UL_MATRIX,
	UL_TEXTURE,
	UL_COLOR,
	UL_ALPHA,
};


//初期化
-(id)init {
	NSLog(@"%s", __PRETTY_FUNCTION__);
	self = [super init];
	if(self){
		self.vertexShader = @""
		"attribute vec2 a_position;\n"
		"attribute vec2 a_texCoord;\n"
		"uniform mat4 u_matrix;\n"
		"varying vec2 v_texCoord;\n"
		"void main(void){\n"
		"	gl_Position = u_matrix * vec4(a_position, 0.0, 1.0);\n"
		"	v_texCoord = a_texCoord;\n"
		"}\n";
		self.fragmentShader = nil;
#if 0
		@""
		"precision mediump float;\n"
		"varying vec2 v_texCoord;\n"
		"uniform lowp vec4 u_color;\n"
		"uniform lowp float u_alpha;\n"
		"uniform sampler2D u_texture;\n"
		"void main(){\n"
		//http://www.slideshare.net/kamiyan2/opengl-es-20
#if 1
		//初期状態。テクスチャをそのまま
		"	gl_FragColor = texture2D(u_texture, v_texCoord);\n"
#endif
		//ぼんやり乗算（縁黒）
#if 0	//iphone4で17fps
		"	float v = min(sin(v_texCoord.x * 3.14) * sin(v_texCoord.y * 3.14) * 5.0, 1.0);\n"
		"	vec4 col = vec4(v,v,v,1);\n"
		"	gl_FragColor = texture2D(u_texture, v_texCoord) * col;\n"
#endif
		//カラー＋グレースケール(animation)
#if 0	//iphone4で15fps弱
		"	float v1 = sin(v_texCoord.x * 3.14) * sin(v_texCoord.y * 3.14);\n"
		"	vec4 col = texture2D(u_texture, v_texCoord);\n"
		"	float v = dot(col,vec4(0.3, 0.6, 0.1, 0));\n"
		"	vec4 gray = vec4(v,v,v,1)\n;"
		"	gl_FragColor = mix(gray, col, u_alpha) * vec4(v1, v1, v1, 1);\n"
#endif
		//グレースケール+Alpha
#if 0	//iphone4で50fps強
		"	vec4 col = texture2D(u_texture, v_texCoord);\n"
		"	float v = dot(col,vec4(0.3, 0.6, 0.1, 0));\n"
		"	float red = (v * u_alpha) + (col.r * (1.0 - u_alpha));\n"
		"	float blue = (v * u_alpha) + (col.b * (1.0 - u_alpha));\n"
		"	float green = (v * u_alpha) + (col.g * (1.0 - u_alpha));\n"
		"	gl_FragColor = vec4(red,green,blue,1);\n"
#endif
		//セピア
#if 0	//iphone4で60fps
		"	vec4 col = texture2D(u_texture, v_texCoord);\n"
		"	float v = dot(col,vec4(0.3, 0.6, 0.1, 0));\n"
		"	gl_FragColor = vec4(v,v * 0.87,v * 0.75,1);\n"
#endif
		//テクスチャに色を乗算
#if 0
		"	gl_FragColor = texture2D(u_texture, v_texCoord) * u_color;\n"
#endif
		//テクスチャのフェードインアウト
#if 0
		"	gl_FragColor = texture2D(u_texture, v_texCoord) * u_alpha;\n"
#endif
		"}\n";
#endif
	}
	return self;
}
//シェーダーをロード
-(BOOL)loadFragShader:(FRAG_SHADER)frgShader
{
	BOOL result = false;
	@try {
		const char *shaders[] = {
			"shader_normal",
		};
		NSString* nameFile = [NSString stringWithFormat:@"%s", shaders[(int)frgShader]];
		NSString* fCodePath = [[NSBundle mainBundle] pathForResource:nameFile ofType:@"txt"];
		self.fragmentShader = [[NSString alloc] initWithContentsOfFile:fCodePath
															  encoding:NSUTF8StringEncoding
																 error:nil];
		result = true;
	}
	@catch (NSException *exception) {
		NSLog(@"%@", [exception reason]);
		assert(false);
	}
	return result;
}


//ビルド
-(NSString*)build{
	NSLog(@"%s", __PRETTY_FUNCTION__);
	NSArray* attribs = [NSArray arrayWithObjects:@"a_position", @"a_texCoord", nil];
	NSArray* uniforms = [NSArray arrayWithObjects:@"u_matrix", @"u_texture",
						 @"u_color", @"u_alpha", nil];
	return [self buildWithVsh:self.vertexShader fsh:self.fragmentShader
		   attributeVariables:attribs uniformVariables:uniforms];
}

//描画
-(void)drawArraysMy:(uint)primitive positions:(float*)pos
			glImage:(IGLImage*)image texCoords:(float*)texCoods count:(int)count
			  color:(CColor)color alpha:(float)alpha{
	//NSLog(@"%s", __PRETTY_FUNCTION__);
	[self use];
	glEnable(GL_BLEND);CHECK_GL_ERROR();
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);CHECK_GL_ERROR();
	glActiveTexture(GL_TEXTURE0);CHECK_GL_ERROR();//TextureUnit0
	[self setUniformInteger:0 at:UL_TEXTURE];//TextureUnit0
	glBindTexture(GL_TEXTURE_2D, image.textureId);CHECK_GL_ERROR();
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);//GL_REPEATは、2のべき乗サイズのテクスチャのときのみ可
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
	[self setVertexAttribute2f:pos at:VA_POSITION];
	[self setVertexAttribute2f:texCoods at:VA_TEXCOORD];
	[self setUniformColor:color at:UL_COLOR];
	[self setUniformMatrix:[IGL sharedGL].viewMatrix at:UL_MATRIX];
	[self setUniformFloat:alpha at:UL_ALPHA];
	[self drawArraysNative:primitive count:count];
}

@end
