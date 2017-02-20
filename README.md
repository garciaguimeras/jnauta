# jNauta

An incomplete Java mail library, to be used with cuban Nauta accounts.

## Features

* Version 0.1.0
    * POP3 protocol for receiving messages
    * Retrieving folder list
    * Retrieving unread messages from a folder
    * Get all message information, including attachments
    * SMTP protocol for sendind messages
    * Adding attachments to messages

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

