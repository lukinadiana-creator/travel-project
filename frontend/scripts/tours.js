const POPULAR_DESTINATIONS = ["Барселона", "Дубай", "Бали", "Мальдивы", "Рим"];

document.addEventListener("DOMContentLoaded", () => {
    renderPopularDestinations();

    const params = new URLSearchParams(window.location.search);

    if (params.has("city-to") || params.has("city-from")) {
        fillFormFromParams(params);
        setDefaultDate();
        showResultsState();
        loadTours(params);
    } else {
        setDefaultDate();
        showEmptyState();
    }

    const form = document.querySelector(".search-tours-form");

    if (form) {
        form.addEventListener("submit", (e) => {
            e.preventDefault();

            const searchParams = new URLSearchParams({
                "city-from": document.getElementById("city-from").value,
                "city-to": document.getElementById("city-to").value,
                "departure_date": document.getElementById("date").value,
                "number_nights": document.getElementById("night").value,
                "number_persons": document.getElementById("person").value
            });

            window.history.pushState(
                {},
                "",
                `${window.location.pathname}?${searchParams.toString()}`
            );

            showResultsState();
            loadTours(searchParams);
        });
    }
});


/* =========================
   Заполнение формы из URL
   ========================= */

function fillFormFromParams(params) {

    const map = {
        "city-from": "city-from",
        "city-to": "city-to",
        "departure_date": "date",
        "number_nights": "night",
        "number_persons": "person"
    };

    Object.entries(map).forEach(([param, inputId]) => {

        const value = params.get(param);
        const input = document.getElementById(inputId);

        if (value && input) {
            input.value = value;
        }
    });
}


/* =========================
   Популярные направления
   ========================= */

function renderPopularDestinations() {

    const list = document.querySelector(".popular-routes__list");

    if (!list) return;

    list.innerHTML = POPULAR_DESTINATIONS
        .map(city => `<button type="button" class="route-chip" data-city="${escapeHtml(city)}">${escapeHtml(city)}</button>`).join("");

    list.querySelectorAll(".route-chip").forEach(chip => {

        chip.addEventListener("click", () => {

            const cityToInput = document.getElementById("city-to");

            if (cityToInput) {
                cityToInput.value = chip.dataset.city;
            }

            document
                .querySelector(".search-tours-form")
                ?.requestSubmit();
        });
    });
}


/* =========================
   Состояния страницы
   ========================= */

function showEmptyState() {

    document
        .getElementById("heroSection")
        ?.classList.add("search-tours--empty");

    const popular = document.getElementById("popularRoutes");

    if (popular) {
        popular.style.display = "";
    }

    const results = document.getElementById("resultsSection");

    if (results) {
        results.style.display = "none";
    }
}


function showResultsState() {

    document
        .getElementById("heroSection")
        ?.classList.remove("search-tours--empty");

    const popular = document.getElementById("popularRoutes");

    if (popular) {
        popular.style.display = "none";
    }

    const results = document.getElementById("resultsSection");

    if (results) {
        results.style.display = "block";
    }
}


/* =========================
   Загрузка туров
   ========================= */
   async function loadTours(params) {
    const grid = document.querySelector(".tours_grid");
    const title = document.getElementById("toursTitle");
    const form = document.querySelector(".search-tours-form");
    if (!grid) return;

    clearFieldErrors(form);
    grid.innerHTML = `<p class="status-message">Загрузка...</p>`;

    const query = new URLSearchParams({
        cityFrom: params.get("city-from") || "",
        cityTo: params.get("city-to") || "",
        checkIn: params.get("departure_date") || "",
        nightCount: params.get("number_nights") || "",
        adults: params.get("number_persons") || ""
    });

    try {
        const response = await fetch(`${API_BASE}/tours?${query.toString()}`);

        if (!response.ok) {
            const error = await parseErrorBody(response);

            if (response.status >= 500) {
                redirectToErrorPage(error);
                return;
            }

            if (error?.fieldErrors) {
                applyFieldErrors(form, error.fieldErrors, {
                    cityFrom: "city-from",
                    cityTo: "city-to",
                    checkIn: "date",
                    nightCount: "night",
                    adults: "person"
                });
                grid.innerHTML = `<p class="status-message">Проверьте правильность заполнения формы.</p>`;
                if (title) title.textContent = "Найдено 0 туров";
                return;
            }

            grid.innerHTML = `<p class="status-message">${escapeHtml(error?.message || "Не удалось загрузить данные.")}</p>`;
            if (title) title.textContent = "Найдено 0 туров";
            return;
        }

        const tours = await response.json();
        renderTours(tours, query);

    } catch (error) {
        console.error("Не удалось загрузить туры:", error);
        grid.innerHTML = `<p class="status-message">Не удалось загрузить данные. Попробуйте позже.</p>`;
        if (title) title.textContent = "Найдено 0 туров";
    }
}

/* =========================
   Отрисовка туров
   ========================= */

function renderTours(tours, searchParams) {
    const grid = document.querySelector(".tours_grid");
    const title = document.getElementById("toursTitle");
    if (!grid) return;

    const count = tours ? tours.length : 0;

    if (title) {
        title.textContent = `Найдено ${count} ${pluralizeTours(count)}`;
    }


    if (!tours || tours.length === 0) {
        grid.innerHTML = `<p class="status-message">Туров по вашему запросу не найдено.</p>`;
        return;
    }

    grid.innerHTML = tours.map(tour => createTourCard(tour)).join("");

    grid.querySelectorAll(".favorite-btn").forEach((button, index) => {
        button.addEventListener("click", (e) => {
            e.stopPropagation();
            const tour = tours[index];
            toggleFavorite('tours', tour.hotelbedsCode, button);
        });
    });

    grid.querySelectorAll(".details-btn")
        .forEach((button, index) => {
            button.addEventListener("click", () => {
                const tour = tours[index];
                const hotelParams = new URLSearchParams({
                    hotelbedsCode: tour.hotelbedsCode,
                    checkIn: searchParams.get("checkIn"),
                    nightCount: searchParams.get("nightCount"),
                    adults: searchParams.get("adults"),
                    cityFrom: searchParams.get("cityFrom"),
                    cityTo: searchParams.get("cityTo")
                });

                window.location.href = `Hotel.html?${hotelParams.toString()}`;
            });
        });
}


/* =========================
   Карточка тура
   ========================= */

   function createTourCard(tour) {
    const stars = Number(tour.rating || tour.stars || 0);
    const starsHtml = '<span class="rating-star">★</span>'.repeat(Math.max(0, Math.min(5, stars)));
    const isFavorite = favoriteHotelIds.has(tour.hotelbedsCode);

    return `
        <div class="card-tours" data-id="${escapeHtml(tour.hotelbedsCode || "")}">
        <div class="card-tours_image" style="background-image: url('${escapeHtml(getImageOrDefault(tour.imageUrl))}')"></div>


            <div class="card-tours_info">
                <h3 class="card-tours_title">${escapeHtml(tour.name || "")}</h3>
                <div class="card-tours_rating">${starsHtml}</div>
                <div class="card-tours_location">${escapeHtml(tour.location || "")}</div>
                <div class="card-tours_price">Цена за тур</div>
                <div class="card-tours_money">${formatMoney(tour.minPrice)} ₽</div>
                <button class="details-btn" type="button">Подробнее<span>→</span></button>
            </div>
        </div>
    `;
}


/* =========================
   Склонение "тур"
   ========================= */

function pluralizeTours(count) {

    const mod10 = count % 10;
    const mod100 = count % 100;

    if (mod10 === 1 && mod100 !== 11) {
        return "тур";
    }

    if ([2, 3, 4].includes(mod10) && ![12, 13, 14].includes(mod100)) {
        return "тура";
    }

    return "туров";
}


/* =========================
   Формат цены
   ========================= */

function formatMoney(value) {

    if (value === undefined || value === null || value === "") {
        return "";
    }

    return Number(value).toLocaleString("ru-RU");
}


/* =========================
   Защита HTML
   ========================= */

function escapeHtml(value) {

    const div = document.createElement("div");

    div.textContent =
        value === undefined || value === null
            ? ""
            : value;

    return div.innerHTML;
}

function setDefaultDate() {
    const dateInput = document.getElementById("date");

    if (!dateInput) return;

    const today = new Date();

    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, "0");
    const day = String(today.getDate()).padStart(2, "0");

    const todayString = `${year}-${month}-${day}`;

    dateInput.min = todayString;

    if (!dateInput.value) {
        dateInput.value = todayString;
    }
}

let favoriteHotelIds = new Set();

async function loadFavoriteHotelIds() {
    currentUser = await fetchCurrentUser();
    if (!currentUser) return;
    try {
        const res = await fetch(`${API_BASE}/favorites/hotels`, { credentials: 'include' });
        if (!res.ok) return;
        const favorites = await res.json();
        favoriteHotelIds = new Set(favorites.map(f => f.hotelbedsCode));
    } catch (error) {
        console.error('Не удалось загрузить избранные туры:', error);
    }
}

function getImageOrDefault(url) {
    return url && url.trim() !== "" ? url : "images/default_photo.png";
}

async function initTours() {
    await loadFavoriteHotelIds();
}
document.addEventListener('DOMContentLoaded', initTours);