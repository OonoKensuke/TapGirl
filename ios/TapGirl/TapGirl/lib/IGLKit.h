//
//  IGLKit.h
//  TapGirl
//
//  Created by nakano_michiharu on 7/4/13.
//  Copyright (c) 2013 oono. All rights reserved.
//

#ifndef TapGirl_IGLKit_h
#define TapGirl_IGLKit_h

#import <OpenGLES/ES2/gl.h>
#import <OpenGLES/ES2/glext.h>

#ifndef uint
#define uint unsigned int
#endif
#ifndef uchar
#define uchar unsigned char
#endif

#ifdef DEBUG //Debugビルド

#define debug_NSLog(format, ...) NSLog(format, ## __VA_ARGS__)
#define CHECK_GL_ERROR()	{ int err = glGetError(); \
if (err != GL_NO_ERROR) { debug_NSLog(@"glGetError() = 0x%x\n", err); glGetError(); assert(false); } }

#else //Releaseビルド

#define debug_NSLog(format, ...)
#define CHECK_GL_ERROR()

#endif
#import "IGL.h"
#import "IGLImage.h"
#import "IGLShader.h"
#import "IGLView.h"
#import "CColor.h"
#import "CMatrix.h"
#import "CVec2D.h"

#endif
