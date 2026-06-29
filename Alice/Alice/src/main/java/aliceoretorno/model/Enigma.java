package aliceoretorno.model;

public class Enigma {

    private final String pergunta;
    private final String respostaCorreta;
    // true = validação flexível (contém palavra-chave), false = correspondência exata
    private final boolean validacaoFlexivel;

    public Enigma(String pergunta, String respostaCorreta, boolean validacaoFlexivel) {
        this.pergunta = pergunta;
        this.respostaCorreta = respostaCorreta;
        this.validacaoFlexivel = validacaoFlexivel;
    }

    public String getPergunta() {
        return pergunta;
    }

    /**
     * Valida a resposta do jogador.
     * Nível 1 (flexível): aceita se a resposta CONTÉM a palavra-chave (case-insensitive).
     * Demais níveis (exato): requer correspondência exata, ignorando espaços em branco.
     */
    public boolean validarResposta(String respostaJogador) {
        if (respostaJogador == null) return false;

        String tentativa = respostaJogador.trim().toLowerCase();
        String esperada  = respostaCorreta.trim().toLowerCase();

        if (validacaoFlexivel) {
            return tentativa.contains(esperada);
        } else {
            return tentativa.equals(esperada);
        }
    }
}
