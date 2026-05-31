package br.com.rts.eventmanager.catalogo.movimentacao.services.impl;

import br.com.rts.eventmanager.catalogo.movimentacao.entities.Movimentacao;
import br.com.rts.eventmanager.catalogo.movimentacao.repositories.MovimentacaoRepository;
import br.com.rts.eventmanager.catalogo.movimentacao.services.MovimentacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovimentacaoServiceImpl implements MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;

    @Override
    public Page<Movimentacao> findAllByInstituicaoAndEvento(Long instituicaoId, Long eventoId, Pageable pageable) {
        return movimentacaoRepository.findAllByInstituicaoAndEvento(instituicaoId, eventoId, pageable);
    }

    @Override
    public Movimentacao findByIdAndInstituicao(Long movimentacaoId, Long instituicaoId, Long eventoId) {
        return movimentacaoRepository.findByIdAndInstituicaoAndEvento(movimentacaoId, instituicaoId, eventoId);
    }

    @Override
    public Movimentacao create(Movimentacao movimentacao, Long instituicaoId, Long eventoId) {
        //TODO: Validar instituicao e evento e adicionar a entidade
        movimentacao.setInstituicao(instituicaoId);
        movimentacao.setEvento(eventoId);
        return movimentacaoRepository.save(movimentacao);
    }

    @Override
    public Movimentacao update(Long movimentacaoId, Movimentacao movimentacaoUpdate, Long instituicaoId, Long eventoId) {
        final Movimentacao movimentacao = this.findByIdAndInstituicao(movimentacaoId, instituicaoId, eventoId);
        movimentacao.setQuantidade(movimentacaoUpdate.getQuantidade());
        movimentacao.setTipoMovimentacao(movimentacaoUpdate.getTipoMovimentacao());
        movimentacao.setMotivoMovimentacao(movimentacaoUpdate.getMotivoMovimentacao());
        movimentacao.setValorUnitario(movimentacaoUpdate.getValorUnitario());
        movimentacao.setDataMovimentacao(movimentacaoUpdate.getDataMovimentacao());
        return movimentacaoRepository.save(movimentacao);
    }

    @Override
    public void delete(Long movimentacaoId, Long instituicaoId, Long eventoId) {
        final Movimentacao movimentacao = this.findByIdAndInstituicao(movimentacaoId, instituicaoId, eventoId);
        movimentacaoRepository.delete(movimentacao);

    }

}