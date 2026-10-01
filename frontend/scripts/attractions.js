const POPULAR_CITIES = ['Париж', 'Рим', 'Токио', 'Барселона', 'Нью-Йорк'];
let favoriteAttractionIds = new Set();
let attractionsData = [];
let attractionsMap = null;

document.addEventListener('DOMContentLoaded', async () => {
    initViewSwitcher();
    renderPopularCities();
    await loadFavoriteAttractionIds();

    const params = new URLSearchParams(window.location.search);
    const city = params.get('city');

    if (city) {
        const input = document.getElementById('city');
        if (input) input.value = city;
        showResultsState();
        loadAttractions(city);
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
            window.location.href = `attractions.html?city=${encodeURIComponent(chip.dataset.city)}`;
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

async function loadAttractions(city) {
    const grid = document.querySelector('.entertainments_grid');
    const form = document.querySelector('.search-entertainment-form');
    clearFieldErrors(form);
    grid.innerHTML = '<p class="status-message">Загрузка...</p>';

    try {
        const response = await fetch(`${API_BASE}/attractions?city=${encodeURIComponent(city)}`);

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

        const attractions = await response.json();
        attractionsData = attractions;
        renderAttractions(attractions);
    
    } catch (error) {
        console.error('Не удалось загрузить достопримечательности:', error);
        grid.innerHTML = '<p class="status-message">Не удалось загрузить данные. Попробуйте позже.</p>';
    }
}

function renderAttractions(attractions) {
    const grid = document.querySelector('.entertainments_grid');

    if (!attractions || attractions.length === 0) {
        grid.innerHTML = '<p class="status-message">Ничего не найдено для этого города.</p>';
        return;
    }

    grid.innerHTML = attractions.map(createAttractionCard).join('');

    grid.addEventListener('click', handleFavoriteClick);
}

async function handleFavoriteClick(event) {
    const btn = event.target.closest('.favorite-btn');
    if (!btn) return;

    const id = Number(btn.dataset.id);
    await toggleFavorite('attractions', id, btn);
}

function createAttractionCard(attraction) {
    const { id, attractionType, name, location, description, imageUrl } = attraction;
    const isFavorite = favoriteAttractionIds.has(id);

    return `
        <div class="card-entertainments" data-id="${id}">
        <div class="card-entertainments__image" style="background-image: url('${escapeHtml(getImageOrDefault(imageUrl))}')"></div>
            <button class="favorite-btn ${isFavorite ? 'active' : ''}" data-id="${id}">
                <img src="images/${isFavorite ? 'heart-after' : 'heart_icon'}.png">
            </button>
            <div class="card-content">
                <div class="card-category">${escapeHtml(attractionType)}</div>
                <h3 class="card-title">${escapeHtml(name)}</h3>
                <div class="card-location">${escapeHtml(location)}</div>
                <p class="card-description">${escapeHtml(description)}</p>
            </div>
        </div>
    `;
}

function escapeHtml(str) {
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}

async function loadFavoriteAttractionIds() {
    currentUser = await fetchCurrentUser();
    if (!currentUser) return;
    try {
        const res = await fetch(`${API_BASE}/favorites/attractions`, { credentials: 'include' });
        if (!res.ok) return;
        const favorites = await res.json();
        favoriteAttractionIds = new Set(favorites.map(f => f.id));
    } catch (error) {
        console.error('Не удалось загрузить избранные достопримечательности:', error);
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

                await renderAttractionsMap();
            }
        });
    });
}

async function renderAttractionsMap() {
    if (!attractionsData || attractionsData.length === 0) {
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

    const validAttractions = attractionsData.filter(
        attraction =>
            attraction.latitude != null &&
            attraction.longitude != null
    );

    if (validAttractions.length === 0) {
        mapContainer.innerHTML =
            '<p class="status-message">Для найденных достопримечательностей нет координат.</p>';
        return;
    }

    const coordinates = validAttractions.map(attraction => [
        Number(attraction.longitude),
        Number(attraction.latitude)
    ]);

    const center = getMapCenter(coordinates);

    attractionsMap = new YMap(
        mapContainer,
        {
            location: {
                center: center,
                zoom: calculateZoom(coordinates)
            }
        }
    );

    attractionsMap.addChild(new YMapDefaultSchemeLayer());
    attractionsMap.addChild(new YMapDefaultFeaturesLayer());

    validAttractions.forEach(attraction => {

        const markerElement = document.createElement('div');
    
        markerElement.className = 'map-marker-container';
    
        markerElement.innerHTML = `
            <div class="map-card">
                <img
                    src="${escapeHtml(getImageOrDefault(attraction.imageUrl))}"
                    alt="${escapeHtml(attraction.name)}"
                >
    
                <div class="map-card-content">
                    <div class="map-card-category">
                        ${escapeHtml(attraction.attractionType)}
                    </div>
    
                    <div class="map-card-title">
                        ${escapeHtml(attraction.name)}
                    </div>
    
                    <div class="map-card-location">
                        ${escapeHtml(attraction.location)}
                    </div>
                </div>
            </div>
    
            <div class="map-marker-dot"></div>
        `;
    
        attractionsMap.addChild(
            new YMapMarker(
                {
                    coordinates: [
                        Number(attraction.longitude),
                        Number(attraction.latitude)
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