const urlParams = new URLSearchParams(window.location.search);

const hotelbedsCode = urlParams.get("hotelbedsCode");
const checkIn = urlParams.get("checkIn");
const nightCount = urlParams.get("nightCount");
const adults = urlParams.get("adults");
const cityFrom = urlParams.get("cityFrom");
const cityTo = urlParams.get("cityTo");

let currentHotel = null;
let selectedRoom = null;
let selectedFlightPair = null;

document.addEventListener("DOMContentLoaded", () => {
    setupBackLink();
    updateSectionSubtitles();
    loadHotelInfo();

    const changeRoomBtn = document.getElementById("changeRoom");
    if (changeRoomBtn) {
        changeRoomBtn.addEventListener("click", () => {
            showStep(1);
            window.scrollTo({ top: 0, behavior: "smooth" });
        });
    }
});

/* ========= Ссылка "Назад к турам" с теми же параметрами ========= */

function setupBackLink() {
    const backLink = document.querySelector(".back-link");
    if (!backLink) return;

    const toursParams = new URLSearchParams({
        "city-from": cityFrom || "",
        "city-to": cityTo || "",
        "departure_date": checkIn || "",
        "number_nights": nightCount || "",
        "number_persons": adults || ""
    });

    backLink.href = `tours.html?${toursParams.toString()}`;
}

function updateSectionSubtitles() {
    const roomSmall = document.querySelector("#roomSection .section-title small");
    if (roomSmall) {
        roomSmall.textContent = `(${nightCount} ${pluralizeNights(Number(nightCount))}, ${adults} ${pluralizePersons(Number(adults))})`;
    }

    const flightSmall = document.querySelector("#flightSection .section-title small");
    if (flightSmall) {
        flightSmall.textContent = `(${cityFrom} → ${cityTo}, ${adults} ${pluralizePersons(Number(adults))})`;
    }
}

function revealStep(step) {
    if (step >= 2) document.getElementById("flightSection")?.classList.remove("hidden");
    if (step >= 3) document.getElementById("checkoutSection")?.classList.remove("hidden");

    document.querySelectorAll(".booking-step").forEach(el => {
        const stepNum = Number(el.dataset.step);
        el.classList.toggle("active", stepNum === step);
        el.classList.toggle("completed", stepNum < step);
    });
}

/* ========= Шаг 1: отель и номера ========= */

async function loadHotelInfo() {
    const params = new URLSearchParams({ checkIn, nightCount, adults });

    try {
        const res = await fetch(`${API_BASE}/tours/${hotelbedsCode}?${params}`);
        if (!res.ok) throw new Error(`Ошибка ${res.status}`);
        currentHotel = await res.json();
        renderHotel(currentHotel);
    } catch (error) {
        console.error("Не удалось загрузить отель:", error);
    }
}

function renderHotel(hotel) {
    document.querySelector(".hotel-hero h1").textContent = hotel.name;
    document.querySelector(".hotel-location").textContent = hotel.location;
    document.querySelector(".hotel-description p").textContent = hotel.description;
    const safeRating = Math.max(0, Math.min(5, Number(hotel.rating) || 0));
    document.querySelector(".hotel-stars").textContent = "★ ".repeat(safeRating).trim();

    const heroImg = hotel.imageUrls?.[0];
    if (heroImg) {
        document.querySelector(".hotel-hero").style.backgroundImage = `url('${heroImg}')`;
    }

    showHotelMap(hotel.latitude, hotel.longitude);

    const roomsGrid = document.querySelector(".rooms-grid");
    roomsGrid.innerHTML = "";

    hotel.rooms.forEach(room => {
        const nights = calcNights(room.arrivalDate, room.departureDate);
        const pricePerNight = nights > 0 ? Math.round(room.price / nights) : room.price;

        const article = document.createElement("article");
        article.className = "room-card";
        article.innerHTML = `
            <div class="room-card__image" style="background-image: url('${escapeHtml(getImageOrDefault(room.imageUrl))}')"></div>
            <div class="room-card__content">
                <h3>${escapeHtml(room.roomType)}</h3>
                <p class="room-meal">${escapeHtml(room.meal)} · ${room.numberOfPerson} гостей</p>
                <div class="room-bottom">
                    <div>
                        <small>за ночь</small>
                        <strong>${formatMoney(pricePerNight)} ₽</strong>
                        <p>${formatMoney(room.price)} ₽ за ${nights} ${pluralizeNights(nights)}</p>
                    </div>
                    <button class="select-room" type="button">Выбрать</button>
                </div>
            </div>
        `;

        article.querySelector(".select-room").addEventListener("click", () => onRoomSelected(room));
        roomsGrid.appendChild(article);
    });
}

function onRoomSelected(room) {
    selectedRoom = room;

    const nights = calcNights(room.arrivalDate, room.departureDate);

    document.getElementById("selectedRoomName").textContent = room.roomType;
    document.getElementById("selectedRoomPrice").textContent =
        `${formatMoney(room.price)} ₽ за ${nights} ${pluralizeNights(nights)}`;

    loadFlights();
    revealStep(2);

    // скроллим к новому шагу, а не наверх страницы
    document.getElementById("flightSection")?.scrollIntoView({ behavior: "smooth", block: "start" });
}

/* ========= Шаг 2: перелёты ========= */

async function loadFlights() {
    const params = new URLSearchParams({ cityFrom, cityTo, departureDate: checkIn, nightCount });

    try {
        const res = await fetch(`${API_BASE}/tours/${hotelbedsCode}/flights?${params}`);
        if (!res.ok) throw new Error(`Ошибка ${res.status}`);
        const flights = await res.json();
        renderFlights(flights);
    } catch (error) {
        console.error("Не удалось загрузить перелёты:", error);
    }
}

function renderFlights(flightPairs) {
    const list = document.querySelector(".flights-list");
    list.innerHTML = "";

    if (!flightPairs || flightPairs.length === 0) {
        list.innerHTML = `<p class="status-message">Перелётов не найдено.</p>`;
        return;
    }

    flightPairs.forEach(pair => {
        const card = document.createElement("div");
        card.className = "flight-card";
        card.innerHTML = `
            <div class="flight-segments">
                ${renderSegment("Туда", pair.outboundFlight)}
                ${renderSegment("Обратно", pair.returnFlight)}
            </div>
            <div class="flight-price">
                <small>всего за ${adults} ${pluralizePersons(Number(adults))}</small>
                <strong>${formatMoney(pair.totalPrice)} ₽</strong>
            </div>
            <button class="select-flight" type="button">Выбрать</button>
        `;

        card.querySelector(".select-flight").addEventListener("click", () => onFlightSelected(pair));
        list.appendChild(card);
    });
}

function renderSegment(label, flight) {
    const dep = new Date(flight.departureDate);
    const arr = new Date(flight.arrivalDate);
    const durationMs = arr - dep;
    const hours = Math.floor(durationMs / (1000 * 60 * 60));
    const minutes = Math.floor((durationMs % (1000 * 60 * 60)) / (1000 * 60));

    return `
        <div class="segment">
            <div class="segment-airline">
                <div class="airline-logo">${getAirlineInitials(flight.companyName)}</div>
                <strong>${escapeHtml(flight.companyName)}</strong>
            </div>
            <small class="segment-label">${label} · ${formatDate(dep)}</small>
            <div class="flight-route">
                <div class="flight-time">
                    <strong>${formatTime(dep)}</strong>
                    <small>${escapeHtml(flight.cityFrom)}</small>
                </div>
                <div class="flight-duration">
                    <span class="line">${hours}ч ${minutes}м</span>
                </div>
                <div class="flight-time">
                    <strong>${formatTime(arr)}</strong>
                    <small>${escapeHtml(flight.cityTo)}</small>
                </div>
            </div>
        </div>
    `;
}

function onFlightSelected(pair) {
    selectedFlightPair = pair;
    renderCheckout();
    revealStep(3);

    // скроллим к новому шагу, а не наверх страницы
    document.getElementById("flightSection")?.scrollIntoView({ behavior: "smooth", block: "start" });
}

/* ========= Шаг 3: оформление ========= */

function renderCheckout() {
    if (!selectedRoom || !selectedFlightPair || !currentHotel) return;
    
    const nights = calcNights(selectedRoom.arrivalDate, selectedRoom.departureDate);
    
    document.querySelector(".order-summary h3").textContent = currentHotel.name;
    
    document.getElementById("summaryRoom").textContent =
        `${selectedRoom.roomType} × ${nights} ${pluralizeNights(nights)}`;
    document.getElementById("summaryRoomPrice").textContent = `${formatMoney(selectedRoom.price)} ₽`;
    
    document.getElementById("summaryFlight").textContent = selectedFlightPair.outboundFlight.companyName;
    document.getElementById("summaryFlightPrice").textContent = `${formatMoney(selectedFlightPair.totalPrice)} ₽`;
    
    const total = Number(selectedRoom.price) + Number(selectedFlightPair.totalPrice);
    document.getElementById("summaryTotal").textContent = `${formatMoney(total)} ₽`;
    
    const dep = new Date(selectedFlightPair.outboundFlight.departureDate);
    const arr = new Date(selectedFlightPair.outboundFlight.arrivalDate);
    const durationMs = arr - dep;
    const hours = Math.floor(durationMs / (1000 * 60 * 60));
    const minutes = Math.floor((durationMs % (1000 * 60 * 60)) / (1000 * 60));
    
    const flightSummaryEl = document.querySelector(".flight-summary");
    if (flightSummaryEl) {
        flightSummaryEl.innerHTML = `
            🕘 ${formatTime(dep)} – ${formatTime(arr)} · ${hours}ч ${minutes}м
            <br>
            ✈ ${escapeHtml(selectedFlightPair.outboundFlight.companyName)}
        `;
    }
    
}

const orderForm = document.getElementById('orderForm');

const orderConfirmModal = document.getElementById('orderConfirmModal');
const orderSuccessModal = document.getElementById('orderSuccessModal');

const cancelOrder = document.getElementById('cancelOrder');
const confirmOrder = document.getElementById('confirmOrder');
const closeSuccess = document.getElementById('closeSuccess');


/* ========= Отправка формы ========= */

if (orderForm) {
    orderForm.addEventListener('submit', async function (event) {
        event.preventDefault();

        try {
            // Проверяем авторизацию пользователя
            const response = await fetch(`${API_BASE}/account`, {
                credentials: 'include'
            });

            if (!response.ok) {
                alert('Для оформления заказа необходимо войти в аккаунт.');
                return;
            }

            // Пользователь авторизован
            orderConfirmModal.classList.add('active');

        } catch (error) {
            console.error('Не удалось проверить авторизацию:', error);
        }
    });
}


/* ========= Отмена подтверждения ========= */

if (cancelOrder) {
    cancelOrder.addEventListener('click', function () {
        orderConfirmModal.classList.remove('active');
    });
}


/* ========= Подтверждение заказа ========= */

if (confirmOrder) {
    confirmOrder.addEventListener('click', async function () {

        orderConfirmModal.classList.remove('active');

        await createOrder();
    });
}


/* ========= Создание заказа ========= */

async function createOrder() {

    if (!selectedRoom || !selectedFlightPair || !hotelbedsCode) {
        console.error('Недостаточно данных для создания заказа');
        return;
    }

    const firstName = document.getElementById('firstName')?.value.trim() || '';
    const lastName = document.getElementById('lastName')?.value.trim() || '';
    const phone = document.getElementById('phone')?.value.trim() || '';
    const wishes = document.getElementById('wishes')?.value.trim() || '';

    const request = {
        tourRequest: {
            hotelbedsCode: Number(hotelbedsCode),
            selectedRoom: selectedRoom,
            selectedFlight: selectedFlightPair
        }
    };

    const params = new URLSearchParams({
        firstName,
        lastName,
        phone,
        wishes
    });

    try {
        const response = await fetch(`${API_BASE}/orders?${params.toString()}`, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(request)
        });

        if (!response.ok) {
            const errorText = await response.text();

            console.error(
                'Ошибка создания заказа:',
                response.status,
                errorText
            );

            return;
        }

        const order = await response.json();

        console.log('Заказ создан:', order);

        orderSuccessModal.classList.add('active');

    } catch (error) {
        console.error('Не удалось создать заказ:', error);
    }
}


/* ========= Закрытие успешного оформления ========= */

if (closeSuccess) {
    closeSuccess.addEventListener('click', function () {
        orderSuccessModal.classList.remove('active');
    });
}

/* ========= Вспомогательные функции ========= */

function calcNights(startDate, endDate) {
    if (!startDate || !endDate) return 0;
    const diffMs = new Date(endDate) - new Date(startDate);
    return Math.round(diffMs / (1000 * 60 * 60 * 24));
}

function formatMoney(value) {
    if (value === undefined || value === null || value === "") return "";
    return Number(value).toLocaleString("ru-RU");
}

function formatTime(date) {
    return date.toLocaleTimeString("ru-RU", { hour: "2-digit", minute: "2-digit" });
}

function formatDate(date) {
    return date.toLocaleDateString("ru-RU", { day: "numeric", month: "long" });
}

function getAirlineInitials(name) {
    return name.split(" ").map(w => w[0]).join("").toUpperCase().slice(0, 2);
}

function pluralizeNights(count) {
    const mod10 = count % 10;
    const mod100 = count % 100;
    if (mod10 === 1 && mod100 !== 11) return "ночь";
    if ([2, 3, 4].includes(mod10) && ![12, 13, 14].includes(mod100)) return "ночи";
    return "ночей";
}

function pluralizePersons(count) {
    const mod10 = count % 10;
    const mod100 = count % 100;
    if (mod10 === 1 && mod100 !== 11) return "персона";
    if ([2, 3, 4].includes(mod10) && ![12, 13, 14].includes(mod100)) return "персоны";
    return "персон";
}

async function showHotelMap(latitude, longitude) {
    await ymaps3.ready;

    const {
        YMap,
        YMapDefaultSchemeLayer,
        YMapDefaultFeaturesLayer,
        YMapMarker
    } = ymaps3;

    const map = new YMap(
        document.getElementById("hotelMap"),
        {
            location: {
                center: [longitude, latitude],
                zoom: 16
            }
        }
    );

    map.addChild(new YMapDefaultSchemeLayer());
    map.addChild(new YMapDefaultFeaturesLayer());

    const markerElement = document.createElement("div");

    markerElement.style.width = "24px";
    markerElement.style.height = "24px";
    markerElement.style.background = "#E2725B";
    markerElement.style.borderRadius = "50%";
    markerElement.style.border = "3px solid white";
    markerElement.style.boxShadow = "0 2px 6px rgba(0,0,0,0.3)";

    map.addChild(
        new YMapMarker(
            {
                coordinates: [longitude, latitude]
            },
            markerElement
        )
    );
}

function getImageOrDefault(url) {
    return url && url.trim() !== "" ? url : "images/default_photo.png";
}

function escapeHtml(value) {
    const div = document.createElement("div");
    div.textContent = value === undefined || value === null ? "" : value;
    return div.innerHTML;
}