document.getElementById('registrationForm').addEventListener('submit', async function(event) {
    event.preventDefault(); // Останавливаем стандартное поведение

    const username = document.getElementById('username').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    const registerData = { username, email, password };

    try {
        const response = await fetch('http://localhost:8088/v1/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(registerData)
        });

        if (response.ok) {
            alert('Registration succeeded! Redirection on login screen...');
        } else {
            const error = await response.json();
            alert(`Something gone wrong: ${error.message || response.statusText}`);
        }
    } catch (err) {
        alert('Error while trying to register!');
        console.error(err);
    }
});