# Clínica Médica — CRUD de consultas

Atividade de **Programação Avançada para Web**: adicionar o CRUD da entidade `consulta (id, data, turno, medico, paciente)` ao projeto do professor Douglas Meneses.

**Aluno:** Walter Yanko de Aragão Brandão.

**Projeto de origem:** [douglasmeneses/clinicaMedica](https://github.com/douglasmeneses/clinicaMedica), commit `0820904ec400de820656aada3eab209f1213c7e9`. A base de pacientes e médicos foi preservada; esta entrega acrescenta consultas e os arquivos necessários para executar e verificar a atividade.

## O que foi implementado

- CRUD completo de consultas: cadastrar, listar, buscar por ID, atualizar e excluir.
- Relacionamentos com médico e paciente existentes por `medicoId` e `pacienteId`.
- Respostas com os objetos `medico` e `paciente`, além das chaves estrangeiras.
- Validação dos dados recebidos, conversão da data e respostas HTTP para entradas inválidas e registros inexistentes.
- Migrações versionadas, dados fictícios de demonstração e testes de integração.

A arquitetura segue o projeto original: **Routes → Controller → Service → Repository → Prisma → PostgreSQL**.

## Executar

Requisitos: **Node.js 22.12 ou superior**, npm e Docker Compose para o banco PostgreSQL. Também é possível usar uma instalação própria do PostgreSQL, ajustando `DATABASE_URL`.

```bash
git clone --branch clinica-medica-consultas --single-branch https://github.com/walteryanko/apsii.git clinicaMedica
cd clinicaMedica
cp .env.example .env
npm ci
docker compose up -d
npm run db:setup
npm run dev
```

No Prompt de Comando do Windows, substitua `cp .env.example .env` por `copy .env.example .env`. No PowerShell, `cp` também funciona.

A API fica disponível em `http://localhost:3000`. Aguarde o PostgreSQL ficar pronto antes de executar `npm run db:setup`.

O comando `db:setup` gera o Prisma Client, aplica as migrações e cadastra um médico e um paciente fictícios. Seus IDs aparecem no terminal; em um banco novo, ambos serão `1`. Os dados de demonstração podem ser criados novamente sem duplicar os registros.

Para executar a versão compilada:

```bash
npm run build
npm start
```

### Banco já existente do projeto original

As migrações incluídas também criam as tabelas originais de pacientes e médicos. Se o seu banco já tem essas tabelas criadas com `db push` e ainda não possui histórico de migrações, registre a migração inicial como aplicada antes de instalar consultas:

```bash
npx prisma migrate resolve --applied 20261007000100_init
npm run db:setup
```

Use esse procedimento apenas quando as tabelas de pacientes e médicos já correspondem ao schema original. Não é necessário para um banco novo. Nenhum comando de reset é necessário.

## Endpoints de consultas

| Método | Endpoint | Operação | Sucesso |
|---|---|---|---|
| POST | `/consultas` | Cadastrar | 201 |
| GET | `/consultas` | Listar | 200 |
| GET | `/consultas/:id` | Buscar por ID | 200 |
| PUT | `/consultas/:id` | Atualizar todos os campos editáveis | 200 |
| DELETE | `/consultas/:id` | Excluir | 204, sem corpo |

Os CRUDs originais de `/medicos` e `/pacientes` continuam disponíveis.

### Exemplo de cadastro

Envie um `POST http://localhost:3000/consultas` com `Content-Type: application/json`:

```json
{
  "data": "2026-10-09T14:00:00.000Z",
  "turno": "TARDE",
  "medicoId": 1,
  "pacienteId": 1
}
```

O ID da consulta é gerado automaticamente. O médico e o paciente precisam existir no banco. `medicoId` e `pacienteId` são as chaves estrangeiras; `medico` e `paciente` são os objetos relacionados devolvidos pela API.

A data aceita `AAAA-MM-DD` ou uma data/hora ISO 8601 com fuso, por exemplo `2026-10-09T11:00:00-03:00`. Datas sem horário são armazenadas à meia-noite UTC. A resposta serializa a data em UTC; `14:00Z` corresponde a `11:00` em UTC−3.

Os turnos são `MANHA`, `TARDE` e `NOITE`. A entrada `manhã` também é aceita e normalizada para `MANHA`.

Para atualizar, envie o mesmo conjunto de quatro campos ao endpoint `PUT /consultas/:id`. O ID da consulta é obtido da URL.

### Respostas de erro

| Código | Situação |
|---|---|
| 400 | ID, data, turno, campos obrigatórios ou JSON inválidos |
| 404 | Consulta, médico ou paciente informado não encontrado |
| 409 | Tentativa de excluir médico ou paciente vinculado a consulta; CPF/CRM duplicado |
| 500 | Falha interna inesperada |

Excluir uma consulta mantém seu médico e paciente. Excluir médico ou paciente com consultas vinculadas é impedido pelas chaves estrangeiras.

## Arquivos da atividade

| Arquivo | Responsabilidade |
|---|---|
| `prisma/schema.prisma` | Modelagem de Consulta e seus relacionamentos |
| `prisma/migrations/` | Criação das tabelas, índices e chaves estrangeiras |
| `src/types/consulta.ts` | DTO de consulta |
| `src/schemas/consultaSchema.ts` | Validação de JSON e ID |
| `src/repositories/consultaRepository.ts` | Operações de persistência com Prisma |
| `src/services/consultaService.ts` | Regras e verificação de médico/paciente |
| `src/controllers/consultaController.ts` | Requisições e respostas HTTP |
| `src/routes/consultaRoutes.ts` | Registro dos cinco endpoints |
| `src/app.ts` e `src/server.ts` | Configuração e inicialização da API |
| `src/middlewares/errorHandler.ts` | Respostas de erro consistentes |
| `requests/consultas.http` | Requisições para teste manual |
| `tests/consultas.test.ts` | Testes de integração com HTTP e banco |

## Verificar

Com `DATABASE_URL` apontando para um PostgreSQL preparado pelas migrações:

```bash
npx prisma validate
npm run build
npm test
```

Os testes usam uma porta HTTP temporária, criam seus próprios médicos/pacientes e removem somente os registros criados na execução. Verificam os cinco endpoints, a persistência, os relacionamentos, a atualização, a exclusão e os principais casos de erro.

O workflow em `.github/workflows/ci.yml` executa a validação, migrações, compilação e testes com PostgreSQL 16.

## Créditos

Base original de **Douglas Meneses**. Implementação da atividade na cópia de **Walter Yanko de Aragão Brandão**, com assistência de IA.
