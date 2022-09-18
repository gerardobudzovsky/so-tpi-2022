package consigna11;

import java.util.ArrayList;
import java.util.List;

public class TestSimulador {

    public static void main (String args[]){

        Memoria memoria = new Memoria();
        memoria.setTamanho(650);

        Particion particionSo = new Particion(0, 150, null, 0, 149);
        Particion particion1 = new Particion(1, 100, null, 150, 249);
        Particion particion2 = new Particion(2, 150, null, 250, 399);
        Particion particion3 = new Particion(3, 250, null, 400, 649);

        List<Particion> particiones = new ArrayList<Particion>();
        particiones.add(particionSo);
        particiones.add(particion1);
        particiones.add(particion2);
        particiones.add(particion3);
        memoria.setParticiones(particiones);

        Proceso so = new Proceso("SO", 150);
        Proceso p1 = new Proceso("P1", 100);
        Proceso p2 = new Proceso("P2", 150);
        Proceso p3 = new Proceso("P3", 250);

        particionSo.setProceso(so);
        particion1.setProceso(p1);
        particion2.setProceso(p2);
        particion3.setProceso(p3);

        System.out.println("Tamanho memoria: " +  memoria.getTamanho() + " kB");

        System.out.printf("%15s %15s %15s %15s", "Proceso ID", "Particion ID", "Direccion inicio", "Tamanho particion");
        System.out.println();
        System.out.println("-----------------------------------------------------------------------------");
        for (Particion particion: memoria.getParticiones()) {
            System.out.printf("%15s %15s %15s %15s", particion.getProceso().getId(), particion.getId(),  particion.getDireccionInicio(), particion.getTamanho());
            System.out.println();
        }

    }
}
