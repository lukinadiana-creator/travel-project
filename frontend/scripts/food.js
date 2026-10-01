const POPULAR_CITIES = ['Париж', 'Рим', 'Токио', 'Барселона', 'Нью-Йорк'];
let favoriteRestaurantIds = new Set();
let restaurantsData = [];
let restaurantsMap = null;

document.addEventListener('DOMContentLoaded', async () => {
    initViewSwitcher();
    renderPopularCities();
    await loadFavoriteRestaurantIds();

    const params = new URLSearchParams(window.location.search);
    const city = params.get('city');

    if (city) {
        const input = document.getElementById('city');
        if (input) input.value = city;
        showResultsState();
        loadRestaurants(city);
    } else {
        showEmptyState();
    }
});

function renderPopularCities() {
    const list = document.querySelector('.popular-cities__list');
    if (!list) return;

    list.innerHTML = POPULAR_CITIES
        .map(city => `<button type="button" class="city-chip" data-city="${city}">${city}</button>`)
        .join('');

    list.querySelectorAll('.city-chip').forEach(chip => {
        chip.addEventListener('click', () => {
            window.location.href = `food.html?city=${encodeURIComponent(chip.dataset.city)}`;
        });
    });
}

function showEmptyState() {
    document.getElementById('heroSection')?.classList.add('search-entertainment--empty');
    const popular = document.getElementById('popularCities');
    if (popular) popular.style.display = '';
    const results = document.getElementById('resultsSection');
    if (results) results.style.display = 'none';
}

function showResultsState() {
    document.getElementById('heroSection')?.classList.remove('search-entertainment--empty');
    const popular = document.getElementById('popularCities');
    if (popular) popular.style.display = 'none';
    const results = document.getElementById('resultsSection');
    if (results) results.style.display = 'block';
}

async function loadRestaurants(city) {
    const grid = document.querySelector('.entertainments_grid');
    const form = document.querySelector('.search-entertainment-form');
    clearFieldErrors(form);
    grid.innerHTML = '<p class="status-message">Загрузка...</p>';

    try {
        const response = await fetch(`${API_BASE}/restaurants?city=${encodeURIComponent(city)}`);

        if (!response.ok) {
            const error = await parseErrorBody(response);

            if (response.status >= 500) {
                redirectToErrorPage(error);
                return;
            }

            if (error?.fieldErrors) {
                applyFieldErrors(form, error.fieldErrors, { city: "city" });
                grid.innerHTML = '<p class="status-message">Проверьте введённый город.</p>';
                return;
            }

            grid.innerHTML = `<p class="status-message">${escapeHtml(error?.message || "Не удалось загрузить данные.")}</p>`;
            return;
        }

        const restaurants = await response.json();
        restaurantsData = restaurants;
        renderRestaurants(restaurants);
    } catch (error) {
        console.error('Не удалось загрузить рестораны:', error);
        grid.innerHTML = '<p class="status-message">Не удалось загрузить данные. Попробуйте позже.</p>';
    }
}

function renderRestaurants(restaurants) {
    const grid = document.querySelector('.entertainments_grid');

    if (!restaurants || restaurants.length === 0) {
        grid.innerHTML = '<p class="status-message">Ничего не найдено для этого города.</p>';
        return;
    }

    grid.innerHTML = restaurants.map(createRestaurantCard).join('');
    grid.addEventListener('click', handleFavoriteClick);
}

async function handleFavoriteClick(event) {
    const btn = event.target.closest('.favorite-btn');
    if (!btn) return;

    const id = Number(btn.dataset.id);
    await toggleFavorite('restaurants', id, btn);
}

function createRestaurantCard(restaurant) {
    const { id, foodType, name, rating, location, averageBill, imageUrl } = restaurant;
    const isFavorite = favoriteRestaurantIds.has(id);
    console.log('ресторан id:', id, 'isFavorite:', isFavorite, 'Set содержит:', [...favoriteRestaurantIds]);

    return `
        <div class="card-entertainments" data-id="${id}">
        <div class="card-entertainments__image" style="background-image: url('${escapeHtml(getImageOrDefault(imageUrl))}')"></div>
            <button class="favorite-btn ${isFavorite ? 'active' : ''}" data-id="${id}">
                <img src="images/${isFavorite ? 'heart-after' : 'heart_icon'}.png">
            </button>
            <div class="card-content">
                <div class="card-category">${escapeHtml(foodType)}</div>
                <h3 class="card-title">${escapeHtml(name)}</h3>
                <div class="card-location">${escapeHtml(location)}</div>
                <div class="card-row">
                    <div class="card-rating"><span class="rating-star">★</span>${Number(rating).toFixed(1)}</div>
                    <div class="card-price">${averageBill}$</div>
                </div>
            </div>
        </div>
    `;
}

function escapeHtml(str) {
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}

async function loadFavoriteRestaurantIds() {
    currentUser = await fetchCurrentUser();
    console.log('currentUser:', currentUser);
    if (!currentUser) return;
    try {
        const res = await fetch(`${API_BASE}/favorites/restaurants`, { credentials: 'include' });
        if (!res.ok) return;
        const favorites = await res.json();
        console.log('избранные рестораны с бэка:', favorites);
        favoriteRestaurantIds = new Set(favorites.map(f => f.id));
        console.log('итоговый Set id:', favoriteRestaurantIds);
    } catch (error) {
        console.error('Не удалось загрузить избранные рестораны:', error);
    }
}

function getImageOrDefault(url) {
    return url && url.trim() !== "" ? url : "images/default_photo.png";
}

function initViewSwitcher() {
    const switcher = document.querySelector('.view-switcher');

    if (!switcher) return;

    const buttons = switcher.querySelectorAll('.view-switcher__button');
    const listView = document.getElementById('listView');
    const mapView = document.getElementById('mapView');

    buttons.forEach(button => {
        button.addEventListener('click', async () => {

            const view = button.dataset.view;

            buttons.forEach(btn => {
                btn.classList.remove('active');
            });

            button.classList.add('active');

            if (view === 'list') {
                listView.style.display = '';
                mapView.style.display = 'none';
            }

            if (view === 'map') {
                listView.style.display = 'none';
                mapView.style.display = 'block';

                await renderRestaurantsMap();
            }
        });
    });
}

async function renderRestaurantsMap() {
    if (!restaurantsData || restaurantsData.length === 0) {
        return;
    }

    await ymaps3.ready;

    const {
        YMap,
        YMapDefaultSchemeLayer,
        YMapDefaultFeaturesLayer,
        YMapMarker
    } = ymaps3;

    const mapContainer = document.getElementById('mapView');

    if (!mapContainer) return;

    mapContainer.innerHTML = '';

    const validRestaurants = restaurantsData.filter(
        restaurant =>
            restaurant.latitude != null &&
            restaurant.longitude != null
    );

    if (validRestaurants.length === 0) {
        mapContainer.innerHTML =
            '<p class="status-message">Для найденных ресторанов нет координат.</p>';
        return;
    }

    const coordinates = validRestaurants.map(restaurant => [
        Number(restaurant.longitude),
        Number(restaurant.latitude)
    ]);

    const center = getMapCenter(coordinates);

    restaurantsMap = new YMap(
        mapContainer,
        {
            location: {
                center: center,
                zoom: calculateZoom(coordinates)
            }
        }
    );

    restaurantsMap.addChild(new YMapDefaultSchemeLayer());
    restaurantsMap.addChild(new YMapDefaultFeaturesLayer());

    validRestaurants.forEach(restaurant => {

        const markerElement = document.createElement('div');

        markerElement.style.width = '28px';
        markerElement.style.height = '28px';
        markerElement.style.background = '#E2725B';
        markerElement.style.borderRadius = '50%';
        markerElement.style.border = '3px solid white';
        markerElement.style.boxShadow = '0 2px 6px rgba(0,0,0,0.3)';
        markerElement.style.cursor = 'pointer';
        markerElement.title = restaurant.name;

        restaurantsMap.addChild(
            new YMapMarker(
                {
                    coordinates: [
                        Number(restaurant.longitude),
                        Number(restaurant.latitude)
                    ]
                },
                markerElement
            )
        );
    });
}

function getMapCenter(coordinates) {
    const longitudes = coordinates.map(coord => coord[0]);
    const latitudes = coordinates.map(coord => coord[1]);

    const minLongitude = Math.min(...longitudes);
    const maxLongitude = Math.max(...longitudes);

    const minLatitude = Math.min(...latitudes);
    const maxLatitude = Math.max(...latitudes);

    return [
        (minLongitude + maxLongitude) / 2,
        (minLatitude + maxLatitude) / 2
    ];
}

function calculateZoom(coordinates) {
    if (coordinates.length === 1) {
        return 15;
    }

    const longitudes = coordinates.map(coord => coord[0]);
    const latitudes = coordinates.map(coord => coord[1]);

    const longitudeRange = Math.max(...longitudes) - Math.min(...longitudes);
    const latitudeRange = Math.max(...latitudes) - Math.min(...latitudes);

    const maxRange = Math.max(longitudeRange, latitudeRange);

    if (maxRange > 10) return 5;
    if (maxRange > 5) return 6;
    if (maxRange > 2) return 8;
    if (maxRange > 1) return 9;
    if (maxRange > 0.5) return 10;
    if (maxRange > 0.2) return 11;
    if (maxRange > 0.1) return 12;
    if (maxRange > 0.05) return 13;

    return 14;
}