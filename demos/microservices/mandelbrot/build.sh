#!/bin/bash
cd MandelbrotPicture
mvn clean package
docker build -t mandelbrot-picture .
cd ..

cd MandelbrotVideo
mvn clean package
docker build -t mandelbrot-video .
cd ..

echo "**** Open page http://localhost:9180/MandelbrotVideo-1.0-SNAPSHOT/"

docker-compose up --remove-orphans

docker-compose down