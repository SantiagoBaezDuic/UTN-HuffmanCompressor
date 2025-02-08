package imple;

import huffman.util.Console;

import huffman.def.Compresor;
import huffman.def.HuffmanInfo;
import huffman.def.HuffmanTable;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CompressorImple implements Compresor {
    @Override
    // Recorre el archivo y retorna un HuffmanTable[256] contando cuántas veces aparece cada byte
    public HuffmanTable[] contarOcurrencias(String fileName){
        //Creo el ArrayList de tablas, lo uso en lugar de un Array por más comodida a la hora de manejarlo
        ArrayList<HuffmanTable> tableAList = new ArrayList<>();

        //Poblar el ArrayList con 256 elementos (capacidad máxima de representación del byte), de esta manera cada uno de los bytes
        // se corresponde con el index de uno de estos elementos.
        for (int i = 0; i < 256; i++){
            //Creo la tabla para cada elemento
            HuffmanTable table = new HuffmanTable();
            table.setN(0); //Inicializo sus ocurrencias en 0.
            table.setCod(Character.toString((char)i)); //Asigno el código del elemento correspondiente.
            tableAList.add(table);
        }

        try {
            //Instancio el inputStream que va leyendo el archivo, pero utilizo bufferedInputStream ya que hace mucho más
            //eficientes las múltiples lecturas necesarias en archivos de cierto tamaño.
            InputStream iStream = new FileInputStream(fileName);
            BufferedInputStream bStream = new BufferedInputStream(iStream);

            int currentByte = bStream.read(); //Byte con el que se va a trabajar actualmente.

            File f = new File(fileName);
            long totalSize = f.length(); //Cantidad total de bytes
            long currentlyRead = 0; //Cantidad leída de bytes

            Console c = Console.get(); //Instancia de la consola.
            long currentProgress = 0;
            c.println("Contando ocurrencias: %" + currentProgress); //%0

            while (currentByte != -1){ //Mientras el byte no sea -1 (fin del archivo)
                tableAList.get(currentByte).increment(); //Suma una ocurrencia a la tabla correspondiente.

                currentlyRead ++;
                long newPercentage = (currentlyRead * 100) / totalSize;

                if (newPercentage > currentProgress){ //Si el porcentaje se tiene que actualizar lo muestro por consola
                    c.println("Contando ocurrencias: %" + newPercentage);
                    currentProgress = newPercentage;
                }

                currentByte = bStream.read(); //Vuelvo a leer
            }
            // Cierro los streams de data
            bStream.close();
            iStream.close();
        } catch (IOException err) { //Error handling
            System.out.print("Error reading file:" + err.getMessage());
        }

        HuffmanTable[] huffmanArray = new HuffmanTable[tableArray.size()];
        tableAList.toArray(huffmanArray);
        return huffmanArray;
    };

    // Retorna una lista ordenada donde cada nodo representa a cada byte del archivo
    public List<HuffmanInfo> crearListaEnlazada(HuffmanTable arr[]){
        List<HuffmanInfo> huffmanList = new LinkedList<>(); //Lista enlazada que va a ser retornada.
        ArrayList<HuffmanTable> huffmanAList = new ArrayList<>(Arrays.asList(arr));

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
