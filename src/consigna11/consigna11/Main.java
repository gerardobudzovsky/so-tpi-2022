package consigna11;
import java.io.*;
import java.util.*;
public class Main {

    public static final String SEPARADOR = ",";
    
    public static void main(String args[]){ 

       
    	Scanner sc = new Scanner(System.in);
       System.out.println("Ingrese el nombre del archivo CSV: ");
        String nombreCSV = sc.nextLine();

        try ( BufferedReader br = new BufferedReader(new FileReader(nombreCSV))) {

            String linea = br.readLine();
            List<Proceso> procesos = new ArrayList<Proceso>();
            while (linea != null) {
                String[] campos = linea.split(SEPARADOR);

                Proceso proceso = new Proceso();
            
				proceso.setId(campos[0]);
				proceso.setTiempoDeArribo(Integer.valueOf(campos[1]));
				proceso.setTiempoDeIrrupcion(Integer.valueOf(campos[2]));
				proceso.setTamanho(Integer.valueOf(campos[3]));
				procesos.add(proceso);
			
                
                linea = br.readLine();
            }
            System.out.println(procesos);

        } catch (IOException e) {

            System.out.println(e.getMessage());
        }
    }
}