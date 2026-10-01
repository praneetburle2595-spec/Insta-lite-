# Focus Layer security and privacy review — 1 October 2026

Scope: author review of v0.1 source, compiled v0.1 APK manifest, dependencies and CI; fixes in v0.2. This is not an independent audit or penetration test. No phone or emulator integration testing has been performed.

## Verified from original APK and source

The v0.1 APK's compiled manifest contains no uses-permission entries, including INTERNET, contacts, storage, location or notifications. Backup is disabled. The exported accessibility service requires Android's BIND_ACCESSIBILITY_SERVICE permission. The launcher is exported without data-taking handlers. The application was debuggable in v0.1. Source has no message-text getters, analytics SDKs, network clients, WebViews, native libraries, shell execution, password collection or notification listener. Interface IDs are held in memory; one block-session counter is stored. The app checks package metadata from the active application window, including other applications, before traversing Instagram's visible IDs. Retrieving a node can give the service potential access to text even though this code does not call its text getter. The platform permission remains broad and is not sandboxed to Instagram by this code.

## Changes implemented in v0.2

- Non-debuggable personal-test release build; still debug-certificate signed, not a production release.
- Explicit local disclosure acceptance required before service activation.
- A Turn off protection button calls disableSelf and removes the cover.
- Coalesced event scheduling prevents constant content-change events from indefinitely postponing inspection.
- A cover-only 300 ms recheck helps remove stale covers on app switching. It does not poll in the background when no cover exists.
- Bounded tree traversal for inbox navigation as well as screen detection; runtime exceptions disable protection and remove its cover.
- Setup scroll container and system-inset fitting reduce the chance of inaccessible recovery controls. Requires device verification.
- GitHub Actions pinned to the exact action revisions used in the successful v0.1 build, read-only workflow token, 15-minute timeout; APK signature, no requested uses-permissions, non-debuggable flag and checksum checks added.

## Remaining material risks

Accessibility access can inspect screens and perform clicks/global navigation. A source change or compromised build can misuse it; no INTERNET permission alone is not a proof against every possible exfiltration path. Candidate Instagram identifiers are unverified. Incorrect classification can block conversations/calls or allow an unexpected screen. Inline videos in conversations are not inspected. Null trees, interruptions, disabled service, browser Instagram and cloned applications can bypass blocking. Screens may flash before the overlay appears. The service intentionally disables on unexpected exceptions to prioritize recovery over enforcement. There is no anti-uninstall or strict mode.

The service receives events across packages because it must dismiss its cover when Instagram is left. It does not read event text. Package filtering would reduce events but is not a complete platform access restriction and can prevent timely dismissal; it was not added as a purported security boundary.

Build plugins/artifacts still come from Google/Maven. Action SHA pinning limits mutable tag drift, not compromise within a pinned action. The hosted runner uses an ephemeral signing key: updates may require reinstalling. Do not store or publish a production private signing key. GitHub hosts the public source with the user's approval; it does not receive Instagram messages from this app.

## Testing gate

Local: seven screen-policy checks passed; source and XML inspection completed. CI: Android compilation plus APK signature, permission and non-debuggable checks must pass before supplying v0.2. These are static/build checks only. Device acceptance remains required: enable/disable, emergency button, app switching, continuous scrolling, keyboard/dialogs, reboot, DM notification routing, text/media messaging, calls, and Reel links/inline previews. Until that passes, do not treat this as a reliable messaging-only Instagram tool.

Recommendation: v0.2 is suitable only for voluntary controlled testing by the owner after reading the accessibility disclosure. Continue to withhold any safety certification or production-readiness claim. If you are uncomfortable with broad Accessibility permission, leave it disabled.
