(function() {
    'use strict';

    document.addEventListener('DOMContentLoaded', function() {
        initContactForm();
    });

    function initContactForm() {
        const form = document.getElementById('contact-form');
        if (!form) return;

        form.addEventListener('submit', function(e) {
            e.preventDefault();

            if (!validateForm(form)) {
                return;
            }

            const formData = new FormData();
            formData.append('name', document.getElementById('name').value);
            formData.append('email', document.getElementById('email').value);
            formData.append('subject', document.getElementById('subject').value);
            formData.append('message', document.getElementById('message').value);

            submitContactForm(formData, form);
        });

        // Add real-time validation
        const emailField = document.getElementById('email');
        if (emailField) {
            emailField.addEventListener('blur', function() {
                const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                if (this.value && !emailRegex.test(this.value)) {
                    this.classList.add('error');
                    showFieldError(this, 'Please enter a valid email address');
                } else {
                    this.classList.remove('error');
                    clearFieldError(this);
                }
            });
        }
    }

    function validateForm(form) {
        let isValid = true;
        const fields = form.querySelectorAll('input[required], textarea[required]');

        fields.forEach(field => {
            if (!field.value.trim()) {
                field.classList.add('error');
                showFieldError(field, 'This field is required');
                isValid = false;
            } else {
                field.classList.remove('error');
                clearFieldError(field);
            }
        });

        return isValid;
    }

    function showFieldError(field, message) {
        clearFieldError(field);
        const error = document.createElement('span');
        error.className = 'field-error';
        error.textContent = message;
        field.parentElement.appendChild(error);
    }

    function clearFieldError(field) {
        const error = field.parentElement.querySelector('.field-error');
        if (error) {
            error.remove();
        }
    }

    function submitContactForm(formData, form) {
        const submitBtn = form.querySelector('.btn');
        const originalText = submitBtn.textContent;

        // Show loading state
        submitBtn.textContent = 'Sending...';
        submitBtn.disabled = true;

        fetch('/bin/mysite/contact', {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // Show success message
                showMessage('success', data.message);
                form.reset();
            } else {
                showMessage('error', data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showMessage('error', 'Failed to send message. Please try again.');
        })
        .finally(() => {
            submitBtn.textContent = originalText;
            submitBtn.disabled = false;
        });
    }

    function showMessage(type, message) {
        // Remove existing message
        const existingMsg = document.querySelector('.form-message');
        if (existingMsg) {
            existingMsg.remove();
        }

        // Create and show new message
        const msgDiv = document.createElement('div');
        msgDiv.className = `form-message ${type}`;
        msgDiv.textContent = message;

        const form = document.getElementById('contact-form');
        form.parentElement.insertBefore(msgDiv, form);

        // Auto-hide after 5 seconds
        setTimeout(() => {
            msgDiv.remove();
        }, 5000);
    }
})();