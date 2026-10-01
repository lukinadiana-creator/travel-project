const API_BASE = "http://localhost:8080";
let currentUser = null;


// ==========================================
// ЭЛЕМЕНТЫ АВТОРИЗАЦИИ
// ==========================================

const authOverlay = document.getElementById("authOverlay");
const authClose = document.getElementById("authClose");

const loginButton = document.getElementById("loginButton");

const loginForm = document.getElementById("loginForm");
const registerForm = document.getElementById("registerForm");

const showRegister = document.getElementById("showRegister");
const showLogin = document.getElementById("showLogin");

const loginFormElement = document.getElementById("loginFormElement");
const registerFormElement = document.getElementById("registerFormElement");


// ==========================================
// ОТКРЫТЬ ВХОД
// ==========================================

function openLogin() {
    clearAuthError("loginError");
    clearAuthError("registerError");

    if (loginForm) {
        loginForm.classList.add("active");
    }

    if (registerForm) {
        registerForm.classList.remove("active");
    }

    if (authOverlay) {
        authOverlay.classList.add("active");
    }
}


// ==========================================
// ОТКРЫТЬ РЕГИСТРАЦИЮ
// ==========================================

function openRegister() {
    clearAuthError("loginError");
    clearAuthError("registerError");

    if (registerForm) {
        registerForm.classList.add("active");
    }

    if (loginForm) {
        loginForm.classList.remove("active");
    }

    if (authOverlay) {
        authOverlay.classList.add("active");
    }
}


// ==========================================
// ЗАКРЫТЬ ОКНО
// ==========================================

function closeAuth() {
    if (authOverlay) {
        authOverlay.classList.remove("active");
    }
}


// ==========================================
// ПОЛУЧИТЬ ТЕКУЩЕГО ПОЛЬЗОВАТЕЛЯ
// ==========================================

async function fetchCurrentUser() {
    try {
        const response = await fetch(`${API_BASE}/account`, {
            method: "GET",
            credentials: "include"
        });

        if (response.status === 401) {
            return null;
        }

        if (!response.ok) {
            console.error("Ошибка получения пользователя:", response.status);
            return null;
        }

        return await response.json();

    } catch (error) {
        console.error("Не удалось получить данные пользователя:", error);
        return null;
    }
}


// ==========================================
// ВХОД
// ==========================================

async function handleLogin(email, password) {
    const form = document.getElementById("loginFormElement");
    clearFieldErrors(form);
    clearAuthError("loginError");

    try {
        const response = await fetch(`${API_BASE}/login`, {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: new URLSearchParams({ email, password }),
            credentials: "include"
        });

        if (response.ok) {
            const user = await fetchCurrentUser();
            if (!user) {
                showAuthError("loginError", "Вход выполнен, но сессия не сохранилась.");
                return;
            }
            closeAuth();
            window.location.href = "account.html";
            return;
        }

        if (response.status === 401) {
            showFieldError(document.getElementById("loginEmail"), "");
            showAuthError("loginError", "Неверный email или пароль");
            return;
        }

        const error = await parseErrorBody(response);
        showAuthError("loginError", error?.message || "Не удалось выполнить вход. Попробуйте позже.");

    } catch (error) {
        console.error("Ошибка входа:", error);
        showAuthError("loginError", "Сервер недоступен. Попробуйте позже.");
    }
}

// ==========================================
// РЕГИСТРАЦИЯ
// ==========================================

async function handleRegister(email, password) {
    const form = document.getElementById("registerFormElement");
    clearFieldErrors(form);
    clearAuthError("registerError");

    try {
        const response = await fetch(`${API_BASE}/registration`, {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: new URLSearchParams({ email, password }),
            credentials: "include"
        });

        if (response.ok) {
            const user = await fetchCurrentUser();
            if (!user) {
                showAuthError("registerError", "Регистрация выполнена, но войти автоматически не удалось.");
                return;
            }
            closeAuth();
            window.location.href = "account.html";
            return;
        }

        const error = await parseErrorBody(response);

        if (error?.fieldErrors) {
            applyFieldErrors(form, error.fieldErrors, {
                email: "registerEmail",
                password: "registerPassword"
            });
            return;
        }

        showAuthError("registerError", error?.message || "Не удалось зарегистрироваться. Попробуйте позже.");

    } catch (error) {
        console.error("Ошибка регистрации:", error);
        showAuthError("registerError", "Сервер недоступен. Попробуйте позже.");
    }
}


// ==========================================
// ОШИБКИ
// ==========================================

function showAuthError(elementId, message) {

    const element = document.getElementById(elementId);

    if (element) {
        element.textContent = message;
    }
}


function clearAuthError(elementId) {

    const element = document.getElementById(elementId);

    if (element) {
        element.textContent = "";
    }
}


// ==========================================
// SUBMIT ВХОДА
// ==========================================

if (loginFormElement) {

    loginFormElement.addEventListener("submit", function (event) {

        event.preventDefault();

        clearAuthError("loginError");

        const emailInput = document.getElementById("loginEmail");
        const passwordInput = document.getElementById("loginPassword");

        if (!emailInput || !passwordInput) {
            return;
        }

        const email = emailInput.value.trim();
        const password = passwordInput.value;

        if (!email || !password) {

            showAuthError(
                "loginError",
                "Введите email и пароль"
            );

            return;
        }

        handleLogin(email, password);
    });
}


// ==========================================
// SUBMIT РЕГИСТРАЦИИ
// ==========================================

if (registerFormElement) {

    registerFormElement.addEventListener("submit", function (event) {

        event.preventDefault();

        clearAuthError("registerError");

        const emailInput = document.getElementById("registerEmail");
        const passwordInput = document.getElementById("registerPassword");

        if (!emailInput || !passwordInput) {
            return;
        }

        const email = emailInput.value.trim();
        const password = passwordInput.value;

        if (!email || !password) {

            showAuthError(
                "registerError",
                "Введите email и пароль"
            );

            return;
        }

        handleRegister(email, password);
    });
}


// ==========================================
// "СОЗДАТЬ АККАУНТ"
// ==========================================

if (showRegister) {

    showRegister.addEventListener("click", function (event) {

        event.preventDefault();

        openRegister();
    });
}


// ==========================================
// "ВОЙТИ" ИЗ РЕГИСТРАЦИИ
// ==========================================

if (showLogin) {

    showLogin.addEventListener("click", function (event) {

        event.preventDefault();

        openLogin();
    });
}


// ==========================================
// КРЕСТИК
// ==========================================

if (authClose) {

    authClose.addEventListener("click", function () {

        closeAuth();
    });
}


// ==========================================
// КЛИК ПО ФОНУ
// ==========================================

if (authOverlay) {

    authOverlay.addEventListener("click", function (event) {

        if (event.target === authOverlay) {
            closeAuth();
        }
    });
}


// ==========================================
// ESC
// ==========================================

document.addEventListener("keydown", function (event) {

    if (event.key === "Escape") {
        closeAuth();
    }
});

// ==========================================
// ИНИЦИАЛИЗАЦИЯ
// ==========================================

async function updateAuthHeader() {
    currentUser = await fetchCurrentUser();

    const favoritesItem = document.getElementById('favoritesNavItem');

    if (currentUser) {
        if (favoritesItem) favoritesItem.style.display = 'flex';

        if (loginButton) {
            loginButton.textContent = "Личный кабинет";
            loginButton.href = "account.html";
            loginButton.onclick = null;
        }
        return;
    }

    if (favoritesItem) favoritesItem.style.display = 'none';

    if (loginButton) {
        loginButton.textContent = "Войти";
        loginButton.href = "#";
        loginButton.onclick = function (event) {
            event.preventDefault();
            openLogin();
        };
    }
}

document.addEventListener("DOMContentLoaded", function () {
    updateAuthHeader();
});