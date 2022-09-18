import java.io.*;
import java.util.*;
public class Main {

    public static final String SEPARADOR = ",";
    
    public static void main(String args[]){ 

       
    	//Scanner sc = new Scanner(System.in);
       //System.out.println("Ingrese el nombre del archivo CSV: ");
        String nombreCSV = "/entradaEjemplo.csv";//sc.nextLine();

        try ( BufferedReader br = new BufferedReader(new FileReader(nombreCSV))) {

            String linea = br.readLine();
            while (linea != null) {
                String[] campos = linea.split(SEPARADOR);
                System.out.println(Arrays.toString(campos));

                linea = br.readLine();
            }

        } catch (IOException e) {

            System.out.println(e.getMessage());
        }
    }
}