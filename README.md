[![Build Status](https://travis-ci.org/kaethorn/alfred.svg?branch=master)](https://travis-ci.org/kaethorn/alfred)

# Features

A web based comic management system for your [ComicRack](http://comicrack.cyolito.com/) library.

* Browse and read comics on your network.
* Web application.
* Mobile application (Android or iOS).

# Stack

* Spring Boot 2.
* MongoDB.
* Ionic v4 application.

# Requirements

A Dockerfile allows building the application without the need to install Java. Building it
* Java 11 and a MongoDB
* or Docker
* Zipped comic book files (.cbz) containing embedded `ComicInfo.xml` metadata files from ComicRack, see [docs](http://comicrack.cyolito.com/software/windows/windows-documentation/7-meta-data-in-comic-files).
* A Client ID for Google Sign-In.
* (optional) A [Comic Vine API](https://comicvine.gamespot.com/api/) key.

# Run

### 1. Network

Set up a common network:

```sh
docker network create alfred-net
```

### 2a. New MongoDB

Set up a new MongoDB connected to the network:

```sh
docker run --name mongo -p 27017:27017 --net=alfred-net mongo
```

### 2b. Existing MongoDB

If you want to use an existing MongoDB instead, run and connect it to the network:

```sh
docker start mongo
docker network connect alfred-net mongo
```

### 3. Build

Build the docker image:

```sh
docker build -t de.wasenweg/alfred .
```

### 4. Run

Run the image and connect to the MongoDB:

```sh
docker run -p 5000:8080 --net=alfred-net -v /path/to/comics:/comics de.wasenweg/alfred
```

Replace `/path/to/comics` with the path to your comic library.

The application will now be available at <http://localhost:5000>.

You can also pass all required options, like so:

```sh
docker run --dns 8.8.8.8 -p 8080:8080 --net=alfred-net --rm \
  -v <Path to your comic library>:/comics \
  -v <Path to where you want to receive log files>:/logs \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATA_MONGODB_URI=mongodb://mongo/alfred \
  -e COMICS.COMIC_VINE_API_KEY=<Your Comic Vine API key> \
  -e LOGGING_FILE=/logs/alfred.log \
  -e AUTH.CLIENT.ID=<Your Google Client ID, ends in .apps.googleusercontent.com> \
  -e AUTH.USERS=<List of allowed user IDs, e.g. email addresses> \
  -e AUTH.JWT.SECRET=<Your own generated or custom JWT secret> \
  --name alfred \
  de.wasenweg/alfred
```

## Configuration

Apart from the JWT secret (`auth.jwt.secret`), all settings can initially be passed to the application via [Spring's configuration options](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html). They're then stored and maintained in the MongoDB `settings` collection and take precedence over configuration options. Once the application is running, settings can be changed on the `settings` page.

### Comics path (`comics.path`)

The default comics path is `/comics` which allows you to associate it via a Docker volume mount.

### Client ID (`auth.client.id`)

Create a set of client credentials in the [Google API console](https://console.developers.google.com/apis/credentials). Configure the `Authorized JavaScript origins` as well as `Authorized redirect URIs` to reflect the host name of your Alfred instance. The Client ID format should be `000000.apps.googleusercontent.com`.

Then pass that client ID as `auth.client.id` to the application. There are [various ways](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html) to achieve that. Running the application via docker, you could pass an environment variable: `docker run -e AUTH.CLIENT.ID=000000.apps.googleusercontent.com ...`.

### Allowed users (`auth.users`)

Users are identified through their email addresses and the initial set of allowed users must be passed to the application on the inital run. They're a comma separated list of email addresses associated with a Google ID. Only users with the email addresses specified in this property will be allowed to use the application and its API.

### JWT secret (`auth.jwt.secret`)

Once the application authenticates the user, it issues its own JWT authentication token which requires a secret key. A default key is supplied but should be overridden whenever the application is run (it's not stored in the DB).

### Comic Vine API key (`comics.comicVineApiKey`)

Comic books not containing a meta data XML or an XML without enough attributes won't be usable. Missing meta data can be fetched from the [Comic Vine API](https://comicvine.gamespot.com/api/) for which you need an API key.

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


## Develop

### Gradle

To run the application on the host system directly, make sure to have a MongoDB running, e.g. on `localhost`, then run:

```sh
./gradlew clean build && java -jar build/libs/alfred.jar --spring.data.mongodb.uri=mongodb://localhost/alfred
```

The application will now be available at <http://localhost:8080>.

### End-to-end tests

### Preparation

Start a test instance

```sh
docker network create alfred-net
docker pull mongo:3.6
docker run -d --name mongo mongo:3.6
docker network connect alfred-net mongo

./gradlew build docker -x test

docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=test -e SPRING_DATA_MONGODB_URI=mongodb://mongo/alfred --net=alfred-net --rm -v $PWD/src/test/resources/fixtures/full:/comics --name alfred de.wasenweg/alfred
```

Install dependencies

`cd ui && npm i`

### Run tests

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

### Debug tests

In order to debug, add a `debugger;` to the test you want to debug and then run the protractor config manually with node. Example for debugging `library.e2e-spec.ts`:

```sh
node --inspect-brk node_modules/protractor/bin/protractor e2e/protractor.conf.js --specs=e2e/src/library.e2e-spec.ts
```

# Enjoy

## Library view

![Library](docs/screenshots/alfred1.png?raw=true){: style="max-width:500px"}

## Settings and menu

![Settings and menu](docs/screenshots/alfred2.png?raw=true){: style="max-width:500px"}

## Volume view

![Volume](docs/screenshots/alfred3.png?raw=true){: style="max-width:500px"}
