//
//  main.m
//  TapGirl
//
//  Created by nakano_michiharu on 7/3/13.
//  Copyright (c) 2013 oono. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "AppDelegate.h"

int main(int argc, char *argv[])
{
	@autoreleasepool {
		int retVal;
		@try {
			retVal = UIApplicationMain(argc, argv, nil, NSStringFromClass([AppDelegate class]));
		}
		@catch (NSException *exception) {
			NSLog(@"%@", [exception callStackSymbols]);
			@throw exception;
		}
		@finally {
		}
		return retVal;
	}
}
