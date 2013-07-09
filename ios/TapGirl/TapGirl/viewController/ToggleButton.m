//
//  ToggleButton.m
//  TapGirl
//
//  Created by nakano_michiharu on 7/9/13.
//  Copyright (c) 2013 oono. All rights reserved.
//

#import "ToggleButton.h"

@interface ToggleButton() {
	BOOL _highlightedFlag;
}

@end

@implementation ToggleButton

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
		_highlightedFlag = false;
    }
    return self;
}

- (void)highlightReset:(id)sender{
	UIButton *button = (UIButton *)sender;
	button.highlighted = NO;
}

- (void)highlighted:(id)sender{
	NSLog(@"highlightedFlag");
	UIButton *button = (UIButton *)sender;
	button.highlighted = YES;
}

- (void)buttonPush:(id)sender{
	UIButton *button = (UIButton *)sender;
	NSLog(@"%d",[button isHighlighted]);
	if (_highlightedFlag) {
		[self highlightReset:button];
	}else{
		[self performSelector:@selector(highlighted:) withObject:button afterDelay:0.0];
	}
	_highlightedFlag = !_highlightedFlag;
}



/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code
}
*/

@end
