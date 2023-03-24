# Deployment

## Docker

    docker build -t mandelbrot-picture .
    docker run -it --publish 8080:8080 mandelbrot-picture

Open `http://localhost:8080/MandelbrotPicture-1.0-SNAPSHOT/`
