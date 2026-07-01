package Telas;

import DAO.PedidoDao;
import Model.ItemPedido;
import Model.Pedido;
import Model.Usuario;

import com.itextpdf.text.Document;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.List;

public class RelatorioPedidos extends JFrame {
    private Usuario usuarioLogado;
    private List<Pedido> pedidosSelecionados;
    private JTextArea textArea;

    public RelatorioPedidos(Usuario usuarioLogado, List<Pedido> pedidosSelecionados) {
        this.usuarioLogado = usuarioLogado;
        this.pedidosSelecionados = pedidosSelecionados;

        setTitle("Relatório de Pedidos Selecionados");
        setSize(750, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(textArea);
        painelPrincipal.add(scrollPane, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnPdf = new JButton("Baixar PDF");
        JButton btnPlanilha = new JButton("Baixar Planilha");
        JButton btnFechar = new JButton("Fechar");

        btnPdf.addActionListener(e -> exportarParaPDF());
        btnPlanilha.addActionListener(e -> exportarParaExcel());
        btnFechar.addActionListener(e -> dispose());

        painelBotoes.add(btnPdf);
        painelBotoes.add(btnPlanilha);
        painelBotoes.add(btnFechar);

        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);
        setContentPane(painelPrincipal);

        gerarTextoRelatorio();

        setVisible(true);
    }

    private void gerarTextoRelatorio() {
        PedidoDao dao = new PedidoDao();
        StringBuilder sb = new StringBuilder();
        BigDecimal totalGeral = BigDecimal.ZERO;

        sb.append("========================================================================\n");
        sb.append("                       RELATÓRIO CONSOLIDADO DE PEDIDOS\n");
        sb.append("========================================================================\n\n");

        for (Pedido p : pedidosSelecionados) {
            sb.append("PEDIDO ID: ").append(p.getIdPedido()).append("\n");
            sb.append("Solicitante: ").append(p.getUsuario().getNomeUsuario()).append("\n");

            String statusStr = p.getStatusPedido() == 0 ? "Pendente" : (p.getStatusPedido() == 1 ? "Aprovado" : "Recusado");
            sb.append("Status Atual: ").append(statusStr).append("\n");

            sb.append("\n--- ITENS ---\n");
            List<ItemPedido> itens = dao.buscarItensPorPedido(p.getIdPedido());
            for (ItemPedido item : itens) {
                sb.append(String.format(" • %-30s | Qtd: %-4d | Preço Un: R$ %.2f\n",
                        item.getProduto().getNome(),
                        item.getQuantidade(),
                        item.getPrecoUnitario()));

                if (item.getObservacao() != null && !item.getObservacao().trim().isEmpty()) {
                    sb.append("   Obs: ").append(item.getObservacao()).append("\n");
                }
            }

            sb.append("\nSubtotal do Pedido: R$ ").append(p.getValorTotal()).append("\n");
            if (p.getStatusPedido() == 2 && p.getMotivoRecusa() != null) {
                sb.append("Motivo da Recusa: ").append(p.getMotivoRecusa()).append("\n");
            }
            sb.append("------------------------------------------------------------------------\n\n");

            if (p.getValorTotal() != null) {
                totalGeral = totalGeral.add(p.getValorTotal());
            }
        }

        sb.append("========================================================================\n");
        sb.append("TOTAL GERAL PROCESSADO: R$ ").append(totalGeral).append("\n");
        sb.append("========================================================================\n");

        textArea.setText(sb.toString());
        textArea.setCaretPosition(0);
    }

    private void exportarParaPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Relatório PDF");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Arquivos PDF (*.pdf)", "pdf"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String caminho = fileChooser.getSelectedFile().getAbsolutePath();
            if (!caminho.toLowerCase().endsWith(".pdf")) {
                caminho += ".pdf";
            }

            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(caminho));
                document.open();

                com.itextpdf.text.Font font = FontFactory.getFont(FontFactory.COURIER, 10);
                document.add(new Paragraph(textArea.getText(), font));

                document.close();
                JOptionPane.showMessageDialog(this, "Relatório PDF salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar PDF: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportarParaExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Planilha Excel");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Arquivos Excel (*.xlsx)", "xlsx"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String caminho = fileChooser.getSelectedFile().getAbsolutePath();
            if (!caminho.toLowerCase().endsWith(".xlsx")) {
                caminho += ".xlsx";
            }

            try (XSSFWorkbook workbook = new XSSFWorkbook();
                 FileOutputStream out = new FileOutputStream(caminho)) {

                Sheet sheet = workbook.createSheet("Relatório Consolidado");
                Row headerRow = sheet.createRow(0);
                String[] colunas = {"ID Pedido", "Data", "Solicitante", "Status", "Produto", "Qtd", "Preço Un (R$)", "Subtotal (R$)", "Observação"};

                for (int i = 0; i < colunas.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(colunas[i]);
                }

                int rowNum = 1;
                PedidoDao dao = new PedidoDao();
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                for (Pedido p : pedidosSelecionados) {
                    String statusStr = p.getStatusPedido() == 0 ? "Pendente" : (p.getStatusPedido() == 1 ? "Aprovado" : "Recusado");
                    String dataStr = p.getDataPedido() != null ? p.getDataPedido().format(formatter) : "";

                    List<ItemPedido> itens = dao.buscarItensPorPedido(p.getIdPedido());

                    for (ItemPedido item : itens) {
                        Row row = sheet.createRow(rowNum++);
                        row.createCell(0).setCellValue(p.getIdPedido());
                        row.createCell(1).setCellValue(dataStr);
                        row.createCell(2).setCellValue(p.getUsuario().getNomeUsuario());
                        row.createCell(3).setCellValue(statusStr);
                        row.createCell(4).setCellValue(item.getProduto().getNome());
                        row.createCell(5).setCellValue(item.getQuantidade());
                        row.createCell(6).setCellValue(item.getPrecoUnitario().doubleValue());
                        row.createCell(7).setCellValue(item.getPrecoUnitario().multiply(new BigDecimal(item.getQuantidade())).doubleValue());
                        row.createCell(8).setCellValue(item.getObservacao() != null ? item.getObservacao() : "");
                    }
                }

                for (int i = 0; i < colunas.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                workbook.write(out);
                JOptionPane.showMessageDialog(this, "Planilha Excel salva com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar Excel: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}