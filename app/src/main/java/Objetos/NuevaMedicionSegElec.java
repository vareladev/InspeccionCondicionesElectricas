package Objetos;

/**
 * Created by DEI on 20/04/2019.
 */

public class NuevaMedicionSegElec {
    private int id_segelectrica;
    private int id_bloque;
    private int id_pregunta;
    private float medicion;
    private float estandar;
    private String magnitud;
    private String comentario;
    private String valoracion;

    //constructor
    public NuevaMedicionSegElec() {
    }
    //constructor
    public NuevaMedicionSegElec(int id_segelectrica, int id_bloque, int id_pregunta, float medicion, float estandar, String magnitud, String comentario, String valoracion) {
        this.id_segelectrica = id_segelectrica;
        this.id_bloque = id_bloque;
        this.id_pregunta = id_pregunta;
        this.medicion = medicion;
        this.estandar = estandar;
        this.magnitud = magnitud;
        this.comentario = comentario;
        this.valoracion = valoracion;
    }

    public int getId_segelectrica() {
        return id_segelectrica;
    }

    public int getId_bloque() {
        return id_bloque;
    }

    public int getId_pregunta() {
        return id_pregunta;
    }

    public float getMedicion() {
        return medicion;
    }

    public float getEstandar() {
        return estandar;
    }

    public String getMagnitud() {
        return magnitud;
    }

    public String getComentario() {
        return comentario;
    }

    public String getValoracion() {
        return valoracion;
    }

    @Override
    public String toString() {
        return "NuevaMedicionSegElec{" +
                "id_segelectrica=" + id_segelectrica +
                ", id_bloque=" + id_bloque +
                ", id_pregunta=" + id_pregunta +
                ", medicion=" + medicion +
                ", estandar=" + estandar +
                ", magnitud='" + magnitud + '\'' +
                ", comentario='" + comentario + '\'' +
                ", valoracion='" + valoracion + '\'' +
                '}';
    }
}
