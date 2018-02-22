package Objetos;

import android.graphics.Bitmap;

/**
 * Created by Varela on 21/02/2018.
 */

public class Area {

    private String id;
    private String area;
    private Bitmap plano;
    private String idHospital;

    public Area(String id, String area, Bitmap plano, String idHospital) {
        this.id = id;
        this.area = area;
        this.plano = plano;
        this.idHospital = idHospital;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Bitmap getPlano() {
        return plano;
    }

    public void setPlano(Bitmap plano) {
        this.plano = plano;
    }

    public String getIdHospital() {
        return idHospital;
    }

    public void setIdHospital(String idHospital) {
        this.idHospital = idHospital;
    }

    //to display object as a string in spinner
    @Override
    public String toString() {
        return area;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Area){
            Area c = (Area )obj;
            if(c.getArea().equals(area) && c.getId()==id ) return true;
        }
        return false;
    }
}
