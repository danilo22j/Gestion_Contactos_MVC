package Controlador;

import Modelo.Persona;
import Modelo.PersonaDAO;
import Vista.VistaPrincipal;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.*;
import java.io.FileWriter;
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
        vista.btnAgregar.addActionListener(e -> {
            try {
                String nombre = vista.txtNombre.getText().trim();
                String celularStr = vista.txtTelefono.getText().trim();
                String email = vista.txtEmail.getText().trim();
                String categoria = (String) vista.cmbCategoria.getSelectedItem();
                boolean favorito = vista.chkFavorito.isSelected();

                if (nombre.isEmpty() || celularStr.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(vista, "Por favor, completa todos los campos.");
                    return;
                }

                int celular = Integer.parseInt(celularStr);

                dao.agregar(new Persona(nombre, celular, email, categoria, favorito));
                cargarContactosDesdeDAO();
                limpiarCamposFormulario();

                JOptionPane.showMessageDialog(vista, "¡Contacto registrado con éxito!");
                vista.jTabbedPane1.setSelectedIndex(0);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(vista, "El campo Celular debe contener únicamente números.");
            }
        });
        vista.tablaContactos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = vista.tablaContactos.getSelectedRow();
                if (fila != -1) {
                    int filaModelo = vista.tablaContactos.convertRowIndexToModel(fila);
                    DefaultTableModel modelo = (DefaultTableModel) vista.tablaContactos.getModel();

                    vista.txtNombre.setText(modelo.getValueAt(filaModelo, 0).toString());
                    vista.txtTelefono.setText(modelo.getValueAt(filaModelo, 1).toString());
                    vista.txtEmail.setText(modelo.getValueAt(filaModelo, 2).toString());
                    vista.cmbCategoria.setSelectedItem(modelo.getValueAt(filaModelo, 3).toString());
                    vista.chkFavorito.setSelected((Boolean) modelo.getValueAt(filaModelo, 4));
                }
            }
        });

        vista.btnModificar.addActionListener(e -> {
            int fila = vista.tablaContactos.getSelectedRow();
            if (fila != -1) {
                int filaModelo = vista.tablaContactos.convertRowIndexToModel(fila);
                try {
                    Persona p = dao.listar().get(filaModelo);

                    p.setNombre(vista.txtNombre.getText().trim());
                    p.setCelular(Integer.parseInt(vista.txtTelefono.getText().trim()));
                    p.setEmail(vista.txtEmail.getText().trim());
                    p.setCategoria((String) vista.cmbCategoria.getSelectedItem());
                    p.setFavorito(vista.chkFavorito.isSelected());

                    cargarContactosDesdeDAO();
                    limpiarCamposFormulario();
                    JOptionPane.showMessageDialog(vista, "Contacto modificado correctamente.");
                    vista.jTabbedPane1.setSelectedIndex(0);

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(vista, "Verifica el formato numérico del Celular.");
                }
            } else {
                JOptionPane.showMessageDialog(vista, "Por favor, selecciona primero un contacto en la tabla.");
            }
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
    }

    private void eliminarFilaSeleccionada() {
        int fila = vista.tablaContactos.getSelectedRow();
        if (fila != -1) {
            int filaModelo = vista.tablaContactos.convertRowIndexToModel(fila);
            dao.eliminar(filaModelo);
            cargarContactosDesdeDAO();
            limpiarCamposFormulario();
            JOptionPane.showMessageDialog(vista, "Contacto eliminado correctamente.");
        } else {
            JOptionPane.showMessageDialog(vista, "Selecciona una fila para eliminar.");
        }
    }

    private void limpiarCamposFormulario() {
        vista.txtNombre.setText("");
        vista.txtTelefono.setText("");
        vista.txtEmail.setText("");
        vista.chkFavorito.setSelected(false);
        if (vista.cmbCategoria.getItemCount() > 0) {
            vista.cmbCategoria.setSelectedIndex(0);
        }
    }

    private void ejecutarExportacionConProgreso() {
        vista.btnExportar.setEnabled(false);

        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 0; i <= 100; i += 25) {
                    Thread.sleep(100);
                    publish(i);
                }

                DefaultTableModel modelo = (DefaultTableModel) vista.tablaContactos.getModel();
                try (FileWriter writer = new FileWriter("contactos_dao.csv")) {
                    writer.append("Nombre,Celular,Email,Categoria,Favorito\n");
                    for (int i = 0; i < modelo.getRowCount(); i++) {
                        writer.append(modelo.getValueAt(i, 0).toString()).append(",")
                                .append(modelo.getValueAt(i, 1).toString()).append(",")
                                .append(modelo.getValueAt(i, 2).toString()).append(",")
                                .append(modelo.getValueAt(i, 3).toString()).append(",")
                                .append(modelo.getValueAt(i, 4).toString()).append("\n");
                    }
                }
                return null;
            }

            @Override
            protected void process(List<Integer> chunks) {
                vista.progressBar.setValue(chunks.get(chunks.size() - 1));
            }

            @Override
            protected void done() {
                vista.btnExportar.setEnabled(true);
                vista.progressBar.setValue(0);
                JOptionPane.showMessageDialog(vista, "¡Archivo CSV 'contactos_dao.csv' creado exitosamente!");
            }
        };
        worker.execute();
    }
}
