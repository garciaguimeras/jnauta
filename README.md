# jNauta

An incomplete Java mail library, 
customized to be used with cuban Nauta accounts.

## Features

* Version 0.2.0
    * IMAP protocol support
    * Read message attachments
    * Get read and new messages count
    * Create new folders
    * Copy messages into folders
    * Mark messages as read
    * Delete messages
    * Retrieve all messages from folder, with pagination
    * Retrieve recent messages from folder
    * Get message count from specific folder

* Version 0.1.0
    * POP3 protocol support
    * SMTP protocol support    
    * Retrieve folder list
    * Retrieve unread messages from a folder
    * Get message information (sender, subject and content in text mode)
    * Add attachments to messages

## Building the library

Clone the repository and build the artifacts using *gradle*

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

## Nauta configurations

### Nauta over POP3

* Server: **pop.nauta.cu**
* Port: **110**
* Available folders:
    * INBOX

### Nauta over IMAP

* Server: **imap.nauta.cu**
* Port: **143**
* Available folders:
    * INBOX
    * Drafts
    * Junk
    * Trash
    * Sent
    * Archives
    * Templates