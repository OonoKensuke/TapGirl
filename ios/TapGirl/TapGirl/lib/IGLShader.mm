//
//  IGLShader.mm
//  TapGirl
//
//  Created by nakano_michiharu on 7/4/13.
//  Copyright (c) 2013 oono. All rights reserved.
//

#import "IGLShader.h"
#import "IGLKit.h"

// プライベート
@interface IGLShader() {
	// ユニフォーム変数位置配列
	int* uniformLacations;
}
// 1つのシェーダをコンパイル。返り値はシェーダオブジェクト。エラー時は0が返る。
-(uint)compileShader:(uint)type source:(NSString*)source;
// シェーダをリンクしてプログラムをセットアップ
-(BOOL)linkProgram:(uint)vShader fShader:(uint)fShader;
@end


@implementation IGLShader

@synthesize vertexShader = _vertexShader;
@synthesize fragmentShader = _fragmentShader;
@synthesize error = _error;
@synthesize programId = _programId;

// 初期化
- (id)init {
	NSLog(@"%s", __PRETTY_FUNCTION__);
    self = [super init];
    if (self) {
		self.programId = 0;
		uniformLacations = nil;
	}
	return self;
}


// 破棄
- (void)dealloc {
	NSLog(@"%s", __PRETTY_FUNCTION__);
	[self deleteProgram];
	[super dealloc];
}

// プログラム削除
-(void)deleteProgram {
	NSLog(@"%s", __PRETTY_FUNCTION__);
	if(self.programId){
		glDeleteProgram(self.programId);CHECK_GL_ERROR();
		self.programId = 0;
	}
	free(uniformLacations);
	uniformLacations = nil;
}

// GLでカレントプログラムに指定
-(void)use {
	//NSLog(@"%s", __PRETTY_FUNCTION__);
	assert(self.programId);//ビルドされていない
	glUseProgram(self.programId);
}

// 1つのシェーダをコンパイル。返り値はシェーダオブジェクト。エラー時は0が返る。
-(uint)compileShader:(uint)type source:(NSString*)source {
	NSLog(@"%s", __PRETTY_FUNCTION__);
	uint shader;
	int compiled;
	
	shader = glCreateShader(type);CHECK_GL_ERROR();
	if (shader == GL_FALSE) {
		assert(false);
		return 0;
	}
	
	const char* src = [source UTF8String];
	glShaderSource(shader, 1, &src, NULL);CHECK_GL_ERROR();
	glCompileShader(shader);CHECK_GL_ERROR();
	glGetShaderiv(shader, GL_COMPILE_STATUS, &compiled);CHECK_GL_ERROR();
	if (!compiled) {//コンパイルエラーのとき
		NSString* name = @"";
		if(type==GL_VERTEX_SHADER){
			name = @"Vertex Shader";
		} else if(type==GL_FRAGMENT_SHADER){
			name = @"Fragment Shader";
		}
		GLint infoLen=0;
		glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &infoLen);CHECK_GL_ERROR();
		if (infoLen > 0) {
			char* infoLog = (char*)malloc(sizeof(char)*infoLen);
			glGetShaderInfoLog(shader, infoLen, NULL, infoLog);CHECK_GL_ERROR();
			self.error = [NSString stringWithFormat:@"Compile Error: %@:\n%@",
						  name, [NSString stringWithUTF8String:infoLog]];
			free(infoLog);
		} else {
			self.error = [NSString stringWithFormat:@"Compile Error: %@:Unknown", name];
		}
		glDeleteShader(shader);CHECK_GL_ERROR();
		[self deleteProgram];
		debug_NSLog(@"%@", self.error);
		return 0;
	}
	return shader;
}

// シェーダをリンクしてプログラムをセットアップ
-(BOOL)linkProgram:(uint)vShader fShader:(uint)fShader {
	NSLog(@"%s", __PRETTY_FUNCTION__);
	glAttachShader(self.programId, vShader);CHECK_GL_ERROR();
	glAttachShader(self.programId, fShader);CHECK_GL_ERROR();
	
	glLinkProgram(self.programId);CHECK_GL_ERROR();
	
	GLint linked;
	glGetProgramiv(self.programId, GL_LINK_STATUS, &linked);CHECK_GL_ERROR();
	if (!linked) {// リンクエラーのとき
		GLint infoLen=0;
		glGetProgramiv(self.programId, GL_INFO_LOG_LENGTH, &infoLen);CHECK_GL_ERROR();
		if (infoLen > 0) {
			char* infoLog = (char*)malloc(sizeof(char)*infoLen);
			glGetProgramInfoLog(self.programId, infoLen, NULL, infoLog);CHECK_GL_ERROR();
			self.error = [NSString stringWithFormat:@"Link Error: %@",
						  [NSString stringWithUTF8String:infoLog]];
			free(infoLog);
		} else {
			self.error = @"Link Error: Unknown";
		}
		glDetachShader(self.programId, vShader);CHECK_GL_ERROR();
		glDetachShader(self.programId, fShader);CHECK_GL_ERROR();
		glDeleteShader(vShader);CHECK_GL_ERROR();
		glDeleteShader(fShader);CHECK_GL_ERROR();
		[self deleteProgram];
		debug_NSLog(@"%@", self.error);
		return false;
	}
	// リンク成功のとき
	glDetachShader(self.programId, vShader);CHECK_GL_ERROR();
	glDetachShader(self.programId, fShader);CHECK_GL_ERROR();
	glDeleteShader(vShader);CHECK_GL_ERROR();
	glDeleteShader(fShader);CHECK_GL_ERROR();
	return true;
}

// ビルド。戻り値はエラーメッセージ。成功のときはnil。
-(NSString*)buildWithVsh:(NSString*)vshSrc fsh:(NSString*)fshSrc
	  attributeVariables:(NSArray*)att uniformVariables:(NSArray*)uniforms{
	NSLog(@"%s", __PRETTY_FUNCTION__);
	assert(fshSrc);
	assert(vshSrc);
	assert(att);
	[self deleteProgram];
	self.error = @"";
	self.programId = glCreateProgram();CHECK_GL_ERROR();
	self.vertexShader = vshSrc;
	self.fragmentShader = fshSrc;
	// 頂点シェーダのコンパイル
	uint vShader = [self compileShader:GL_VERTEX_SHADER source:vshSrc];
	if(vShader == 0){
		[self deleteProgram];
		return self.error;
	}
	// フラグメントシェーダのコンパイル
	uint fShader = [self compileShader:GL_FRAGMENT_SHADER source:fshSrc];
	if(fShader == 0){
		[self deleteProgram];
		return self.error;
	}
	// アトリビュート変数（頂点フォーマットメンバ）の設定
	for(int i = 0; i < att.count; i++){
		NSString* name = [att objectAtIndex:i];
		glBindAttribLocation(self.programId, i, [name UTF8String]);CHECK_GL_ERROR();
	}
	// リンク
	BOOL link = [self linkProgram:vShader fShader:fShader];
	if( link == NO){
		[self deleteProgram];
		return self.error;
	}
	// ユニフォーム変数の位置を取得
	NSMutableString* err = nil;
	uniformLacations = (int*)malloc(uniforms.count * sizeof(int));
	for (int i = 0; i < uniforms.count; i++) {
		NSString* name = [uniforms objectAtIndex:i];
		uniformLacations[i] = glGetUniformLocation(self.programId, [name UTF8String]);CHECK_GL_ERROR();
		if(uniformLacations[i] < 0){//エラーのとき
			if(err == nil){//最初のとき
				err = [[NSMutableString alloc] initWithString:@"Uniform Error:\n"];
			}
			[err appendFormat:@"Can't find uniform variable '%@'.\n", name];
		}
	}
	if(err){// ユニフォーム変数が見つからなかったとき
		debug_NSLog(@"%@", err);
		//[self deleteProgram];
		self.error = err;
		return nil;
	}
	return nil;
}

// 頂点属性データを設定（float x,float yの配列)
-(void)setVertexAttribute2f:(float*)positions at:(uint)indexOfMember {
	//NSLog(@"%s", __PRETTY_FUNCTION__);
	glEnableVertexAttribArray(indexOfMember);CHECK_GL_ERROR();
	glVertexAttribPointer(indexOfMember, 2, GL_FLOAT, false, 0, positions);CHECK_GL_ERROR();
}

// 整数のユニフォーム変数設定
-(void)setUniformInteger:(int)value at:(int)idx {
	//NSLog(@"%s", __PRETTY_FUNCTION__);
	assert(self.programId);//ビルドは成功していなければならない
	glUniform1i(uniformLacations[idx], value);CHECK_GL_ERROR();
}

// floatのユニフォーム変数設定
-(void)setUniformFloat:(float)value at:(int)idx {
	//NSLog(@"%s", __PRETTY_FUNCTION__);
	assert(self.programId);//ビルドは成功していなければならない
	glUniform1f(uniformLacations[idx], value);CHECK_GL_ERROR();
}

// カラーのユニフォーム変数設定
-(void)setUniformColor:(CColor)color at:(int)idx {
	//NSLog(@"%s", __PRETTY_FUNCTION__);
	assert(self.programId);//ビルドは成功していなければならない
	glUniform4f(uniformLacations[idx], color.r, color.g, color.b, color.a);CHECK_GL_ERROR();
}

// 4x4行列設定
-(void)setUniformMatrix:(const float*)mat44 at:(int)idx {
	//NSLog(@"%s", __PRETTY_FUNCTION__);
	assert(self.programId);//ビルドは成功していなければならない
	glUniformMatrix4fv(uniformLacations[idx], 1, FALSE, mat44);CHECK_GL_ERROR();
}

// 描画ローレベル
-(void)drawArraysNative:(uint)primitive count:(int)count {
	//NSLog(@"%s", __PRETTY_FUNCTION__);
	assert(self.programId);//ビルドは成功していなければならない
	glDrawArrays(primitive, 0, count);
}

@end
