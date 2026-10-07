CREATE TABLE "consultas" (
    "id" SERIAL NOT NULL,
    "data" TIMESTAMP(3) NOT NULL,
    "turno" TEXT NOT NULL,
    "medico_id" INTEGER NOT NULL,
    "paciente_id" INTEGER NOT NULL,
    CONSTRAINT "consultas_pkey" PRIMARY KEY ("id")
);

CREATE INDEX "consultas_medico_id_idx" ON "consultas"("medico_id");
CREATE INDEX "consultas_paciente_id_idx" ON "consultas"("paciente_id");

ALTER TABLE "consultas" ADD CONSTRAINT "consultas_medico_id_fkey"
    FOREIGN KEY ("medico_id") REFERENCES "medicos"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
ALTER TABLE "consultas" ADD CONSTRAINT "consultas_paciente_id_fkey"
    FOREIGN KEY ("paciente_id") REFERENCES "pacientes"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
