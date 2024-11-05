<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<html>
<head>
    <title>AML Declined Verification</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f3f4f6;
            margin: 0;
            padding: 0;
        }
        .container {
            width: 80%;
            margin: 30px auto;
            background-color: #ffffff;
            border-radius: 12px;
            box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
            padding: 30px;
        }
        .header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding-bottom: 15px;
            border-bottom: 1px solid #d1d5db;
            margin-bottom: 20px;
        }
        .header-info {
            display: flex;
            align-items: center;
        }
        .header img {
            border-radius: 50%;
            width: 60px;
            height: 60px;
            margin-right: 15px;
        }
        .header-text {
            color: #374151;
        }
        .potential-match {
            text-align: right;
            color: #f97316;
            font-weight: bold;
            font-size: 1.2em;
        }
        .section-title {
            font-size: 1.5em;
            color: #374151;
            border-bottom: 1px solid #d1d5db;
            padding-bottom: 5px;
            margin-top: 25px;
            font-weight: bold;
        }
        .section {
            margin-bottom: 20px;
        }
        .info-section {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 20px;
            margin-top: 15px;
        }
        .info-column {
            display: grid;
            grid-template-columns: 1fr 2fr;
            align-items: center;
            padding: 10px 0;
            border-bottom: 1px solid #e5e7eb;
        }
        .label {
            font-weight: bold;
            color: #4b5563;
            text-align: left;
            padding-right: 15px;
        }
        .value {
            color: #1f2937;
            text-align: left;
        }
        .value a {
            color: #3b82f6;
            text-decoration: none;
        }
        .value a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <div class="header-info"> 
		    <img src="${imageURL}" alt="Profile Image">
		    <span>
		        <span id="name"></span> | 
		        <span id="country"></span> | DOB
		        <span id="dob"></span>
		    </span>
		</div>

        
        <div class="potential-match">
        	<span>
        		<span class="label">Potential Match |:</span>
                <span class="value" id="matchPercentage"></span>%
        	</span>
        </div>
    </div>
    
    <div class="reference-section">
			<h2 class="label">${reference}</h2>
	</div>

	<div class="info-section-section">
	    	<div class="header-info-row">
	            <div class="info-row-section">
	                    <span class="label">Relevance :</span>
	                    <span class="value" id="relevance"></span> |
	                    <span class="label">AppearsOn :</span>
	                    <span class="value" id="appearsOn"></span>
	                </div>
	            
	        </div>
	    </div>
	    
    <h2 class="section-title">Entity Details</h2>
    <div class="info-section">
        <div class="info-column">
            <c:if test="${not empty hits}">
                <div class="info-row">
                    <span class="label">Address:</span>
                    <span class="value" id="address"></span>
                </div>
                <div class="info-row">
                    <span class="label">Alma Mater:</span>
                    <span class="value">${hits[2].almaMater}</span>
                </div>
                <div class="info-row">
                    <span class="label">Category:</span>
                    <span class="value" id="Category"></span>
                </div>
                <div class="info-row">
                    <span class="label">Country:</span>
                    <span class="value" id="Country"></span>
                </div>
                <div class="info-row">
                    <span class="label">Criminal Status:</span>
                    <span class="value"></span>
                </div>
                <div class="info-row">
                    <span class="label">Entity Type:</span>
                    <span class="value" id="EntityType"></span>
                </div>
                <div class="info-row">
                    <span class="label">Gender:</span>
                    <span class="value" id="gender"></span>
                </div>
                <div class="info-row">
                    <span class="label">Imprisoned At:</span>
                    <span class="value"></span>
                </div>
            </c:if>
        </div>

        <div class="info-column">
        
        	<c:if test="${not empty hits}">
                <div class="info-row">
                    <span class="label">Alias:</span>
                    <span class="value" id="alternativeNames"></span>
                </div>
                <div class="info-row">
                    <span class="label">Born:</span>
                    <span class="value" id="Born"></span>
                </div>
                <div class="info-row">
                    <span class="label">Children:</span>
                    <span class="value"></span>
                </div>
                <div class="info-row">
                    <span class="label">Criminal Charge:</span>
                    <span class="value"></span>
                </div>
                <div class="info-row">
                    <span class="label">Date of Birth:</span>
                    <span class="value" id="Dob"></span>
                </div>
                <div class="info-row">
                    <span class="label">First Name:</span>
                    <span class="value" id="firstName"></span>
                    
                </div>
                <div class="info-row">
                    <span class="label">Image URL:</span>
                    <span class="value"></span>
                </div>
            </c:if>
        </div>
    </div>

    <div class="info-section">
        <div class="info-column">
            <c:if test="${not empty hits}">
                <div class="info-row">
                    <span class="label">Last Name:</span>
                    <span class="value" id="lastName" ></span>
                </div>
                <div class="info-row">
                    <span class="label">Name:</span>
                    <span class="value" id="Name"></span>
                </div>
                <div class="info-row">
                    <span class="label">Notes:</span>
                    <span class="value" id="notes"></span>
                </div>
                <div class="info-row">
                    <span class="label">Place of Birth:</span>
                    <span class="value" id="placeOfBirth"></span>
                </div>
                <div class="info-row">
                    <span class="label">Spouse:</span>
                    <span class="value" id="spouseNames"></span>
                </div>
                <div class="info-row">
                    <span class="label">Wanted By:</span>
                    <span class="value"></span>
                </div>
                <div class="info-row">
                    <span class="label">Years Active:</span>
                    <span class="value"></span>
                </div>
            </c:if>
        </div>

        <div class="info-column">
            <c:if test="${not empty hits}">
                <div class="info-row">
                    <span class="label">Known For:</span>
                    <span class="value"></span>
                </div>
                <div class="info-row">
                    <span class="label">Location:</span>
                    <span class="value"></span>
                </div>
                <div class="info-row">
                    <span class="label">Nationality:</span>
                    <span class="value" id="Nationality"></span>
                </div>
                <div class="info-row">
                    <span class="label">Organization:</span>
                    <span class="value"></span>
                </div>
                <div class="info-row">
                    <span class="label">Relatives:</span>
                    <span class="value" id="relatives"></span>
                </div>
                <div class="info-row">
                    <span class="label">Victims:</span>
                    <span class="value"></span>
                </div>
                <div class="info-row">
                    <span class="label">Wanted Since:</span>
                    <span class="value"></span>
                </div>
            </c:if>
        </div>
        
        
    </div>
</div>

<script>
        
        const hit = JSON.parse(sessionStorage.getItem('selectedHit'));
		console.log(hit,'hit');
 
        if (hit) {
            // Populate the fields with the hit data
            document.getElementById('firstName').textContent = hit.first_name || "N/A";
            document.getElementById('dob').textContent = hit.dob;
            document.getElementById('relevance').textContent = hit.relevance;
			document.getElementById('appearsOn').textContent = hit.appearsOn;
            document.getElementById('matchPercentage').textContent = hit.matchPercentage;
            document.getElementById('country').textContent = hit.country;
            document.getElementById('lastName').textContent = hit.last_name;
            document.getElementById('address').textContent = hit.address;
            document.getElementById('Category').textContent = hit.category;
            document.getElementById('gender').textContent = hit.gender;
			document.getElementById('name').textContent = hit.name;
			document.getElementById('Dob').textContent = hit.dob;
			document.getElementById('Name').textContent = hit.name;
			document.getElementById('Nationality').textContent = hit.nationality;
			document.getElementById('notes').textContent = hit.notes;
			document.getElementById('placeOfBirth').textContent = hit.placeOfBirth;
			document.getElementById('spouseNames').textContent = hit.spouseNames;
			document.getElementById('alternativeNames').textContent = hit.alternativeNames;
			document.getElementById('Country').textContent = hit.country;
			document.getElementById('EntityType').textContent = hit.entityType;
			document.getElementById('Born').textContent = hit.dob + " " + hit.placeOfBirth;
			document.getElementById('relatives').textContent = hit.relatives;
			
            document.getElementById('matchPercentage').textContent = hit.matchPercentage;
            
			
			

                        
        } else {
            console.error('No hit data found!');
        }
        
</script>

</body>
</html> 