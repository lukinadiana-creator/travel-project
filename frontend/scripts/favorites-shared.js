const FAVORITE_ENDPOINTS = {
    tours: 'favorites/hotels',
    attractions: 'favorites/attractions',
    restaurants: 'favorites/restaurants'
};

async function toggleFavorite(category, id, buttonEl) {
    console.log('toggleFavorite вызван:', category, id);
    if (!currentUser) {
        openLogin(); // из auth.js
        return;
    }

    const endpoint = FAVORITE_ENDPOINTS[category];
    const isActive = buttonEl.classList.contains('active');
    const method = isActive ? 'DELETE' : 'POST';

    setHeartState(buttonEl, !isActive);

    try {
        const res = await fetch(`${API_BASE}/${endpoint}/${id}`, {
            method,
            credentials: 'include'
        });

        if (!res.ok) throw new Error(`Ошибка ${res.status}`);

    } catch (error) {
        console.error('Не удалось обновить избранное:', error);
        setHeartState(buttonEl, isActive);
    }
}

function setHeartState(buttonEl, isFavorite) {
    const img = buttonEl.querySelector('img');

    if (isFavorite) {
        buttonEl.classList.add('active');
        if (img) img.src = 'images/heart-after.png';
    } else {
        buttonEl.classList.remove('active');
        if (img) img.src = 'images/heart_icon.png';
    }
}