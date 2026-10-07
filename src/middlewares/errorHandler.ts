import type { ErrorRequestHandler } from "express";
import { Prisma } from "@prisma/client";
import { ZodError } from "zod";
import { HttpError } from "../errors/HttpError.js";

export const errorHandler: ErrorRequestHandler = (erro, _req, res, _next) => {
  if (erro instanceof ZodError) {
    res.status(400).json({ mensagem: "Dados inválidos", erros: erro.issues });
    return;
  }

  if (erro instanceof HttpError) {
    res.status(erro.status).json({ mensagem: erro.message });
    return;
  }

  if (erro instanceof Prisma.PrismaClientKnownRequestError) {
    if (erro.code === "P2025") {
      res.status(404).json({ mensagem: "Registro não encontrado" });
      return;
    }
    if (erro.code === "P2003") {
      res.status(409).json({ mensagem: "Operação impedida por vínculo entre consulta, médico e paciente" });
      return;
    }
    if (erro.code === "P2002") {
      res.status(409).json({ mensagem: "Já existe um registro com esse CPF ou CRM" });
      return;
    }
  }

  if (erro instanceof SyntaxError && "type" in erro && erro.type === "entity.parse.failed") {
    res.status(400).json({ mensagem: "JSON inválido" });
    return;
  }

  console.error(erro);
  res.status(500).json({ mensagem: "Erro interno do servidor" });
};
