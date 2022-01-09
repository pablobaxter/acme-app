# Acme App

App to display the best routes for drivers based on a "secret" algorithm.

## Usage
### Creating APK
To generate an APK, set your terminal to the root of the project, and run the following command:
```
./gradlew assemble
```

This will generate a debug and unsigned APK under the `{PROJECT_HOME}/build/outputs/apk` directory. To install the APKs on an emulator or real device, ensure they are running/connected to your machine, developer settings is enabled, and run the following command from the project root:

```
adb install ./build/outputs/apk/debug/app-debug.apk
```

More information about how to use ADB and install apps below
- ADB Setup
  - https://developer.android.com/studio/command-line/adb#move
- Developer Settings Setup
  - https://developer.android.com/studio/debug/dev-options
