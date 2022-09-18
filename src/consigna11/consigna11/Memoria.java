package consigna11;

import java.util.List;

public class Memoria {
    private List<Particion> particiones;
    private Integer tamanho;

    public Memoria() {
        super();
    }

    public Memoria(List<Particion> particiones, Integer tamanho) {
        super();
        this.particiones = particiones;
        this.tamanho = tamanho;
    }

    public Integer getTamanho() {
        return tamanho;
    }

    public void setTamanho(Integer tamanho) {
        this.tamanho = tamanho;
    }

    public List<Particion> getParticiones() {
        return particiones;
    }

    public void setParticiones(List<Particion> particiones) {
        this.particiones = particiones;
    }

    @Override
    public String toString() {
        return "consigna11.Memoria{" +
                "particiones=" + particiones +
                '}';
    }
}
