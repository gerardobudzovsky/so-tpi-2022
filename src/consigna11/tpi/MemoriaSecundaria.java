package tpi;

import java.util.List;

public class MemoriaSecundaria {

	List<Proceso> procesosSuspendidos;

	public List<Proceso> getProcesosSuspendidos() {
		return procesosSuspendidos;
	}

	public void setProcesosSuspendidos(List<Proceso> procesosSuspendidos) {
		this.procesosSuspendidos = procesosSuspendidos;
	}

	@Override
	public String toString() {
		return "MemoriaSecundaria [procesosSuspendidos=" + procesosSuspendidos + "]";
	}
	
}
