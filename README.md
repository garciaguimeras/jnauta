# jNauta

An incomplete Java mail library, to be used with cuban Nauta accounts.

## Features

* Version 0.2.0
    * Added IMAP protocol support
    * Reading message attachments
    * Getting read and new messages count
    * Creating new folders
    * Copying messages into folders
    * Marking messages as read
    * Deleting messages

* Version 0.1.0
    * POP3 protocol for receiving messages
    * Retrieving folder list
    * Retrieving unread messages from a folder
    * Getting message information (sender, subject and content in text mode)
    * SMTP protocol for sendind messages
    * Adding attachments to messages

## Building the library

Clone the repository and build the artifacts using *gradle*

```
git clone https://github.com/garciaguimeras/jnauta.git
cd jnauta
gradle jar 
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