<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Search Results</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/resultsStyle.css">
</head>
<body>
    <div class="container">
        <h1>Search Results</h1>
        <h3>Search completed in <strong>${searchTimeMs}</strong> ms.</h3>

        <div class="search-container">
            <form action="search" method="post">
                <input type="text" name="query" value="${query}" placeholder="Enter your search query...">
                <button type="submit">🔍 Search Again</button>
            </form>
        </div>

        <div class="results">
            <c:if test="${empty results}">
                <p>No results found for your query.</p>
            </c:if>

            <c:if test="${not empty results}">
                <p>Found <strong>${totalResults}</strong> results:</p>
                <ul>
                    <c:forEach var="result" items="${results}">
                        <li>
                            <strong>${result.title}</strong>
                            <br>
                            <a href="${result.url}" target="_blank">${result.url}</a>
                        </li>
                    </c:forEach>
                </ul>
            </c:if>
        </div>

        <div class="pagination">
            <form action="search" method="post">
                <input type="hidden" name="query" value="${query}">
                <input type="hidden" name="page" value="${currentPage - 1}">
                <c:if test="${hasPrevPage}">
                    <button type="submit" class="prev-page">⬅ Previous</button>
                </c:if>
            </form>

            <form action="search" method="post">
                <input type="hidden" name="query" value="${query}">
                <input type="hidden" name="page" value="${currentPage + 1}">
                <c:if test="${hasNextPage}">
                    <button type="submit" class="next-page">Next ➡</button>
                </c:if>
            </form>
        </div>


        <div class="links">
            <a href="${pageContext.request.contextPath}/">⬅ Back to Home</a>
        </div>
    </div>
</body>
</html>
