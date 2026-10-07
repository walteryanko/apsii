import { prisma } from "../config/prisma.js";
import type { ConsultaDTO } from "../types/consulta.js";

const relacionamentos = { medico: true, paciente: true } as const;

export async function findAll() {
  return await prisma.consulta.findMany({
    include: relacionamentos,
    orderBy: [{ data: "asc" }, { id: "asc" }],
  });
}

export async function findById(id: number) {
  return await prisma.consulta.findUnique({
    where: { id },
    include: relacionamentos,
  });
}

export async function create(data: ConsultaDTO) {
  return await prisma.consulta.create({ data, include: relacionamentos });
}

export async function update(id: number, data: ConsultaDTO) {
  return await prisma.consulta.update({
    where: { id },
    data,
    include: relacionamentos,
  });
}

export async function remove(id: number) {
  return await prisma.consulta.delete({ where: { id } });
}
