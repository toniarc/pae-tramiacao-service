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

	// nao pode selecionar documentos com tramitacao e documentos sem tramitacao ao
	// mesmo tempo

	public void tramitarDocumentos(TramitacaoRequestDto dto) {

		// TODO adiquirir um lock para cada documento/protocolo

		Date now = new Date();

		TramitacaoValidator validator = TramitacaoValidator.getInstance(dto, usuarioLogado);

		validator
			.validarSeOUsuarioInformouAoMenosUmDocumento()
			.validarSeOAnoENumeroDeProtocoloForamInformados()
		.validar();

		List<DocumentoProtocoladoDto> documentosProtocolados = protocoloService.buscarDocumentosProtocolados(dto);

		validator.validarSeTodosOsDocumentosInformadosExistem(documentosProtocolados).validar();

		List<OrgaoPaeDto> orgaos = suporteService.buscarTodosOsOrgaos();
		List<LocalizacaoBasicDto> localizacoesUsuario = suporteService.buscarLocalizacoesUsuario(usuarioLogado);

		List<DocumentoProtocoladoDto> documentosSemTramitacao = new ArrayList<DocumentoProtocoladoDto>();
		List<DocumentoProtocoladoDto> documentosComTramitacao = new ArrayList<DocumentoProtocoladoDto>();

		for (DocumentoProtocoladoDto documentoProtocolado : documentosProtocolados) {

			if (documentoProtocolado.getTramitado()) {
				documentosComTramitacao.add(documentoProtocolado);
			} else {
				documentosSemTramitacao.add(documentoProtocolado);
			}
		}

		List<Tramitacao> tramitacoes = new ArrayList<>(); 
		tramitarDocumentosSemTramitacao(documentosSemTramitacao, dto, validator, now, orgaos, localizacoesUsuario, tramitacoes);
		//tramitarDocumentosComTramitacao(documentosComTramitacao);

		validator.validar();

		//confirmarTodosOsAnexosAssinadosENaoConfirmados(tramitacoes);

		// .certificarQueOSetorDestinoEstaAtivo()
		// .certificarQueOSetorDestinoPossuiResponsavel()
		// .certificarQueODocumentoAindaSeEncontraNoSetorOrigem()
		// .certificarQueODocumentoAindaSeEncontraNoSetorDaUltimaTramitacao()
	}

	private void tramitarDocumentosComTramitacao(List<DocumentoProtocoladoDto> documentosComTramitacao,
			TramitacaoValidator validator) {

	}

	private List<Tramitacao> tramitarDocumentosSemTramitacao(List<DocumentoProtocoladoDto> documentosSemTramitacao,
			TramitacaoRequestDto tramitacaoDto, TramitacaoValidator validator, Date now, List<OrgaoPaeDto> orgaos,
			List<LocalizacaoBasicDto> localizacoesUsuario, List<Tramitacao> tramitacoes) {

		for (DocumentoProtocoladoDto documentoProtocolado : documentosSemTramitacao) {

			validator
				.certificarQueNenhumDocumentoPossuiPendenciaDeAssinatura(documentoProtocolado)
				.certificarQueTodosOsDocumentosEstaoNoSetorDoUsuarioLogado(documentoProtocolado, localizacoesUsuario)
				.certificarQueOProtocoloEhDoTipoEletronico(documentoProtocolado)
				.certificarQueNenhumDocumentoFoiArquivado(documentoProtocolado);

			List<ProtocoloDto> protocolos = protocoloService.buscarProtocolosPorDocumentoProtocolado(documentoProtocolado);
			TramitarDocumentoDto documentoTramitado = tramitacaoDto.getDocumentoTramitado(
					documentoProtocolado.getDocumentoAno(), documentoProtocolado.getDocumentoNumero());

			for (ProtocoloDto protocolo : protocolos) {

				OrgaoPaeDto orgaoOrigem = buscarOrgao(protocolo.getOrgaoOrigemId(), orgaos);
				LocalizacaoBasicDto localizacaoOrigem = buscarLocalizacao(protocolo.getLocalizacaoOrigemId(), localizacoesUsuario);

				OrgaoPaeDto orgaoDestino = buscarOrgao(protocolo.getOrgaoDestinoId(), orgaos);
				LocalizacaoBasicDto localizacaoDestino = suporteService.buscarLocalizacao(protocolo.getLocalizacaoDestinoId());

				validator
					.certificarQueOSetorOrigemEhDiferenteDoSetorDestino(protocolo)
					.validarConfiguracoesDeSaidaDoOrgaoOrigem(protocolo, orgaoOrigem, localizacaoOrigem)
					.validarConfiguracoesDeEntradaDoOrgaoDestino(protocolo, orgaoDestino, localizacaoDestino)
					.certificarQueOrgaoDestinoEstaHabilitado(orgaoDestino, protocolo);

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

				if (documentoProtocolado.getTipoDestino().equals("ORGAO")) {
					t.setOrgaoDestinoId(protocolo.getOrgaoDestinoId());
					t.setLocalizacaoDestinoId(orgaoDestino.getLocalizacaoPadraoRecebimento().getId());
					t.setUsuarioRecebeuId(orgaoDestino.getLocalizacaoPadraoRecebimento().getResponsavel().getId());
				}

				if (documentoProtocolado.getTipoDestino().equals("SETOR")) {
					t.setOrgaoDestinoId(protocolo.getOrgaoDestinoId());
					t.setLocalizacaoDestinoId(protocolo.getLocalizacaoDestinoId());
					t.setUsuarioRecebeuId(orgaoDestino.getLocalizacaoPadraoRecebimento().getResponsavel().getId());
				}

				tramitacoes.add(t);
			}
		}

		return tramitacoes;
	}

	private LocalizacaoBasicDto buscarLocalizacao(Long id, List<LocalizacaoBasicDto> localizacoes) {
		return localizacoes.stream().filter( l -> l.getId().equals(id)).findFirst().get();
	}

	private OrgaoPaeDto buscarOrgao(Long orgaoDestinoId, List<OrgaoPaeDto> orgaos) {
		return orgaos.stream().filter(o -> o.getId().equals(orgaoDestinoId)).findFirst().get();
	}

	private DocumentoProtocoladoDto getDocumentoPorAnoENumeroDocumento(
			List<DocumentoProtocoladoDto> documentosProtocolados, TramitarDocumentoDto docProtocolado) {
		return documentosProtocolados.stream()
				.filter(doc -> doc.getDocumentoAno().equals(docProtocolado.getDocumentoAno())
						&& doc.getDocumentoNumero().equals(docProtocolado.getDocumentoNumero()))
				.findFirst()
				.orElseThrow(() -> new DomainException(
						String.format("Não foi encontrado nenhum documento com o número %d/%d",
								docProtocolado.getDocumentoAno(), docProtocolado.getDocumentoNumero())));
	}

}
