package br.com.rts.eventmanager.seguranca;

import br.com.rts.eventmanager.gestao.InstituicaoCriadaEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InstituicaoCriadaListener {

    private final SegurancaFacade segurancaFacade;

    @EventListener
    public void onInstituicaoCriada(InstituicaoCriadaEvent event) {
        segurancaFacade.vincularUsuarioAInstituicao(event.email(), event.instituicaoId());
    }
}
