[![Build Status](https://travis-ci.org/kaethorn/comix.svg?branch=master)](https://travis-ci.org/kaethorn/comix)

# KomiX

A [Plex](https://www.plex.tv/) like comic management system for your [ComicRack](http://comicrack.cyolito.com/) library.

## Requirements

* Java 8
* A MongoDB
* (optional) Docker
* Zipped comic book files (.cbz) containing embedded `ComicInfo.xml` metadata files from ComicRack, see [docs](http://comicrack.cyolito.com/software/windows/windows-documentation/7-meta-data-in-comic-files).

## Run

### Docker

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
`docker build -t komix .`

#### 4. Run
Run the image and connect to the MongoDB:
`docker run -p 5000:8080 --net=komix-net komix`

The application will now be available at http://localhost:5000.

### Standalone/Gradle

`./gradlew clean build && java -jar build/libs/komix.jar`

The application will now be available at http://localhost:8080.

## Stack

* Spring Boot 2 App using MongoDB.
* Angular 7 UI with Material design.
