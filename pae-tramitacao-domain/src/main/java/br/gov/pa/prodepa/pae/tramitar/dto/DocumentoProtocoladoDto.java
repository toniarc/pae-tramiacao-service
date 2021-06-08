package br.gov.pa.prodepa.pae.tramitar.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentoProtocoladoDto {

	private boolean jaFoiTramitado;

	private Long localizacaoOrigemId;

	private Long orgaoDestinoId;
	
	private Long localizacaoDestinoId;

	private Integer protocoloAno;
	
	private Long protocoloNumero;

	private Integer documentoAno;
	
	private Long documentoNumero;

	private Boolean tramitado;
	
	private Boolean arquivado;
	
	private String tipoDestino;

	private List<Long> usuariosQueDevemAssinar;
	
	private List<Long> usuariosQueJaAssinaram;
	
	public Boolean todosOsUsuariosJaAssinaram() {
		return usuariosQueJaAssinaram.containsAll(usuariosQueDevemAssinar);
	}
}
