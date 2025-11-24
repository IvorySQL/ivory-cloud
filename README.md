# Installation

## Prerequisites

- JDK 1.8 
- [Maven](http://maven.apache.org/) 3.0 and above
- IvorySQL 5.0
- Kubernetes 1.23 (Must have default storage class)

## Installation instructions

Fork the backend repository of the open-source cloud platform to your own GitHub account, then clone it to your local machine, for example:

```sh
YOUR_GITHUB_UN="<your GitHub username>"
git clone "git@github.com:${YOUR_GITHUB_UN}/ivory-cloud.git"
```

Enter the project root directory:

```
cd ivory-cloud
```


## Deployment

### Compile and build
1. Please ensure that all files ending with .sh in the ivory-cloud\cloudnative\src\main\resources\monitor folder and all its subfolders are in UNIX format. If they are not, please run the dos2unix command to convert them to UNIX format.

2. Maven：Maven configuration is required，After configuration, execute the following command：

```
mvn clean
mvn package -D maven.test.skip=true
```
After the packaging is completed, you can find the cloudnative-1.0-SNAPSHOT.jar file at the path ivory-cloud/cloudnative/target.
### Deploy backend projects 
1. Install a database such as ivorysql on your server and start it.
2. create a directory to store the application files. For example:
```
   mkdir -p /home/ivory
```
4. upload JAR file (cloudnative-1.0-SNAPSHOT.jar) to your server.
5. create a directory(name must be: config) under /home/ivory to store the application files. For example:
```
    mkdir -p /home/ivory/config
```
6. Please upload below files from the source code path ivory-cloud/cloudnative/src/main/resources to the /home/ivory/config path on your server.
```
   application.yaml   
   application-native.yaml   
   spring_pro_logback.xml
```
8. then edit /home/ivory/config/application-native.yaml
Please modify url, username, password parts according to your actual situation.
```
datasource:
    druid:
      db-type: com.alibaba.druid.pool.DruidDataSource
      driver-class-name: org.postgresql.Driver
      url: jdbc:postgresql://127.0.0.1:5432/ivorysql
      username: ivorysql
      password: "ivory@123"
```
7. Launch ivory-cloud app
   cd /home/ivory/

```
nohup java -jar cloudnative-1.0-SNAPSHOT.jar > log_native 2>&1 &
```
