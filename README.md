Dolphin Streaming Server
========================

Project to create a streaming server, in java, following the RFC's RTP and RTSP.

In principle only the server will be developed because there are already many standard, and very good, clients in the Internet. This server will be able to broadcast live audio/video, image, webcam, microphone sound and the desktop.

Current Status

The current version is 0.3, which is be able to transmitting MPEG files (audio/video and/or audio only) to an "N" number of clients. The client used to test have been VLC.

Execution
---------

The main class is: com.dolphinss.Servidor.java

The client, as JMStudio should open a URL such as: rtsp://localhost/myfile.mp3. The "myfile.mp3" file must be found in the top directory to which the server runs. Here's an example of file structure:

    /myapp/
        /src/
        /lib/ 
    /myfile.mp3 

RTP/RSTP Clients
----------------

Below is a list of clients on the server works:

    VLC <a href="http://www.videolan.org/vlc/">VLC</a>s
