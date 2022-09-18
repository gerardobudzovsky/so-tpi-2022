package consigna11;

public class Proceso {

    private String id;
    private Integer tamanho;

    public Proceso(String id, Integer tamanho) {
        super();
        this.id = id;
        this.tamanho = tamanho;
    }

    public Proceso() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
