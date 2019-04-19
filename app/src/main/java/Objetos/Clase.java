package Objetos;

/**
 * Created by DEI on 16/04/2019.
 * Define la clase de un equipo médico a inspeccionar
 * tiene dos posibles clases: I y II
 */

public class Clase {
    private int id;
    private String clase;

    public Clase(int id, String clase) {
        this.id = id;
        this.clase = clase;
    }

    public int getId() {
        return id;
    }

    public String getClase() {
        return clase;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    //to display object as a string in spinner
    @Override
    public String toString() {
        return clase;
    }

}
