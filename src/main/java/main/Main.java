package main;

import java.util.List;

import huffman.def.Compresor;
import huffman.def.HuffmanTable;
import huffman.def.HuffmanInfo;
import huffman.util.Console;
import imple.Factory;

public class Main {

    static private String fileSelect(){
        Console c = Console.get();
        c.println("Seleccione el archivo a comprimir/descomprimir.");
        String selectedFile = c.fileExplorer();
        c.println("Usted ha seleccionado el archivo: " + selectedFile);
        return selectedFile;
    }

    static private void compressFile(String fileName){
        Compresor compressor = Factory.getCompresor();
        HuffmanTable[] huffmanArray = compressor.contarOcurrencias(fileName);
        List<HuffmanInfo> huffmanList = compressor.crearListaEnlazada(huffmanArray);
        HuffmanInfo rootNode = compressor.convertirListaEnArbol(huffmanList);
    }

    static private void decompressFile(String fileName){

    }

    public static void main(String[] args) {
        String fileName = fileSelect();
        Console c = Console.get();

        if (fileName.endsWith(".huf")){
            c.println("Comenzando descompresión");
            decompressFile(fileName);
        } else {
            c.println("Comenzando compresión");
            compressFile(fileName);
        }
    }
}