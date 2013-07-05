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
		self.fragmentShader = @""
		"precision mediump float;\n"
		"varying vec2 v_texCoord;\n"
		"uniform lowp vec4 u_color;\n"
		"uniform lowp float u_alpha;\n"
		"uniform sampler2D u_texture;\n"
		"void main(){\n"
		//http://www.slideshare.net/kamiyan2/opengl-es-20
		//初期状態。テクスチャをそのまま
		"	gl_FragColor = texture2D(u_texture, v_texCoord);\n"
		"}\n";
	}
	return self;
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
