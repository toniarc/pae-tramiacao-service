package br.gov.pa.prodepa.pae.tramitar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProtocoloDto {

	private Integer protocoloAno;
	private Long protocoloNumero;
	
	private Long orgaoDestinoId;
	private Long localizacaoDestinoId;
	
	private Long orgaoOrigemId;
	private Long localizacaoOrigemId;
}
