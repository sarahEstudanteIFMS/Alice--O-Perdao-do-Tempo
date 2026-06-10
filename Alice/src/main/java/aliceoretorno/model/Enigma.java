package aliceoretorno.model;


public class Enigma {
    private final String pergunta;
    private final String respostaCorreta;
    private final boolean isLvl1;

    public Enigma(String pergunta, String respostaCorreta, boolean isLvl1) {
        this.pergunta = pergunta;
        this.respostaCorreta = respostaCorreta;
        this.isLvl1 = isLvl1;
    }

    public String getPergunta() { return pergunta; }

    public boolean validarResposta(String respostaJogador) {
        if (respostaJogador == null) return false;
        if (isLvl1) {
            // Regra do Nível 1: apenas conter a palavra "depende"
            return respostaJogador.toLowerCase().contains(respostaCorreta.toLowerCase());
        }
        // Demais níveis: correspondência exata desconsiderando espaços em branco nas pontas
        return respostaJogador.trim().equalsIgnoreCase(respostaCorreta.trim());
    }
}
