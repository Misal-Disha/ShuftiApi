<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>AML Check</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
</head>
<body>
    <h1>AML Check Page</h1>
    
    <!-- Button to trigger the API call -->
    <button id="amlCheckButton">Perform AML Check</button>
    
    <!-- Retry Button -->
	<button id="retryButton" style="display:inline-block; background-color: yellow;">In Progress</button>
    
    <!-- Div to show the response -->
    <div id="result"></div>
    
    <script type="text/javascript">
    $(document).ready(function() {
        // On Perform AML Check button click
        $("#amlCheckButton").click(function() {
            var requestData = JSON.stringify({ key: "value" });

            $.ajax({
                url: "https://Devshuftipro.flairminds.com/api/aml/amlCheckPage",
                type: "POST",
                contentType: "application/json",
                data: requestData,
                success: function(response) {
                    // Parse the response (since it's returned as JSON)
                    var parsedResponse = JSON.parse(response);
                    
                    // Display the result
                    $("#result").html("<p>" + parsedResponse.data + "</p>");
                    
                    // Extract reference, status, and other needed data
                    var reference = parsedResponse.reference;
                    var status = parsedResponse.status;

                    // Change Retry button text based on status
                    if (status === "Passed") {
                        $("#retryButton").text("Passed").css('background-color', 'green').show();
                        // Redirect to the result page with data as query params
                        window.location.href = "/api/aml/result?reference=" + reference + "&status=Passed";
                    } else if (status === "Failed") {
                        $("#retryButton").text("Failed").css('background-color', 'red').show();
                        // Redirect to the failed result page with data as query params
                        window.location.href = "/api/aml/amldeclined?reference=" + reference + "&status=Failed";
                    } else {
                        $("#retryButton").text("Retry").css('background-color', 'yellow').show();
                    }
                },
                error: function(error) {
                    $("#retryButton").text("Retry").css('background-color', 'yellow').show();
                    $("#result").html("<p>Error performing AML check</p>");
                    console.log("Error:", error);
                }
            });
        });

        // Handle Retry button click (optional functionality)
        $("#retryButton").click(function() {
            $("#result").html("<p>Retrying AML Check...</p>");
            $("#amlCheckButton").trigger('click');  // Re-trigger the AML Check
        });
    });
</script>

</body>
</html>
