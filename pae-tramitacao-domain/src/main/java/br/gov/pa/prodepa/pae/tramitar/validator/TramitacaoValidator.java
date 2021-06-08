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
import br.gov.pa.prodepa.pae.tramitar.port.PaeSuporteService;

public class TramitacaoValidator {

	public static final String NAO_PODE_MISTURAR_PROCESSOS_TRAMITADOS_COM_NAO_TRAMITADOS = "Não é permitido tramitar processos que ainda não foram tramitados junto com processos que já foram tramitados";
	public static final String DOCUMENTO_NULL = "Informe ao menos um documento para ser tramitado";
	public static final String ANO_OU_NUMERO_DOCUCMENTO_NULL = "Informe o ano e o número do documento para cada documento a ser tramitado";
	public static final String DOCUMENTO_NAO_ESTA_NO_SETOR_DO_USUARIO = "O documento d%/d% não se encontra no setor do usuário logado";
	private static final String DOCUMENTO_NAO_ENCONTRADO = "O documento com o número d%/d% não foi encontrado";
	private static final String ORGAO_DESTINO_NULL = "Informe um órgão de destino para o documento d%/d%";
	private static final String LOCALIZACAO_DESTINO_NULL = "Informe uma localização de destino para o documento d%/d%";
	private static final String DOCUMENTO_PENDENCIA_ASSINATURA = "O documento número d%/d% possui pendência de assinatura";
	private static final String DOCUMENTO_ARQUIVADO = "O documento número d%/d% está arquivado";
	
	DomainException de = new DomainException();
	private TramitacaoRequestDto dto;
	private PaeSuporteService paeSuporteService;
	private UsuarioDto usuarioLogado;
	
	public TramitacaoValidator(TramitacaoRequestDto dto, PaeSuporteService paeSuporteService, UsuarioDto usuarioLogado) {
		this.dto = dto;
		this.paeSuporteService = paeSuporteService;
		this.usuarioLogado = usuarioLogado;
	}

	public static TramitacaoValidator getInstance(TramitacaoRequestDto dto,  
			PaeSuporteService paeSuporteService, UsuarioDto usuarioLogado) {
		return new TramitacaoValidator(dto, paeSuporteService, usuarioLogado);
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

	public TramitacaoValidator validarSeODestinoFoiInformado() {
		
		for(TramitarDocumentoDto t  : dto.getDocumentos()) {
			if(t.getOrgaoDestinoId() == null) {
				de.addError(String.format(ORGAO_DESTINO_NULL, t.getDocumentoAno(), t.getDocumentoNumero()));
			} else {
				if(t.getOrgaoDestinoId().equals(usuarioLogado.getOrgao().getId())) {
					//ORGAO DESTINO IGUAL AO ORGAO DO USUARIO LOGADO
					if(t.getLocalizacaoDestinoId() == null) {
						de.addError(String.format(LOCALIZACAO_DESTINO_NULL, t.getDocumentoAno(), t.getDocumentoNumero()));
					}
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
				de.addError(ANO_OU_NUMERO_DOCUCMENTO_NULL);
			}
		}
		
		return this;
	}
	
}
