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
	private List<Proceso> colaDeAdmitidos;

	//Constructor
	public PlanificadorControlador() {
		super();
		this.planificadorServicio = new PlanificadorServicio();
		this.cpu = new Cpu();
		this.memoriaPrincipal = new MemoriaPrincipal();
		this.memoriaPrincipal.setTamanho(Constantes.TAMANHO_PARTICION_SO + Constantes.TAMANHO_PARTICION_T_GRANDES
				+ Constantes.TAMANHO_PARTICION_T_MEDIANOS + Constantes.TAMANHO_PARTICION_T_PEQUENHOS);
		List<Particion> particiones = this.planificadorServicio.inicializarParticiones();
		this.memoriaPrincipal.setParticiones(particiones);
		this.memoriaPrincipal.setParticionSo(this.planificadorServicio.inicializarParticionSo());
		this.procesosEnArchivoCsv = new ArrayList<Proceso>();
		this.tiempo = 0;
		this.colaDeNuevos = new ArrayList<Proceso>();
		this.colaDeAdmitidos = new ArrayList<Proceso>();
	}
	
	public void ejecutar() {
		
		Integer cantidadDeProcesosFinalizados = 0;
		String gantt = "";
		//En el metodo leerProcesos() leemos los procesos del csv, controlamos su formato y 
		//y si no hay errores los cargamos en la lista procesosEnArchivoCsv
		this.procesosEnArchivoCsv = this.planificadorServicio.leerProcesos();
		
//		System.out.println("Memoria Principal");
//		System.out.println(memoriaPrincipal);
		
		do {
			
			//En este ciclo for recorro los procesos en el csv. Todo proceso del csv con tiempo de arribo igual al tiempo actual
			//es agregado a la lista procesosLlegadosEnElInstanteActual
			ArrayList<Proceso> procesosLlegadosEnElInstanteActual = new ArrayList<Proceso>();
			for (Proceso proceso : procesosEnArchivoCsv) {
				if (proceso.getTiempoDeArribo().equals(this.tiempo)) {
					proceso.setEstado(Estado.NUEVO);
					procesosLlegadosEnElInstanteActual.add(proceso);
				}
			}
			
			//Pregunto si la lista procesosLlegadosEnElInstanteActual esta vacia o no, es decir pregunto si arribaron nuevos 
			//procesos en el instante actual
			if (!procesosLlegadosEnElInstanteActual.isEmpty()) {
				
				//Si arribaron procesos en el instante actual los agrego a la cola de nuevos,
				this.colaDeNuevos.addAll(procesosLlegadosEnElInstanteActual);
				this.colaDeNuevos.sort(Comparator.comparing(Proceso::getTiempoDeIrrupcion));
				System.out.println("Arribaron los siguientes procesos en el instante " + tiempo);
				System.out.println(procesosLlegadosEnElInstanteActual);
				
				if (this.colaDeAdmitidos.size() < Constantes.NIVEL_DE_MULTIPROGRAMACION) {
					
					Proceso primerProcesoColaNuevos = this.colaDeNuevos.get(0);
					
					if (planificadorServicio.existeAlgunaParticionLibre(this.memoriaPrincipal)) {
						System.out.println("Existe particion libre");
							if (planificadorServicio.existeAlgunaParticionLibreDondeQuepaElProceso(this.memoriaPrincipal, primerProcesoColaNuevos)) {
								System.out.println("El proceso cabe en alguna particion");
								planificadorServicio.worstFit(primerProcesoColaNuevos, this.memoriaPrincipal);
								System.out.println(this.memoriaPrincipal);
							} else {

							}
					} else {

					}
					
				} else {

				}
				
				
				
			} else {
				System.out.println("No arribaron procesos en el instante " + tiempo);
				
				//Pregunto si la cola de nuevos no esta vacia
				if (!this.colaDeNuevos.isEmpty()) {
					//Ir a la parte donde pregunta por el nivel de multiprogramacion
				} else {
					
					//Pregunto si en la cola de admitidos (que no esta vacia) hay procesos con estado "listo y suspendido"
					
					for (Proceso proceso : this.colaDeAdmitidos) {
						
					}

				}
			}
			

			
					
			                                                                                                                                                                                            
//			if (cpu.getProceso() == null) {
//	        	cpu.setProceso(colaDeNuevos.get(0));
//	        	cpu.getProceso().setEstado(Estado.EN_EJECUCION);
//	        	colaDeNuevos.remove(0);
//	        }
//	        
//	        this.cpu.getProceso().setTiempoDeIrrupcion(this.cpu.getProceso().getTiempoDeIrrupcion() - 1);
//
//	        
//	        System.out.println("Procesos en cola de nuevos al final del instante " + this.tiempo);
//	        System.out.println(this.colaDeNuevos);
//	        System.out.println("Proceso en CPU: " + this.cpu.getProceso().getId());
//	        gantt = gantt + this.cpu.getProceso().getId() + "-";
//	        System.out.println("Gantt: " + gantt);
//	        System.out.println();
//		
//        	if (this.cpu.getProceso().getTiempoDeIrrupcion() == 0) {
        		cantidadDeProcesosFinalizados++;
//        		this.cpu.setProceso(null);
//        	}
        	        
	        this.tiempo++;
	       			
		} while (cantidadDeProcesosFinalizados < procesosEnArchivoCsv.size());
		
	}
	
}
