# jNauta

An incomplete Java library to be used with cuban Nauta accounts.
Contains the following subprojects:

* **jNauta Mail** for sending and receiving emails
* **jNauta Net** for login and logout on Nauta network

## jNauta Mail

### Features

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

### Configurations

#### Nauta over POP3

* Server: **pop.nauta.cu**
* Port: **110**
* Available folders:
    * INBOX

#### Nauta over IMAP

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

## jNauta Net

### Features

* Version 0.3.0
    * Allows to login and logout from the Nauta net
    * Gets user available time
    * Informs about bad username and password
    * Informs about expired accounts (no money)
    * Informs about an account already connected to the network

### Implementation

The connection was implemented using three different librarys, known as "connection methods":

* **DEFAULT** uses java.net library
* **OK_HTTP** uses oknttp3 library
* **HTTP_CLIENT** uses org.apache.http.client library

Currently the DEFAULT method (java.net library) works perfectly. Other two methods redirects login page
to https://google.com, avoiding to get the logout url.
So its highly recommended **NOT TO USE** the OK_HTTP and HTTP_CLIENT methods yet.

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
