Auto FTP
========

Auto FTP will listen on a connection for new files -- files you're interested in -- and will automatically add them to a download queue and process them. This means you can run this application, set up host information -- including file filters -- then leave it running in the background. It works by:

- Connecting to the host at a [user specified] interval (default every 5 minutes).
- Navigates to a user specified remote directory.
- Scans all files that have been modified since last scan.
- Runs files through the filter list, picking out relevant files.
- Adds selected files to a download queue
- Downloads files, one at a time to a user specified local directory
- Closes connection once all files have been downloaded and updates the last scan date-time.

Quick FTP Example
-----------------

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


Project Status
--------------

**General**

|Feature|Status|
|-------|------|
|FTP Support|Done|
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
