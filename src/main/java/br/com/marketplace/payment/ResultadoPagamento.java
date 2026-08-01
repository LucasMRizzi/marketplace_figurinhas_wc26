package br.com.marketplace.payment;

public record ResultadoPagamento (
        boolean aprovado,
        String codigoTransacao,
        String mensagem
){

    public static ResultadoPagamento aprovado(
            String codigoTransacao
    ) {
        return new ResultadoPagamento(
                true,
                codigoTransacao,
                "Pagamento aprovado."
        );
    }

    public static ResultadoPagamento recusado(
            String mensagem
    ) {
        return new ResultadoPagamento(
                false,
                null,
                mensagem
        );
    }

}
