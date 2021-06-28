package br.gov.pa.prodepa.pae.tramitar.dto.suporte;

import br.gov.pa.prodepa.pae.common.domain.dto.UsuarioBasicDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LocalizacaoBasicDto {
	
	private Long id;
	private UsuarioBasicDto responsavel;
	private SetorBasicDto setor;
	private Boolean ativo;
	
}