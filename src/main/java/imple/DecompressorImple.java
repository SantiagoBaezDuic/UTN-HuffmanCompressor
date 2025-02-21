package imple;

import huffman.util.Console;

import huffman.def.BitReader;
import huffman.def.Descompresor;
import huffman.def.HuffmanInfo;

import java.io.*;
import java.nio.ByteBuffer;

public class DecompressorImple implements Descompresor {
    public long recomponerArbol(String filename, HuffmanInfo arbol){
        Console console = Console.get();
        console.println("Recomponiendo árbol: %" + 0);
        long headerLength = 0;
        try {
            InputStream iStream = new FileInputStream(filename);
            BufferedInputStream bStream = new BufferedInputStream(iStream);
            int leavesAmount = bStream.read();
            headerLength++;

            if (leavesAmount == 0){
                leavesAmount = 256;
            }

            BitReader reader = Factory.getBitReader();
            reader.using(bStream);

            long progress = 0;

            int auxID = 256;
            for (int i = 0; i < leavesAmount; i++){
                long percentage = ((i + 1) * 100) / leavesAmount;
                if (percentage > progress){
                    console.println("Recomponiendo árbol: %" + percentage);
                    progress = percentage;
                }

                int node1 = bStream.read();
                int node2 = bStream.read();
                long byteAmount = (node2 / 8) + 2;
                headerLength += byteAmount;
                StringBuilder cod = new StringBuilder();

                for (int x = 0; x < node2; x++){
                    int bit = reader.readBit();
                    cod.append(bit);
                }

                HuffmanInfo aux = arbol;
                for (int x = 0; x < node2; x++){
                    char bit = cod.charAt(x);
                    if (bit == '1'){
                        if (aux.getRight() == null){
                            HuffmanInfo r = new HuffmanInfo(auxID, 0);
                            auxID++;
                            aux.setRight(r);
                        }
                        aux = aux.getRight();
                    } else {
                        if (aux.getLeft() == null){
                            HuffmanInfo l = new HuffmanInfo(auxID, 0);
                            auxID++;
                            aux.setLeft(l);
                        }
                        aux = aux.getLeft();
                    }
                }
                aux.setC(node1);
                if (node2 % 8 != 0){
                    headerLength++;
                    reader.flush();
                }
            }
            bStream.close();
            iStream.close();
            console.println("Árbol recompuesto con éxito!");
        } catch (IOException err) {
            System.out.println("Error composing tree: " + err.getMessage());
        }
        return headerLength;
    }
    public void descomprimirArchivo(HuffmanInfo root,long n,String filename){
        Console console = Console.get();
        console.println("Descomprimiento archivo: %" + 0);
        try{
            String originalFilename = filename.substring(0, filename.length() - 4);
            InputStream iStream = new FileInputStream(filename);
            BufferedInputStream buffInpStream = new BufferedInputStream(iStream);
            buffInpStream.skip(n); //Skipea el header
            OutputStream oStream = new FileOutputStream(originalFilename);
            BufferedOutputStream bStream = new BufferedOutputStream(oStream);

            int oLength;
            byte[] readByteLength = new byte[4];
            buffInpStream.read(readByteLength);
            oLength = ByteBuffer.wrap(readByteLength).getInt();

            BitReader reader = Factory.getBitReader();
            reader.using(buffInpStream);
            int bit = reader.readBit();

            long decompFileLength = 0;
            HuffmanInfo huffInfoAux = root;

            long progress = 0;

            while (bit >= 0 && oLength > decompFileLength){
                if (bit == 1){
                    huffInfoAux = huffInfoAux.getRight();
                } else {
                    huffInfoAux = huffInfoAux.getLeft();
                }
                if (huffInfoAux.getC() <= 255){
                    int c = huffInfoAux.getC();
                    bStream.write(c);
                    decompFileLength++;
                    long percentage = (decompFileLength * 100) / oLength;
                    if (percentage > progress){
                        console.println("Descomprimiendo archivo: %" + percentage);
                        progress = percentage;
                    }
                    huffInfoAux = root;
                }
                bit = reader.readBit();
            }
            bStream.close();
            oStream.close();
            buffInpStream.close();
            iStream.close();
            console.println("Archivo descomprimido con éxito!");
            console.println("Proceso finalizado, presione cualquier tecla...");
            console.pressAnyKey();
            console.closeAndExit();
        } catch (IOException err) {
            System.out.println("Error decompressing file: " + err.getMessage());
        }
    }
}
