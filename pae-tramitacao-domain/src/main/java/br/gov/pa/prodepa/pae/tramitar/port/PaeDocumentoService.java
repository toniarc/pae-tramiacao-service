package br.gov.pa.prodepa.pae.tramitar.port;

import java.util.List;

import br.gov.pa.prodepa.pae.tramitar.dto.DocumentoProtocoladoDto;
import br.gov.pa.prodepa.pae.tramitar.dto.TramitacaoRequestDto;

public interface PaeDocumentoService {

	List<DocumentoProtocoladoDto> buscarDocumentosProtocolados(TramitacaoRequestDto dto);

}
