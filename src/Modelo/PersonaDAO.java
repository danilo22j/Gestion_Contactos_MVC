package Modelo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class PersonaDAO {

    private List<Persona> listaPersonas;
    private final String NOMBRE_ARCHIVO = "contactos_dao.csv";

    private final Object lockArchivo = new Object();
    private final ReentrantLock lockEdicion = new ReentrantLock();

    private int indiceEnEdicion = -1;

    public PersonaDAO() {
        listaPersonas = new ArrayList<>();
        cargarDesdeCSV();
    }

    public synchronized List<Persona> listar() {
        return new ArrayList<>(listaPersonas);
    }

    public synchronized void agregar(Persona p) {
        listaPersonas.add(p);
    }

    public synchronized void eliminar(int index) {
        if (index >= 0 && index < listaPersonas.size()) {
            listaPersonas.remove(index);
        }
    }

    public void guardarEnCSV() {
        synchronized (lockArchivo) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(NOMBRE_ARCHIVO))) {
                bw.write("Nombre,Celular,Email,Categoria,Favorito");
                bw.newLine();
                for (Persona p : listar()) {
                    bw.write(p.getNombre() + "," + p.getCelular() + ","
                            + p.getEmail() + "," + p.getCategoria() + "," + p.getFavorito());
                    bw.newLine();
                }
            } catch (IOException e) {
                System.out.println("Error al escribir en el archivo CSV: " + e.getMessage());
            }
        }
    }

    private void cargarDesdeCSV() {
        synchronized (lockArchivo) {
            File archivo = new File(NOMBRE_ARCHIVO);
            if (!archivo.exists()) {
                return;
            }

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
                        String celular = datos[1].trim();
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

    //Verificar duplicados de forma segura
    public synchronized boolean existeContacto(String nombre, String celular, String email) {
        for (Persona p : listaPersonas) {
            if (p.getNombre().equalsIgnoreCase(nombre)
                    || p.getCelular().equals(celular)
                    || p.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    //Sincronizacion al modificar y eliminar
    public synchronized void actualizar(int index, Persona pActualizada) {
        if (index >= 0 && index < listaPersonas.size()) {
            listaPersonas.set(index, pActualizada);
            guardarEnCSV();
        }
    }

    //Mecanismo de bloqueo de recursos para edicion unica
    public synchronized boolean bloquearContactoParaEdicion(int index) {
        if (lockEdicion.isLocked() && indiceEnEdicion != index) {
            return false; // Ya hay otro usuario/hilo editando un contacto
        }
        if (!lockEdicion.isHeldByCurrentThread()) {
            if (!lockEdicion.tryLock()) {
                return false;
            }
        }
        indiceEnEdicion = index;
        return true;
    }

// Libera el bloqueo cuando termina la edición o se cancela
    public synchronized void liberarBloqueoEdicion() {
        indiceEnEdicion = -1;
        if (lockEdicion.isHeldByCurrentThread()) {
            lockEdicion.unlock();
        }
    }

}
