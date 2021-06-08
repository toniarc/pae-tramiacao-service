package br.gov.pa.prodepa.pae.tramitar.dto.suporte;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SetorBasicDto {

	private Long id;
	private String nome;
	private String sigla;
	private Boolean protocoladora;
	
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
		SetorBasicDto other = (SetorBasicDto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}