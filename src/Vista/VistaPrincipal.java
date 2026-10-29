package Vista;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JOptionPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.util.Locale;
import java.util.ResourceBundle;

public class VistaPrincipal extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VistaPrincipal.class.getName());

    public javax.swing.JTextField txtBuscar;
    public javax.swing.JPopupMenu popupMenu;
    public javax.swing.JMenuItem itemEliminarContextual;

    public VistaPrincipal() {
        initComponents();
        inicializarComponentesExtras();

        Font fuenteEncabezado = new Font("Bookman Old Style", Font.BOLD, 14);
        tablaContactos.getTableHeader().setFont(fuenteEncabezado);
        tablaContactos.getTableHeader().setForeground(new Color(25, 23, 109));

        ((AbstractDocument) txtTelefono.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string == null) {
                    return;
                }

                // Validar que el texto entrante contenga SOLO numeros y que no supere el limite de 12
                if (string.matches("\\d+") && (fb.getDocument().getLength() + string.length() <= 12)) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text == null) {
                    return;
                }

                // Validar al escribir o pegar texto: SOLO numeros y longitud máxima de 12
                if (text.matches("\\d+") && (fb.getDocument().getLength() - length + text.length() <= 12)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        txtTelefono.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                String texto = txtTelefono.getText().trim();
                // Solo validamos si el usuario escribió algo para no molestar si está vacío
                if (!texto.isEmpty() && texto.length() < 10) {
                    JOptionPane.showMessageDialog(VistaPrincipal.this,
                            "¡Atención! Un número de teléfono válido debe tener entre 10 y 12 dígitos.",
                            "Formato Incorrecto",
                            JOptionPane.WARNING_MESSAGE);
                    txtTelefono.requestFocus();
                }
            }
        });
    }

    public void cambiarIdioma(String idioma, String pais) {
        Locale locale = new Locale(idioma, pais);
        ResourceBundle bundle = ResourceBundle.getBundle("Recursos.mensajes", locale);

        // Titulo de la ventana
        try {
            this.setTitle(bundle.getString("titulo"));
        } catch (Exception e) {
        }

        // Traduccion segura de etiquetas e interfaz
        try {
            jLabel1.setText(bundle.getString("lbl_nombre"));
        } catch (Exception e) {
            System.out.println("Falta clave lbl_nombre en idioma: " + idioma);
        }
        try {
            jLabel2.setText(bundle.getString("lbl_celular"));
        } catch (Exception e) {
            System.out.println("Falta clave lbl_celular en idioma: " + idioma);
        }
        try {
            jLabel3.setText(bundle.getString("lbl_email"));
        } catch (Exception e) {
            System.out.println("Falta clave lbl_email en idioma: " + idioma);
        }

        // Traduccion de la tabla
        try {
            javax.swing.table.TableColumnModel colModel = tablaContactos.getColumnModel();
            colModel.getColumn(0).setHeaderValue(bundle.getString("lbl_nombre").replace(":", ""));
            colModel.getColumn(1).setHeaderValue(bundle.getString("lbl_celular").replace(":", ""));
            colModel.getColumn(2).setHeaderValue(bundle.getString("lbl_email").replace(":", ""));
            colModel.getColumn(3).setHeaderValue(bundle.getString("lbl_categoria"));
            colModel.getColumn(4).setHeaderValue(bundle.getString("lbl_favorito"));
            tablaContactos.getTableHeader().repaint();
        } catch (Exception e) {
            System.out.println("Error en columnas de tabla: " + e.getMessage());
        }
        try {
            //Guardar el indice que el usuario tenia seleccionado actualmente para no perderlo
            int indiceSeleccionado = cmbCategoria.getSelectedIndex();
            
            //Limpiar todos los elementos viejos del meni desplegable
            cmbCategoria.removeAllItems();
            
            //Insertar los nuevos elementos traducidos desde los archivos properties
            cmbCategoria.addItem(bundle.getString("cat_familia"));
            cmbCategoria.addItem(bundle.getString("cat_amigo"));
            cmbCategoria.addItem(bundle.getString("cat_trabajo"));
            
            //Restaurar la seleccion previa si sigue estando en el rango valido
            if (indiceSeleccionado >= 0 && indiceSeleccionado < cmbCategoria.getItemCount()) {
                cmbCategoria.setSelectedIndex(indiceSeleccionado);
            }
        } catch (Exception e) {
            System.out.println("Error al traducir las categorías: " + e.getMessage());
        }
        try {
            // Traduce el texto que acompaña al cuadro de selección
            chkFavorito.setText(bundle.getString("lbl_contacto_favorito"));
        } catch (Exception e) {
            System.out.println("Error al traducir el checkbox favorito: " + e.getMessage());
        }
    }

    private void inicializarComponentesExtras() {
        // Creación del menu contextual para el clic derecho sobre la tabla
        popupMenu = new javax.swing.JPopupMenu();
        itemEliminarContextual = new javax.swing.JMenuItem("Eliminar fila seleccionada");
        popupMenu.add(itemEliminarContextual);

        // Inicializacion de las variables de la barra de progreso y buscador
        txtBuscar = new javax.swing.JTextField(15);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        chkFavorito = new javax.swing.JCheckBox();
        cmbCategoria = new javax.swing.JComboBox<>();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnAgregar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtNombre = new javax.swing.JTextPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtTelefono = new javax.swing.JTextPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtEmail = new javax.swing.JTextPane();
        btnExportar = new javax.swing.JButton();
        progressBar = new javax.swing.JProgressBar();
        cmbIdioma = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaContactos = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane1.setBackground(new java.awt.Color(153, 153, 153));
        jTabbedPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jTabbedPane1.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jTabbedPane1.setName(""); // NOI18N
        jTabbedPane1.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
                jTabbedPane1AncestorRemoved(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(153, 153, 153));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setForeground(new java.awt.Color(153, 153, 153));

        jLabel1.setFont(new java.awt.Font("Bookman Old Style", 2, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(25, 23, 109));
        jLabel1.setText("Nombre:");

        jLabel2.setFont(new java.awt.Font("Bookman Old Style", 2, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(25, 23, 109));
        jLabel2.setText("Celular: ");

        jLabel3.setFont(new java.awt.Font("Bookman Old Style", 2, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(25, 23, 109));
        jLabel3.setText("Email:");

        chkFavorito.setBackground(new java.awt.Color(153, 153, 153));
        chkFavorito.setFont(new java.awt.Font("Bookman Old Style", 2, 14)); // NOI18N
        chkFavorito.setForeground(new java.awt.Color(25, 23, 109));
        chkFavorito.setText("Contacto Favorito");
        chkFavorito.setName("chkFavorito"); // NOI18N
        chkFavorito.addActionListener(this::chkFavoritoActionPerformed);

        cmbCategoria.setBackground(new java.awt.Color(204, 204, 204));
        cmbCategoria.setFont(new java.awt.Font("Bookman Old Style", 2, 14)); // NOI18N
        cmbCategoria.setForeground(new java.awt.Color(25, 23, 109));
        cmbCategoria.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Familia", "Amigo", "Trabajo" }));
        cmbCategoria.setName("cbxCategoria"); // NOI18N
        cmbCategoria.addActionListener(this::cmbCategoriaActionPerformed);

        btnModificar.setBackground(new java.awt.Color(204, 204, 204));
        btnModificar.setForeground(new java.awt.Color(0, 204, 204));
        btnModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagen/actualizar-datos.png"))); // NOI18N
        btnModificar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 153)));
        btnModificar.setName("btnModificar"); // NOI18N
        btnModificar.addActionListener(this::btnModificarActionPerformed);

        btnEliminar.setBackground(new java.awt.Color(204, 204, 204));
        btnEliminar.setForeground(new java.awt.Color(0, 204, 204));
        btnEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagen/eliminar-contacto.png"))); // NOI18N
        btnEliminar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 153)));
        btnEliminar.setName("btnEliminar"); // NOI18N
        btnEliminar.addActionListener(this::btnEliminarActionPerformed);

        btnAgregar.setBackground(new java.awt.Color(204, 204, 204));
        btnAgregar.setForeground(new java.awt.Color(0, 204, 204));
        btnAgregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagen/directorio-telefonico.png"))); // NOI18N
        btnAgregar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 153)));
        btnAgregar.setName("btnRegistrar"); // NOI18N
        btnAgregar.addActionListener(this::btnAgregarActionPerformed);

        jScrollPane2.setBackground(new java.awt.Color(153, 153, 153));

        txtNombre.setBackground(new java.awt.Color(204, 204, 204));
        txtNombre.setFont(new java.awt.Font("Bookman Old Style", 0, 14)); // NOI18N
        txtNombre.setName("txtNombre"); // NOI18N
        jScrollPane2.setViewportView(txtNombre);

        txtTelefono.setBackground(new java.awt.Color(204, 204, 204));
        txtTelefono.setFont(new java.awt.Font("Bookman Old Style", 0, 14)); // NOI18N
        txtTelefono.setMaximumSize(new java.awt.Dimension(1, 9));
        txtTelefono.setMinimumSize(new java.awt.Dimension(1, 9));
        txtTelefono.setName("txtCelular"); // NOI18N
        jScrollPane3.setViewportView(txtTelefono);

        txtEmail.setBackground(new java.awt.Color(204, 204, 204));
        txtEmail.setFont(new java.awt.Font("Bookman Old Style", 0, 14)); // NOI18N
        txtEmail.setName("txtEmail"); // NOI18N
        jScrollPane4.setViewportView(txtEmail);

        btnExportar.setBackground(new java.awt.Color(204, 204, 204));
        btnExportar.setForeground(new java.awt.Color(0, 204, 204));
        btnExportar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagen/exportar-archivo.png"))); // NOI18N
        btnExportar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 153)));
        btnExportar.setName("btnEliminar"); // NOI18N

        cmbIdioma.setBackground(new java.awt.Color(204, 204, 204));
        cmbIdioma.setFont(new java.awt.Font("Bookman Old Style", 2, 12)); // NOI18N
        cmbIdioma.setForeground(new java.awt.Color(25, 23, 109));
        cmbIdioma.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ES", "US", "FR" }));
        cmbIdioma.addActionListener(this::cmbIdiomaActionPerformed);

        tablaContactos.setBackground(new java.awt.Color(153, 153, 153));
        tablaContactos.setFont(new java.awt.Font("Bookman Old Style", 0, 14)); // NOI18N
        tablaContactos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Nombre", "Celular", "Email", "Categoria", "Favorito"
            }
        ));
        jScrollPane1.setViewportView(tablaContactos);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(104, 104, 104))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(chkFavorito)
                                .addGap(36, 36, 36)
                                .addComponent(cmbCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel1)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(jLabel3)))
                                .addGap(33, 33, 33)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jScrollPane4)
                                    .addComponent(jScrollPane3)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbIdioma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnExportar, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 551, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbIdioma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnExportar, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jLabel1)))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkFavorito)
                            .addComponent(cmbCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Registrar Contacto", jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTabbedPane1AncestorRemoved(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_jTabbedPane1AncestorRemoved
        // TODO add your handling code here:
    }//GEN-LAST:event_jTabbedPane1AncestorRemoved

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAgregarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnModificarActionPerformed

    private void cmbCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCategoriaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbCategoriaActionPerformed

    private void chkFavoritoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFavoritoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkFavoritoActionPerformed

    private void cmbIdiomaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbIdiomaActionPerformed
        int seleccion = cmbIdioma.getSelectedIndex();

        switch (seleccion) {
            case 0:
                cambiarIdioma("es", "ES"); // Carga mensajes_es.properties
                break;
            case 1:
                cambiarIdioma("en", "US"); // Carga mensajes_en.properties
                break;
            case 2:
                cambiarIdioma("fr", "FR"); // Carga mensajes_fr.properties
                break;
        }
    }//GEN-LAST:event_cmbIdiomaActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new VistaPrincipal().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton btnAgregar;
    public javax.swing.JButton btnEliminar;
    public javax.swing.JButton btnExportar;
    public javax.swing.JButton btnModificar;
    public javax.swing.JCheckBox chkFavorito;
    public javax.swing.JComboBox<String> cmbCategoria;
    public javax.swing.JComboBox<String> cmbIdioma;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    public javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    public javax.swing.JTabbedPane jTabbedPane1;
    public javax.swing.JProgressBar progressBar;
    public javax.swing.JTable tablaContactos;
    public javax.swing.JTextPane txtEmail;
    public javax.swing.JTextPane txtNombre;
    public javax.swing.JTextPane txtTelefono;
    // End of variables declaration//GEN-END:variables
}
