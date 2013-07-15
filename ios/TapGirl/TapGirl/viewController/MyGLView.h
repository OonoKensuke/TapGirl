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
-(BOOL)loadShaders;
-(void)resetTouchLength:(float)length;
- (void)updateLabelInfo;
+ (MyGLView*)getInstance;

@property(assign, nonatomic) float touchLength;
@property(assign, nonatomic) int roundNum;

@end
