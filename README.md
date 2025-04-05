# **Advanced Search Engine**

A highly efficient and feature-rich search engine built with custom algorithms, including stemming, synonym expansion, Okapi BM25 ranking, and PageRank. This project leverages machine learning techniques for enhanced search relevance and performance.

---

## **Features**

- **Custom Ranking**: Combines Okapi BM25, PageRank, and a variety of other relevance metrics.
- **Synonym Expansion**: Automatically expands queries to account for synonyms.
- **Stemming**: Reduces words to their base form to improve matching.
- **Search Time Measurement**: Optimized to show search time for performance monitoring.
- **Pagination**: Efficient pagination to manage large sets of search results.
- **Session-Based Caching**: Avoids redundant searches by caching results across pages.
- **Machine Learning Integration**: Future-proof design with options for learning-to-rank models and relevance feedback.

---

## **How It Works**

1. **Search Query**: The user inputs a search query via a web interface.
2. **Search Engine**: The query is processed using a custom-built search engine powered by BM25 and PageRank, with optimizations like stemming and synonym expansion.
3. **Ranking**: Documents are ranked based on multiple factors such as term frequency, document length, and relevance.
4. **Caching**: Search results are cached for the duration of the session to speed up navigation across multiple pages.
5. **Machine Learning**: In future updates, machine learning models will be used to rank results based on user preferences and historical interaction.

---

## **Technologies Used**

- **Java**: Core language for the search engine.
- **Spring Boot**: For handling HTTP requests and serving the web interface.
- **Thymeleaf**: Templating engine for building dynamic HTML views.
- **Okapi BM25**: For ranking documents based on term frequency and document length.
- **PageRank**: For ranking web pages based on their inbound links.
- **Synonym Expansion**: Enhance search results with synonym-based matching.
- **Stemming**: Word normalization to improve search accuracy.
- **Machine Learning (Planned)**: Future implementation of a learning-to-rank model for personalized results.

---

## **How to Run**

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/yourusername/advanced-search-engine.git
   cd advanced-search-engine
