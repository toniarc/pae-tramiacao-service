package br.gov.pa.prodepa.pae.tramitar.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TramitarDocumentoDto {

	private Integer documentoAno;
	private Long documentoNumero;
	
	private Integer protocoloAno;
	private Long protocoloNumero;
	
	private String anotacao;
	
	private Long orgaoDestinoId;
	private Long localizacaoDestinoId;
	
	private Long localizacaoOrigemId;
	
	public boolean possuiNumeroProtocolo() {
		return protocoloAno != null && protocoloAno > 0 &&
				protocoloNumero != null && protocoloNumero > 0;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((documentoAno == null) ? 0 : documentoAno.hashCode());
		result = prime * result + ((documentoNumero == null) ? 0 : documentoNumero.hashCode());
		result = prime * result + ((protocoloAno == null) ? 0 : protocoloAno.hashCode());
		result = prime * result + ((protocoloNumero == null) ? 0 : protocoloNumero.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TramitarDocumentoDto other = (TramitarDocumentoDto) obj;
		if (documentoAno == null) {
			if (other.documentoAno != null)
				return false;
		} else if (!documentoAno.equals(other.documentoAno))
			return false;
		if (documentoNumero == null) {
			if (other.documentoNumero != null)
				return false;
		} else if (!documentoNumero.equals(other.documentoNumero))
			return false;
		if (protocoloAno == null) {
			if (other.protocoloAno != null)
				return false;
		} else if (!protocoloAno.equals(other.protocoloAno))
			return false;
		if (protocoloNumero == null) {
			if (other.protocoloNumero != null)
				return false;
		} else if (!protocoloNumero.equals(other.protocoloNumero))
			return false;
		return true;
	}

}
