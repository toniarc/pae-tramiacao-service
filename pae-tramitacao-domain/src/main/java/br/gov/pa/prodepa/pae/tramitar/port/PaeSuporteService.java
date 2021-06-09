package br.gov.pa.prodepa.pae.tramitar.port;

import java.util.List;

import br.gov.pa.prodepa.pae.common.domain.dto.UsuarioDto;
import br.gov.pa.prodepa.pae.tramitar.dto.suporte.LocalizacaoBasicDto;
import br.gov.pa.prodepa.pae.tramitar.dto.suporte.OrgaoPaeDto;

public interface PaeSuporteService {

	List<LocalizacaoBasicDto> buscarLocalizacoesUsuario(UsuarioDto usuarioLogado);

	List<OrgaoPaeDto> buscarTodosOsOrgaos();

    LocalizacaoBasicDto buscarLocalizacao(Long localizacaoDestinoId);

}
