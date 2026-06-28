package Telas;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RealizarPedido extends JFrame {
    private JTextField TfCracha;
    private JTextField TfCategoria;
    private JTextField TfItem;
    private JTextField TfQuantidade;
    private JTextField TfObservacao;
    private JButton BtEnviar;
    private JButton BtCancelar;
    private JButton BtExcluir;
    private JLabel TitleLabel;
    private JFormattedTextField formattedTFData;

   
    public RealizarPedido() {
        inicializarData();
    }

    private void inicializarData() {
        DateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");
        formattedTFData.setFormatterFactory(new DefaultFormatterFactory(new DateFormatter(formatoData)));
        formattedTFData.setValue(new Date());
    }
}