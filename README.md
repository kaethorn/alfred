# KomiX

A [Plex](https://www.plex.tv/) like comic management system for your [ComicRack](http://comicrack.cyolito.com/) library.

## Requirements

* Java 8 or Docker
* Zipped comic book files (.cbz) containing embedded `ComicInfo.xml` metadata files from ComicRack, see [docs](http://comicrack.cyolito.com/software/windows/windows-documentation/7-meta-data-in-comic-files).

## Run

### Docker

Build and run the docker image:
`./gradlew clean build`
`docker build -t komix .`
`docker run -p 5000:8080 komix`

The application will now be available at http://localhost:5000.

### Standalone/Gradle

`./gradlew clean build && java -jar build/libs/komix.jar`

The application will now be available at http://localhost:8080.

## Stack

* Spring Boot 2 App using a H2 database
* Angular 7 UI with Material design

## TODO

* [x] Scan Comics and read metadata.
* [x] Update scan progress via WebSockets.
* [x] Comic book reader.
* [ ] Filter comics.
* [ ] Browser comics.
* [ ] Dockerize.
