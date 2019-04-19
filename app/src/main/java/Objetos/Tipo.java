package Objetos;

/**
 * Created by DEI on 16/04/2019.
 * Define la clase de un equipo médico a inspeccionar
 * tiene dos posibles clases: I y II
 */

public class Tipo {
    private int id;
    private String tipo;

    public Tipo(int id, String clase) {
        this.id = id;
        this.tipo = clase;
    }

    public int getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTipo(String clase) {
        this.tipo = clase;
    }

    //to display object as a string in spinner
    @Override
    public String toString() {
        return tipo;
    }

}
