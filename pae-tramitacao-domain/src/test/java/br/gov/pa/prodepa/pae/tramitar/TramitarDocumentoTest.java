package br.gov.pa.prodepa.pae.tramitar;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.gov.pa.prodepa.pae.common.domain.dto.UsuarioDto;
import br.gov.pa.prodepa.pae.common.domain.exception.DomainException;
import br.gov.pa.prodepa.pae.tramitar.dto.TramitacaoRequestDto;
import br.gov.pa.prodepa.pae.tramitar.dto.TramitarDocumentoDto;
import br.gov.pa.prodepa.pae.tramitar.port.PaeSuporteService;
import br.gov.pa.prodepa.pae.tramitar.validator.TramitacaoValidator;

@ExtendWith(MockitoExtension.class)
public class TramitarDocumentoTest {

	@Mock
	private PaeSuporteService suporteService;
	
	private UsuarioDto usuarioLogado = UsuarioDto.builder()
		.id(3799L)
		.nome("Antonio Junior")
		.build();
	
	@Test
	public void deveFalharQuandoNaoInformarNenhumDocumento() {
		
		TramitacaoRequestDto dto = new TramitacaoRequestDto();
		
		DomainException de = Assertions.assertThrows(DomainException.class, () -> {
			TramitacaoValidator.getInstance(dto, null, suporteService, usuarioLogado)
			.validarCamposObrigatorios()
			.validar();
		});
		assertTrue(de.getMessage().contains(TramitacaoValidator.DOCUMENTO_NULL));
	}
	
	@Test
	public void deveFalharQuandoNaoInformarOAnoOuONumeroDoDocumento() {
		
		TramitacaoRequestDto dto = new TramitacaoRequestDto();
		
		TramitarDocumentoDto tdp1 = TramitarDocumentoDto.builder()
				.anotacao(null)
				.documentoAno(2021)
				.documentoNumero(null)
				.build();
		
		dto.setDocumentos(Arrays.asList(tdp1));
		
		DomainException de = Assertions.assertThrows(DomainException.class, () -> {
			TramitacaoValidator.getInstance(dto, null, suporteService, usuarioLogado)
			.validarCamposObrigatorios()
			.validar();
		});
		assertTrue(de.getMessage().contains(TramitacaoValidator.ANO_OU_NUMERO_DOCUCMENTO_NULL));
	}
}
