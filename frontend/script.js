const tabela = document.querySelector("#tabelaIncidentes tbody");
const buscarBtn = document.getElementById("buscarBtn");
const limparBtn = document.getElementById("limparBtn");
const addBtn = document.getElementById("addBtn");
const exportBtn = document.getElementById("exportBtn");
const showDashboard = document.getElementById("showDashboard");
const dashboardModal = document.getElementById("dashboardModal");
const closeDashboard = document.getElementById("closeDashboard");

let chartStatus = null;
let chartOperadora = null;

async function carregarIncidentes(filtros = {}) {
  const params = new URLSearchParams(filtros);
  const url =
    Object.keys(filtros).length > 0
      ? `/api/incidents/search?${params.toString()}`
      : "/api/incidents";

  const resp = await fetch(url);
  const dados = await resp.json();
  renderTabela(dados);
}

function renderTabela(lista) {
  tabela.innerHTML = "";
  if (!lista || lista.length === 0) {
    tabela.innerHTML =
      '<tr><td colspan="13" class="text-center p-4 text-gray-500">Nenhum registro encontrado.</td></tr>';
    return;
  }

  for (const i of lista) {
    const row = document.createElement("tr");
    row.innerHTML = `
      <td class="border p-2">${i.baGt || ""}</td>
      <td class="border p-2">${i.dataAbertura || ""}</td>
      <td class="border p-2">${i.dddOrigem || ""}</td>
      <td class="border p-2">${i.origem || ""}</td>
      <td class="border p-2">${i.dddDestino || ""}</td>
      <td class="border p-2">${i.destino || ""}</td>
      <td class="border p-2">${i.descricaoFalha || ""}</td>
      <td class="border p-2">${i.operadora || ""}</td>
      <td class="border p-2">${i.baOperadora || ""}</td>
      <td class="border p-2">${i.status || ""}</td>
      <td class="border p-2">${i.dataEncerramento || ""}</td>
      <td class="border p-2">${i.cliente || ""}</td>
      <td class="border p-2">
        <button class="editBtn bg-yellow-400 px-2 py-1 rounded" data-id="${i.id}">Editar</button>
        <button class="delBtn bg-red-500 text-white px-2 py-1 rounded" data-id="${i.id}">Excluir</button>
      </td>
    `;
    tabela.appendChild(row);
  }

  document.querySelectorAll(".delBtn").forEach(b => {
    b.addEventListener("click", async (e) => {
      const id = e.target.dataset.id;
      if (!confirm("Confirma exclusão?")) return;
      await fetch(`/api/incidents/${id}`, { method: "DELETE" });
      carregarIncidentes(currentFilters);
    });
  });

  document.querySelectorAll(".editBtn").forEach(b => {
    b.addEventListener("click", async (e) => {
      const id = e.target.dataset.id;
      const resp = await fetch(`/api/incidents/${id}`);
      const data = await resp.json();
      // preenche campos para edição simples (prompt)
      const novoStatus = prompt("Status:", data.status || "");
      const dataEnc = prompt("Data de encerramento (YYYY-MM-DD):", data.dataEncerramento || "");
      if (novoStatus === null) return;
      data.status = novoStatus;
      data.dataEncerramento = dataEnc || null;
      await fetch(`/api/incidents/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
      });
      carregarIncidentes(currentFilters);
    });
  });
}

let currentFilters = {};

buscarBtn.addEventListener("click", () => {
  const filtros = {
    status: document.getElementById("status").value || null,
    cliente: document.getElementById("cliente").value || null,
    operadora: document.getElementById("operadora").value || null,
    baGt: document.getElementById("baGt").value || null,
    dataAberturaInicio:
      document.getElementById("dataAberturaInicio").value || null,
    dataAberturaFim: document.getElementById("dataAberturaFim").value || null,
  };

  Object.keys(filtros).forEach((k) => filtros[k] == null && delete filtros[k]);
  currentFilters = filtros;
  carregarIncidentes(filtros);
});

limparBtn.addEventListener("click", () => {
  document.querySelectorAll("input").forEach((i) => (i.value = ""));
  currentFilters = {};
  carregarIncidentes();
});

addBtn.addEventListener("click", async () => {
  const novo = {
    baGt: document.getElementById("newBaGt").value,
    cliente: document.getElementById("newCliente").value,
    operadora: document.getElementById("newOperadora").value,
    dddOrigem: document.getElementById("newDddOrigem").value,
    origem: document.getElementById("newOrigem").value,
    dddDestino: document.getElementById("newDddDestino").value,
    destino: document.getElementById("newDestino").value,
    baOperadora: document.getElementById("newBaOperadora").value,
    status: document.getElementById("newStatus").value || "Aberto",
    descricaoFalha: document.getElementById("newDescricao").value,
    dataAbertura: new Date().toISOString().split("T")[0],
  };

  await fetch("/api/incidents", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(novo),
  });

  carregarIncidentes(currentFilters);
});

exportBtn.addEventListener("click", () => {
  const params = new URLSearchParams({
    status: document.getElementById("status").value || "",
    cliente: document.getElementById("cliente").value || "",
    operadora: document.getElementById("operadora").value || ""
  });
  window.open(`/api/incidents/export/csv?${params.toString()}`, "_blank");
});

showDashboard.addEventListener("click", async () => {
  dashboardModal.classList.remove("hidden");
  await loadCharts();
});

closeDashboard.addEventListener("click", () => {
  dashboardModal.classList.add("hidden");
});

async function loadCharts() {
  const sResp = await fetch('/api/incidents/stats/by-status');
  const oResp = await fetch('/api/incidents/stats/by-operadora');
  const sData = await sResp.json();
  const oData = await oResp.json();

  const ctxS = document.getElementById('chartStatus').getContext('2d');
  const ctxO = document.getElementById('chartOperadora').getContext('2d');

  if (chartStatus) chartStatus.destroy();
  if (chartOperadora) chartOperadora.destroy();

  chartStatus = new Chart(ctxS, {
    type: 'pie',
    data: {
      labels: Object.keys(sData),
      datasets: [{ data: Object.values(sData) }]
    }
  });

  chartOperadora = new Chart(ctxO, {
    type: 'bar',
    data: {
      labels: Object.keys(oData),
      datasets: [{ label: 'Ocorrências', data: Object.values(oData) }]
    },
    options: { responsive: true, maintainAspectRatio: false }
  });
}

carregarIncidentes();
