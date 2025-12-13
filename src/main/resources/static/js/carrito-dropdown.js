(function () {
  if (window.__kiwishaCartDropdownInitialized) return;
  window.__kiwishaCartDropdownInitialized = true;

  const API_BASE = '/api/public/carrito';
  const DEFAULT_IMG = '/files/defaults/producto-default.svg';

  function qsAll(selector, root) {
    return Array.from((root || document).querySelectorAll(selector));
  }

  function toFileUrl(ruta) {
    if (!ruta) return DEFAULT_IMG;
    if (ruta.startsWith('http://') || ruta.startsWith('https://')) return ruta;
    if (ruta.startsWith('/files/')) return ruta;
    return '/files/' + ruta.replace(/^\/+/, '');
  }

  function formatMoney(value) {
    try {
      const num = typeof value === 'number' ? value : Number(value);
      if (Number.isNaN(num)) return 'S/ 0.00';
      return 'S/ ' + num.toFixed(2);
    } catch {
      return 'S/ 0.00';
    }
  }

  async function apiGetCart() {
    const res = await fetch(API_BASE, { headers: { 'Accept': 'application/json' } });
    const json = await res.json();
    if (!json || json.success !== true) {
      throw new Error((json && json.message) || 'No se pudo obtener el carrito');
    }
    return json.data;
  }

  async function apiAdd(productoId, cantidad) {
    const res = await fetch(API_BASE + '/agregar', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
      body: JSON.stringify({ productoId, cantidad })
    });
    const json = await res.json();
    if (!json || json.success !== true) {
      throw new Error((json && json.message) || 'No se pudo agregar al carrito');
    }
    return json.data;
  }

  async function apiUpdateQty(productoId, cantidad) {
    const res = await fetch(API_BASE + '/items/' + encodeURIComponent(productoId) + '?cantidad=' + encodeURIComponent(cantidad), {
      method: 'PUT',
      headers: { 'Accept': 'application/json' }
    });
    const json = await res.json();
    if (!json || json.success !== true) {
      throw new Error((json && json.message) || 'No se pudo actualizar la cantidad');
    }
    return json.data;
  }

  async function apiRemove(productoId) {
    const res = await fetch(API_BASE + '/items/' + encodeURIComponent(productoId), {
      method: 'DELETE',
      headers: { 'Accept': 'application/json' }
    });
    const json = await res.json();
    if (!json || json.success !== true) {
      throw new Error((json && json.message) || 'No se pudo eliminar del carrito');
    }
    return json.data;
  }

  function setBadgeCount(count) {
    qsAll('[data-cart-count]').forEach((el) => {
      const n = Number(count || 0);
      if (n > 0) {
        el.textContent = String(n);
        el.classList.remove('hidden');
      } else {
        el.textContent = '0';
        el.classList.add('hidden');
      }
    });
  }

  function pulseBadge() {
    qsAll('[data-cart-count]').forEach((el) => {
      el.classList.remove('animate-pulse');
      // reflow
      void el.offsetWidth;
      el.classList.add('animate-pulse');
      setTimeout(() => el.classList.remove('animate-pulse'), 800);
    });
  }

  function closeAllDropdowns(except) {
    qsAll('[data-cart-dropdown]').forEach((dd) => {
      if (except && dd === except) return;
      dd.classList.add('hidden');
    });
  }

  function renderDropdown(dropdownEl, carrito) {
    const itemsEl = dropdownEl.querySelector('[data-cart-dropdown-items]');
    const emptyEl = dropdownEl.querySelector('[data-cart-dropdown-empty]');
    if (!itemsEl) return;

    itemsEl.innerHTML = '';

    const items = (carrito && carrito.items) ? carrito.items : [];
    if (!items.length) {
      if (emptyEl) emptyEl.classList.remove('hidden');
      return;
    }

    if (emptyEl) emptyEl.classList.add('hidden');

    items.forEach((item) => {
      const img = toFileUrl(item.productoImagen);
      const titulo = item.productoTitulo || 'Producto';
      const precio = formatMoney(item.precio);
      const maxStock = Number(item.stockDisponible || 0);
      const cantidad = Number(item.cantidad || 1);

      const row = document.createElement('div');
      row.className = 'flex gap-3 items-start border-b border-solid border-[#f4ede6] pb-3';
      row.innerHTML = `
        <img class="h-12 w-12 rounded-lg object-cover bg-[#f4ede6]" src="${img}" alt="${titulo.replace(/\"/g, '')}" onerror="this.onerror=null; this.src='${DEFAULT_IMG}';" />
        <div class="flex-1 min-w-0">
          <div class="flex items-start justify-between gap-2">
            <p class="text-[#1c140d] text-sm font-medium leading-normal truncate">${titulo}</p>
            <button type="button" data-cart-remove="${item.productoId}" class="text-[#9e7347] text-sm font-medium leading-normal hover:underline">Eliminar</button>
          </div>
          <p class="text-[#9e7347] text-sm font-normal leading-normal">${precio}</p>
          <div class="flex items-center gap-2 pt-2">
            <span class="text-[#9e7347] text-xs">Cantidad</span>
            <input data-cart-qty="${item.productoId}" type="number" min="1" max="${maxStock || 1}" value="${cantidad}"
                   class="form-input h-9 w-20 rounded-lg border-none bg-[#f4ede6] text-[#1c140d] text-sm focus:outline-0 focus:ring-0" />
            <span class="text-[#9e7347] text-xs">/ ${maxStock || 0}</span>
          </div>
        </div>
      `;
      itemsEl.appendChild(row);
    });
  }

  async function refreshCart(dropdownEl) {
    const carrito = await apiGetCart();
    setBadgeCount(carrito && carrito.cantidadItems);
    if (dropdownEl) renderDropdown(dropdownEl, carrito);
    return carrito;
  }

  function wireDropdown(dropdownEl) {
    dropdownEl.addEventListener('click', async (e) => {
      const removeBtn = e.target.closest('[data-cart-remove]');
      if (removeBtn) {
        const productoId = Number(removeBtn.getAttribute('data-cart-remove'));
        try {
          removeBtn.setAttribute('disabled', 'disabled');
          const carrito = await apiRemove(productoId);
          setBadgeCount(carrito && carrito.cantidadItems);
          renderDropdown(dropdownEl, carrito);
        } catch (err) {
          alert(err.message || 'Error al eliminar del carrito');
        } finally {
          removeBtn.removeAttribute('disabled');
        }
      }
    });

    dropdownEl.addEventListener('change', async (e) => {
      const qtyInput = e.target.closest('[data-cart-qty]');
      if (!qtyInput) return;

      const productoId = Number(qtyInput.getAttribute('data-cart-qty'));
      const cantidad = Math.max(1, Number(qtyInput.value || 1));

      try {
        qtyInput.setAttribute('disabled', 'disabled');
        const carrito = await apiUpdateQty(productoId, cantidad);
        setBadgeCount(carrito && carrito.cantidadItems);
        renderDropdown(dropdownEl, carrito);
      } catch (err) {
        alert(err.message || 'Error al actualizar cantidad');
        try {
          await refreshCart(dropdownEl);
        } catch {
          // ignore
        }
      } finally {
        qtyInput.removeAttribute('disabled');
      }
    });
  }

  function initCartUI() {
    // Wire toggles
    qsAll('[data-cart-toggle]').forEach((toggle) => {
      const wrapper = toggle.closest('[data-cart-wrapper]') || toggle.parentElement;
      const dropdown = wrapper ? wrapper.querySelector('[data-cart-dropdown]') : null;
      if (dropdown) wireDropdown(dropdown);

      toggle.addEventListener('click', async (e) => {
        e.preventDefault();
        e.stopPropagation();

        if (!dropdown) return;

        const willOpen = dropdown.classList.contains('hidden');
        closeAllDropdowns(willOpen ? dropdown : null);
        dropdown.classList.toggle('hidden');

        if (willOpen) {
          try {
            await refreshCart(dropdown);
          } catch (err) {
            // Si falla, al menos no rompemos el UI
            console.error(err);
          }
        }
      });
    });

    // Close on outside click
    document.addEventListener('click', (e) => {
      const insideCart = e.target.closest('[data-cart-wrapper]');
      if (!insideCart) closeAllDropdowns();
    });

    // Add-to-cart buttons
    qsAll('[data-add-to-cart]').forEach((btn) => {
      btn.addEventListener('click', async (e) => {
        e.preventDefault();
        const productoId = Number(btn.getAttribute('data-producto-id'));
        if (!productoId) return;

        try {
          btn.setAttribute('disabled', 'disabled');
          const carrito = await apiAdd(productoId, 1);
          setBadgeCount(carrito && carrito.cantidadItems);
          pulseBadge();
        } catch (err) {
          alert(err.message || 'No se pudo agregar al carrito');
        } finally {
          btn.removeAttribute('disabled');
        }
      });
    });

    // Initial count
    refreshCart().catch(() => {});
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initCartUI);
  } else {
    initCartUI();
  }
})();
