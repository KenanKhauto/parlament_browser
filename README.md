# Parliament_Browser

##### Contributors:
Kenan Khauto, 7592047 \
Maximilian Chen, 7716911 \
Simon Schütt, 7731983 \
Stanley Mathew, 2766232 \
Tim König, Matr.Nr. 3476673

### About

An application to analyze parliament protocols from the German Bundestag. It is based on reading the xml files of the
report, storing the data into own data structures, saving it to a mongoDB and providing database query requests.
All of this is accessible via a Java Spark Webserver, which hosts a Website with navigations and menus.

To start the program, just run the main method without any arguments. \
This launches the webserver and you can access the website on: `http://localhost:4567` or with any other device in the network under the server's ip address and port 4567.

### System Requirements

- Webserver: Java Version 1.8+
- Database: MongoDB Server Version 4.4.0+
- Browser Support: Safari 15.4+, Chrome 98+, Edge 98+, Firefox 94+, Opera 84+
- A Minimum of 4 GB RAM
- Broadband internet connection
- 200 MB of Free Disk Space
