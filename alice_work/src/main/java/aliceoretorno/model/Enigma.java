package aliceoretorno.model;

public class Enigma {

    private final String pergunta;
    private final String respostaCorreta;
    // true = validação flexível (contém palavra-chave), false = correspondência exata
    private final boolean validacaoFlexivel;
    // dica opcional exibida quando o jogador usa o botão de dica (pode ser null)
    private final String dica;

    public Enigma(String pergunta, String respostaCorreta, boolean validacaoFlexivel) {
        this(pergunta, respostaCorreta, validacaoFlexivel, null);
    }

    public Enigma(String pergunta, String respostaCorreta, boolean validacaoFlexivel, String dica) {
        this.pergunta = pergunta;
        this.respostaCorreta = respostaCorreta;
        this.validacaoFlexivel = validacaoFlexivel;
        this.dica = dica;
    }

    public String getPergunta() {
        return pergunta;
    }

    public String getDica() {
        return dica;
    }

    public boolean temDica() {
        return dica != null && !dica.isBlank();
    }

    /**
     * Valida a resposta do jogador.
     * Validação flexível: aceita se a resposta CONTÉM a palavra-chave (case-insensitive).
     * Validação exata: requer correspondência exata, ignorando espaços em branco e caixa.
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
