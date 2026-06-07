package br.com.rts.eventmanager.seguranca.permissao.enumerators;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AcaoPermissaoEnum {

    LISTAR("Listar"),
    CADASTRAR("Cadastrar"),
    EDITAR("Editar"),
    DELETAR("Deletar"),
    IMPRIMIR("Imprimir"),
    ATIVAR("Ativar/Desativar"),
    ACESSO("Acesso");

    private final String descricao;

}
