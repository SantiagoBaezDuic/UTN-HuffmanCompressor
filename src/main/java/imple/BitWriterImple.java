package imple;

import java.io.OutputStream;
import java.io.IOException;
import huffman.def.BitWriter;

public class BitWriterImple implements BitWriter 
{
	private OutputStream os;
	private int buffer;
	private int bufferCount;
    @Override
    public void using(OutputStream os)
    {
    	this.os = os;
    	buffer = 0;
    	bufferCount = 0;
    }

    @Override
    public void writeBit(int bit) 
    {
    	buffer = (buffer * 2) + bit;
    	bufferCount +=1;
    	if(bufferCount == 8) {
    		try {
    			os.write(buffer);
    			bufferCount = 0;
    		} catch(IOException e){
    			System.out.print("error ocurred: " + e.getMessage());
    		}
    	}
    }
        
    @Override
    public void flush() 
    {
    	int amount = 8 - bufferCount;
    	 for(int i=0; i < amount;i++) {
    		 writeBit(0);
    	 }
    }
}
