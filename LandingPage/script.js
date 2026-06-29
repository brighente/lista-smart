// Logica para alternar entre Modo Claro e Escuro
const themeToggleBtn = document.getElementById("themeToggle");

themeToggleBtn.addEventListener("click", () => {
  const currentTheme = document.documentElement.getAttribute("data-theme");

  if (currentTheme === "dark") {
    document.documentElement.removeAttribute("data-theme");
    themeToggleBtn.textContent = "Alternar Modo Escuro";
  } else {
    document.documentElement.setAttribute("data-theme", "dark");
    themeToggleBtn.textContent = "Alternar Modo Claro";
  }
});

// formatar campo de CPF
document.getElementById("cpf").addEventListener("input", function (e) {
  let value = e.target.value;

  /* Remove qualquer caractere que nao seja numero */
  value = value.replace(/\D/g, "");

  /* Adiciona o primeiro ponto apos os 3 primeiros numeros */
  value = value.replace(/(\d{3})(\d)/, "$1.$2");

  /* Adiciona o segundo ponto apos os proximos 3 numeros */
  value = value.replace(/(\d{3})(\d)/, "$1.$2");

  /* Adiciona o traço antes dos ultimos 2 numeros */
  value = value.replace(/(\d{3})(\d{1,2})$/, "$1-$2");

  e.target.value = value;
});

/* Sugerir dominios no email com dropdown customizado */
const emailInput = document.getElementById("email");
const suggestionList = document.getElementById("email-suggestions-list");
const domains = [
  "gmail.com",
  "outlook.com",
  "hotmail.com",
  "yahoo.com",
  "icloud.com",
  "uol.com.br",
  "zoho.com",
  "mail.com",
  "proton.me",
  "bol.com.br",
];

emailInput.addEventListener("input", function (e) {
  const value = e.target.value;
  const atPosition = value.indexOf("@");

  /* Oculta e limpa a lista a cada nova digitacao */
  suggestionList.style.display = "none";
  suggestionList.innerHTML = "";

  if (atPosition > -1) {
    const userPart = value.slice(0, atPosition + 1);
    const domainPart = value.slice(atPosition + 1);

    const filtered = domains.filter((domain) => domain.startsWith(domainPart));

    /* Condicao solicitada: nao sugerir se o dominio ja estiver completamente digitado */
    if (filtered.length === 1 && filtered[0] === domainPart) {
      return;
    }

    /* Se houver sugestoes compativeis, constroi a lista na tela */
    if (filtered.length > 0) {
      filtered.forEach((domain) => {
        const li = document.createElement("li");
        /* Mostra APENAS o dominio na tela */
        li.textContent = domain;

        /* Logica de clique (Append) */
        li.addEventListener("click", function () {
          /* Junta o nome do usuario com o dominio clicado */
          emailInput.value = userPart + domain;
          /* Esconde a lista apos o clique */
          suggestionList.style.display = "none";
          /* Devolve o foco para o input */
          emailInput.focus();
        });

        suggestionList.appendChild(li);
      });

      /* Torna a lista visivel */
      suggestionList.style.display = "block";
    }
  }
});

/* Oculta a lista se o usuario clicar fora dela e fora do input */
document.addEventListener("click", function (e) {
  if (e.target !== emailInput && e.target !== suggestionList) {
    suggestionList.style.display = "none";
  }
});

// Interceptacao do formulario para evitar recarregamento
document
  .getElementById("registroForm")
  .addEventListener("submit", function (e) {
    e.preventDefault();
    alert(
      "Simulacao de criacao de conta! Aqui o usuario seria redirecionado para o painel do sistema.",
    );
    // window.location.href = "https://listasmart.com";
  });

const linksSuaves = document.querySelectorAll(".scroll-suave");

linksSuaves.forEach((link) => {
  link.addEventListener("click", function (event) {
    event.preventDefault();

    const destinoId = this.getAttribute("href");

    if (destinoId === "#") {
      window.scrollTo({ top: 0, behavior: "smooth" });
      return ; 
    }

    const elementoDestino = document.querySelector(destinoId);

    if (elementoDestino) {
      elementoDestino.scrollIntoView({
        behavior: "smooth",
        block: "start",
      });
    } else {
      console.warn(`Atenção: O destino ${destinoId} não foi encontrado. Redirecionando para o topo.`);
      window.scrollTo({ top: 0, behavior: "smooth" });
    }
  });
});