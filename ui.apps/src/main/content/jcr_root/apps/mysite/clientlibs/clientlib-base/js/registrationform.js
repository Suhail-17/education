(function() {
    'use strict';

    document.addEventListener('DOMContentLoaded', function() {
        initRegistrationForm();
    });

    function initRegistrationForm() {
        const form = document.getElementById('registration-form');
        const successMessage = document.getElementById('success-message');

        if (!form) return;

        // Add real-time validation
        const emailField = document.getElementById('email');
        const phoneField = document.getElementById('phone');
        const startDateField = document.getElementById('startDate');

        // Email validation
        if (emailField) {
            emailField.addEventListener('blur', function() {
                const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                if (this.value && !emailRegex.test(this.value)) {
                    this.setCustomValidity('Please enter a valid email address');
                } else {
                    this.setCustomValidity('');
                }
            });
        }

        // Phone validation
        if (phoneField) {
            phoneField.addEventListener('input', function() {
                this.value = this.value.replace(/\D/g, '');
                if (this.value && this.value.length !== 10) {
                    this.setCustomValidity('Phone number must be 10 digits');
                } else {
                    this.setCustomValidity('');
                }
            });
        }

        // Set minimum date to today
        if (startDateField) {
            const today = new Date().toISOString().split('T')[0];
            startDateField.setAttribute('min', today);
        }

        // Handle form submission
        form.addEventListener('submit', function(e) {
            e.preventDefault();

            // Validate form
            if (!form.checkValidity()) {
                form.reportValidity();
                return;
            }

            // Prepare form data
            const formData = new FormData();
            formData.append('fullName', document.getElementById('fullName').value);
            formData.append('email', document.getElementById('email').value);
            formData.append('phone', document.getElementById('phone').value || '');
            formData.append('course', document.getElementById('course').value);
            formData.append('experience', document.getElementById('experience').value || '');
            formData.append('startDate', document.getElementById('startDate').value);

            // Submit to servlet
            submitToServlet(formData, form, successMessage);
        });
    }

    function submitToServlet(formData, form, successMessage) {
        const submitBtn = form.querySelector('.btn-primary');
        const originalText = submitBtn.textContent;

        // Show loading state
        submitBtn.textContent = 'Submitting...';
        submitBtn.disabled = true;

        // Make AJAX call to servlet
        fetch('/bin/mysite/registration', {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // Show success message
                if (successMessage) {
                    successMessage.style.display = 'block';
                    successMessage.innerHTML = '<p>' + data.message + '</p>';
                    if (data.registrationId) {
                        successMessage.innerHTML += '<p>Registration ID: ' + data.registrationId + '</p>';
                    }
                    successMessage.scrollIntoView({ behavior: 'smooth', block: 'center' });
                }

                // Reset form
                form.reset();

                // Hide success message after 7 seconds
                setTimeout(function() {
                    if (successMessage) {
                        successMessage.style.display = 'none';
                    }
                }, 7000);
            } else {
                // Show error
                alert('Error: ' + (data.message || 'Registration failed. Please try again.'));
            }
        })
        .catch(error => {
            console.error('Error submitting registration:', error);
            alert('An error occurred. Please try again.');
        })
        .finally(() => {
            // Reset button
            submitBtn.textContent = originalText;
            submitBtn.disabled = false;
        });
    }
})();