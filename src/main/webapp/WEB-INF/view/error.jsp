<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Error</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
    <div class="container">
        <h1>Error</h1>
        <div class="error-message">
            <p>${error}</p>
        </div>
        <div class="links">
            <a href="${pageContext.request.contextPath}/">Back to Home</a>
        </div>
    </div>
</body>
</html>