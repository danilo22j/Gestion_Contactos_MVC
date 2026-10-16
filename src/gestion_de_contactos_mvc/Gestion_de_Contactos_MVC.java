package gestion_de_contactos_mvc;

import Modelo.PersonaDAO;
import Vista.VistaPrincipal;
import Controlador.PersonaControlador;
import javax.swing.SwingUtilities;

public class Gestion_de_Contactos_MVC {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VistaPrincipal vista = new VistaPrincipal();
            PersonaDAO dao = new PersonaDAO();
            new PersonaControlador(vista, dao);

            vista.setLocationRelativeTo(null);
            vista.setVisible(true);
        });
    }
}
