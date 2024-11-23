<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>AML Check Form</title>
    <style>
        /* Basic styling */
        body {
            font-family: Arial, sans-serif;
            max-width: 600px;
            margin: auto;
        }
        form {
            display: flex;
            flex-direction: column;
            gap: 15px;
        }
        label {
            font-weight: bold;
        }
        input[type="text"], input[type="date"], select {
            width: 100%;
            padding: 8px;
            box-sizing: border-box;
        }
        button {
            padding: 10px 15px;
            background-color: #4CAF50;
            color: white;
            border: none;
            cursor: pointer;
        }
        button:hover {
            background-color: #45a049;
        }
        .error {
            color: red;
        }
        .dropdown-container, .select-container {
            position: relative;
            display: inline-block;
            width: 100%;
        }
        .dropdown-btn, .country-button {
            padding: 8px;
            background-color: #f9f9f9;
            cursor: pointer;
            border: 1px solid #ccc;
            text-align: left;
            width: 100%;
        }
        .dropdown-btn:after, .country-button:after {
            content: ' ▼';
            float: right;
        }
        .dropdown-menu, .country-select {
            display: none;
            position: absolute;
            width: 100%;
            max-height: 150px;
            overflow-y: auto;
            border: 1px solid #ccc;
            background-color: #fff;
            z-index: 1;
        }
        .dropdown-menu label {
            display: block;
            padding: 8px;
            cursor: pointer;
        }
        .dropdown-menu label:hover {
            background-color: #f1f1f1;
        }
    </style>
</head>
<body>
    <h2>AML Check Configure</h2>
    <form id="amlForm">
        <!-- Name fields -->
        <div>
            <label for="firstName">First Name:</label>
            <input type="text" id="firstName" name="firstName" required>
            <div id="firstNameError" class="error" style="display:none;">First Name is mandatory.</div>
        </div>
        <div>
            <label for="middleName">Middle Name:</label>
            <input type="text" id="middleName" name="middleName">
        </div>
        <div>
            <label for="lastName">Last Name:</label>
            <input type="text" id="lastName" name="lastName" required>
            <div id="lastNameError" class="error" style="display:none;">Last Name is mandatory.</div>
        </div>
        <div>
            <label for="dob">Date of Birth:</label>
            <input type="date" id="dob" name="dob" required>
            <div id="dobError" class="error" style="display:none;">Date of Birth is mandatory.</div>
        </div>

        <!-- Filters Dropdown -->
        <div class="form-group">
            <label for="filters">Filters:</label>
            <div class="dropdown-container">
                <div class="dropdown-btn" onclick="toggleFilterDropdown()">
                    <span id="filterPlaceholder">Select Filters</span>
                </div>
                <div class="dropdown-menu" id="filterDropdownMenu">
                    <label><input type="checkbox" name="filters" value="sanction"> Sanction</label>
                    <label><input type="checkbox" name="filters" value="fitness-probity"> Fitness Probity</label>
                    <label><input type="checkbox" name="filters" value="warning"> Warning</label>
                    <label><input type="checkbox" name="filters" value="pep"> PEP</label>
                    <label><input type="checkbox" name="filters" value="pep-class-1"> PEP Class 1</label>
                    <label><input type="checkbox" name="filters" value="pep-class-2"> PEP Class 2</label>
                    <label><input type="checkbox" name="filters" value="pep-class-3"> PEP Class 3</label>
                    <label><input type="checkbox" name="filters" value="pep-class-4"> PEP Class 4</label>
                </div>
            </div>
            <div class="error" id="filterError" style="display:none;">Please select at least one filter.</div>
        </div>

        <!-- Country Selection -->
        <div class="form-group">
            <label for="country">Country(s)</label>
            <div class="select-container">
                <div class="country-button" onclick="toggleCountryDropdown()">
                    <span id="countryPlaceholder">Select Country</span>
                </div>
                <select id="country" class="country-select" multiple size="5" onchange="updateCountryPlaceholder()">
                    <option value="US">US</option>
                    <option value="CA">CA</option>
                    <option value="UK">UK</option>
                    <option value="DE">DE</option>
                    <option value="FR">FR</option>
                    <option value="IN">IN</option>
                </select>
            </div>
        </div>

        <!-- Match Score Input -->
        <div class="form-group">
            <label for="match-score">Match Score: (Select between 0-100) -  <span id="matchScoreDisplay">50</span></label>
            <input type="text" id="match-score" name="matchScore" value="50" oninput="updateMatchScoreDisplay(this.value)">
        </div>

        <!-- Submit Button -->
        <button type="button" id="submitButton">Submit</button>
    </form>

    <!-- JavaScript for handling form and dropdown functionality -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script type="text/javascript">
        function toggleFilterDropdown() {
            const dropdownMenu = document.getElementById("filterDropdownMenu");
            dropdownMenu.style.display = dropdownMenu.style.display === "block" ? "none" : "block";
        }

        function updateFilterPlaceholder() {
            const selectedFilters = Array.from(document.querySelectorAll("input[name=filters]:checked"));
            const placeholder = selectedFilters.length > 0 
                ? selectedFilters.map(filter => filter.parentElement.textContent.trim()).join(", ")
                : "Select Filters";
            document.getElementById("filterPlaceholder").innerText = placeholder;
        }

        document.querySelectorAll("input[name=filters]").forEach(checkbox => {
            checkbox.addEventListener("change", updateFilterPlaceholder);
        });

        function toggleCountryDropdown() {
            const countrySelect = document.getElementById("country");
            countrySelect.style.display = countrySelect.style.display === "block" ? "none" : "block";
        }

        function updateCountryPlaceholder() {
            const selectedCountries = Array.from(document.getElementById("country").selectedOptions);
            const placeholder = selectedCountries.length > 0 
                ? selectedCountries.map(option => option.text).join(", ")
                : "Select Country(s)";
            document.getElementById("countryPlaceholder").innerText = placeholder;
        }

        function updateMatchScoreDisplay(value) {
            document.getElementById("matchScoreDisplay").innerText = value;
        }

        $("#submitButton").click(function() {
            // Reset error messages
            $("#firstNameError").hide();
            $("#lastNameError").hide();
            $("#dobError").hide();  // Reset DOB error message
            $("#filterError").hide();

            // Validate required fields
            var isValid = true;

            // Check if First Name is empty
            if ($("#firstName").val().trim() === "") {
                $("#firstNameError").show();
                isValid = false;
            }

            // Check if Last Name is empty
            if ($("#lastName").val().trim() === "") {
                $("#lastNameError").show();
                isValid = false;
            }

            // Check if DOB is empty
            if ($("#dob").val().trim() === "") {
                $("#dobError").show();
                isValid = false;
            }

            if (isValid) {
                var formData = {
                    firstName: $("#firstName").val(),
                    middleName: $("#middleName").val(),
                    lastName: $("#lastName").val(),
                    dob: $("#dob").val(),
                    filters: $("input[name=filters]:checked").map(function() { return this.value; }).get(),
                    countries: $("#country").val(),
                    matchScore: $("#match-score").val()
                };

                if (formData.filters.length === 0) {
                    $("#filterError").show();
                    return;
                } else {
                    $("#filterError").hide();
                }

                $.ajax({
                    url: "<%= request.getContextPath() %>/api/aml/check",
                    type: "POST",
                    contentType: "application/json",
                    data: JSON.stringify(formData),
                    success: function(response) {
                        if (typeof response === "string") {
                            response = JSON.parse(response);
                        }
                        
                        if (response.redirectPage === "amlRetryPage") {
				            // Redirect to amlRetryPage.jsp if there's an error
				            window.location.href = "<%= request.getContextPath() %>/amlRetryPage.jsp";
				        } else {
	                        $("#result").html("<p>" + response.data + "</p>");
	
	                        var reference = response.reference;
	                        var status = response.status;
	
	                        if (status === "Passed") {
	                            window.location.href = "<%= request.getContextPath() %>/api/aml/result?reference=" + encodeURIComponent(reference) + "&status=Passed";
	                        } else if (status === "Failed") {
	                            window.location.href = "<%= request.getContextPath() %>/api/aml/amldeclined?reference=" + encodeURIComponent(reference) + "&status=Failed";
	                        } else{
	                        	window.location.href = "<%= request.getContextPath() %>/amlRetryPage.jsp?reference=" + encodeURIComponent(reference);
	                        }
	                    }
                    },
                    error: function(error) {
                        $("#result").html("<p>Error performing AML check</p>");
                    }
                });
            }
        });
    </script>
</body>
</html>
