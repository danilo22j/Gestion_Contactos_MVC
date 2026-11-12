package Controlador;

import Modelo.Persona;
import Modelo.PersonaDAO;
import Vista.VistaPrincipal;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.*;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class PersonaControlador {

    private VistaPrincipal vista;
    private PersonaDAO dao;
    private TableRowSorter<DefaultTableModel> sorter;

    public PersonaControlador(VistaPrincipal vista, PersonaDAO dao) {
        this.vista = vista;
        this.dao = dao;

        inicializarTablaYSorter();
        cargarContactosDesdeDAO();
        inicializarAccionesFormulario();

        vista.txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                buscarContactosEnSegundoPlano(vista.txtBuscar.getText());
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                buscarContactosEnSegundoPlano(vista.txtBuscar.getText());
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                buscarContactosEnSegundoPlano(vista.txtBuscar.getText());
            }
        });
    }

    private void inicializarTablaYSorter() {
        DefaultTableModel modelo = (DefaultTableModel) vista.tablaContactos.getModel();
        sorter = new TableRowSorter<>(modelo);
        vista.tablaContactos.setRowSorter(sorter);
    }

    private void cargarContactosDesdeDAO() {
        DefaultTableModel modelo = (DefaultTableModel) vista.tablaContactos.getModel();
        modelo.setRowCount(0);

        List<Persona> lista = dao.listar();
        for (Persona p : lista) {
            modelo.addRow(new Object[]{
                p.getNombre(), p.getCelular(), p.getEmail(), p.getCategoria(), p.getFavorito()
            });
        }
    }

    private void inicializarAccionesFormulario() {
        vista.tablaContactos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = vista.tablaContactos.getSelectedRow();
                if (fila != -1) {
                    int filaModelo = vista.tablaContactos.convertRowIndexToModel(fila);
                    if (!dao.bloquearContactoParaEdicion(filaModelo)) {
                        JOptionPane.showMessageDialog(vista,
                                "Recurso bloqueado: Otro usuario está editando un contacto en este momento.");
                        vista.tablaContactos.clearSelection();
                        return;
                    }
                    DefaultTableModel modelo = (DefaultTableModel) vista.tablaContactos.getModel();
                    vista.txtNombre.setText(modelo.getValueAt(filaModelo, 0).toString());
                    vista.txtTelefono.setText(modelo.getValueAt(filaModelo, 1).toString());
                    vista.txtEmail.setText(modelo.getValueAt(filaModelo, 2).toString());
                    vista.cmbCategoria.setSelectedItem(modelo.getValueAt(filaModelo, 3).toString());
                    Object valFav = modelo.getValueAt(filaModelo, 4);
                    vista.chkFavorito.setSelected(valFav != null && Boolean.parseBoolean(valFav.toString()));
                }
            }
        });

        vista.btnModificar.addActionListener(e -> {
            int fila = vista.tablaContactos.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(vista, "Por favor, selecciona primero un contacto en la tabla.");
                return;
            }

            int filaModelo = vista.tablaContactos.convertRowIndexToModel(fila);
            String nombre = vista.txtNombre.getText().trim();
            String celular = vista.txtTelefono.getText().trim();
            String email = vista.txtEmail.getText().trim();
            String categoria = (String) vista.cmbCategoria.getSelectedItem();
            boolean favorito = vista.chkFavorito.isSelected();

            if (nombre.isEmpty() || celular.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Por favor, completa todos los campos.");
                return;
            }
            if (!celular.matches("\\d{10,12}")) {
                JOptionPane.showMessageDialog(vista, "El celular debe tener entre 10 y 12 dígitos numéricos.");
                return;
            }

            // Ejecuta la actualizacion sincronizada en segundo plano
            new Thread(() -> {
                try {
                    dao.actualizar(filaModelo, new Persona(nombre, celular, email, categoria, favorito));

                    SwingUtilities.invokeLater(() -> {
                        cargarContactosDesdeDAO();
                        limpiarCamposFormulario();
                        mostrarNotificacionTiempoReal("Contacto modificado de forma segura");
                    });
                } finally {
                    dao.liberarBloqueoEdicion();
                }
            }).start();
        });

        vista.btnEliminar.addActionListener(e -> eliminarFilaSeleccionada());
        vista.itemEliminarContextual.addActionListener(e -> eliminarFilaSeleccionada());

        vista.tablaContactos.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                evaluarPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                evaluarPopup(e);
            }

            private void evaluarPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = vista.tablaContactos.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < vista.tablaContactos.getRowCount()) {
                        vista.tablaContactos.setRowSelectionInterval(row, row);
                    }
                    vista.popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        vista.btnExportar.addActionListener(e -> ejecutarExportacionConProgreso());
        vista.btnAgregar.addActionListener(e -> registrarContactoConValidacion());
    }

    private void eliminarFilaSeleccionada() {
        int fila = vista.tablaContactos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Selecciona una fila para eliminar.");
            return;
        }

        int filaModelo = vista.tablaContactos.convertRowIndexToModel(fila);

        if (!dao.bloquearContactoParaEdicion(filaModelo)) {
            JOptionPane.showMessageDialog(vista, "El contacto está siendo editado actualmente. No se puede eliminar.");
            return;
        }

        new Thread(() -> {
            try {
                dao.eliminar(filaModelo);

                SwingUtilities.invokeLater(() -> {
                    cargarContactosDesdeDAO();
                    limpiarCamposFormulario();
                    mostrarNotificacionTiempoReal("Contacto eliminado correctamente");
                });
            } finally {
                dao.liberarBloqueoEdicion();
            }
        }).start();
    }

    private void limpiarCamposFormulario() {
        vista.txtNombre.setText("");
        vista.txtTelefono.setText("");
        vista.txtEmail.setText("");
        vista.chkFavorito.setSelected(false);
        if (vista.cmbCategoria.getItemCount() > 0) {
            vista.cmbCategoria.setSelectedIndex(0);
        }
        vista.configurarTextoGuiaBuscador();
        dao.liberarBloqueoEdicion();
    }

    private void ejecutarExportacionConProgreso() {
        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                publish(20);
                Thread.sleep(150); // Simula carga de procesamiento en segundo plano
                publish(50);
                // Ejecuta la escritura sincronizada en el archivo CSV
                dao.guardarEnCSV();
                publish(85);
                Thread.sleep(150);
                publish(100);
                return null;
            }

            @Override
            protected void process(List<Integer> chunks) {
                // Actualiza la barra de progreso en el hilo de interfaz (EDT)
                int progresoActual = chunks.get(chunks.size() - 1);
                vista.progressBar.setValue(progresoActual);
            }

            @Override
            protected void done() {
                vista.progressBar.setValue(0);
                mostrarNotificacionTiempoReal("Exportación CSV completada");
            }
        };
        worker.execute();
    }

    //Metodo para notificaciones seguras en la UI
    private void mostrarNotificacionTiempoReal(String mensaje) {
        new Thread(() -> {
            SwingUtilities.invokeLater(() -> {
                vista.setTitle("Gestión de Contactos - " + mensaje);
            });
            try {
                Thread.sleep(3500); // Mantiene la notificacion visible por 3.5 segundos
            } catch (InterruptedException ignored) {
            }
            SwingUtilities.invokeLater(() -> {
                vista.setTitle("Gestión de Contactos");
            });
        }).start();
    }

//Validacion de duplicados en segundo plano al agregar
    private void registrarContactoConValidacion() {
        String nombre = vista.txtNombre.getText().trim();
        String celularStr = vista.txtTelefono.getText().trim();
        String email = vista.txtEmail.getText().trim();
        String categoria = (String) vista.cmbCategoria.getSelectedItem();
        boolean favorito = vista.chkFavorito.isSelected();

        if (nombre.isEmpty() || celularStr.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Por favor, completa todos los campos.");
            return;
        }

        // Valida que solo tenga números y entre 10 y 12 dígitos
        if (!celularStr.matches("\\d{10,12}")) {
            JOptionPane.showMessageDialog(vista, "El campo Celular debe tener entre 10 y 12 dígitos numéricos.");
            return;
        }

        vista.btnAgregar.setEnabled(false); // Evita doble clic mientras valida

        new Thread(() -> {
            boolean duplicado = dao.existeContacto(nombre, celularStr, email);

            SwingUtilities.invokeLater(() -> {
                vista.btnAgregar.setEnabled(true);
                if (duplicado) {
                    JOptionPane.showMessageDialog(vista, "Error: El contacto ya está registrado (Nombre, Celular o Email duplicado).");
                } else {
                    dao.agregar(new Persona(nombre, celularStr, email, categoria, favorito));
                    cargarContactosDesdeDAO();
                    limpiarCamposFormulario();
                    mostrarNotificacionTiempoReal("Contacto guardado con exito");
                }
            });
        }).start();

        vista.txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                buscarContactosEnSegundoPlano(vista.txtBuscar.getText());
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                buscarContactosEnSegundoPlano(vista.txtBuscar.getText());
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                buscarContactosEnSegundoPlano(vista.txtBuscar.getText());
            }
        });

    }

    //Busqueda en segundo plano con SwingWorker
    private void buscarContactosEnSegundoPlano(String criterio) {
        if (criterio.trim().equals("Buscar contacto...")) {
            return;
        }
        SwingWorker<List<Persona>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Persona> doInBackground() {
                List<Persona> resultados = new ArrayList<>();
                String filtro = criterio.toLowerCase().trim();

                for (Persona p : dao.listar()) {
                    if (p.getNombre().toLowerCase().contains(filtro)
                            || String.valueOf(p.getCelular()).contains(filtro)
                            || p.getEmail().toLowerCase().contains(filtro)
                            || p.getCategoria().toLowerCase().contains(filtro)) {
                        resultados.add(p);
                    }
                }
                return resultados;
            }

            @Override
            protected void done() {
                try {
                    List<Persona> filtrados = get();
                    DefaultTableModel modelo = (DefaultTableModel) vista.tablaContactos.getModel();
                    modelo.setRowCount(0);
                    for (Persona p : filtrados) {
                        modelo.addRow(new Object[]{
                            p.getNombre(), p.getCelular(), p.getEmail(), p.getCategoria(), p.getFavorito()
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void iniciarEdicionContacto() {
        int fila = vista.tablaContactos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Selecciona un contacto de la tabla para editar.");
            return;
        }

        int filaModelo = vista.tablaContactos.convertRowIndexToModel(fila);

        if (!dao.bloquearContactoParaEdicion(filaModelo)) {
            JOptionPane.showMessageDialog(vista, "El contacto está siendo editado actualmente. Intenta más tarde.");
            return;
        }

        // Cargar datos en los campos de texto para editar...
    }

    private void guardarEdicionContacto(int filaSeleccionada, Persona personaEditada) {
        try {
            dao.actualizar(filaSeleccionada, personaEditada);
            cargarContactosDesdeDAO();
            mostrarNotificacionTiempoReal("Contacto modificado con éxito");
        } finally {
            // Garantiza que el bloqueo siempre se libere, incluso si ocFurre un error
            dao.liberarBloqueoEdicion();
        }
    }
}
