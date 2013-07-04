//
//  IGLShader.h
//  TapGirl
//
//  Created by nakano_michiharu on 7/4/13.
//  Copyright (c) 2013 oono. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "CColor.h"

@interface IGLShader : NSObject
// 初期化
-(id)init;
// GLでカレントプログラムに指定
-(void)use;
// プログラム削除
-(void)deleteProgram;

// 以下サブクラス用
// ビルド。戻り値はエラーメッセージ。成功のときはnil。
-(NSString*)buildWithVsh:(NSString*)vshSrc fsh:(NSString*)fshSrc
	  attributeVariables:(NSArray*)att uniformVariables:(NSArray*)uniforms;
// 頂点属性データを設定（float x,float yの配列)
-(void)setVertexAttribute2f:(float*)positions at:(uint)indexOfMember;
// 整数のユニフォーム変数設定
-(void)setUniformInteger:(int)value at:(int)idx;
// floatのユニフォーム変数設定
-(void)setUniformFloat:(float)value at:(int)idx;
// カラーのユニフォーム変数設定
-(void)setUniformColor:(CColor)color at:(int)idx;
// 4x4行列設定
-(void)setUniformMatrix:(const float*)mat44 at:(int)idx;
// 描画ローレベル
-(void)drawArraysNative:(uint)primitive count:(int)count;

// GLプログラムID
@property uint programId;
//頂点シェーダーソース
@property(strong, nonatomic) NSString* vertexShader;
//フラグメントシェーダーソース
@property(strong, nonatomic) NSString* fragmentShader;
//ビルドエラー
@property(strong, nonatomic) NSString* error;

@end
