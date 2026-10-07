import * as consultaRepository from "../repositories/consultaRepository.js";
import * as medicoRepository from "../repositories/medicoRepository.js";
import * as pacienteRepository from "../repositories/pacienteRepository.js";
import { HttpError } from "../errors/HttpError.js";
import type { ConsultaDTO } from "../types/consulta.js";

async function validarRelacionamentos(dados: ConsultaDTO): Promise<void> {
  const [medico, paciente] = await Promise.all([
    medicoRepository.findById(dados.medicoId),
    pacienteRepository.findById(dados.pacienteId),
  ]);

  if (!medico) throw new HttpError(404, "Médico informado não encontrado");
  if (!paciente) throw new HttpError(404, "Paciente informado não encontrado");
}

export async function listarConsultas() {
  return await consultaRepository.findAll();
}

export async function encontrarUmaConsulta(id: number) {
  return await consultaRepository.findById(id);
}

export async function criarConsulta(dados: ConsultaDTO) {
  await validarRelacionamentos(dados);
  return await consultaRepository.create(dados);
}

export async function atualizarConsulta(id: number, dados: ConsultaDTO) {
  const consulta = await consultaRepository.findById(id);
  if (!consulta) throw new HttpError(404, "Consulta não encontrada");

  await validarRelacionamentos(dados);
  return await consultaRepository.update(id, dados);
}

export async function deletarConsulta(id: number): Promise<void> {
  const consulta = await consultaRepository.findById(id);
  if (!consulta) throw new HttpError(404, "Consulta não encontrada");

  await consultaRepository.remove(id);
}
