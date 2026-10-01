const tabs = document.querySelectorAll('.favorites-tab');
const sections = document.querySelectorAll('.favorites-content');
        
tabs.forEach(tab => {
    tab.addEventListener('click', () => {
        const category = tab.dataset.category;
        
        tabs.forEach(item => {
            item.classList.remove('active');
        });

        sections.forEach(section => {
            section.classList.remove('active');
        });

        tab.classList.add('active');

        document
            .getElementById(category)
            .classList.add('active');
    });
        
});

const CATEGORY_CONFIG = {
    // tours: {
    //     endpoint: 'favorites/hotels',
    //     grid: '.tours_grid',
    //     emptyTitle: 'Нет сохранённых туров',
    //     emptyText: 'Нажмите на сердечко на карточке, чтобы добавить тур в избранное',
    //     render: renderTourCard
    // },
    attractions: {
        endpoint: 'favorites/attractions',
        grid: '.entertainments_grid',
        emptyTitle: 'Нет сохранённых достопримечательностей',
        emptyText: 'Нажмите на сердечко на карточке, чтобы добавить место в избранное',
        render: renderAttractionCard
    },
    restaurants: {
        endpoint: 'favorites/restaurants',
        grid: '.entertainments_grid',
        emptyTitle: 'Нет сохранённых ресторанов',
        emptyText: 'Нажмите на сердечко на карточке, чтобы добавить ресторан в избранное',
        render: renderRestaurantCard
    }
};

document.addEventListener('DOMContentLoaded', async () => {
    currentUser = await fetchCurrentUser();

    if (!currentUser) {
        window.location.href = 'index.html';
        return;
    }

    for (const category of Object.keys(CATEGORY_CONFIG)) {
        await loadCategory(category);
    }

    setupTabs();
});

async function loadCategory(category) {
    const config = CATEGORY_CONFIG[category];
    const section = document.getElementById(category);
    const grid = section.querySelector(config.grid);
    const countEl = document.querySelector(`.favorites-tab[data-category="${category}"] b`);

    try {
        const res = await fetch(`${API_BASE}/${config.endpoint}`, { credentials: 'include' });
        if (!res.ok) throw new Error(`Ошибка ${res.status}`);

        const items = await res.json();
        if (countEl) countEl.textContent = items.length;

        if (items.length === 0) {
            grid.innerHTML = renderEmptyState(config.emptyTitle, config.emptyText);
            return;
        }

        grid.innerHTML = items.map(item => config.render(item)).join('');

        grid.querySelectorAll('.favorite-btn').forEach(btn => {
            btn.classList.add('active');
            btn.querySelector('img').src = 'images/heart-after.png';

            btn.addEventListener('click', async () => {
                const id = btn.dataset.id;
                await toggleFavorite(category, id, btn);

                if (!btn.classList.contains('active')) {
                    btn.closest('.card-tours, .card-entertainments').remove();

                    const remaining = grid.children.length;
                    if (countEl) countEl.textContent = remaining;
                    if (remaining === 0) {
                        grid.innerHTML = renderEmptyState(config.emptyTitle, config.emptyText);
                    }
                }
            });
        });

    } catch (error) {
        console.error(`Не удалось загрузить избранное (${category}):`, error);
        grid.innerHTML = `<p class="status-message">Не удалось загрузить данные</p>`;
    }
}

function renderEmptyState(title, text) {
    return `
        <div class="empty-favorites">
            <div class="empty-heart">♡</div>
            <h2>${title}</h2>
            <p>${text}</p>
        </div>
    `;
}

// function renderTourCard(hotel) {
//     return `
//         <div class="card-tours">
//             <div class="card-tours_image" style="background-image:url('${hotel.imageUrl ?? ''}')"></div>
//             <button class="favorite-btn" data-id="${hotel.hotelbedsCode}">
//                 <img src="images/heart-after.png" alt="Удалить из избранного">
//             </button>
//             <div class="card-tours_info">
//                 <h3 class="card-tours_title">${escapeHtml(hotel.name)}</h3>
//                 <div class="card-tours_rating">${'<img src="images/star.png" alt="★">'.repeat(hotel.rating)}</div>
//                 <div class="card-tours_location">${escapeHtml(hotel.location)}</div>
//             </div>
//         </div>
//     `;
// }

function renderAttractionCard(a) {
    return `
        <div class="card-entertainments">
            <div class="card-entertainments__image" style="background-image:url('${a.imageUrl ?? ''}')"></div>
            <button class="favorite-btn" data-id="${a.id}">
                <img src="images/heart-after.png" alt="Удалить из избранного">
            </button>
            <div class="card-content">
                <div class="card-category">${escapeHtml(a.attractionType)}</div>
                <h3 class="card-title">${escapeHtml(a.name)}</h3>
                <div class="card-location">${escapeHtml(a.location)}</div>
                <p class="card-description">${escapeHtml(a.description)}</p>
            </div>
        </div>
    `;
}

function renderRestaurantCard(r) {
    return `
        <div class="card-entertainments">
            <div class="card-entertainments__image" style="background-image:url('${r.imageUrl ?? ''}')"></div>
            <button class="favorite-btn" data-id="${r.id}">
                <img src="images/heart-after.png" alt="Удалить из избранного">
            </button>
            <div class="card-content">
                <div class="card-category">${escapeHtml(r.foodType)}</div>
                <h3 class="card-title">${escapeHtml(r.name)}</h3>
                <div class="card-location">${escapeHtml(r.location)}</div>
                <div class="card-row">
                    <div class="card-rating"><span class="rating-star">★</span>${r.rating}</div>
                    <div class="card-price">${r.averageBill}$</div>
                </div>
            </div>
        </div>
    `;
}

function escapeHtml(value) {
    const div = document.createElement('div');
    div.textContent = value ?? '';
    return div.innerHTML;
}

function setupTabs() {
    const tabs = document.querySelectorAll('.favorites-tab');
    const sections = document.querySelectorAll('.favorites-content');

    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            tabs.forEach(t => t.classList.remove('active'));
            sections.forEach(s => s.classList.remove('active'));
            tab.classList.add('active');
            document.getElementById(tab.dataset.category).classList.add('active');
        });
    });
}