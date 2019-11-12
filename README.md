# ![project logo](TuubesCore_logo.png)

[![](https://img.shields.io/badge/next%20version-0.6-green.svg)](https://github.com/mcphoton/Photon-Server/milestone/2)
[![](https://img.shields.io/badge/discord-join%20chat!-7289DA.svg)](https://discord.gg/vWYembz)

The Tuubes project aims to create a free (libre) and scalable server for voxel-based games.

## TuubesCore
TuubesCore is the server engine. It does not implement any concrete game feature: no blocks, no entities, etc.
It does implement common utilities like entity management, world saving, plugin loading, etc. and provides an API for implementing a concrete multiplayer voxel game as simply and efficiently as possible.

TuubesCore is split in several modules :
- `engine`: type registration, entity management and common APIs
- `maths`: maths library (useful for 3D computations)
- `network`: optimized packet handling
- `plugin`: plugin loader/unloader
- `worldgen`: bases for world generation

The `runnable` module combines all the aforementioned modules into an executable jar.

This repository contains the essential bricks of the [Tuubes project](http://tuubes.org), which aims to create an open-source scalable server for voxel games.

TuubesCore is independent from any game. Game-dependent content like block types and creatures are/will be implemented in other repositories.

## Project setup
TuubesCore is built with [Mill](https://www.lihaoyi.com/mill/). You don't need to install mill on your computer, I've included some scripts for you.

### Linux/MacOS
Compiling:
```
./mill runnable.compile
```

Creating an IntelliJ IDEA project:
```
./setup-intellij.sh
```

### Windows
Compiling:
```
millw.bat runnable.compile
```

Creating an IntelliJ IDEA project:
```
millw.bat mill.scalalib.GenIdea/idea
```

## License
TuubesCore is licensed under GPLv3, *not LGPL*. It means that if you modify TuubesCore **or create a Tuubes plugin** that you share with some other people (e.g. on internet), you **must distribute your sources** under GPLv3.
Unlike Bukkit, this is 100% intended, and I won't be nice if you break the rules.
