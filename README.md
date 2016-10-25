# Photon Server
Photon server implementation. **Supports Minecraft version 1.10**.  

**This project is licensed under the terms of the GNU Affero General Public License version 3**, which you can find in the LICENSE file.
All the files in this repository (except the LICENSE file which is copyrighted by the Free Software Foundation) are copyrighted by TheElectronWill and others who wrote them (the contributors).

## How to build
Photon uses [gradle](http://gradle.org) to manage its dependencies. Building the project is very easy:

1. Install the Java 8 SDK (aka JDK 8) if you don't already have it.
2. Put the [Photon API](https://github.com/mcphoton/Photon-API) in the same directory as the Photon Server project folder.
3. Put the [Photon Protocol library](https://github.com/mcphoton/Photon-ProtocolLib) in the same directory as the Photon Server project folder. 
4. Run the following command in the Photon Server project's directory: `./gradlew build`

## How to contribute
You can fork this project and send me pull requests :)
* To contribute to the Photon implementation, you must first understand the Photon API. Please read [the API wiki](https://github.com/mcphoton/Photon-API/wiki) to know about the project's guidelines and architecture.
* Please also read *the server architecture* (coming soon) to understand how the server is organized.
* Good java skills are required.

## Branches
The *develop* branch contains the latest **in development** version of the Photon server. It's probably unstable and may not work at all. More stable releases can be found in the *master* branch.

## Current status
The Photon server **isn't ready** yet. As of release "0.4.0" you can connect to it, but you can't do anything in the world (well, actually there's no real world: the map is completely empty).
