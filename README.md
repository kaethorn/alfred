[![Build Status](https://travis-ci.org/kaethorn/komix.svg?branch=master)](https://travis-ci.org/kaethorn/komix)
[![CodeFactor](https://www.codefactor.io/repository/github/kaethorn/komix/badge)](https://www.codefactor.io/repository/github/kaethorn/komix)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/ef19770451cb4dc692488da4382f9ffc)](https://app.codacy.com/app/scf/komix?utm_source=github.com&utm_medium=referral&utm_content=kaethorn/komix&utm_campaign=Badge_Grade_Dashboard)

# KomX

A [Plex](https://www.plex.tv/) like comic management system for your [ComicRack](http://comicrack.cyolito.com/) library.

![A Screenshot](docs/screenshots/komix_1.png?raw=true)

## Requirements

* Java 8
* A MongoDB
* (optional) Docker
* Zipped comic book files (.cbz) containing embedded `ComicInfo.xml` metadata files from ComicRack, see [docs](http://comicrack.cyolito.com/software/windows/windows-documentation/7-meta-data-in-comic-files).

## Run

### Docker using Gradle

`./gradlew build docker`
`docker run de.wasenweg/komix`

### Docker manually

This will basically replicate what the Gradle docker plugin manages.

#### 1. Network
Set up a common network:
`docker network create komix-net`

#### 2a. New MongoDB
Set up a new MongoDB connected to the network:
`docker run --name mongo -p 27017:27017 --net=komix-net mongo`

#### 2b. Existing MongoDB
If you want to use an existing MongoDB instead, run and connect it to the network:
`docker start mongo`
`docker network connect komix-net mongo`

#### 3. Build
Build the docker image:
`./gradlew clean build`
`mkdir target`
`unzip build/libs/komix.jar -d target/dependency`
`docker build -t de.wasenweg/komix .`

#### 4. Run
Run the image and connect to the MongoDB:
`docker run -p 5000:8080 --net=komix-net -v /path/to/comics:/comics komix`
Replace `/path/to/comics` with the path to your comic library.

The application will now be available at http://localhost:5000.

### Gradle

To run the application on the host system directly, make sure to have a MongoDB running, e.g. on `localhost`, then run:
`./gradlew clean build && java -jar build/libs/komix.jar --spring.data.mongodb.uri=mongodb://localhost/komix`

The application will now be available at http://localhost:8080.

## Stack

* Spring Boot 2 App using MongoDB.
* Angular 7 UI with Material design.

## Debug

### E2E tests

`./gradlew bootRunEmbedded`
`cd ui && npm run e2e -- --base-url=http://localhost:8080/ --dev-server-target=`
