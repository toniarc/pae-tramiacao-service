package br.gov.pa.prodepa.pae.tramitar.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.gov.pa.prodepa.pae.common.domain.dto.UsuarioDto;
import br.gov.pa.prodepa.pae.common.domain.exception.DomainException;
import br.gov.pa.prodepa.pae.tramitar.dto.DocumentoProtocoladoDto;
import br.gov.pa.prodepa.pae.tramitar.dto.ProtocoloDto;
import br.gov.pa.prodepa.pae.tramitar.dto.TramitacaoRequestDto;
import br.gov.pa.prodepa.pae.tramitar.dto.TramitarDocumentoDto;
import br.gov.pa.prodepa.pae.tramitar.dto.suporte.LocalizacaoBasicDto;
import br.gov.pa.prodepa.pae.tramitar.dto.suporte.OrgaoPaeDto;
import br.gov.pa.prodepa.pae.tramitar.model.Tramitacao;
import br.gov.pa.prodepa.pae.tramitar.port.MessageService;
import br.gov.pa.prodepa.pae.tramitar.port.PaeProtocoloService;
import br.gov.pa.prodepa.pae.tramitar.port.PaeSuporteService;
import br.gov.pa.prodepa.pae.tramitar.port.TramitacaoRepository;
import br.gov.pa.prodepa.pae.tramitar.validator.TramitacaoValidator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TramitarDomainService {

	private final TramitacaoRepository tramitacaoRepository;
	private final PaeProtocoloService protocoloService;
	private final PaeSuporteService suporteService;
	private final UsuarioDto usuarioLogado;
	private final MessageService messageService;
	
	//nao pode selecionar documentos com tramitacao e documentos sem tramitacao ao mesmo tempo
	
	public void tramitarDocumentos(TramitacaoRequestDto dto) {

		//TODO adiquirir um lock para cada documento/protocolo

		Date now = new Date();
		
		TramitacaoValidator validator = TramitacaoValidator.getInstance(dto, suporteService, usuarioLogado);
		
		validator
			.validarSeOUsuarioInformouAoMenosUmDocumento()
			.validarSeOAnoENumeroDeProtocoloForamInformados()
		.validar();
		
		List<DocumentoProtocoladoDto> documentosProtocolados = protocoloService.buscarDocumentosProtocolados(dto);

		validator
			.validarSeTodosOsDocumentosInformadosExistem(documentosProtocolados)
		.validar();
		
		List<OrgaoPaeDto> orgaos = suporteService.buscarTodosOsOrgaos();
		List<LocalizacaoBasicDto> localizacoesUsuario = suporteService.buscarLocalizacoesUsuario(usuarioLogado);
		
		List<DocumentoProtocoladoDto> documentosSemTramitacao = new ArrayList<DocumentoProtocoladoDto>();
		List<DocumentoProtocoladoDto> documentosComTramitacao = new ArrayList<DocumentoProtocoladoDto>();
		
		for(DocumentoProtocoladoDto documentoProtocolado : documentosProtocolados) {
			
			if(documentoProtocolado.getTramitado()) {
				documentosComTramitacao.add(documentoProtocolado);
			} else {
				//NAO FOI TRAMITADO
				validator
					.validarSeOAnoENumeroDeProtocoloForamInformados()
				.validar();
				documentosSemTramitacao.add(documentoProtocolado);
			}
		}
		
		tramitarDocumentosSemTramitacao(documentosSemTramitacao, dto, validator, now, orgaos);
		tramitarDocumentosComTramitacao(documentosComTramitacao);
		
		TramitacaoValidator.getInstance(dto, suporteService, usuarioLogado)
		
		
		
		//.validarConfiguracoesDeEntradaDoOrgaoDestino()
		//.certificarQueOSetorDestinoEstaAtivo()
		//.certificarQueOSetorDestinoPossuiResponsavel()
		//.certificarQueODocumentoAindaSeEncontraNoSetorOrigem()
		//.certificarQueODocumentoAindaSeEncontraNoSetorDaUltimaTramitacao()
		.validar();
		
	}

	private void tramitarDocumentosComTramitacao(List<DocumentoProtocoladoDto> documentosComTramitacao, TramitacaoValidator validator) {
		
	}

	private void tramitarDocumentosSemTramitacao(List<DocumentoProtocoladoDto> documentosSemTramitacao, TramitacaoRequestDto tramitacaoDto, TramitacaoValidator validator, Date now, List<OrgaoPaeDto> orgaos, List<LocalizacaoBasicDto> localizacoesUsuario) {
		
		for(DocumentoProtocoladoDto documentoProtocolado : documentosSemTramitacao) {

			validator
				.certificarQueTodosOsDocumentosEstaoNoSetorDoUsuarioLogado(documentoProtocolado, localizacoesUsuario)
				.certificarQueNenhumDocumentoPossuiPendenciaDeAssinatura(documentoProtocolado)
				.certificarQueNenhumDocumentoFoiArquivado(documentoProtocolado)
				//.certificarQueOSetorOrigemEhDiferenteDoSetorDestino()
			.validar();
			
			List<ProtocoloDto> protocolos = protocoloService.buscarProtocolosPorDocumentoProtocolado(documentoProtocolado);
			TramitarDocumentoDto documentoTramitado = tramitacaoDto.getDocumentoTramitado(documentoProtocolado.getDocumentoAno(), documentoProtocolado.getDocumentoNumero()); 
			
			for(ProtocoloDto protocolo : protocolos) {
				validator
					.validarConfiguracoesDeSaidaDoOrgaoOrigem(protocolo)
				.validar()
				
				Tramitacao t = Tramitacao.builder()
					.anotacao(documentoTramitado.getAnotacao())
					.atualizadoEm(now)
					.atualizadoPor(usuarioLogado.getId())
					.criadoEm(now)
					.criadoPor(usuarioLogado.getId())
					.dataTramitacao(now)
					.orgaoOrigemId(protocolo.getOrgaoOrigemId())
					.localizacaoOrigemId(protocolo.getLocalizacaoOrigemId())
					.usuarioTramitacaoId(usuarioLogado.getId())
					.documentoAno(documentoProtocolado.getDocumentoAno())
					.documentoNumero(documentoProtocolado.getDocumentoNumero())
					.protocoloAno(protocolo.getProtocoloAno())
					.protocoloNumero(protocolo.getProtocoloNumero())
					.build();

				OrgaoPaeDto orgao = buscarOrgao(protocolo.getOrgaoDestinoId(), orgaos);
				
				if(documentoProtocolado.getTipoDestino().equals("ORGAO")) {
					t.setOrgaoDestinoId(protocolo.getOrgaoDestinoId());
					t.setLocalizacaoDestinoId(orgao.getLocalizacaoPadraoRecebimento().getId());
					t.setUsuarioRecebeuId(orgao.getLocalizacaoPadraoRecebimento().getResponsavel().getId());
				}
				
				if(documentoProtocolado.getTipoDestino().equals("SETOR")) {
					t.setOrgaoDestinoId(protocolo.getOrgaoDestinoId());
					t.setLocalizacaoDestinoId(protocolo.getLocalizacaoDestinoId());
					t.setUsuarioRecebeuId(orgao.getLocalizacaoPadraoRecebimento().getResponsavel().getId());
				}
					
				messageService.enviarParaFilaDeTramitacoes(t);
			}
		}
	}

	private OrgaoPaeDto buscarOrgao(Long orgaoDestinoId, List<OrgaoPaeDto> orgaos) {
		return orgaos.stream()
				.filter(o -> o.getId().equals(orgaoDestinoId))
				.findFirst()
				.get();
	}

	private DocumentoProtocoladoDto getDocumentoPorAnoENumeroDocumento(List<DocumentoProtocoladoDto> documentosProtocolados,
			TramitarDocumentoDto docProtocolado) {
		return documentosProtocolados.stream()
				.filter( doc -> doc.getDocumentoAno().equals(docProtocolado.getDocumentoAno()) && doc.getDocumentoNumero().equals(docProtocolado.getDocumentoNumero()))
				.findFirst()
				.orElseThrow( () -> new DomainException(String.format("Não foi encontrado nenhum documento com o número %d/%d", docProtocolado.getDocumentoAno(), docProtocolado.getDocumentoNumero())));
	}

}
