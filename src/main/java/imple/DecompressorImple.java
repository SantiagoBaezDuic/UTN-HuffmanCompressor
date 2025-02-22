package imple;

import huffman.util.Console;
import huffman.util.MessageReplacer;

import huffman.def.BitReader;
import huffman.def.Descompresor;
import huffman.def.HuffmanInfo;

import java.io.*;
import java.nio.ByteBuffer;

public class DecompressorImple implements Descompresor {
    Console console = Console.get();

    public long recomponerArbol(String filename, HuffmanInfo arbol){
        String message = "Recomponiendo árbol: %";
        console.println(MessageReplacer.replaceProgressMessage(message, 0));
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

            int auxID = 256; //Auxiliar para los nodos *
            for (int i = 0; i < leavesAmount; i++){
                long percentage = ((i + 1) * 100) / leavesAmount;
                if (percentage > progress){
                    console.println(MessageReplacer.replaceProgressMessage(message, percentage));
                    progress = percentage;
                }

                int character = bStream.read(); //Leo C
                int rawCodLength = bStream.read(); //Leo H
                headerLength += (rawCodLength / 8) + 2;
                StringBuilder cod = new StringBuilder();

                for (int x = 0; x < rawCodLength; x++){ //Leo todos los bits del código Huffman y los agrego al string
                    int bit = reader.readBit();
                    cod.append(bit);
                }

                HuffmanInfo aux = arbol;
                for (int x = 0; x < rawCodLength; x++){ //Rearmo el camino moviéndome a izquierda o derecha según el bit
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
                aux.setC(character);
                if (rawCodLength % 8 != 0){
                    headerLength++;
                    reader.flush();
                }
            }
            bStream.close();
            iStream.close();
            console.println(MessageReplacer.replaceInfoMessage("Árbol recompuesto con éxito!"));
        } catch (IOException err) {
            System.out.println(MessageReplacer.replaceInfoMessage("Error composing tree: " + err.getMessage()));
        }
        return headerLength;
    }
    public void descomprimirArchivo(HuffmanInfo root,long n,String filename){
        String message = "Descomprimiendo archivo: %";
        console.println(MessageReplacer.replaceProgressMessage(message, 0));
        try{
            String originalFilename = filename.substring(0, filename.length() - 4);
            InputStream iStream = new FileInputStream(filename);
            BufferedInputStream buffInpStream = new BufferedInputStream(iStream);
            buffInpStream.skip(n); //Saltea el header
            OutputStream oStream = new FileOutputStream(originalFilename);
            BufferedOutputStream bStream = new BufferedOutputStream(oStream);

            //Leemos los bytes que almacenan la longitud del archivo original
            int oLength;
            byte[] readByteLength = new byte[4];
            buffInpStream.read(readByteLength);
            oLength = ByteBuffer.wrap(readByteLength).getInt();

            BitReader reader = Factory.getBitReader();
            reader.using(buffInpStream);
            int bit = reader.readBit();

            long decompressedFileLength = 0;
            HuffmanInfo huffInfoAux = root; //Auxiliar para moverse por el árbol

            long progress = 0;

            while (bit >= 0 && oLength > decompressedFileLength){
                if (bit == 1){
                    huffInfoAux = huffInfoAux.getRight();
                } else {
                    huffInfoAux = huffInfoAux.getLeft();
                }
                if (huffInfoAux.getC() <= 255){ //Si es una hoja
                    int c = huffInfoAux.getC();
                    bStream.write(c);
                    decompressedFileLength++;
                    long percentage = (decompressedFileLength * 100) / oLength;
                    if (percentage > progress){
                        console.println(MessageReplacer.replaceProgressMessage(message, percentage));
                        progress = percentage;
                    }
                    huffInfoAux = root; //Me vuelvo a parar en la raíz
                }
                bit = reader.readBit();
            }
            bStream.close();
            oStream.close();
            buffInpStream.close();
            iStream.close();
            console.println(MessageReplacer.replaceInfoMessage("Archivo descomprimido con éxito!"));
            console.println(MessageReplacer.replaceInfoMessage("Proceso finalizado, presione cualquier tecla..."));
            console.pressAnyKey();
            console.closeAndExit();
        } catch (IOException err) {
            System.out.println(MessageReplacer.replaceInfoMessage("Error decompressing file: " + err.getMessage()));
        }
    }
}
