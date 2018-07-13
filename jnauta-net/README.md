# jNauta Net

## Features

* Version 0.3.0
    * Allows to login and logout from the Nauta net
    * Gets user available time
    * Informs about bad username and password
    * Informs about expired accounts (no money)
    * Informs about an account already connected to the network

## Implementation

The connection was implemented using three different librarys, known as "connection methods":

* **DEFAULT** uses java.net library
* **OK_HTTP** uses oknttp3 library
* **HTTP_CLIENT** uses org.apache.http.client library

Currently the DEFAULT method (java.net library) works perfectly. Other two methods redirects login page
to https://google.com, avoiding to get the logout url.
So its highly recommended **NOT TO USE** the OK_HTTP and HTTP_CLIENT methods yet.
