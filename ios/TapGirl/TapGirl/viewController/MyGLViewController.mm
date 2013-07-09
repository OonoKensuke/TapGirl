//
//  MyGLViewController.m
//  TapGirl
//
//  Created by nakano_michiharu on 7/3/13.
//  Copyright (c) 2013 oono. All rights reserved.
//

#import "MyGLViewController.h"
#import "IGLKit.h"
#import "ChangeData.h"

@interface MyGLViewController ()
@property(strong, nonatomic) NSString* fshSource;

@end

static MyGLViewController* s_Instance = nil;

@implementation MyGLViewController
@synthesize glView = _glView;
@synthesize fshSource = _fshSource;

+(MyGLViewController*) getInstance
{
	return s_Instance;
}

#pragma mark -view life cycle
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
	debug_NSLog(@"%s", __PRETTY_FUNCTION__);
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
	debug_NSLog(@"%s", __PRETTY_FUNCTION__);
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
	self.glView.contentScaleFactor = [UIScreen mainScreen].scale;
	s_Instance = self;
}

- (void)viewWillAppear:(BOOL)animated
{
	debug_NSLog(@"%s", __PRETTY_FUNCTION__);
	[super viewWillAppear:animated];
	//self.glView.shader.fragmentShader = self.fshSource;
	BOOL resultLoad = [self.glView loadShaders];
	assert(resultLoad);
	NSString* error = [self.glView buildShader];
	if (error != nil) {
		debug_NSLog(@"%@", error);
	}
	ChangeData* changeData = [ChangeData getInstance];
	const CHANGE_PARAM *pParam = [changeData getChangeParam];
	int lenInit = (int)(pParam->restLength);
	self.countLabel.text = [NSString stringWithFormat:@"%d", lenInit];
}

- (void)viewDidAppear:(BOOL)animated
{
	debug_NSLog(@"%s", __PRETTY_FUNCTION__);
	[super viewDidAppear:animated];
	[self.glView startAnimation];
}

- (void)didReceiveMemoryWarning
{
	debug_NSLog(@"%s", __PRETTY_FUNCTION__);
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)dealloc {
	debug_NSLog(@"%s", __PRETTY_FUNCTION__);
	[_glView release];
    [_countLabel release];
	s_Instance = nil;
    [_btnToggleSound release];
	[super dealloc];
}
- (void)viewDidUnload {
	debug_NSLog(@"%s", __PRETTY_FUNCTION__);
	[self setGlView:nil];
    [self setCountLabel:nil];
    [self setBtnToggleSound:nil];
	[super viewDidUnload];
	self.glView = nil;
}

- (IBAction)onPushButton:(id)sender
{
	debug_NSLog(@"%s", __PRETTY_FUNCTION__);
}

@end
