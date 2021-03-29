# openapi2apigee-archetype

Scaffolds an api proxy from and openapi v3 spec, it also if instructed creates a mock server to be consumed by the proxy.

## installing the archetype
In order to make this archetype available to your system we need to first install it locally.

Clone this repo then from the cloned folder install the archetype
```bash
mvn install
```

In case you get an error about the archetype not being able to download the dependencies, then install as
```bash
mvn install -Dgrape.config=./grapeConfig.xml
```
----
Be aware that **archetypeId** is used as name of the API Proxy.

Maven plugins in generated project
* [apigee-deploy-maven-plugin](https://github.com/apigee/apigee-deploy-maven-plugin)
* [apigee-config-maven-plugin](https://github.com/apigee/apigee-config-maven-plugin)
* [yaml-properties-maven-plugin](https://github.com/ozimov/yaml-properties-maven-plugin)
* [replacer](https://github.com/iarellano/maven-replacer-plugin)
* [apigee-deploy-maven-plugin](https://github.com/apigee/apigee-deploy-maven-plugin)
* [apigee-config-maven-plugin](https://github.com/apigee/apigee-config-maven-plugin)
* maven-antrun-plugin
* exec-maven-plugin
* jmeter-maven-plugin
* maven-resources-plugin
* exec-maven-plugin

## archetype parameters
| parameter                    | required | description                                                                    | default                              |
| -----------                  | -------- | -----------                                                                    | -------                              |
| archetypeGroupId             | yes      | must be provided fixed to: com.github.iarellano                                |                                      |
| DarchetypeArtifactId         | yes      | must be provided fixed to: openapi2apigee-archetype                            |                                      |
| interactiveMode              | no       | recomended for automatic jobs: false                                           | true                                 |
| groupId                      | yes      | your company or business domain name, it will be your groupId in your pom.xml  |                                      |
| artifactId                   | yes      | name of your api proxy to generate, it will be your artifactId in your pom.xml |                                      |
| version                      | yes      | version of your pom.xml                                                        |                                      |
| spec                         | yes      | url or file location of the spec file                                          |                                      |
| org                          | yes      | Apigee EDGE org                                                                |                                      |
| envs                         | yes      | comma separated names, e.g. test,prod                                          |                                      |
| spec-auth-name               | no       | name of authorization to pass if spec is hosted e.g. _Aurhorization_           |                                      |
| spec-auth-value              | yes      | value of authorization if spec is hosted e.g. _Basic lkjlijalsdf..._           |                                      |
| spec-auth-type               | yes      | location of authentication [ header &#124; query ]                             |                                      |
| hosturl                      | no       | edge management api url                                                        | https://api.enterprise.apigee.com    |
| apiversion                   | no       | edge management api version                                                    | v1                                   |
| options                      | no       | apigee-deploy-maven-plugin apigee.options parameter                            | update                               |
| delay                        | no       | apigee-deploy-maven-plugin apigee.delay parameter                              | 1000                                 |
| authtype                     | no       | apigee-deploy-maven-plugin apigee.authtype parameter                           | basic                                |
| tokenurl                     | no       | used when authtype is oauth                                                    | https://login.apigee.com/oauth/token |
| clientid                     | no       | used when authtype is oauth                                                    | edgecli                              |
| clientsecret                 | no       | used when authtype is oauth                                                    | edgeclisecret                        |
| basepath                     | yes      | api proxy basepath                                                             |                                      |
| virtualhost                  | yes      | virtualhost to access the api proxy                                            |                                      |
| proxydomain                  | yes      | domain of the server to consume the api proxy                                  |                                      |
| proxyport                    | no       | remote port to access the api proxy                                            |                                      |
| proxyscheme                  | no       | to use either https or http to access the api proxy                            | https                                |
| targeturl                    | no       | target url to be consumed by proxy, ignore if mockserver=true                  |                                      |
| mockserver                   | no       | whether to create a mock server                                                | false                                |
| config-options               | no       | apigee-config-maven-plugin apigee.config.options parameter                     | update                               |
| config-exportdir             | no       | apigee-config-maven-plugin apigee.config.exportDir parameter                   | target                               |
| config-dir                   | no       | apigee-config-maven-plugin apigee.config.dir parameter                         | target/ege                           |
| enable-cors                  | no       | add a CORS policy                                                              | false                                |
| cors-policy-name             | no       | name of the CORS policy only if <code>enable-cors=true</code>                  | AM-AddCORS                           |
| apigee-deploy-plugin-version | no       | version of apigee-deploy-maven-plugin to use                                   | 1.1.7                                |
| apigee-config-plugin-version | no       | version of apigee-config-maven-plugin to use                                   | 1.3.7                                |

**Note 1**
If you feel limited by the parameters you can use a setup file in either json or yaml, using a setup file enables to supports multiple organizations,
multiple environments, etc..., when you instruct the archetype to use a setup file through <code>edgeSetupFile</code> parameter then although
they keep their default value the following parameters are ignored:

| parameter        |
| ---------        |
| org              |
| envs             |
| authtype         |
| virtualhost      |
| targeturl        |
| options          |
| delay            |
| tokenurl         |
| clientid         |
| clientsecret     |
| proxydomain      |
| proxyscheme      |
| proxyport        |
| hosturl          |
| apiversion       |
| config-options   |
| config-exportdir |
| config-dir       |
| config-dir       |
| mockserver       |
| targeturl        |

**Note 2**
If you don't like any of my generated files you can also use your own [velocity templates](https://velocity.apache.org/engine/1.7/user-guide.html)
and replace them through the <code>templateDir</code> parameter, you are not limited to only replace them but to also include yours and also include
non-template files, when using your templates you can also have nested directories.

**Important note** Only files with extension .vm are run through velocity engine.


## usage
Asuming you already have apigee edge account with two environmets test and prod and if you don't no problem, register for trial org at [apigee.com](https://apigee.com.) 

### Create an API Proxy without a setup file
To create an api proxy with mock server for the petstore spec, go to your project folder where you want to create your project, then in your console:
```bash
mvn archetype:generate \
  -DarchetypeGroupId=com.github.iarellano \
  -DarchetypeArtifactId=openapi2apigee-archetype \
  -DinteractiveMode=false \
  -DgroupId=com.example \
  -DartifactId=petstore-v1 \
  -Dversion=1.0 \
  -Dspec=https://raw.githubusercontent.com/OAI/OpenAPI-Specification/master/examples/v3.0/petstore.yaml \
  -Dorg=[APIGEE_ORG] \
  -Denvs=test,prod \
  -Dbasepath=/petstore/v1 \
  -Dhosturl=https://api.enterprise.apigee.com \
  -Dproxydomain=[EDGE ORG DOMAIN] \
  -Dvirtualhost=secure \
  -Dmockserver=true
```

### Create an API Proxy without a setup file
To create an api proxy with mock server for the petstore spec, go to your project folder where you want to create your project, then in your console:
```bash
mvn archetype:generate \
  -DarchetypeGroupId=com.github.iarellano \
  -DarchetypeArtifactId=openapi2apigee-archetype \
  -DinteractiveMode=false \
  -DgroupId=com.example \
  -DartifactId=petstore-v1 \
  -Dversion=1.0 \
  -Dspec=https://raw.githubusercontent.com/OAI/OpenAPI-Specification/master/examples/v3.0/petstore.yaml \
  -Dbasepath=/petstore/v1 \
  -DedgeSetupFile=./setup.yaml
```

For an example of using a setup file check [samples/setup-file](samples/setup-file)


That will gereate a forlder petstore-v1 containing all your artifacts. Follow the instructions in the generated README.md file.

## generate files/folders

| name     | description                                                                             |
| -----    | -----------                                                                             |
| apiproxy | apiproxy implementation                                                                 |
| config   | files configuration variables per environment                                           |
| edge     | configuration files for apigee entities                                                 |
| mock     | mock server when <code>mockserver=true</code>                                           |
| test     | basic integration tests with cucumberjs + [Apickli](https://github.com/apickli/apickli) |

**Important note** Instructions for deploying and testing the apiproxy will be in generated README.md file
