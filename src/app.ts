import express from "express";
import pacienteRoutes from "./routes/pacienteRoutes.js";
import medicoRoutes from "./routes/medicoRoutes.js";
import consultaRoutes from "./routes/consultaRoutes.js";
import { errorHandler } from "./middlewares/errorHandler.js";

const app = express();

app.use(express.json());
app.use(pacienteRoutes);
app.use(medicoRoutes);
app.use(consultaRoutes);
app.use(errorHandler);

export default app;
