# Parliament_Browser
## Project Satuts

**Note: This project is not maintained anymore.**

A new version of the same project is being developed.

##### Contributors:
Kenan Khauto, 7592047 \
Maximilian Chen, 7716911 \
Simon Schütt, 7731983 \
Stanley Mathew, 2766232 \
Tim König, Matr.Nr. 3476673

### About

An application to analyze parliament protocols from the German Bundestag, utilizing NLP for sentiment analysis. It involves reading/scraping XML files of the reports, extracting relevant data, storing it in custom data structures, and persisting it in a MongoDB database. Additionally, the application provides database query functionalities and is accessible through a Java Spark Webserver, which hosts a website with navigations and menus.

With the integration of NLP for sentiment analysis, the application gains the ability to analyze the sentiment expressed in the parliament protocols. This involves employing NLP techniques to process the textual content of the reports and determine the sentiment associated with specific topics, discussions, or speeches. The sentiment analysis can provide insights into the emotional tone, opinions, and attitudes expressed within the protocols.

By leveraging NLP libraries and techniques, such as tokenization, part-of-speech tagging, and sentiment analysis algorithms, the application can process the text and assign sentiment scores or labels to different sections or speeches. These sentiment scores can range from positive to negative or be categorized into sentiment classes such as positive, neutral, or negative.

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
