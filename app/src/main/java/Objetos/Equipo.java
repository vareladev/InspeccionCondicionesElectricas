package Objetos;

/**
 * Created by Varela on 20/02/2018.
 */

public class Equipo {
    private String id;
    private String equipo;

    public Equipo(String id, String equipo) {
        this.id = id;
        this.equipo = equipo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEquipo() {
        return equipo;
    }

    public void setEquipo(String equipo) {
        this.equipo = equipo;
    }

    //to display object as a string in spinner
    @Override
    public String toString() {
        return equipo;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Equipo){
            Equipo c = (Equipo )obj;
            if(c.getEquipo().equals(equipo) && c.getId()==id ) return true;
        }
        return false;
    }
}
