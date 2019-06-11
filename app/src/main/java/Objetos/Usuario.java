package Objetos;

/**
 * Created by Varela on 21/02/2018.
 */

public class Usuario {
    private int id;
    private String nick;
    private String pass;
    private String nombre;
    private String correo;
    private int id_universidad;

    public Usuario() {
    }

    public Usuario(int id, String nick, String nombre, String correo, int id_universidad) {
        this.id = id;
        this.nick = nick;
        this.nombre = nombre;
        this.correo = correo;
        this.id_universidad = id_universidad;
    }

    public int getId() {
        return id;
    }

    public String getNick() {
        return nick;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public int getId_universidad() {
        return id_universidad;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setId_universidad(int id_universidad) {
        this.id_universidad = id_universidad;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nick='" + nick + '\'' +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
