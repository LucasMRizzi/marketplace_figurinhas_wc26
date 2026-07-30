package br.com.marketplace.entity.enums;

public enum TipoFigurinha {
    COMUM("Comum"),
    LEGEND_COMUM("Legend(comum)"),
    LEGEND_BRONZE("Legend(bronze)"),
    LEGEND_PRATA("Legend(prata)"),
    LEGEND_OURO("Legend(ouro)");

    private final String valorBanco;

    TipoFigurinha(String valorBanco) {
        this.valorBanco = valorBanco;
    }

    public String getValorBanco() {
        return valorBanco;
    }

    public static TipoFigurinha fromValorBanco(String valor){
        for(TipoFigurinha tipo : values()){
            if(tipo.valorBanco.equals(valor)) {
                return tipo;
            }
        }

        throw new IllegalArgumentException(
                "Tipo de figurinha inválido: " + valor
        );
    }
}