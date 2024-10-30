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
	    .list-inline {
	        display: flex;
	        flex-wrap: wrap;
	        gap: 10px; /* Optional: spacing between items */
	        list-style-type: none;
	        padding: 0;
	        margin: 0;
	    }
	    .list-inline li {
	        background: #f0f0f0; /* Optional: background color for items */
	        padding: 10px;
	        border-radius: 5px;
	        text-align: left;
	    }
	    .aml-results-grid {
		    display: flex;
		    flex-wrap: wrap;
		    gap: 20px; /* Space between cards */
		}
		
		.aml-badge-red, .aml-badge-gray {
		    display: inline-block;
		    padding: 5px 10px;
		    border-radius: 3px;
		}
		
		.aml-badge-red {
		    background-color: red;
		    color: white;
		}
		
		.aml-badge-gray {
		    background-color: gray;
		    color: white;
		}
		
		.aml-inline-item {
		    display: flex;
		    align-items: center;
		}
		
		.aml-dot {
		    height: 10px;
		    width: 10px;
		    background-color: green; /* Change as needed */
		    border-radius: 50%;
		    margin-right: 5px;
		}
		.aml-results-container {
	        display: flex;
	        flex-wrap: wrap;
	        gap: 16px; /* Adds space between boxes */
	    }
	
	    .aml-result-box {
	        flex: 1 1 calc(33.333% - 16px); /* Three items per row */
	        box-sizing: border-box;
	        border: 1px solid #ddd;
	        padding: 16px;
	        border-radius: 8px;
	        background-color: #f9f9f9;
	    }
	
	    .aml-result-title {
	        font-weight: bold;
	        margin-bottom: 8px;
	    }
	    
	    

    </style>
    
    <script>
	    function showAMLResults() {
	        document.getElementById('amlResults').style.display = 'block';
	    } 
	    
	    function openDetailedView(reference) {
		    const url = `/api/aml/detailedamldeclined?reference=${reference}`;
		    window.location.href = url;
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
			<div class="info-box">
			   <h5>Filters Applied</h5>
			   <c:choose>
			       <c:when test="${not empty filters}">
			           <div class="list-inline">
			                   <li>✔${filters[0]}</li>
			                   <li>✔${filters[1]}</li>
			                   <li>✔${filters[2]}</li>
			                   <li>✔${filters[3]}</li>
			                   <li>✔${filters[4]}</li>
			                   <li>✔${filters[5]}</li>
			                   <li>✔${filters[6]}</li>
			                   <li>✔${filters[7]}</li>
			                   <li>✔${filters[8]}</li>
			           </div>
			       </c:when>
			   </c:choose>
			</div>
			
			
			
			<!-- Second Row: Sources -->
			<div class="info-box">
			    <h5>Sources</h5>
			    <div class="list-inline">
			        		   <li>✔${sources[0]}</li>
			                   <li>✔${sources[1]}</li>
			                   <li>✔${sources[2]}</li>
			                   <li>✔${sources[3]}</li>
			                   <li>✔${sources[4]}</li>
			                   <li>✔${sources[5]}</li>
			                   <li>✔${sources[6]}</li>
					    </div>
			</div>
			
			<!-- Third Row: Found Alert (Categories) -->
			<div class="info-box">
				<h5>Categories</h5>
			    <div class="list-inline">
			    	<li><span class="yellow-dot"></span> ${categories[0]}</li>
			        <li><span class="yellow-dot"></span> ${categories[1]}</li>
			        <li><span class="yellow-dot"></span> ${categories[2]}</li>
			        <li><span class="yellow-dot"></span> ${categories[3]}</li>
			        <li><span class="yellow-dot"></span> ${categories[4]}</li>
			        <li><span class="yellow-dot"></span> ${categories[5]}</li>
			        <li><span class="yellow-dot"></span> ${categories[6]}</li>
			    </div>
			</div>
		
	    
		
		    <!-- Fourth Row: Match Status -->
		    <div class="row">
		        <div class="col-md-12">
		            <h5>Match Status</h5>
		            <p><span class="yellow-dot"></span> Potential Match</p>
		        </div>
		    </div>
		</div>


		<div id="amlResults" class="aml-results-container">
    		<h5>AML Screening Detailed Results</h5>
		
		    <c:choose>
		        <!-- Display details for the first entry if available -->
		        <c:when test="${not empty hits[0]}">
		           <div class="aml-result-box" onclick="openDetailedView('${hit.reference}');">
		                <div class="aml-result-title">Potential Match | ${hits[0].matchPercentage}%</div>
		                <p><strong>Name:</strong> ${hits[0].name}</p>
		                <p><strong>Date of Birth:</strong> ${hits[0].dob}</p>
		                <p><strong>Appears on:</strong>
		                    <c:choose>
		                        <c:when test="${not empty hits[0].appearsOn[0]}">
		                            <span class="aml-badge-red">${hits[0].appearsOn[0]}</span>
		                        </c:when>
		                        <c:when test="${not empty hits[0].appearsOn[1]}">
		                            <span class="aml-badge-red">${hits[0].appearsOn[1]}</span>
		                        </c:when>
		                    </c:choose>
		                </p>
		                <p><strong>Countries:</strong>
		                    <c:choose>
		                        <c:when test="${not empty hits[0].countries[0]}">
		                            <span class="aml-badge-gray">${hits[0].countries[0]}</span>
		                        </c:when>
		                        <c:when test="${not empty hits[0].countries[1]}">
		                            <span class="aml-badge-gray">${hits[0].countries[1]}</span>
		                        </c:when>
		                    </c:choose>
		                </p>
		                <p><strong>Relevance:</strong>
		                    <c:choose>
		                        <c:when test="${not empty hits[0].relevance[0]}">
		                            <span class="aml-inline-item"><span class="aml-dot"></span>${hits[0].relevance[0]}</span>
		                        </c:when>
		                        <c:when test="${not empty hits[0].relevance[1]}">
		                            <span class="aml-inline-item"><span class="aml-dot"></span>${hits[0].relevance[1]}</span>
		                        </c:when>
		                    </c:choose>
		                </p>
		            </div>
		        </c:when>
		
		        <!-- Display details for the second entry if available -->
		        <c:when test="${not empty hits[1]}">
		            <div class="aml-result-box" onclick="openDetailedView('${hit.reference}');">
		                <div class="aml-result-title">Potential Match | ${hits[1].matchPercentage}%</div>
		                <p><strong>Name:</strong> ${hits[1].name}</p>
		                <p><strong>Date of Birth:</strong> ${hits[1].dob}</p>
		                <p><strong>Appears on:</strong>
		                    <c:choose>
		                        <c:when test="${not empty hits[1].appearsOn[0]}">
		                            <span class="aml-badge-red">${hits[1].appearsOn[0]}</span>
		                        </c:when>
		                        <c:when test="${not empty hits[1].appearsOn[1]}">
		                            <span class="aml-badge-red">${hits[1].appearsOn[1]}</span>
		                        </c:when>
		                    </c:choose>
		                </p>
		                <p><strong>Countries:</strong>
		                    <c:choose>
		                        <c:when test="${not empty hits[1].countries[0]}">
		                            <span class="aml-badge-gray">${hits[1].countries[0]}</span>
		                        </c:when>
		                        <c:when test="${not empty hits[1].countries[1]}">
		                            <span class="aml-badge-gray">${hits[1].countries[1]}</span>
		                        </c:when>
		                    </c:choose>
		                </p>
		                <p><strong>Relevance:</strong>
		                    <c:choose>
		                        <c:when test="${not empty hits[1].relevance[0]}">
		                            <span class="aml-inline-item"><span class="aml-dot"></span>${hits[1].relevance[0]}</span>
		                        </c:when>
		                        <c:when test="${not empty hits[1].relevance[1]}">
		                            <span class="aml-inline-item"><span class="aml-dot"></span>${hits[1].relevance[1]}</span>
		                        </c:when>
		                    </c:choose>
		                </p>
		            </div>
		        </c:when>
		
		        <!-- Repeat the above <c:when> structure for hits[2] to hits[5] as shown in the original code -->
		
		    </c:choose>
		    
		    <!-- Display details for the third entry if available -->
			<c:when test="${not empty hits[2]}">
			    <div class="aml-result-box" onclick="openDetailedView('${hit.reference}');">
			        <div class="aml-result-title">Potential Match | ${hits[2].matchPercentage}%</div>
			        <p><strong>Name:</strong> ${hits[2].name}</p>
			        <p><strong>Date of Birth:</strong> ${hits[2].dob}</p>
			        <p><strong>Appears on:</strong>
			            <c:choose>
			                <c:when test="${not empty hits[2].appearsOn[0]}">
			                    <span class="aml-badge-red">${hits[2].appearsOn[0]}</span>
			                </c:when>
			                <c:when test="${not empty hits[2].appearsOn[1]}">
			                    <span class="aml-badge-red">${hits[2].appearsOn[1]}</span>
			                </c:when>
			            </c:choose>
			        </p>
			        <p><strong>Countries:</strong>
			            <c:choose>
			                <c:when test="${not empty hits[2].countries[0]}">
			                    <span class="aml-badge-gray">${hits[2].countries[0]}</span>
			                </c:when>
			                <c:when test="${not empty hits[2].countries[1]}">
			                    <span class="aml-badge-gray">${hits[2].countries[1]}</span>
			                </c:when>
			            </c:choose>
			        </p>
			        <p><strong>Relevance:</strong>
			            <c:choose>
			                <c:when test="${not empty hits[2].relevance[0]}">
			                    <span class="aml-inline-item"><span class="aml-dot"></span>${hits[2].relevance[0]}</span>
			                </c:when>
			                <c:when test="${not empty hits[2].relevance[1]}">
			                    <span class="aml-inline-item"><span class="aml-dot"></span>${hits[2].relevance[1]}</span>
			                </c:when>
			            </c:choose>
			        </p>
			    </div>
			</c:when>
			
			<!-- Display details for the fourth entry if available -->
			<c:when test="${not empty hits[3]}">
			    <div class="aml-result-box" onclick="openDetailedView('${hit.reference}');">
			        <div class="aml-result-title">Potential Match | ${hits[3].matchPercentage}%</div>
			        <p><strong>Name:</strong> ${hits[3].name}</p>
			        <p><strong>Date of Birth:</strong> ${hits[3].dob}</p>
			        <p><strong>Appears on:</strong>
			            <c:choose>
			                <c:when test="${not empty hits[3].appearsOn[0]}">
			                    <span class="aml-badge-red">${hits[3].appearsOn[0]}</span>
			                </c:when>
			                <c:when test="${not empty hits[3].appearsOn[1]}">
			                    <span class="aml-badge-red">${hits[3].appearsOn[1]}</span>
			                </c:when>
			            </c:choose>
			        </p>
			        <p><strong>Countries:</strong>
			            <c:choose>
			                <c:when test="${not empty hits[3].countries[0]}">
			                    <span class="aml-badge-gray">${hits[3].countries[0]}</span>
			                </c:when>
			                <c:when test="${not empty hits[3].countries[1]}">
			                    <span class="aml-badge-gray">${hits[3].countries[1]}</span>
			                </c:when>
			            </c:choose>
			        </p>
			        <p><strong>Relevance:</strong>
			            <c:choose>
			                <c:when test="${not empty hits[3].relevance[0]}">
			                    <span class="aml-inline-item"><span class="aml-dot"></span>${hits[3].relevance[0]}</span>
			                </c:when>
			                <c:when test="${not empty hits[3].relevance[1]}">
			                    <span class="aml-inline-item"><span class="aml-dot"></span>${hits[3].relevance[1]}</span>
			                </c:when>
			            </c:choose>
			        </p>
			    </div>
			</c:when>
			
			<!-- Display details for the fifth entry if available -->
			<c:when test="${not empty hits[4]}">
			    <div class="aml-result-box" onclick="openDetailedView('${hit.reference}');">
			        <div class="aml-result-title">Potential Match | ${hits[4].matchPercentage}%</div>
			        <p><strong>Name:</strong> ${hits[4].name}</p>
			        <p><strong>Date of Birth:</strong> ${hits[4].dob}</p>
			        <p><strong>Appears on:</strong>
			            <c:choose>
			                <c:when test="${not empty hits[4].appearsOn[0]}">
			                    <span class="aml-badge-red">${hits[4].appearsOn[0]}</span>
			                </c:when>
			                <c:when test="${not empty hits[4].appearsOn[1]}">
			                    <span class="aml-badge-red">${hits[4].appearsOn[1]}</span>
			                </c:when>
			            </c:choose>
			        </p>
			        <p><strong>Countries:</strong>
			            <c:choose>
			                <c:when test="${not empty hits[4].countries[0]}">
			                    <span class="aml-badge-gray">${hits[4].countries[0]}</span>
			                </c:when>
			                <c:when test="${not empty hits[4].countries[1]}">
			                    <span class="aml-badge-gray">${hits[4].countries[1]}</span>
			                </c:when>
			            </c:choose>
			        </p>
			        <p><strong>Relevance:</strong>
			            <c:choose>
			                <c:when test="${not empty hits[4].relevance[0]}">
			                    <span class="aml-inline-item"><span class="aml-dot"></span>${hits[4].relevance[0]}</span>
			                </c:when>
			                <c:when test="${not empty hits[4].relevance[1]}">
			                    <span class="aml-inline-item"><span class="aml-dot"></span>${hits[4].relevance[1]}</span>
			                </c:when>
			            </c:choose>
			        </p>
			    </div>
			</c:when>
			
			<!-- Display details for the sixth entry if available -->
			<c:when test="${not empty hits[5]}">
			    <div class="aml-result-box" onclick="openDetailedView('${hit.reference}');">
			        <div class="aml-result-title">Potential Match | ${hits[5].matchPercentage}%</div>
			        <p><strong>Name:</strong> ${hits[5].name}</p>
			        <p><strong>Date of Birth:</strong> ${hits[5].dob}</p>
			        <p><strong>Appears on:</strong>
			            <c:choose>
			                <c:when test="${not empty hits[5].appearsOn[0]}">
			                    <span class="aml-badge-red">${hits[5].appearsOn[0]}</span>
			                </c:when>
			                <c:when test="${not empty hits[5].appearsOn[1]}">
			                    <span class="aml-badge-red">${hits[5].appearsOn[1]}</span>
			                </c:when>
			            </c:choose>
			        </p>
			        <p><strong>Countries:</strong>
			            <c:choose>
			                <c:when test="${not empty hits[5].countries[0]}">
			                    <span class="aml-badge-gray">${hits[5].countries[0]}</span>
			                </c:when>
			                <c:when test="${not empty hits[5].countries[1]}">
			                    <span class="aml-badge-gray">${hits[5].countries[1]}</span>
			                </c:when>
			            </c:choose>
			        </p>
			        <p><strong>Relevance:</strong>
			            <c:choose>
			                <c:when test="${not empty hits[5].relevance[0]}">
			                    <span class="aml-inline-item"><span class="aml-dot"></span>${hits[5].relevance[0]}</span>
			                </c:when>
			                <c:when test="${not empty hits[5].relevance[1]}">
			                    <span class="aml-inline-item"><span class="aml-dot"></span>${hits[5].relevance[1]}</span>
			                </c:when>
			            </c:choose>
			        </p>
			    </div>
			</c:when>
		    
		</div>
		
		
		


    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>



