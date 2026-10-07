import assert from "node:assert/strict";
import { once } from "node:events";
import { randomUUID } from "node:crypto";
import { after, before, describe, it } from "node:test";
import type { Server } from "node:http";
import type { AddressInfo } from "node:net";
import app from "../src/app.js";
import { prisma } from "../src/config/prisma.js";

describe("CRUD de consultas com HTTP, Prisma e PostgreSQL", { concurrency: false }, () => {
  let servidor: Server;
  let url: string;
  let medicoId: number;
  let outroMedicoId: number;
  let pacienteId: number;
  let outroPacienteId: number;
  let consultaId: number;
  const idsConsultas: number[] = [];
  const idsMedicos: number[] = [];
  const idsPacientes: number[] = [];

  async function requisicao(metodo: string, caminho: string, dados?: unknown) {
    const resposta = await fetch(`${url}${caminho}`, {
      method: metodo,
      headers: { "Content-Type": "application/json" },
      ...(dados === undefined ? {} : { body: JSON.stringify(dados) }),
    });
    const texto = await resposta.text();
    return { status: resposta.status, body: texto ? JSON.parse(texto) : null };
  }

  function dadosValidos() {
    return { data: "2026-10-09T14:00:00.000Z", turno: "TARDE", medicoId, pacienteId };
  }

  before(async () => {
    servidor = app.listen(0, "127.0.0.1");
    await once(servidor, "listening");
    url = `http://127.0.0.1:${(servidor.address() as AddressInfo).port}`;
    const codigo = randomUUID();

    for (const sufixo of ["A", "B"]) {
      const medico = await requisicao("POST", "/medicos", {
        nome: `Médico teste ${sufixo}`, crm: `TEST-${codigo}-${sufixo}`, especialidade: "Clínica geral",
      });
      assert.equal(medico.status, 201);
      idsMedicos.push(medico.body.id);

      const paciente = await requisicao("POST", "/pacientes", {
        nome: `Paciente teste ${sufixo}`, cpf: `TEST-${codigo}-${sufixo}`, telefone: "00000000000",
      });
      assert.equal(paciente.status, 201);
      idsPacientes.push(paciente.body.id);
    }
    [medicoId, outroMedicoId] = idsMedicos as [number, number];
    [pacienteId, outroPacienteId] = idsPacientes as [number, number];
  });

  after(async () => {
    try {
      // Remove apenas registros criados nesta execução de teste.
      await prisma.consulta.deleteMany({ where: { id: { in: idsConsultas } } });
      await prisma.medico.deleteMany({ where: { id: { in: idsMedicos } } });
      await prisma.paciente.deleteMany({ where: { id: { in: idsPacientes } } });
    } finally {
      await prisma.$disconnect();
      if (servidor) await new Promise<void>((resolve, reject) => servidor.close((erro) => erro ? reject(erro) : resolve()));
    }
  });

  it("POST cria consulta com ID automático e os objetos médico e paciente", async () => {
    const resultado = await requisicao("POST", "/consultas", dadosValidos());
    assert.equal(resultado.status, 201);
    consultaId = resultado.body.id;
    idsConsultas.push(consultaId);
    assert.ok(Number.isInteger(consultaId) && consultaId > 0);
    assert.equal(resultado.body.data, "2026-10-09T14:00:00.000Z");
    assert.equal(resultado.body.turno, "TARDE");
    assert.equal(resultado.body.medico.id, medicoId);
    assert.equal(resultado.body.paciente.id, pacienteId);

    const persistida = await prisma.consulta.findUnique({ where: { id: consultaId } });
    assert.equal(persistida?.medicoId, medicoId);
    assert.equal(persistida?.pacienteId, pacienteId);
  });

  it("GET lista a consulta persistida", async () => {
    const resultado = await requisicao("GET", "/consultas");
    assert.equal(resultado.status, 200);
    assert.ok(Array.isArray(resultado.body));
    assert.ok(resultado.body.some((consulta: { id: number }) => consulta.id === consultaId));
  });

  it("GET por ID retorna a consulta com relacionamentos", async () => {
    const resultado = await requisicao("GET", `/consultas/${consultaId}`);
    assert.equal(resultado.status, 200);
    assert.equal(resultado.body.id, consultaId);
    assert.equal(resultado.body.medico.id, medicoId);
    assert.equal(resultado.body.paciente.id, pacienteId);
  });

  it("PUT atualiza data, turno, médico e paciente no banco", async () => {
    const resultado = await requisicao("PUT", `/consultas/${consultaId}`, {
      data: "2026-10-10", turno: "manhã", medicoId: outroMedicoId, pacienteId: outroPacienteId,
    });
    assert.equal(resultado.status, 200);
    assert.equal(resultado.body.id, consultaId);
    assert.equal(resultado.body.data, "2026-10-10T00:00:00.000Z");
    assert.equal(resultado.body.turno, "MANHA");
    assert.equal(resultado.body.medico.id, outroMedicoId);
    assert.equal(resultado.body.paciente.id, outroPacienteId);
    const leitura = await requisicao("GET", `/consultas/${consultaId}`);
    assert.deepEqual(leitura.body, resultado.body);
  });

  for (const id of ["abc", "0", "-1", "1.5", "2147483648"]) {
    it(`GET rejeita ID inválido: ${id}`, async () => {
      assert.equal((await requisicao("GET", `/consultas/${id}`)).status, 400);
    });
  }

  const casosInvalidos = [
    ["data inexistente", { data: "2026-02-30" }],
    ["data nula", { data: null }],
    ["turno inválido", { turno: "MADRUGADA" }],
    ["médico com tipo errado", { medicoId: "1" }],
    ["paciente com ID negativo", { pacienteId: -1 }],
    ["ID de consulta informado pelo cliente", { id: 999 }],
  ] as const;

  for (const [nome, alteracoes] of casosInvalidos) {
    it(`POST retorna 400 para ${nome}`, async () => {
      const resultado = await requisicao("POST", "/consultas", { ...dadosValidos(), ...alteracoes });
      assert.equal(resultado.status, 400);
      assert.equal(resultado.body.mensagem, "Dados inválidos");
    });
  }

  it("POST rejeita corpo ausente e campos obrigatórios ausentes", async () => {
    assert.equal((await requisicao("POST", "/consultas")).status, 400);
    assert.equal((await requisicao("POST", "/consultas", {})).status, 400);
  });

  it("POST rejeita JSON malformado", async () => {
    const resposta = await fetch(`${url}/consultas`, {
      method: "POST", headers: { "Content-Type": "application/json" }, body: "{",
    });
    assert.equal(resposta.status, 400);
  });

  it("POST rejeita referências a médico e paciente inexistentes", async () => {
    const semMedico = await requisicao("POST", "/consultas", { ...dadosValidos(), medicoId: 2147483647 });
    assert.equal(semMedico.status, 404);
    const semPaciente = await requisicao("POST", "/consultas", { ...dadosValidos(), pacienteId: 2147483647 });
    assert.equal(semPaciente.status, 404);
  });

  it("GET, PUT e DELETE retornam 404 para consulta inexistente", async () => {
    assert.equal((await requisicao("GET", "/consultas/2147483647")).status, 404);
    assert.equal((await requisicao("PUT", "/consultas/2147483647", dadosValidos())).status, 404);
    assert.equal((await requisicao("DELETE", "/consultas/2147483647")).status, 404);
  });

  it("PUT inválido não modifica a consulta", async () => {
    const antes = await requisicao("GET", `/consultas/${consultaId}`);
    assert.equal((await requisicao("PUT", `/consultas/${consultaId}`, { ...dadosValidos(), turno: "" })).status, 400);
    assert.equal((await requisicao("PUT", `/consultas/${consultaId}`, { ...dadosValidos(), medicoId: 2147483647 })).status, 404);
    const depois = await requisicao("GET", `/consultas/${consultaId}`);
    assert.deepEqual(depois.body, antes.body);
  });

  it("protege médico e paciente vinculados contra exclusão", async () => {
    assert.equal((await requisicao("DELETE", `/medicos/${outroMedicoId}`)).status, 409);
    assert.equal((await requisicao("DELETE", `/pacientes/${outroPacienteId}`)).status, 409);
    assert.equal((await requisicao("GET", `/consultas/${consultaId}`)).status, 200);
  });

  it("DELETE retorna 204, remove do banco e mantém médico e paciente", async () => {
    const resultado = await requisicao("DELETE", `/consultas/${consultaId}`);
    assert.equal(resultado.status, 204);
    assert.equal(resultado.body, null);
    assert.equal(await prisma.consulta.findUnique({ where: { id: consultaId } }), null);
    assert.equal((await requisicao("GET", `/consultas/${consultaId}`)).status, 404);
    assert.equal((await requisicao("DELETE", `/consultas/${consultaId}`)).status, 404);
    assert.equal((await requisicao("GET", `/medicos/${outroMedicoId}`)).status, 200);
    assert.equal((await requisicao("GET", `/pacientes/${outroPacienteId}`)).status, 200);
  });
});
