package tpi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import tpi.constantes.Constantes;
import tpi.constantes.Estado;
import tpi.entidades.CantidadDeProcesosFinalizados;
import tpi.entidades.CantidadDeSwappings;
import tpi.entidades.Cpu;
import tpi.entidades.HayErrorDeFormato;
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
	private HayErrorDeFormato hayErrorDeFormato;
	private Logueo logueoInicial;
	private Logueo logueoFinal;

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
		this.hayErrorDeFormato = new HayErrorDeFormato(false);
	}
	
	public void ejecutar(String pathDeArchivo) {
		
		final String SALTO_DE_LINEA = "\n";
		//En el metodo leerProcesos() leemos los procesos del csv, controlamos su formato y 
		//y si no hay errores los cargamos en la lista procesosEnArchivoCsv
		this.procesosEnArchivoCsv = this.planificadorServicio.leerProcesos(pathDeArchivo, this.hayErrorDeFormato);
		
		if (!this.hayErrorDeFormato.valor) {
		
			this.realizarLogueoInicial();
			
			do {
				
				Logueo logueo = new Logueo(this.tiempo,"", false, false);
				//System.out.println("INSTANTE " + this.tiempo);
				logueo.setTexto( logueo.getTexto().concat("INSTANTE " + this.tiempo) + SALTO_DE_LINEA );
				
				//En este metodo todo proceso del csv con tiempo de arribo igual al tiempo actual
				//es agregado a la lista procesosLlegadosEnElInstanteActual
				List<Proceso> procesosLlegadosEnElInstanteActual = this.planificadorServicio.obtenerProcesosLlegadosEnElInstanteActual(this.procesosEnArchivoCsv, this.tiempo);
				
				//Pregunto si la lista procesosLlegadosEnElInstanteActual esta vacia o no, es decir pregunto si arribaron nuevos 
				//procesos en el instante actual
				if (!procesosLlegadosEnElInstanteActual.isEmpty()) {
					
					logueo.setEsInstanteDondeArribanProcesos(true);
					// Si arribaron procesos en el instante actual, seteo los procesos con Estado NUEVO
					for (Proceso proceso : procesosLlegadosEnElInstanteActual) {
						proceso.setEstado(Estado.NUEVO);
					}
					
					// Agrego los procesos a la cola de nuevos,
					this.colaDeNuevos.addAll(procesosLlegadosEnElInstanteActual);
					//La cola de nuevos es FIFO (esta ordenada por tiempo de arribo)
					this.colaDeNuevos.sort(Comparator.comparing(Proceso::getTiempoDeArribo));
					//System.out.println("Arribaron los siguientes procesos en el instante " + tiempo + ":");
					logueo.setTexto( logueo.getTexto().concat("Arribaron los siguientes procesos en el instante " + tiempo + " :" + SALTO_DE_LINEA) );
					//System.out.println(procesosLlegadosEnElInstanteActual);
					logueo.setTexto( logueo.getTexto().concat(procesosLlegadosEnElInstanteActual.toString()) + SALTO_DE_LINEA);
					
					//llamada a un metodo que al comiezo pregunta por multiprogramacion
					
					this.planificadorServicio.iterarSobreColaDeNuevos(this.cpu, this.memoriaPrincipal, this.colaDeNuevos, this.colaDeAdmitidos, this.tiempo, this.cantidadDeProcesosFinalizados, this.cantidadDeSwappings, this.colaDeFinalizados, logueo);
	
				} else {
					
					//Pregunto si la cola de nuevos no esta vacia
					if (!this.colaDeNuevos.isEmpty()) {
						this.planificadorServicio.iterarSobreColaDeNuevos(this.cpu, this.memoriaPrincipal, this.colaDeNuevos, this.colaDeAdmitidos, this.tiempo, this.cantidadDeProcesosFinalizados, this.cantidadDeSwappings, this.colaDeFinalizados, logueo);
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
											this.planificadorServicio.iterarSobreColaDeNuevos(cpu ,memoriaPrincipal, colaDeNuevos, colaDeAdmitidos, tiempo, this.cantidadDeProcesosFinalizados, this.cantidadDeSwappings, this.colaDeFinalizados, logueo);
										} else {
											this.planificadorServicio.trabajoEnCpu(this.cpu, this.colaDeAdmitidos, this.memoriaPrincipal, this.cantidadDeProcesosFinalizados, this.tiempo, this.colaDeFinalizados, logueo);
										}
									
								} else {
	
									if ( this.planificadorServicio.esFactibleHacerSwapping(memoriaPrincipal, primerProcesoEnMemoriaSecundaria, tiempo) ) {
										List<Particion> particionesCandidatasAlSwapping = this.planificadorServicio.obtenerParticionesCandidatasParaSwapping(memoriaPrincipal, primerProcesoEnMemoriaSecundaria, tiempo);
										this.planificadorServicio.worstFitEnParticionesCandidatasAlSwapping(primerProcesoEnMemoriaSecundaria, particionesCandidatasAlSwapping, this.cantidadDeSwappings);
										colaDeAdmitidos.sort(Comparator.comparing(Proceso::getTiempoDeIrrupcion));
										
										if (colaDeNuevos.size() > 0) {
											this.planificadorServicio.iterarSobreColaDeNuevos(cpu, memoriaPrincipal, colaDeNuevos, colaDeAdmitidos, tiempo, cantidadDeProcesosFinalizados, this.cantidadDeSwappings, this.colaDeFinalizados, logueo);
										} else {
											this.planificadorServicio.trabajoEnCpu(cpu, colaDeAdmitidos, memoriaPrincipal, cantidadDeProcesosFinalizados, tiempo, this.colaDeFinalizados, logueo);
										}
	
									} else {
										
										if (colaDeNuevos.size() > 0) {
											this.planificadorServicio.iterarSobreColaDeNuevos(this.cpu, this.memoriaPrincipal, this.colaDeNuevos, this.colaDeAdmitidos, this.tiempo, this.cantidadDeProcesosFinalizados, this.cantidadDeSwappings, this.colaDeFinalizados, logueo);
										} else {
											this.planificadorServicio.trabajoEnCpu(this.cpu, this.colaDeAdmitidos, this.memoriaPrincipal, this.cantidadDeProcesosFinalizados, this.tiempo, this.colaDeFinalizados, logueo);
										}
									}
									
								}
							} else {
								
								if ( this.planificadorServicio.esFactibleHacerSwapping(memoriaPrincipal, primerProcesoEnMemoriaSecundaria, tiempo) ) {
									List<Particion> particionesCandidatasAlSwapping = this.planificadorServicio.obtenerParticionesCandidatasParaSwapping(memoriaPrincipal, primerProcesoEnMemoriaSecundaria, tiempo);
									this.planificadorServicio.worstFitEnParticionesCandidatasAlSwapping(primerProcesoEnMemoriaSecundaria, particionesCandidatasAlSwapping, this.cantidadDeSwappings);
									colaDeAdmitidos.sort(Comparator.comparing(Proceso::getTiempoDeIrrupcion));
									
									if (colaDeNuevos.size() > 0) {
										this.planificadorServicio.iterarSobreColaDeNuevos(cpu, memoriaPrincipal, colaDeNuevos, colaDeAdmitidos, tiempo, cantidadDeProcesosFinalizados, this.cantidadDeSwappings, this.colaDeFinalizados, logueo);
									} else {
										this.planificadorServicio.trabajoEnCpu(this.cpu, this.colaDeAdmitidos, this.memoriaPrincipal, this.cantidadDeProcesosFinalizados, this.tiempo, this.colaDeFinalizados, logueo);
									}
	
								}else {
									
									if (colaDeNuevos.size() > 0) {
										this.planificadorServicio.iterarSobreColaDeNuevos(this.cpu, this.memoriaPrincipal, this.colaDeNuevos, this.colaDeAdmitidos, this.tiempo, this.cantidadDeProcesosFinalizados, this.cantidadDeSwappings, this.colaDeFinalizados, logueo);
									} else {
										this.planificadorServicio.trabajoEnCpu(this.cpu, this.colaDeAdmitidos, this.memoriaPrincipal, this.cantidadDeProcesosFinalizados, this.tiempo, this.colaDeFinalizados, logueo);
									}
								}
							}
						} else {
							this.planificadorServicio.trabajoEnCpu(this.cpu, this.colaDeAdmitidos, this.memoriaPrincipal, this.cantidadDeProcesosFinalizados, this.tiempo, this.colaDeFinalizados, logueo);
						}
					}
				}
				
	
	        	//System.out.println(); 
	        	logueo.setTexto(logueo.getTexto() + SALTO_DE_LINEA);
				//System.out.println("AL FINAL DEL INSTANTE " + this.tiempo + " TENEMOS:");
				logueo.setTexto( logueo.getTexto().concat("AL FINAL DEL INSTANTE " + this.tiempo + " TENEMOS:" + SALTO_DE_LINEA) );
				if (this.cpu.getProceso() != null) {
					//System.out.println("En ejecucion: " + this.cpu.getProceso().getId() + " TI=" + this.cpu.getProceso().getTiempoDeIrrupcion());
					logueo.setTexto( logueo.getTexto().concat("En ejecucion: " + this.cpu.getProceso().getId() + " TI=" + this.cpu.getProceso().getTiempoDeIrrupcion()) + SALTO_DE_LINEA);
				} else {
					//System.out.println("En ejecucion: NO HAY");
					logueo.setTexto( logueo.getTexto().concat("En ejecucion: NO HAY" + SALTO_DE_LINEA) );
				}
				//System.out.println("Cola de Listos: " + this.planificadorServicio.mostrarColaDeListos(colaDeAdmitidos));
				logueo.setTexto( logueo.getTexto().concat("Cola de Listos: " + this.planificadorServicio.mostrarColaDeListos(colaDeAdmitidos) + SALTO_DE_LINEA));
				//System.out.println("Cola de Listos/Suspendidos: " + this.planificadorServicio.mostrarColaDeListosSuspendidos(colaDeAdmitidos));
				logueo.setTexto( logueo.getTexto().concat("Cola de Listos/Suspendidos: " + this.planificadorServicio.mostrarColaDeListosSuspendidos(colaDeAdmitidos) + SALTO_DE_LINEA));
				//System.out.println("Cola de Nuevos: " + this.colaDeNuevos);
				logueo.setTexto( logueo.getTexto().concat("Cola de Nuevos: " + this.colaDeNuevos.toString() + SALTO_DE_LINEA));
				//System.out.println("Particiones de Memoria Principal");
				logueo.setTexto( logueo.getTexto().concat("Particiones de Memoria Principal" + SALTO_DE_LINEA));
				for (Particion particion : this.memoriaPrincipal.getParticiones()) {
					//System.out.println("Nombre: " + particion.getId() + ", Tamanho: " + particion.getTamanho() + " kB, Proceso: " + particion.getProceso() + ", Fragmentacion Interna: " + particion.getFragmentacionInterna());
					logueo.setTexto( logueo.getTexto().concat("Nombre: " + particion.getId() + ", Tamanho: " + particion.getTamanho() + " kB, Proceso: " + particion.getProceso() + ", Fragmentacion Interna: " + particion.getFragmentacionInterna() + SALTO_DE_LINEA));
				}
				//System.out.println("____________________________________________________________________________________________");
				logueo.setTexto( logueo.getTexto().concat("____________________________________________________________________________________________" + SALTO_DE_LINEA));
				//System.out.println();
				logueo.setTexto(logueo.getTexto() + SALTO_DE_LINEA);
				this.logueos.add(logueo);
				this.tiempo++;
				
	
			} while (this.cantidadDeProcesosFinalizados.valor < procesosEnArchivoCsv.size());
			
			this.realizarLogueoFinal();
		}
	}
	
	public List<Logueo> getLogueos() {
		return logueos;
	}
	
	public void setLogueos(List<Logueo> logueos) {
		this.logueos = logueos;
	}
	
	public Logueo getLogueoInicial() {
		return logueoInicial;
	}
	
	public void setLogueoInicial(Logueo logueoInicial) {
		this.logueoInicial = logueoInicial;
	}
	
	public Logueo getLogueoFinal() {
		return logueoFinal;
	}
	
	public void setLogueoFinal(Logueo logueoFinal) {
		this.logueoFinal = logueoFinal;
	}
	
	public void realizarLogueoInicial() {
		final String SALTO_DE_LINEA = "\n";
		logueoInicial = new Logueo();
		logueoInicial.setTexto("");
		String[][] tablaDeProcesos = new String[this.procesosEnArchivoCsv.size() + 1][];
		tablaDeProcesos[0] = new String[] { "TR", "TA", "TI", "TAM" };
		
		for (int i = 1; i < tablaDeProcesos.length; i++) {
			tablaDeProcesos[i] = new String[] { this.procesosEnArchivoCsv.get(i - 1).getId(), this.procesosEnArchivoCsv.get(i - 1).getTiempoDeArribo().toString(), this.procesosEnArchivoCsv.get(i - 1).getTiempoDeIrrupcion().toString(), this.procesosEnArchivoCsv.get(i - 1).getTamanho().toString() };
		}
		
		//System.out.println("Tabla De Procesos");
		logueoInicial.setTexto( logueoInicial.getTexto().concat("Tabla De Procesos" + SALTO_DE_LINEA) );
		//System.out.println("TR TA TI TAM");
		logueoInicial.setTexto( logueoInicial.getTexto().concat("TR TA TI TAM" + SALTO_DE_LINEA) );
		for (Proceso proceso : this.procesosEnArchivoCsv) {
			//System.out.println(proceso.getId() + "  " + proceso.getTiempoDeArribo() + "  " + proceso.getTiempoDeIrrupcion() + "  " + proceso.getTamanho());
			logueoInicial.setTexto( logueoInicial.getTexto().concat(proceso.getId() + "  " + proceso.getTiempoDeArribo() + "  " + proceso.getTiempoDeIrrupcion() + "  " + proceso.getTamanho() + SALTO_DE_LINEA) );
			
		}
		//System.out.println("");
		logueoInicial.setTexto( logueoInicial.getTexto().concat(SALTO_DE_LINEA) );
		
		//System.out.println("Particiones de Memoria Principal");
		logueoInicial.setTexto( logueoInicial.getTexto().concat("Particiones de Memoria Principal" + SALTO_DE_LINEA) );
		Particion particionSo = this.memoriaPrincipal.getParticionSo();
		//System.out.println("Nombre: " + particionSo.getId() + " Tamanho: " + particionSo.getTamanho() + " kB, Direccion de inicio: " + particionSo.getDireccionInicio());
		logueoInicial.setTexto( logueoInicial.getTexto().concat("Nombre: " + particionSo.getId() + " Tamanho: " + particionSo.getTamanho() + " kB, Direccion de inicio: " + particionSo.getDireccionInicio() + SALTO_DE_LINEA) );
		
		for (Particion particion : this.memoriaPrincipal.getParticiones()) {
			//System.out.println("Nombre: " + particion.getId() + ", Tamanho: " + particion.getTamanho() + " kB, Direccion de inicio: " + particion.getDireccionInicio() );
			logueoInicial.setTexto( logueoInicial.getTexto().concat("Nombre: " + particion.getId() + ", Tamanho: " + particion.getTamanho() + " kB, Direccion de inicio: " + particion.getDireccionInicio()  + SALTO_DE_LINEA) );
		}
//		//System.out.println("");
//		logueoInicial.setTexto( logueoInicial.getTexto().concat(SALTO_DE_LINEA) );
		//System.out.println("____________________________________________________________________________________________");
		logueoInicial.setTexto( logueoInicial.getTexto().concat("____________________________________________________________________________________________" + SALTO_DE_LINEA));
		//System.out.println("");
		logueoInicial.setTexto( logueoInicial.getTexto().concat(SALTO_DE_LINEA) );
	}
	
	public void realizarLogueoFinal() {
		final String SALTO_DE_LINEA = "\n";
		logueoFinal = new Logueo();
		logueoFinal.setTexto("");
		//System.out.println("Los procesos terminaron su ejecucion en el siguiente orden: ");
		logueoFinal.setTexto(logueoFinal.getTexto() + "Los procesos terminaron su ejecucion en el siguiente orden: " + SALTO_DE_LINEA);
		//System.out.println(this.colaDeFinalizados);
		logueoFinal.setTexto(logueoFinal.getTexto() + this.colaDeFinalizados + SALTO_DE_LINEA);
		//System.out.println("ESTADISTICAS");
		logueoFinal.setTexto(logueoFinal.getTexto() + "ESTADISTICAS" + SALTO_DE_LINEA);
		//System.out.println("Cantidad de swappings realizados: " + this.cantidadDeSwappings.valor);
		logueoFinal.setTexto(logueoFinal.getTexto() + "Cantidad de swappings realizados: " + this.cantidadDeSwappings.valor + SALTO_DE_LINEA);
		//System.out.println("Tiempo de Retorno Promedio: " + this.planificadorServicio.obtenerTiempoDeRetornoPromedio(this.colaDeFinalizados));
		logueoFinal.setTexto(logueoFinal.getTexto() + "Tiempo de Retorno Promedio: " + this.planificadorServicio.obtenerTiempoDeRetornoPromedio(this.colaDeFinalizados) + SALTO_DE_LINEA);
		//System.out.println("Tiempo de Espera Promedio: " + this.planificadorServicio.obtenerTiempoDeEsperaPromedio(this.colaDeFinalizados));
		logueoFinal.setTexto(logueoFinal.getTexto() + "Tiempo de Espera Promedio: " + this.planificadorServicio.obtenerTiempoDeEsperaPromedio(this.colaDeFinalizados) + SALTO_DE_LINEA);
	}
	
}
