# Racing Minimap

A minimap for Minecraft boat racing on ice tracks. While riding a boat it shows
a live, rotating map of the ice track around you and marks the other racers
(boat drivers) on it.

This mod is a 1.21.4 port of the minimap from
[BoatHud (F1 2023)](https://modrinth.com/mod/boathud-f1) by jewtvet (MIT),
with everything else (speedometer, leaderboard, race status, broadcast mode,
default-HUD overrides, mixins) stripped out.

## Features

- Realtime ice-track minimap while riding a boat, generated from the world.
- Rounded, semi-transparent map that rotates to match your view direction.
- Other boat drivers shown as red markers.
- Configurable position (X/Y) and track depth via Mod Menu (Cloth Config).
  The map scans the area around the player on the chosen Y-level to show the
  track even when it is hidden under other blocks; deeper blocks appear dimmer.

## Requirements

- Fabric Loader
- Fabric API
- Cloth Config (optional at runtime, needed for the config screen)
- Mod Menu (optional, provides the config screen entrypoint)

## Building

```sh
./gradlew build
```

The built jar is in `build/libs/`.