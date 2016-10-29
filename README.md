[ ![Download](https://api.bintray.com/packages/kevalpatel2106/maven/user-aware-videoview/images/download.svg) ](https://bintray.com/kevalpatel2106/maven/user-aware-videoview/_latestVersion) [![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](https://github.com/kevalpatel2106/UserAwareVideoView) 

[![GitHub forks](https://img.shields.io/github/forks/badges/shields.svg?style=social&label=Fork)](https://github.com/kevalpatel2106/UserAwareVideoView) [![GitHub stars](https://img.shields.io/github/stars/badges/shields.svg?style=social&label=Star)](https://github.com/kevalpatel2106/UserAwareVideoView) [![GitHub followers](https://img.shields.io/github/followers/espadrine.svg?style=social&label=Follow)](https://github.com/kevalpatel2106/UserAwareVideoView)

# UserAwareVideoView
UserAwareVideoView is a customizable VideoView that smartly play and pause the video based on your user is looking at the video or not. This uses Play Services Mobile Vision APIs to detect user's eyes. If the user is not looking at the screen than this will automatically pause the video, so your user does not miss any part of the video.

##How to use this library???
###Gradle dependency:
Add these lines to your `build.gradle` file to start integration. 

```
dependency{
    compile 'com.kevalpatel2106:userawarevideoview:1.0'
}
```

- This library automatically adds `android.permission.CAMERA` permission in your applications `AndroidManifest.xml` file.

###Add to XML layout:
You can use `UserAwareVideoView` just like you user the default VideoView in your layout. 
```
 <com.kevalpatel.userawarevieoview.UserAwareVideoView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:id="@+id/video_view"/>
```

###Initialize in your activity:
- **Step-1:** Check for the Camera permission in runtime.
- **Step-2:** Register `UserAwarenessListener` to get the callbacks from the `UserAwareVideoView` whenever error occurs in eyes detection or eye detection starts stops.
- **Step-3:** Handle errors that may occur while eyes detection. 
```
mVideoView = (UserAwareVideoView) findViewById(R.id.video_view);
mVideoView.setUserAwarenessListener(new UserAwarenessListener() {
            @Override
            public void onErrorOccurred(int errorCode) {
                //Handle errors.
                 switch (errorCode) {
                            case Errors.UNDEFINED:
                                //Unknown error occured. 
                                //This will stop eye tracking, but video will keep playing.
                                break;
                            case Errors.FRONT_CAMERA_NOT_AVAILABLE:
                                //This indicates that device doesnot have the front camera
                                //This will stop eye tracking, but video will keep playing.
                                break;
                            case Errors.CAMERA_PERMISSION_NOT_AVAILABLE:
                                //This indicates that camera permission is not available.
                                //Ask for the runtime camera permission.
                                break;
                            case Errors.LOW_LIGHT:
                                //This indicates that there is dark out side. We cannot detect user's face.
                                //This will stop eye tracking, but video will keep playing.
                                break;
                        }
            }

            @Override
            public void onEyeTrackingStarted() {
                //Eye detection started
            }

            @Override
            public void onEyeTrackingStop() {
                //Eye detection is stopped.
            }
        });
//Atatch your media controller, provide video to play and start the video
//......
//......
```

That's it. UserAwareVideoView is ready to use.

#Contribute:
####Simple 3 step to contribute into this repo:

1. Fork the project. 
2. Make required changes and commit. 
3. Generate pull request. Mention all the required description regarding changes you made.

