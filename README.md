# A Windows 10 Calendar-Styled App

![](screenshots/Screenshot.png)

## Build Artifacts

GitHub Actions builds and uploads these artifacts for each run:

- Android release APK: `app-release`
- macOS arm64 DMG for Apple Silicon Macs: `calendar-macos-arm64`

The macOS artifact is built on GitHub Actions' `macos-14` arm64 runner with:

```bash
./gradlew :desktop:packageDmg
```

Local desktop packaging uses the current machine architecture. On an Apple Silicon Mac, the same command produces a macOS arm64 DMG under `desktop/build/compose/binaries/main/dmg/`.

## MVI Architecture Flow

```mermaid
flowchart TD
    User[User input<br/>clicks and scrolls] --> View[Compose UI<br/>CalendarMain]
    View -->|collect StateFlow| Render[Render CalendarUiState]
    View -->|dispatch CalendarIntent| Store[CalendarStore]

    Contract[CalendarContract<br/>UiState / Intent / Effect] -. defines .-> View
    Contract -. defines .-> Store

    Store --> Intent{CalendarIntent}
    Intent -->|TodaySelected| ScrollEffect[emit ScrollToItem effect]
    Intent -->|MonthNavigationSelected| TargetIndex[CalendarDateCalculator.targetIndexFor]
    TargetIndex --> ScrollEffect
    Intent -->|ViewportChanged| ViewportReduce[reduce viewport fields]
    ViewportReduce --> VisibleRange[CalendarDateCalculator.highlightedRange<br/>dateForIndex]
    VisibleRange --> MonthReduce[reduce highlightedRange<br/>currentMonth]

    Clock[Clock tick every second] --> ClockReduce[reduce clock state]

    ClockReduce --> State[(MutableStateFlow<br/>CalendarUiState)]
    ViewportReduce --> State
    MonthReduce --> State
    State -->|state| View

    ScrollEffect --> Effects[(SharedFlow<br/>CalendarEffect)]
    Effects -->|CalendarEffects collects| Animate[LazyGridState.animateScrollToItem]
    Animate -->|after animation| ForcedViewport[dispatch ViewportChanged<br/>force = true]
    ForcedViewport --> Store
```
