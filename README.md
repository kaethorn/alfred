[![Build Status](https://travis-ci.org/kaethorn/alfred.svg?branch=master)](https://travis-ci.org/kaethorn/alfred)
[![CodeFactor](https://www.codefactor.io/repository/github/kaethorn/alfred/badge)](https://www.codefactor.io/repository/github/kaethorn/alfred)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/ef19770451cb4dc692488da4382f9ffc)](https://app.codacy.com/app/scf/alfred?utm_source=github.com&utm_medium=referral&utm_content=kaethorn/alfred&utm_campaign=Badge_Grade_Dashboard)

# Alfred

A [Plex](https://www.plex.tv/) like comic management system for your [ComicRack](http://comicrack.cyolito.com/) library.
![Alfred](./ui/src/icons/alfred.svg)

## Requirements

* Java 8
* A MongoDB
* (optional) Docker
* Zipped comic book files (.cbz) containing embedded `ComicInfo.xml` metadata files from ComicRack, see [docs](http://comicrack.cyolito.com/software/windows/windows-documentation/7-meta-data-in-comic-files).

## Run

### Docker using Gradle

`./gradlew build docker`
`docker run de.wasenweg/alfred`

### Docker manually

This will basically replicate what the Gradle docker plugin manages.

#### 1. Network
Set up a common network:
`docker network create alfred-net`

#### 2a. New MongoDB
Set up a new MongoDB connected to the network:
`docker run --name mongo -p 27017:27017 --net=alfred-net mongo`

#### 2b. Existing MongoDB
If you want to use an existing MongoDB instead, run and connect it to the network:
`docker start mongo`
`docker network connect alfred-net mongo`

#### 3. Build
Build the docker image:
`./gradlew clean build`
`mkdir target`
`unzip build/libs/alfred.jar -d target/dependency`
`docker build -t de.wasenweg/alfred .`

#### 4. Run
Run the image and connect to the MongoDB:
`docker run -p 5000:8080 --net=alfred-net -v /path/to/comics:/comics alfred`
Replace `/path/to/comics` with the path to your comic library.

The application will now be available at http://localhost:5000.

### Gradle

To run the application on the host system directly, make sure to have a MongoDB running, e.g. on `localhost`, then run:
`./gradlew clean build && java -jar build/libs/alfred.jar --spring.data.mongodb.uri=mongodb://localhost/alfred`

The application will now be available at http://localhost:8080.

## Stack

* Spring Boot 2 App using MongoDB.
* Angular 7 UI with Material design.

## Debug

### E2E tests

`./gradlew bootRunEmbedded`
`cd ui && npm run e2e -- --base-url=http://localhost:8080/ --dev-server-target=`
