# jNauta

An incomplete Java library to be used with cuban Nauta accounts.
Contains the following subprojects:

* **jNauta Mail** for sending and receiving emails
* **jNauta Net** for login and logout on Nauta network

## Building the library

Clone the repository and build the artifacts using **gradle**

```
git clone https://github.com/garciaguimeras/jnauta.git
cd jnauta
gradle clean jar 
```

## Including jNauta dependency

Include the [JitPack](https://jitpack.io) repository

``` 
allprojects {
    repositories {
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency

```
dependencies {
    compile 'com.github.garciaguimeras:jnauta.{version}'
}
```
