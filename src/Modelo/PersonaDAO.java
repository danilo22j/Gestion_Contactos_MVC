package Modelo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAO {
    
    private List<Persona> listaPersonas;
    private final String NOMBRE_ARCHIVO = "contactos_dao.csv";

    public PersonaDAO() {
        listaPersonas = new ArrayList<>();
        cargarDesdeCSV();
    }

    public List<Persona> listar() {
        return listaPersonas;
    }

    public void agregar(Persona p) {
        listaPersonas.add(p);
    }

    public void eliminar(int index) {
        if (index >= 0 && index < listaPersonas.size()) {
            listaPersonas.remove(index);
        }
    }
    
    private void guardarEnCSV() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(NOMBRE_ARCHIVO))) {
            bw.write("Nombre,Celular,Email,Categoria,Favorito");
            bw.newLine();

            for (Persona p : listaPersonas) {
                bw.write(p.getNombre() + "," + 
                         p.getCelular() + "," + 
                         p.getEmail() + "," + 
                         p.getCategoria() + "," + 
                         p.getFavorito());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo CSV: " + e.getMessage());
        }
    }
    private void cargarDesdeCSV() {
        File archivo = new File(NOMBRE_ARCHIVO);
        if (!archivo.exists()) return; 

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean esCabecera = true;
            
            while ((linea = br.readLine()) != null) {
                if (esCabecera) {
                    esCabecera = false;
                    continue;
                }

                String[] datos = linea.split(",");
                if (datos.length == 5) {
                    String nombre = datos[0].trim();
                    int celular = Integer.parseInt(datos[1].trim());
                    String email = datos[2].trim();
                    String categoria = datos[3].trim();
                    boolean favorito = Boolean.parseBoolean(datos[4].trim());

                    listaPersonas.add(new Persona(nombre, celular, email, categoria, favorito));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error al procesar y leer el archivo CSV: " + e.getMessage());
        }
    }
}
