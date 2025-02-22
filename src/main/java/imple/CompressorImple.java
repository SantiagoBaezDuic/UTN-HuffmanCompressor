package imple;

import huffman.def.BitWriter;
import huffman.util.Console;
import huffman.util.MessageReplacer;

import huffman.def.Compresor;
import huffman.def.HuffmanInfo;
import huffman.def.HuffmanTable;
import huffman.util.HuffmanTree;

import java.io.*;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.*;

public class CompressorImple implements Compresor {

    int leavesAmount = 0; //Para llevar el conteo de las hojas del árbol
    Console console = Console.get(); //Única instancia de la consola

    private static void createElements(ArrayList<HuffmanTable> aList){
        for (int i = 0; i < 256; i++){
            //Creo la tabla para cada elemento
            HuffmanTable table = new HuffmanTable();
            table.setN(0); //Inicializo sus ocurrencias en 0.
            table.setCod(Character.toString((char)i)); //Asigno el código del elemento correspondiente.
            aList.add(table);
        }
    }

    @Override
    // Recorre el archivo y retorna un HuffmanTable[256] contando cuántas veces aparece cada byte
    public HuffmanTable[] contarOcurrencias(String fileName){
        long iT = System.currentTimeMillis();
        //Creo el ArrayList de tablas, lo uso en lugar de un Array por más comodidad a la hora de manejarlo
        ArrayList<HuffmanTable> tableAList = new ArrayList<>();

        //Poblar el ArrayList con 256 elementos (capacidad máxima de representación del byte), de esta manera cada uno de los bytes
        // se corresponde con el index de uno de estos elementos.
        createElements(tableAList);

        try {
            //Inicio una instancia del inputStream que va leyendo el archivo, pero utilizo bufferedInputStream, ya que hace mucho más
            //eficientes las múltiples lecturas necesarias en archivos de cierto tamaño.
            InputStream iStream = new FileInputStream(fileName);
            BufferedInputStream bStream = new BufferedInputStream(iStream);

            int currentByte = bStream.read(); //Byte con el que se va a trabajar actualmente.

            File f = new File(fileName);
            long totalSize = f.length(); //Cantidad total de bytes
            long currentlyRead = 0; //Cantidad leída de bytes
            long currentProgress = 0;
            String message = "Contando ocurrencias: %";
            console.println(MessageReplacer.replaceProgressMessage(message, 0)); //%0

            while (currentByte != -1){ //Mientras el byte no sea -1 (fin del archivo)
                tableAList.get(currentByte).increment(); //Suma una ocurrencia a la tabla correspondiente.

                currentlyRead ++;
                long newPercentage = (currentlyRead * 100) / totalSize;

                if (newPercentage > currentProgress){ //Si el porcentaje se tiene que actualizar lo muestro por consola
                    console.println(MessageReplacer.replaceProgressMessage(message, newPercentage));
                    currentProgress = newPercentage;
                }

                currentByte = bStream.read(); //Vuelvo a leer
            }
            // Cierro los streams de data
            bStream.close();
            iStream.close();
        } catch (IOException err) { //Error handling
            System.out.print(MessageReplacer.replaceInfoMessage("Error reading file:" + err.getMessage()));
        }

        HuffmanTable[] huffmanArray = new HuffmanTable[tableAList.size()];
        tableAList.toArray(huffmanArray);
        long fT = System.currentTimeMillis();
        console.println(MessageReplacer.replaceInfoMessage("Ocurrencias contadas en " + (fT-iT) + " milisegundos."));
        return huffmanArray;
    }

    // Retorna una lista ordenada donde cada nodo representa a cada byte del archivo
    public List<HuffmanInfo> crearListaEnlazada(HuffmanTable[] arr){
        long iT = System.currentTimeMillis();
        console.println(MessageReplacer.replaceInfoMessage("Creando lista enlazada..."));
        ArrayList<HuffmanTable> huffmanAList = new ArrayList<>(Arrays.asList(arr)); //ArrayList de partida
        List<HuffmanInfo> huffmanList = new LinkedList<>(); //Lista enlazada que va a ser retornada.

        huffmanAList.sort(Comparator.comparingInt(HuffmanTable::getN)); //Ordeno la lista según cantidad ascendente de ocurrencias

        for (int i = 0; i < 256; i++){
            HuffmanTable currentNode = huffmanAList.get(i); //Leo el nodo

            if (currentNode.getN() != 0){ //Si el byte tiene alguna ocurrencia
                leavesAmount++;
                String code = currentNode.getCod(); //Leo el byte asociado
                int c = (code.charAt(0)); //Transformo el byte a su expresión en ASCII
                HuffmanInfo huffmanInfo = new HuffmanInfo(c, currentNode.getN()); //Combino la expresión con la # de ocurrencias
                huffmanList.add(huffmanInfo); //Añado la hoja/nodo a la lista enlazada
            }
        }
        console.println(MessageReplacer.replaceInfoMessage("Lista enlazada creada con éxito!"));
        long fT = System.currentTimeMillis();
        console.println(MessageReplacer.replaceInfoMessage("Lista enlazada creada en " + (fT-iT) + " milisegundos."));
        return huffmanList;
    }

    private static void orderedInsert(List<HuffmanInfo> lista, HuffmanInfo node){
        boolean alreadyInserted = false; //Para saber si ya fue insertado y el proceso debe parar
        for (int i = 0; i <= lista.size() && !alreadyInserted; i++){
            if (i == lista.size()){ // Si alcanzamos el final de la lista, inserto el nodo al final
                lista.add(node);
                alreadyInserted = true; // Detengo el proceso
            } else if (node.getN() < lista.get(i).getN()) { //Si la cantidad de ocurrencias es menor que la del nodo siguiente, inserto el nodo
                lista.add(i, node);
                alreadyInserted = true;
            }
        }
    }

    // Convierte la lista en el árbol Huffman
    public HuffmanInfo convertirListaEnArbol(List<HuffmanInfo> lista){
        long iT = System.currentTimeMillis();
        console.println(MessageReplacer.replaceInfoMessage("Convirtiendo lista en árbol..."));
        for (int i = 256; lista.size() != 1; i++){
            HuffmanInfo a = lista.remove(0); //Elimino el primer nodo
            HuffmanInfo b = lista.remove(0); //Elimino el segundo nodo
            int newNodeAmount = a.getN() + b.getN(); //Sumo las ocurrencias
            HuffmanInfo newNode = new HuffmanInfo(i, newNodeAmount); //Creo el nuevo nodo
            newNode.setRight(a); //Asigno el primer nodo como hijo derecho
            newNode.setLeft(b); //Asigno el segundo nodo como hijo izquierdo
            orderedInsert(lista, newNode);
        }
        HuffmanInfo rootNode = lista.get(0); //Selecciono el único nodo que quedó en el árbol (el nodo raíz)
        console.println(MessageReplacer.replaceInfoMessage("Árbol creado con éxito!"));
        long fT = System.currentTimeMillis();
        console.println(MessageReplacer.replaceInfoMessage("Árbol creado en " + (fT-iT) + " milisegundos."));
        return rootNode;
    }

    // Recorre el árbol Huffman y completa los códigos en el array
    public void generarCodigosHuffman(HuffmanInfo root, HuffmanTable[] arr){
        long iT = System.currentTimeMillis();
        HuffmanTree huffmanTree = new HuffmanTree(root);
        StringBuffer sBuff = new StringBuffer();
        HuffmanInfo currentLeaf = huffmanTree.next(sBuff);
        List<HuffmanTable> huffmanList = new ArrayList<>(Arrays.asList(arr));

        String message = "Generando códigos Huffman: %";
        console.println(MessageReplacer.replaceProgressMessage(message, 0));

        long currentProgress = 0;

        long currentLeavesAmount = 0;

        while (currentLeaf != null) {
            currentLeavesAmount++;
            long newPercentage = (currentLeavesAmount * 100) / leavesAmount;

            if (newPercentage > currentProgress){
                console.println(MessageReplacer.replaceProgressMessage(message, newPercentage));
                currentProgress = newPercentage;
            }

            HuffmanTable huffmanTable = huffmanList.get(currentLeaf.getC());
            String code = sBuff.toString();
            huffmanTable.setCod(code);

            currentLeaf = huffmanTree.next(sBuff);
        }

        console.println(MessageReplacer.replaceInfoMessage("Códigos Huffman generados con éxito!"));
        long fT = System.currentTimeMillis();
        console.println(MessageReplacer.replaceInfoMessage("Códigos creados en " + (fT-iT) + " milisegundos."));

        huffmanList.toArray(arr);
    }

    static private void byteFlusher(int b, BitWriter w){
        if (b % 8 != 0){
            w.flush();
        }
    }

    static private void writeStringData(String s, BitWriter w){
        for (int x = 0; x < s.length(); x++){
            char bit = s.charAt(x);
            if (bit == '0'){
                w.writeBit(0);
            } else {
                w.writeBit(1);
            }
        }
    }

    // Escribe el encabezado en el archivo filename+".huf", y retorna cuántos bytes ocupa el encabezado
    public long escribirEncabezado(String filename, HuffmanTable[] arr){
        long iT = System.currentTimeMillis();
        List<HuffmanTable> huffmanAList = new ArrayList<>(Arrays.asList(arr));
        String outputFile = filename + ".huf"; //Se agrega la extensión nueva

        String message = "Escribiendo encabezados: %";
        console.println(MessageReplacer.replaceProgressMessage(message, 0));

        long progress = 0; //Variable para seguir el progreso

        try {
            //Abro el outputStream y el bufferedOutputStream para poder escribir el archivo sin tantas búsquedas
            OutputStream oStream = new FileOutputStream(outputFile);
            BufferedOutputStream bStream = new BufferedOutputStream(oStream);

            bStream.write(leavesAmount); //Escribo la cantidad de hojas

            long leavesWritten = 0; //Variable para seguir el progreso

            for (int i = 0; i < 256; i++){ //Recorro el arraylist
                HuffmanTable huffmanTable = huffmanAList.get(i); //Obtengo el elemento

                if (huffmanTable.getN() > 0){ // Si hay ocurrencias y merece escribirse
                    leavesWritten++;
                    long percentage = (leavesWritten * 100) / leavesAmount;
                    if (percentage > progress){
                        console.println(MessageReplacer.replaceProgressMessage(message, percentage));
                        progress = percentage;
                    }

                    bStream.write(i);
                    String codString = huffmanTable.getCod();
                    bStream.write(codString.length());
                    BitWriter writer = Factory.getBitWriter();
                    writer.using(bStream);

                    writeStringData(codString, writer);

                    //Se completa el último byte
                    byteFlusher(codString.length(), writer);
                }
            }
            File originalFile = new File(filename);
            int originalLength = (int)originalFile.length();
            ByteBuffer bBuffer = ByteBuffer.allocate(4);
            bBuffer.putInt(originalLength);
            byte[] bytes = bBuffer.array();
            bStream.write(bytes); //Escribo la longitud del archivo original

            //Cierro los streams
            bStream.close();
            oStream.close();
        } catch (IOException err) {
            System.out.println(MessageReplacer.replaceInfoMessage("Error writing file headers: " + err.getMessage()));
        }
        console.println(MessageReplacer.replaceInfoMessage("Encabezados escritos con éxito!"));
        long fT = System.currentTimeMillis();
        console.println(MessageReplacer.replaceInfoMessage("Encabezados escritos en " + (fT-iT) + " milisegundos."));
        File compressedFile = new File(outputFile);
        return compressedFile.length();
    }

    // Recorre el archivo filename por cada byte escribe su código en filename+".huf"
    public void escribirContenido(String filename, HuffmanTable[] arr){
        long iT = System.currentTimeMillis();
        try {
            InputStream iStream = new FileInputStream(filename);
            BufferedInputStream buffInpStream = new BufferedInputStream(iStream);
            String outputFilename = filename + ".huf";
            OutputStream oStream = new FileOutputStream(outputFilename, true); //Añade al final del archivo
            BufferedOutputStream bStream = new BufferedOutputStream(oStream);

            //Configuro el BitWriter
            int currentByte = buffInpStream.read();
            BitWriter writer = Factory.getBitWriter();
            writer.using(bStream);

            int totalBits = 0;
            long oBytes = new File(filename).length();
            long compressedBytes = 0;

            long progress = 0;

            String message = "Escribiendo contenidos: %";
            console.println(MessageReplacer.replaceProgressMessage(message, 0));

            List<HuffmanTable> huffmanAList = new ArrayList<>(Arrays.asList(arr));

            while (currentByte >= 0){
                compressedBytes++;
                long percentage = (compressedBytes * 100) / oBytes;
                if (percentage > progress){
                    console.println(MessageReplacer.replaceProgressMessage(message, percentage));
                    progress = percentage;
                }

                HuffmanTable huffmanTable = huffmanAList.get(currentByte);
                String cod = huffmanTable.getCod();
                totalBits += cod.length();

                writeStringData(cod, writer);

                currentByte = buffInpStream.read();
            }

            //Se completa el último byte
            byteFlusher(totalBits, writer);

            //Cierro los streams
            bStream.close();
            oStream.close();
            buffInpStream.close();
            iStream.close();

            console.println(MessageReplacer.replaceInfoMessage("Contenidos escritos con éxito!"));
            long fT = System.currentTimeMillis();
            console.println(MessageReplacer.replaceInfoMessage("Contenidos escritos en " + (fT-iT) + " milisegundos."));
            console.println(MessageReplacer.replaceInfoMessage("Archivo comprimido con éxito!"));
            console.println(MessageReplacer.replaceInfoMessage("Proceso finalizado, presione cualquier tecla..."));
            console.pressAnyKey();
            console.closeAndExit();
        } catch (IOException err) {
            System.out.println(MessageReplacer.replaceInfoMessage("Error writing file content: " + err.getMessage()));
        }
    }
}
