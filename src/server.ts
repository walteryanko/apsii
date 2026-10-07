import "dotenv/config";
import app from "./app.js";

const PORT: number = Number(process.env.PORT || 3000);

app.listen(PORT, () => {
  console.log(`A API subiu na porta ${PORT}`);
});
