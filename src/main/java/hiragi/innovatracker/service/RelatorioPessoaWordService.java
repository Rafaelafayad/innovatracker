package hiragi.innovatracker.service;


import hiragi.innovatracker.dto.PessoaResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.springframework.stereotype.Service;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Slf4j
@Service
public class RelatorioPessoaWordService {


    // Paleta de Cores Institucional (Hexadecimal)
    private static final String COLOR_PRIMARY = "1F4E78";     // Azul Escuro
    private static final String COLOR_SECONDARY = "D9E1F2";   // Azul Claro (Fundo do Resumo)
    private static final String COLOR_ZEBRA = "F2F4F7";       // Cinza Claro (Linhas Pares)
    private static final String COLOR_TEXT = "333333";        // Texto padrão
    private static final String COLOR_SUCCESS = "27AE60";     // Verde (Ativo)
    private static final String COLOR_DANGER = "C0392B";      // Vermelho (Inativo)
    private static final String COLOR_MUTED = "7F8C8D";       // Cinza Secundário
    private static final String COLOR_WHITE = "FFFFFF";


    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");


    /**
     * Método principal que coordena a montagem do documento .docx.
     */
    public byte[] gerarRelatorioPessoas(List<PessoaResponseDTO> pessoas, String tituloRelatorio) {
        log.info("Gerando documento Word (.docx) para {} pessoas", pessoas.size());


        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {


            // Executa as etapas de construção do documento através de funções especialistas
            configurarMargens(document);
            configurarMetadados(document, tituloRelatorio);
            configurarCabecalhoRodape(document);


            adicionarTitulo(document, tituloRelatorio);
            adicionarQuadroResumo(document, pessoas.size());
            adicionarTabelaPessoas(document, pessoas);


            document.write(out);
            return out.toByteArray();


        } catch (IOException e) {
            log.error("Erro ao gerar arquivo Word", e);
            throw new RuntimeException("Falha na geração do relatório em formato Word (.docx)", e);
        }
    }


    // =========================================================================
    // FUNÇÕES ESPECIALISTAS DE ESTRUTURAÇÃO DO DOCUMENTO
    // =========================================================================
    private void configurarMargens(XWPFDocument document) {
        // 1. Obtém ou cria a seção de propriedades do documento (CTSectPr)
        CTSectPr sectPr = document.getDocument().getBody().isSetSectPr()
                ? document.getDocument().getBody().getSectPr()
                : document.getDocument().getBody().addNewSectPr();


        // 2. Obtém ou cria a tag de margens da página (CTPageMar)
        CTPageMar pageMar = sectPr.isSetPgMar()
                ? sectPr.getPgMar()
                : sectPr.addNewPgMar();


        // 3. Define as margens em dxa (1440 dxa = 1 polegada = ~2.54 cm)
        BigInteger marginValue = BigInteger.valueOf(1440);


        pageMar.setLeft(marginValue);
        pageMar.setRight(marginValue);
        pageMar.setTop(marginValue);
        pageMar.setBottom(marginValue);
    }


    private void configurarMetadados(XWPFDocument document, String titulo) {
        POIXMLProperties.CoreProperties properties = document.getProperties().getCoreProperties();
        properties.setTitle(titulo);
        properties.setCreator("InnovaTracker - Sistema de Gestão");
        properties.setSubjectProperty("Relatório de Cadastro de Pessoas"); // Método atualizado
    }


    private void configurarCabecalhoRodape(XWPFDocument document) {
        criarCabecalho(document);
        criarRodape(document);
    }


    private void criarCabecalho(XWPFDocument document) {
        XWPFHeader header = document.createHeader(HeaderFooterType.DEFAULT);
        XWPFParagraph pHeader = header.createParagraph();
        pHeader.setAlignment(ParagraphAlignment.RIGHT);


        XWPFRun rHeader = pHeader.createRun();
        rHeader.setText("InnovaTracker | Relatório Operacional");
        rHeader.setFontFamily("Calibri");
        rHeader.setFontSize(9);
        rHeader.setColor(COLOR_MUTED);
        rHeader.setItalic(true);
    }


    private void criarRodape(XWPFDocument document) {
        XWPFFooter footer = document.createFooter(HeaderFooterType.DEFAULT);
        XWPFParagraph pFooter = footer.createParagraph();
        pFooter.setAlignment(ParagraphAlignment.LEFT);


        XWPFRun rFooter = pFooter.createRun();
        rFooter.setText("Emissão: " + LocalDateTime.now().format(DATETIME_FORMATTER) + "  |  Página ");
        rFooter.setFontFamily("Calibri");
        rFooter.setFontSize(9);
        rFooter.setColor(COLOR_MUTED);


        // Inserção das variáveis de paginação nativas do Word (PAGE / NUMPAGES)
        pFooter.getCTP().addNewFldSimple().setInstr("PAGE");
        XWPFRun rFooterOf = pFooter.createRun();
        rFooterOf.setText(" de ");
        rFooterOf.setFontFamily("Calibri");
        rFooterOf.setFontSize(9);
        rFooterOf.setColor(COLOR_MUTED);
        pFooter.getCTP().addNewFldSimple().setInstr("NUMPAGES");
    }


    private void adicionarTitulo(XWPFDocument document, String titulo) {
        XWPFParagraph titlePara = document.createParagraph();
        titlePara.setAlignment(ParagraphAlignment.LEFT);
        titlePara.setSpacingAfter(100);


        XWPFRun titleRun = titlePara.createRun();
        titleRun.setText(titulo);
        titleRun.setBold(true);
        titleRun.setFontSize(22);
        titleRun.setFontFamily("Calibri Light");
        titleRun.setColor(COLOR_PRIMARY);


        XWPFParagraph subPara = document.createParagraph();
        subPara.setSpacingAfter(200);
        XWPFRun subRun = subPara.createRun();
        subRun.setText("Listagem detalhada das pessoas e seus respectivos dados cadastrais.");
        subRun.setFontSize(11);
        subRun.setFontFamily("Calibri");
        subRun.setColor(COLOR_TEXT);
    }


    private void adicionarQuadroResumo(XWPFDocument document, int totalRegistros) {
        XWPFTable summaryTable = document.createTable(1, 2);
        summaryTable.setWidth("100%");


        configurarCelulaResumo(summaryTable.getRow(0).getCell(0), "Total de Registros: ", String.valueOf(totalRegistros), ParagraphAlignment.LEFT);
        configurarCelulaResumo(summaryTable.getRow(0).getCell(1), "Data: ", LocalDateTime.now().format(DATE_FORMATTER), ParagraphAlignment.RIGHT);


        XWPFParagraph spacer = document.createParagraph();
        spacer.setSpacingAfter(200);
    }


    private void configurarCelulaResumo(XWPFTableCell cell, String rotulo, String valor, ParagraphAlignment alinhamento) {
        cell.setColor(COLOR_SECONDARY);
        cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(4680));


        XWPFParagraph p = cell.getParagraphs().get(0);
        p.setAlignment(alinhamento);


        XWPFRun rLabel = p.createRun();
        rLabel.setText(rotulo);
        rLabel.setBold(true);
        rLabel.setFontFamily("Calibri");


        XWPFRun rVal = p.createRun();
        rVal.setText(valor);
        rVal.setFontFamily("Calibri");
    }


    // =========================================================================
    // FUNÇÕES DE CONSTRUÇÃO E FORMATAÇÃO DE TABELA
    // =========================================================================


    private void adicionarTabelaPessoas(XWPFDocument document, List<PessoaResponseDTO> pessoas) {
        if (pessoas.isEmpty()) {
            adicionarMensagemVazia(document);
            return;
        }


        XWPFTable table = document.createTable(pessoas.size() + 1, 6);
        table.setWidth("100%");


        int[] colWidths = {800, 2400, 2400, 1800, 1200, 1000};
        String[] headers = {"ID", "Nome Completo", "E-mail", "Departamento", "Nascimento", "Status"};


        construirCabecalhoTabela(table.getRow(0), headers, colWidths);


        for (int i = 0; i < pessoas.size(); i++) {
            boolean linhaPar = (i % 2 == 1);
            construirLinhaDados(table.getRow(i + 1), pessoas.get(i), colWidths, linhaPar);
        }
    }


    private void adicionarMensagemVazia(XWPFDocument document) {
        XWPFParagraph emptyPara = document.createParagraph();
        XWPFRun emptyRun = emptyPara.createRun();
        emptyRun.setText("Nenhum registro encontrado para exibição neste relatório.");
        emptyRun.setItalic(true);
        emptyRun.setFontFamily("Calibri");
        emptyRun.setColor(COLOR_MUTED);
    }


    private void construirCabecalhoTabela(XWPFTableRow headerRow, String[] headers, int[] colWidths) {
        headerRow.getCtRow().addNewTrPr().addNewTblHeader();


        for (int i = 0; i < headers.length; i++) {
            XWPFTableCell cell = headerRow.getCell(i);
            cell.setColor(COLOR_PRIMARY);
            cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(colWidths[i]));


            XWPFParagraph p = cell.getParagraphs().get(0);
            p.setSpacingBefore(100);
            p.setSpacingAfter(100);
            p.setAlignment(isColunaCentralizada(i) ? ParagraphAlignment.CENTER : ParagraphAlignment.LEFT);


            XWPFRun r = p.createRun();
            r.setText(headers[i]);
            r.setBold(true);
            r.setColor(COLOR_WHITE);
            r.setFontFamily("Calibri");
            r.setFontSize(10);
        }
    }


    private void construirLinhaDados(XWPFTableRow row, PessoaResponseDTO p, int[] colWidths, boolean linhaPar) {
        String bgColor = linhaPar ? COLOR_ZEBRA : COLOR_WHITE;


        String[] cellValues = {
                p.idtPessoa() != null ? String.valueOf(p.idtPessoa()) : "-",
                p.nmePessoa() != null ? p.nmePessoa() : "-",
                p.emlPessoa() != null ? p.emlPessoa() : "-",
                p.nmeDepartamento() != null ? p.nmeDepartamento() : "Sem Depto",
                p.dtaNascimentoPessoa() != null ? p.dtaNascimentoPessoa().format(DATE_FORMATTER) : "-",
                Boolean.TRUE.equals(p.flgAtivoPessoa()) ? "ATIVO" : "INATIVO"
        };


        for (int colIndex = 0; colIndex < cellValues.length; colIndex++) {
            XWPFTableCell cell = row.getCell(colIndex);
            cell.setColor(bgColor);
            cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(colWidths[colIndex]));


            XWPFParagraph pCell = cell.getParagraphs().get(0);
            pCell.setSpacingBefore(80);
            pCell.setSpacingAfter(80);
            pCell.setAlignment(isColunaCentralizada(colIndex) ? ParagraphAlignment.CENTER : ParagraphAlignment.LEFT);


            XWPFRun rCell = pCell.createRun();
            rCell.setText(cellValues[colIndex]);
            rCell.setFontFamily("Calibri");
            rCell.setFontSize(9.5f);


            // Formatação condicional de cor para a coluna de Status
            if (colIndex == 5) {
                rCell.setBold(true);
                rCell.setColor(Boolean.TRUE.equals(p.flgAtivoPessoa()) ? COLOR_SUCCESS : COLOR_DANGER);
            } else {
                rCell.setColor(COLOR_TEXT);
            }
        }
    }


    private boolean isColunaCentralizada(int indexColuna) {
        return indexColuna == 0 || indexColuna == 4 || indexColuna == 5;
    }
}
