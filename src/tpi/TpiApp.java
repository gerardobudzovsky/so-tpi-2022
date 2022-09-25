package tpi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import tpi.entidades.Proceso;
import tpi.utils.Utils;

public class TpiApp {

	public static void main(String args[]) {

		List<Proceso> procesos = Utils.leerProcesos();
		Planificador planificador = new Planificador();
		planificador.ejecutar(procesos);
		

//        MemoriaPrincipal memoria = new MemoriaPrincipal();
//        memoria.setTamanho(650);
//
//        Particion particionSo = new Particion(0, 150, null, 0, 149);
//        Particion particion1 = new Particion(1, 100, null, 150, 249);
//        Particion particion2 = new Particion(2, 150, null, 250, 399);
//        Particion particion3 = new Particion(3, 250, null, 400, 649);
//
//        List<Particion> particiones = new ArrayList<Particion>();
//        particiones.add(particionSo);
//        particiones.add(particion1);
//        particiones.add(particion2);
//        particiones.add(particion3);
//        memoria.setParticiones(particiones);

//        Proceso so = new Proceso("SO", 150);
//        Proceso p1 = new Proceso("P1", 100);
//        Proceso p2 = new Proceso("P2", 150);
//        Proceso p3 = new Proceso("P3", 250);

//        particionSo.setProceso(so);
//        particion1.setProceso(p1);
//        particion2.setProceso(p2);
//        particion3.setProceso(p3);

//        System.out.println("Tamanho memoria: " +  memoria.getTamanho() + " kB");
//
//        System.out.printf("%15s %15s %15s %15s", "Proceso ID", "Particion ID", "Direccion inicio", "Tamanho particion");
//        System.out.println();
//        System.out.println("-----------------------------------------------------------------------------");
//        for (Particion particion: memoria.getParticiones()) {
//            System.out.printf("%15s %15s %15s %15s", particion.getProceso().getId(), particion.getId(),  particion.getDireccionInicio(), particion.getTamanho());
//            System.out.println();
//        }

		// Scanner sc = new Scanner(System.in);
		// System.out.println("Ingrese el nombre del archivo CSV: ");
		// String nombreCSV = sc.nextLine();

//         System.out.println("Procesos desordenados: ");
//         System.out.println(procesos);
//         procesos.sort(Comparator.comparing(Proceso::getTiempoDeArribo));
//         System.out.println("Procesos ordenados por Tiempo De Arribo: ");
//         System.out.println(procesos);

	}
}
