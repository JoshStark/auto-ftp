Auto FTP
========

The idea behind Auto FTP is to listen on servers for files that move around often. If you have a remote datastore and need to receive files often, you'll need to manually connect via FTP and download them. Auto FTP will listen on a connection for new files -- files you're interested in -- and will automatically add them to a download queue and process them.

Project Status
--------------

**General**

|Feature|Status|
|-------|------|
|FTP Support|In progress|
|SFTP Support|Done|
|FTPS Support|Not yet implemented|
|Proxy Support|Not yet implemented|

**User Configuration**

|Feature|Status|
|-------|------|
|Download Directory|User can configure|
|Host Settings|Not yet implemented|
|File filtering|Not yet implemented|


**User Interface**

|Feature|Status|
|-------|------|
|Main screen|Not yet implemented|
|Terminal panel|Not yet implemented|
|Settings window|Not yet implemented|

Build Status
------------

[![Build Status](https://travis-ci.org/JAGFin1/auto-ftp.png?branch=master)](https://travis-ci.org/JAGFin1/auto-ftp)
