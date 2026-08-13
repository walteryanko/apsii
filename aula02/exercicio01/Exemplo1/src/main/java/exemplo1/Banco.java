package exemplo1;

public class Banco {

	private String nome;
	private String cpf;
	private double saldo;
	
	public Banco (String nome, String cpf, double saldo) {
		this.nome = nome;
		this.cpf = cpf;
		this.setSaldo(saldo);
	}
	
	public Banco () {
		this.nome = "sem nome";
		this.cpf = "0000";
		this.setSaldo(0);
	}

	public double getSaldo() {
		return saldo;
	}

	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}
}
