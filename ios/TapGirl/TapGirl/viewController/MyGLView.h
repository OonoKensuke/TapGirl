//
//  MyGLView.h
//  TapGirl
//
//  Created by nakano_michiharu on 7/4/13.
//  Copyright (c) 2013 oono. All rights reserved.
//

#import "IGLView.h"
#import "MyGLShader.h"

@interface MyGLView : IGLView
// 描画
-(void)drawMain;
-(NSString*)buildShader;

@property(assign, nonatomic) float touchLength;

@end
