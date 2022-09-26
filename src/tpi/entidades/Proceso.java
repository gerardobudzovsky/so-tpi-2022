package tpi.entidades;

import tpi.constantes.Estado;

public class Proceso {

    private String id;
    private Integer tamanho;
    private Integer tiempoDeArribo;
    private Integer tiempoDeIrrupcion;
    private Estado estado;

    public Integer getTamanho() {
		return tamanho;
	}

	public void setTamanho(Integer tamanho) {
		this.tamanho = tamanho;
	}

	public Integer getTiempoDeArribo() {
		return tiempoDeArribo;
	}

	public void setTiempoDeArribo(Integer tiempoDeArribo) {
		this.tiempoDeArribo = tiempoDeArribo;
	}

	public Integer getTiempoDeIrrupcion() {
		return tiempoDeIrrupcion;
	}

	public void setTiempoDeIrrupcion(Integer tiempoDeIrrupcion) {
		this.tiempoDeIrrupcion = tiempoDeIrrupcion;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Proceso() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

//	@Override
//	public String toString() {
//		return "Proceso [id=" + id + ", tamanho=" + tamanho + ", tiempoDeArribo=" + tiempoDeArribo
//				+ ", tiempoDeIrrupcion=" + tiempoDeIrrupcion + ", estado=" + estado + "]"+"\n";
//	}
	
	@Override
	public String toString() {
		return this.id;
	}
    

}
