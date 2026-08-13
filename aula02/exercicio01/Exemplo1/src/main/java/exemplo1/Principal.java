package exemplo1;

public class Principal {

	public static void main(String[] args) {
		
		Banco b1 = new Banco();
		Banco b2 = new Banco("Raul", "1111", 15);
		
		System.out.println(b1.getSaldo());
		b1.setSaldo(10);
		System.out.println(b1.getSaldo());
		
	}

}
