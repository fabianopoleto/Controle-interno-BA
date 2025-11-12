
# Controle-interno-BA

Projeto minimal funcional: backend Spring Boot (API) + frontend estático (HTML/JS).
Feito para deploy grátis no Render (backend + Postgres) e Netlify (frontend).

## Estrutura
- `backend/` - código Java Spring Boot
- `frontend/` - arquivos estáticos (index.html + script.js)
- `Dockerfile`, `docker-compose.yml`, `README.md`

## Como usar localmente (com Docker)
1. Ter Docker instalado.
2. Na raiz do projeto:
```bash
docker-compose up --build
```
3. Abra `http://localhost:8080/` — o frontend estático está em `/frontend`.  
Se quiser servir o frontend local com seu navegador, abra `frontend/index.html` (ajuste chamadas para apontar ao backend).

## Deploy gratuito (Render + Netlify) - resumo

### 1) Subir repositório no GitHub
Crie um repositório no GitHub e faça commit de todos os arquivos (a pasta raíz deste ZIP).

### 2) Backend no Render
1. Crie conta em https://render.com (grátis).
2. New → Web Service → Connect to GitHub → selecione o repositório.
3. Environment: **Docker** (Render usará o Dockerfile).
4. Add a PostgreSQL Database (Managed) - Render oferece opção gratuita.
5. Configure environment variables on the Web Service:
   - `SPRING_DATASOURCE_URL` = `jdbc:postgresql://<HOST>:<PORT>/<DB>`
   - `SPRING_DATASOURCE_USERNAME` = `<USER>`
   - `SPRING_DATASOURCE_PASSWORD` = `<PASSWORD>`
6. Deploy — Render fará build e executará.

### 3) Frontend no Netlify
1. Crie conta em https://netlify.com.
2. New Site → Import from Git → select the repo → Publish directory: `frontend`.
3. Netlify gera uma URL com HTTPS.

### 4) Ajustar CORS / Endpoints
- No frontend `script.js` as chamadas usam caminhos relativos (`/api/...`).  
  Se você hospedar frontend em outro domínio, altere as URLs para apontar ao endereço do backend (ex: `https://seu-backend.onrender.com/api/incidents`).

## Observações finais
- O projeto é um ponto de partida: verifique e ajuste permissões, autenticação e backups antes de produção.
- Se quiser, eu faço o `commit` e `push` dos arquivos diretamente no seu repositório (você pode me dar temporariamente acesso) ou eu te envio o ZIP pronto para você subir.

