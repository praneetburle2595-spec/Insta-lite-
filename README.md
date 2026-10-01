# Focus Layer — Android prototype v0.1

Companion app for the official Instagram Android app. Covers unrecognized screens with an accessibility overlay; only recognized inbox, conversation and call screens are allowed. Feed, profiles, Stories, Explore and Reels are blocked by default. No Instagram login, root or modified Instagram APK is used.

## Current status — read before installing

Source prototype, not a verified working Instagram blocker. The screen-policy unit checks have been run with Java 17. GitHub Actions successfully compiled and generated a debug APK on 1 October 2026 (run 36807073171); all seven screen-policy checks passed. No device/integration test has been performed. The Instagram view IDs are candidate identifiers, not confirmed against your installed version. Messaging and calls may remain blocked until those IDs are calibrated. Accessibility events happen after a screen changes, so content may briefly appear before a cover. Missing accessibility trees, service interruption, in-chat inline media, or changing Instagram interfaces can bypass protection. This is a voluntary focus aid, not tamperproof enforcement. Browser Instagram, Instagram Lite and cloned apps are outside scope.

## Build on a computer (Android Studio)

1. Extract this folder and open `focus-layer` in Android Studio.
2. Use JDK 17 for Gradle. Install Android SDK Platform 35 and Build Tools 35.0.0 through SDK Manager. Let Gradle sync finish; internet access is needed.
3. Build > Generate App Bundles or APKs > Generate APKs (menu wording varies by Android Studio version), or run `gradle :app:assembleDebug` with Gradle 8.9 installed.
4. The debug APK is `app/build/outputs/apk/debug/app-debug.apk`. It is signed automatically by your local debug key. Keep the same key for subsequent updates, or uninstall/reinstall (local counter resets).

## Build without Android Studio

The project is published in this repository with `settings.gradle` at the repository root. In Actions, run **Build Android APK**. Download the **Focus-Layer-debug-APK** artifact from the completed run and unzip it to obtain `app-debug.apk`. The workflow uses third-party GitHub Actions and Google/Maven build downloads. The workflow was run successfully; the APK is available in the completed run's artifacts. Workflow debug keys are ephemeral: future APKs may need uninstall/reinstall. For durable releases configure your own signing key; never commit private keys.

## Samsung / Android phone setup

1. Transfer the APK to your phone, open it and allow this file source to install apps if prompted. Turn off that install-source permission afterward.
2. Open Focus Layer > Set up protection. Read the disclosure and choose Open settings.
3. In Settings > Accessibility > Installed apps (or Installed services), select Focus Layer and enable it.
4. If Android reports restricted settings, open Settings > Apps > Focus Layer > the top-right menu > Allow restricted settings, if your device provides that option. Return to Accessibility and enable the service. Never disable Play Protect or device security protections to install this prototype.
5. Open Instagram. The cover should appear over the feed. Tap Go to messages. This clicks a recognized inbox button in Instagram; it does not use an undocumented DM deep link. If that button is not recognized, open a direct-message notification instead.
6. Keep normal Instagram notification permission enabled for DM alerts. Focus Layer does not access notifications.
7. If the service stops while idle, inspect Samsung sleeping-app/battery settings for Focus Layer. Prefer the default settings first.

## Acceptance tests on YOUR phone

- Inbox opens and text, image, voice messages can be sent/received.
- A DM notification opens its conversation without the cover.
- Audio/video calls can be answered, ended and switched back to the thread.
- Home, Reels, Explore, Stories and profiles are covered.
- A Reel opened from a DM stays covered, including after Back and repeated taps.
- Test inline video in a DM: this version does NOT inspect message content and cannot guarantee blocking inline Reel playback.
- Exiting Instagram dismisses the cover, and other apps are unaffected.
- After force-stop, reboot and screen lock/unlock, confirm service/protection status.
- Check empty inbox, message requests, new-message composer, attachments, keyboard, permission dialogs and different language settings. Unrecognized Instagram screens intentionally remain covered.

Do not rely on the blocker until these pass. A screenshot alone cannot confirm IDs. For calibration, a developer can connect the phone with USB debugging, use `adb shell uiautomator dump /sdcard/window.xml`, then `adb pull /sdcard/window.xml` on each relevant screen with Focus Layer disabled. These dumps may contain private message text/usernames: use a test conversation and redact all content before sharing. Only structural resource IDs are needed. Update ScreenPolicy.java and the inbox shortcut IDs in FocusService.java, rebuild and rerun the acceptance tests. Disable USB debugging afterward.

## Recovery

Press Exit Instagram on the cover, then open Android Settings > Accessibility > Installed apps > Focus Layer and turn it off. You may also uninstall it normally. Nothing prevents disabling or uninstalling. No strict mode or anti-uninstall mechanism is included.

## Privacy / architecture

No INTERNET permission, analytics, password access, notification listener, VPN or storage permission. Android accessibility grants broad potential access; the implementation retrieves only the active Instagram application window, inspects visible resource IDs, and never reads node text or saves conversation contents. It receives window-change events for other packages solely to dismiss the cover when you leave Instagram. One local integer tracks blocking sessions, not attempts or time saved. No timings are inferred.

MainActivity: disclosure, setup, service state and local counter.
FocusService: event-driven classifier, blocking cover, explicit inbox navigation, Back/Home recovery.
ScreenPolicy: narrow allowlist with Reel/Explore veto. Candidate IDs need real-device verification.

Run policy checks:
```
mkdir -p /tmp/focus-test
javac -d /tmp/focus-test app/src/main/java/com/praneet/focuslayer/ScreenPolicy.java tests/ScreenPolicyTest.java
java -cp /tmp/focus-test ScreenPolicyTest
```

Android references:
https://developer.android.com/guide/topics/ui/accessibility/views/service
https://developer.android.com/reference/android/accessibilityservice/AccessibilityService

Personal prototype. A Play Store release requires a separate policy review, disclosure/consent validation, signing and extensive device tests.
