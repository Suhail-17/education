(function() {
    "use strict";

    // Run this script only after the page's HTML is fully loaded
    document.addEventListener("DOMContentLoaded", function() {
        // Find the search input field on the page
        const searchInput = document.querySelector(".cmp-search__input");

        // If there's no search bar on this page, stop running the script
        if (!searchInput) {
            return;
        }

        // Create a new div to hold the search results and add it to the page
        const resultsContainer = document.createElement("div");
        resultsContainer.className = "custom-search-results";
        searchInput.closest('.cmp-search').insertAdjacentElement('afterend', resultsContainer);

        // Listen for when the user types in the search box
        searchInput.addEventListener("keyup", function() {
            const searchTerm = searchInput.value;

            // Don't search for very short terms
            if (searchTerm.length < 3) {
                resultsContainer.innerHTML = ""; // Clear any old results
                return;
            }

            // Make an AJAX call to your AEM servlet using the Fetch API
            fetch(`/bin/mysite/search?term=${searchTerm}`)
                .then(response => response.json()) // Parse the JSON data from the response
                .then(data => {
                    resultsContainer.innerHTML = ""; // Clear old results before displaying new ones

                    if (data.length > 0) {
                        const list = document.createElement('ul');
                        // Loop through the results and create a list item for each
                        data.forEach(course => {
                            const listItem = document.createElement('li');
                            listItem.innerHTML = `<a href="${course.pagePath}">${course.title}</a>`;
                            list.appendChild(listItem);
                        });
                        resultsContainer.appendChild(list);
                    } else {
                        resultsContainer.innerHTML = "<p>No results found.</p>";
                    }
                })
                .catch(error => {
                    console.error('Error fetching search results:', error);
                    resultsContainer.innerHTML = "<p>Error performing search.</p>";
                });
        });
    });
})();