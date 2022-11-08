package tpi.entidades;

public class Cpu {
	
	private Proceso proceso;

	public Proceso getProceso() {
		return proceso;
	}

	public void setProceso(Proceso proceso) {
		this.proceso = proceso;
	}

	@Override
	public String toString() {
		return "Cpu [proceso=" + proceso + "]";
	}
	
}
