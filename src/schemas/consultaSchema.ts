import { z } from "zod";

// Os tipos TypeScript não validam o JSON recebido em tempo de execução.
export const consultaSchema = z.strictObject({
  data: z.union([z.iso.date(), z.iso.datetime({ offset: true })])
    .transform((valor) => new Date(valor)),
  turno: z.string().trim()
    .transform((valor) => valor.normalize("NFD").replace(/[\u0300-\u036f]/g, "").toUpperCase())
    .pipe(z.enum(["MANHA", "TARDE", "NOITE"])),
  medicoId: z.number().int().positive().max(2147483647),
  pacienteId: z.number().int().positive().max(2147483647),
});

export const consultaIdSchema = z.string().regex(/^[1-9]\d*$/)
  .transform(Number)
  .pipe(z.number().int().positive().max(2147483647));
