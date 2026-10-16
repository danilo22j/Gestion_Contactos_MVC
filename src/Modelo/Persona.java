package Modelo;

public class Persona {
    
    private String Nombre;
    private int Celular;
    private String Email;
    private String Categoria;
    private Boolean Favorito;

    public Persona() {
    }

    public Persona(String Nombre, int Celular, String Email, String Categoria, Boolean Favorito) {
        this.Nombre = Nombre;
        this.Celular = Celular;
        this.Email = Email;
        this.Categoria = Categoria;
        this.Favorito = Favorito;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

    public int getCelular() {
        return Celular;
    }

    public void setCelular(int Celular) {
        this.Celular = Celular;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getCategoria() {
        return Categoria;
    }

    public void setCategoria(String Categoria) {
        this.Categoria = Categoria;
    }

    public Boolean getFavorito() {
        return Favorito;
    }

    public void setFavorito(Boolean Favorito) {
        this.Favorito = Favorito;
    }
    
    public String datosContacto(){
        String contacto = String.format("%s,%s,%s,%s,%s",Nombre,Celular,Email,Categoria,Favorito);
        return contacto;
    }
    
    public String formatoList(){
        String contacto = String.format("%-40s,%-40s,%-40s,%-40s,%-40s",Nombre,Celular,Email,Categoria,Favorito);
        return contacto;
    }
}
