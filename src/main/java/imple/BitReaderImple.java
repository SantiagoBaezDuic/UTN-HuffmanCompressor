package imple;

import java.io.InputStream;
import java.io.IOException;
import huffman.def.BitReader;

public class BitReaderImple implements BitReader
{
	private InputStream is;
	private int buffer;
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
		int num = -1;
		if(bufferCount == 0) {
			try {
				int x = is.read();
				if(x == -1) {
					num = x;
					bufferCount = -1;
				} else {
					//seguir
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
