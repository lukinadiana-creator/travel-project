document.addEventListener('DOMContentLoaded', async () => {
    const user = await fetchCurrentUser();

    if (!user) {
        window.location.href = 'index.html';
        return;
    }

    renderProfile(user);
    setupNameEditing(user);
    loadOrders();
});

async function fetchCurrentUser() {
    try {
        const res = await fetch(`${API_BASE}/account`, { credentials: 'include' });
        if (!res.ok) return null;
        return await res.json();
    } catch (error) {
        console.error('Не удалось получить данные пользователя:', error);
        return null;
    }
}

function renderProfile(user) {
    const nameEl = document.querySelector('.profile-card h3');
    const infoEl = document.querySelector('.profile-info');

    if (nameEl) {
        nameEl.textContent = user.name && user.name.trim() ? user.name : 'Имя не указано';
    }

    if (infoEl) {
        infoEl.innerHTML = `<p>${escapeHtml(user.email || '')}</p>`;
    }
}

async function loadOrders() {
    const grid = document.getElementById('ordersList');
    if (!grid) return;

    grid.innerHTML = '<p class="status-message">Загрузка...</p>';

    try {
        const res = await fetch(`${API_BASE}/orders`, { credentials: 'include' });
        if (!res.ok) throw new Error(`Ошибка сервера: ${res.status}`);

        const orders = await res.json();
        renderOrders(orders);
    } catch (error) {
        console.error('Не удалось загрузить заказы:', error);
        grid.innerHTML = '<p class="status-message">Не удалось загрузить историю заказов.</p>';
    }
}

function renderOrders(orders) {
    const grid = document.getElementById('ordersList');
    if (!grid) return;

    if (!orders || orders.length === 0) {
        grid.innerHTML = `
            <div class="empty-orders">
                <div class="empty-orders-icon">✈</div>
                <h2>Заказов пока нет</h2>
                <p>
                    Найдите подходящий тур на странице поиска
                    и оформите первое путешествие
                </p>
            </div>
        `;
        return;
    }

    grid.innerHTML = orders.map(createOrderCard).join('');
}

function createOrderCard(order) {
    const { id, hotelName, location, startTour, endTour, adults, amount, orderStatus } = order;
    const nights = calculateNights(startTour, endTour);

    return `
        <article class="order-card">
            <div class="order-top">
                <div>
                    <p class="order-id">Заказ ${escapeHtml(String(id ?? ''))}</p>
                    <h3>${escapeHtml(hotelName || '')}</h3>
                    <p class="location">${escapeHtml(location || '')}</p>
                </div>
                <span class="order-status">${escapeHtml(orderStatus || '')}</span>
            </div>

            <div class="order-bottom">
                <div class="order-details">
                    <span>Дата вылета: <b>${formatDate(startTour)}</b></span>
                    ${nights !== null ? `<span>${nights} ${pluralizeNights(nights)}</span>` : ''}
                    <span>${adults ?? ''} ${pluralizePersons(adults)}</span>
                </div>
                <div class="order-price">${formatMoney(amount)} ₽</div>
            </div>
        </article>
    `;
}

function calculateNights(start, end) {
    if (!start || !end) return null;
    const startDate = new Date(start);
    const endDate = new Date(end);
    const diffMs = endDate - startDate;
    if (Number.isNaN(diffMs)) return null;
    return Math.round(diffMs / (1000 * 60 * 60 * 24));
}

function formatDate(value) {
    if (!value) return '';
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return value;
    return date.toLocaleDateString('ru-RU', { day: 'numeric', month: 'long', year: 'numeric' });
}

function formatMoney(value) {
    if (value === undefined || value === null) return '';
    return Number(value).toLocaleString('ru-RU');
}

function pluralizeNights(count) {
    const mod10 = count % 10;
    const mod100 = count % 100;
    if (mod10 === 1 && mod100 !== 11) return 'ночь';
    if ([2, 3, 4].includes(mod10) && ![12, 13, 14].includes(mod100)) return 'ночи';
    return 'ночей';
}

function pluralizePersons(count) {
    const mod10 = count % 10;
    const mod100 = count % 100;
    if (mod10 === 1 && mod100 !== 11) return 'персона';
    if ([2, 3, 4].includes(mod10) && ![12, 13, 14].includes(mod100)) return 'персоны';
    return 'персон';
}

function escapeHtml(value) {
    const div = document.createElement('div');
    div.textContent = value === undefined || value === null ? '' : value;
    return div.innerHTML;
}

function renderProfile(user) {
    currentUser = user;

    const nameEl = document.getElementById('profileName');
    const infoEl = document.querySelector('.profile-info');

    if (nameEl) {
        nameEl.textContent = user.name && user.name.trim() ? user.name : 'Имя не указано';
    }

    if (infoEl) {
        infoEl.innerHTML = `<p>${escapeHtml(user.email || '')}</p>`;
    }
}

function setupNameEditing() {
    const editBtn = document.getElementById('editNameBtn');
    const overlay = document.getElementById('editNameOverlay');
    const closeBtn = document.getElementById('editNameClose');
    const form = document.getElementById('editNameForm');
    const input = document.getElementById('nameInput');

    if (!editBtn || !overlay || !form) return;

    editBtn.addEventListener('click', () => {
        input.value = currentUser?.name && currentUser.name.trim() ? currentUser.name : '';
        overlay.classList.add('active');
        input.focus();
    });

    closeBtn.addEventListener('click', () => {
        overlay.classList.remove('active');
    });

    overlay.addEventListener('click', (e) => {
        if (e.target === overlay) overlay.classList.remove('active');
    });

    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') overlay.classList.remove('active');
    });

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const name = input.value.trim();
        if (!name) return;

        try {
            const res = await fetch(`${API_BASE}/account?${new URLSearchParams({ name })}`, {
                method: 'PUT',
                credentials: 'include'
            });

            if (!res.ok) throw new Error(`Ошибка ${res.status}`);

            const updatedUser = await res.json();
            renderProfile(updatedUser);
            overlay.classList.remove('active');

        } catch (error) {
            console.error('Не удалось обновить имя:', error);
            alert('Не удалось сохранить имя. Попробуйте позже.');
        }
    });
}