package imple;

import java.io.InputStream;
import java.io.IOException;
import huffman.def.BitReader;

public class BitReaderImple implements BitReader
{
	private InputStream is;
	private String buffer;
	private int bufferCount;
	@Override
	public void using(InputStream is)
	{
		this.is = is;
		this.buffer = "";
		this.bufferCount = 0;
	}

	private int readMostSignificantBit(){
		System.out.println("Initial Buffer: " + buffer);
		int bit = buffer.charAt(0) - '0';
		System.out.println("Num: " + bit);
		buffer = buffer.substring(1);
		bufferCount = buffer.length();
		System.out.println("New Buffer: " + buffer);
		return bit;
	}

	@Override
	public int readBit()
	{
		int num = 0;
		if(bufferCount == 0) {
			try {
				int data = is.read();
				if (data != -1) { //Hay nuevo bit por leer
					buffer = String.format("%8s", Integer.toBinaryString(data)).replace(' ', '0'); //toBinaryString elimina los ceros "no relevantes" y a veces devuelve un string de menos de 8 caracteres, mediante format y replace rellenamos los 0 eliminados
					bufferCount = buffer.length(); //Se setea el count en 8
					num = readMostSignificantBit();
				} else { //Se llegó al final de los datos
					num = -1;
				}
			} catch(IOException e) {
				System.out.print("error occurred: " + e.getMessage());
			}
		} else {
				num = readMostSignificantBit();
		}
		
		return num;
	}
	
	@Override
	public void flush()
	{
		buffer = "";
		bufferCount = 0;
	}
}
