<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<html>
<head>
    <title>AML Declined Verification</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f5f5f5;
        }
        .container {
            width: 80%;
            margin: 20px auto;
            background-color: #ffffff;
            border: 1px solid #d1d5db;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }
        .header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 10px;
            border-bottom: 2px solid #e5e7eb;
            margin-bottom: 20px;
        }
        .header img {
            border-radius: 50%;
            width: 60px;
            height: 60px;
        }
        .potential-match {
            text-align: right;
        }
        .potential-match h2 {
            margin: 0;
            color: #f97316;
            font-size: 1.2em;
        }
        .section-title {
            font-size: 1.5em;
            color: #374151;
            border-bottom: 2px solid #e5e7eb;
            padding-bottom: 5px;
            margin-top: 20px;
        }
        .info-section {
            display: flex;
            margin-top: 20px;
        }
        .info-column {
            flex: 1;
            padding: 10px;
        }
        .info-row {
            display: flex;
            justify-content: space-between;
            padding: 5px 0;
        }
        .label {
            font-weight: bold;
            color: #4b5563;
        }
        .value {
            color: #1f2937;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <div class="header-info">
            <img src="${imageURL}" alt="Profile Image">
            <h2>${firstName} ${lastName} | ${country} | DOB: ${dob}</h2>
            <p class="label">${reference}</p>
        </div>
        
        <div class="potential-match">
            <h2>Potential Match | ${hits[2].matchPercentage}%</h2>
        </div>
    </div>

    <h2 class="section-title">Entity Details</h2>
    <div class="info-section">
        <div class="info-column">
            <c:if test="${not empty hits and not empty hits[2]}">
                <div class="info-row">
                    <span class="label">Address:</span>
                    <span class="value">${hits[2].address}</span>
                </div>
                <div class="info-row">
                    <span class="label">Alma Mater:</span>
                    <span class="value">${hits[2].almaMater}</span>
                </div>
                <div class="info-row">
                    <span class="label">Category:</span>
                    <span class="value">${hits[2].category}</span>
                </div>
                <div class="info-row">
                    <span class="label">Country:</span>
                    <span class="value">${hits[2].country}</span>
                </div>
                <div class="info-row">
                    <span class="label">Criminal Status:</span>
                    <span class="value">${hits[2].criminalStatus}</span>
                </div>
                <div class="info-row">
                    <span class="label">Entity Type:</span>
                    <span class="value">${hits[2].entityType}</span>
                </div>
                <div class="info-row">
                    <span class="label">Gender:</span>
                    <span class="value">${hits[2].gender}</span>
                </div>
                <div class="info-row">
                    <span class="label">Imprisoned At:</span>
                    <span class="value">${hits[2].imprisonedAt}</span>
                </div>
            </c:if>
        </div>

        <div class="info-column">
            <c:if test="${not empty hits and not empty hits[2]}">
                <div class="info-row">
                    <span class="label">Last Name:</span>
                    <span class="value">${hits[2].lastName}</span>
                </div>
                <div class="info-row">
                    <span class="label">Name:</span>
                    <span class="value">${hits[2].name}</span>
                </div>
                <div class="info-row">
                    <span class="label">Notes:</span>
                    <span class="value">${hits[2].notes}</span>
                </div>
                <div class="info-row">
                    <span class="label">Place of Birth:</span>
                    <span class="value">${hits[2].placeOfBirth}</span>
                </div>
                <div class="info-row">
                    <span class="label">Spouse:</span>
                    <span class="value">${hits[2].spouse}</span>
                </div>
                <div class="info-row">
                    <span class="label">Wanted By:</span>
                    <span class="value">${hits[2].wantedBy}</span>
                </div>
                <div class="info-row">
                    <span class="label">Years Active:</span>
                    <span class="value">${hits[2].yearsActive}</span>
                </div>
            </c:if>
        </div>
    </div>

    <div class="info-section">
        <div class="info-column">
            <c:if test="${not empty hits and not empty hits[2]}">
                <div class="info-row">
                    <span class="label">Alias:</span>
                    <span class="value">${hits[2].alternativeNames}</span>
                </div>
                <div class="info-row">
                    <span class="label">Born:</span>
                    <span class="value">${hits[2].born}</span>
                </div>
                <div class="info-row">
                    <span class="label">Children:</span>
                    <span class="value">${hits[2].children}</span>
                </div>
                <div class="info-row">
                    <span class="label">Criminal Charge:</span>
                    <span class="value">${hits[2].criminalCharge}</span>
                </div>
                <div class="info-row">
                    <span class="label">Date of Birth:</span>
                    <span class="value">${hits[2].dateOfBirth}</span>
                </div>
                <div class="info-row">
                    <span class="label">First Name:</span>
                    <span class="value">${hits[2].firstName}</span>
                </div>
                <div class="info-row">
                    <span class="label">Image URL:</span>
                    <span class="value"><a href="${hits[2].imageURL}">Link</a></span>
                </div>
            </c:if>
        </div>

        <div class="info-column">
            <c:if test="${not empty hits and not empty hits[2]}">
                <div class="info-row">
                    <span class="label">Known For:</span>
                    <span class="value">${hits[2].knownFor}</span>
                </div>
                <div class="info-row">
                    <span class="label">Location:</span>
                    <span class="value">${hits[2].location}</span>
                </div>
                <div class="info-row">
                    <span class="label">Nationality:</span>
                    <span class="value">${hits[2].nationality}</span>
                </div>
                <div class="info-row">
                    <span class="label">Organization:</span>
                    <span class="value">${hits[2].organization}</span>
                </div>
                <div class="info-row">
                    <span class="label">Relatives:</span>
                    <span class="value">${hits[2].relatives}</span>
                </div>
                <div class="info-row">
                    <span class="label">Victims:</span>
                    <span class="value">${hits[2].victims}</span>
                </div>
                <div class="info-row">
                    <span class="label">Wanted Since:</span>
                    <span class="value">${hits[2].wantedSince}</span>
                </div>
            </c:if>
        </div>
    </div>
</div>
</body>
</html>
