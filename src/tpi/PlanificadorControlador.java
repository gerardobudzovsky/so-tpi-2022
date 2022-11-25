package tpi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import tpi.constantes.Constantes;
import tpi.constantes.Estado;
import tpi.entidades.CantidadDeProcesosFinalizados;
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
	private CantidadDeProcesosFinalizados cantidadDeProcesosFinalizados;

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
		this.cantidadDeProcesosFinalizados = new CantidadDeProcesosFinalizados(0);
	}
	
	public void ejecutar() {
		
		String gantt = "";
		//En el metodo leerProcesos() leemos los procesos del csv, controlamos su formato y 
		//y si no hay errores los cargamos en la lista procesosEnArchivoCsv
		this.procesosEnArchivoCsv = this.planificadorServicio.leerProcesos();
		
//		System.out.println("Memoria Principal");
//		System.out.println(memoriaPrincipal);
		
		do {
			
			//En este metodo todo proceso del csv con tiempo de arribo igual al tiempo actual
			//es agregado a la lista procesosLlegadosEnElInstanteActual
			List<Proceso> procesosLlegadosEnElInstanteActual = this.planificadorServicio.obtenerProcesosLlegadosEnElInstanteActual(this.procesosEnArchivoCsv, this.tiempo);
			
			//Pregunto si la lista procesosLlegadosEnElInstanteActual esta vacia o no, es decir pregunto si arribaron nuevos 
			//procesos en el instante actual
			if (!procesosLlegadosEnElInstanteActual.isEmpty()) {
				
				// Si arribaron procesos en el instante actual, seteo los procesos con Estado NUEVO
				for (Proceso proceso : procesosLlegadosEnElInstanteActual) {
					proceso.setEstado(Estado.NUEVO);
				}
				
				// Agrego los procesos a la cola de nuevos,
				this.colaDeNuevos.addAll(procesosLlegadosEnElInstanteActual);
				//La cola de nuevos es FIFO (esta ordenada por tiempo de arribo)
				this.colaDeNuevos.sort(Comparator.comparing(Proceso::getTiempoDeArribo));
				System.out.println("Arribaron los siguientes procesos en el instante " + tiempo);
				System.out.println(procesosLlegadosEnElInstanteActual);
				
				//llamada a un metodo que al comiezo pregunta por multiprogramacion
				
				this.planificadorServicio.iterarSobreColaDeNuevos(this.cpu, this.memoriaPrincipal, this.colaDeNuevos, this.colaDeAdmitidos, this.tiempo, this.cantidadDeProcesosFinalizados);

			} else {
				System.out.println("No arribaron procesos en el instante " + this.tiempo);
				
				//Pregunto si la cola de nuevos no esta vacia
				if (!this.colaDeNuevos.isEmpty()) {
					this.planificadorServicio.iterarSobreColaDeNuevos(this.cpu, this.memoriaPrincipal, this.colaDeNuevos, this.colaDeAdmitidos, this.tiempo, this.cantidadDeProcesosFinalizados);
				} else {
					
					//Pregunto si en la cola de admitidos (que no esta vacia) hay procesos con estado "listo y suspendido"
					if (this.planificadorServicio.existenProcesosEnMemoriaSecundaria(this.colaDeAdmitidos)) {
						//Tomo el primer proceso de la cola de listos/suspendidos
						Proceso primerProcesoEnMemoriaSecundaria = this.planificadorServicio.obtenerPrimerProcesoEnMemoriaSecundaria(this.colaDeAdmitidos);
						if (this.planificadorServicio.existeAlgunaParticionLibre(memoriaPrincipal)) {
							System.out.println("Existe al menos una particion libre en Memoria Principal.");
							if (this.planificadorServicio.existeAlgunaParticionLibreDondeQuepaElProceso(memoriaPrincipal, primerProcesoEnMemoriaSecundaria)) {
								
								System.out.println("El proceso " + primerProcesoEnMemoriaSecundaria.getId() + " cabe en una particion libre de Memoria Principal.");
								this.planificadorServicio.worstFitEnMemoriaPrincipal(primerProcesoEnMemoriaSecundaria, memoriaPrincipal);
								System.out.println("El proceso " + primerProcesoEnMemoriaSecundaria.getId() + " se cargo en Memoria Principal.");
								this.colaDeAdmitidos.sort(Comparator.comparing(Proceso::getTiempoDeIrrupcion));
								
									if (colaDeNuevos.size() > 0) {
										this.planificadorServicio.iterarSobreColaDeNuevos(cpu ,memoriaPrincipal, colaDeNuevos, colaDeAdmitidos, tiempo, this.cantidadDeProcesosFinalizados);
									} else {
										this.planificadorServicio.trabajoEnCpu(this.cpu, this.colaDeAdmitidos, this.memoriaPrincipal, this.cantidadDeProcesosFinalizados);
									}
								
								//TODO
							} else {
								System.out.println("El proceso " + primerProcesoEnMemoriaSecundaria.getId() + " no cabe en ninguna particion libre de Memoria Principal.");

								if ( this.planificadorServicio.esFactibleHacerSwapping(memoriaPrincipal, primerProcesoEnMemoriaSecundaria, tiempo) ) {
									//TODO
								} else {
									System.out.println("El proceso " + primerProcesoEnMemoriaSecundaria.getId() + " se queda en Memoria Secundaria.");
									
									if (colaDeNuevos.size() > 0) {
										this.planificadorServicio.iterarSobreColaDeNuevos(this.cpu, this.memoriaPrincipal, this.colaDeNuevos, this.colaDeAdmitidos, this.tiempo, this.cantidadDeProcesosFinalizados);
									} else {
										this.planificadorServicio.trabajoEnCpu(this.cpu, this.colaDeAdmitidos, this.memoriaPrincipal, this.cantidadDeProcesosFinalizados);
									}
								}
								
							}
						} else {
							//TODO
							System.out.println("No hay ninguna particion libre en Memoria Principal.");
							
							if ( this.planificadorServicio.esFactibleHacerSwapping(memoriaPrincipal, primerProcesoEnMemoriaSecundaria, tiempo) ) {
								//TODO
							}else {
								System.out.println("El proceso " + primerProcesoEnMemoriaSecundaria.getId() + " se queda en Memoria Secundaria.");
								
								if (colaDeNuevos.size() > 0) {
									this.planificadorServicio.iterarSobreColaDeNuevos(this.cpu, this.memoriaPrincipal, this.colaDeNuevos, this.colaDeAdmitidos, this.tiempo, this.cantidadDeProcesosFinalizados);
								} else {
									this.planificadorServicio.trabajoEnCpu(this.cpu, this.colaDeAdmitidos, this.memoriaPrincipal, this.cantidadDeProcesosFinalizados);
								}
							}
						}
					} else {
						this.planificadorServicio.trabajoEnCpu(this.cpu, this.colaDeAdmitidos, this.memoriaPrincipal, this.cantidadDeProcesosFinalizados);
					}
				}
			}
			

//	        System.out.println("Gantt: " + gantt);
        	System.out.println();        	
			System.out.println("AL FINAL DEL INSTANTE " + this.tiempo + " TENEMOS:");
			if (this.cpu.getProceso() != null) {
				System.out.println("En ejecución: " + this.cpu.getProceso().getId() + " TI=" + this.cpu.getProceso().getTiempoDeIrrupcion());
			} else {
				System.out.println("En ejecución: NO HAY");
			}
			System.out.println("Cola de Listos: " + this.planificadorServicio.mostrarColaDeListos(colaDeAdmitidos));
			System.out.println("Cola de Listos/Suspendidos: " + this.planificadorServicio.mostrarColaDeListosSuspendidos(colaDeAdmitidos));
//			System.out.println("Admitidos: " + this.colaDeAdmitidos);
			System.out.println("Cola de Nuevos: " + this.colaDeNuevos);
			System.out.print(this.memoriaPrincipal.getParticiones().get(0));
			System.out.print(this.memoriaPrincipal.getParticiones().get(1));
			System.out.print(this.memoriaPrincipal.getParticiones().get(2));
			System.out.println();
			
			this.tiempo++;
			

		} while (this.cantidadDeProcesosFinalizados.valor < procesosEnArchivoCsv.size());
		
	}
	
}
