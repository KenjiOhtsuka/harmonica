# Kotlin Database Migration Tool

[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0) [![](https://jitpack.io/v/KenjiOhtsuka/harmonica.svg)](https://jitpack.io/#KenjiOhtsuka/harmonica)
[![CircleCI](https://circleci.com/gh/KenjiOhtsuka/harmonica/tree/master.svg?style=svg)](https://circleci.com/gh/KenjiOhtsuka/harmonica/tree/master)
[![Twitter Follow](https://img.shields.io/twitter/follow/escamilloIII.svg?style=social)](https://twitter.com/escamilloIII)


Gradle Plugin Page: https://plugins.gradle.org/plugin/com.improve_future.harmonica

This is Database Migration Tool, gradle plugin, made with Kotlin.
It is made similar to Phinx, Rails.

This is used for PostgreSQL, not fully adapted to MySQL.

## Usage, Command

Please look at [Wiki](https://github.com/KenjiOhtsuka/harmonica/wiki).

## Caution

Sometimes, it says "The connection has already been closed" and the migrations fail.
Then, execute `gradlew --stop` to clear dead connections.

## Contribute

Pull requests are welcomed!! Please feel free to use it, and to contribute.
