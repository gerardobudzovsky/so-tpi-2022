package tpi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import tpi.constantes.Constantes;
import tpi.constantes.Estado;
import tpi.entidades.Particion;
import tpi.entidades.Proceso;

public class PlanificadorServicio {

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
		}
		return null;
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
	
	public List<Proceso> sjf(List<Proceso> colaDeNuevos, List<Proceso> procesosEnArchivoCsv, Integer tiempo){
		
		
		for (Proceso proceso : procesosEnArchivoCsv) {
			if (proceso.getTiempoDeArribo().equals(tiempo)) {
				proceso.setEstado(Estado.NUEVO);
				colaDeNuevos.add(proceso);
			}
		}
		colaDeNuevos.sort(Comparator.comparing(Proceso::getTiempoDeIrrupcion));
		return colaDeNuevos;
	}
	
	public void worstFit() {

//        //Worst-Fit
//		for (Proceso proceso : colaDeNuevos) {
//			
//			Integer espacioRemanenteMaximo = 0;
//			for (Particion particion : this.memoriaPrincipal.getParticiones()) {
//				
//				Integer espacioRemanente = particion.getTamanho() - proceso.getTamanho();
//				
//				if (! particion.getId().equals("SO") & espacioRemanente > espacioRemanenteMaximo ) {
//					espacioRemanenteMaximo = espacioRemanente;
//					particion.setProceso(proceso);
//				}
//			}				
//		}
//		
//		for (Particion particion : memoriaPrincipal.getParticiones()) {
//			if (particion.getId() != "SO") {
//				System.out.println("Particion " + particion.getId() );
//				System.out.println("Proceso " + particion.getProceso());
//			}
//
//		}
		
	}
	
}