# Entrega — CRUD de Consulta

**Aluno:** Walter Yanko de Aragão Brandão.

**Disciplina:** Programação Avançada para Web.

**Enunciado:** “Adicione o CRUD da entidade consulta (id, data, turno, medico, paciente)”.

## Implementação

A entidade Consulta possui `id` inteiro autoincrementado, `data` do tipo DateTime e `turno`. Cada consulta está ligada a um Médico e a um Paciente, por meio das chaves estrangeiras `medicoId` e `pacienteId`. A API inclui os objetos `medico` e `paciente` nas respostas.

Foram adicionados DTO, schema de validação, repository, service, controller e routes. O registro de todas as rotas fica em `src/app.ts`; `src/server.ts` inicia o servidor. Essa separação permite testar a aplicação por HTTP sem iniciar uma segunda instância na porta 3000.

| Operação | Endpoint | Sucesso |
|---|---|---|
| Cadastrar | `POST /consultas` | 201 |
| Listar | `GET /consultas` | 200 |
| Buscar por ID | `GET /consultas/:id` | 200 |
| Atualizar | `PUT /consultas/:id` | 200 |
| Excluir | `DELETE /consultas/:id` | 204 |

A entrada é validada com Zod. O serviço verifica a existência do médico e do paciente. As chaves estrangeiras protegem os vínculos no banco. Dados inválidos retornam 400, registros inexistentes retornam 404 e exclusões de médicos/pacientes vinculados retornam 409.

## Verificação executada em 07/10/2026

- Instalação das dependências por lockfile e geração do Prisma Client 7.10.0.
- Aplicação das duas migrações em um banco vazio.
- Execução do seed com médico e paciente fictícios.
- `npm test`: **22 testes passaram, 0 falhas**.
- `npm run build`: compilação TypeScript concluída.
- `npx prisma validate`: schema válido.

Os testes executados usam requisições HTTP reais, Express, Prisma e uma instância local PGlite com motor PostgreSQL 18.3. Verificam persistência, alteração dos relacionamentos, remoção, proteção das chaves estrangeiras, datas e IDs inválidos, JSON malformado e operações com registros inexistentes. O workflow de CI está configurado para executar os mesmos testes com PostgreSQL 16.

## Execução e teste manual

O README contém as instruções de instalação, configuração e inicialização. `requests/consultas.http` reúne exemplos dos cinco endpoints e casos de erro.

## Origem

Base original: `douglasmeneses/clinicaMedica`, commit `0820904ec400de820656aada3eab209f1213c7e9`. Implementação desta atividade com assistência de IA. O relatório original do professor foi mantido como referência histórica e identificado dessa forma.
