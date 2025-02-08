package main;

import huffman.def.Compresor;
import huffman.def.HuffmanTable;
import huffman.util.Console;
import imple.Factory;

public class Main {

    static private String fileSelect(){
        Console c = Console.get();
        c.println("Seleccione el archivo a comprimir/descomprimir.");
        return c.fileExplorer();
    }

    static private void compressFile(String fileName){
        Compresor compressor = Factory.getCompresor();
        HuffmanTable[] huffmanArray = compressor.contarOcurrencias(fileName);
    }

    static private void decompressFile(String fileName){

    }

    public static void main(String[] args) {
        String fileName = fileSelect();

        if (fileName.endsWith(".huf")){
            decompressFile(fileName);
        } else {
            compressFile(fileName);
        }
    }
}