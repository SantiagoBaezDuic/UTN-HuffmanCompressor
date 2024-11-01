package huffman.def;

public interface Descompresor
{
	// ** en todos los casos filename es el nombre del archivo original **

	// Restaura el árbol leyendo el encabezado desde el archivo filename+".huf" 
	public HuffmanInfo recomponerArbol(String filename);
	
	// Recorre bit por bit el archivo filename+".huf", decodifica y escribe 
	// cada byte decodificado en el arhivo filename
	public void descomprimirArchivo(HuffmanInfo root,String filename);
}
