CREATE TABLE "pacientes" (
    "id" SERIAL NOT NULL,
    "nome" TEXT NOT NULL,
    "cpf" TEXT NOT NULL,
    "telefone" TEXT NOT NULL,
    "criado_em" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "atualizado_em" TIMESTAMP(3) NOT NULL,
    CONSTRAINT "pacientes_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "medicos" (
    "id" SERIAL NOT NULL,
    "nome" TEXT NOT NULL,
    "crm" TEXT NOT NULL,
    "especialidade" TEXT NOT NULL,
    "criado_em" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "atualizado_em" TIMESTAMP(3) NOT NULL,
    CONSTRAINT "medicos_pkey" PRIMARY KEY ("id")
);

CREATE UNIQUE INDEX "pacientes_cpf_key" ON "pacientes"("cpf");
CREATE UNIQUE INDEX "medicos_crm_key" ON "medicos"("crm");
