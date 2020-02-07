![](https://github.com/kaethorn/alfred/workflows/Tests/badge.svg?branch=master)

# Alfred

A web based comic book reader and library manager.

![Alfred](./art/alfred.svg)

## Features

* Scan & manage comic books.
* Read your comic books anywhere.
* Log in with your Google account.
* Continue reading while offline.
* Automatically sync progress when connectivity is restored.
* Make use of embedded metadata from [ComicRack](http://comicrack.cyolito.com/).
* Look up missing metadata from the [Comic Vine API](https://comicvine.gamespot.com/api/).
* Edit metadata on individual comic books.
* Switch color schemes (aka dark mode).

## Stack

* Spring Boot 2 back end.
* MongoDB.
* Google Sign-In.
* Ionic v4 PWA.


## Requirements

Building requires either **Java 11 SDK** or **Docker**.

Running the application requires a **Java 11 JRE** or **Docker** and a Client ID for **Google Sign-In**.


## Usage

### Docker

This is the recommended way to build and run Alfred.

#### TL;DR

```sh
docker network create alfred-net
docker start mongo || docker run -d -p 27017:27017 --name mongo mongo:3.6
docker network connect alfred-net mongo
docker build -t de.wasenweg/alfred .
docker run --dns 8.8.8.8 -p 5000:8080 --net=alfred-net --rm \
  -v <Path to your comic library>:/comics \
  -v <Path to where you want to receive log files>:/logs \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATA_MONGODB_URI=mongodb://mongo/alfred \
  -e COMICS_COMICVINE_APIKEY=<Your Comic Vine API key> \
  -e LOGGING_FILE=/logs/alfred.log \
  -e AUTH_CLIENT_ID=<Your Google Client ID, ends in .apps.googleusercontent.com> \
  -e AUTH_USERS=<List of allowed user IDs, e.g. email addresses> \
  -e AUTH_JWT_SECRET=<Your own generated or custom JWT secret> \
  --name alfred \
  de.wasenweg/alfred
```

Configuration options are explained [here](#configuration).

For a detailed explanation of, please continue:

#### 1. Network

Set up a common network:

```sh
docker network create alfred-net
```

#### 2a. New MongoDB

Set up a new MongoDB connected to the network:

```sh
docker run --name mongo -p 27017:27017 --net=alfred-net mongo
```

#### 2b. Existing MongoDB

If you want to use an existing MongoDB instead, run and connect it to the network:

```sh
docker start mongo
docker network connect alfred-net mongo
```

#### 3. Build

Build the docker image:

```sh
docker build -t de.wasenweg/alfred .
```

#### 4. Run

Run the image and connect to the MongoDB:

```sh
docker run -p 5000:8080 --net=alfred-net -v /path/to/comics:/comics de.wasenweg/alfred
```

Replace `/path/to/comics` with the path to your comic library.

The application will now be available at <http://localhost:5000>.

### Alternative (Gradle)

Running Alfred on the host system directly requires **Java 11 SDK** and a running MongoDB instance, e.g. on `localhost`.

```sh
./gradlew clean build -x check
java -jar build/libs/alfred.jar --spring.data.mongodb.uri=mongodb://localhost/alfred
```

The application will now be available at <http://localhost:8080>.


## Configuration

Apart from the JWT secret (`auth.jwt.secret`), all settings can initially be passed to the application via [Spring's configuration options](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html). They're then stored and maintained in the MongoDB `settings` collection and take precedence over configuration options. Once the application is running, settings can be changed on the `settings` page.

To pass an option via docker, use an environment variable, e.g.: `docker run -e AUTH_CLIENT_ID=000000.apps.googleusercontent.com ...`.

### Comics path (`comics.path`)

The default comics path is `/comics` which allows you to associate it via a Docker volume mount.

### Client ID (`auth.client.id`)

Create a set of client credentials in the [Google API console](https://console.developers.google.com/apis/credentials). Configure the `Authorized JavaScript origins` as well as `Authorized redirect URIs` to reflect the host name of your Alfred instance. The Client ID format should be `000000.apps.googleusercontent.com`.

### Allowed users (`auth.users`)

Users are identified through their email addresses and the initial set of allowed users must be passed to the application on the inital run. They're a comma separated list of email addresses associated with a Google ID. Only users with the email addresses specified in this property will be allowed to use the application and its API.

### JWT secret (`auth.jwt.secret`)

Once the application authenticates the user, it issues its own JWT authentication token which requires a secret key. A default key is supplied but should be overridden whenever the application is run (it's not stored in the DB).

### Comic Vine API key (`comics.comicVine.ApiKey`)

Comic books not containing a meta data XML or an XML without enough attributes won't be usable. Missing meta data can be fetched from the [Comic Vine API](https://comicvine.gamespot.com/api/) for which you need an API key.


## Metadata

Comic book files need to be in CBZ format which is essentially a zip archive containing page images. In order to recoginize a comic book, Alfred needs at least the following information:

* A **publisher**, e.g. `DC Comics`.
* A **series**, e.g. `Batman`.
* A **volume**, e.g. `1940`.
* An **issue number**, e.g. `380`.

It expects to find these in a file named `ComicInfo.xml` which is located on the top level of the zip archive. Its format is loosley compatible with that produced by ComicRack, see [docs](http://comicrack.cyolito.com/software/windows/windows-documentation/7-meta-data-in-comic-files).

Failing that, Alfred will attempt to retrieve metadata from the [Comic Vine API](https://comicvine.gamespot.com/api/) based on information found in the file path, which is why it must match the followin structure:

`/any/directory/<publisher>/<series> (<volume>)/<series> <issue number> (<volume>).cbz`


## Logging

By default, the application logs only to the console it was started in. In order to output logs to a file, use the `logging.file` application property, e.g. start the app with `LOGGING_FILE=alfred.log`.

In docker, this can be achieved by settings a volume and an environment variable. Starting the container with these parameters

```
-v $PWD/logs:/logs -e LOGGING_FILE=/logs/alfred.log
```

will log to `./logs/alfred.log` on the host.


## Notes

### Reverse proxy

When running the application through a reverse proxy, make sure to enable streaming. This will allow for server-sent events to push updates during a library scan. Otherwise, there would be no feedback until the scan is complete.

To enable streaming in lighttpd, use the `server.stream-response-body` option.


## Development

### End-to-end tests

#### Preparation

Start a test instance

```sh
docker pull mongo:3.6
docker run -d --name mongo mongo:3.6

./gradlew clean build -x check
java -jar build/libs/alfred.jar --spring.profiles.active=test --spring.data.mongodb.uri=mongodb://localhost/alfred
```

Switch to UI and dependencies:

`cd ui && npm i`

#### Run tests

Run via the ng-cli wrapper:

```sh
npm run e2e -- --base-url=http://localhost:8080/ --dev-server-target=
```

or directly via protractor, skipping webdriver update:

```sh
npm run protractor
```

There is also a headless version:

```sh
npm run protractorHeadless
```

#### Debug tests

In order to debug, add a `debugger;` next to the statement you want to debug and then run the protractor config manually through node. Example for debugging `library.e2e-spec.ts`:

```sh
node --inspect-brk node_modules/protractor/bin/protractor e2e/protractor.conf.js --specs=e2e/src/library.e2e-spec.ts
```
