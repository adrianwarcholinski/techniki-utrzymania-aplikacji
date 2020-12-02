## 0. Prerequisities
s2i binary has to be installed. It can be downloaded from https://github.com/openshift/source-to-image/releases/tag/v1.3.1

Next required tools are JAR files with EclipseLink. Put them into `modules/system/layers/base/org/eclipse/persistence/main` directory.
Required JAR files:
* https://mvnrepository.com/artifact/org.eclipse.persistence/eclipselink/2.7.4 (but name of the file should be "eclipselink.jar"
* https://mvnrepository.com/artifact/org.wildfly/jipijapa-eclipselink/20.0.0.Final 

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
