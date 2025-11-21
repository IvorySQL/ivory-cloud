# Installation

## Installation instructions

As an open-source backend project, ivory-cloud requires the local installation of `git`, `jdk`, `maven`, etc., during the development process.

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

Maven：Maven configuration is required，After configuration, execute the following command：

```
mvn clean
mvn package -D maven.test.skip=true
```
After the packaging is completed, you can find the `pkg` directory in the root directory of the project. It contains the jar package to be deployed.
### Deploy backend projects 
Install a database such as ivorysql on your server and start it.
Place the above jar package in your server, then edit [configuration file](./cloudnative/src/main/resources/application-native.yaml) 
Among them, 5432 is the port on which the ivorysql database runs; please modify it according to the actual situation.
```
datasource:
    druid:
      db-type: com.alibaba.druid.pool.DruidDataSource
      driver-class-name: org.postgresql.Driver
      url: jdbc:postgresql://127.0.0.1:5432/ivory
      username: ivorysql
      password: "ivory@123"
```
In the directory where the jar package is located, execute:

```
nohup java -jar cloudnative-1.0-SNAPSHOT.jar > log_native 2>&1 &
```
cloudnative-1.0-SNAPSHOT.jar is the name of the jar package; please replace it according to the actual situation.
