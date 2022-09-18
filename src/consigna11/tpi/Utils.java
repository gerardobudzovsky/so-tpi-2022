package tpi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Utils {

	public static List<Proceso> leerProcesos() {

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(Constantes.NOMBRE_CSV))) {

			String linea = bufferedReader.readLine();
			List<Proceso> procesos = new ArrayList<Proceso>();
			while (linea != null) {
				String[] campos = linea.split(Constantes.SEPARADOR);

				Proceso proceso = new Proceso();

				proceso.setId(campos[0]);
				proceso.setTiempoDeArribo(Integer.valueOf(campos[1]));
				proceso.setTiempoDeIrrupcion(Integer.valueOf(campos[2]));
				proceso.setTamanho(Integer.valueOf(campos[3]));
				procesos.add(proceso);

				linea = bufferedReader.readLine();
			}

			return procesos;
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

}
