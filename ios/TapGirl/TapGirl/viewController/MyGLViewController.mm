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
#import <Twitter/Twitter.h>
#import "MoreGamesViewController.h"
extern "C" {
#import "GADBannerView.h"
};
#import "FacebookPostViewController.h"

#include "gameDefs.h"

#define _SAVETAG_TOUCH_LENGTH	@"SaveTouchLength"
#define _SAVETAG_ROUND_NUM		@"SaveRoundNum"

@interface MyGLViewController ()
{
	GADBannerView *_admobView;
	BOOL _adsInitialized;
	BOOL _isAppeared;
}
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
	_adsInitialized = false;
	_isAppeared = false;
	BOOL bLoadResult = [self loadData];
	assert(bLoadResult);
}

- (void)viewWillAppear:(BOOL)animated
{
	debug_NSLog(@"%s", __PRETTY_FUNCTION__);
	[super viewWillAppear:animated];
	//ナビゲーションバーは非表示。MoreGamesなどから返った時にもここを通る
	[self.navigationController setNavigationBarHidden:true];
	//self.glView.shader.fragmentShader = self.fshSource;
	if (!_isAppeared) {
		BOOL resultLoad = [self.glView loadShaders];
		assert(resultLoad);
		NSString* error = [self.glView buildShader];
		if (error != nil) {
			debug_NSLog(@"%@", error);
		}
		/*
		ChangeData* changeData = [ChangeData getInstance];
		int lenInit = (int)(changeData.restLength);
		self.countLabel.text = [NSString stringWithFormat:@"%d", lenInit];
		 */
		[self.glView updateLabelInfo];
		_isAppeared = true;
	}

	[self initAds];
}

- (void)viewWillDisappear:(BOOL)animated
{
	debug_NSLog(@"%s", __PRETTY_FUNCTION__);
	[super viewWillDisappear:animated];
	[self.glView stopAnimation];
	[self saveData];
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
    [_btnMoreApps release];
	[_btnTweet release];
	[_btnFacebook release];
    [_roundLabel release];
	[super dealloc];
}
- (void)viewDidUnload {
	debug_NSLog(@"%s", __PRETTY_FUNCTION__);
	[self setGlView:nil];
    [self setCountLabel:nil];
    [self setBtnToggleSound:nil];
    [self setBtnMoreApps:nil];
	[_admobView release];
	[self setBtnTweet:nil];
	[self setBtnFacebook:nil];
    [self setRoundLabel:nil];
	[super viewDidUnload];
	self.glView = nil;
}

#pragma mark -Ads

- (BOOL)initAds
{
	BOOL result = false;
	if (!_adsInitialized) {
		NSArray *arrayLang = [NSLocale preferredLanguages];
		NSString *szLang  = [arrayLang objectAtIndex:0];
		debug_NSLog(@"%@", szLang);
		if ([szLang isEqualToString:@"ja"]) {
			result = [self initNend];
		}
		else {
			result = [self initAdmob];
		}
		_adsInitialized = true;
	}
	return result;
}

- (BOOL)initAdmob
{
	BOOL result = false;
	if (_admobView == nil) {
		_admobView = [[GADBannerView alloc] initWithAdSize:kGADAdSizeBanner];
		CGRect rect = _admobView.frame;
		rect.origin.y = self.glView.frame.size.height - rect.size.height;
		[_admobView setFrame:rect];
		debug_NSLog(@"%f", rect.origin.x);
		_admobView.adUnitID = _ADMOB_PUBLISHER_ID;
		_admobView.rootViewController = self;
		[self.glView addSubview:_admobView];
		[_admobView loadRequest:[GADRequest request]];
		result = true;
	}
	return result;
}

- (BOOL)initNend
{
	BOOL result = false;
	if (self.nadView == nil) {
		float y = self.glView.frame.size.height - NAD_ADVIEW_SIZE_320x50.height;
		self.nadView = [[NADView alloc] initWithFrame:CGRectMake(0.0f, y,
																 NAD_ADVIEW_SIZE_320x50.width,
																 NAD_ADVIEW_SIZE_320x50.height)];
		[self.nadView setNendID:_NEND_ID spotID:_NEND_SPOT_ID];
		[self.nadView setDelegate:self];
		[self.nadView load];
		[self.glView addSubview:self.nadView];		
	}
	return result;
}

- (void)nadViewDidFinishLoad:(NADView *)adView
{
	debug_NSLog(@"%s", __PRETTY_FUNCTION__);
}

- (void)nadViewDidReceiveAd:(NADView *)adView
{
	debug_NSLog(@"%s", __PRETTY_FUNCTION__);
}

- (void)nadViewDidFailToReceiveAd:(NADView *)adView
{
	debug_NSLog(@"%s", __PRETTY_FUNCTION__);
}

- (void)nadViewDidClickAd:(NADView *)adView
{
	debug_NSLog(@"%s", __PRETTY_FUNCTION__);
}
#pragma mark -SNS
- (void)tweet
{
	int len = (int)(self.glView.touchLength);
	NSString *str = [NSString stringWithFormat:SNS_TWEET_FORMAT, len, SNS_APP_NAME, SNS_HASHTAG];
	TWTweetComposeViewController *controller = [[TWTweetComposeViewController alloc]init];
	[controller setInitialText:str];
	[controller addURL:[NSURL URLWithString:SNS_URL]];
	
	controller.completionHandler = ^(TWTweetComposeViewControllerResult res) {
		if (res == TWTweetComposeViewControllerResultDone) {
			debug_NSLog(@"tweet done");
		}
		else if (res == TWTweetComposeViewControllerResultCancelled) {
			debug_NSLog(@"tweet cancel");
		}
	};
	[self presentModalViewController:controller animated:true];
}
- (void)facebookBySocialframework
{
	int len = (int)(self.glView.touchLength);
	NSString *str = [NSString stringWithFormat:SNS_TWEET_FORMAT, len, SNS_APP_NAME, SNS_HASHTAG];
	SLComposeViewController *facebookPostVC = [SLComposeViewController
											   composeViewControllerForServiceType:SLServiceTypeFacebook];
	[facebookPostVC setInitialText:str];
	[self presentViewController:facebookPostVC animated:YES completion:nil];	
}

#pragma mark -Save Load
- (BOOL)saveData
{
	BOOL result = false;
	@try {
		debug_NSLog(@"%s", __PRETTY_FUNCTION__);
		NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
		int len = (int)self.glView.touchLength;
		[userDefaults setInteger:len forKey:_SAVETAG_TOUCH_LENGTH];
		[userDefaults setInteger:self.glView.roundNum forKey:_SAVETAG_ROUND_NUM];
		[userDefaults synchronize];
	}
	@catch (NSException *exception) {
		NSLog(@"%s %@ %@", __FUNCTION__, [exception name], [exception reason]);
	}
	return result;
}

- (BOOL)loadData
{
	BOOL result = false;
	@try {
		int iData = -1;
		debug_NSLog(@"%s", __PRETTY_FUNCTION__);
		NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
		iData = [userDefaults integerForKey:_SAVETAG_TOUCH_LENGTH];
		debug_NSLog(@"load len = %d", iData);
		[self.glView resetTouchLength:(float)iData];
		iData = [userDefaults integerForKey:_SAVETAG_ROUND_NUM];
		self.glView.roundNum = iData;
		
		result = true;
	}
	@catch (NSException *exception) {
		NSLog(@"%s %@ %@", __FUNCTION__, [exception name], [exception reason]);
	}
	return result;
}

#pragma mark -IBAction

- (IBAction)onPushButton:(id)sender
{
	debug_NSLog(@"%s", __PRETTY_FUNCTION__);
	if (sender == self.btnToggleSound) {
		[self.btnToggleSound buttonPush:sender];
		if (self.btnToggleSound.highlighted) {
			debug_NSLog(@"sound off");
		}
		else {
			debug_NSLog(@"sound on");
		}
	}
	else if (sender == self.btnMoreApps) {
		debug_NSLog(@"more apps");
		MoreGamesViewController *controller = [[MoreGamesViewController alloc]initWithNibName:@"MoreGamesViewController" bundle:nil];
		[self.navigationController pushViewController:controller animated:true];
		[controller release];
	}
	else if (sender == self.btnTweet) {
		[self tweet];
	}
	else if (sender == self.btnFacebook) {
		if (NSClassFromString(@"SLComposeViewController")) {
			//use social framework
			debug_NSLog(@"use social.framework");
			[self facebookBySocialframework];
		}
		else {
			//
			debug_NSLog(@"use facebook SDK");
			FacebookPostViewController* controller = [[FacebookPostViewController alloc] initWithNibName:@"FacebookPostViewController" bundle:nil];
			[self.navigationController pushViewController:controller animated:true];
			[controller release];
		}
	}
}

@end
