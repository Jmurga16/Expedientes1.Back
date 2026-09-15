package com.gestionexpedientes.demanda.service;

import com.gestionexpedientes.demanda.dto.DemandaListDto;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DemandaExcelServiceTest {

    private final DemandaExcelService service = new DemandaExcelService();

    @Test
    @DisplayName("El Excel lleva cabecera, una fila por expediente y el estado como texto")
    void elExcelLlevaCabeceraYUnaFilaPorExpediente() throws Exception {
        byte[] contenido = service.generar(List.of(demanda("001-OBR-2026-00001", 3), demanda("001-OBR-2026-00002", 7)));

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(contenido))) {
            Sheet hoja = workbook.getSheetAt(0);

            assertEquals("Expedientes", hoja.getSheetName());
            assertEquals(2, hoja.getLastRowNum());
            assertEquals("Carátula", hoja.getRow(0).getCell(0).getStringCellValue());

            Row primera = hoja.getRow(1);
            assertEquals("001-OBR-2026-00001", primera.getCell(0).getStringCellValue());
            assertEquals("Ana Pérez", primera.getCell(1).getStringCellValue());
            assertEquals("En tratamiento", primera.getCell(9).getStringCellValue());

            assertEquals("Finalizado", hoja.getRow(2).getCell(9).getStringCellValue());
        }
    }

    @Test
    @DisplayName("Los campos sin valor salen vacíos, no como null")
    void losCamposSinValorSalenVacios() throws Exception {
        byte[] contenido = service.generar(List.of(new DemandaListDto()));

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(contenido))) {
            Row fila = workbook.getSheetAt(0).getRow(1);

            assertEquals("", fila.getCell(0).getStringCellValue());
            assertEquals("Eliminada", fila.getCell(9).getStringCellValue());
        }
    }

    private DemandaListDto demanda(String caratula, int estado) {
        DemandaListDto dto = new DemandaListDto();
        dto.setCaratula(caratula);
        dto.setDemandante("Ana Pérez");
        dto.setDni("30123456");
        dto.setTipoDemanda("OBR");
        dto.setTipologia("Obras");
        dto.setSubtipologia("Vereda");
        dto.setDescripcion("Reclamo por vereda rota");
        dto.setInformacionAdicional("Frente al 250");
        dto.setPaso("Inicio");
        dto.setEstado(estado);
        return dto;
    }
}
