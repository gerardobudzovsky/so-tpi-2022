package tpi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import tpi.constantes.Constantes;
import tpi.entidades.Cpu;
import tpi.entidades.MemoriaPrincipal;
import tpi.entidades.Particion;
import tpi.entidades.Proceso;

public class Planificador {

	private Cpu cpu;
	private MemoriaPrincipal memoriaPrincipal;
	private List<Proceso> colaDeListos;
	private List<Proceso> colaDeNuevos; // cola de nuevos está en memoria secundaria
	private List<Proceso> colaDeListosSuspendidos;
	private Integer tiempo;

	public Planificador() {
		this.cpu = new Cpu();
		this.tiempo = 0;
		this.colaDeListos = new ArrayList<Proceso>();
		this.colaDeNuevos = new ArrayList<Proceso>();
		this.colaDeListosSuspendidos = new ArrayList<Proceso>();
		this.memoriaPrincipal = new MemoriaPrincipal();
		this.memoriaPrincipal.setTamanho(Constantes.TAMANHO_PARTICION_SO + Constantes.TAMANHO_PARTICION_T_GRANDES + Constantes.TAMANHO_PARTICION_T_MEDIANOS + Constantes.TAMANHO_PARTICION_T_PEQUENHOS);
		List<Particion> particiones = this.inicializarParticiones();		
		this.memoriaPrincipal.setParticiones(particiones);
	}
	
	public void ejecutar(List<Proceso> procesosEnCsv) {
		
		Integer cantidadDeProcesosFinalizados = 0;
		String gantt = "";
		
		System.out.println("Memoria Principal");
		System.out.println(memoriaPrincipal);
		
		do {
			
			//SJF
			for (Proceso proceso : procesosEnCsv) {
				if (proceso.getTiempoDeArribo().equals(this.tiempo))
					this.colaDeNuevos.add(proceso);
			}
			this.colaDeNuevos.sort(Comparator.comparing(Proceso::getTiempoDeIrrupcion));
			
//	        //Worst-Fit
//			for (Proceso proceso : colaDeNuevos) {
//				
//				Integer espacioRemanenteMaximo = 0;
//				for (Particion particion : this.memoriaPrincipal.getParticiones()) {
//					
//					Integer espacioRemanente = particion.getTamanho() - proceso.getTamanho();
//					
//					if (! particion.getId().equals("SO") & espacioRemanente > espacioRemanenteMaximo ) {
//						espacioRemanenteMaximo = espacioRemanente;
//						particion.setProceso(proceso);
//					}
//				}				
//			}
//			
//			for (Particion particion : memoriaPrincipal.getParticiones()) {
//				if (particion.getId() != "SO") {
//					System.out.println("Particion " + particion.getId() );
//					System.out.println("Proceso " + particion.getProceso());
//				}
//
//			}
					
			                                                                                                                                                                                            
			if (cpu.getProceso() == null) {
	        	cpu.setProceso(colaDeNuevos.get(0));
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
	       
			
		} while (cantidadDeProcesosFinalizados < procesosEnCsv.size());
		
	}
	
	public List<Particion> inicializarParticiones(){
		
		List<Particion> particiones = new ArrayList<Particion>();
		
		Particion particionSo = new Particion();
		particionSo.setId("SO");
		particionSo.setTamanho(Constantes.TAMANHO_PARTICION_SO);
		particionSo.setDireccionInicio(0);
		particionSo.setDireccionFinal(Constantes.TAMANHO_PARTICION_SO - 1);
		particiones.add(particionSo);
		
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
	
}