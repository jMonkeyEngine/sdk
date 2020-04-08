#include <stdint.h>
#import <CoreGraphics/CoreGraphics.h>
#import "jmeAppDelegate.h"

static JNIEnv*
getEnv(JavaVM* vm)
{
    void* env;
    if ((*vm)->GetEnv(vm, &env, JNI_VERSION_1_4) == JNI_OK) {
        return (JNIEnv*) env;
    } else {
        return 0;
    }
}

@implementation jmeAppDelegate

@synthesize window = _window;
@synthesize vm = _vm;
@synthesize app = _app;
@synthesize harness = _harness;
@synthesize pauseMethod = _pauseMethod;
@synthesize reactivateMethod = _reactivateMethod;
@synthesize closeMethod = _closeMethod;
@synthesize updateMethod = _updateMethod;
@synthesize drawMethod = _drawMethod;
@synthesize reshapeMethod = _reshapeMethod;
@synthesize injectTouchBegin = _injectTouchBegin;
@synthesize injectTouchMove = _injectTouchMove;
@synthesize injectTouchEnd = _injectTouchEnd;
@synthesize ctx = _ctx;
@synthesize glview = _glview;
@synthesize glviewController = _glviewController;

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    /**
     * GLES Context initialization
     **/
    _ctx = [[EAGLContext alloc] initWithAPI:kEAGLRenderingAPIOpenGLES2];
    _glview.context = _ctx;
    _glview.drawableDepthFormat = GLKViewDrawableDepthFormat24;
    _glview.delegate = self;
    
    [EAGLContext setCurrentContext: _ctx];
    
    /**
     * GLES View Controller initialization
     **/
    _glviewController = [[GLKViewController alloc] initWithNibName:nil bundle:nil]; // 1
    _glviewController.view = _glview; // 2
    _glviewController.delegate = self; // 3
    _glviewController.preferredFramesPerSecond = 30; // 4
    _glviewController.paused = NO;
    _window.rootViewController = _glviewController; // 5

    /**
     * Java initilization.
     * Note that though it looks like a JVM is created, in fact only the JNI api is being used here,
     * the whole application and java classpath has been precompiled.
     **/
    JavaVMInitArgs vmArgs;
    vmArgs.version = JNI_VERSION_1_4;
    vmArgs.nOptions = 5;
    vmArgs.ignoreUnrecognized = JNI_TRUE;
    
    JavaVMOption options[vmArgs.nOptions];
    vmArgs.options = options;
    
    options[0].optionString = (char*) "-Davian.bootimage=bootimageBin";
    options[1].optionString = (char*) "-Davian.codeimage=codeimageBin";
    options[2].optionString = (char*) "-Dsun.reflect.inflationThreshold=2147483647";
    options[3].optionString = (char*) "-Xbootclasspath:[resourcesJar]";

    // Enable logging
    NSString *path = [[NSBundle mainBundle] pathForResource:@"logging" ofType:@"properties"];
    NSString *log = [NSString stringWithFormat:@"-Djava.util.logging.config.file=%@", path];
    options[4].optionString = (char*) [log cStringUsingEncoding:NSASCIIStringEncoding];
    //
    
    JavaVM* vm;
    void* env;
    JNI_CreateJavaVM(&vm, &env, &vmArgs);
    JNIEnv* e = (JNIEnv*) env;
    if ((*e)->ExceptionCheck(e)) {
        (*e)->ExceptionDescribe(e);
        (*e)->ExceptionClear(e);
        return NO;
    }
    
    /**
     * iOS Harness initilization.
     * The harness is being called after creating the application.
     **/
    jclass harnessClass = (*e)->FindClass(e, "JmeAppHarness");
    if (! (*e)->ExceptionCheck(e)) {
        jmethodID harnessConstructor = (*e)->GetMethodID(e, harnessClass, "<init>", "(J)V");
        if (! (*e)->ExceptionCheck(e)) {
            jobject harnessObject = (*e)->NewObject(e, harnessClass, harnessConstructor, (jlong)self);
            if (! (*e)->ExceptionCheck(e)) {
                self.harness = harnessObject;
                (*e)->NewGlobalRef(e, harnessObject);
                self.pauseMethod = (*e)->GetMethodID(e, harnessClass, "appPaused", "()V");
                (*e)->ExceptionCheck(e);
                self.reactivateMethod = (*e)->GetMethodID(e, harnessClass, "appReactivated", "()V");
                (*e)->ExceptionCheck(e);
                self.closeMethod = (*e)->GetMethodID(e, harnessClass, "appClosed", "()V");
                (*e)->ExceptionCheck(e);
                self.updateMethod = (*e)->GetMethodID(e, harnessClass, "appUpdate", "()V");
                (*e)->ExceptionCheck(e);
                self.drawMethod = (*e)->GetMethodID(e, harnessClass, "appDraw", "()V");
                (*e)->ExceptionCheck(e);
                self.reshapeMethod = (*e)->GetMethodID(e, harnessClass, "appReshape", "(II)V");
                (*e)->ExceptionCheck(e);
                self.injectTouchBegin = (*e)->GetMethodID(e, harnessClass, "injectTouchBegin", "(IJFF)V");
                (*e)->ExceptionCheck(e);
                self.injectTouchMove = (*e)->GetMethodID(e, harnessClass, "injectTouchMove", "(IJFF)V");
                (*e)->ExceptionCheck(e);
                self.injectTouchEnd = (*e)->GetMethodID(e, harnessClass, "injectTouchEnd", "(IJFF)V");
                (*e)->ExceptionCheck(e);
            }else{
                NSLog(@"Could not create new iOS Harness object");
                (*e)->ExceptionDescribe(e);
                (*e)->ExceptionClear(e);
                return NO;
            }
        }else{
            NSLog(@"Could not find iOS Harness constructor");
            (*e)->ExceptionDescribe(e);
            (*e)->ExceptionClear(e);
            return NO;
        }
    }else{
        (*e)->ExceptionDescribe(e);
        (*e)->ExceptionClear(e);
        NSLog(@"Could not find iOS Harness class");
        return NO;
    }

    self.vm = vm;
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didRotate:) name:UIDeviceOrientationDidChangeNotification object:nil];
    
    //return (*e)->ExceptionCheck(e) ? NO : YES;
    return YES;
}

- (void)applicationWillResignActive:(UIApplication *)application
{
    /*
     Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
     Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
     */
    JNIEnv* e = getEnv(self.vm);
    if (e) {
        (*e)->CallVoidMethod(e, self.harness, self.pauseMethod);
    }
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    /*
     Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
     If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
     */
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    /*
     Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
     */
    JNIEnv* e = getEnv(self.vm);
    if (e) {
        (*e)->CallVoidMethod(e, self.harness, self.reactivateMethod);
    }
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    /*
     Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
     */
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    JNIEnv* e = getEnv(self.vm);
    if (e) {
        (*e)->CallVoidMethod(e, self.harness, self.closeMethod);
    }
}

- (void)dealloc
{
    JNIEnv* e = getEnv(self.vm);
    if (e) {
        if(self.app!=nil){
            (*e)->DeleteGlobalRef(e, self.app);
        }
        if(self.harness!=nil){
            (*e)->DeleteGlobalRef(e, self.harness);
        }
    }
    (*self.vm)->DestroyJavaVM(self.vm);
    [_window release];
    _glview.context = nil;
    [_glview release];
    [_ctx release];
    [super dealloc];
}

- (BOOL)shouldAutorotate
{
    //returns true if want to allow orientation change
    return TRUE;
}

- (NSUInteger)supportedInterfaceOrientations
{
    //decide number of origination tob supported by Viewcontroller.
    return UIInterfaceOrientationMaskAll;
}

- (void)didRotate:(NSNotification *)notification
{
    CGRect originalFrame = [[UIScreen mainScreen] bounds];
    CGRect frame = [self.glview convertRect:originalFrame fromView:nil];
    JNIEnv* e = getEnv(self.vm);
    if (e) {
        float scale = _glview.contentScaleFactor;
        (*e)->CallVoidMethod(e, self.harness, self.reshapeMethod, (int)(frame.size.width * scale), (int)(frame.size.height * scale));
        if ((*e)->ExceptionCheck(e)) {
            NSLog(@"Could not invoke iOS Harness reshape");
            (*e)->ExceptionDescribe(e);
            (*e)->ExceptionClear(e);
        }
    }
}

#pragma mark - GLKViewDelegate

- (void)glkView:(GLKView *)view drawInRect:(CGRect)rect {
    JNIEnv* e = getEnv(self.vm);
    if (e) {
        (*e)->CallVoidMethod(e, self.harness, self.drawMethod);
        if ((*e)->ExceptionCheck(e)) {
            NSLog(@"Could not invoke iOS Harness update");
            (*e)->ExceptionDescribe(e);
            (*e)->ExceptionClear(e);
        }
    }
}

#pragma mark - GLKViewControllerDelegate

- (void)glkViewControllerUpdate:(GLKViewController *)controller {
    JNIEnv* e = getEnv(self.vm);
    if (e) {
        (*e)->CallVoidMethod(e, self.harness, self.updateMethod);
        if ((*e)->ExceptionCheck(e)) {
            NSLog(@"Could not invoke iOS Harness update");
            (*e)->ExceptionDescribe(e);
            (*e)->ExceptionClear(e);
        }
    }

}

#pragma mark - UIResponder

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    NSLog(@"touchesBegan");
    JNIEnv* e = getEnv(self.vm);
    if (e) {
        NSEnumerator *enumerator = [touches objectEnumerator];
        UITouch *touch;
        while (touch = [enumerator nextObject]) {
            CGPoint position = [touch locationInView: nil];
            float scale = _glview.contentScaleFactor;
            // NOTE: cast to jlong is required, otherwise JmeAppHarness receives non-sense values
            (*e)->CallVoidMethod(e, self.harness, self.injectTouchBegin, (jint) touch.hash, (jlong) touch.timestamp, position.x * scale, position.y * scale);
            if ((*e)->ExceptionCheck(e)) {
                NSLog(@"Could not invoke iOS Harness injectTouchBegin");
                (*e)->ExceptionDescribe(e);
                (*e)->ExceptionClear(e);
            }
        }
    }
}

- (void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event {
    NSLog(@"touchesMoved");
    JNIEnv* e = getEnv(self.vm);
    if (e) {
        NSEnumerator *enumerator = [touches objectEnumerator];
        UITouch *touch;
        while (touch = [enumerator nextObject]) {
            CGPoint position = [touch locationInView: nil];
            float scale = _glview.contentScaleFactor;
            // NOTE: cast to jlong is required, otherwise JmeAppHarness receives non-sense values
            (*e)->CallVoidMethod(e, self.harness, self.injectTouchMove, (jint) touch.hash, (jlong) touch.timestamp, position.x * scale, position.y * scale);
            if ((*e)->ExceptionCheck(e)) {
                NSLog(@"Could not invoke iOS Harness injectTouchMove");
                (*e)->ExceptionDescribe(e);
                (*e)->ExceptionClear(e);
            }
        }
    }
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event {
    NSLog(@"touchesEnded");
    JNIEnv* e = getEnv(self.vm);
    if (e) {
        NSEnumerator *enumerator = [touches objectEnumerator];
        UITouch *touch;
        while (touch = [enumerator nextObject]) {
            CGPoint position = [touch locationInView: nil];
            float scale = _glview.contentScaleFactor;
            // NOTE: cast to jlong is required, otherwise JmeAppHarness receives non-sense values
            (*e)->CallVoidMethod(e, self.harness, self.injectTouchEnd, (jint) touch.hash, (jlong) touch.timestamp, position.x * scale, position.y * scale);
            if ((*e)->ExceptionCheck(e)) {
                NSLog(@"Could not invoke iOS Harness injectTouchEnd");
                (*e)->ExceptionDescribe(e);
                (*e)->ExceptionClear(e);
            }
        }
    }
}

- (void)touchesCancelled:(NSSet *)touches withEvent:(UIEvent *)event {
    NSLog(@"touchesCancelled");
}

@end
