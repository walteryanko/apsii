import { prisma } from "../src/config/prisma.js";

try {
  const medico = await prisma.medico.upsert({
    where: { crm: "DEMO-001" },
    update: {},
    create: { nome: "Médico de demonstração", crm: "DEMO-001", especialidade: "Clínica geral" },
  });
  const paciente = await prisma.paciente.upsert({
    where: { cpf: "00000000000" },
    update: {},
    create: { nome: "Paciente de demonstração", cpf: "00000000000", telefone: "00000000000" },
  });

  console.log("Dados fictícios para testar consultas:");
  console.log(JSON.stringify({ medicoId: medico.id, pacienteId: paciente.id }, null, 2));
} finally {
  await prisma.$disconnect();
}
