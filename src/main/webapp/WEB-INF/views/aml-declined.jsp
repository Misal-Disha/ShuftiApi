<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>AML Check Result</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <style>
        body {
            background-color: #f4f5f7;
            font-family: Arial, sans-serif;
        }
        .container {
            margin-top: 20px;
        }
        .header {
            font-size: 20px;
            color: #d9534f;
            font-weight: bold;
            margin-bottom: 10px;
        }
        .declined-badge {
            background-color: #d9534f;
            color: white;
            border-radius: 12px;
            padding: 6px 12px;
            font-size: 14px;
        }
        .info-box {
            background-color: #fff;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0px 4px 12px rgba(0, 0, 0, 0.1);
            margin-bottom: 20px;
        }
        .info-title {
            font-weight: bold;
            font-size: 18px;
            margin-bottom: 10px;
        }
        .info-item {
            margin-bottom: 8px;
        }
        .browser-info {
            margin-top: 15px;
        }
        .browser-info span {
            display: inline-block;
            margin-right: 15px;
        }
        .section-title {
            font-weight: bold;
            margin-bottom: 10px;
            margin-top: 20px;
            color: #d9534f;
        }
        .aml-screening-title {
            font-size: 18px;
            font-weight: bold;
            margin-bottom: 10px;
        }
        .table-custom {
            margin-top: 10px;
            width: 100%;
            border-collapse: separate;
            border-spacing: 0 10px;
        }
        .table-custom th {
            background-color: #f7f7f7;
            padding: 10px;
            text-align: left;
            border-top-left-radius: 8px;
            border-top-right-radius: 8px;
        }
        .table-custom td {
            padding: 10px;
            background-color: #fff;
            box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
        }
        .status-success {
            color: #28a745;
        }
        .status-warning {
            color: #ffc107;
        }
        .status-danger {
            color: #d9534f;
        }
        .list-inline-item {
            display: inline-block;
            margin-right: 15px;
            margin-bottom: 10px;
            font-weight: 600;
        }
        .yellow-dot {
		    height: 10px;
		    width: 10px;
		    background-color: #ffc107; /* Bootstrap's warning color */
		    border-radius: 50%;
		    display: inline-block;
		    margin-right: 5px;
		    vertical-align: middle;
		}
        
    </style>
    
    <script>
        function showDetailedResults() {
            document.getElementById('detailedResults').style.display = 'block';
        }
        function showAMLResults() {
	        document.getElementById('amlResults').style.display = 'block';
	    }
	    
    </script>
</head>

<body>
    <div class="container">
        <!-- AML Check Result Header -->
        <div class="info-box d-flex justify-content-between align-items-center">
            <div class="header">${declinedReason}</div>
			<span class="declined-badge">Declined</span>
        </div>

        <!-- Reference and Browser Info -->
        <div class="info-box">
            <div class="info-title">Reference ID: ${reference}</div>
            <div class="info-item">
                <span><i class="bi bi-calendar"></i> ${date}</span>
                <span><i class="bi bi-clock"></i> ${time} (UTC+00:00)</span>
            </div>
            <div class="browser-info">
                <span><strong>IP:</strong> ${ip}</span>
                <span><strong>Location:</strong> ${location}</span>
                <span><strong>Browser:</strong> ${browser}</span>
            </div>
        </div>

        <!-- Individual AML Screening Verification -->
		<div class="info-box">
		    <div class="aml-screening-title">Individual AML Screening Verification</div>
		    <p class="list-inline-item">
		        ❌ Individual AML Screening
		    </p>
		    
		    <button class="btn btn-primary float-end" onclick="showAMLResults()">View AML Results</button>
		</div>
		
		<!-- First Row: Checks Performed -->
		<div class="row">
			<div class="col-md-12">
			<h5>Checks Performed</h5>
			<ul class="list-inline">
			<% 
			
			                List<String> filters = (List<String>) request.getAttribute("filters");
			
			                if (filters != null) {
			
			                    for (String filter : filters) {
			
			            %>
			<li class="list-inline-item">
			<span class="status-success">✔</span> <%= filter != null ? filter : "N/A" %>
			</li>
			<% 
			
			                    }
			
			                } 
			
			            %>
			</ul>
			</div>
			</div>

 
		
		<!-- Second Row: Sources -->
		<div class="row">
		    <div class="col-md-12">
		        <h5>Sources</h5>
		        <ul class="list-inline">
		            <c:forEach var="source" items="${sources}">
		                <li class="list-inline-item">
		                    <span class="status-success">✔</span> ${source}
		                </li>
		            </c:forEach>
		        </ul>
		    </div>
		</div>
		
		<!-- Third Row: Found Alert (Categories) -->
		<div class="row">
		    <div class="col-md-12">
		        <h5>Categories</h5>
		        <ul class="list-inline">
		            <c:forEach var="category" items="${categories}">
		                <li class="list-inline-item">
		                    <span class="yellow-dot"></span> ${category}
		                </li>
		            </c:forEach>
		        </ul>
		    </div>
		</div>
		
	    
		
		    <!-- Fourth Row: Match Status -->
		    <div class="row">
		        <div class="col-md-12">
		            <h5>Match Status</h5>
		            <p>Potential Match</p>
		        </div>
		    </div>
		</div>


		<!-- Detailed Results for Hits -->
		<div id="amlResults" style="display: none; overflow-y: auto; max-height: 400px; margin-top: 20px;">
		    <h5>AML Screening Detailed Results</h5>
		    <c:forEach var="hit" items="${hits}">
		        <div class="info-box">
		            <div class="aml-screening-title">Potential Match | ${hit.matchPercentage}%</div>
		            <p><strong>Name:</strong> ${hit.name}</p>
		            <p><strong>Date of Birth:</strong> ${hit.dob}</p>
		            <p><strong>Appears on:</strong> 
		                <c:forEach var="list" items="${hit.appearsOn}">
		                    <span class="badge bg-danger">${list}</span>
		                </c:forEach>
		            </p>
		            <p><strong>Countries:</strong> 
		                <c:forEach var="country" items="${hit.countries}">
		                    <span class="badge bg-secondary">${country}</span>
		                </c:forEach>
		            </p>
		            <p><strong>Relevance:</strong> 
		                <c:forEach var="relevance" items="${hit.relevance}">
		                    <span class="list-inline-item"><span class="yellow-dot"></span>${relevance}</span>
		                </c:forEach>
		            </p>
		        </div>
		    </c:forEach>
		</div>
		


    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>



