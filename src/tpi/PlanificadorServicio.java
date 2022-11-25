package tpi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import tpi.constantes.Constantes;
import tpi.constantes.Estado;
import tpi.entidades.Cpu;
import tpi.entidades.MemoriaPrincipal;
import tpi.entidades.Particion;
import tpi.entidades.Proceso;

public class PlanificadorServicio {

	public List<Particion> inicializarParticiones(){
		
		List<Particion> particiones = new ArrayList<Particion>();
				
		Particion particionTGrandes = new Particion();
		particionTGrandes.setId("T_GRANDES");
		particionTGrandes.setTamanho(Constantes.TAMANHO_PARTICION_T_GRANDES);
		particionTGrandes.setDireccionInicio(Constantes.TAMANHO_PARTICION_SO);
		particionTGrandes.setDireccionFinal(Constantes.TAMANHO_PARTICION_SO + Constantes.TAMANHO_PARTICION_T_GRANDES - 1);
		particiones.add(particionTGrandes);
		
		Particion particionTMedianos = new Particion();
		particionTMedianos.setId("T_MEDIANOS");
		particionTMedianos.setTamanho(Constantes.TAMANHO_PARTICION_T_MEDIANOS);
		particionTMedianos.setDireccionInicio(Constantes.TAMANHO_PARTICION_SO + Constantes.TAMANHO_PARTICION_T_GRANDES);
		particionTMedianos.setDireccionFinal(Constantes.TAMANHO_PARTICION_SO + Constantes.TAMANHO_PARTICION_T_GRANDES + Constantes.TAMANHO_PARTICION_T_MEDIANOS - 1);
		particiones.add(particionTMedianos);
		
		Particion particionTPequenhos = new Particion();
		particionTPequenhos.setId("T_PEQUENHOS");
		particionTPequenhos.setTamanho(Constantes.TAMANHO_PARTICION_T_PEQUENHOS);
		particionTPequenhos.setDireccionInicio(Constantes.TAMANHO_PARTICION_SO + Constantes.TAMANHO_PARTICION_T_GRANDES + Constantes.TAMANHO_PARTICION_T_MEDIANOS);
		particionTPequenhos.setDireccionFinal(Constantes.TAMANHO_PARTICION_SO + Constantes.TAMANHO_PARTICION_T_GRANDES + Constantes.TAMANHO_PARTICION_T_MEDIANOS + Constantes.TAMANHO_PARTICION_T_PEQUENHOS - 1);
		particiones.add(particionTPequenhos);
		
		return particiones;
	}
	
	public Particion inicializarParticionSo(){
		
		Particion particionSo = new Particion();
		particionSo.setId("SO");
		particionSo.setTamanho(Constantes.TAMANHO_PARTICION_SO);
		particionSo.setProceso(new Proceso("SO", 100, 0, 0, Estado.EN_EJECUCION));
		particionSo.setDireccionInicio(0);
		particionSo.setDireccionFinal(Constantes.TAMANHO_PARTICION_SO - 1);

		return particionSo;
	}

	public List<Proceso> leerProcesos() {

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(Constantes.NOMBRE_CSV))) {

			String linea = bufferedReader.readLine();
			List<Proceso> procesos = new ArrayList<Proceso>();
			Integer cantidadDeProcesos = 0;
			
			while (linea != null) {
				String[] campos = linea.split(Constantes.SEPARADOR);

				Proceso proceso = new Proceso();

				proceso.setId(campos[0]);
				proceso.setTiempoDeArribo(Integer.valueOf(campos[1]));
				proceso.setTiempoDeIrrupcion(Integer.valueOf(campos[2]));
				Integer tamanhoDeProceso = Integer.valueOf(campos[3]);
				
				if (tamanhoDeProceso <= Constantes.TAMANHO_PARTICION_T_GRANDES) {
					proceso.setTamanho(tamanhoDeProceso);
				} else {
					System.out.println("El tamanho del proceso " + proceso.getId() + " es mayor que la particion mas grande en memoria.");
					System.exit(0);
				}
				
				procesos.add(proceso);
				cantidadDeProcesos++;
				linea = bufferedReader.readLine();
			}
			
			if (cantidadDeProcesos < Constantes.CANTIDAD_MINIMA_DE_PROCESOS_EN_CSV) {
				System.out.println("La cantidad de procesos es menor al minimo permitido");
				System.exit(0);
			}
			
			if (cantidadDeProcesos > Constantes.CANTIDAD_MAXIMA_DE_PROCESOS_EN_CSV) {
				System.out.println("La cantidad de procesos es mayor al maximo permitido");
				System.exit(0);
			}
			
			return procesos;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.out.println("No es un archivo .csv");
			System.exit(0);
			
		}
		return new ArrayList<Proceso>();
	}	
	
	public List<Proceso> obtenerProcesosLlegadosEnElInstanteActual(List<Proceso> procesosEnArchivoCsv, Integer tiempo){
	
		List<Proceso> procesosLlegadosEnElInstanteActual = new ArrayList<Proceso>();
		
		for (Proceso proceso : procesosEnArchivoCsv) {
			if (proceso.getTiempoDeArribo().equals(tiempo))
				procesosLlegadosEnElInstanteActual.add(proceso);
		}
		
		return procesosLlegadosEnElInstanteActual;
	
	}
	
	public void worstFitEnMemoriaPrincipal(Proceso procesoAAsignar, MemoriaPrincipal memoriaPrincipal) {
			
			Integer indexDeParticionConMayorEspacioRemanente = 0;
			Integer espacioRemanenteMaximo = Integer.MIN_VALUE;
			Integer espacioRemanente = 0;
			
			for (Particion particion : memoriaPrincipal.getParticiones()) {
				
				if (particion.getProceso() == null && particion.getTamanho() >= procesoAAsignar.getTamanho()) {
					espacioRemanente = particion.getTamanho() - procesoAAsignar.getTamanho();
				}
								
				if (espacioRemanente > espacioRemanenteMaximo) {
					espacioRemanenteMaximo = espacioRemanente;
					indexDeParticionConMayorEspacioRemanente = memoriaPrincipal.getParticiones().indexOf(particion);
				}
			}
			procesoAAsignar.setEstado(Estado.LISTO);
			memoriaPrincipal.getParticiones().get(indexDeParticionConMayorEspacioRemanente).setProceso(procesoAAsignar);
		
	}
	
	public void worstFitEnParticionesCandidatasAlSwapping(Proceso procesoAAsignar, List<Particion> particionesCandidatasAlSwapping) {
		
		Integer indexDeParticionConMayorEspacioRemanente = 0;
		Integer espacioRemanenteMaximo = Integer.MIN_VALUE;
		Integer espacioRemanente = 0;
		
		for (Particion particion : particionesCandidatasAlSwapping) {
			
			if (particion.getTamanho() >= procesoAAsignar.getTamanho()) {
				espacioRemanente = particion.getTamanho() - procesoAAsignar.getTamanho();
			}
							
			if (espacioRemanente > espacioRemanenteMaximo) {
				espacioRemanenteMaximo = espacioRemanente;
				indexDeParticionConMayorEspacioRemanente = particionesCandidatasAlSwapping.indexOf(particion);
			}
		}
		
		particionesCandidatasAlSwapping.get(indexDeParticionConMayorEspacioRemanente).getProceso().setEstado(Estado.LISTO_SUSPENDIDO);;
		procesoAAsignar.setEstado(Estado.LISTO);
		particionesCandidatasAlSwapping.get(indexDeParticionConMayorEspacioRemanente).setProceso(procesoAAsignar);	
}

	public boolean existeAlgunaParticionLibre(MemoriaPrincipal memoriaPrincipal) {
		
		for (Particion particion : memoriaPrincipal.getParticiones()) {
			if (particion.getProceso() == null)
				return true;
		}
		
		return false;
		
	}
	
	public boolean existeAlgunaParticionLibreDondeQuepaElProceso(MemoriaPrincipal memoriaPrincipal, Proceso proceso) {
		
		for (Particion particion : memoriaPrincipal.getParticiones()) {
			if (particion.getProceso() == null && particion.getTamanho() >= proceso.getTamanho())
				return true;
		}
		return false;
		
	}
	
	public void iterarSobreColaDeNuevos(Cpu cpu, MemoriaPrincipal memoriaPrincipal, List<Proceso> colaDeNuevos, List<Proceso> colaDeAdmitidos, Integer tiempo, Integer cantidadDeProcesosFinalizados) {
		
		if (colaDeAdmitidos.size() < Constantes.NIVEL_DE_MULTIPROGRAMACION) {
			
			System.out.println("Cola de Nuevos: " + colaDeNuevos);
			System.out.println("Cola de Listos: " + this.mostrarColaDeListos(colaDeAdmitidos));
			System.out.println("Cola de Listos/Suspendidos: " + this.mostrarColaDeListosSuspendidos(colaDeAdmitidos));
			if (cpu.getProceso() != null) {
				System.out.println("Proceso ejecutandose: " + cpu.getProceso().getId());
			} else {
				System.out.println("No hay proceso en ejecucion");
			}
			Proceso primerProcesoEnColaDeNuevos = colaDeNuevos.get(0);
			System.out.println("Se tomó el proceso " + primerProcesoEnColaDeNuevos.getId() + " de la cola de nuevos.");
			
			if (this.existeAlgunaParticionLibre(memoriaPrincipal)) {
				System.out.println("Existe al menos una particion libre en Memoria Principal.");
					if (this.existeAlgunaParticionLibreDondeQuepaElProceso(memoriaPrincipal, primerProcesoEnColaDeNuevos)) {
						System.out.println("El proceso " + primerProcesoEnColaDeNuevos.getId() + " cabe en una particion libre de Memoria Principal.");
						this.worstFitEnMemoriaPrincipal(primerProcesoEnColaDeNuevos, memoriaPrincipal);
						System.out.println("El proceso " + primerProcesoEnColaDeNuevos.getId() + " se cargo en Memoria Principal.");
						colaDeAdmitidos.add(primerProcesoEnColaDeNuevos);
						colaDeAdmitidos.sort(Comparator.comparing(Proceso::getTiempoDeIrrupcion));
						colaDeNuevos.remove(primerProcesoEnColaDeNuevos);
						
							if (colaDeNuevos.size() > 0) {
								this.iterarSobreColaDeNuevos(cpu, memoriaPrincipal, colaDeNuevos, colaDeAdmitidos, tiempo, cantidadDeProcesosFinalizados);
							} else {
								this.trabajoEnCpu(cpu, colaDeAdmitidos, memoriaPrincipal, cantidadDeProcesosFinalizados);
							}
						
					} else {
						System.out.println("El proceso " + primerProcesoEnColaDeNuevos.getId() + " no cabe en ninguna particion libre de Memoria Principal.");
						if ( esFactibleHacerSwapping(memoriaPrincipal, primerProcesoEnColaDeNuevos, tiempo) ) {
							
							List<Particion> particionesCandidatasAlSwapping = this.obtenerParticionesCandidatasParaSwapping(memoriaPrincipal, primerProcesoEnColaDeNuevos, tiempo);
							this.worstFitEnParticionesCandidatasAlSwapping(primerProcesoEnColaDeNuevos, particionesCandidatasAlSwapping);
							System.out.println("El proceso " + primerProcesoEnColaDeNuevos.getId() + " se cargo en Memoria Principal.");
							colaDeAdmitidos.add(primerProcesoEnColaDeNuevos);
							colaDeAdmitidos.sort(Comparator.comparing(Proceso::getTiempoDeIrrupcion));
							colaDeNuevos.remove(primerProcesoEnColaDeNuevos);
							
							if (colaDeNuevos.size() > 0) {
								this.iterarSobreColaDeNuevos(cpu, memoriaPrincipal, colaDeNuevos, colaDeAdmitidos, tiempo, cantidadDeProcesosFinalizados);
							} else {
								this.trabajoEnCpu(cpu, colaDeAdmitidos, memoriaPrincipal, cantidadDeProcesosFinalizados);
							}
							
							
						} else {
							System.out.println("El proceso " + primerProcesoEnColaDeNuevos.getId() + " se cargo en Memoria Secundaria.");
							primerProcesoEnColaDeNuevos.setEstado(Estado.LISTO_SUSPENDIDO);
							colaDeAdmitidos.add(primerProcesoEnColaDeNuevos);
							colaDeAdmitidos.sort(Comparator.comparing(Proceso::getTiempoDeIrrupcion));
							colaDeNuevos.remove(primerProcesoEnColaDeNuevos);
							
							if (colaDeNuevos.size() > 0) {
								this.iterarSobreColaDeNuevos(cpu, memoriaPrincipal, colaDeNuevos, colaDeAdmitidos, tiempo, cantidadDeProcesosFinalizados);
							} else {
								this.trabajoEnCpu(cpu, colaDeAdmitidos, memoriaPrincipal, cantidadDeProcesosFinalizados);
							}
						}
					}
			} else {
				
				System.out.println("No hay ninguna particion libre en Memoria Principal.");
				if ( esFactibleHacerSwapping(memoriaPrincipal, primerProcesoEnColaDeNuevos, tiempo) ) {
					//TODO
				}else {
					//TODO
				}

				
			}
			
		} else {
			this.trabajoEnCpu(cpu, colaDeAdmitidos, memoriaPrincipal, cantidadDeProcesosFinalizados);
		}
	}
	
	public boolean esFactibleHacerSwapping(MemoriaPrincipal memoriaPrincipal, Proceso procesoNuevo, Integer tiempo) {

		List<Particion> particionesCandidatasParaSwapping = new ArrayList<Particion>();
		
		for (Particion particion : memoriaPrincipal.getParticiones()) {

			if (particion.getProceso() != null
					&& (particion.getProceso().getEstado() != Estado.EN_EJECUCION || tiempo.equals(0) )
					&& procesoNuevo.getTamanho() <= particion.getTamanho()
					&& procesoNuevo.getTiempoDeIrrupcion() < particion.getProceso().getTiempoDeIrrupcion()) {
				particionesCandidatasParaSwapping.add(particion);
			}
		}
		
		if (particionesCandidatasParaSwapping.isEmpty())
			return false;
		
		return true;
	}
	
	public List<Particion> obtenerParticionesCandidatasParaSwapping(MemoriaPrincipal memoriaPrincipal, Proceso procesoNuevo, Integer tiempo) {

		List<Particion> particionesCandidatasParaSwapping = new ArrayList<Particion>();
		
		for (Particion particion : memoriaPrincipal.getParticiones()) {

			if (particion.getProceso() != null
					&& (particion.getProceso().getEstado() != Estado.EN_EJECUCION || tiempo.equals(0) )
					&& procesoNuevo.getTamanho() <= particion.getTamanho()
					&& procesoNuevo.getTiempoDeIrrupcion() < particion.getProceso().getTiempoDeIrrupcion()) {
				particionesCandidatasParaSwapping.add(particion);
			}
		}
		
		return particionesCandidatasParaSwapping;
		
	}	
	
	public void trabajoEnCpu(Cpu cpu, List<Proceso> colaDeAdmitidos, MemoriaPrincipal memoriaPrincipal, Integer cantidadDeProcesosFinalizados) {
		
		if (cpu.getProceso() != null) {
			
			cpu.getProceso().setTiempoDeIrrupcion(cpu.getProceso().getTiempoDeIrrupcion() -1);
			
			if (cpu.getProceso().getTiempoDeIrrupcion().equals(0)) {
				cpu.getProceso().setEstado(Estado.SALIENTE);
				cantidadDeProcesosFinalizados++;
				colaDeAdmitidos.remove(cpu.getProceso());
				
				for (Particion particion : memoriaPrincipal.getParticiones()) {
					if (particion.getProceso() != null && particion.getProceso().equals(cpu.getProceso())) {
						particion.setProceso(null);
					}
				}
				
				System.out.println("El proceso " + cpu.getProceso().getId() + " finalizo.");
				cpu.setProceso(null);
			}
			
		} else {
			
			if (this.existeProcesoListoEnColaDeAdmitidos(colaDeAdmitidos)) {				
				List<Proceso> colaDeListos = new ArrayList<Proceso>();
				
				for (Proceso proceso : colaDeAdmitidos) {
					if (proceso.getEstado().equals(Estado.LISTO)) {
						colaDeListos.add(proceso);
					}
				}
				colaDeListos.sort(Comparator.comparing(Proceso::getTiempoDeIrrupcion));
				Proceso primerProcesoListo = new Proceso();
				
				//TODO hacer un metodo para el caso en que haya dos procesos en estado listo con el mismo tiempo de irrupicion
				//TODO en ese caso se debe elegir el proceso con el tiempo de arriba mas bajo
				if (colaDeListos.size() > 1 
						&& colaDeListos.get(0).getTiempoDeIrrupcion().equals(colaDeListos.get(1).getTiempoDeIrrupcion())
						&& colaDeListos.get(0).getTiempoDeArribo() >  colaDeListos.get(1).getTiempoDeArribo()) {
					primerProcesoListo = colaDeListos.get(1);
				}else {
					primerProcesoListo = colaDeListos.get(0);
				}
				primerProcesoListo.setEstado(Estado.EN_EJECUCION);
				cpu.setProceso(primerProcesoListo);
				cpu.getProceso().setTiempoDeIrrupcion(cpu.getProceso().getTiempoDeIrrupcion() -1);
			}

		}
		
	}
	
	public boolean existeProcesoListoEnColaDeAdmitidos(List<Proceso> colaDeAdmitidos) {
		for (Proceso proceso : colaDeAdmitidos) {
			if (proceso.getEstado().equals(Estado.LISTO)) 
				return true;
		}
		return false;
	}
	
	public String mostrarColaDeListos(List<Proceso> colaDeAdmitidos) {
		String salida = "[";
		
		for (Proceso proceso : colaDeAdmitidos) {
			if (proceso.getEstado().equals(Estado.LISTO)) {
				salida = salida + proceso.toString() + ", ";
			}
		}
		
		salida = salida + "]";
		return salida;
	}
	
	public String mostrarColaDeListosSuspendidos(List<Proceso> colaDeAdmitidos) {
		String salida = "[";
		
		for (Proceso proceso : colaDeAdmitidos) {
			if (proceso.getEstado().equals(Estado.LISTO_SUSPENDIDO)) {
				salida = salida + proceso.toString() + ", ";
			}
		}
		
		salida = salida + "]";
		return salida;
	}

	public boolean existenProcesosEnMemoriaSecundaria(List<Proceso> colaDeAdmitidos) {
		for (Proceso proceso : colaDeAdmitidos) {
			if (proceso.getEstado().equals(Estado.LISTO_SUSPENDIDO))
				return true;
		}
		return false;		
	}

	public Proceso obtenerPrimerProcesoEnMemoriaSecundaria(List<Proceso> colaDeAdmitidos) {
		for (Proceso proceso : colaDeAdmitidos) {
			if (proceso.getEstado().equals(Estado.LISTO_SUSPENDIDO))
				return proceso;
		}
		return new Proceso();	
	}
	
}
