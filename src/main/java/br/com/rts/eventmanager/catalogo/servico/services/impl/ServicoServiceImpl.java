package br.com.rts.eventmanager.catalogo.servico.services.impl;

import br.com.rts.eventmanager.catalogo.servico.entities.Servico;
import br.com.rts.eventmanager.catalogo.servico.repositories.ServicoRepository;
import br.com.rts.eventmanager.catalogo.servico.services.ServicoService;
import br.com.rts.eventmanager.utils.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServicoServiceImpl implements ServicoService {

    private final ServicoRepository repository;

    @Override
    public Page<Servico> findAllByInstituicaoAndEvento(Long instituicaoId, Long eventoId, Pageable pageable) {
        return repository.findAllByInstituicaoAndEvento(instituicaoId, eventoId, pageable);
    }

    @Override
    public Servico findByInstituicaoAndEvento(Long servicoId, Long instituicaoId, Long eventoId) {
        return repository.findByIdAndInstituicaoAndEvento(servicoId, instituicaoId, eventoId)
                .orElseThrow(() -> new NotFoundException("servico não encontrado!"));

    }

    @Override
    public Servico create(Servico servico, Long instituicaoId, Long eventoId) {
        //TODO: Validar se instituicao e evento estao corretos, atribuir á entidade
        servico.setInstituicao(instituicaoId);
        servico.setEvento(eventoId);
        return repository.save(servico);
    }

    @Override
    public Servico update(Long servicoId, Servico servicoUpdate, Long instituicaoId, Long eventoId) {
        Servico servico = this.findByInstituicaoAndEvento(servicoId, instituicaoId, eventoId);

        servico.setNome(servicoUpdate.getNome());
        servico.setValorVenda(servicoUpdate.getValorVenda());
        if (servicoUpdate.getCategoria() != null) {
            servico.setCategoria(servicoUpdate.getCategoria());
        }
        if (servicoUpdate.getSubCategoria() != null) {
            servico.setSubCategoria(servicoUpdate.getSubCategoria());
        }
        return repository.save(servico);
    }

    @Override
    public void delete(Long servicoId, Long instituicaoId, Long eventoId) {
        final Servico servico = this.findByInstituicaoAndEvento(servicoId, instituicaoId, eventoId);

        repository.delete(servico);
    }

}
