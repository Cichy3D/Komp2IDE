package pl.cichy3d.komp2ide.assembly;

public enum Register {
	
	A(0), B(1), C(2), D(3), E(4), H(5), I(6), P(7);
	
	private int num;

	private Register(int num) {
		this.num = num;
	}

	public int getNum() {
		return num;
	}
	
	public static Register get(String s) {
		return Register.valueOf(s.toUpperCase());
	}
}
