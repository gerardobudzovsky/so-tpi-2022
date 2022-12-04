package tpi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tpi.constantes.Constantes;
import tpi.constantes.Estado;
import tpi.entidades.CantidadDeProcesosFinalizados;
import tpi.entidades.CantidadDeSwappings;
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
		particionSo.setProceso(new Proceso("SO", 100, 0, 0, Estado.EN_EJECUCION,0,0,0));
		particionSo.setDireccionInicio(0);
		particionSo.setDireccionFinal(Constantes.TAMANHO_PARTICION_SO - 1);

		return particionSo;
	}

	public List<Proceso> leerProcesos(String pathDeArchivo){
		if(pathDeArchivo == null) {
			System.out.println("Debe ingresar un archivo CSV.");
			System.exit(0);
		}
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(pathDeArchivo))) {

			String linea = bufferedReader.readLine();

			List<Proceso> procesos = new ArrayList<Proceso>();
			Integer cantidadDeProcesos = 0;
			Boolean isAValidHeader = Objects.equals(linea, "TR,TA,TI,TAM");
			String validProcess = "^T(?:\\+|-)?\\d+[,](?:\\+|-)?\\d+[,](?:\\+|-)?\\d+[,](?:\\+|-)?\\d+";
			Pattern pattern = Pattern.compile(validProcess);

			if(!isAValidHeader) {
				System.out.println("Error: El header tiene un formato invalido");
				System.exit(0);
			}
			Integer numeroLines = 0;

			while (linea != null) {
				if(numeroLines == 0) {
					//headers
					numeroLines = numeroLines+1;
					linea = bufferedReader.readLine();
				} else {

					String[] campos = linea.split(Constantes.SEPARADOR);
					Matcher emparejador = pattern.matcher(linea);
					boolean isAValidProcess = emparejador.find();

					Proceso proceso = new Proceso();
					if(linea.trim().equals("") || linea.trim().equals("\n")) {
						System.out.println("Error: CSV inconsistente. Hay una linea vacia");
						System.exit(0);
					}

					if(!isAValidProcess) {
						System.out.println("Error: el CSV tiene formato incorrecto");
						System.exit(0);
					}

					if(isAValidProcess) {
						int ta = Integer.parseInt(campos[1]);
						int ti = Integer.parseInt(campos[2]);
						int tam = Integer.parseInt(campos[3]);
						if(ta < 0 || ti <0 || tam < 0) {
							System.out.println("Error: el CSV no puede contener numeros negativos");
							System.exit(0);
						}
					}

					if(campos[0].equals("") || campos[0].equals(" ")) {
						System.out.println("Error: Existe un proceso sin id.");
						System.exit(0);
					}

					if(campos[1].equals("") || campos[1].equals(" ")) {
						System.out.println("Error: Existe un proceso sin tiempo de arribo.");
						System.exit(0);
					}

					if(campos[2].equals("") || campos[2].equals(" ")) {
						System.out.println("Error: Existe un proceso sin tiempo irrupción.");
						System.exit(0);
					}

					//esta validacion corresponde al tamaño ya que si no viene el campo tiene length 3. En los otros casos rompe antes.
					if(campos.length == 3 ) {
						System.out.println("Error: Existe un proceso sin tamaño.");
						System.exit(0);
					}
					Integer tamanhoDeProceso = Integer.valueOf(campos[3]);

					if (tamanhoDeProceso <= Constantes.TAMANHO_PARTICION_T_GRANDES) {
						proceso.setTamanho(tamanhoDeProceso);
					} else {
						System.out.println("Error: Existe un proceso que excede el tamaño permitido.");
						System.exit(0);
					}

					proceso.setId(campos[0]);
					proceso.setTiempoDeArribo(Integer.valueOf(campos[1]));
					proceso.setTiempoDeIrrupcion(Integer.valueOf(campos[2]));
					proceso.setTiempoDeIrrupcionOriginal(Integer.valueOf(campos[2]));

					procesos.add(proceso);
					cantidadDeProcesos++;
					numeroLines++;
					linea = bufferedReader.readLine();
				}

			}

			if(numeroLines == 1) {
				System.out.println("Error: El CSV no tiene procesos");
				System.exit(0);
			}
			if (cantidadDeProcesos <= Constantes.CANTIDAD_MINIMA_DE_PROCESOS_EN_CSV) {
						System.out.println("Error: El CSV esta vacío");
						System.exit(0);
					}

			if (cantidadDeProcesos >= Constantes.CANTIDAD_MAXIMA_DE_PROCESOS_EN_CSV) {
				System.out.println("Error: La cantidad de procesos es mayor al maximo permitido");
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
			memoriaPrincipal.getParticiones().get(indexDeParticionConMayorEspacioRemanente).setFragmentacionInterna(espacioRemanenteMaximo);		
	}
	
	public void worstFitEnParticionesCandidatasAlSwapping(Proceso procesoAAsignar, List<Particion> particionesCandidatasAlSwapping, CantidadDeSwappings cantidadDeSwappings) {
		
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
		cantidadDeSwappings.valor++;
		particionesCandidatasAlSwapping.get(indexDeParticionConMayorEspacioRemanente).setFragmentacionInterna(espacioRemanenteMaximo);

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
	
	public void iterarSobreColaDeNuevos(Cpu cpu, MemoriaPrincipal memoriaPrincipal, List<Proceso> colaDeNuevos, List<Proceso> colaDeAdmitidos, Integer tiempo, CantidadDeProcesosFinalizados cantidadDeProcesosFinalizados, CantidadDeSwappings cantidadDeSwappings, List<Proceso> colaDeFinalizados) {
		
		if (colaDeAdmitidos.size() < Constantes.NIVEL_DE_MULTIPROGRAMACION) {
			
			if (cpu.getProceso() != null) {
				System.out.println("Proceso ejecutandose: " + cpu.getProceso().getId());
			} else {
				System.out.println("No hay proceso en ejecucion");
			}
			Proceso primerProcesoEnColaDeNuevos = colaDeNuevos.get(0);
			
			if (this.existeAlgunaParticionLibre(memoriaPrincipal)) {
					if (this.existeAlgunaParticionLibreDondeQuepaElProceso(memoriaPrincipal, primerProcesoEnColaDeNuevos)) {
						this.worstFitEnMemoriaPrincipal(primerProcesoEnColaDeNuevos, memoriaPrincipal);
						colaDeAdmitidos.add(primerProcesoEnColaDeNuevos);
						colaDeAdmitidos.sort(Comparator.comparing(Proceso::getTiempoDeIrrupcion));
						colaDeNuevos.remove(primerProcesoEnColaDeNuevos);
						
							if (colaDeNuevos.size() > 0) {
								this.iterarSobreColaDeNuevos(cpu, memoriaPrincipal, colaDeNuevos, colaDeAdmitidos, tiempo, cantidadDeProcesosFinalizados, cantidadDeSwappings, colaDeFinalizados);
							} else {
								this.trabajoEnCpu(cpu, colaDeAdmitidos, memoriaPrincipal, cantidadDeProcesosFinalizados, tiempo, colaDeFinalizados);
							}
						
					} else {
						if ( esFactibleHacerSwapping(memoriaPrincipal, primerProcesoEnColaDeNuevos, tiempo) ) {
							
							List<Particion> particionesCandidatasAlSwapping = this.obtenerParticionesCandidatasParaSwapping(memoriaPrincipal, primerProcesoEnColaDeNuevos, tiempo);
							this.worstFitEnParticionesCandidatasAlSwapping(primerProcesoEnColaDeNuevos, particionesCandidatasAlSwapping, cantidadDeSwappings);
							colaDeAdmitidos.add(primerProcesoEnColaDeNuevos);
							colaDeAdmitidos.sort(Comparator.comparing(Proceso::getTiempoDeIrrupcion));
							colaDeNuevos.remove(primerProcesoEnColaDeNuevos);
							
							if (colaDeNuevos.size() > 0) {
								this.iterarSobreColaDeNuevos(cpu, memoriaPrincipal, colaDeNuevos, colaDeAdmitidos, tiempo, cantidadDeProcesosFinalizados, cantidadDeSwappings, colaDeFinalizados);
							} else {
								this.trabajoEnCpu(cpu, colaDeAdmitidos, memoriaPrincipal, cantidadDeProcesosFinalizados, tiempo, colaDeFinalizados);
							}
							
							
						} else {
							primerProcesoEnColaDeNuevos.setEstado(Estado.LISTO_SUSPENDIDO);
							colaDeAdmitidos.add(primerProcesoEnColaDeNuevos);
							colaDeAdmitidos.sort(Comparator.comparing(Proceso::getTiempoDeIrrupcion));
							colaDeNuevos.remove(primerProcesoEnColaDeNuevos);
							
							if (colaDeNuevos.size() > 0) {
								this.iterarSobreColaDeNuevos(cpu, memoriaPrincipal, colaDeNuevos, colaDeAdmitidos, tiempo, cantidadDeProcesosFinalizados, cantidadDeSwappings, colaDeFinalizados);
							} else {
								this.trabajoEnCpu(cpu, colaDeAdmitidos, memoriaPrincipal, cantidadDeProcesosFinalizados, tiempo, colaDeFinalizados);
							}
						}
					}
			} else {
				
				if ( esFactibleHacerSwapping(memoriaPrincipal, primerProcesoEnColaDeNuevos, tiempo) ) {
					List<Particion> particionesCandidatasAlSwapping = this.obtenerParticionesCandidatasParaSwapping(memoriaPrincipal, primerProcesoEnColaDeNuevos, tiempo);
					this.worstFitEnParticionesCandidatasAlSwapping(primerProcesoEnColaDeNuevos, particionesCandidatasAlSwapping, cantidadDeSwappings);
					colaDeAdmitidos.add(primerProcesoEnColaDeNuevos);
					colaDeAdmitidos.sort(Comparator.comparing(Proceso::getTiempoDeIrrupcion));
					colaDeNuevos.remove(primerProcesoEnColaDeNuevos);
					
					if (colaDeNuevos.size() > 0) {
						this.iterarSobreColaDeNuevos(cpu, memoriaPrincipal, colaDeNuevos, colaDeAdmitidos, tiempo, cantidadDeProcesosFinalizados, cantidadDeSwappings, colaDeFinalizados);
					} else {
						this.trabajoEnCpu(cpu, colaDeAdmitidos, memoriaPrincipal, cantidadDeProcesosFinalizados, tiempo, colaDeFinalizados);
					}
				}else {
					primerProcesoEnColaDeNuevos.setEstado(Estado.LISTO_SUSPENDIDO);
					colaDeAdmitidos.add(primerProcesoEnColaDeNuevos);
					colaDeAdmitidos.sort(Comparator.comparing(Proceso::getTiempoDeIrrupcion));
					colaDeNuevos.remove(primerProcesoEnColaDeNuevos);
					
					if (colaDeNuevos.size() > 0) {
						this.iterarSobreColaDeNuevos(cpu, memoriaPrincipal, colaDeNuevos, colaDeAdmitidos, tiempo, cantidadDeProcesosFinalizados, cantidadDeSwappings, colaDeFinalizados);
					} else {
						this.trabajoEnCpu(cpu, colaDeAdmitidos, memoriaPrincipal, cantidadDeProcesosFinalizados, tiempo, colaDeFinalizados);
					}
				}

				
			}
			
		} else {
			this.trabajoEnCpu(cpu, colaDeAdmitidos, memoriaPrincipal, cantidadDeProcesosFinalizados, tiempo, colaDeFinalizados);
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
	
	public void trabajoEnCpu(Cpu cpu, List<Proceso> colaDeAdmitidos, MemoriaPrincipal memoriaPrincipal, CantidadDeProcesosFinalizados cantidadDeProcesosFinalizados, Integer tiempo, List<Proceso> colaDeFinalizados) {
		
		if (cpu.getProceso() != null) {
			
			cpu.getProceso().setTiempoDeIrrupcion(cpu.getProceso().getTiempoDeIrrupcion() -1);
			
			if (cpu.getProceso().getTiempoDeIrrupcion().equals(0)) {
				cpu.getProceso().setEstado(Estado.FINALIZADO);
				cantidadDeProcesosFinalizados.valor++;
				cpu.getProceso().setTiempoDeRetorno(tiempo - cpu.getProceso().getTiempoDeArribo());
				cpu.getProceso().setTiempoDeEspera(cpu.getProceso().getTiempoDeRetorno() - cpu.getProceso().getTiempoDeIrrupcionOriginal() + 1);
				System.out.println("Tiempo de Retorno de" + cpu.getProceso().getId() + " :" + cpu.getProceso().getTiempoDeRetorno());
				System.out.println("Tiempo de Espera de" + cpu.getProceso().getId() + " :" + cpu.getProceso().getTiempoDeEspera());
				colaDeFinalizados.add(cpu.getProceso());
				colaDeAdmitidos.remove(cpu.getProceso());
				
				for (Particion particion : memoriaPrincipal.getParticiones()) {
					if (particion.getProceso() != null && particion.getProceso().equals(cpu.getProceso())) {
						particion.setProceso(null);
						particion.setFragmentacionInterna(null);
					}
				}
				
				System.out.println("El proceso " + cpu.getProceso().getId() + " finalizo.");
				cpu.setProceso(null);
				
//				logueo.setEsInstanteConProcesosTerminados(true);
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

	public Integer obtenerTiempoDeRetornoPromedio(List<Proceso> colaDeFinalizados) {
		
		Integer tiempoDeRetornoPromedio = 0;
		
		for (Proceso proceso : colaDeFinalizados)
			tiempoDeRetornoPromedio += proceso.getTiempoDeRetorno();
		
		tiempoDeRetornoPromedio = tiempoDeRetornoPromedio/colaDeFinalizados.size();
		
		return tiempoDeRetornoPromedio;
	}
	
	public Integer obtenerTiempoDeEsperaPromedio(List<Proceso> colaDeFinalizados) {
		
		Integer tiempoDeEsperaPromedio = 0;
		
		for (Proceso proceso : colaDeFinalizados)
			tiempoDeEsperaPromedio += proceso.getTiempoDeEspera();
		
		tiempoDeEsperaPromedio = tiempoDeEsperaPromedio/colaDeFinalizados.size();
		
		return tiempoDeEsperaPromedio;
	}
}
