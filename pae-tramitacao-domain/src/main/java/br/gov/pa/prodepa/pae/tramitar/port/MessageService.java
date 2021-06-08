package br.gov.pa.prodepa.pae.tramitar.port;

import br.gov.pa.prodepa.pae.tramitar.model.Tramitacao;

public interface MessageService {

	void enviarParaFilaDeTramitacoes(Tramitacao t);

}
