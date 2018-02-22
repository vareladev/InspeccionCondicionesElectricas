package Objetos;

/**
 * Created by Varela on 20/02/2018.
 */

public class Hospital {
    private String id;
    private String hospital;

    public Hospital(String id, String hospital) {
        this.id = id;
        this.hospital = hospital;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    //to display object as a string in spinner
    @Override
    public String toString() {
        return hospital;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Hospital){
            Hospital c = (Hospital )obj;
            if(c.getHospital().equals(hospital) && c.getId()==id ) return true;
        }
        return false;
    }
}
