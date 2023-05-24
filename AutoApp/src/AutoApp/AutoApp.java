package AutoApp;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import com.google.gson.*;




public class AutoApp {

	
	public static String folderZip = "AutoApp";
	public static String nombreApp = "AutoApp";
	public static String urlAPI = "";
	public static String pathFile = "AutoApp-plantilla.zip";
	
	
	public static String getUrlAPIFile(String folderName) {
		return folderName + "/app/src/main/java/ua/autoapp/gvalumni/entity/URL.kt";
	}
	
	public static String getItemResponseFile(String folderName) {
		return folderName + "/app/src/main/java/ua/autoapp/gvalumni/entity/ItemResponse.kt";
	}
	
	public static String getStringsFile(String folderName) {
		return folderName + "/app/src/main/res/values/strings.xml";
	}
	
	public static void getCampos(String folderName) {
		File origenFile = new File(pathFile);
        if (!origenFile.exists()) {
        	System.out.println("No existe el archivo zip");
        }
        else {
        	descomprimirZip();	
        	File urlApiFile = new File(getUrlAPIFile(folderName));
            if (!urlApiFile.exists()) {
            	System.out.println("No existe el archivo URL.kt");
            	System.out.println("No existe el archivo URL.kt: " + getUrlAPIFile(folderName));
            	System.out.println("folder zip: " + folderZip);

            }
            else {
            	File urlItemResponseFile = new File(getItemResponseFile(folderName));
                if (!urlItemResponseFile.exists()) {
                	System.out.println("No existe el archivo ItemResponseFile.kt");
                }
                else {
                	File urlStringsFile = new File(getStringsFile(folderName));
                    if (!urlStringsFile.exists()) {
                    	System.out.println("No existe el archivo strings.xml");
                    }
                    else {
                    	editApiFile(urlAPI,getUrlAPIFile(folderName));	
                    	editApiFile(nombreApp,getStringsFile(folderName));	
                    	llamarAPI(folderName);
                    }
                }
            }
        }
		
	}
	
	public static void llamarAPI(String folderName) {

		   try {
	            // Leer el contenido de la URL
	            String contenido = leerContenidoURL(urlAPI);

	            // Convertir el contenido en un JsonArray
	            JsonObject jsonObject = new Gson().fromJson(contenido, JsonObject.class);
	            

	            // Obtener el JsonElement dentro del JsonObject utilizando el identificador "results"
	            JsonArray jsonElement = jsonObject.get("results").getAsJsonArray();

	            // Utilizar el JsonArray según tus necesidades
	            for (JsonElement elemento : jsonElement) {
	                // Acceder a cada objeto JSON en el array
	                JsonObject objeto = elemento.getAsJsonObject();
	                
	                // Obtener el conjunto de entradas del JsonObject
	                Set<Map.Entry<String, JsonElement>> entrySet = objeto.entrySet();

	                // Convertir el conjunto en una lista para acceder por índice
	                List<Map.Entry<String, JsonElement>> entryList = new ArrayList<>(entrySet);
	                String key ="";
	                for(int i=0;i<entryList.size();i++) {
	                	// Obtener el elemento por posición (ejemplo: el primer elemento)
		                Map.Entry<String, JsonElement> entry = entryList.get(i);
		                // Acceder a la clave y al valor del elemento
		                String clave = entry.getKey();
		                key += "\tvar " + clave + " : String? = null, \n" ;
		               		                
	                }
	                editApiFile(key, getItemResponseFile(folderName));
	                
	                System.out.println("Aplicación generada correctamente");
	                break;
	                
	            }
	        } catch (IOException e) {
	            System.out.println(e.getMessage());
	        }
	}
	
    private static String leerContenidoURL(String urlString) throws IOException {
    	URL url = new URL(urlString);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder contenido = new StringBuilder();
        String linea;
        while ((linea = reader.readLine()) != null) {
            contenido.append(linea);
        }
        reader.close();
        return contenido.toString();
    }
	
	public static void descomprimirZip() {
	    String archivoZip = pathFile;
        String carpetaDestino = folderZip;

        try {
            File carpetaDestinoFile = new File(carpetaDestino);
            if (!carpetaDestinoFile.exists()) {
                carpetaDestinoFile.mkdirs();
            }

            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(archivoZip));
            ZipEntry entrada;

            while ((entrada = zipInputStream.getNextEntry()) != null) {
                String nombreArchivo = entrada.getName();
                File archivo = new File(carpetaDestino + File.separator + nombreArchivo);
                archivo.setWritable(true);


                // Si la entrada es un directorio, se crea la carpeta
                if (entrada.isDirectory()) {
                    archivo.mkdirs();
                } else {
                    // Si la entrada es un archivo, se crea y se escriben los datos descomprimidos
                    File carpetaArchivo = archivo.getParentFile();
                    if (!carpetaArchivo.exists()) {
                        carpetaArchivo.mkdirs();
                    }

                    byte[] buffer = new byte[1024];
                    FileOutputStream fos = new FileOutputStream(archivo);
                    int leido;
                    while ((leido = zipInputStream.read(buffer)) > 0) {
                        fos.write(buffer, 0, leido);
                    }
                    fos.close();
                }
                zipInputStream.closeEntry();
            }

            zipInputStream.close();

            System.out.println("Descompresión completada.");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
	}
	
	public static void main(String[] args) {
		
		if(args.length == 2) {
			
			nombreApp = args[0];
			if(nombreApp.isEmpty() == false && nombreApp.isBlank() == false) {
				folderZip = nombreApp;
			}
			urlAPI = args[1];
			
			getCampos(folderZip);
			
	
		}
		else if(args.length == 3) {
			nombreApp = args[0];
			if(nombreApp.isEmpty() == false && nombreApp.isBlank() == false) {
				folderZip = nombreApp;
			}
			urlAPI = args[1];
			pathFile = args[2];
			getCampos(folderZip);
		}
		
		else {
			
			System.out.println("Argumentos incorrectos. Prueba escribiendo el nombre de la app seguido de la url de la API y la ruta del archivo con la App de plantilla");
			
		}
                
	}

	private static void editApiFile(String replaceText, String replaceFile ) {
        String lines = "";
        BufferedReader br = null;
        File archivoAEditar = new File(replaceFile);
        if (!archivoAEditar.exists()) {
        	System.out.println("No existe el archivo: " + replaceFile);
        }
        else {
        	System.out.println("Encontrado el archivo: " + replaceFile);
			try {
				br = new BufferedReader(new FileReader(replaceFile));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
	            System.out.println(e.getMessage());
			}
	        String line = "";
	        try {
				while ((line = br.readLine()) != null) {
					if(line.contains("placeholder")) {
						lines += line.replace("placeholder", replaceText) + "\n\t";
					}
					else {
						lines += line + "\n";	
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
	            System.out.println(e.getMessage());
			} catch (NullPointerException e) {
	            System.out.println(e.getMessage());
			}
	        try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
	            System.out.println(e.getMessage());
			}catch (NullPointerException e) {
	            System.out.println(e.getMessage());
			}
	        
	        try (BufferedWriter writer = new BufferedWriter(new FileWriter(replaceFile))) {
	            writer.write(lines);
	        } catch (IOException e) {
	            System.out.println(e.getMessage());
	        }
	        
	        PrintWriter writer = null;
			try {

				writer = new PrintWriter(replaceFile, "UTF-8");
				writer.println(lines);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				writer.close();				
			}
	        
        }
		
	}

}
