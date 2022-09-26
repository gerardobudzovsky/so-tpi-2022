package tpi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import tpi.constantes.Constantes;
import tpi.constantes.Estado;
import tpi.entidades.Cpu;
import tpi.entidades.MemoriaPrincipal;
import tpi.entidades.Particion;
import tpi.entidades.Proceso;

public class PlanificadorControlador {

	private PlanificadorServicio planificadorServicio;
	private Cpu cpu;
	private MemoriaPrincipal memoriaPrincipal;
	private List<Proceso> procesosEnArchivoCsv;
	private Integer tiempo;
	private List<Proceso> colaDeNuevos;
	private List<Proceso> colaDeListos;
	private List<Proceso> colaDeListosSuspendidos;

	public PlanificadorControlador() {
		this.planificadorServicio = new PlanificadorServicio();
		this.cpu = new Cpu();
		this.memoriaPrincipal = new MemoriaPrincipal();
		this.memoriaPrincipal.setTamanho(Constantes.TAMANHO_PARTICION_SO + Constantes.TAMANHO_PARTICION_T_GRANDES + Constantes.TAMANHO_PARTICION_T_MEDIANOS + Constantes.TAMANHO_PARTICION_T_PEQUENHOS);
		List<Particion> particiones = this.planificadorServicio.inicializarParticiones();		
		this.memoriaPrincipal.setParticiones(particiones);
		this.procesosEnArchivoCsv = new ArrayList<Proceso>();
		this.tiempo = 0;
		this.colaDeNuevos = new ArrayList<Proceso>();
		this.colaDeListos = new ArrayList<Proceso>();
		this.colaDeListosSuspendidos = new ArrayList<Proceso>();
	}
	
	public void ejecutar() {
		
		Integer cantidadDeProcesosFinalizados = 0;
		String gantt = "";
		this.procesosEnArchivoCsv = this.planificadorServicio.leerProcesos();
		
		System.out.println("Memoria Principal");
		System.out.println(memoriaPrincipal);
		
		do {
			
			this.colaDeNuevos = this.planificadorServicio.sjf(this.colaDeNuevos, this.procesosEnArchivoCsv, this.tiempo);
			
			this.planificadorServicio.worstFit();
					
			                                                                                                                                                                                            
			if (cpu.getProceso() == null) {
	        	cpu.setProceso(colaDeNuevos.get(0));
	        	cpu.getProceso().setEstado(Estado.EN_EJECUCION);
	        	colaDeNuevos.remove(0);
	        }
	        
	        this.cpu.getProceso().setTiempoDeIrrupcion(this.cpu.getProceso().getTiempoDeIrrupcion() - 1);

	        
	        System.out.println("Procesos en cola de nuevos al final del instante " + this.tiempo);
	        System.out.println(this.colaDeNuevos);
	        System.out.println("Proceso en CPU: " + this.cpu.getProceso().getId());
	        gantt = gantt + this.cpu.getProceso().getId() + "-";
	        System.out.println("Gantt: " + gantt);
	        System.out.println();
		
        	if (this.cpu.getProceso().getTiempoDeIrrupcion() == 0) {
        		cantidadDeProcesosFinalizados++;
        		this.cpu.setProceso(null);
        	}
        	        
	        this.tiempo++;
	       			
		} while (cantidadDeProcesosFinalizados < procesosEnArchivoCsv.size());
		
	}
	
}
