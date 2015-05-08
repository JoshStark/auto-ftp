[![Build Status](https://travis-ci.org/JAGFin1/auto-ftp.png?branch=master)](https://travis-ci.org/JAGFin1/auto-ftp)

Auto FTP
========

Auto FTP will listen on a connection for new files -- files you're interested in -- and will automatically add them to a download queue and process them. This means you can run this application, set up host information -- including file filters -- then leave it running in the background. It works by:

- Connecting to the host at a [user specified] interval.
- Navigates to a user specified remote directory.
- Scans all files that have been modified since last scan.
- Runs files through the filter list, picking out relevant files.
- Adds selected files to a download queue
- Downloads files, one at a time to a user specified local directory
- Closes connection once all files have been downloaded and updates the last scan date-time.


Project Setup
-------------

This is a Gradle based project and includes everything you'll need to get its dependencies. The Gradle Wrapper that has been included means you don't need to have Gradle pre-installed before you get the project. It will download any Gradle-specific libraries itself. Just do the following and Gradle should build the project for you:

UNIX/OS X:
```bash
./gradlew build
```

Windows:
```bash
gradlew build
```

This project is fully tested and all tests can be run via Gradle:
```bash
gradlew test
```

If you use Eclipse, it may be worth also running the Eclipse-specific tasks as well to help set up your .project:
```bash
gradlew eclipse
```

Quick FTP Example
-----------------
```java
Client client = new ClientFactory().createClient(ClientType.FTP);
client.setHost("a.host.name");
client.setPort(21);
client.setCredentials("username", "password");

Connection connection = client.connect();
connection.setRemoteDirectory("files/todownload");
  
List<FtpFile> remoteFiles = connection.listFiles();
  
for (FtpFile file : remoteFiles)
    System.out.println(file.getName());
    
client.disconnect();
```

Note: The protocol behind the client is defined via the factory. If you want an FTP connection, then use `ClientType.FTP`. If you want it to be SFTP, then use `ClientType.SFTP`. The protocol type doesn't affect any of the above code syntax; just how it behaves.

License
-------

```
The MIT License (MIT)

Copyright (c) 2015 Josh Stark

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
