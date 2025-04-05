<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Search Engine</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
    <div class="container">
        <h1>Welcome to the RankTank Search Engine</h1>
        <div class="search-container">
            <form action="search" method="post">
                <input type="text" name="query" placeholder="Enter your search query...">
                <button type="submit">🔍 Search</button>
            </form>
        </div>
    </div>
</body>
</html>
