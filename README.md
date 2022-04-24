# CS505
CS505 Spring 2022 Final Project

## Running Maven
1. Install maven
2. Under .m2 folder, make a settings.xml file. Paste the following contents:
```
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
   <localRepository />
   <interactiveMode />
   <offline />
   <pluginGroups />
   <servers />
   <mirrors>
      <mirror>
         <id>centralhttps</id>
         <mirrorOf>central</mirrorOf>
         <name>Maven central https</name>
         <url>http://insecure.repo1.maven.org/maven2/</url>
      </mirror>
      <mirror>
         <id>wso2-nexus-public</id>
         <name>mirror</name>
         <url>http://maven.wso2.org/nexus/content/groups/wso2-public/</url>
         <mirrorOf>wso2-nexus</mirrorOf>
      </mirror>
      <mirror>
         <id>wso2.releases-mirror</id>
         <name>ForgeRock Internal Snapshots Repository mirror</name>
         <url>http://maven.wso2.org/nexus/content/repositories/releases/</url>
         <mirrorOf>wso2.releases</mirrorOf>
      </mirror>
      <mirror>
         <id>nexus-repo-mirror</id>
         <url>http://maven.wso2.org/nexus/content/repositories/snapshots/</url>
         <mirrorOf>nexus-repo</mirrorOf>
      </mirror>
   </mirrors>
   <proxies />
   <profiles />
   <activeProfiles />
</settings>
```
3. Run mvn clean package

## Run in localhost
1. Run this command:
```
bash run.sh
```

## Deploy using Docker file
1. Run the following command to build the docker image:
```
  sudo docker build -t cs505-final .
```
2. 
Run in background:
```
  sudo docker run -d --rm -p 9000:9000 cs505-final
```
Run in foreground:
```
    sudo docker run -it --rm -p 9000:9000 cs505-final
```

## Running APIs
1. ```
   curl --header "X-Auth-API-key:1234" "http://<hostname>:<port#>/api/getteam"
   ```
2. ```
   curl --header "X-Auth-API-key:1234" "http://<hostname>:<port#>/api/getpatientstatus"
   ```
3. ```
   curl --header "X-Auth-API-key:1234" "http://<hostname>:<port#>/api/getalertlist"
   ```

*Check the collection **CS505_Final.postman_collection.json** *