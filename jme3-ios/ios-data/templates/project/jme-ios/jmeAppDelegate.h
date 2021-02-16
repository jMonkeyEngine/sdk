#import <UIKit/UIKit.h>
#import <OpenGLES/ES2/gl.h>
#import <OpenGLES/ES3/gl.h>
#import <GLKit/GLKit.h>
#define __LP64__ 1
#include <jni.h>

@interface jmeAppDelegate : UIResponder <UIApplicationDelegate, GLKViewDelegate, GLKViewControllerDelegate>

@property (nonatomic, retain) IBOutlet UIWindow *window;
@property (nonatomic, retain) IBOutlet GLKView *glview;
@property (nonatomic, strong) GLKViewController *glviewController;
@property (nonatomic, retain) EAGLContext *ctx;
@property JavaVM* vm;
@property jobject app;
@property jobject harness;
@property jmethodID pauseMethod;
@property jmethodID reactivateMethod;
@property jmethodID closeMethod;
@property jmethodID updateMethod;
@property jmethodID drawMethod;
@property jmethodID reshapeMethod;
@property jmethodID injectTouchBegin;
@property jmethodID injectTouchMove;
@property jmethodID injectTouchEnd;

@end
