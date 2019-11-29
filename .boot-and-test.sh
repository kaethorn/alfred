#!/bin/bash

# turn on bash's job control
set -m

# Start the Spring Boot app
gradle -q bootRun > /dev/null &

# Wait for app to start
while ! curl --output /dev/null --silent --head --fail http://localhost:8080; do sleep 1; done

# Start UI tests
gradle -q npm_run_e2eHeadless

