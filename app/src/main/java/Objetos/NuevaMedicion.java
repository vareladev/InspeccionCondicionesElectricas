package Objetos;

/**
 * Created by Varela on 22/02/2018.
 */

public class NuevaMedicion {
    private String idUsuario;
    private String idArea;
    private String servicioAnalizado;
    private String responsable;
    private String telefono;

    public NuevaMedicion(String idUsuario, String idArea, String servicioAnalizado, String responsable, String telefono) {
        this.idUsuario = idUsuario;
        this.idArea = idArea;
        this.servicioAnalizado = servicioAnalizado;
        this.responsable = responsable;
        this.telefono = telefono;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdArea() {
        return idArea;
    }

    public void setIdArea(String idArea) {
        this.idArea = idArea;
    }

    public String getServicioAnalizado() {
        return servicioAnalizado;
    }

    public void setServicioAnalizado(String servicioAnalizado) {
        this.servicioAnalizado = servicioAnalizado;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return "NuevaMedicion{" +
                "idUsuario='" + idUsuario + '\'' +
                ", idArea='" + idArea + '\'' +
                ", servicioAnalizado='" + servicioAnalizado + '\'' +
                ", responsable='" + responsable + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }
}
