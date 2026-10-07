import type { Request, Response } from "express";
import * as service from "../services/consultaService.js";
import { consultaIdSchema, consultaSchema } from "../schemas/consultaSchema.js";

export async function listar(_req: Request, res: Response) {
  const consultas = await service.listarConsultas();
  return res.json(consultas);
}

export async function buscarPorId(req: Request, res: Response) {
  const id = consultaIdSchema.parse(req.params.id);
  const consulta = await service.encontrarUmaConsulta(id);

  if (!consulta) {
    return res.status(404).json({ mensagem: "Consulta não encontrada" });
  }

  return res.json(consulta);
}

export async function cadastrar(req: Request, res: Response) {
  const dados = consultaSchema.parse(req.body);
  const consulta = await service.criarConsulta(dados);
  return res.status(201).json(consulta);
}

export async function atualizar(req: Request, res: Response) {
  const id = consultaIdSchema.parse(req.params.id);
  const dados = consultaSchema.parse(req.body);
  const consulta = await service.atualizarConsulta(id, dados);
  return res.json(consulta);
}

export async function deletar(req: Request, res: Response) {
  const id = consultaIdSchema.parse(req.params.id);
  await service.deletarConsulta(id);
  return res.status(204).send();
}
