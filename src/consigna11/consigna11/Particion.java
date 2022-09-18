package consigna11;

public class Particion {
    private Integer id;
    private Integer tamanho;
    private Proceso proceso;
    private Integer direccionInicio;
    private Integer direccionFinal;

    public Integer getDireccionInicio() {
        return direccionInicio;
    }

    public void setDireccionInicio(Integer direccionInicio) {
        this.direccionInicio = direccionInicio;
    }

    public Integer getDireccionFinal() {
        return direccionFinal;
    }

    public void setDireccionFinal(Integer direccionFinal) {
        this.direccionFinal = direccionFinal;
    }

    public Particion() {
        super();
    }

    public Particion(Integer id, Integer tamanho, Proceso proceso, Integer direccionInicio, Integer direccionFinal) {
        this.id = id;
        this.tamanho = tamanho;
        this.proceso = proceso;
        this.direccionInicio = direccionInicio;
        this.direccionFinal = direccionFinal;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTamanho() {
        return tamanho;
    }

    public void setTamanho(Integer tamanho) {
        this.tamanho = tamanho;
    }

    public Proceso getProceso() {
        return proceso;
    }

    public void setProceso(Proceso proceso) {
        this.proceso = proceso;
    }

    @Override
    public String toString() {
        return "consigna11.Particion{" +
                "id=" + id +
                ", tamanho=" + tamanho +
                ", proceso=" + proceso +
                '}';
    }
}
