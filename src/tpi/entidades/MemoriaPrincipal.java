package tpi.entidades;

import java.util.List;

public class MemoriaPrincipal {
	private Particion particionSo;
    private List<Particion> particiones;
    private Integer tamanho;

    public MemoriaPrincipal() {
        super();
    }

    public MemoriaPrincipal(List<Particion> particiones, Integer tamanho) {
        super();
        this.particiones = particiones;
        this.tamanho = tamanho;
    }

    public Particion getParticionSo() {
		return particionSo;
	}

	public void setParticionSo(Particion particionSo) {
		this.particionSo = particionSo;
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
        return "Memoria{" +
                "particiones=" + particiones +
                '}';
    }
}
