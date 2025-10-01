const API_BASE_URL = "http://localhost:8080/api/cachorros";
let currentSearchType = "nome";

// Inicialização
document.addEventListener("DOMContentLoaded", function () {
  carregarCachorros();
  setupEventListeners();
});

// Configuração de event listeners
function setupEventListeners() {
  // Navegação
  document.querySelectorAll(".nav-btn").forEach((btn) => {
    btn.addEventListener("click", () => {
      const section = btn.dataset.section;
      mostrarSecao(section);
    });
  });

  // Tabs de busca
  document.querySelectorAll(".search-tab").forEach((tab) => {
    tab.addEventListener("click", () => {
      currentSearchType = tab.dataset.type;
      document
        .querySelectorAll(".search-tab")
        .forEach((t) => t.classList.remove("active"));
      tab.classList.add("active");
      document.getElementById(
        "search-input"
      ).placeholder = `Digite o ${currentSearchType} para buscar...`;
    });
  });

  // Formulários
  document
    .getElementById("form-cachorro")
    .addEventListener("submit", criarCachorro);
  document
    .getElementById("form-editar")
    .addEventListener("submit", atualizarCachorro);

  // Modal
  document.querySelector(".close").addEventListener("click", fecharModal);
  window.addEventListener("click", (e) => {
    if (e.target === document.getElementById("modal-editar")) {
      fecharModal();
    }
  });
}

// Navegação entre seções
function mostrarSecao(sectionId) {
  document.querySelectorAll(".section").forEach((section) => {
    section.classList.remove("active");
  });
  document.querySelectorAll(".nav-btn").forEach((btn) => {
    btn.classList.remove("active");
  });

  document.getElementById(sectionId).classList.add("active");
  document
    .querySelector(`[data-section="${sectionId}"]`)
    .classList.add("active");
}

// Toast notifications
function mostrarToast(mensagem, tipo = "success") {
  const toast = document.getElementById("toast");
  toast.textContent = mensagem;
  toast.className = `toast ${tipo} show`;

  setTimeout(() => {
    toast.classList.remove("show");
  }, 3000);
}

// Carregar todos os cachorros
async function carregarCachorros() {
  const container = document.getElementById("lista-cachorros");
  container.innerHTML =
    '<div class="loading"><i class="fas fa-spinner fa-spin"></i><p>Carregando cachorros...</p></div>';

  try {
    const response = await fetch(API_BASE_URL);
    if (!response.ok) throw new Error("Erro ao carregar cachorros");

    const cachorros = await response.json();
    exibirCachorros(cachorros, container);
  } catch (error) {
    container.innerHTML =
      '<div class="error">Erro ao carregar cachorros: ' +
      error.message +
      "</div>";
    mostrarToast("Erro ao carregar cachorros", "error");
  }
}

// Exibir cachorros em cards
function exibirCachorros(cachorros, container) {
  if (cachorros.length === 0) {
    container.innerHTML =
      '<div class="loading"><i class="fas fa-dog"></i><p>Nenhum cachorro encontrado.</p></div>';
    return;
  }

  container.innerHTML = cachorros
    .map(
      (cachorro) => `
        <div class="card" data-id="${cachorro.id}">
            <img src="${
              cachorro.imagem ||
              "https://images.dog.ceo/breeds/mixed/n02107312_100.jpg"
            }" 
                 alt="${cachorro.nome}" 
                 class="card-img"
                 onerror="this.src='https://images.dog.ceo/breeds/mixed/n02107312_100.jpg'">
            <div class="card-content">
                <h3 class="card-title">${cachorro.nome}</h3>
                <div class="card-info">
                    <i class="fas fa-user"></i>
                    <strong>Dono:</strong> ${cachorro.nomeDono}
                </div>
                <div class="card-info">
                    <i class="fas fa-paw"></i>
                    <strong>Raça:</strong> ${cachorro.raca}
                </div>
                <div class="card-actions">
                    <button class="btn btn-warning" onclick="editarCachorro(${
                      cachorro.id
                    })">
                        <i class="fas fa-edit"></i> Editar
                    </button>
                    <button class="btn btn-danger" onclick="deletarCachorro(${
                      cachorro.id
                    })">
                        <i class="fas fa-trash"></i> Deletar
                    </button>
                </div>
            </div>
        </div>
    `
    )
    .join("");
}

// Criar novo cachorro
async function criarCachorro(event) {
  event.preventDefault();

  const formData = new FormData(event.target);
  const cachorro = {
    nome: formData.get("nome"),
    nomeDono: formData.get("nomeDono"),
    raca: formData.get("raca") || "",
    imagem: formData.get("imagem") || "",
  };

  try {
    const response = await fetch(API_BASE_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(cachorro),
    });

    if (!response.ok) throw new Error("Erro ao criar cachorro");

    const novoCachorro = await response.json();
    mostrarToast(
      `Cachorro ${novoCachorro.nome} criado com sucesso!`,
      "success"
    );
    limparFormulario();
    carregarCachorros();
    mostrarSecao("listar");
  } catch (error) {
    mostrarToast("Erro ao criar cachorro: " + error.message, "error");
  }
}

// Buscar cachorros
async function buscarCachorros() {
  const termo = document.getElementById("search-input").value.trim();
  const container = document.getElementById("resultados-busca");

  if (!termo) {
    mostrarToast("Digite um termo para buscar", "warning");
    return;
  }

  container.innerHTML =
    '<div class="loading"><i class="fas fa-spinner fa-spin"></i><p>Buscando...</p></div>';

  try {
    const url = `${API_BASE_URL}/search/${currentSearchType}?${currentSearchType}=${encodeURIComponent(
      termo
    )}`;
    const response = await fetch(url);

    if (!response.ok) throw new Error("Erro na busca");

    const resultados = await response.json();
    exibirCachorros(resultados, container);

    if (resultados.length === 0) {
      mostrarToast("Nenhum resultado encontrado", "warning");
    } else {
      mostrarToast(`Encontrados ${resultados.length} resultado(s)`, "success");
    }
  } catch (error) {
    container.innerHTML =
      '<div class="error">Erro na busca: ' + error.message + "</div>";
    mostrarToast("Erro na busca", "error");
  }
}

// Editar cachorro - Abrir modal
async function editarCachorro(id) {
  try {
    const response = await fetch(`${API_BASE_URL}/${id}`);
    if (!response.ok) throw new Error("Erro ao carregar dados do cachorro");

    const cachorro = await response.json();

    document.getElementById("edit-id").value = cachorro.id;
    document.getElementById("edit-nome").value = cachorro.nome;
    document.getElementById("edit-nomeDono").value = cachorro.nomeDono;
    document.getElementById("edit-raca").value = cachorro.raca;
    document.getElementById("edit-imagem").value = cachorro.imagem || "";

    document.getElementById("modal-editar").style.display = "block";
  } catch (error) {
    mostrarToast("Erro ao carregar dados para edição", "error");
  }
}

// Atualizar cachorro
async function atualizarCachorro(event) {
  event.preventDefault();

  const id = document.getElementById("edit-id").value;
  const formData = new FormData(event.target);
  const cachorro = {
    nome: formData.get("nome"),
    nomeDono: formData.get("nomeDono"),
    raca: formData.get("raca"),
    imagem: formData.get("imagem"),
  };

  try {
    const response = await fetch(`${API_BASE_URL}/${id}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(cachorro),
    });

    if (!response.ok) throw new Error("Erro ao atualizar cachorro");

    mostrarToast(
      `Cachorro ${cachorro.nome} atualizado com sucesso!`,
      "success"
    );
    fecharModal();
    carregarCachorros();
  } catch (error) {
    mostrarToast("Erro ao atualizar cachorro: " + error.message, "error");
  }
}

// Deletar cachorro
async function deletarCachorro(id) {
  if (!confirm("Tem certeza que deseja deletar este cachorro?")) {
    return;
  }

  try {
    const response = await fetch(`${API_BASE_URL}/${id}`, {
      method: "DELETE",
    });

    if (!response.ok) throw new Error("Erro ao deletar cachorro");

    mostrarToast("Cachorro deletado com sucesso!", "success");
    carregarCachorros();
  } catch (error) {
    mostrarToast("Erro ao deletar cachorro: " + error.message, "error");
  }
}

// Fechar modal
function fecharModal() {
  document.getElementById("modal-editar").style.display = "none";
}

// Limpar formulário
function limparFormulario() {
  document.getElementById("form-cachorro").reset();
  mostrarToast("Formulário limpo", "success");
}
