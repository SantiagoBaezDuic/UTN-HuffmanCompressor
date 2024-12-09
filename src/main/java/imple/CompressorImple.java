package imple;

import huffman.def.Compresor;
import huffman.def.HuffmanInfo;
import huffman.def.HuffmanTable;

import java.util.LinkedList;
import java.util.List;

public class CompressorImple implements Compresor {
    // Recorre filename y retorna un HuffmanTable[256] contando cuántas veces aparece cada byte
    public HuffmanTable[] contarOcurrencias(String filename){
        return new HuffmanTable[0];
    };

    // Retorna una lista ordenada donde cada nodo representa a cada byte del archivo
    public List<HuffmanInfo> crearListaEnlazada(HuffmanTable arr[]){
        return new LinkedList<>();
    };

    // Convierte la lista en el árbol Huffman
    public HuffmanInfo convertirListaEnArbol(List<HuffmanInfo> lista){
        return new HuffmanInfo();
    };

    // Recorre el árbol Huffman y completa los códigos en el array
    public void generarCodigosHuffman(HuffmanInfo root,HuffmanTable arr[]){

    };

    // Escribe el encabezado en el archivo filename+".huf", y retorna cuántos bytes ocupa el encabezado
    public long escribirEncabezado(String filename,HuffmanTable arr[]){
        return 0;
    };

    // Recorre el archivo filename por cada byte escribe su código en filename+".huf"
    public void escribirContenido(String filename,HuffmanTable arr[]){

    };
}
