package aliceoretorno.model;

public class Jogador {
    private int id;
    private String username;
    private int engrenagens;
    private int pontosUpgrade;
    private int sanidadeMaxUpgrade;
    private int tempoBaseUpgrade;

    public Jogador(int id, String username, int engrenagens, int pontosUpgrade, int sanidadeMaxUpgrade, int tempoBaseUpgrade) {
        this.id = id;
        this.username = username;
        this.engrenagens = engrenagens;
        this.pontosUpgrade = pontosUpgrade;
        this.sanidadeMaxUpgrade = sanidadeMaxUpgrade;
        this.tempoBaseUpgrade = tempoBaseUpgrade;
    }
    public Jogador() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getEngrenagens() {
        return engrenagens;
    }

    public void setEngrenagens(int engrenagens) {
        this.engrenagens = engrenagens;
    }

    public int getPontosUpgrade() {
        return pontosUpgrade;
    }

    public void setPontosUpgrade(int pontosUpgrade) {
        this.pontosUpgrade = pontosUpgrade;
    }

    public int getSanidadeMaxUpgrade() {
        return sanidadeMaxUpgrade;
    }

    public void setSanidadeMaxUpgrade(int sanidadeMaxUpgrade) {
        this.sanidadeMaxUpgrade = sanidadeMaxUpgrade;
    }

    public int getTempoBaseUpgrade() {
        return tempoBaseUpgrade;
    }

    public void setTempoBaseUpgrade(int tempoBaseUpgrade) {
        this.tempoBaseUpgrade = tempoBaseUpgrade;
    }

    
}