package br.gov.pa.prodepa.pae.tramitar.port;

import java.util.List;

import br.gov.pa.prodepa.pae.tramitar.dto.ProtocoloDto;
import br.gov.pa.prodepa.pae.tramitar.model.Tramitacao;

public interface TramitacaoRepository {

    void save(List<Tramitacao> tramitacoes);

}
