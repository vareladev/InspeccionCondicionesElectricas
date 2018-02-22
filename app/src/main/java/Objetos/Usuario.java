package Objetos;

/**
 * Created by Varela on 21/02/2018.
 */

public class Usuario {
    private String id;
    private String dui;
    private String nombre;

    public Usuario(String id, String dui, String nombre) {
        this.id = id;
        this.dui = dui;
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id='" + id + '\'' +
                ", dui='" + dui + '\'' +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
