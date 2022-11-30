package tpi.entidades;

public class Particion {
    private String id;
    private Integer tamanho;
    private Proceso proceso;
    private Integer direccionInicio;
    private Integer direccionFinal;
    private Integer fragmentacionInterna;
    
    
    public Integer getFragmentacionInterna() {
		return fragmentacionInterna;
	}

	public void setFragmentacionInterna(Integer fragmentacionInterna) {
		this.fragmentacionInterna = fragmentacionInterna;
	}

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

    public Particion(String id, Integer tamanho, Proceso proceso, Integer direccionInicio, Integer direccionFinal) {
        this.id = id;
        this.tamanho = tamanho;
        this.proceso = proceso;
        this.direccionInicio = direccionInicio;
        this.direccionFinal = direccionFinal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
		return "Particion [id=" + id + ", tamanho=" + tamanho + ", proceso=" + proceso + ", direccionInicio="
				+ direccionInicio + ", direccionFinal=" + direccionFinal + "]" + "\n";
	}


}
