# Shake-Reporter
![Icon](https://github.com/Yazan98/Shake-Reporter-Kt/blob/main/icons/rounded_icon.png?raw=true)

[![Maven Central](https://img.shields.io/maven-central/v/com.yazantarifi/shake-reporter.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.yazantarifi%22%20AND%20a:%22shake-reporter%22)

## Description
Shake Reporter is An Android Library Built to Report Crashes, OkHttp Requests in Any Android Application
Based on OkHttp3 Interceptor and UncaughtExceptionHandler in Any Application

Mainly This Library Built to Report Crashes and Network Calls on Release Build, Not Debuggable Build
The Main Reason for this is Release Build is Reporting Crashes Without Any Crashlytics Configuration for Testing Build
also Crashlytics Sometimes Take Too Much Time to Push Crashes to Crashlytics Dashboard and You want to Know what is the Exception ASAP
for This Reason this Library Will show Notification Once UncaughtExceptionHandler Triggered an Exception
and Clicking on the Notification will Show Screen with The History of Crashes

The Library Screen is Accessible Via Icon in Launcher or Shake the Screen Feature Depends if the Shake is Enabled or Not

## Supported Features
1. Crashes History
2. Http Requests Logs
3. Notifications on Crashes
4. Shake Screen To Open Reports Screen
5. Create Report Text File in Shared Media Storage

## Installation

```
allprojects {
    repositories {
        mavenCentral()
    }
}
```

```
dependencies {
    implementation("com.yazantarifi:shake-reporter:1.1.1")
}
```

## Usage

1. Initialize The Library To Declare Crashes Report Listener

> Init has 2 Methods One to Override Default Listener and Another One to Trigger Exception if You want Addetional Work

```
class ApplicationInstance: Application() {

    override fun onCreate() {
        super.onCreate()
        ShakeReporter.init(this)
    }

}
```

2. Register Shake Listener

> If you want To Open Screen Via Shake the Activity Add this Line in OnCreate of The Base Screen

```
    ShakeReporter.startSensorListener(this, ShakeSensorListener(this))
```

3. If you want To Un Register The Shake Listener add This Line to onDestroy

```
    ShakeReporter.destroySensorListener(this, listener)
```

4. Add Network Configuration to Track Http Requests

```
    private fun create() : ApiRequests {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.github.com/")
            .client(OkHttpClient.Builder().addInterceptor(ReporterInterceptor()).build())
            .build()
        return retrofit.create(ApiRequests::class.java)
    }
```

## Library Information
1. RecyclerView Version : 1.2.1
2. OkHttp Version : 4.9.1
3. Min SDK Version Supported : 19
4. Target SDK Version : 31


## Screenshots

Feature Name  | Screenshot
------------- | -------------
Crash Notification  | ![](https://github.com/Yazan98/Shake-Reporter-Kt/blob/main/images/Screenshot_20211001-185842_Shake%20Reporter.jpg?raw=true)
Crashes History | ![](https://github.com/Yazan98/Shake-Reporter-Kt/blob/main/images/Screenshot_20211001-191845_Shake%20Reporter.jpg?raw=true)
Requests History | ![](https://github.com/Yazan98/Shake-Reporter-Kt/blob/main/images/Screenshot_20211001-191908_Shake%20Reporter.jpg?raw=true)
Request Info | ![](https://github.com/Yazan98/Shake-Reporter-Kt/blob/main/images/Screenshot_20211001-191919_Shake%20Reporter.jpg?raw=true)
Export Crashes, Requests | ![](https://github.com/Yazan98/Shake-Reporter-Kt/blob/main/images/Screenshot_20211001-191928_Shake%20Reporter.jpg?raw=true)

## License

Copyright (C) 2021 Shake Reporter is An Open Source Library (Licensed under the MIT License)

