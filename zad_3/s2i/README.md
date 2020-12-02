## 0. Prerequisities
s2i binary has to be installed. It can be downloaded from https://github.com/openshift/source-to-image/releases/tag/v1.3.1

## 1. Build custom builder image
```
docker build -t tua06-wildfly:1.0
```

## 2. Use created builder image to build the application
```
s2i build --context-dir=zad_2/app https://github.com/adrianwarcholinski/techniki-utrzymania-aplikacji tua06-wildfly:1.0 strzelbex:1.0
```

## 3. Run the application
```
docker run -p 8443:8443 strzelbex:1.0
```
