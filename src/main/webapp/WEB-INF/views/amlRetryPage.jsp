<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>AML Check Error</title>
</head>
<body>
    <h2>Error performing AML check</h2>
    <p>There was an issue with the AML check process. Please try again.</p>
    <button onclick="goBack()">Go back</button>

    <script>
        function goBack() {
            window.location.href = '<%= request.getContextPath() %>/api/aml/aml-form'; 
        }
    </script>
</body>
</html>
