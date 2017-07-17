Jenkins Rancher Plugin
======================

Plugin for integrating Jenkins with Rancher using pipeline scripts.

You can learn more on the [Rancher/](http://rancher.com/)

## Requirements

### Jenkins

Jenkins version 2.11 or newer is required.

### Rancher

Rancher API v2-beta 

## Setup

### Add the Rancher API Key to Jenkins:

1. Navigate to your Jenkins instance
2. Select "Manage Jenkins" from the Jenkins sidebar
3. Click "Configure System" link
4. Search "Rancher API Keys"
5. Click "Add"
6. Enter "Rancher Configuration". See image.
   1. Name: A name for your configutation. Ex: BASIS
   2. Rancher URL: API endpoint 
   3. Access Key: Rancher access key
   4. Private Key: Rancher secret key
7. Click "Save" for apply configuration 

![](https://github.com/BasisTI/rancher-jenkins-plugin/blob/master/config.png)

## Job configuration

### Pipeline job configuration

1. Create a new pipeline project
2. Example pipeline definition:

```groovy
stage('Calling Rancher API'){
    rancher
        .config('BASIS') //Name you defined into Rancher Configuration
        .service() // Rancher Service API
        .upgrade("RANCHER ENVIRONMENT NAME", "RANCHER STACK NAME","RANCHER SERVICE NAME");
        // .upgrade("PRODUCTION", "SYSTEM","SERVER");
}
```

