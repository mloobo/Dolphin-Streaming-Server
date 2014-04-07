package com.dolphinss.util;

public class UtilBytes {
	public static int getBit(byte[] data, int pos) {
		int posByte = pos / 8;
		int posBit = pos % 8;
		byte valByte = data[posByte];
		int valInt = valByte >> (8 - (posBit + 1)) & 0x0001;
		return valInt;
	}
	
	public static int getBitFromArray(byte[] data, int pos) throws Exception{
		int numByte=(pos/8);
		int posEnByte=(pos%8);
//		System.out.println("El bit numero '"+pos+"' estará en el byte numero '"+numByte+"' de '"+(data.length-1)+"'");
		if(numByte>=data.length || pos<0){
			//lanza exception porque está intentando acceder a una posición bit que no existe
			throw new Exception("La posicion '"+pos+"' no existe en el array de bytes");
		}
		byte b=data[numByte];
//		System.out.println(UtilBytes.getBitsFromByte(b));
		Boolean a=isBitSet(b, posEnByte);
		if(a)
			return 1;
		return 0;
	}

	public static String getBitsEntero(int valor) {
		int mascaraMostrar = 1 << 31;
		StringBuffer bufer = new StringBuffer(35);
		int c = 0;
		for (int i = 0; i <= 32; i++) {
			bufer.append((valor & mascaraMostrar) == 0 ? '0' : '1');
			valor <<= 1;
			if (c == 8) {
				bufer.append(" ");
				c = 0;
			}
			c++;
		}
		return bufer.toString();
	}

	public static String getBitsFromByte(byte b) {
		String r = "";
		for (int i = 0; i < 8; i++) {
			r += "" + (isBitSet(b, i) ? "1" : "0");
		}
		r += " ";
		return r;
	}

	private static Boolean isBitSet(byte b, int bit) {
		return (b & (1 << bit)) != 0;
	}
	
//	public static void main(String[] args){
//		byte b=12;
//		byte[] bb={12,13};
////		System.out.println(UtilBytes.getBitsFromByte(b));
////		System.out.println(UtilBytes.getBitsFromByte((byte)13));
//		for(int i=0;i<bb.length*8+4;i++){
//			try {
//				System.out.println(UtilBytes.getBitFromArray(bb, i));
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				System.exit(0);
//			}
//		}
//	}
}
