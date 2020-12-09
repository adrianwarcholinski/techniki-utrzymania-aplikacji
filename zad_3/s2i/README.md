## 0. Prerequisities
s2i binary has to be installed. It can be downloaded from https://github.com/openshift/source-to-image/releases/tag/v1.3.1

Download WildFly from https://download.jboss.org/wildfly/20.0.0.Final/wildfly-20.0.0.Final.zip and put `modules` directory under this directory (so the absolute path of `modules` directory is `.../techniki-utrzymania-aplikacji/zad_3/s2i/modules`. 

Next required tool is JAR file with EclipseLink. Download JAR from https://mvnrepository.com/artifact/org.eclipse.persistence/eclipselink/2.7.4 and save it in `modules/system/layers/base/org/eclipse/persistence/main` directory with name "eclipselink.jar".

Create `modules/system/layers/base/com/mysql/main` directory and put `module.xml` file with content of `mysql-module.xml`. Additionally, download JDBC Driver (mysql-connector-java-8.0.21.jar) from here: https://mvnrepository.com/artifact/mysql/mysql-connector-java/8.0.21 and put it into `modules/system/layers/base/com/mysql/main`.

Last step is to replace `modules/system/layers/base/org/eclipse/persistence/main/module.xml` file with content of `org-eclipse-persistence-main-module.xml`.

## 1. Build custom builder image
```
docker build -t tua06-wildfly:1.0 . 
```

## 2. Use created builder image to build the application
```
s2i build --context-dir=zad_2/app https://github.com/adrianwarcholinski/techniki-utrzymania-aplikacji tua06-wildfly:1.0 strzelbex:1.0
```

## 3. Run the application
```
docker run -p 8443:8443 -p 9990:9990 strzelbex:1.0
```
