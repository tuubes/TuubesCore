[![](https://img.shields.io/badge/next%20version-0.5.0-yellow.svg)](https://github.com/mcphoton/Photon-Server/projects/1)
[![](https://img.shields.io/badge/progress-60%25-yellow.svg)](https://github.com/mcphoton/Photon-Server/projects/1)
[![](https://img.shields.io/badge/discord-join%20chat!-7289DA.svg)](https://discord.gg/vWYembz)

From-scratch, extensible and multithreaded minecraft server.

## Project setup
Photon uses [gradle](http://gradle.org) to manage its dependencies.

### Linux and MacOS
1. Install the Java 8 SDK and git.
2. Open a terminal where you want the project to be, and execute
```bash
bash <(curl -s https://raw.githubusercontent.com/mcphoton/Photon-Server/scala-rewrite/setup.sh)
```
This will clone all the repositories and create two scripts: `fetch-all.sh`and `pull-all.sh` to fetch and pull all the repos at once.
3. To build the project, cd in the Photon-Server directory and run `./gradlew build`

### Windows
1. Install the Java 8 SDK.
2. Create a directory for the project and clone this repo + [the ProcolLib repo](https://github.com/mcphoton/Photon-ProtocolLib) + [the Utils repo](https://github.com/mcphoton/Utils)
3. To build the project, cd in the Photon-Server directory and run `gradlew build`

## How to contribute
You can fork this project and send me pull requests :)
* To contribute to the Photon implementation, you must first understand the Photon API. ~~Please read [the API wiki](https://github.com/mcphoton/Photon-API/wiki) to know about the project's guidelines and architecture.~~ The wiki isn't exactly up-to-date, and since major changes are coming it won't be updated in a near future.
* Please respect [the coding style](https://github.com/mcphoton/Photon-Server/blob/develop/Coding%20Style.md).
* Good java skills are required.

## Branches
The *develop* branch contains the latest **in development** version of the Photon server. It's probably unstable and may not work at all. More stable releases can be found in the *master* branch.

## Current status
The Photon server **isn't ready** yet. As of release "0.4.0" you can connect to it, but you can't do anything in the world (well, actually there's no real world: the map is completely empty).

The progress of the project can be found [here](https://github.com/mcphoton/Photon-Server/projects/1).

## License
LGPLv3