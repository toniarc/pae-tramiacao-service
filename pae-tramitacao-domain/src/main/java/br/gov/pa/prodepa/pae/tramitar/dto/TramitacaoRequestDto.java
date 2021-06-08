package br.gov.pa.prodepa.pae.tramitar.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TramitacaoRequestDto {

	private List<TramitarDocumentoDto> documentos;

	public TramitarDocumentoDto getDocumentoTramitado(Integer documentoAno, Long documentoNumero) {
		return documentos.stream()
				.filter( doc -> doc.getDocumentoAno().equals(documentoAno) && doc.getDocumentoNumero().equals(documentoNumero))
				.findFirst()
				.get();
	}
	
}
