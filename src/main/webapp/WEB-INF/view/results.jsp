<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Search Results</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
    <style>
        /* Modern styling */
        body {
            font-family: 'Arial', sans-serif;
            background: #f4f4f4;
            display: flex;
            justify-content: center;
            align-items: flex-start; /* Ensures the content is visible */
            min-height: 100vh;
            padding: 20px;
            margin: 0;
        }

        .container {
            background: #fff;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
            text-align: center;
            width: 100%;
            max-width: 800px;
        }

        h1 {
            color: #333;
            font-size: 26px;
            margin-bottom: 10px;
        }

        h3 {
            color: #555;
            font-size: 18px;
            margin-bottom: 15px;
        }

        .search-container {
            margin: 20px 0;
        }

        input[type="text"] {
            width: 80%;
            padding: 12px;
            font-size: 16px;
            border: 1px solid #ccc;
            border-radius: 6px;
            outline: none;
            transition: all 0.3s ease;
        }

        input[type="text"]:focus {
            border-color: #007bff;
            box-shadow: 0 0 5px rgba(0, 123, 255, 0.3);
        }

        button {
            margin-top: 10px;
            padding: 12px 20px;
            background: #007bff;
            color: white;
            font-size: 16px;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            transition: background 0.3s ease;
        }

        button:hover {
            background: #0056b3;
        }

        .results {
            text-align: left;
            margin-top: 20px;
        }

        .results ul {
            list-style: none;
            padding: 0;
        }

        .results li {
            background: #f9f9f9;
            padding: 15px;
            margin: 10px 0;
            border-radius: 6px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .results li strong {
            font-size: 18px;
            color: #333;
        }

        .results li a {
            color: #007bff;
            text-decoration: none;
            font-size: 14px;
        }

        .results li a:hover {
            text-decoration: underline;
        }

        .links {
            margin-top: 20px;
        }

        .links a {
            color: #007bff;
            text-decoration: none;
            font-size: 16px;
        }

        .links a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Search Results</h1>
        <h3>Query: <strong>${query}</strong></h3>

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
                <p>Found <strong>${results.size()}</strong> results:</p>
                <ul>
                    <c:forEach var="result" items="${results}">
                        <li>
                            <strong>${result.title}</strong>
                            <br>
                            <a href="${result.url}" target="_blank">${result.url}</a>
                            <!-- Removed the text content -->
                        </li>
                    </c:forEach>
                </ul>
            </c:if>
        </div>

        <div class="links">
            <a href="${pageContext.request.contextPath}/">⬅ Back to Home</a>
        </div>
    </div>
</body>
</html>
