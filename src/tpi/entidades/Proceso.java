package tpi.entidades;

import tpi.constantes.Estado;

public class Proceso {

    private String id;
    private Integer tamanho;
    private Integer tiempoDeArribo;
    private Integer tiempoDeIrrupcion;
    private Estado estado;
    private Integer tiempoDeIrrupcionOriginal;
    private Integer tiempoDeRetorno;
    private Integer tiempoDeEspera;

	public Proceso() {
        super();
    }
	
    public Proceso(String id, Integer tamanho, Integer tiempoDeArribo, Integer tiempoDeIrrupcion, Estado estado, Integer tiempoDeIrrupcionOriginal, Integer tiempoDeRetorno, Integer tiempoDeEspera) {
		super();
		this.id = id;
		this.tamanho = tamanho;
		this.tiempoDeArribo = tiempoDeArribo;
		this.tiempoDeIrrupcion = tiempoDeIrrupcion;
		this.estado = estado;
		this.tiempoDeIrrupcionOriginal = tiempoDeIrrupcionOriginal;
		this.tiempoDeRetorno = tiempoDeRetorno;
		this.tiempoDeEspera = tiempoDeEspera;
	}
    
    public Integer getTiempoDeEspera() {
    	return tiempoDeEspera;
    }
    
    public void setTiempoDeEspera(Integer tiempoDeEspera) {
    	this.tiempoDeEspera = tiempoDeEspera;
    }
    
    public Integer getTiempoDeRetorno() {
    	return tiempoDeRetorno;
    }
    
    public void setTiempoDeRetorno(Integer tiempoDeRetorno) {
    	this.tiempoDeRetorno = tiempoDeRetorno;
    }
    
    public Integer getTiempoDeIrrupcionOriginal() {
    	return tiempoDeIrrupcionOriginal;
    }
    
    public void setTiempoDeIrrupcionOriginal(Integer tiempoDeIrrupcionOriginal) {
    	this.tiempoDeIrrupcionOriginal = tiempoDeIrrupcionOriginal;
    }
    
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
		return this.id + " TI=" + this.tiempoDeIrrupcion + " Tamanho=" + this.tamanho;
	}
    

}
