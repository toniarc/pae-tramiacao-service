package br.gov.pa.prodepa.pae.tramitar.model;

import java.util.Date;

import br.gov.pa.prodepa.pae.common.domain.model.Auditoria;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class Tramitacao extends Auditoria {

	//ORIGEM
	private Long orgaoOrigemId;

	private Long localizacaoOrigemId;
	
	//DESTINO
	private Long orgaoDestinoId;
	
	private Long localizacaoDestinoId;
	
	private Long usuarioTramitacaoId;
	
	private Long usuarioRecebeuId;
	
	private Date dataTramitacao;
	
	private String anotacao;	

	//DOCUMENTO
	private Integer documentoAno;
	
	private Long documentoNumero;
	
	//PROTOCOLO
	private Integer protocoloAno;
	
	private Long protocoloNumero;
	
}
