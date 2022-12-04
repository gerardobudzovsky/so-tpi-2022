package tpi;

public class Logueo {
	
	private Integer instante;
	private String texto;
	private Boolean esInstanteDondeArribanProcesos;
	private Boolean esInstanteConProcesosTerminados;
	
	public Logueo() {
		super();
	}

	public Logueo(Integer instante, String texto, Boolean esInstanteDondeArribanProcesos,
			Boolean esInstanteConProcesosTerminados) {
		super();
		this.instante = instante;
		this.texto = texto;
		this.esInstanteDondeArribanProcesos = esInstanteDondeArribanProcesos;
		this.esInstanteConProcesosTerminados = esInstanteConProcesosTerminados;
	}

	public Integer getInstante() {
		return instante;
	}

	public void setInstante(Integer instante) {
		this.instante = instante;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public Boolean getEsInstanteDondeArribanProcesos() {
		return esInstanteDondeArribanProcesos;
	}

	public void setEsInstanteDondeArribanProcesos(Boolean esInstanteDondeArribanProcesos) {
		this.esInstanteDondeArribanProcesos = esInstanteDondeArribanProcesos;
	}

	public Boolean getEsInstanteConProcesosTerminados() {
		return esInstanteConProcesosTerminados;
	}

	public void setEsInstanteConProcesosTerminados(Boolean esInstanteConProcesosTerminados) {
		this.esInstanteConProcesosTerminados = esInstanteConProcesosTerminados;
	}
	
}
