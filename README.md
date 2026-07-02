# Nova Player

Dark-Glass Media-Player für Android (Musik + Video) auf Basis von Jetpack Compose + Media3 (ExoPlayer).

## Features
- Musik & Video aus dem MediaStore scannen und abspielen
- Background-Playback mit Notification-Steuerung (MediaSessionService)
- Glassmorphism-UI, Dark Theme mit Indigo/Violet/Green-Akzenten
- Mini-Player-Leiste + Vollbild-Player mit Slider, Skip, Play/Pause

## Auf GitHub hochladen & APK bauen lassen

```bash
git init
git add .
git commit -m "Nova Player initial"
git branch -M main
git remote add origin https://github.com/DEIN_USERNAME/nova-player.git
git push -u origin main
```

Der Workflow unter `.github/workflows/build.yml` läuft automatisch bei jedem Push auf `main`
(und lässt sich auch manuell über "Run workflow" im Actions-Tab starten).

Nach dem Lauf: **Actions-Tab → Workflow-Run öffnen → unter "Artifacts" die `NovaPlayer-debug.zip`
herunterladen** – darin liegt die fertige `app-debug.apk` zum Installieren.

## Lokal bauen (falls gewünscht)
Voraussetzung: Android SDK + JDK 17.

```bash
./gradlew assembleDebug
```
(Falls `gradlew` fehlt: `gradle wrapper` einmal lokal mit installiertem Gradle ausführen,
oder einfach den GitHub-Workflow nutzen – der braucht keinen Wrapper.)

## Struktur
- `MainActivity.kt` – Navigation (Library ↔ Player) + Permission-Handling
- `PlaybackService.kt` – MediaSessionService, hält ExoPlayer + Notification am Leben
- `PlayerViewModel.kt` – verbindet UI via MediaController mit dem Service
- `MediaScanner.kt` – liest Audio/Video aus dem MediaStore
- `ui/` – Compose Screens (Library, Player, MiniPlayer) + Glass-Komponenten & Theme

## Nächste Schritte / Ideen
- Album-Art via Coil laden statt Platzhalter-Gradient
- Playlists / Warteschlange editierbar machen
- Equalizer, Sleep-Timer
- Release-Signing für signierte APK statt Debug-Build
