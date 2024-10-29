<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>AML Check</title>
    <script>
        function performAMLCheck() {
            // Change button text to indicate the request is being processed
            document.getElementById("checkButton").innerText = "Processing...";
            
            // Perform the API call using Fetch API
            fetch("/api/aml/check", {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ requestData: "Your Request Data" }) // Modify with actual request data
            })
            .then(response => response.json())
            .then(data => {
                // If the API call is successful, update the button text
                if (data.status === 'Passed') {
                    document.getElementById("checkButton").innerText = "Passed";
                    // Redirect to result page after success
                    window.location.href = "/api/aml/result"; // Assuming this URL serves your result page
                } else {
                    document.getElementById("checkButton").innerText = "Retry";
                    alert("AML Check Failed: " + data.message);
                }
            })
            .catch(error => {
                console.error("Error:", error);
                document.getElementById("checkButton").innerText = "Retry";
            });
        }
    </script>
</head>
<body>
    <h1>AML Check Page</h1>
    <button id="checkButton" onclick="performAMLCheck()">Retry</button>
</body>
</html>
