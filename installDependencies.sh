#//usr/bin/env sh

mkdir java-api && cd java-api
git clone https://github.com/pulse00/Composer-Java-Bindings.git .
git checkout develop
mvn clean install -DskipTests
