# Installation

## Installation instructions

As an open-source backend project, ivory-cloud requires the local installation of git, jdk, maven, etc., during the development process.

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
mvn install
```
After the packaging is completed, you can find the `pkg` directory in the root directory of the project. It contains the jar package to be deployed.
### Deploy backend projects 
