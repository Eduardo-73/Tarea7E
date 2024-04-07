/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daw;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author edu
 */
public class Main {

    public static void main(String[] args) throws JAXBException, IOException {
        String rutaJSON = "./aplicacionesJSON";
        List<App> aplicaciones = generarListaApp(10);
        escribirFicheroXML("aplicacionXML.xml", aplicaciones);
        escribirFicheroJSON("aplicacionesJSON.json", aplicaciones);
        crearDirectorio(rutaJSON);
        //generarFicherosJSON(rutaJSON, aplicaciones);
        System.out.println("Mostrar archivos XML\n");
        lecturaFicheroXML("aplicacionXML.xml");
        System.out.println("Mostrar archivos JSON\n");
        lecturaFicheroJSON("aplicacionesJSON.json");
        System.out.println("Listar Directorios");
        listarDirecorio(rutaJSON);
        preguntarUsuarioFichero();
    }

    private static List<App> generarListaApp(int numeroApps) {
        List<App> lista = new ArrayList();
        for (int i = 0; i < numeroApps; i++) {
            lista.add(new App());
        }
        return lista;
    }

    private static void crearDirectorio(String ruta) {
        Path direccion = Paths.get(ruta);
        try {
            Files.createDirectory(direccion);
        } catch (FileAlreadyExistsException faee) {
            System.out.println("No se puede crear " + ruta + " porque ya existe");
        } catch (AccessDeniedException ade) {
            System.out.println("No tiene permisos para crear " + ruta);
        } catch (IOException e) {
            System.out.println("Problema creando el directorio " + ruta);
            System.out.println("Seguramente la ruta está mal escrita o no existe");
        }
    }

    private static void escribirFicheroXML(String ruta, List<App> lista) throws JAXBException {
        CatalogoApp catalogo = new CatalogoApp();
        catalogo.setLista(lista);
        catalogo.setDescripcion("Mi catalogo");
        JAXBContext contexto = JAXBContext.newInstance(CatalogoApp.class);
        Marshaller serializador = contexto.createMarshaller();
        serializador.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        serializador.marshal(catalogo, new File(ruta));
    }

    private static void escribirFicheroJSON(String ruta, List<App> lista) throws IOException {
        ObjectMapper mapeador = new ObjectMapper();
        mapeador.registerModule(new JavaTimeModule());
        mapeador.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapeador.writeValue(new File(ruta), lista);
    }

    private static void generarFicherosJSON(String ruta, List<App> lista) throws IOException {
        ObjectMapper mapeador = new ObjectMapper();
        for (App app : lista) {
            mapeador.registerModule(new JavaTimeModule());
            mapeador.configure(SerializationFeature.INDENT_OUTPUT, true);
            mapeador.writeValue(new File(ruta + "/" + app.getNombre()), app);
        }
    }

    private static void lecturaFicheroXML(String ruta) throws JAXBException {
        JAXBContext contexto = JAXBContext.newInstance(CatalogoApp.class);
        Unmarshaller um = contexto.createUnmarshaller();
        CatalogoApp catalogo = (CatalogoApp) um.unmarshal(new File(ruta));
        List<App> lista = catalogo.getListaMuebles();
        lista.forEach(System.out::println);
    }

    private static void lecturaFicheroJSON(String ruta) throws IOException {
        ObjectMapper mapeador = new ObjectMapper();
        mapeador.registerModule(new JavaTimeModule());

        List<App> catalogo = mapeador.readValue(new File(ruta),
                mapeador.getTypeFactory().constructCollectionType(
                        List.class, App.class));
        for (App app : catalogo) {
            System.out.println(app);
        }
    }

    private static void listarDirecorio(String ruta) {
        File fila = new File(ruta);
        if (fila.exists()) {
            File[] fichero = fila.listFiles();
            for (File file2 : fichero) {
                System.out.println(file2.getName());
            }
        } else {
            System.out.println("El directorio a lista no existe");
        }
    }

    private static void preguntarUsuarioFichero() throws IOException{
        Scanner teclado = new Scanner(System.in);
        ObjectMapper mapeador = new ObjectMapper();
        System.out.println("Dime el nombre del fichero");
        String nombreFichero = teclado.nextLine();
        App app = mapeador.readValue(new File("./aplicacionesJSON/" + nombreFichero)
                ,App.class);
        System.out.println(app);
    }
    
    public static void borrarElemento(String ruta) {
        Path file = Paths.get(ruta);
        try {
            Files.delete(file);
        } catch (NoSuchFileException nsfe) {
            System.out.println("No se puede borrar " + ruta + " porque no existe");
        } catch (DirectoryNotEmptyException dnee) {
            System.out.println("No se puede borrar el directorio porque no está vacío");
        } catch (IOException e) {
            System.out.println("Problema borrando el elemento " + ruta);
        }
    }
}
