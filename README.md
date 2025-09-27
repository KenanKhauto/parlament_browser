# Parliament Browser  

## About  

**Parliament_Browser** is an application designed to analyze parliamentary protocols from the German Bundestag using Natural Language Processing (NLP) techniques.  

Key features include:  
- **Data ingestion**: Scraping and processing XML protocol files.  
- **Custom storage**: Extracted data is structured and stored in a MongoDB database.  
- **Web interface**: A Java Spark Webserver provides an accessible web application with navigation and menus.  
- **Sentiment analysis**: NLP models analyze speeches, discussions, and topics, providing insights into sentiment (positive, neutral, negative).  

The sentiment analysis pipeline leverages standard NLP techniques such as tokenization, part-of-speech tagging, and sentiment classification. Results are stored alongside protocol data, enabling sentiment-based queries and exploration.  

To start the application, simply run the `main` method (no arguments required).  
The webserver will launch, and you can access the interface at:  
- `http://localhost:4567`  
- Or from another device in the same network using the server’s IP and port `4567`.  

---

## Screenshots  
---
### Homepage
![Homepage](images/homepage1.png)

### Deputies View
![Deputies View](images/deputies_page1.png)

### Deputies View
![Deputies View](images/deputies_page2.png)

### Full Text Page with markdowns
![Full Text Page with markdowns](images/fulltext_page1.png)

### PDF Export
![PDF Export](images/latex_page2.png)

### Comment Network
![Comment Network](images/commentNetwork_page1.png)

### Speech Sentiment Network
![Speech Sentiment Network](images/speechSentimentNetwork_page1.png)

### Speech Topic Network
![Speech Topic Network](images/speechTopicNetwork_page1.png)

### Search Engine
![Search Engine](images/search_page1.png)

### Statistics
![Token Quantity](images/graphs_page2.png)
![POS Quantity](images/graphs_page3.png)
![Sentiment Distribution](images/graphs_page4.png)
![Named Entitiy Quantity Person](images/graphs_page5.png)
![Named Entitiy Quantity Location](images/graphs_page6.png)
![Named Entitiy Quantity Organisation](images/graphs_page7.png)
![Speaker Speech Count](images/graphs_page8.png)

---

### System Requirements

- Webserver: Java Version 1.8+
- Database: MongoDB Server Version 4.4.0+
- Browser Support: Safari 15.4+, Chrome 98+, Edge 98+, Firefox 94+, Opera 84+
- A Minimum of 4 GB RAM
- Broadband internet connection
- 200 MB of Free Disk Space

---

### Contributors  
- Kenan Khauto  
- Maximilian Chen  
- Simon Schütt  
- Stanley Mathew  
- Tim König  
