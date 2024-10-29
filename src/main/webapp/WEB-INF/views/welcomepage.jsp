<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Transaction Page</title>
</head>
<body>
 
    <h1>Fund Transfer Form</h1>
 
    <!-- Transaction Form -->
    <form action="/fund-transfer" method="post">
 
        <label for="uuid">UUID:</label>
        <input type="text" id="uuid" name="uuid" required><br><br>
 
        <label for="batchID">Batch ID:</label>
        <input type="text" id="batchID" name="batchID" required><br><br>
 
        <label for="amount">Amount:</label>
        <input type="number" id="amount" name="amount" required><br><br>
 
        <label for="accountNo">Account Number:</label>
        <input type="text" id="accountNo" name="accountNo" required><br><br>
 
        <label for="ifscCode">IFSC Code:</label>
        <input type="text" id="ifscCode" name="ifscCode" required><br><br>
 
        <label for="beneficiaryName">Beneficiary Name:</label>
        <input type="text" id="beneficiaryName" name="beneficiaryName" required><br><br>
 
        <button type="submit">Submit Transaction</button>
    </form>
 
</body>
</html>