![project logo](photon-logo.png)

# Photon Server

[![](https://img.shields.io/badge/next%20version-0.6-green.svg)](https://github.com/mcphoton/Photon-Server/milestone/2)
[![](https://img.shields.io/badge/discord-join%20chat!-7289DA.svg)](https://discord.gg/vWYembz)

Photon is a from-scratch implementation of a Minecraft server whose main goals are:
- To scale extremely well by taking advantage of multiple CPU cores
- To provide a very powerful and easy-to-use plugin API for developers
- To free the community from the proprietary, opaque and buggy Mojang's server

Photon is a free software licensed under the LGPLv3.

## Project setup
Photon uses [gradle](http://gradle.org) to manage its dependencies.

### Linux and MacOS
1. Install the Java 8 SDK and git.
2. Open a terminal where you want the project to be, and execute
```bash
curl -s "https://raw.githubusercontent.com/mcphoton/Photon-Server/scala-rewrite/setup.sh" | bash
```
This will clone all the required repositories and create two scripts: `fetch-all.sh` and `pull-all.sh` to fetch and pull all the repos at once.

3. To build the project, cd in the Photon-Server directory and run `./gradlew build`

### Windows
1. Install the Java 8 SDK and a git client.
2. Create a directory for the project and clone this repo + [the ProcolLib repo](https://github.com/mcphoton/Photon-ProtocolLib) + [the Utils repo](https://github.com/mcphoton/Utils)
3. To build the project, cd in the Photon-Server directory and run `gradlew build`

## How to contribute
Contributors are always welcome :-) 
To contribute to the code, fork the repositories and send me a [pull request](https://help.github.com/articles/about-pull-requests/).

For more informations please read [the contributing guidelines](CONTRIBUTING.md).

## Branches
- **master**: stable releases only
- **develop**: base branch for unstable development
- **scala-rewrite**: contains the ongoing work to rewrite a part of Photon in [the Scala programming language](http://docs.scala-lang.org). See issue #9

## Offering ideas/reporting issues
New ideas and issues can be reported as [github issues](https://github.com/mcphoton/Photon-Server/issues).

To discuss about the project, [join the discord server](https://discord.gg/vWYembz)!

## State of the project
After a major revamp in Scala, the project works more and more!
For now, you spawn in a simple generated world with a few blocks (water, stone, grass, bedrock) 
and a limited size. You can move and try to break the blocks (by hand), but your changes will be 
reset on your next connection. The server responds correctly to the "server list ping".
Below is a screenshot of what it looks like:
![ingame screenshot](ingame-screenshot.png)