package com.gestionexpedientes.demanda.service;

import com.gestionexpedientes.demanda.dto.DemandaListDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class DemandaExcelService {

    private static final String HOJA = "Expedientes";

    private static final String[] CABECERAS = {
            "Carátula", "Demandante", "DNI", "Tipo de demanda", "Tipología",
            "Subtipología", "Descripción", "Información adicional", "Paso", "Estado"
    };

    private static final Map<Integer, String> ESTADOS = Map.of(
            0, "Eliminada",
            1, "Receptada",
            2, "Suspendido",
            3, "En tratamiento",
            4, "Cerrado y Resuelto",
            5, "Cerrado sin Resolución",
            6, "Pendiente",
            7, "Finalizado");

    public byte[] generar(List<DemandaListDto> demandas) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream salida = new ByteArrayOutputStream()) {

            Sheet hoja = workbook.createSheet(HOJA);
            escribirCabecera(workbook, hoja);

            int fila = 1;
            for (DemandaListDto demanda : demandas)
                escribirDemanda(hoja.createRow(fila++), demanda);

            for (int columna = 0; columna < CABECERAS.length; columna++)
                hoja.autoSizeColumn(columna);

            hoja.createFreezePane(0, 1);

            workbook.write(salida);
            return salida.toByteArray();
        }
    }

    private void escribirCabecera(Workbook workbook, Sheet hoja) {
        Font negrita = workbook.createFont();
        negrita.setBold(true);

        CellStyle estilo = workbook.createCellStyle();
        estilo.setFont(negrita);

        Row fila = hoja.createRow(0);
        for (int columna = 0; columna < CABECERAS.length; columna++) {
            Cell celda = fila.createCell(columna);
            celda.setCellValue(CABECERAS[columna]);
            celda.setCellStyle(estilo);
        }
    }

    private void escribirDemanda(Row fila, DemandaListDto demanda) {
        String[] valores = {
                demanda.getCaratula(),
                demanda.getDemandante(),
                demanda.getDni(),
                demanda.getTipoDemanda(),
                demanda.getTipologia(),
                demanda.getSubtipologia(),
                demanda.getDescripcion(),
                demanda.getInformacionAdicional(),
                demanda.getPaso(),
                ESTADOS.getOrDefault(demanda.getEstado(), "Desconocido")
        };

        for (int columna = 0; columna < valores.length; columna++)
            fila.createCell(columna).setCellValue(valores[columna] == null ? "" : valores[columna]);
    }
}
