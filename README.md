# ComiX

A [Plex](https://www.plex.tv/) like comic management system for your [ComicRack](http://comicrack.cyolito.com/) library.

## Requirements

* Java 8
* Zipped comic book files (.cbz) containing embedded `ComicInfo.xml` metadata files from ComicRack, see [docs](http://comicrack.cyolito.com/software/windows/windows-documentation/7-meta-data-in-comic-files).

## 

## Stack

* Spring Boot 2 App using a H2 database
* Angular 7 UI with Material design

## TODO

* [x] Scan Comics and read metadata.
* [x] Update scan progress via WebSockets.
* [ ] Comic book reader.
* [ ] Filter comics.
* [ ] Browser comics.
* [ ] Dockerize.
