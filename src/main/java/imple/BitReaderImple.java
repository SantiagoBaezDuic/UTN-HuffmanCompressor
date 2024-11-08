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
		this.buffer = 0;
		this.bufferCount = 0;
	}

	@Override
	public int readBit()
	{
		int num = 9;
		if(bufferCount == 0) {
			try {
				String binString = Integer.toBinaryString(is.read());
				System.out.print("read " + binString);
				 if(binString == "-1") {
					buffer = binString;
					bufferCount = -1;
				} else {
					buffer = binString;
					bufferCount = 8;
				}
			} catch(IOException e) {
				System.out.print("error ocurred: " + e.getMessage());
			}
		} else {
				
		}
		
		
		return num;
	}
	
	@Override
	public void flush()
	{
		buffer = 0;
	}
}
