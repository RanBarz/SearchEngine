# **Advanced Search Engine**

A highly efficient and feature-rich search engine built with custom algorithms, including stemming, synonym expansion, Okapi BM25 ranking, and PageRank. 

---

## **Features**

- **Custom Ranking**: Combines Okapi BM25, PageRank, and a variety of other relevance metrics.
- **Synonym Expansion**: Automatically expands queries to account for synonyms.
- **Stemming**: Reduces words to their base form to improve matching.
- **Web UI**: A simple and modern-looking web ui.
---

## **How It Works**

1. **Search Query**: The user inputs a search query via a web interface.
2. **Search Engine**: The query is processed using a custom-built search engine powered by BM25 and PageRank, with optimizations like stemming and synonym expansion.
3. **Ranking**: Documents are ranked based on multiple factors such as term frequency, document length, and relevance.

---

## **Technologies Used**

- **Java**: Core language for the search engine.
- **Spring Boot**: For handling HTTP requests and serving the web interface.
- **Okapi BM25**: For ranking documents based on term frequency and document length.
- **PageRank**: For ranking web pages based on their inbound links.
- **Synonym Expansion**: Enhance search results with synonym-based matching.
- **Stemming**: Word normalization to improve search accuracy.
