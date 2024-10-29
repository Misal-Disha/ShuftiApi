<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>AML Check Result</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <style>
        .card {
            border-radius: 10px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }
        .card-header {
            background-color: #f5f5f5;
            font-weight: bold;
        }
        .status {
            background-color: #28a745;
            color: white;
            padding: 8px 12px;
            border-radius: 12px;
            font-size: 14px;
            float: right;
        }
        .verification-details li {
            display: inline-block;
            margin-right: 15px;
            margin-bottom: 10px;
            font-weight: 600;
        }
        .verification-details li::before {
            content: "\2022"; /* Bullet point */
            color: #28a745; /* Color of the bullet */
            font-weight: bold;
            display: inline-block; 
            width: 1em;
            margin-left: -1em;
        }
    </style>
</head>

<body class="container my-4">
    <div class="card">
        <div class="card-header d-flex justify-content-between align-items-center">
            <span>Verification has been successfully completed and accepted.</span>
            <span class="status">Accepted</span>
        </div>
        <div class="card-body">
            <div class="mb-3">
                <strong>Reference ID:</strong> ${reference} <br>
                <strong>Date:</strong> ${currentDate} <br>
                <strong>Browser Info:</strong> ${browserInfo} <br>
                <strong>Name:</strong> ${firstName} ${lastName} <br>
                <strong>DOB:</strong> ${dob}
            </div>
            <hr>

            <div>
			    <h5>Individual AML Screening Verification</h5>
			    <ul class="verification-details list-unstyled">
			        <c:forEach var="filter" items="${filters}">
			            <li>✔${filter}</li>
			        </c:forEach>
			    </ul>
			</div>

            <div>
                <ul class="list-unstyled">
                    <li><strong>Ongoing:</strong> No</li>
                    <li><strong>Rca Search:</strong> Yes</li>
                    <li><strong>Match Score:</strong> ${matchScore}</li>
                    <li><strong>Countries:</strong> ${country}</li>
                    <li><strong>Alias Search:</strong> No</li>
                </ul>
            </div>
        </div>
    </div>
</body>

<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.2/dist/js/bootstrap.bundle.min.js"></script>

</html>
