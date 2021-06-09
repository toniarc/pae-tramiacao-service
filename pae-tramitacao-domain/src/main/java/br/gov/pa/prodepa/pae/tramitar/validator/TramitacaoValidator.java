package br.gov.pa.prodepa.pae.tramitar.validator;

import java.util.List;

import br.gov.pa.prodepa.pae.common.domain.dto.ApplicationRoles;
import br.gov.pa.prodepa.pae.common.domain.dto.UsuarioDto;
import br.gov.pa.prodepa.pae.common.domain.exception.DomainException;
import br.gov.pa.prodepa.pae.tramitar.dto.DocumentoProtocoladoDto;
import br.gov.pa.prodepa.pae.tramitar.dto.ProtocoloDto;
import br.gov.pa.prodepa.pae.tramitar.dto.TramitacaoRequestDto;
import br.gov.pa.prodepa.pae.tramitar.dto.TramitarDocumentoDto;
import br.gov.pa.prodepa.pae.tramitar.dto.suporte.LocalizacaoBasicDto;
import br.gov.pa.prodepa.pae.tramitar.dto.suporte.OrgaoPaeDto;

public class TramitacaoValidator {

	public static final String NAO_PODE_MISTURAR_PROCESSOS_TRAMITADOS_COM_NAO_TRAMITADOS = "Não é permitido tramitar processos que ainda não foram tramitados junto com processos que já foram tramitados";
	public static final String DOCUMENTO_NULL = "Informe ao menos um documento para ser tramitado";
	public static final String ANO_OU_NUMERO_DOCUCMENTO_NULL = "Informe o ano e o número do documento para cada documento a ser tramitado";
	public static final String DOCUMENTO_NAO_ESTA_NO_SETOR_DO_USUARIO = "O documento d%/d% não se encontra no setor do usuário logado";
	public static final String DOCUMENTO_NAO_ENCONTRADO = "O documento com o número d%/d% não foi encontrado";
	public static final String ORGAO_DESTINO_NULL = "Informe um órgão de destino para o documento d%/d%";
	public static final String LOCALIZACAO_DESTINO_NULL = "Informe uma localização de destino para o documento d%/d%";
	public static final String DOCUMENTO_PENDENCIA_ASSINATURA = "O documento número d%/d% possui pendência de assinatura";
	public static final String DOCUMENTO_ARQUIVADO = "O documento número d%/d% está arquivado";
	public static final String CONFIGURACAO_SAIDA_ORGAO_ORIGEM_INVALIDA = "Tramitação não realizada: De acordo com as configurações do órgão Origem dessa tramitação, o protocolo só pode ser tramitado para outro órgão se estiver num setor de protocolo e o usuário tem perfil de protocolista.";
	public static final String CONFIGURACAO_ENTRADA_ORGAO_DESTINO_INVALIDA = "Tramitação não realizada: De acordo com as configurações do órgão destino dessa tramitação, a tramitação só poderá ser realizada se o setor destino for setor de protocolo.";
	public static final String SETOR_DESTINO_IGUAL_SETOR_ORIGEM = "Tramitação não realizada: O Setor Origem e o Setor Destino devem ser diferentes.";
	public static final String DOCUMENTO_NAO_EH_TIPO_ELETRONICO = "Não foi possível tramitar o documento %d/%d. O documento deve ser do tipo eletrônico.";
	public static final String ORGAO_DESTINO_NAO_HABILITADO = "Não foi possível tramitar o protocolo %d/%d. O órgão destino não está habilitado.";
	
	DomainException de = new DomainException();
	private TramitacaoRequestDto dto;
	private UsuarioDto usuarioLogado;
	
	public TramitacaoValidator(TramitacaoRequestDto dto, UsuarioDto usuarioLogado) {
		this.dto = dto;
		this.usuarioLogado = usuarioLogado;
	}

	public static TramitacaoValidator getInstance(TramitacaoRequestDto dto, UsuarioDto usuarioLogado) {
		return new TramitacaoValidator(dto, usuarioLogado);
	}
	
	public TramitacaoValidator validar() {
		de.throwException();
		return this;
	}

	public TramitacaoValidator validarSeOUsuarioInformouAoMenosUmDocumento() {
		if(dto.getDocumentos() == null || dto.getDocumentos().size() == 0) {
			de.addError(DOCUMENTO_NULL);
		}
		return this;
	}
	
	public TramitacaoValidator validarSeOAnoENumeroDeProtocoloForamInformados() {
		if(dto.getDocumentos() != null && dto.getDocumentos().size() > 0) {
			for(TramitarDocumentoDto t : dto.getDocumentos()) {
				if(t.getDocumentoAno() == null || t.getDocumentoAno() == 0 
						|| t.getDocumentoNumero() == null || t.getDocumentoNumero() == 0) {
					de.addError(ANO_OU_NUMERO_DOCUCMENTO_NULL);
					break;
				}
			}
		}
		return this;
	}

	public TramitacaoValidator validarSeTodosOsDocumentosInformadosExistem(List<DocumentoProtocoladoDto> documentosProtocolados) {
		for(TramitarDocumentoDto t  : dto.getDocumentos()) {
			for(DocumentoProtocoladoDto dp : documentosProtocolados) {
				if( !(dp.getDocumentoAno().equals(t.getDocumentoAno()) && dp.getDocumentoNumero().equals(t.getDocumentoNumero())) ) {
					de.addError(String.format(DOCUMENTO_NAO_ENCONTRADO, t.getDocumentoAno(), t.getDocumentoNumero()));
				}
			}
		}
		return this;
	}

	public TramitacaoValidator certificarQueNenhumDocumentoPossuiPendenciaDeAssinatura(DocumentoProtocoladoDto dp) {
		if(!dp.todosOsUsuariosJaAssinaram()) {
			de.addError(String.format(DOCUMENTO_PENDENCIA_ASSINATURA, dp.getDocumentoAno(), dp.getDocumentoNumero()));
		}
		return this;
	}

	public TramitacaoValidator certificarQueNenhumDocumentoFoiArquivado(DocumentoProtocoladoDto dp) {
		if(dp.getArquivado()) {
			de.addError(String.format(DOCUMENTO_ARQUIVADO, dp.getDocumentoAno(), dp.getDocumentoNumero()));
		}
		return this;
	}

	public TramitacaoValidator certificarQueTodosOsDocumentosEstaoNoSetorDoUsuarioLogado(DocumentoProtocoladoDto dp, List<LocalizacaoBasicDto> localizacoesUsuario) {
		if(!localizacoesUsuario.contains(LocalizacaoBasicDto.builder().id(dp.getLocalizacaoOrigemId()).build())) {
			de.addError(String.format(DOCUMENTO_NAO_ESTA_NO_SETOR_DO_USUARIO, dp.getProtocoloAno(),dp.getProtocoloNumero()));
		}
		return this;
	}
	
	public TramitacaoValidator validarConfiguracoesDeSaidaDoOrgaoOrigem(ProtocoloDto protocolo, OrgaoPaeDto orgaoOrigem, LocalizacaoBasicDto localizacaoOrigem) {
		
		if(!protocolo.getOrgaoOrigemId().equals(protocolo.getOrgaoDestinoId())) {
			if(orgaoOrigem.getSaidaProcesso().equals("SOMENTE_SETOR_DE_PROTOCOLO") && 
					!localizacaoOrigem.getSetor().getProtocoladora() && 
					usuarioLogado.hasRole(ApplicationRoles.PROTOCOLISTA)) {
				de.addError(CONFIGURACAO_SAIDA_ORGAO_ORIGEM_INVALIDA);
			}
		}
		
		return this;
	}

	public TramitacaoValidator validarConfiguracoesDeEntradaDoOrgaoDestino(ProtocoloDto protocolo, OrgaoPaeDto orgaoDestino, LocalizacaoBasicDto localizacaoDestino) {
		if(!protocolo.getOrgaoOrigemId().equals(protocolo.getOrgaoDestinoId())) {
			if(orgaoDestino.getSaidaProcesso().equals("SOMENTE_SETOR_DE_PROTOCOLO") && 
					!localizacaoDestino.getSetor().getProtocoladora() ) {
				de.addError(CONFIGURACAO_ENTRADA_ORGAO_DESTINO_INVALIDA);
			}
		}
		return this;
	}

	public TramitacaoValidator certificarQueOSetorOrigemEhDiferenteDoSetorDestino(ProtocoloDto protocolo) {
		if(protocolo.getLocalizacaoOrigemId().equals(protocolo.getLocalizacaoDestinoId())){
			de.addError(SETOR_DESTINO_IGUAL_SETOR_ORIGEM);
		}
		return this;
	}

	public TramitacaoValidator certificarQueOProtocoloEhDoTipoEletronico(DocumentoProtocoladoDto dp) {
		if(!dp.getTipoDocumento().equals("ELETRONICO")){
			de.addError(String.format(DOCUMENTO_NAO_EH_TIPO_ELETRONICO, dp.getDocumentoAno(), dp.getDocumentoNumero()));
		}
		return this;
	}

	public TramitacaoValidator certificarQueOrgaoDestinoEstaHabilitado(OrgaoPaeDto orgaoDestino, ProtocoloDto protocolo) {
		if(orgaoDestino.getHabilitado() == null || !orgaoDestino.getHabilitado()){
			de.addError(String.format(ORGAO_DESTINO_NAO_HABILITADO, protocolo.getProtocoloAno(), protocolo.getProtocoloNumero()));
		}
		return this;
	}
	
}
