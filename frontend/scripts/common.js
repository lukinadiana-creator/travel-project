function clearFieldErrors(container) {
    container.querySelectorAll('.input-error').forEach(el => el.classList.remove('input-error'));
    container.querySelectorAll('.field-error-message').forEach(el => el.remove());
}

function showFieldError(input, message) {
    if (!input) return;
    input.classList.add('input-error');

    const msg = document.createElement('span');
    msg.className = 'field-error-message';
    msg.textContent = message;
    input.insertAdjacentElement('afterend', msg);
}


function applyFieldErrors(container, fieldErrors, fieldMap = {}) {
    clearFieldErrors(container);
    console.log('fieldErrors от сервера:', fieldErrors);

    fieldErrors.forEach(fe => {
        const cleanField = fe.field.includes('.') ? fe.field.split('.').pop() : fe.field;
        const inputId = fieldMap[cleanField] || cleanField;
        const input = document.getElementById(inputId);
        showFieldError(input, fe.message);
    });
}

async function parseErrorBody(response) {
    try {
        return await response.json();
    } catch {
        return null;
    }
}

function redirectToErrorPage(errorResponse) {
    const params = new URLSearchParams({
        status: errorResponse?.status || 500,
        message: errorResponse?.message || 'Внутренняя ошибка сервера'
    });

    if (errorResponse?.errorId) {
        params.set('errorId', errorResponse.errorId);
    }

    window.location.href = `error.html?${params.toString()}`;
}

async function detectUserCity() {
    const cityInput = document.getElementById("city-from");

    if (!cityInput || cityInput.value.trim() !== "") {
        return;
    }

    try {
        const response = await fetch("http://ip-api.com/json/?lang=ru");

        if (!response.ok) {
            throw new Error(`Ошибка определения города: ${response.status}`);
        }

        const data = await response.json();

        console.log("Ответ IP-API:", data);

        if (data.status === "success" && data.city) {
            cityInput.value = data.city;
        }

    } catch (error) {
        console.error("Не удалось определить город:", error);
    }
}

function setTodayDate() {

    const dateInput = document.getElementById('date');

    if (!dateInput) {
        return;
    }

    const today = new Date();

    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');

    dateInput.value = `${year}-${month}-${day}`;
}

document.addEventListener("DOMContentLoaded", () => {
    detectUserCity();
    setTodayDate();

    const cards = document.querySelectorAll(".card");

    cards.forEach(card => {
        card.addEventListener("click", () => {
            const city = card.querySelector(".card__subtitle").textContent.trim();

            const cityToInput = document.getElementById("city-to");
            if (cityToInput) {
                cityToInput.value = city;
            }

            document.querySelector(".search-form")?.requestSubmit();
        });
    });

});