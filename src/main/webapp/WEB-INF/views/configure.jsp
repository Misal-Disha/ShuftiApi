<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>AML Filter Configure</title>
    <style>
        .container {
            width: 500px;
            margin: 50px auto;
            padding: 30px;
            border: 1px solid #d3d3d3;
            border-radius: 8px;
            background-color: #ffffff;
            box-shadow: 0px 6px 12px rgba(0,0,0,0.1);
            font-family: Arial, sans-serif;
        }
        h2 {
            font-size: 22px;
            font-weight: bold;
            margin-bottom: 25px;
            text-align: center;
            color: #333333;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            font-weight: bold;
            font-size: 14px;
            margin-bottom: 8px;
            color: #666666;
        }
        input[type="range"] {
            width: 100%;
            margin-top: 10px;
        }

        /* Styling for the Country dropdown button */
        .select-container {
            position: relative;
            width: 100%;
        }

        .country-button {
            background-color: #f7f7f7;
            border: 1px solid #cccccc;
            padding: 10px;
            width: 100%;
            font-size: 14px;
            color: #333333;
            border-radius: 4px;
            text-align: left;
            cursor: pointer;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .country-button::after {
            content: '▼'; /* Unicode for downward arrow */
            font-size: 14px;
            color: #666666;
        }

        .country-select {
            display: none; /* Hide dropdown initially */
            position: absolute;
            top: 100%;
            left: 0;
            width: 100%;
            border: 1px solid #cccccc;
            border-radius: 4px;
            background-color: #ffffff;
            max-height: 150px;
            overflow-y: auto;
            z-index: 10;
            box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
        }

        .country-select option {
            padding: 8px;
            cursor: pointer;
        }
        
        .country-select option:hover {
            background-color: #f0f0f0;
        }

        /* Style for the Filter List dropdown with arrow */
        .dropdown {
            position: relative;
            width: 100%;
        }

        .dropdown-button {
            background-color: #f7f7f7;
            border: 1px solid #cccccc;
            padding: 10px;
            width: 100%;
            font-size: 14px;
            color: #333333;
            border-radius: 4px;
            text-align: left;
            cursor: pointer;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .dropdown-button::after {
            content: '▼';
            font-size: 14px;
            color: #666666;
        }

        .dropdown-content {
            display: none;
            position: absolute;
            background-color: #ffffff;
            border: 1px solid #cccccc;
            padding: 10px;
            width: 100%;
            max-height: 150px;
            overflow-y: auto;
            box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
            z-index: 10;
        }

        .dropdown-content label {
            display: flex;
            align-items: center;
            margin: 6px 0;
        }
        
        .dropdown:hover .dropdown-content {
            display: block;
        }

        .form-buttons {
            display: flex;
            justify-content: space-between;
            margin-top: 20px;
        }
        
        button {
            padding: 12px;
            font-size: 16px;
            font-weight: bold;
            border: none;
            color: white;
            border-radius: 4px;
            cursor: pointer;
            transition: background-color 0.3s ease;
        }
        
        button:nth-child(1) {
            background-color: #4CAF50; /* Green for Configure */
        }
        
        button:nth-child(2) {
            background-color: #333333; /* Grey for Set */
        }
        
        button:hover {
            opacity: 0.9;
        }

        .sub-list {
            display: none;
            padding-left: 20px;
        }

        .checkbox-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 10px;
        }

        .submit-button {
            background-color: #007BFF; /* Blue for Submit */
            margin-top: 20px;
            width: 100%;
            padding: 12px;
            font-size: 16px;
            font-weight: bold;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            transition: background-color 0.3s ease;
        }

        .submit-button:hover {
            opacity: 0.9;
        }

        .additional-config {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 10px;
        }

        .config-buttons {
            display: flex;
            flex-direction: column;
            align-items: flex-end; /* Align buttons to the right */
            margin-top: 10px; /* Adjust spacing above buttons */
        }
    </style>
</head>
<body>
<div class="container">
    <h2>AML Filter Configure</h2>
    
    <div class="form-group">
        <label for="country">Country(s)</label>
        <div class="select-container">
            <div class="country-button" onclick="toggleCountryDropdown()">
                <span id="country-placeholder">Select Country(s)</span>
            </div>
            <select id="country" class="country-select" multiple size="5" onchange="updateCountryPlaceholder()">
                <!-- Country options will be populated by JavaScript -->
            </select>
        </div>
    </div>

    <div class="form-group">
        <label for="search-list">Search Lists <span style="color: red;">*</span></label>
        <div class="dropdown">
            <div class="dropdown-button" onclick="toggleFilterDropdown()">
                Select Filter
            </div>
            <div class="dropdown-content" id="dropdown-content">
                <label><input type="checkbox" name="filter" value="sip"> Special Interest Person (SIP)</label>
                <label><input type="checkbox" name="filter" value="sie"> Special Interest Entity (SIE)</label>
                <label><input type="checkbox" name="filter" value="insolvency"> Insolvency</label>
                <label><input type="checkbox" name="filter" value="sanctions"> Sanctions</label>
                <label><input type="checkbox" name="filter" value="warnings"> Warnings</label>
                <label><input type="checkbox" name="filter" value="fitness"> Fitness & Probity</label>
                <label><input type="checkbox" name="filter" value="adverse_media"> Adverse Media</label>
                
                <label>
                    <input type="checkbox" name="filter" value="pep_list" onclick="toggleSubList(this)">
                    PEP List
                </label>
                <div class="sub-list">
                    <label><input type="checkbox" name="filter" value="pep"> PEP</label>
                    <label><input type="checkbox" name="filter" value="pep_class_1"> PEP Class 1</label>
                    <label><input type="checkbox" name="filter" value="pep_class_2"> PEP Class 2</label>
                    <label><input type="checkbox" name="filter" value="pep_class_3"> PEP Class 3</label>
                </div>
            </div>
        </div>
    </div>
    
    <div class="form-group">
	    <label for="match-score">Match Score: <span id="matchScoreDisplay">50</span></label>
	    <input type="text" id="match-score" name="matchScore" value="50" oninput="updateMatchScoreDisplay(this.value)">
	</div>


    <div class="form-group">
        <label>Additional Configurations</label>
        <div class="additional-config">
            <label><input type="checkbox" name="matchRelatives"> Match Relatives & Close Associates</label>
            <label><input type="checkbox" name="matchAliases"> Match Aliases & Alternate Names</label>
        </div>
        <div class="config-buttons">
            <button onclick="configure()">Configure</button>
            <button onclick="set()">Set</button>
        </div>
    </div>

    <button class="submit-button" onclick="submitAMLCheck()">Submit</button>
</div>

<script>
    // Populate countries for dropdown
    const countries = [
        { code: "US", name: "United States" },
        { code: "CA", name: "Canada" },
        { code: "MX", name: "Mexico" },
        { code: "GB", name: "United Kingdom" },
        { code: "FR", name: "France" },
        { code: "DE", name: "Germany" },
        { code: "JP", name: "Japan" }
    ];

    function populateCountries() {
        const countrySelect = document.getElementById("country");
        countries.forEach(country => {
            const option = document.createElement("option");
            option.value = country.code;
            option.textContent = country.name;
            countrySelect.appendChild(option);
        });
    }

    function updateMatchScoreDisplay(value) {
	    const matchScoreDisplay = document.getElementById("matchScoreDisplay");
	    const numericValue = parseInt(value, 10);
	    if (!isNaN(numericValue) && numericValue >= 0 && numericValue <= 100) {
	        matchScoreDisplay.textContent = numericValue;
	    } else {
	        matchScoreDisplay.textContent = "Invalid";
	    }
	}


    function toggleCountryDropdown() {
        const countrySelect = document.getElementById("country");
        countrySelect.style.display = countrySelect.style.display === 'block' ? 'none' : 'block';
    }

    function updateCountryPlaceholder() {
        const countrySelect = document.getElementById("country");
        const selectedOptions = Array.from(countrySelect.selectedOptions);
        const placeholder = document.getElementById("country-placeholder");

        if (selectedOptions.length > 0) {
            placeholder.textContent = selectedOptions.map(option => option.text).join(", ");
        } else {
            placeholder.textContent = "Select Country(s)";
        }
    }

    function toggleFilterDropdown() {
        const dropdownContent = document.getElementById("dropdown-content");
        dropdownContent.style.display = dropdownContent.style.display === 'block' ? 'none' : 'block';
    }

    function toggleSubList(checkbox) {
        const subList = checkbox.parentNode.nextElementSibling;
        subList.style.display = checkbox.checked ? 'block' : 'none';
    }

    document.addEventListener("DOMContentLoaded", populateCountries);
    
    function configure() {
        // Collect settings for Match Aliases
        const matchAliases = document.querySelector('input[name="matchAliases"]').checked;

        // Additional logic for configuring Match Aliases can be added here
        alert("Configured Match Aliases: " + matchAliases);
    }

    function set() {
        // Logic for Set button can be added here
        alert("Set button clicked");
    }

    function submitAMLCheck() {
        // Collect country selections
        const countrySelect = document.getElementById("country");
        const selectedCountries = Array.from(countrySelect.selectedOptions).map(option => option.value);

        // Collect filter selections
        const filters = Array.from(document.querySelectorAll('input[name="filter"]:checked')).map(input => input.value);

        // Get the match score
        const matchScore = document.getElementById("match-score").value;

        // Get additional configurations
        const matchRelatives = document.querySelector('input[name="matchRelatives"]').checked;
        const matchAliases = document.querySelector('input[name="matchAliases"]').checked;

        // Prepare request payload
        const requestData = {
            countries: selectedCountries,
            filters: filters,
            matchScore: parseInt(matchScore),
            matchRelatives: matchRelatives,
            matchAliases: matchAliases
        };

        // Send data to backend
        fetch("/check", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestData)
        })
        .then(response => response.json())
        .then(data => {
            console.log("Response from server:", data);
            alert("AML Check Status: " + data.status + "\n" + data.data);
        })
        .catch(error => console.error("Error:", error));
    }
</script>
</body>
</html>
