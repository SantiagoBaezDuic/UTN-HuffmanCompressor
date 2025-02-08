package imple;

import huffman.util.Console;

import huffman.def.Compresor;
import huffman.def.HuffmanInfo;
import huffman.def.HuffmanTable;
import huffman.util.HuffmanTree;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CompressorImple implements Compresor {

    int leavesAmount = 0; //Para llevar el conteo de las hojas del árbol

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

        HuffmanTable[] huffmanArray = new HuffmanTable[tableAList.size()];
        tableAList.toArray(huffmanArray);
        return huffmanArray;
    };

    // Retorna una lista ordenada donde cada nodo representa a cada byte del archivo
    public List<HuffmanInfo> crearListaEnlazada(HuffmanTable arr[]){
        Console console = Console.get();
        console.println("Creando lista enlazada...");
        ArrayList<HuffmanTable> huffmanAList = new ArrayList<>(Arrays.asList(arr)); //ArrayList de partida
        List<HuffmanInfo> huffmanList = new LinkedList<>(); //Lista enlazada que va a ser retornada.

        huffmanAList.sort((e1, e2) -> Integer.compare(e1.getN(), e2.getN())); //Ordeno la lista según cantidad ascendente de ocurrencias

        for (int i = 0; i < 256; i++){
            HuffmanTable currentNode = huffmanAList.get(i); //Leo el nodo

            if (currentNode.getN() != 0){ //Si el byte tiene alguna ocurrencia
                leavesAmount++;
                String code = currentNode.getCod(); //Leo el byte asociado
                int c = (int)(code.charAt(0)); //Transformo el byte a su expresión en ASCII
                HuffmanInfo huffmanInfo = new HuffmanInfo(c, currentNode.getN()); //Junto la expresión con la # de ocurrencias
                huffmanList.add(huffmanInfo); //Añado la hoja/nodo a la lista enlazada
            }
        }
        console.println("Lista enlazada creada con éxito!");
        return huffmanList;
    };

    private static void orderedInsert(List<HuffmanInfo> lista, HuffmanInfo node){
        Boolean alreadyInserted = false; //Para saber si ya fue insertado y el proceso debe parar
        for (int i = 0; i <= lista.size() && !alreadyInserted; i++){
            if (i == lista.size()){ // Si alcanzamos el final de la lista, inserto el nodo al final
                lista.add(node);
                alreadyInserted = true; // Detengo el proceso
            } else if (node.getN() < lista.get(i).getN()) { //Si la cantidad de ocurrencias es menor que la del nodo siguiente, inserto el nodo
                lista.add(node);
                alreadyInserted = true;
            }
        }
    }

    // Convierte la lista en el árbol Huffman
    public HuffmanInfo convertirListaEnArbol(List<HuffmanInfo> lista){
        Console c = Console.get();
        c.println("Convirtiendo lista en árbol...");
        for (int i=256; lista.size() != 1; i++){
            HuffmanInfo a = lista.remove(0); //Elimino el primer nodo
            HuffmanInfo b = lista.remove(0); //Elimino el segundo nodo
            int newNodeAmount = a.getN() + b.getN(); //Sumo las ocurrencias
            HuffmanInfo newNode = new HuffmanInfo(i, newNodeAmount); //Creo el nuevo nodo
            newNode.setRight(a); //Asigno el primer nodo como hijo derecho
            newNode.setLeft(b); //Asigno el segundo nodo como hijo izquierdo
            orderedInsert(lista, newNode);
        }
        HuffmanInfo rootNode = lista.get(0); //Selecciono el único nodo que quedó en el árbol (el nodo raíz)
        c.println("Árbol creado con éxito!");
        return rootNode;
    };

    // Recorre el árbol Huffman y completa los códigos en el array
    public void generarCodigosHuffman(HuffmanInfo root,HuffmanTable arr[]){
        HuffmanTree huffmanTree = new HuffmanTree(root);
        
    };

    // Escribe el encabezado en el archivo filename+".huf", y retorna cuántos bytes ocupa el encabezado
    public long escribirEncabezado(String filename,HuffmanTable arr[]){
        return 0;
    };

    // Recorre el archivo filename por cada byte escribe su código en filename+".huf"
    public void escribirContenido(String filename,HuffmanTable arr[]){

    };
}
