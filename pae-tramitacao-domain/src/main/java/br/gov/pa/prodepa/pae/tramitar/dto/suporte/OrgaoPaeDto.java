package br.gov.pa.prodepa.pae.tramitar.dto.suporte;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public final class OrgaoPaeDto {

	private Long id;
	private String sigla;
	private String nome;
	private String cnpj;
	private String logo;
	private String entradaProcesso;
	private String saidaProcesso;
	private LocalizacaoBasicDto localizacaoPadraoRecebimento;
	private Date dataHabilitacao;
	
	public OrgaoPaeDto(){
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		OrgaoPaeDto other = (OrgaoPaeDto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}