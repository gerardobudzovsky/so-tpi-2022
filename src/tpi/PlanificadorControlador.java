package tpi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import tpi.constantes.Constantes;
import tpi.constantes.Estado;
import tpi.entidades.CantidadDeProcesosFinalizados;
import tpi.entidades.CantidadDeSwappings;
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
	private List<Proceso> colaDeFinalizados;
	private CantidadDeProcesosFinalizados cantidadDeProcesosFinalizados;
	private CantidadDeSwappings cantidadDeSwappings;
	private List<Logueo> logueos;


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
		this.colaDeFinalizados = new ArrayList<Proceso>();
		this.cantidadDeProcesosFinalizados = new CantidadDeProcesosFinalizados(0);
		this.cantidadDeSwappings = new CantidadDeSwappings(0);
		this.logueos = new ArrayList<Logueo>();
	}
	
	public void ejecutar(String pathDeArchivo) {
		
		//En el metodo leerProcesos() leemos los procesos del csv, controlamos su formato y 
		//y si no hay errores los cargamos en la lista procesosEnArchivoCsv
		final String SALTO_DE_LINEA = "\n";
		this.procesosEnArchivoCsv = this.planificadorServicio.leerProcesos(pathDeArchivo);
		String[][] tablaDeProcesos = new String[this.procesosEnArchivoCsv.size() + 1][];
		tablaDeProcesos[0] = new String[] { "TR", "TA", "TI", "TAM" };
		
		for (int i = 1; i < tablaDeProcesos.length; i++) {
			tablaDeProcesos[i] = new String[] { this.procesosEnArchivoCsv.get(i - 1).getId(), this.procesosEnArchivoCsv.get(i - 1).getTiempoDeArribo().toString(), this.procesosEnArchivoCsv.get(i - 1).getTiempoDeIrrupcion().toString(), this.procesosEnArchivoCsv.get(i - 1).getTamanho().toString() };
		}
		
		System.out.println("Tabla De Procesos");
		System.out.println("TR TA TI TAM");
		for (Proceso proceso : this.procesosEnArchivoCsv) {
			System.out.println(proceso.getId() + "  " + proceso.getTiempoDeArribo() + "  " + proceso.getTiempoDeIrrupcion() + "  " + proceso.getTamanho()) ;
		}
		System.out.println("");
		System.out.println("Particiones de Memoria Principal");
			System.out.println("Nombre: " + this.memoriaPrincipal.getParticionSo().getId() + " Tamanho: " + this.memoriaPrincipal.getParticionSo().getTamanho());
		for (Particion particion : this.memoriaPrincipal.getParticiones()) {
			System.out.println("Nombre: " + particion.getId() + " Tamanho: " + particion.getTamanho() + " kB");
		}
		
		do {
			
			Logueo logueo = new Logueo(this.tiempo,"", false, false);
			
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
				
				this.planificadorServicio.iterarSobreColaDeNuevos(this.cpu, this.memoriaPrincipal, this.colaDeNuevos, this.colaDeAdmitidos, this.tiempo, this.cantidadDeProcesosFinalizados, this.cantidadDeSwappings, this.colaDeFinalizados);

			} else {
				
				//Pregunto si la cola de nuevos no esta vacia
				if (!this.colaDeNuevos.isEmpty()) {
					this.planificadorServicio.iterarSobreColaDeNuevos(this.cpu, this.memoriaPrincipal, this.colaDeNuevos, this.colaDeAdmitidos, this.tiempo, this.cantidadDeProcesosFinalizados, this.cantidadDeSwappings, this.colaDeFinalizados);
				} else {
					
					//Pregunto si en la cola de admitidos (que no esta vacia) hay procesos con estado "listo y suspendido"
					if (this.planificadorServicio.existenProcesosEnMemoriaSecundaria(this.colaDeAdmitidos)) {
						//Tomo el primer proceso de la cola de listos/suspendidos
						Proceso primerProcesoEnMemoriaSecundaria = this.planificadorServicio.obtenerPrimerProcesoEnMemoriaSecundaria(this.colaDeAdmitidos);
						if (this.planificadorServicio.existeAlgunaParticionLibre(memoriaPrincipal)) {
							if (this.planificadorServicio.existeAlgunaParticionLibreDondeQuepaElProceso(memoriaPrincipal, primerProcesoEnMemoriaSecundaria)) {
								
								this.planificadorServicio.worstFitEnMemoriaPrincipal(primerProcesoEnMemoriaSecundaria, memoriaPrincipal);
								this.colaDeAdmitidos.sort(Comparator.comparing(Proceso::getTiempoDeIrrupcion));
								
									if (colaDeNuevos.size() > 0) {
										this.planificadorServicio.iterarSobreColaDeNuevos(cpu ,memoriaPrincipal, colaDeNuevos, colaDeAdmitidos, tiempo, this.cantidadDeProcesosFinalizados, this.cantidadDeSwappings, this.colaDeFinalizados);
									} else {
										this.planificadorServicio.trabajoEnCpu(this.cpu, this.colaDeAdmitidos, this.memoriaPrincipal, this.cantidadDeProcesosFinalizados, this.tiempo, this.colaDeFinalizados);
									}
								
							} else {

								if ( this.planificadorServicio.esFactibleHacerSwapping(memoriaPrincipal, primerProcesoEnMemoriaSecundaria, tiempo) ) {
									List<Particion> particionesCandidatasAlSwapping = this.planificadorServicio.obtenerParticionesCandidatasParaSwapping(memoriaPrincipal, primerProcesoEnMemoriaSecundaria, tiempo);
									this.planificadorServicio.worstFitEnParticionesCandidatasAlSwapping(primerProcesoEnMemoriaSecundaria, particionesCandidatasAlSwapping, this.cantidadDeSwappings);
									colaDeAdmitidos.sort(Comparator.comparing(Proceso::getTiempoDeIrrupcion));
									
									if (colaDeNuevos.size() > 0) {
										this.planificadorServicio.iterarSobreColaDeNuevos(cpu, memoriaPrincipal, colaDeNuevos, colaDeAdmitidos, tiempo, cantidadDeProcesosFinalizados, this.cantidadDeSwappings, this.colaDeFinalizados);
									} else {
										this.planificadorServicio.trabajoEnCpu(cpu, colaDeAdmitidos, memoriaPrincipal, cantidadDeProcesosFinalizados, tiempo, this.colaDeFinalizados);
									}

								} else {
									
									if (colaDeNuevos.size() > 0) {
										this.planificadorServicio.iterarSobreColaDeNuevos(this.cpu, this.memoriaPrincipal, this.colaDeNuevos, this.colaDeAdmitidos, this.tiempo, this.cantidadDeProcesosFinalizados, this.cantidadDeSwappings, this.colaDeFinalizados);
									} else {
										this.planificadorServicio.trabajoEnCpu(this.cpu, this.colaDeAdmitidos, this.memoriaPrincipal, this.cantidadDeProcesosFinalizados, this.tiempo, this.colaDeFinalizados);
									}
								}
								
							}
						} else {
							
							if ( this.planificadorServicio.esFactibleHacerSwapping(memoriaPrincipal, primerProcesoEnMemoriaSecundaria, tiempo) ) {
								List<Particion> particionesCandidatasAlSwapping = this.planificadorServicio.obtenerParticionesCandidatasParaSwapping(memoriaPrincipal, primerProcesoEnMemoriaSecundaria, tiempo);
								this.planificadorServicio.worstFitEnParticionesCandidatasAlSwapping(primerProcesoEnMemoriaSecundaria, particionesCandidatasAlSwapping, this.cantidadDeSwappings);
								colaDeAdmitidos.sort(Comparator.comparing(Proceso::getTiempoDeIrrupcion));
								
								if (colaDeNuevos.size() > 0) {
									this.planificadorServicio.iterarSobreColaDeNuevos(cpu, memoriaPrincipal, colaDeNuevos, colaDeAdmitidos, tiempo, cantidadDeProcesosFinalizados, this.cantidadDeSwappings, this.colaDeFinalizados);
								} else {
									this.planificadorServicio.trabajoEnCpu(this.cpu, this.colaDeAdmitidos, this.memoriaPrincipal, this.cantidadDeProcesosFinalizados, this.tiempo, this.colaDeFinalizados);
								}

							}else {
								
								if (colaDeNuevos.size() > 0) {
									this.planificadorServicio.iterarSobreColaDeNuevos(this.cpu, this.memoriaPrincipal, this.colaDeNuevos, this.colaDeAdmitidos, this.tiempo, this.cantidadDeProcesosFinalizados, this.cantidadDeSwappings, this.colaDeFinalizados);
								} else {
									this.planificadorServicio.trabajoEnCpu(this.cpu, this.colaDeAdmitidos, this.memoriaPrincipal, this.cantidadDeProcesosFinalizados, this.tiempo, this.colaDeFinalizados);
								}
							}
						}
					} else {
						this.planificadorServicio.trabajoEnCpu(this.cpu, this.colaDeAdmitidos, this.memoriaPrincipal, this.cantidadDeProcesosFinalizados, this.tiempo, this.colaDeFinalizados);
					}
				}
			}
			

        	System.out.println();        	
			System.out.println("AL FINAL DEL INSTANTE " + this.tiempo + " TENEMOS:");
			logueo.setTexto("AL FINAL DEL INSTANTE " + this.tiempo + " TENEMOS:" + SALTO_DE_LINEA);
			if (this.cpu.getProceso() != null) {
				System.out.println("En ejecución: " + this.cpu.getProceso().getId() + " TI=" + this.cpu.getProceso().getTiempoDeIrrupcion());
			} else {
				System.out.println("En ejecución: NO HAY");
			}
			System.out.println("Cola de Listos: " + this.planificadorServicio.mostrarColaDeListos(colaDeAdmitidos));
			logueo.setTexto(logueo.getTexto().concat("Cola de Listos: " + colaDeAdmitidos.toString() + SALTO_DE_LINEA));
			System.out.println("Cola de Listos/Suspendidos: " + this.planificadorServicio.mostrarColaDeListosSuspendidos(colaDeAdmitidos));
			logueo.setTexto(logueo.getTexto().concat("Cola de Listos/Suspendidos: " + this.planificadorServicio.mostrarColaDeListosSuspendidos(colaDeAdmitidos) + SALTO_DE_LINEA));
			System.out.println("Cola de Nuevos: " + this.colaDeNuevos);
			logueo.setTexto(logueo.getTexto().concat("Cola de Nuevos: " + this.colaDeNuevos.toString() + SALTO_DE_LINEA));
			System.out.println("Particiones de Memoria Principal");
			for (Particion particion : this.memoriaPrincipal.getParticiones()) {
				System.out.println("Nombre: " + particion.getId() + " Tamanho: " + particion.getTamanho() + " kB" + " Proceso: " + particion.getProceso() + " Fragmentacion Interna: " + particion.getFragmentacionInterna());
			}
			System.out.println();
			
			this.logueos.add(logueo);
			this.tiempo++;
			

		} while (this.cantidadDeProcesosFinalizados.valor < procesosEnArchivoCsv.size());
		
		System.out.println("");
		System.out.println("ESTADISTICAS");
		System.out.println("Cantidad de swappings realizados: " + this.cantidadDeSwappings.valor);
		System.out.println("Tiempo de Retorno Promedio: " + this.planificadorServicio.obtenerTiempoDeRetornoPromedio(this.colaDeFinalizados));
		System.out.println("Tiempo de Espera Promedio: " + this.planificadorServicio.obtenerTiempoDeEsperaPromedio(this.colaDeFinalizados));
	}
	
	public List<Logueo> getLogueos() {
		return logueos;
	}
	
	public void setLogueos(List<Logueo> logueos) {
		this.logueos = logueos;
	}
}
